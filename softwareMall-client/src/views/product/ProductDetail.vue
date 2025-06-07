<script setup>
import { useRoute } from 'vue-router';
import { productGetByIdService } from '@/api/product';
import { onMounted, ref,watch } from 'vue';
import { useUserStore } from '@/stores';
import { favoriteAddService, favoriteDeleteService, checkFavoriteStatus } from '@/api/favourite'
import { orderAddService } from '@/api/order'
import { useRouter } from 'vue-router';
import { addToCart } from '@/api/cart';
import { addressList } from '@/api/user';
import { codeToText } from 'element-china-area-data';

const route = useRoute();
const userStore = useUserStore();
const userId = userStore.user.id;
const router = useRouter();

const productId = route.params.id;
const product = ref({});


const isFavorite = ref(false); // 使用单独的变量来跟踪收藏状态
const showDialog = ref(false);
const selectedSpec = ref(null); // 当前选中的规格

// 检查收藏状态
const checkFavorite = async () => {
  try {
    const res = await checkFavoriteStatus(productId);
    console.log(res)
    isFavorite.value = res.data.data; // 假设返回的是boolean值
  } catch (error) {
    console.error('检查收藏状态失败', error);
    isFavorite.value = false;
  }
};

onMounted(async () => {
  const res = await (await productGetByIdService(productId)).data.data;
  product.value = res;
  
  // 设置默认选中的规格
  if (product.value.specItemVos && product.value.specItemVos.length > 0) {
    selectedSpec.value = product.value.specItemVos[0];
  }

  await checkFavorite(); // 初始化时检查收藏状态
});

const toggleLike = async () => {
  try {
    if (isFavorite.value) {
      await favoriteDeleteService(productId);
      ElMessage.success("取消收藏成功~");
    } else {
      await favoriteAddService(productId);
      ElMessage.success("收藏成功~");
    }
    // 更新收藏状态
    await checkFavorite();
  } catch (error) {
    console.error('操作收藏失败', error);
    ElMessage.error('操作失败，请稍后再试');
  }
};

// 选择规格
const selectSpec = (spec) => {
  selectedSpec.value = spec;
};
// 加入购物车
const addToCartHandler = async () => {
  if (!selectedSpec.value) {
    ElMessage.warning('请选择商品规格');
    return;
  }
  
  try {
    await addToCart({
      userId,
      productId,
      specId: selectedSpec.value.id,
      quantity: 1
    });
    // eslint-disable-next-line no-undef
    ElMessage.success('已加入购物车');
  } catch (error) {
    console.error('加入购物车失败', error);
    // eslint-disable-next-line no-undef
    ElMessage.error('加入购物车失败');
  }
};
// 支付相关
// 地址数据
const userAddresses = ref([]);
const showOrderDialog = async() => {
  const addresses =  (await addressList()).data.data
  userAddresses.value = addresses

  orderInfo.value = {
    name: `${product.value.name} ${selectedSpec.value.name}`,
    image: selectedSpec.value.image,
    price: selectedSpec.value.price,
    categoryName: `${product.value.level1CategoryName}/${product.value.level2CategoryName}`,
  };
  showDialog.value = true;
};

// 订单信息
const orderInfo = ref({
  name: "",
  image: "",
  price: 0,
  quantity: 1, // 默认购买数量
  addressId: null,
  paymentMethod: "alipay"
});

const totalPrice = ref(0); // 总价（商品总价 + 运费）

// 计算总价
const updateTotalPrice = () => {
  const basePrice = orderInfo.value.price * orderInfo.value.quantity;
  totalPrice.value = basePrice
};

// 格式化地址显示
const formatAddress = (address) => {
  return `${address.name} ${address.phone} ${codeToText[address.city]} ${codeToText[address.province]} ${codeToText[address.district]}${address.detail}`;
};

// 管理地址
const goToAddress = () => {
  router.push('/address');
};

