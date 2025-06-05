<template>
  <div class="goods-management">
    <div class="header">
      <h2 class="title">商品管理</h2>
      <div class="subtitle">高效管理您的商品信息</div>
    </div>
    
    <el-card class="search-container">
      <GoodsSearch 
        @search="handleSearch"
        @reset="resetSearch"
      />
    </el-card>
    
    <div class="action-bar">
      <el-button type="primary" class="add-btn" @click="showAddDialog">
        <el-icon><Plus /></el-icon>
        <span>新增商品</span>
      </el-button>
      <div class="stats">
        <div class="stat-item">
          <div class="stat-value">{{ stats.total }}</div>
          <div class="stat-label">商品总数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.onSale }}</div>
          <div class="stat-label">已上架</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ stats.pending }}</div>
          <div class="stat-label">审核中</div>
        </div>
      </div>
    </div>
    
    <el-card class="list-container">
      <GoodsList 
        ref="goodsListRef"
        :search-params="searchParams"
        @edit="showEditDialog"
        @detail="showDetailDialog"
        @stats-update="updateStats"
      />
    </el-card>
    
    <GoodsAddDialog 
      v-model="addDialogVisible"
      @success="handleAddSuccess"
    />
    
    <GoodsEditDialog 
      v-model="editDialogVisible"
      :goods-id="currentGoodsId"
      @success="handleEditSuccess"
    />
    
    <GoodsDetailDialog 
      v-model="detailDialogVisible"
      :goods-id="currentGoodsId"
    />
  </div>
</template>
<script setup>
import { ref, reactive } from 'vue'
import GoodsSearch from '@/views/product/components/GoodsSearch.vue'
import GoodsList from '@/views/product/components/GoodsList.vue'
import GoodsAddDialog from '@/views/product/components/GoodsAddDialog.vue'
import GoodsEditDialog from '@/views/product/components/GoodsEditDialog.vue'
import GoodsDetailDialog from './components/GoodsDetailDialog.vue'
import { Plus } from '@element-plus/icons-vue'
import { productStatis } from '@/api/product'
// 搜索参数
const searchParams = ref({})
const goodsListRef = ref(null)

// 对话框状态
const addDialogVisible = ref(false)
const editDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentGoodsId = ref(null)

// 商品状态统计
const stats = reactive({
  total: 0,
  onSale: 0,
  pending: 0,
})


// 搜索处理
const handleSearch = (params) => {
  searchParams.value = {
    ...params,
  }
}

// 重置搜索
const resetSearch = () => {
  searchParams.value = {}
  goodsListRef.value?.refresh()
}

// 显示新增对话框
const showAddDialog = () => {
  addDialogVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (goodsId) => {
  currentGoodsId.value = goodsId
  editDialogVisible.value = true
}

// 显示详情对话框
const showDetailDialog = (goodsId) => {
  currentGoodsId.value = goodsId
  detailDialogVisible.value = true
}

// 获取统计信息
const getStatis = async() => {
  const res = (await productStatis()).data.data
  stats.total = res.total
  stats.onSale = res.onSale
  stats.pending = res.pending

}
getStatis()

// 新增成功处理
const handleAddSuccess = () => {
  goodsListRef.value?.refresh()
  ElMessage.success('商品添加成功')
}

// 编辑成功处理
const handleEditSuccess = () => {
  goodsListRef.value?.refresh()
  ElMessage.success('商品更新成功')
}

// 更新状态统计
const updateStats = () => {
  getStatis()
}
</script>

<style scoped>
.goods-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.header {
  margin-bottom: 24px;
}

.header .title {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.header .subtitle {
  font-size: 15px;
  color: #909399;
}

.search-container {
  margin-bottom: 16px;
  border-radius: 8px;
  background-color: #fff;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 12px 0;
}

.add-btn {
  background: linear-gradient(135deg, #1e90ff, #70a1ff);
  border: none;
  padding: 10px 20px;
  font-weight: 500;
}

.add-btn:hover {
  background: linear-gradient(135deg, #1e90ff, #5490ff);
}

.stats {
  display: flex;
  gap: 24px;
}

.stat-item {
  text-align: center;
  padding: 0 15px;
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #1e90ff;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-top: 4px;
}

.list-container {
  border-radius: 8px;
  background-color: #fff;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}
</style>