<script setup>
import { ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import { productSearchService } from '@/api/product'
import { categoryGetAllService } from '@/api/category'
import { onMounted } from 'vue';

// 获取路由的查询参数
const route = useRoute();
const categories = ref([]);

// 当前页码
const currentPage = ref(1);
// 每页展示的数量
const itemsPerPage = ref(10);
// 搜索查询条件
const searchQuery = ref(route.query.query || '');
// 过滤后的搜索结果
const filteredResults = ref([]);
// 分页后的结果
const pagedResults = ref([]);

// 获取当前分类名称
const getCategoryName = (id) => {
  const category = categories.value.find(item => item.id === id);
  return category ? category.name : "未知分类";
}

// 过滤函数
const filterResults = async () => {
  const res = await productSearchService(searchQuery.value);
  filteredResults.value = res.data.data;
  filteredResults.value = filteredResults.value.filter(item => item.status === 1);
  updatePagedResults();  // 更新分页结果
};

// 更新分页结果
const updatePagedResults = () => {
  const start = (currentPage.value - 1) * itemsPerPage.value;
  const end = start + itemsPerPage.value;
  pagedResults.value = filteredResults.value.slice(start, end);
};

// 监听 searchQuery 和 route.query.query 变化，确保每次查询变化时都进行过滤
watch(
  () => [route.query.query, route.query.t],
  () => {
    searchQuery.value = route.query.query || ''; // 更新 searchQuery
    filterResults(); // 重新过滤
  },
  { immediate: true } // 页面加载时就调用一次过滤
);

// 处理分页变化
const pageChange = (page) => {
  currentPage.value = page;  // 更新当前页码
  updatePagedResults();  // 更新分页数据
};

onMounted(async () => {
  const category_res = await categoryGetAllService();
  categories.value = category_res.data.data;
});
</script>

<template>
  <div class="search-result-page">
    <div v-if="filteredResults.length === 0" class="no-data">
      <p>暂无数据，请尝试其他搜索关键词。</p>
    </div>
    <div v-else class="product-list">
      <ProductCard
        v-for="product in pagedResults"
        :key="product.id"
        :product="product"
        :categoryName="getCategoryName(product.categoryId)"
      />
    </div>

    <!-- 分页组件 -->
    <el-pagination
      v-if="filteredResults.length > 0"
      :current-page="currentPage"
      :page-size="itemsPerPage"
      :total="filteredResults.length"
      layout="prev, pager, next"
      @current-change="pageChange"
      class="pagination"
    />
  </div>
</template>

<style scoped>
.search-result-page {
  padding: 20px;
  margin-left: 120px;
}

.no-data {
  text-align: center;
  color: #999;
  font-size: 18px;
  margin-top: 50px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;  /* 允许换行 */
  gap: 20px;  /* 设置卡片之间的间距 */
  justify-content: left;  /* 每一行的卡片居中 */
}

.pagination {
  padding-left: 40%;
  text-align: center;
  margin-top: 20px;
}

.category {
  font-size: 14px;
  color: #777;
  margin-top: 5px;
  font-style: italic;
}
</style>
