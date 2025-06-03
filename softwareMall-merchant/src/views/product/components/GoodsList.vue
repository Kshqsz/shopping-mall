<template>
  <div class="goods-list">
    <el-table 
      :data="goodsList" 
      v-loading="loading" 
      style="width: 100%;" 
      size="medium"
      stripe
      highlight-current-row
      @row-click="handleRowClick"
    >
      <el-table-column label="商品名称" width="150" align="center">
        <template #default="{ row }">
            <div class="name">{{ row.name }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="最低售价" width="150" align="center">
        <template #default="{ row }">
          <div class="price">¥{{ formatPrice(row.price) }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="总销量" width="150" align="center">
        <template #default="{ row }">
          <div class="sales">{{ row.sales }}</div>
        </template>
      </el-table-column>

      <el-table-column label="分类" width="150" align="center">
        <template #default="{ row }">
          <div class="stock">{{ row.category }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" width="150" align="center">
        <template #default="{ row }">
          <el-tag 
            size="medium" 
            :type="statusType[row.status]"
            class="status-tag"
          >
            {{ statusText[row.status] }}
          </el-tag>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template #default="{ row }">
          <div class="actions">
            <el-button 
              type="primary" 
              size="small" 
              plain
              @click.stop="handleDetail(row.id)"
            >
              详情
            </el-button>
            <el-button 
              type="warning" 
              size="small" 
              plain
              @click.stop="handleEdit(row.id)"
            >
              编辑
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>
    
    <div class="pagination-container">
      <el-pagination
        v-model:currentPage="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        @current-change="getGoodsList"
        @size-change="handleSizeChange"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        background
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
import { selectGoods } from '@/api/product'

const props = defineProps({
  searchParams: Object
})

const emit = defineEmits(['edit', 'detail', 'stats-update'])

// 状态映射
const statusText = {
  0: '审核中',
  [-1]: '已下架',
  1: '已上架'
}
const statusType = {
  [-1]: 'warning',
  0: 'info',
  1: 'success'
}

// 数据
const loading = ref(false)
const goodsList = ref([])
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 监听搜索参数变化
watch(() => props.searchParams, () => {
  pagination.page = 1
  getGoodsList()
}, { deep: true })

// 处理每页数量变化
const handleSizeChange = (size) => {
  pagination.size = size
  pagination.page = 1 // 每页数量变化时重置到第一页
  getGoodsList()
}

// 获取商品列表
const getGoodsList = async () => {
   try {
    loading.value = true
    
    // 组合分页参数和搜索参数
    const params = {
      ...props.searchParams,
      page: pagination.page,
      size: pagination.size
    }
    
    // 调用API获取数据
    const response = await selectGoods(params)
    const data = response.data.data
    
    // 更新列表数据
    goodsList.value = data.list
    
    // 更新分页信息
    pagination.total = data.total
    
  } catch (error) {
    console.error('获取商品列表失败:', error)
    // 这里可以添加错误提示
  } finally {
    loading.value = false
  }
}

// 暴露刷新方法给父组件
defineExpose({ refresh: getGoodsList })

// 编辑商品
const handleEdit = (id) => {
  emit('edit', id)
}

// 查看详情
const handleDetail = (id) => {
  emit('detail', id)
}

// 点击行查看详情
const handleRowClick = (row) => {
  emit('detail', row.id)
}

// 辅助函数
const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

// 初始化
getGoodsList()
</script>

<style scoped>
.goods-list {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
}

.product-info {
  display: flex;
  align-items: center;
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  border: 1px solid #eee;
  margin-right: 12px;
}

.product-name .name {
  font-weight: 600;
  margin-bottom: 4px;
  cursor: pointer;
  transition: color 0.2s;
}

.product-name .name:hover {
  color: #1e90ff;
}

.product-name .category {
  font-size: 13px;
  color: #909399;
}

.price {
  color: #ff5000;
  font-weight: 600;
  font-size: 15px;
}

.stock {
  font-weight: 500;
  color: #606266;
}

.sales {
  color: #1e90ff;
  font-weight: 500;
}

.status-tag {
  font-weight: 500;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.pagination-container {
  padding: 16px 10px 0;
  background: #fff;
  display: flex;
  justify-content: flex-end;
}
</style>