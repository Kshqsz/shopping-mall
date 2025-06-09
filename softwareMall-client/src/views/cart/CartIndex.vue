<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores';
import { getCartList, deleteCartItems, updateCart ,cartPay} from '@/api/cart';
import { addressList } from '@/api/user';
import { orderPayService } from '@/api/order'; 
import { codeToText } from 'element-china-area-data';

const router = useRouter()
const userStore = useUserStore();
const user = userStore.user;
const orderIds = ref([])
// 结算弹窗的显示状态
const showCheckoutDialog = ref(false)
// 结算弹窗中的商品数据
const checkoutItems = ref([])
// 收货地址列表
const userAddresses = ref([])
// 默认地址
const defaultAddress = ref(null)
// 支付弹窗状态
const showPaymentDialog = ref(false)

const detail = (id) => {
  router.push(`/productDetail/${id}`);
};

const cartItems = ref([])

const fetchCartList = async(userId) => {
  try {
    const res = await getCartList(userId)
    cartItems.value = res.data.data.map(item => ({
      ...item,
      selected: true,
      price: parseFloat(item.price)
    }))
  } catch (error) {
    console.error('获取购物车数据失败:', error)
    ElMessage.error('获取购物车数据失败')
  }
}

// 获取收货地址
const fetchAddresses = async () => {
  try {
    const res = await addressList();
    userAddresses.value = res.data.data;
    
    // 设置默认地址
    defaultAddress.value = userAddresses.value.find(addr => addr.isDefault);
    
    // 为每个结算商品初始化地址
    if (checkoutItems.value.length > 0 && defaultAddress.value) {
      checkoutItems.value.forEach(item => {
        item.selectedAddressId = defaultAddress.value.id;
      });
    }
  } catch (error) {
    console.error('获取地址列表失败:', error);
    ElMessage.error('获取地址失败');
  }
};

// 全选计算属性
const selectAll = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: (val) => {
    cartItems.value.forEach(item => {
      item.selected = val
    })
  }
})

// 已选商品数量
const selectedCount = computed(() => cartItems.value.filter(item => item.selected).length)

// 计算总价
const totalPrice = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((sum, item) => sum + item.price * item.quantity, 0)
})

// 修改商品数量
const changeQuantity = (item, val) => {
  const newQuantity = item.quantity + val
  if (newQuantity < 1) {
    ElMessage.warning('商品数量不能少于1')
    return
  }

  item.quantity = newQuantity
  updateCart(item.cartId, item.quantity)
}

// 删除商品
const removeItem = (item) => {
  ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async() => {
    await deleteCartItems([item.cartId])
    ElMessage.success('删除成功')
    fetchCartList(user.id)
  }).catch(() => {})
}

// 批量删除选中的商品
const removeSelectedItems = () => {
  const selectedIds = cartItems.value
    .filter(item => item.selected)
    .map(item => item.cartId)
    
  if (selectedIds.length === 0) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  
  ElMessageBox.confirm('确定要删除选中的商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async() => {
    console.log(selectedIds)
    await deleteCartItems(selectedIds)
    ElMessage.success('删除成功')
    fetchCartList(user.id)
  }).catch(() => {})
}

// 准备结算
const prepareCheckout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
    ElMessage.warning('请至少选择一件商品')
    return
  }
  
  // 设置结算商品数据
  checkoutItems.value = selectedItems.map(item => ({
    ...item,
    selectedAddressId: null // 初始化为null，稍后设置默认地址
  }));
  
  // 显示结算弹窗
  showCheckoutDialog.value = true;
  
  // 确保弹窗显示后加载地址信息
  nextTick(() => {
    fetchAddresses();
  });
}

// 管理地址
const goToAddress = () => {
  router.push('/address');
};

// 格式化地址显示
const formatAddress = (address) => {
  if (!address) return '';
  const province = codeToText[address.province];
  const city = codeToText[address.city];
  const district = codeToText[address.district];
  return `${city}${province}${district} ${address.detail}`;
};
const generateOrderNo = () => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const randomNum = Math.floor(1000 + Math.random() * 9000);
      return `ORD${year}${month}${day}${randomNum}`;
    };
