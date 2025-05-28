<script setup>
import { useRouter } from 'vue-router';

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  categoryName: {
    type: String,
    required: true,
  },
});

const router = useRouter();

const detail = (id) => {
  router.push(`/productDetail/${id}`);
};
</script>

<template>
  <el-card
    class="product-card"
    :body-style="{ padding: '20px' }"
    @click="detail(product.id)"
  >
    <div class="card-header">
      <img :src="product.image" alt="product image" class="product-image" />
      <div class="product-info">
        <h3>{{ product.name }}</h3>
        <div class="category">{{ categoryName }}</div>
        <div class="price">￥{{ product.price }}</div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.product-card {
  width: 265px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  cursor: pointer;
  margin-bottom: 20px;
}

.product-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.product-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
}

.product-info {
  margin-top: 10px;
}

.category {
  font-size: 14px;
  color: #777;
  margin-top: 5px;
  font-style: italic;
}

.price {
  font-size: 18px;
  font-weight: bold;
  margin-top: 10px;
}
</style>
