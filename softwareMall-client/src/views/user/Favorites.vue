<script setup>
import { ref, onMounted, computed } from 'vue';
import { getFavoriteGoods, favoriteDeleteService } from '@/api/favourite';
import ProductCard from '@/components/ProductCard.vue';


const favoriteGoods = ref([]);
const loading = ref(false);
const selectedProducts = ref(new Set()); // 使用Set存储选中的商品ID
const isEditMode = ref(false); // 是否处于编辑模式

// 计算属性：是否全选
const isAllSelected = computed({
  get: () => favoriteGoods.value.length > 0 && 
        selectedProducts.value.size === favoriteGoods.value.length,
  set: (val) => {
    if (val) {
      favoriteGoods.value.forEach(product => {
        selectedProducts.value.add(product.id);
      });
    } else {
      selectedProducts.value.clear();
    }
  }
});

// 获取收藏商品
const getFavorites = async () => {
  try {
    const res = (await getFavoriteGoods()).data.data;
    favoriteGoods.value = res;
  } catch (error) {
    console.error('获取收藏商品失败', error);
  }
};

// 切换编辑模式
const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value;
  if (!isEditMode.value) {
    selectedProducts.value.clear(); // 退出编辑模式时清空选择
  }
};

// 处理单个商品的选择变化
const handleSelectChange = (productId, isChecked) => {
  if (isChecked) {
    selectedProducts.value.add(productId);
  } else {
    selectedProducts.value.delete(productId);
  }
};

// 批量取消收藏
const batchRemove = () => {
  if (selectedProducts.value.size === 0) {
    // eslint-disable-next-line no-undef
    ElMessage.warning('请至少选择一件商品');
    return;
  }
// eslint-disable-next-line no-undef
  ElMessageBox.confirm('确定要取消收藏选中的商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await favoriteDeleteService(Array.from(selectedProducts.value));
      // eslint-disable-next-line no-undef
      ElMessage.success('取消收藏成功');
      selectedProducts.value.clear(); // 清空选择
      isEditMode.value = false; // 退出编辑模式
      getFavorites();
    } catch (error) {
      console.error('取消收藏失败', error);
      // eslint-disable-next-line no-undef
      ElMessage.error('取消收藏失败');
    }
  }).catch(() => {
    // 用户点击了取消
  });
};

onMounted(() => {
  getFavorites();
});
</script>

<template>
  <el-card class="content" shadow="never">
    <div class="header">
      <h3 class="page-title">我喜欢的商品</h3>
      <div class="actions">
        <el-button 
          v-if="!isEditMode && favoriteGoods.length > 0" 
          @click="toggleEditMode"
          type="primary"
          plain
        >
          管理收藏
        </el-button>
        <template v-else-if="isEditMode">
          <el-checkbox 
            v-model="isAllSelected"
            :indeterminate="selectedProducts.size > 0 && !isAllSelected"
            class="select-all"
          >
            全选
          </el-checkbox>
          <el-button @click="toggleEditMode">取消</el-button>
          <el-button 
            type="danger" 
            :disabled="selectedProducts.size === 0"
            @click="batchRemove"
          >
            取消收藏({{ selectedProducts.size }})
          </el-button>
        </template>
      </div>
    </div>
    
    <el-divider />
    
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-icon class="is-loading" :size="30"><Loading /></el-icon>
      <div class="loading-text">加载中...</div>
    </div>
    
    <!-- 无数据状态 -->
    <div v-else-if="favoriteGoods.length === 0" class="no-data">
      <el-empty description="暂无收藏的商品" />
      <el-button type="primary" @click="$router.push('/home')">去逛逛</el-button>
    </div>
    
    <!-- 商品列表 -->
    <div v-else class="product-container">
      <div class="product-list">
        <div v-for="product in favoriteGoods" :key="product.id" class="product-wrapper">
          <el-checkbox 
            v-if="isEditMode"
            :model-value="selectedProducts.has(product.id)"
            @change="(val) => handleSelectChange(product.id, val)"
            class="product-checkbox"
          />
          <ProductCard
            :product="product"
            class="product-item"
            :class="{ 'selected': selectedProducts.has(product.id) }"
          />
        </div>
      </div>
    </div>
  </el-card>
</template>

<style scoped>
.content {
  flex: 1;
  padding: 20px;
  min-height: 60vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin-bottom: 0;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-left: auto;
}

.select-all {
  margin-right: 10px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
}

.loading-text {
  margin-top: 15px;
  color: #666;
}

.no-data {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  text-align: center;
}

.product-container {
  margin-top: 10px;
}

.product-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.product-wrapper {
  position: relative;
}

.product-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.product-item {
  width: 100%;
  max-width: 280px;
  transition: all 0.3s ease;
}

.product-item:hover {
  transform: translateY(-5px);
}

.product-item.selected {
  box-shadow: 0 0 0 2px var(--el-color-primary);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .product-list {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
    gap: 15px;
  }
  
  .page-title {
    font-size: 18px;
  }
}

@media (max-width: 480px) {
  .product-list {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .content {
    padding: 15px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .actions {
    margin-left: 0;
    width: 100%;
  }
}
</style>