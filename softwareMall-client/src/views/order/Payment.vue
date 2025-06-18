<script setup>
import { onMounted, ref,computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderCancelService, orderPayService ,getOrderById} from '@/api/order'
import { codeToText } from 'element-china-area-data'
import { ElMessage, ElMessageBox } from 'element-plus'
// 获取路由参数
const route = useRoute()
const router = useRouter()
const dialogVisible = ref(false)
const isPaying = ref(false)
const orderId = ref(JSON.parse(route.query.order))
const order = ref({});
// 支付操作
const pay = async () => {
  try {
    isPaying.value = true
    // 添加支付中的视觉效果
    ElMessage.info('正在处理支付...')
    
    // 模拟支付延迟效果
    await new Promise(resolve => setTimeout(resolve, 1500))
    
    const res = await orderPayService(order.value.id)
    
    if (res.data.code === 0) {
      // 支付成功
      ElMessage.success("支付成功！")
      dialogVisible.value = true
      
      // 更新订单状态
      order.value.status = 1
      order.value.paymentTime = new Date().toISOString()
    } else {
      ElMessage.error(res.message || "支付失败，请重试")
    }
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error("支付失败，请重试")
  } finally {
    isPaying.value = false
  }
}

// 取消订单
const cancelOrder = () => {
  ElMessageBox.confirm(
    '确定要取消订单吗？',
    '确认取消',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const res = await orderCancelService(order.value.id)
      if (res.data.code === 0) {
        ElMessage.success('订单已取消')
        router.push(`/productDetail/${order.value.productId}`)
      } else {
        ElMessage.error(res.message || '取消订单失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
      ElMessage.error('取消订单失败')
    }
  })
}

// 格式化时间
const formatDate = (timestamp) => {
  if (!timestamp) return '--'
  const date = new Date(timestamp)
  return date.toLocaleString()
}

// 格式化地址
const formatAddress = (order) => {
  return `${codeToText[order.receiverProvince] || ''}${codeToText[order.receiverCity] || ''}${codeToText[order.receiverDistrict] || ''}${order.receiverDetail || ''}`
}

const viewOrderDetails = (id) => {
  router.push(`/orderDetail/${id}`)
}

// 倒计时相关代码
const remainingTime = ref(0) // 剩余秒数
let timer = null

// 计算剩余秒数（30分钟倒计时）
const calculateRemainingTime = () => {
  if (order.value.status !== 0) return 0
  
  const now = new Date()
  const createdAt = new Date(order.value.createTime)
  const elapsedSeconds = Math.floor((now - createdAt) / 1000)
  const remaining = 300 - elapsedSeconds // 30分钟=1800秒
  
  return Math.max(0, remaining)
}