// 提交订单
const submitOrder = async () => {
  try {
    const createPromises = checkoutItems.value.map(item => 
        cartPay({
        orderNo: generateOrderNo(),
        userId: user.id,
        merchantId:item.merchantId,
        productId: item.productId,
        specId: item.specId,
        productPrice:item.price,
        quantity: item.quantity,
        totalAmount: item.price * item.quantity,
        addressId: item.selectedAddressId
      }),
    );
    
    const responses = await Promise.all(createPromises);
    orderIds.value = responses.map(res => res.data.data);

     // 显示支付弹窗
    showPaymentDialog.value = true;

   
    
  } catch (error) {
    console.error('订单创建或支付失败:', error);
    ElMessage.error('支付失败: ' + error.message);
  }
}

// 执行支付
const processPayment = async () => {
  try {
     for (const orderId of orderIds.value) {
      await new Promise(resolve => setTimeout(resolve, 500)); // 模拟1秒延迟
      await orderPayService(orderId); // 调用支付API
      
    }
    
    ElMessage.success('支付成功！');
    showPaymentDialog.value = false;
    showCheckoutDialog.value = false;
    fetchCartList(user.id);
    
  } catch (error) {
     // 重新加载购物车数据
    fetchCartList(user.id);
    console.error('支付失败:', error);
    ElMessage.error('支付失败: ' + error.message);
  }
}


const continueShopping = () => {
  router.push('/home')
}

onMounted(() => {
  fetchCartList(user.id)
})
</script>