// 提交订单
const submitOrder = async () => {
  // 验证地址是否已选择
  if (!orderInfo.value.addressId) {
    // eslint-disable-next-line no-undef
    ElMessage.warning('请选择收货地址');
    return;
  }
  
   try {
    // 生成订单编号 (格式: ORD + 年月日 + 4位随机数)
    const generateOrderNo = () => {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const randomNum = Math.floor(1000 + Math.random() * 9000);
      return `ORD${year}${month}${day}${randomNum}`;
    };

    const orderData = {
      orderNo: generateOrderNo(), // 前端生成的订单编号
      userId: userId,
      merchantId: product.value.merchantId,
      productId: productId,
      specId: selectedSpec.value.id,
      productPrice: orderInfo.value.price, // 商品单价
      quantity: orderInfo.value.quantity,
      totalAmount: totalPrice.value,
      addressId: orderInfo.value.addressId
    };

    const res = (await orderAddService(orderData)) 
    
    const order = res.data.data;
    ElMessage.success('订单创建成功！即将跳转到支付页面...');
    showDialog.value = false;
    
    // 这里通常会跳转到支付页面
    setTimeout(() => {
      router.push({
        path: '/payment',
        query: {
          order: JSON.stringify(order),
        },
      });
    }, 500);
  } catch (error) {
    ElMessage.error('订单创建失败，请稍后再试！'+error);
  }
};

// 当显示弹窗时初始化数据
watch(showDialog, (visible) => {
  if (visible) {
    const basePrice = selectedSpec.value ? selectedSpec.value.price : product.value.lowPrice;
    orderInfo.value = {
      name: `${product.value.name}${selectedSpec.value ? ' ' + selectedSpec.value.name : ''}`,
      image: selectedSpec.value ? selectedSpec.value.image : product.value.mainImage,
      price: basePrice,
      quantity: 1,
      addressId: userAddresses.value.find(addr => addr.isDefault)?.id || null,
    };
    updateTotalPrice();
  }
});
</script>

