<script setup>
import { useRoute } from 'vue-router';
import { productGetByIdService } from '@/api/product';
import { onMounted, ref } from 'vue';
import { categoryGetAllService } from '@/api/category'
import { useUserStore } from '@/stores';
import { favoriteAddService, favoriteDeleteService, checkFavoriteStatus } from '@/api/favourite'
import { orderAddService } from '@/api/order'
import { useRouter } from 'vue-router';
import { addToCart } from '@/api/cart';

const route = useRoute();
const userStore = useUserStore();
const userId = userStore.user.id;
const router = useRouter();

const productId = route.params.id;
const product = ref({});
const categories = ref([]);

const isFavorite = ref(false); // 使用单独的变量来跟踪收藏状态
const showDialog = ref(false);
const orderInfo = ref({
  name: "",
  image: "",
  price: 0,
  categoryName: "",
  merchantName: "",
});
const selectedSpec = ref(null); // 当前选中的规格

const confirmOrder = async () => {
  try {
    const res = await orderAddService({
      userId,
      productId,
      specId: selectedSpec.value.id // 添加规格ID
    });
    const order = res.data.data;
    ElMessage.success('订单创建成功！即将跳转到支付页面...');
    showDialog.value = false;
    setTimeout(() => {
      router.push({
        path: '/payment',
        query: {
          order: JSON.stringify(order),
        },
      });
    }, 1000);
  } catch (error) {
    ElMessage.error('订单创建失败，请稍后再试！'+error);
  }
};

const showOrderDialog = () => {
  if (!selectedSpec.value) {
    ElMessage.warning('请选择商品规格');
    return;
  }
  
  orderInfo.value = {
    name: `${product.value.name} ${selectedSpec.value.name}`,
    image: selectedSpec.value.image,
    price: selectedSpec.value.price,
    categoryName: `${product.value.level1CategoryName}/${product.value.level2CategoryName}`,
  };
  showDialog.value = true;
};

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

  const category_res = await categoryGetAllService();
  categories.value = category_res.data.data;

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
      title="确认订单"
      width="500px"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
        <img :src="orderInfo.image" alt="Product Image" class="dialog-image" />
        <p><strong>商品名称：</strong>{{ orderInfo.name }}</p>
        <p><strong>分类名称：</strong>{{ orderInfo.categoryName }}</p>
        <p><strong>商家名称：</strong>{{ orderInfo.merchantName }}</p>
        <p><strong>价格：</strong>￥{{ orderInfo.price }}</p>
      </div>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmOrder">确认下单</el-button>
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

.dialog-content {
  text-align: left;
  font-size: 16px;
}

.dialog-image {
  width: 100px;
  height: 100px;
  border-radius: 6px;
  object-fit: cover;
  margin-bottom: 20px;
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

</style>