<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>
    
    <div class="cart-container">
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也~">
          <el-button type="primary" @click="continueShopping">去逛逛</el-button>
        </el-empty>
      </div>
      
      <div v-else class="cart-list">
        <div class="cart-header">
          <el-checkbox v-model="selectAll">全选</el-checkbox>
          <span class="header-item">商品信息</span>
          <span class="header-item">单价</span>
          <span class="header-item">数量</span>
          <span class="header-item">小计</span>
          <span class="header-item">操作</span>
        </div>
        
        <div class="cart-item" v-for="item in cartItems" :key="item.cartId">
          <el-checkbox v-model="item.selected" class="item-checkbox" />
          <div class="item-info" @click="detail(item.productId)">
            <img :src="item.image" class="item-image" />
            <div class="item-details">
              <span class="item-name">{{ item.productName }}</span>
              <span class="item-spec">规格: {{ item.specName }}</span>
            </div>
          </div>
          <div class="item-price">￥{{ item.price.toFixed(2) }}</div>
          <div class="item-quantity">
            <el-button @click="changeQuantity(item, -1)" :disabled="item.quantity <= 1">-</el-button>
            <span class="quantity">{{ item.quantity }}</span>
            <el-button @click="changeQuantity(item, 1)">+</el-button>
          </div>
          <div class="item-total">￥{{ (item.price * item.quantity).toFixed(2) }}</div>
          <div class="item-action">
            <el-button type="danger" text @click="removeItem(item)">删除</el-button>
          </div>
        </div>
      </div>
      
      <div v-if="cartItems.length > 0" class="cart-footer">
        <div class="footer-left">
          <el-button @click="removeSelectedItems">删除选中的商品</el-button>
          <el-button @click="continueShopping">继续购物</el-button>
          <span class="selected-count">已选择 {{ selectedCount }} 件商品</span>
        </div>
        <div class="footer-right">
          <span class="total-price">合计: ￥{{ totalPrice.toFixed(2) }}</span>
          <el-button type="danger" @click="prepareCheckout">结算</el-button>
        </div>
      </div>
    </div>
    
    <!-- 结算弹窗 -->
     <el-dialog
    v-model="showCheckoutDialog"
    title="订单结算"
    width="900px"
    :close-on-click-modal="false"
    class="checkout-dialog"
  >
    <div class="cd-container">
      <div class="cd-products">
        <div class="cd-product" v-for="(item, index) in checkoutItems" :key="index">
          <div class="cd-product-main">
            <img :src="item.image" class="cd-product-image" />
            <div class="cd-product-info">
              <div class="cd-product-name">{{ item.productName }}</div>
              <div class="cd-product-spec">规格: {{ item.specName }}</div>
              <div class="cd-product-quantity">数量: {{ item.quantity }}</div>
              <div class="cd-product-price">￥{{ (item.price * item.quantity).toFixed(2) }}</div>
            </div>
          </div>
          
          <div class="cd-address-section">
            <el-select 
              v-model="item.selectedAddressId" 
             
              class="cd-address-select"
            >
              <el-option
                v-for="address in userAddresses"
                :key="address.id"
                :label="`${address.name} ${address.phone} ${formatAddress(address)}`"
                :value="address.id"
              >
                <div class="cd-address-option">
                  <div class="cd-address-header">
                    <span class="cd-recipient">{{ address.name }}</span>
                    <span class="cd-phone">{{ address.phone }}</span>
                    <div class="cd-address-detail">{{ formatAddress(address) }}</div>
                    <el-tag v-if="address.isDefault" size="small" type="success">默认</el-tag>
                  </div>
                  
                </div>
              </el-option>
            </el-select>
          </div>
        </div>
      </div>
      
      <div class="cd-summary">
        <div class="cd-total-price">
          总计:
          <span class="cd-total-amount">￥{{ totalPrice.toFixed(2) }}</span>
        </div>
      </div>
    </div>
    
    <template #footer>
      <div class="cd-dialog-footer">
        <el-button @click="goToAddress">管理地址</el-button>
        <div class="cd-footer-right">
          <span class="cd-total-price-footer">
            合计:
            <span class="cd-total-amount">￥{{ totalPrice.toFixed(2) }}</span>
          </span>
          <el-button type="danger" @click="submitOrder">提交订单</el-button>
        </div>
      </div>
    </template>
  </el-dialog>
  <!-- 支付弹窗 -->
  <el-dialog
    v-model="showPaymentDialog"
    title="确认支付"
    width="400px"
    :close-on-click-modal="false"
  >
    <div class="payment-dialog-content">
      <div class="payment-summary">
        <p>订单总金额</p>
        <div class="payment-amount">￥{{ totalPrice.toFixed(2) }}</div>
      </div>
    </div>
    
    <template #footer>
      <div class="payment-dialog-footer">
        <el-button @click="showPaymentDialog = false">取消</el-button>
        <el-button type="danger" @click="processPayment">确认支付</el-button>
      </div>
    </template>
  </el-dialog>
  </div>
</template>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