// 格式化时间为 MM:SS
const formattedTime = computed(() => {
  const minutes = Math.floor(remainingTime.value / 60)
  const seconds = remainingTime.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 开始倒计时（每秒更新一次）
const startCountdown = () => {
  remainingTime.value = calculateRemainingTime()
  
  timer = setInterval(() => {
    remainingTime.value -= 1
    
    if (remainingTime.value <= 0) {
      clearInterval(timer)
      onCountdownEnd()
    }
  }, 1000) // 每秒更新一次
}

// 倒计时结束后执行的方法
const onCountdownEnd = async () => {
  try {
    // 1. 调用取消订单API
    await orderCancelService(order.value.id)
    // 2. 刷新订单状态
    order.value = (await getOrderById(orderId.value.id)).data.data;
    
  } catch (error) {
    console.error('自动取消订单失败:', error)
    // 如果取消失败（比如订单已支付），刷新状态
    order.value = (await getOrderById(orderId.value.id)).data.data;
  }
}


onMounted(async () => {
  order.value = (await getOrderById(orderId.value.id)).data.data;
  if (order.value.status === 0) {
    startCountdown()
  }
});
</script>

<template>
  <div class="payment-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="title">订单支付</h1>
      <p class="order-no">订单号: {{ order.orderNo }}</p>
    </div>

    <!-- 订单信息展示 -->
    <el-card class="order-card" shadow="hover">
      <div class="order-header">
        <div class="status-info">
          订单状态: 
          <el-tag 
              :type="{
                0: 'warning',
                1: 'success',
                5: 'danger'
              }[order.status]"
            >
              {{
                {
                  0: '待支付',
                  1: '已支付',
                  2: '已发货',
                  4: '已完成',
                  5: '已取消'
                }[order.status] || '未知状态'
              }}
            </el-tag>
        </div>
        <!-- 添加倒计时显示 -->
          <!-- 精确到秒的倒计时显示 -->
          <div v-if="order.status === 0" class="countdown-text">
            <span v-if="remainingTime > 0">订单将在 {{ formattedTime }} 后自动取消</span>
            <span v-else class="expired-text">订单已超时，即将自动取消</span>
          </div>
        <div class="create-time">
          创建时间: {{ formatDate(order.createTime) }}
        </div>
      </div>
      
      <div class="order-info">
        <!-- 商品图片 -->
        <div class="product-image">
          <img 
            :src="order.productImage" 
            :alt="order.productName" 
            class="product-img"
          />
        </div>
        
        <div class="order-details">
          <div class="detail-item">
            <span class="detail-label">商品名称:</span>
            <span class="detail-value">{{ order.productName }}</span>
          </div>
          
          <div class="detail-item">
            <span class="detail-label">商品规格:</span>
            <span class="detail-value">{{ order.productSpecs }}</span>
          </div>
          
          <div class="detail-item">
            <span class="detail-label">商品单价:</span>
            <span class="detail-value">¥{{ order.productPrice?.toFixed(2) }}</span>
          </div>
          
          <div class="detail-item">
            <span class="detail-label">购买数量:</span>
            <span class="detail-value">{{ order.quantity }}</span>
          </div>
          
          <div class="detail-item">
            <span class="detail-label">订单总额:</span>
            <span class="detail-value total-price">¥{{ order.totalAmount?.toFixed(2) }}</span>
          </div>
          
          <div class="detail-item">
            <span class="detail-label">商家名称:</span>
            <span class="detail-value">{{ order.shopName || '--' }}</span>
          </div>
        </div>
      </div>

      <!-- 收货地址信息 -->
      <div class="shipping-info">
        <h3>收货信息</h3>
        <div class="address-details">
          <div class="address-item">
            <span class="address-label">收货人:</span>
            <span class="address-value">{{ order.receiverName }}</span>
          </div>
          <div class="address-item">
            <span class="address-label">联系电话:</span>
            <span class="address-value">{{ order.receiverPhone }}</span>
          </div>
          <div class="address-item">
            <span class="address-label">收货地址:</span>
            <span class="address-value">{{ formatAddress(order) }}</span>
          </div>
        </div>
      </div>

      <!-- 支付时间信息 -->
      <div class="time-info" v-if="order.status !== 0">
        <h3>支付信息</h3>
        <div class="time-details">
          <div class="time-item">
            <span class="time-label">支付时间:</span>
            <span class="time-value">{{ formatDate(order.paymentTime) }}</span>
          </div>
        </div>
      </div>

      <!-- 支付和取消按钮区域 -->
      <div class="payment-actions" v-if="order.status === 0">
        <el-button 
          type="default" 
          size="large" 
          class="cancel-button" 
          @click="cancelOrder"
        >
          取消订单
        </el-button>
        <el-button 
          type="primary" 
          size="large" 
          class="pay-button" 
          @click="pay" 
          :loading="isPaying"
          :disabled="isPaying"
        >
          <i class="fab fa-weixin wechat-icon"></i> 微信支付
        </el-button>
      </div>

      <!-- 已支付状态下的操作按钮 -->
      <div class="payment-actions" v-else>
        <el-button 
          type="primary" 
          size="large" 
          class="view-button" 
          @click="viewOrderDetails(order.id)"
        >
          查看订单详情
        </el-button>
      </div>
    </el-card>

    <!-- 支付成功弹窗 -->
    <el-dialog v-model="dialogVisible" title="支付成功" width="500px" :show-close="false">
      <div class="success-dialog">
        <i class="el-icon-success success-icon"></i>
        <h3>支付成功！</h3>
        <p>您的订单已支付成功，我们将尽快为您处理发货</p>
        
        <div class="dialog-actions">
          <el-button type="primary" @click="viewOrderDetails(order.id)">查看订单</el-button>
          <el-button @click="router.push('/homePage')">继续购物</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>
.payment-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #f8f9fa;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.order-no {
  font-size: 16px;
  color: #666;
}

.order-card {
  margin-bottom: 30px;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
}

.status-info {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 500;
}

.create-time {
  color: #909399;
  font-size: 14px;
}

.order-info {
  display: flex;
  padding: 20px;
  border-bottom: 1px dashed #ebeef5;
}

.product-image {
  width: 180px;
  height: 180px;
  margin-right: 20px;
  background: #f8f8f8;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.product-img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.order-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.detail-item {
  display: flex;
  margin-bottom: 12px;
}

.detail-label {
  width: 100px;
  color: #606266;
}

.detail-value {
  flex: 1;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
}

.shipping-info, .time-info {
  padding: 20px;
  border-bottom: 1px dashed #ebeef5;
}

.shipping-info h3, .time-info h3 {
  font-size: 16px;
  margin-bottom: 15px;
  color: #333;
}

.address-details, .time-details {
  padding-left: 10px;
}

.address-item, .time-item {
  display: flex;
  margin-bottom: 10px;
}

.address-label, .time-label {
  width: 80px;
  color: #909399;
}

.payment-actions {
  display: flex;
  justify-content: flex-end;
  padding: 20px;
  gap: 15px;
}

.cancel-button {
  width: 150px;
  height: 50px;
  font-size: 16px;
}

.pay-button, .view-button {
  width: 180px;
  height: 50px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.pay-button {
  background-color: #67C23A;
  border-color: #67C23A;
}

.wechat-icon {
  font-size: 24px;
  margin-right: 10px;
}

.success-dialog {
  text-align: center;
  padding: 20px;
}

.success-icon {
  font-size: 60px;
  color: #67C23A;
  margin-bottom: 20px;
}

.success-dialog h3 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 15px;
}

.success-dialog p {
  font-size: 16px;
  color: #606266;
  margin-bottom: 30px;
}

.dialog-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.dialog-actions .el-button {
  width: 120px;
}
.countdown-text {
  color: #ca2323;
}
</style>