<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { productGetAllService } from '@/api/product'
import ProductCard from '@/components/ProductCard.vue' // 确保正确导入组件
import { debounce } from 'lodash-es' // 引入防抖函数

const route = useRoute()
const keyword = ref('')
const products = ref([])
const loading = ref(false)
const pageTitle = ref('')
const hasSearched = ref(false) // 跟踪是否已执行搜索

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
    
    // 更新页面标题
    pageTitle.value = keyword.value 
      ? `搜索关键词：${keyword.value}`
      : '全部商品'
  } catch (error) {
    console.error('搜索失败', error)
  } finally {
    loading.value = false
    hasSearched.value = true
  }
}, 300)

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
</script>

<template>
  <div class="search-result-page">
    <!-- 页面标题和状态信息 -->
    <div v-if="hasSearched" class="page-header">
      <h1 class="title">{{ pageTitle }}</h1>
      <div v-if="products.length > 0" class="results-count">
        找到 {{ products.length }} 件商品
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="loader"></div>
      <div class="loading-text">搜索中...</div>
    </div>
    
    <!-- 搜索结果 -->
    <div v-else>
      <div v-if="products.length > 0" class="product-container">
        <div class="product-list">
          <ProductCard
            v-for="item in products"
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

.no-result-img {
  width: 200px;
  height: 200px;
  margin-bottom: 24px;
  opacity: 0.7;
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
</style>