<template>
  <div class="product-detail-page">
    <!-- 商品展示区域 -->
    <div class="product-main">
      <div class="product-image">
        <img 
          :src="selectedSpec ? selectedSpec.image : product.mainImage" 
          alt="Product Image" 
          class="product-main-image" 
        />
      </div>

      <div class="product-info">
        <!-- 右上角收藏按钮 -->
        <div class="favorite-button-container">
          <button 
            :class="{'favorite-button': true, 'liked': isFavorite}"
            @click.stop="toggleLike"
          >
            <i
              :class="isFavorite ? 'fas fa-heart' : 'far fa-heart'"
              class="like-icon"
            ></i>
            {{ isFavorite ? '已收藏' : '收藏' }}
          </button>
        </div>

        <h2 class="product-title">{{ product.name }}</h2>
        <p class="product-category">
          {{ product.level1CategoryName }} > {{ product.level2CategoryName }}
        </p>
        
        <div class="price-section">
          <span class="price-label">价格：</span>
          <span class="product-price">￥{{ selectedSpec ? selectedSpec.price : product.lowPrice }}</span>
        </div>
        
        <div class="spec-section" v-if="product.specItemVos && product.specItemVos.length > 0">
          <h3 class="section-title">选择规格</h3>
          <div class="spec-options">
            <div 
              v-for="spec in product.specItemVos" 
              :key="spec.id"
              class="spec-option"
              :class="{ 'selected': selectedSpec && selectedSpec.id === spec.id }"
              @click="selectSpec(spec)"
            >
              {{ spec.name }}
            </div>
          </div>
        </div>
        
        <div class="stock-section" v-if="selectedSpec">
          <span>库存：</span>
          <span class="stock-count">{{ selectedSpec.stock }}件</span>
        </div>
        
        <div class="description-section">
          <h3 class="section-title">商品描述</h3>
          <p class="product-description">{{ product.description }}</p>
        </div>

        <div class="product-actions">
          <button class="buy-button" @click="showOrderDialog">立即购买</button>
          <button 
            class="cart-button"
            @click="addToCartHandler"
          >
            加入购物车
          </button>
        </div>
      </div>
    </div>

    <div class="divider"></div>
    
    <div class="merchant-info">
      <h3>店铺信息</h3>
      <div class="merchant-details">
        <div class="merchant-text">
          <p class="merchant-name">{{ product.shopName }}</p>
        </div>
      </div>
    </div>

    <!-- 视频播放器 -->
    <div v-if="product.video" class="video-player-section">
      <h3>产品视频</h3>
      <video controls class="video-player">
        <source :src="product.video" type="video/mp4" />
        您的浏览器不支持视频播放。
      </video>
    </div>
    
    <el-dialog
    v-model="showDialog"
    title="订单确认"
    width="600px"
    :close-on-click-modal="false"
    class="payment-dialog"
  >
    <div class="pd-container">
      <!-- 商品信息区域 -->
      <div class="pd-product-info">
        <div class="pd-product-image">
          <img 
            :src="orderInfo.image || product.mainImage" 
            alt="商品图片" 
            class="pd-thumb"
          />
        </div>
        <div class="pd-details">
          <h3 class="pd-product-name">{{ orderInfo.name || product.name }}</h3>
          <div class="pd-spec">
            <span v-if="selectedSpec">规格：{{ selectedSpec.name }}</span>
          </div>
          <div class="pd-price">
            单价：<span class="pd-price-value">￥{{ orderInfo.price || selectedSpec.price || product.lowPrice }}</span>
          </div>
        </div>
      </div>
      
      <!-- 订单信息区域 -->
      <div class="pd-order-info">
        <div class="pd-form-item">
          <label class="pd-form-label">购买数量：</label>
          <el-input-number 
            v-model="orderInfo.quantity" 
            :min="1" 
            :max="selectedSpec ? selectedSpec.stock : 100" 
            size="medium"
            controls-position="right"
            @change="updateTotalPrice"
          />
          <span class="pd-stock-tip">
            <span v-if="selectedSpec">库存：{{ selectedSpec.stock }}件</span>
          </span>
        </div>
        
        <div class="pd-form-item">
          <label class="pd-form-label">收货地址：</label>
          <el-select 
            v-model="orderInfo.addressId" 
            placeholder="请选择收货地址"
            class="pd-address-select"
          >
            <el-option
              v-for="address in userAddresses"
              :key="address.id"
              :label="formatAddress(address)"
              :value="address.id"
            >
              <div class="pd-address-option">
                <div class="pd-address-main">
                  <span class="pd-recipient">{{ address.name }}</span>
                  <span class="pd-phone">{{ address.phone }}</span>
                  <div class="pd-address-detail">{{ codeToText[address.city] + codeToText[address.province] + codeToText[address.district] + address.detail }}</div>
                  <el-tag v-if="address.isDefault" size="small">默认</el-tag>
                </div>
                
              </div>
            </el-option>
          </el-select>
          <el-button type="text" class="pd-manage-address" @click="goToAddress">管理地址</el-button>
        </div>
        
        <div class="pd-form-item pd-payment-info">
          <div class="pd-payment-total">
            <span class="pd-payment-label">应付总额：</span>
            <span class="pd-payment-value pd-total-price">￥{{ totalPrice.toFixed(2) }}</span>
          </div>
        </div>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="showDialog = false">取消</el-button>
      <el-button type="primary" @click="submitOrder">去支付</el-button>
    </template>
  </el-dialog>
  </div>
</template>

<style scoped>
.product-detail-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.product-main {
  display: flex;
  gap: 40px;
  margin-top: 20px;
}

.product-image {
  flex: 1;
  display: flex;
  justify-content: center;
  min-width: 400px;
}

.product-main-image {
  width: 100%;
  max-width: 500px;
  height: auto;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 20px;
}

.product-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
  color: #333;
}

.product-category {
  font-size: 14px;
  color: #666;
  margin-bottom: 16px;
}

.price-section {
  margin: 20px 0;
  padding: 15px;
  background-color: #f8f8f8;
  border-radius: 6px;
}

.price-label {
  font-size: 16px;
  color: #666;
}

.product-price {
  font-size: 28px;
  color: #f44336;
  font-weight: bold;
}

