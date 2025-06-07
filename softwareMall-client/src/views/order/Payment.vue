<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { orderCancelService, orderPayService } from '@/api/order'
import { codeToText } from 'element-china-area-data';

// 获取路由参数
const route = useRoute();
const router = useRouter();
const dialogVisible = ref(false);
const order = JSON.parse(route.query.order);
console.log(order)


// 支付操作
const pay = async () => {
  try {
    await orderPayService(order.id)
    ElMessage.success("支付成功！")
    dialogVisible.value = true
  } catch (error) {
    console.error('支付失败:', error)
    ElMessage.error("支付失败，请重试")
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
      console.log(order)
      await orderCancelService(order.id)
      ElMessage.success('订单已取消')
      router.push(`/productDetail/${order.productId}`)
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
  return `${codeToText[order.receiverCity]}${codeToText[order.receiverProvince]}${codeToText[order.receiverDistrict]}${order.receiverDetail}`
}
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
          <el-tag :type="order.status === 0 ? 'warning' : 'success'">
            {{ order.status === 0 ? '待支付' : '已完成' }}
          </el-tag>
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

      <!-- 支付和取消按钮区域 -->
      <div class="payment-actions">
        <el-button 
          type="default" 
          size="large" 
          class="cancel-button" 
          @click="cancelOrder"
          v-if="order.status === 0"
        >
          取消订单
        </el-button>
        <el-button 
          type="primary" 
          size="large" 
          class="pay-button" 
          @click="pay" 
          v-if="order.status === 0"
        >
          <i class="fab fa-weixin wechat-icon"></i> 微信支付
        </el-button>
      </div>
    </el-card>

    <!-- 支付成功弹窗 -->
    <el-dialog v-model="dialogVisible" title="支付成功" width="500px">
      <div class="success-dialog">
        <i class="el-icon-success success-icon"></i>
        <h3>支付成功！</h3>
        <p>您的订单已支付成功，我们将尽快为您处理发货</p>
        
        <div class="dialog-actions">
          <el-button type="primary" @click="viewOrderDetails">查看订单</el-button>
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
</style>