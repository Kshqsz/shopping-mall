<script setup>
import { ref, onMounted } from 'vue';
import { userGetFavouriteService } from '@/api/user';
import { useUserStore } from '@/stores';
import { productGetByIdsService } from '@/api/product';
import { categoryGetAllService } from '@/api/category';

const categories = ref([]);
const userStore = useUserStore();
const user = userStore.user;
const favourites = ref([]);

// 当前页码
const currentPage = ref(1);
// 每页展示的数量
const itemsPerPage = ref(8);
// 分页后的结果
const pagedResults = ref([]);

const getFavourites = async () => {
  const userId = user.id;
  const res = await userGetFavouriteService(userId);
  favourites.value = res.data.data;
  favourites.value = favourites.value.map(item => item.productId);

  const temp = favourites.value;
  const res_product = await productGetByIdsService(temp);
  favourites.value = res_product.data.data;
  updatePagedResults();  // 更新分页数据
};

// 获取分类名称
const getCategoryName = (id) => {
  const category = categories.value.find(item => item.id === id);
  return category ? category.name : "未知分类";
};

// 更新分页数据
const updatePagedResults = () => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  pagedResults.value = favourites.value.slice(start, end);
};

// 监听页码变化
const pageChange = (page) => {
  currentPage.value = page;
  updatePagedResults();
};

onMounted(async () => {
  const category_res = await categoryGetAllService();
  categories.value = category_res.data.data;
  await getFavourites();
});
</script>

<template>
  <el-card class="content" shadow="never">
    <h3>我喜欢的商品</h3>
    <hr style="margin-top: 40px; opacity: 0.4">
    <div class="search-result-page">
      <div v-if="favourites.length === 0" class="no-data">
        <p>暂无喜欢的产品，快去收藏吧~</p>
      </div>
      <div v-else class="product-list">
        <ProductCard
          v-for="product in pagedResults"
          :key="product.id"
          :product="product"
          :categoryName="getCategoryName(product.categoryId)"
          :style="{ width: '250px' }"
        />
      </div>

      <!-- 分页组件 -->
      <el-pagination
        v-if="favourites.length > 0"
        :current-page="currentPage"
        :page-size="itemsPerPage"
        :total="favourites.length"
        layout="prev, pager, next"
        @current-change="pageChange"
        class="pagination"
      />
    </div>
  </el-card>
</template>

<style scoped>
.no-data {
  text-align: center;
  color: #999;
  font-size: 18px;
  margin-top: 50px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px; /* 设置卡片之间的间距 */
  justify-content: left; /* 每一行的卡片居中 */
}

.content {
  flex: 1;
  padding: 20px;
}

.pagination {
  padding-left: 45%;
  text-align: center;
  margin-top: 20px;
}
</style>
