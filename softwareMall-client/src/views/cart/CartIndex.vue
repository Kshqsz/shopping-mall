<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores';
import { getCartList,deleteCartItems,updateCart } from '@/api/cart';

const router = useRouter()
const userStore = useUserStore();
const user = userStore.user;

// 购物车数据
const cartItems = ref([])

// 获取购物车数据
const fetchCartList = async(userId) => {
  try {
    const res = await getCartList(userId)
    cartItems.value = res.data.data.map(item => ({
      ...item,
      selected: true, // 默认选中
      price: parseFloat(item.price) // 确保price是数字类型
    }))
  } catch (error) {
    console.error('获取购物车数据失败:', error)
    // eslint-disable-next-line no-undef
    ElMessage.error('获取购物车数据失败')
  }
}

// 全选计算属性
const selectAll = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: (val) => {
    cartItems.value.forEach(item => {
      item.selected = val
    })
  }
})

// 不确定状态（部分选中）
const isIndeterminate = computed(() => {
  const selectedCount = cartItems.value.filter(item => item.selected).length
  return selectedCount > 0 && selectedCount < cartItems.value.length
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
  updateCart(item.cartId,item.quantity)
}

// 删除商品
const removeItem = (item) => {
  // eslint-disable-next-line no-undef
  ElMessageBox.confirm('确定要删除该商品吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async() => {
    console.log(item)
    await deleteCartItems(item.cartId)
    // eslint-disable-next-line no-undef
    ElMessage.success('删除成功')
    fetchCartList(user.id)
  }).catch(() => {
  })
}

// 去结算
const checkout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  if (selectedItems.length === 0) {
        // eslint-disable-next-line no-undef
    ElMessage.warning('请至少选择一件商品')
    return
  }
  router.push({
    path: '/checkout',
    query: {
      items: JSON.stringify(selectedItems)
    }
  })
}

// 继续购物
const continueShopping = () => {
  router.push('/home')
}

// 组件挂载时获取购物车数据
onMounted(() => {
  fetchCartList(user.id)
})
</script>

<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>
    
    <div class="cart-container">
      <!-- 购物车为空时的提示 -->
      <div v-if="cartItems.length === 0" class="empty-cart">
        <el-empty description="购物车空空如也~">
          <el-button type="primary" @click="continueShopping">去逛逛</el-button>
        </el-empty>
      </div>
      
      <!-- 购物车列表 -->
      <div v-else class="cart-list">
        <div class="cart-header">
          <el-checkbox v-model="selectAll" :indeterminate="isIndeterminate">全选</el-checkbox>
          <span class="header-item">商品信息</span>
          <span class="header-item">单价</span>
          <span class="header-item">数量</span>
          <span class="header-item">小计</span>
          <span class="header-item">操作</span>
        </div>
        
        <div class="cart-item" v-for="item in cartItems" :key="item.cartId">
          <el-checkbox v-model="item.selected" class="item-checkbox" />
          <div class="item-info">
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
      
      <!-- 结算栏 -->
      <div v-if="cartItems.length > 0" class="cart-footer">
        <div class="footer-left">
          <el-button @click="continueShopping">继续购物</el-button>
          <span class="selected-count">已选择 {{ selectedCount }} 件商品</span>
        </div>
        <div class="footer-right">
          <span class="total-price">合计: ￥{{ totalPrice.toFixed(2) }}</span>
          <el-button type="danger" @click="checkout">去结算</el-button>
        </div>
      </div>
    </div>
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
}
</style>