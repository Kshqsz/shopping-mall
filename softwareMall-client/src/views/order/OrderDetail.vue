<template>
  <div class="order-detail-container">
    <!-- 头部导航 -->
    <el-page-header @back="router.go(-1)" title="返回" class="page-header">
      <template #content>
        <span class="text-large font-600 mr-3">订单详情</span>
      </template>
    </el-page-header>

    <!-- 订单状态卡片 -->
    <el-card class="status-card" :class="statusClass">
      <div class="status-content">
        <el-icon :size="40" class="status-icon">
          <component :is="statusIcon" />
        </el-icon>
        <div class="status-text">
          <h3>{{ orderStatusText }}</h3>
          <p class="status-desc">{{ statusDesc }}</p>
          <!-- 待付款倒计时 -->
          <div v-if="order.status === 0" class="countdown-text payment-countdown">
            <span v-if="remainingPaymentTime > 0">订单将在 {{ formattedPaymentTime }} 后自动取消</span>
            <span v-else class="expired-text">订单已超时，即将自动取消</span>
          </div>
          
          <!-- 待收货倒计时 -->
          <div v-if="order.status === 2" class="countdown-text confirm-countdown">
            <span v-if="remainingConfirmTime > 0">将在 {{ formattedConfirmTime }} 后自动确认收货</span>
            <span v-else class="expired-text">已超时，即将自动确认收货</span>
          </div>
          
          <!-- 退款处理倒计时 -->
          <div v-if="order.status === 6" class="countdown-text refund-countdown">
            <span v-if="remainingRefundTime > 0">商家需在 {{ formattedRefundTime }} 内处理退款,超时将自动退款</span>
            <span v-else class="expired-text">商家处理超时，系统将自动退款</span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 订单信息 -->
    <el-card class="info-card">
      <el-descriptions title="订单信息" :column="2" border>
        <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ formatDate(order.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ formatDate(order.paymentTime) || '--' }}</el-descriptions-item>
        <el-descriptions-item label="商家名称">{{ order.shopName }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 收货信息 -->
    <el-card class="info-card" v-if="order.receiverName">
      <el-descriptions title="收货信息" :column="1" border>
        <el-descriptions-item label="收货人">{{ order.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ order.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="收货地址">{{ fullAddress }}</el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 物流信息 -->
    <el-card class="info-card">
      <el-descriptions title="物流信息" :column="2" border>
        <el-descriptions-item label="物流状态">
          <el-tag :type="logisticsStatusType">{{ logisticsStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="物流公司" v-if="order.shippingCompany">
          {{ order.shippingCompany }}
        </el-descriptions-item>
        <el-descriptions-item label="物流单号" v-if="order.shippingNumber">
          {{ order.shippingNumber }}
        </el-descriptions-item>
        <el-descriptions-item label="发货时间" v-if="order.shippingTime">
          {{ formatDate(order.shippingTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 商品信息 -->
    <el-card class="info-card" @click="detail(order.productId)">
      <template #header>
        <div class="card-header">
          <span>商品信息</span>
        </div>
      </template>
      <div class="goods-item">
        <el-image 
          :src="order.productImage" 
          :zoom-rate="1.2" 
          fit="cover" 
          class="goods-image"
        />
        <div class="goods-detail">
          <h4 class="goods-name">{{ order.productName }}</h4>
          <p class="goods-spec">{{ order.productSpecs }}</p>
          <div class="goods-price">¥{{ order.productPrice.toFixed(2) }}</div>
        </div>
        <div class="goods-quantity">x{{ order.quantity }}</div>
      </div>
    </el-card>

    <!-- 金额明细 -->
    <el-card class="info-card">
      <template #header>
        <div class="card-header">
          <span>金额明细</span>
        </div>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="商品总价">
          <span class="price">¥{{ (order.productPrice * order.quantity).toFixed(2) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="优惠金额">
          <span class="price">-¥0.00</span>
        </el-descriptions-item>
        <el-descriptions-item label="实付金额">
          <span class="total-price">¥{{ order.totalAmount.toFixed(2) }}</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button 
        v-if="order.status === 0" 
        type="danger" 
        @click="handleCancelOrder"
      >
        取消订单
      </el-button>
      <el-button 
        v-if="order.status === 0" 
        type="primary" 
        @click="handlePayOrder"
      >
        立即支付
      </el-button>
      <el-button 
        v-if="order.status === 2" 
        type="primary" 
        @click="handleConfirmReceipt"
      >
        确认收货
      </el-button>
      <el-button 
        v-if="order.status === 2 || order.status === 1" 
        type="danger" 
        @click="handleReturnGoods"
      >
        退货退款
      </el-button>
      <el-button 
        v-if="order.status === 7" 
        @click="router.push('/homePage')"
      >
        返回首页
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  Timer,
  ShoppingCart,
  Van,
  CircleCheck,
  Close,
  Refresh,
  Goods,
  Warning
} from '@element-plus/icons-vue'
import { getOrderById ,receive,orderCancelService,returnService,agreeReturn} from '@/api/order'
import { codeToText } from 'element-china-area-data';


const route = useRoute()
const router = useRouter()
const detail = (id) => {
  router.push(`/productDetail/${id}`);
};
// 订单数据
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

// 计算属性
const orderStatusText = computed(() => {
  const statusMap = {
    0: '待付款',
    1: '待发货',
    2: '待收货',
    4: '已完成',
    5: '已取消',
    6: '退款中',
    7: '已退款'
  }
  return statusMap[order.value.status] || '未知状态'
})

const statusDesc = computed(() => {
  const descMap = {
    0: '订单已创建，等待付款',
    1: '商家正在准备商品',
    2: '商品已发出，请注意查收',
    4: '交易已完成',
    5: '订单已取消',
    6: '退款申请已提交待商家通过',
    7: '退款已完成'
  }
  return descMap[order.value.status] || ''
})

const statusIcon = computed(() => {
  const iconMap = {
    0: Timer,
    1: ShoppingCart,
    2: Van,
    4: CircleCheck,
    5: Close,
    6: Refresh,
    7: Goods
  }
  return iconMap[order.value.status] || Warning
})
const logisticsStatusText = computed(() => {
  if (order.value.status === 6) return '退货中'
  if (order.value.status === 7) return '已退货'
  if (order.value.status >= 4) return '已签收'
  if (order.value.status >= 2) return '已发货'
  return '未发货'
})

const logisticsStatusType = computed(() => {
  switch (order.value.status) {
    case 2: return 'warning'
    case 4: return 'success'
    case 6: return 'danger'
    case 7: return 'info'
    default: return ''
  }
})
const statusClass = computed(() => {
  const classMap = {
    0: 'status-pending',
    1: 'status-paid',
    2: 'status-shipped',
    4: 'status-completed',
    5: 'status-canceled',
    6: 'status-refunding',
    7: 'status-refunded'
  }
  return classMap[order.value.status] || ''
})

const fullAddress = computed(() => {

  return `${codeToText[order.value.receiverCity]}${codeToText[order.value.receiverProvince]}${codeToText[order.value.receiverDistrict]}${order.value.receiverDetail}`
})

// 方法
const formatDate = (dateString) => {
  if (!dateString) return '--'
  const date = new Date(dateString)
  return date.toLocaleString()
}

const fetchOrderDetail = async (id) => {
  const res = await (await getOrderById(id)).data.data
  order.value = res
  // 根据订单状态启动不同的倒计时
  if (order.value.status === 0) {
    startPaymentCountdown()
  } else if (order.value.status === 2) {
    startConfirmCountdown()
  } else if (order.value.status === 6) {
    startRefundCountdown()
  }
}

const handleCancelOrder = () => {
  // eslint-disable-next-line no-undef
  ElMessageBox.confirm('确定要取消此订单吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await orderCancelService(route.params.id)
    // eslint-disable-next-line no-undef
    ElMessage.success('订单取消成功')
    fetchOrderDetail(route.params.id)
  }).catch(() => {
    // eslint-disable-next-line no-undef
    ElMessage.info('已取消操作')
  })
}

const handlePayOrder = () => {
  router.push({
    path: '/payment',
    query: {
      order: JSON.stringify(order.value)
    }
  })
}

const handleConfirmReceipt = () => {
  // eslint-disable-next-line no-undef
  ElMessageBox.confirm('确认收到商品了吗?', '确认收货', {
    confirmButtonText: '确认收货',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async() => {
    await receive(order.value.id)
    // eslint-disable-next-line no-undef
    ElMessage.success('确认收货成功')
    fetchOrderDetail(route.params.id)
  }).catch(() => {
    // eslint-disable-next-line no-undef
    ElMessage.info('已取消操作')
  })
}

const handleReturnGoods = async () =>{
  try {
    await ElMessageBox.confirm(
      '确定要申请退货退款吗？',
      '确认退货退款',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    await returnService(route.params.id)
    ElMessage.success('退货退款申请已提交')
    fetchOrderDetail(route.params.id)
  } catch (error) {
    if (error !== 'cancel') {
      console.error('申请退货退款失败:', error)
      ElMessage.error('申请退货退款失败')
    }
  }
  
}

// 倒计时相关代码
const remainingPaymentTime = ref(0)    // 待付款倒计时（5分钟）
const remainingConfirmTime = ref(0)    // 自动确认收货倒计时（10天）
const remainingRefundTime = ref(0)     // 自动退款倒计时（5天）
let paymentTimer = null
let confirmTimer = null
let refundTimer = null


// 计算剩余秒数（5分钟倒计时）
const calculatePaymentRemainingTime = () => {
  if (order.value.status !== 0) return 0
  const now = new Date()
  const createdAt = new Date(order.value.createTime)
  const elapsedSeconds = Math.floor((now - createdAt) / 1000)
  return Math.max(0, 300 - elapsedSeconds) // 5分钟=300秒
}

// 计算剩余秒数（10天自动确认收货）
const calculateConfirmRemainingTime = () => {
  if (order.value.status !== 2 || !order.value.shippingTime) return 0
  const now = new Date()
  const shippedAt = new Date(order.value.shippingTime)
  const elapsedSeconds = Math.floor((now - shippedAt) / 1000)
  return Math.max(0, 864000 - elapsedSeconds) // 10天=864000秒
}

// 计算剩余秒数（5天自动退款）
const calculateRefundRemainingTime = () => {
  if (order.value.status !== 6 || !order.value.refundTime) return 0
  const now = new Date()
  const refundAt = new Date(order.value.refundTime)
  const elapsedSeconds = Math.floor((now - refundAt) / 1000)
  return Math.max(0, 432000 - elapsedSeconds) // 5天=432000秒
}

// 格式化支付时间为 MM:SS
const formattedPaymentTime = computed(() => {
  const minutes = Math.floor(remainingPaymentTime.value / 60)
  const seconds = remainingPaymentTime.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 格式化确认收货时间为 X天XX小时
const formattedConfirmTime = computed(() => {
  const totalHours = Math.floor(remainingConfirmTime.value / 3600)
  const days = Math.floor(totalHours / 24)
  const hours = totalHours % 24
  return days > 0 ? `${days}天${hours}小时` : `${hours}小时`
})

// 格式化退款时间为 X天XX小时
const formattedRefundTime = computed(() => {
  const totalHours = Math.floor(remainingRefundTime.value / 3600)
  const days = Math.floor(totalHours / 24)
  const hours = totalHours % 24
  return days > 0 ? `${days}天${hours}小时` : `${hours}小时`
})

// 启动待付款倒计时
const startPaymentCountdown = () => {
  remainingPaymentTime.value = calculatePaymentRemainingTime()
  if (remainingPaymentTime.value <= 0) {
    onPaymentCountdownEnd()
    return
  }
  paymentTimer = setInterval(() => {
    remainingPaymentTime.value -= 1
    if (remainingPaymentTime.value <= 0) {
      clearInterval(paymentTimer)
      onPaymentCountdownEnd()
    }
  }, 1000)
}

// 启动确认收货倒计时
const startConfirmCountdown = () => {
  remainingConfirmTime.value = calculateConfirmRemainingTime()
  if (remainingConfirmTime.value <= 0) {
    onConfirmCountdownEnd()
    return
  }
  confirmTimer = setInterval(() => {
    remainingConfirmTime.value -= 1
    if (remainingConfirmTime.value <= 0) {
      clearInterval(confirmTimer)
      onConfirmCountdownEnd()
    }
  }, 1000)
}

// 启动退款倒计时
const startRefundCountdown = () => {
  remainingRefundTime.value = calculateRefundRemainingTime()
  if (remainingRefundTime.value <= 0) {
    onRefundCountdownEnd()
    return
  }
  refundTimer = setInterval(() => {
    remainingRefundTime.value -= 1
    if (remainingRefundTime.value <= 0) {
      clearInterval(refundTimer)
      onRefundCountdownEnd()
    }
  }, 1000)
}

// 待付款倒计时结束处理
const onPaymentCountdownEnd = async () => {
  try {
    await orderCancelService(order.value.id)
    await fetchOrderDetail(route.params.id)
  } catch (error) {
    console.error('自动取消订单失败:', error)
    await fetchOrderDetail(route.params.id)
  }
}

// 确认收货倒计时结束处理
const onConfirmCountdownEnd = async () => {
  try {
    await receive(order.value.id)
    await fetchOrderDetail(route.params.id)
  } catch (error) {
    console.error('自动确认收货失败:', error)
    await fetchOrderDetail(route.params.id)
  }
}

// 退款倒计时结束处理
const onRefundCountdownEnd = async () => {
  try {
    await agreeReturn(order.value.id)
    console.log(order.value.id)
    await fetchOrderDetail(route.params.id)
  } catch (error) {
    console.error('自动退款失败:', error)
    await fetchOrderDetail(route.params.id)
  }
}

onMounted(() => {
  console.log(route.params.id)
  fetchOrderDetail(route.params.id)
})
</script>

<style scoped>
.order-detail-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: 20px;
}

.status-card {
  margin-bottom: 20px;
  color: white;
}

.status-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.status-icon {
  margin-right: 20px;
}

.status-text h3 {
  margin: 0;
  font-size: 20px;
}

.status-desc {
  margin: 5px 0 0;
  font-size: 14px;
  opacity: 0.9;
}

.status-pending {
  background: linear-gradient(to right, #ff9500, #ff5e3a);
}

.status-paid {
  background: linear-gradient(to right, #1a73e8, #0d47a1);
}

.status-shipped {
  background: linear-gradient(to right, #00c853, #009624);
}

.status-completed {
  background: linear-gradient(to right, #9e9e9e, #616161);
}

.status-canceled {
  background: linear-gradient(to right, #f44336, #c62828);
}

.status-refunding {
  background: linear-gradient(to right, #ff9800, #f57c00);
}

.status-refunded {
  background: linear-gradient(to right, #607d8b, #455a64);
}

.info-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
}

.goods-image {
  width: 100px;
  height: 100px;
  margin-right: 20px;
  border-radius: 4px;
}

.goods-detail {
  flex: 1;
}

.goods-name {
  margin: 0 0 10px;
  font-size: 16px;
}

.goods-spec {
  margin: 0 0 10px;
  color: #999;
  font-size: 14px;
}

.goods-price {
  color: #ff4444;
  font-weight: bold;
  font-size: 16px;
}

.goods-quantity {
  color: #666;
  font-size: 16px;
}

.price {
  color: #ff4444;
}

.total-price {
  color: #ff4444;
  font-weight: bold;
  font-size: 16px;
}

.action-buttons {
  margin-top: 20px;
  text-align: right;
}

.action-buttons .el-button {
  margin-left: 10px;
}
/* 倒计时样式 */
.countdown-text {
  margin-top: 10px;
  display: flex;
  align-items: center;
  font-size: 14px;
}

.countdown-text .el-icon {
  margin-right: 5px;
}

/* 支付倒计时样式 */
.payment-countdown .el-icon {
  color: #ff9900; /* 橙色 */
}

/* 确认收货倒计时样式 */
.confirm-countdown .el-icon {
  color: #67c23a; /* 绿色 */
}

/* 退款倒计时样式 */
.refund-countdown .el-icon {
  color: #e6a23c; /* 黄色 */
}

/* 过期文本样式 */
.expired-text {
  color: #f56c6c; /* 红色 */
  font-weight: bold;
}

</style>