.section-title {
  font-size: 18px;
  margin: 20px 0 15px 0;
  color: #333;
  font-weight: 600;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.spec-option {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.spec-option:hover {
  border-color: #ff9800;
}

.spec-option.selected {
  border-color: #ff9800;
  background-color: #fff8e6;
  color: #ff9800;
}

.stock-section {
  font-size: 14px;
  color: #666;
  margin-bottom: 20px;
}

.stock-count {
  color: #4caf50;
  font-weight: 500;
}

.description-section {
  margin: 20px 0;
}

.product-description {
  font-size: 15px;
  line-height: 1.6;
  color: #555;
}

.divider {
  height: 1px;
  background-color: #eee;
  margin: 30px 0;
}

.merchant-info {
  margin: 30px 0;
}

.merchant-details {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-top: 15px;
}

.merchant-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  object-fit: cover;
}

.merchant-name {
  font-size: 18px;
  font-weight: 500;
  color: #333;
}

.merchant-id {
  font-size: 14px;
  color: #888;
}

.product-actions {
  display: flex;
  gap: 20px;
  margin-top: 30px;
}

.buy-button,
.like-button {
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 4px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.buy-button {
  background-color: #ff9800;
  color: white;
  flex: 2;
}

.buy-button:hover {
  background-color: #f57c00;
}



.video-player-section {
  margin: 40px 0;
}

.video-player {
  width: 100%;
  max-width: 800px;
  height: 450px;
  border-radius: 8px;
  background-color: #000;
  margin-top: 20px;
}

.favorite-button-container {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 10;
}

.favorite-button {
  padding: 8px 16px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 1px solid #ddd;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.favorite-button:hover {
  background-color: #fff;
}

.favorite-button.liked {
  background-color: #fff0f0;
  color: #f44336;
  border-color: #f44336;
}

.like-icon {
  margin-right: 6px;
}

.product-info {
  position: relative; /* 为收藏按钮定位提供参考 */
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 20px;
}

.cart-button {
  padding: 12px 30px;
  font-size: 16px;
  cursor: pointer;
  border: none;
  border-radius: 4px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 1;
  background-color: #666;
  color: #f5f5f5
}

.cart-button:hover {
  background-color: #645e5e;
}
/* 支付弹窗相关 */
.payment-dialog .pd-container {
  padding: 10px;
}

.pd-product-info {
  display: flex;
  padding: 15px;
  margin-bottom: 15px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.pd-product-image {
  width: 120px;
  height: 120px;
  margin-right: 20px;
}

.pd-thumb {
  width: 100px;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #eee;
}

.pd-details {
  flex: 1;
}

.pd-product-name {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.pd-spec {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.pd-price {
  font-size: 16px;
}

.pd-price-value {
  color: #f56c6c;
  font-weight: bold;
  font-size: 20px;
}

.pd-order-info {
  padding: 0 15px;
}

.pd-form-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
  padding: 10px 0;
}

.pd-form-label {
  width: 100px;
  font-weight: 500;
  color: #333;
  font-size: 14px;
  line-height: 32px;
  flex-shrink: 0;
}

.pd-address-select {
  flex: 1;
}

.pd-manage-address {
  margin-left: 10px;
  white-space: nowrap;
}

.pd-address-option {
  display: flex;
  flex-direction: column;
}

.pd-address-main {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pd-recipient {
  font-weight: 500;
}

.pd-phone {
  color: #666;
}

.pd-address-detail {
  font-size: 13px;
  color: #999;
  margin-top: 5px;
}

.pd-payment-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 15px;
  margin-top: 10px;
  border-top: 1px solid #eee;
  border-bottom: none;
}


.pd-payment-label {
  color: #666;
}

.pd-payment-value {
  color: #333;
}

.pd-payment-total {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  margin-top: 10px;
  padding-top: 10px;
}

.pd-total-price {
  font-size: 20px;
  font-weight: bold;
  color: #f56c6c;
}

.pd-stock-tip {
  margin-left: 15px;
  color: #999;
  font-size: 13px;
  line-height: 32px;
}
</style>