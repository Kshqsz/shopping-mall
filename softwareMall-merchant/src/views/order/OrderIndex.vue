<template>
  <div class="order-management-container">
    <h2>订单管理</h2>
    
    <!-- 订单筛选 -->
    <div class="filters">
      <div class="filter-item">
        <el-input
          v-model="searchParams.orderNo"
          placeholder="订单编号"
          clearable
          style="width: 220px; margin-right: 10px"
        />
        <el-select
          v-model="searchParams.status"
          placeholder="订单状态"
          clearable
          style="width: 140px; margin-right: 10px"
        >
          <el-option label="全部" value="" />
          <el-option label="待支付" :value="0" />
          <el-option label="待发货" :value="1" />
          <el-option label="待收货" :value="2" />
          <el-option label="已完成" :value="4" />
          <el-option label="已取消" :value="5" />
          <el-option label="退款中" :value="6" />
          <el-option label="已退款" :value="7" />
        </el-select>
        <el-button type="primary" @click="searchOrders">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </div>
    </div>

    <!-- 订单列表表格 -->
    <el-table :data="paginatedOrders" style="width: 100%" border height="calc(100vh - 280px)">
      <el-table-column prop="orderNo" label="订单编号" width="180" />
      <el-table-column label="商品信息" width="300">
        <template #default="{ row }">
          <div class="product-info">
            <el-image
              :src="row.productImage"
              fit="cover"
              class="product-image"
              style="width: 50px; height: 50px"
            />
            <div class="product-details">
              <div class="product-name">{{ row.productName }}</div>
            </div>
            
          </div>
        </template>
      </el-table-column>
      <el-table-column label="商品规格" width="200">
        <template #default="{ row }">
          <div class="product-info">

            <div class="product-details">
              <div class="product-name">{{ row.productSpecs }}</div>
            </div>
            
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="订单状态" width="140">
        <template #default="{ row }">
          <el-tag :type="getStatusTagType(row.status)" size="large">
            {{ getStatusText(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="订单金额" width="150">
        <template #default="{ row }">
          <span class="price">¥{{ row.totalAmount.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <div class="actions">
            <el-button size="middle" @click="viewOrderDetail(row.id)" type="primary">详情</el-button>
            <el-button
              v-if="row.status === 1"
              size="middle"
              type="primary"
              @click="handleShip(row)"
             
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 6"
              size="middle"
              type="danger"
              @click="returnService(row.id)"
            >
              同意退款
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页控件 -->
    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="filteredOrders.length"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- 发货对话框 -->
    <el-dialog v-model="shipDialogVisible" title="订单发货" width="500px">
      <el-form :model="shipForm" label-width="80px">
        <el-form-item label="物流公司">
          <el-input v-model="shipForm.shippingCompany" placeholder="请输入物流公司名称" />
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="shipForm.shippingNumber" placeholder="请输入物流单号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="shipDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmShip">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getOrderList ,deliver,agreeReturn} from '@/api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const orderList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const shipDialogVisible = ref(false)


const searchParams = ref({
  orderNo: '',
  status: '',
})

const shipForm = ref({
  id:null,
  shippingCompany: '',
  shippingNumber: ''
})

// 获取订单列表
const fetchOrders = async () => {
  try {
    const res = await getOrderList({
      keyword: searchParams.value.orderNo,
      status: searchParams.value.status,
    })
    orderList.value = res.data.data
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '--'
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 订单状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'warning', // 待支付
    1: '',         // 待发货
    2: 'info',     // 待收货
    4: 'success',  // 已完成
    5: 'danger',   // 已取消
    6: 'warning',  // 退款中
    7: 'info'      // 已退款
  }
  return typeMap[status] || ''
}

// 订单状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '待发货',
    2: '待收货',
    4: '已完成',
    5: '已取消',
    6: '退款中',
    7: '已退款'
  }
  return textMap[status] || '未知状态'
}

// 过滤订单
const filteredOrders = computed(() => {
  return orderList.value.filter(order => {
    const matchesOrderNo = searchParams.value.orderNo
      ? order.orderNo.includes(searchParams.value.orderNo)
      : true
    
    const matchesStatus = searchParams.value.status !== ''
      ? order.status === parseInt(searchParams.value.status)
      : true
    
    return matchesOrderNo && matchesStatus
  })
})

// 分页订单
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

// 查询订单
const searchOrders = () => {
  currentPage.value = 1
  fetchOrders()
}

// 重置查询
const resetSearch = () => {
  searchParams.value = {
    orderNo: '',
    status: '',
  }
  searchOrders()
}

// 查看订单详情
const viewOrderDetail = (id) => {
  router.push(`/orderDetail/${id}`)
}

// 处理发货
const handleShip = (order) => {
  shipForm.value.id = order.id
  shipDialogVisible.value = true
}

// 确认发货
const confirmShip = async () => {
  if (!shipForm.value.shippingCompany || !shipForm.value.shippingNumber) {
    ElMessage.warning('请填写完整的物流信息')
    return
  }

  try {
    await deliver(shipForm.value)
    ElMessage.success('发货成功')
    shipDialogVisible.value = false
    fetchOrders()
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
}

// 分页页码改变
const handlePageChange = () => {
  // 可以在这里添加页码改变时的逻辑
}

//处理退款
const returnService = async (id) => {
  await agreeReturn(id)
  fetchOrders()
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-management-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

h2 {
  margin-bottom: 24px;
  color: #333;
  font-weight: 600;
}

.filters {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.filter-item {
  display: flex;
  gap: 10px;
  align-items: center;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 0;
}

.product-image {
  border-radius: 4px;
  object-fit: cover;
}

.product-details {
  flex: 1;
  overflow: hidden;
}

.product-name {
  font-size: 14px;
  color: #333;
  line-height: 1.4;
  word-break: break-all;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.actions {
  display: flex;
  gap: 8px;             /* 按钮之间的间隙 */
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table__row) {
  height: 80px;
}

:deep(.el-table td) {
  padding: 12px 0;
}
</style>