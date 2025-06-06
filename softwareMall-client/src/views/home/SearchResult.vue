<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { productGetAllService } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue'
import { debounce } from 'lodash-es'

const route = useRoute()
const keyword = ref('')
const products = ref([])
const loading = ref(false)
const hasSearched = ref(false)

// 分页相关状态
const currentPage = ref(1)
const itemsPerPage = 20

// 排序相关状态
const sortOptions = [
  { value: 'default', label: '默认' },
  { value: 'sales-desc', label: '销量' },
  { value: 'price-asc', label: '价格↑' },
  { value: 'price-desc', label: '价格↓' }
]
const selectedSort = ref('default')

// 获取搜索关键字
const getKeyword = () => {
  return route.query.q || ''
}

// 带防抖的搜索函数
const debouncedSearch = debounce(async () => {
  loading.value = true
  try {
    const searchQuery = {
      name: keyword.value,
      level1Category: null,
      level2Category: null
    }
    const res = (await productGetAllService(searchQuery)).data.data
    products.value = res
    currentPage.value = 1 // 重置到第一页
  } catch (error) {
    console.error('搜索失败', error)
  } finally {
    loading.value = false
    hasSearched.value = true
  }
}, 300)

// 排序产品
const sortedProducts = computed(() => {
  const productsCopy = [...products.value]
  
  switch (selectedSort.value) {
    case 'sales-desc':
      return productsCopy.sort((a, b) => (b.totalSales || 0) - (a.totalSales || 0))
    case 'price-asc':
      return productsCopy.sort((a, b) => (a.lowPrice || 0) - (b.lowPrice || 0))
    case 'price-desc':
      return productsCopy.sort((a, b) => (b.lowPrice || 0) - (a.lowPrice|| 0))
    default:
      return productsCopy
  }
})

// 分页产品
const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return sortedProducts.value.slice(start, end)
})

// 总页数
const totalPages = computed(() => {
  return Math.ceil(sortedProducts.value.length / itemsPerPage)
})

// 上一页
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 下一页
const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    window.scrollTo({ top: 0, behavior: 'smooth' })
  }
}

// 首次加载
onMounted(() => {
  keyword.value = getKeyword()
  debouncedSearch()
})

// 监听路由参数变化
watch(
  () => route.query.q,
  (newVal) => {
    keyword.value = newVal
    debouncedSearch()
  }
)

// 监听排序变化
watch(selectedSort, () => {
  currentPage.value = 1 // 排序变化时重置到第一页
})
</script>

<template>
  <div class="search-result-page">
    <!-- 页面标题和状态信息 -->
    <div v-if="hasSearched" class="page-header">
      <div v-if="products.length > 0" class="results-count">
        找到 {{ products.length }} 件商品
      </div>
    </div>

    <!-- 排序控件 -->
    <div v-if="products.length > 0" class="sort-controls">
      <div class="sort-buttons">
        <button
          v-for="option in sortOptions"
          :key="option.value"
          @click="selectedSort = option.value"
          :class="['sort-button', { 'active': selectedSort === option.value }]"
        >
          <span class="icon">{{ option.icon }}</span>
          <span>{{ option.label }}</span>
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loader"></div>
      <div class="loading-text">搜索中...</div>
    </div>
    
    <!-- 搜索结果 -->
    <div v-else>
      <div v-if="paginatedProducts.length > 0" class="product-container">
        <div class="product-list">
          <ProductCard
            v-for="item in paginatedProducts"
            :key="item.id"
            :product="item"
            class="product-item"
          />
        </div>
      </div>
      
      <!-- 无结果状态 -->
      <div v-else-if="hasSearched" class="no-result">
        <p>没有找到"{{ keyword }}"相关商品</p>
        <p class="suggestion">尝试其他关键词，或浏览其他类别商品</p>
      </div>

      <!-- 分页控件 -->
      <div v-if="products.length > itemsPerPage" class="pagination">
        <button 
          @click="prevPage" 
          :disabled="currentPage === 1"
          class="pagination-button"
        >
          上一页
        </button>
        <span class="page-info">第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
        <button 
          @click="nextPage" 
          :disabled="currentPage === totalPages"
          class="pagination-button"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.search-result-page {
  max-width: 1280px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 24px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.results-count {
  font-size: 14px;
  color: #666;
}

.sort-controls {
  margin: 20px 0;
}

.sort-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin: 20px 0;
}

.sort-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 16px;
  background-color: white;
  border: 1px solid #e0e0e0;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #555;
  cursor: pointer;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
  min-width: 80px;
}

.sort-button:hover {
  background-color: #f8f8f8;
  border-color: #d0d0d0;
  transform: translateY(-1px);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.sort-button.active {
  background-color: var(--el-color-primary);
  color: white;
  border-color: var(--el-color-primary);
  box-shadow: 0 2px 8px rgba(var(--el-color-primary-rgb), 0.3);
}

.sort-button .icon {
  font-size: 14px;
  display: flex;
  align-items: center;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.loader {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top-color: var(--el-color-primary);
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-text {
  font-size: 16px;
  color: #666;
}

.product-container {
  margin-top: 20px;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: flex-start;
  margin-top: 20px;
}

.no-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
}

.no-result p {
  font-size: 18px;
  color: #666;
  margin-bottom: 8px;
}

.suggestion {
  font-size: 14px;
  color: #999;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 30px;
  gap: 20px;
}

.pagination-button {
  padding: 8px 16px;
  background-color: var(--el-color-primary);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.pagination-button:hover:not(:disabled) {
  background-color: var(--el-color-primary-light-3);
}

.pagination-button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #666;
}
</style>