<template>
  <div class="order-detail-container">
    <el-page-header @back="router.go(-1)" title="返回" class="page-header">
      <template #content>
        <h2 class="text-large font-600">订单详情</h2>
      </template>
    </el-page-header>

    <el-card class="info-card" shadow="hover">
      <div class="order-header">
        <div class="status-info">
          <el-tag 
            :type="getStatusTagType(order.status)" 
            size="large"
            :class="'status-tag status-' + order.status"
          >
            
            {{ getStatusText(order.status) }}
          </el-tag>
          <div class="action-buttons">
            <el-button 
              type="primary" 
              size="small" 
              v-if="order.status === 1"
              @click="showDeliverDialog = true"
            >
              发货
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              v-if="order.status === 6"
              @click="handleAgreeReturn()"
              :icon="CircleCheck"
            >
              同意退款
            </el-button>
          </div>
        </div>
        <div class="order-no">订单编号: {{ order.orderNo }}</div>
      </div>
      
      <!-- 发货对话框 -->
      <el-dialog v-model="showDeliverDialog" title="订单发货" width="500px">
        <el-form :model="deliverForm" label-width="80px">
          <el-form-item label="物流公司" required>
            <el-select v-model="deliverForm.shippingCompany" placeholder="请选择物流公司">
              <el-option
                v-for="item in shippingCompanies"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="物流单号" required>
            <el-input v-model="deliverForm.shippingNumber" placeholder="请输入物流单号" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showDeliverDialog = false">取消</el-button>
          <el-button type="primary" @click="handleDeliver">确认发货</el-button>
        </template>
      </el-dialog>
      
      <el-divider />
      
      <!-- 商品信息 -->
      <div class="section-title">商品信息</div>
      <div class="product-info">
        <el-image 
          :src="order.productImage" 
          class="product-image"
          :preview-src-list="[order.productImage]"
          :initial-index="0"
          fit="cover"
        />
        <div class="product-details">
          <div class="name">{{ order.productName }}</div>
          <div class="specs">{{ order.productSpecs }}</div>
          <div class="price-quantity">
            <span>¥{{ order.productPrice.toFixed(2) }}</span>
            <span>× {{ order.quantity }}</span>
          </div>
        </div>
      </div>
      
      <!-- 金额信息 -->
      <el-divider />
      <div class="section-title">金额信息</div>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="商品总价">
          <span class="price">¥{{ (order.productPrice * order.quantity).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="运费">¥0.00</el-descriptions-item>
        <el-descriptions-item label="优惠">-¥0.00</el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span>
        </el-descriptions-item>
      </el-descriptions>
      
      <!-- 买家信息 -->
      <el-divider />
      <div class="section-title">买家信息</div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="买家名称">{{ order.username || '--' }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ order.receiverPhone || '--' }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 收货信息 -->
      <el-divider v-if="order.receiverName" />
      <div class="section-title" v-if="order.receiverName">收货信息</div>
      <el-descriptions :column="1" border v-if="order.receiverName">
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ fullAddress }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 发货信息 -->
      <el-divider v-if="order.shippingCompany" />
      <div class="section-title" v-if="order.shippingCompany">发货信息</div>
      <el-descriptions :column="1" border v-if="order.shippingCompany">
        <el-descriptions-item label="物流公司">{{ order.shippingCompany }}</el-descriptions-item>
        <el-descriptions-item label="物流单号">{{ order.shippingNumber }}</el-descriptions-item>
        <el-descriptions-item label="发货时间">{{ formatDate(order.shippingTime) }}</el-descriptions-item>
      </el-descriptions>
      
      <!-- 时间信息 -->
      <el-divider />
      <div class="section-title">时间信息</div>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="创建时间">{{ formatDate(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="付款时间">{{ order.paymentTime ? formatDate(order.paymentTime) : '--' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ order.completeTime ? formatDate(order.completeTime) : '--' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getOrderById, deliver, agreeReturn } from '@/api/order'
import { codeToText } from 'element-china-area-data'


const route = useRoute()
const router = useRouter()
const order = ref({
  id: null,
  orderNo: '',
  createTime: '',
  paymentTime: null,
  cancelTime: null,
  shippingTime: null,
  completeTime: null,
  refundTime: null,
  status: 0,
  userId: null,
  username: '',
  merchantId: null,
  shopName: '',
  productId: null,
  productName: '',
  productImage: '',
  productPrice: 0,
  productSpecs: '',
  specId: null,
  quantity: 1,
  totalAmount: 0,
  receiverName: '',
  receiverPhone: '',
  receiverProvince: '',
  receiverCity: '',
  receiverDistrict: '',
  receiverDetail: null,
  shippingCompany: null,
  shippingNumber: null
})

const showDeliverDialog = ref(false)
const deliverForm = ref({
  shippingCompany: '',
  shippingNumber: ''
})

const shippingCompanies = ref([
  { value: '顺丰速运', label: '顺丰速运' },
  { value: '中通快递', label: '中通快递' },
  { value: '圆通速递', label: '圆通速递' },
  { value: '申通快递', label: '申通快递' },
  { value: '韵达快递', label: '韵达快递' },
  { value: '京东物流', label: '京东物流' },
  { value: '邮政EMS', label: '邮政EMS' }
])

// 获取订单详情
const fetchOrderDetail = async () => {
  try {
    const res = await getOrderById(route.params.id)
    order.value = res.data.data
  } catch (error) {
    console.error('获取订单详情失败:', error)
    ElMessage.error('获取订单详情失败')
  }
}

// 处理发货
const handleDeliver = async () => {
  if (!deliverForm.value.shippingCompany) {
    ElMessage.warning('请选择物流公司')
    return
  }
  if (!deliverForm.value.shippingNumber) {
    ElMessage.warning('请输入物流单号')
    return
  }
  
  try {
    await deliver({
      id: order.value.id,
      shippingCompany: deliverForm.value.shippingCompany,
      shippingNumber: deliverForm.value.shippingNumber
    })
    ElMessage.success('发货成功')
    showDeliverDialog.value = false
    fetchOrderDetail() // 刷新订单数据
  } catch (error) {
    console.error('发货失败:', error)
    ElMessage.error('发货失败')
  }
}

// 处理同意退款
const handleAgreeReturn = async () => {
  try {
    // eslint-disable-next-line no-undef
    await ElMessageBox.confirm('确定同意该订单的退款申请吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await agreeReturn(order.value.id)
    ElMessage.success('退款处理成功')
    fetchOrderDetail() // 刷新订单数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退款处理失败:', error)
      ElMessage.error('退款处理失败')
    }
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '--'
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 格式化地址
const fullAddress = computed(() => {
  const { receiverProvince, receiverCity, receiverDistrict, receiverDetail } = order.value
  return `${codeToText[receiverProvince] || ''}${codeToText[receiverCity] || ''}${codeToText[receiverDistrict] || ''}${receiverDetail || ''}`
})

// 订单状态标签类型
const getStatusTagType = (status) => {
  const typeMap = {
    0: 'warning', // 待支付
    1: 'primary', // 待发货
    2: 'info',    // 待收货
    4: 'success', // 已完成
    5: 'danger',  // 已取消
    6: 'warning', // 退款中
    7: 'info'     // 已退款
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

onMounted(() => {
  fetchOrderDetail()
})
</script>

<style scoped>
.order-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.info-card {
  padding: 25px;
  border-radius: 8px;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.status-tag {
  font-size: 16px;
  padding: 0 12px;
  height: 32px;
  line-height: 32px;
}

.status-tag .status-icon {
  margin-right: 6px;
  display: inline-flex;
  align-items: center;
}

.status-0 {
  background-color: #fdf6ec;
  border-color: #f5dab1;
  color: #e6a23c;
}

.status-1 {
  background-color: #ecf5ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.status-2 {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
}

.status-4 {
  background-color: #f0f9eb;
  border-color: #c2e7b0;
  color: #67c23a;
}

.status-5 {
  background-color: #fef0f0;
  border-color: #fbc4c4;
  color: #f56c6c;
}

.status-6 {
  background-color: #fdf6ec;
  border-color: #f5dab1;
  color: #e6a23c;
}

.status-7 {
  background-color: #f4f4f5;
  border-color: #d3d4d6;
  color: #909399;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.order-no {
  font-size: 16px;
  color: #666;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin: 15px 0;
  color: #333;
}

.product-info {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 6px;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  object-fit: cover;
}

.product-details {
  flex: 1;
}

.product-details .name {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
}

.product-details .specs {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.price-quantity {
  font-size: 16px;
  color: #e1251b;
  font-weight: bold;
}

.price-quantity span {
  margin-right: 15px;
}

.price {
  color: #e1251b;
  font-weight: bold;
}

.total-price {
  color: #e1251b;
  font-size: 16px;
  font-weight: bold;
}

:deep(.el-descriptions__body) {
  background: #f9f9f9;
}
</style>