.cart-container {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.empty-cart {
  padding: 40px 0;
  text-align: center;
}

.cart-header {
  display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  font-weight: bold;
}

.cart-header .header-item {
  flex: 1;
  text-align: center;
}

.cart-header .header-item:first-child {
  flex: 2;
  text-align: left;
  padding-left: 40px;
}

.cart-item {
   display: grid;
  grid-template-columns: 80px 2fr 1fr 1fr 1fr 80px;
  align-items: center;
  padding: 20px 0;
  border-bottom: 1px solid #f5f5f5;
}

.item-checkbox {
  justify-self: center;
}

.item-info {
  display: flex;
  align-items: center;
  padding-right: 20px;
}

.item-image {
  width: 100px;
  height: 100px;
  object-fit: contain;
  margin-right: 20px;
  border: 1px solid #f5f5f5;
  border-radius: 4px;
}

.item-details {
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
}

.item-spec {
  font-size: 14px;
  color: #999;
}

.item-price,
.item-total {
  flex: 1;
  text-align: center;
  color: #f56c6c;
  font-weight: bold;
}

.item-quantity {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.item-quantity .el-button {
  width: 28px;
  height: 28px;
  padding: 0;
}

.item-quantity .quantity {
  margin: 0 10px;
  min-width: 30px;
  text-align: center;
}

.item-action {
  flex: 1;
  text-align: center;
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.selected-count {
  margin-left: 20px;
  color: #666;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  margin-right: 20px;
}

/* 结算弹窗样式 */
.checkout-dialog .cd-container {
  max-height: 70vh;
  overflow-y: auto;
  padding-right: 10px;
}

.cd-products {
  margin-bottom: 20px;
}

.cd-product {
  display: flex;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  border: 1px solid #eee;
}

.cd-product-main {
  display: flex;
  flex: 1;
}

.cd-product-image {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-right: 15px;
  border-radius: 4px;
  border: 1px solid #eee;
}

.cd-product-info {
  flex: 1;
}

.cd-product-name {
  font-weight: bold;
  margin-bottom: 5px;
}

.cd-product-spec {
  color: #666;
  font-size: 13px;
  margin-bottom: 5px;
}

.cd-product-quantity {
  color: #666;
  font-size: 14px;
  margin-bottom: 5px;
}

.cd-product-price {
  color: #f56c6c;
  font-weight: bold;
  font-size: 16px;
}

.cd-product-address {
  width: 350px;
  margin-left: 20px;
}

.cd-address-default {
  margin-bottom: 10px;
}

.cd-address-option {
  display: block;
  width: 100%;
}

.cd-address-content {
  padding: 10px;
  border-radius: 4px;
  background-color: #f9f9f9;
}

.cd-address-header {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
}

.cd-recipient {
  font-weight: bold;
  margin-right: 10px;
}

.cd-phone {
  color: #666;
  margin-right: 10px;
}

.cd-address-detail {
  color: #666;
  font-size: 13px;
  line-height: 1.5;
}

.cd-address-select {
  width: 100%;
}

.cd-summary {
  padding-top: 15px;
  border-top: 1px solid #eee;
  text-align: right;
}

.cd-total-price {
  font-size: 16px;
}

.cd-total-amount {
  font-size: 22px;
  color: #f56c6c;
  font-weight: bold;
  margin-left: 10px;
}

.cd-dialog-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cd-footer-right {
  display: flex;
  align-items: center;
}

.cd-total-price-footer {
  font-size: 18px;
  font-weight: 500;
  margin-right: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .cart-header {
    display: none;
  }
  
  .cart-item {
    flex-wrap: wrap;
    position: relative;
    padding: 15px 0;
  }
  
  .item-checkbox {
    position: absolute;
    left: 0;
    top: 15px;
  }
  
  .item-info {
    flex: 100%;
    margin-bottom: 10px;
    padding-left: 30px;
  }
  
  .item-price,
  .item-quantity,
  .item-total,
  .item-action {
    flex: 1;
    margin-bottom: 10px;
    text-align: left;
  }
  
  .cart-footer {
    flex-direction: column;
  }
  
  .footer-right {
    margin-top: 15px;
    width: 100%;
    display: flex;
    justify-content: space-between;
  }
  
  .checkout-dialog {
    width: 95% !important;
  }
  
  .cd-product {
    flex-direction: column;
  }
  
  .cd-product-address {
    width: 100%;
    margin-left: 0;
    margin-top: 15px;
  }
}
.cd-address-section {
  width: 350px;
  margin-left: 20px;
}

.cd-address-select {
  width: 100%;
}

.cd-address-option {
  padding: 8px 0;
  line-height: 1.5;
}

.cd-address-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.cd-recipient {
  font-weight: bold;
  margin-right: 10px;
}

.cd-phone {
  color: #666;
  margin-right: 10px;
}

.cd-address-detail {
  color: #666;
  font-size: 13px;
}
/* 支付弹窗样式 */
.payment-dialog-content {
  text-align: center;
  padding: 20px;
}

.payment-summary {
  margin-bottom: 20px;
}

.payment-summary p {
  color: #606266;
  margin-bottom: 10px;
}

.payment-amount {
  font-size: 24px;
  font-weight: bold;
  color: #f56c6c;
}

.payment-dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>