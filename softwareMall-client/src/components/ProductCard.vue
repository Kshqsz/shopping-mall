<template>
  <el-card
    class="product-card"
    :body-style="{ padding: '20px' }"
    @click="detail(product.id)"
  >
    <div class="vertical-layout">
      <img :src="product.mainImage" alt="product image" class="product-image" />
      <div class="product-info">
        <h3 class="product-title">{{ product.name }}</h3>
        <div class="price-shop-row">
          <div class="price">￥{{ product.lowPrice }}起</div>
          <div class="shop-name">{{ product.shopName }}</div>
        </div>
        <div class="meta-info">
          <div class="sales">销量：{{ product.totalSales }}</div>
          <div class="category">{{ product.level2CategoryName }}</div>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  product: {
    type: Object,
    required: true,
  }
});

const router = useRouter();

const detail = (id) => {
  router.push(`/productDetail/${id}`);
};
</script>

<style scoped>
.product-card {
  width: 240px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.product-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.vertical-layout {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
}

.product-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 15px;
}

.product-info {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.product-title {
  font-size: 16px;
  font-weight: bold;
  margin: 0 0 5px 0;
  text-align: center;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  width: 100%;
  min-height: 24px;
}

.price-shop-row {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 4px;
}

.price {
  margin-left: 20px;
  padding-right: 24px;
  font-size: 20px;
  font-weight: bold;
  color: #e60012;
}

.shop-name {
  font-size: 13px;
  color: #666;
  text-align: right;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 50%;
}

.meta-info {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 8px;
}

.sales,
.category {
  font-size: 13px;
  color: #666;
  padding: 4px 8px;
  background-color: #f7f7f7;
  border-radius: 4px;
  white-space: nowrap;
}
</style>