<template>
  <div class="goods-list">
    <el-table 
      :data="sortedGoodsList" 
      v-loading="loading" 
      style="width: 100%;" 
      size="medium"
      stripe
      highlight-current-row
      @row-click="handleRowClick"
    >
      <el-table-column label="商品名称" width="180" align="center">
        <template #default="{ row }">
            <div class="name">{{ row.name }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="最低售价" width="180" align="center">
        <template #default="{ row }">
          <div class="price">¥{{ formatPrice(row.lowPrice) }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="总销量" width="180" align="center">
        <template #default="{ row }">
          <div class="sales">{{ row.totalSales }}</div>
        </template>
      </el-table-column>

      <el-table-column label="分类" width="180" align="center">
        <template #default="{ row }">
          <div class="stock">{{ row.secondCategoryName }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="状态" width="180" align="center">
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
      <el-table-column label="更新时间" width="200" align="center">
        <template #default="{ row }">
          <div class="sales">{{ row.updateTime ? row.updateTime.replace('T', ' ').slice(0, 19) : '' }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="操作" width="250" align="center" >
        <template #default="{ row }">
          <div class="fixed-actions">
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
              align="left"
              @click.stop="handleEdit(row.id)"
            >
              编辑
            </el-button>
            <div class="dynamic-actions">
              <el-button 
                v-if="row.status === 1" 
                type="danger" 
                size="small" 
                plain
                @click.stop="handleOffShelves(row.id)"
              >
                下架
              </el-button>
              
              <el-button 
                v-if="row.status === -1" 
                type="success" 
                size="small" 
                plain
                @click.stop="handleReOnShelves(row.id)"
              >
                重新上架
              </el-button>
            </div>
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
import { ref, reactive, watch,computed } from 'vue'
import { selectGoods,updateGoodsStatus } from '@/api/product'
import { ElMessage } from 'element-plus'

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
  [-1]: 'danger',
  0: 'warning',
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

// 新增：更新商品状态
const updateStatus = async (goodsId, newStatus) => {
  try {
    const response = await updateGoodsStatus({
      id: goodsId,
      status: newStatus
    })
    
    if (response.data.code === 0) {
      ElMessage.success(`商品${newStatus === 1 ? '上架' : '下架'}成功！`)
      
      // 更新本地数据状态 - 避免重新获取整个列表
      const index = goodsList.value.findIndex(item => item.id === goodsId)
      if (index !== -1) {
        goodsList.value[index].status = newStatus
        goodsList.value[index].updateTime = new Date().toISOString()
      }
      
      // 通知父组件状态变化
      emit('status-changed')
    } else {
      ElMessage.error(response.data.message || '操作失败')
    }
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 下架商品
const handleOffShelves = (goodsId) => {
  ElMessageBox.confirm('确定要下架该商品吗？下架后用户将无法购买', '确认下架', {
    confirmButtonText: '确认下架',
    cancelButtonText: '取消',
    type: 'warning',
    center: true
  }).then(() => {
    updateStatus(goodsId, -1)
  }).catch(() => {
    // 用户取消操作
  })
}

// 重新上架商品
const handleReOnShelves = (goodsId) => {
  ElMessageBox.confirm('确定要重新上架该商品吗？', '确认上架', {
    confirmButtonText: '确认上架',
    cancelButtonText: '取消',
    type: 'success',
    center: true
  }).then(() => {
    updateStatus(goodsId,0)
  }).catch(() => {
    // 用户取消操作
  })
}


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
    console.log(response)
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

// 新增排序方法
const sortMethods = {
  salesDesc: (a, b) => b.totalSales - a.totalSales,
  salesAsc: (a, b) => a.totalSales - b.totalSales,
  priceDesc: (a, b) => b.lowPrice - a.lowPrice,
  priceAsc: (a, b) => a.lowPrice - b.lowPrice,
  newest: (a, b) => new Date(b.updateTime) - new Date(a.updateTime)
}
// 使用computed实现前端排序
const sortedGoodsList = computed (() => {
  const list = [...goodsList.value]
  if (props.searchParams.sort && sortMethods[props.searchParams.sort]) {
    return list.sort(sortMethods[props.searchParams.sort])
  }
  return list
})

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
/* 操作按钮容器 */
.actions-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

/* 左侧固定按钮组 */
.fixed-actions {
  display: flex;
  gap: 8px;
}

/* 右侧动态按钮组 */
.dynamic-actions {
  display: flex;
  gap: 8px;
  margin-left: 8px; /* 确保动态按钮靠右 */
}
</style>