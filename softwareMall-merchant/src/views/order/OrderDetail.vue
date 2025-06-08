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
          <el-tag :type="getStatusTagType(order.status)" size="large">
            {{ getStatusText(order.status) }}
          </el-tag>
        </div>
        <div class="order-no">订单编号: {{ order.orderNo }}</div>
      </div>
      
      <el-divider />
      
      <!-- 商品信息 -->
      <div class="section-title">商品信息</div>
      <div class="product-info">
        <el-image 
          :src="order.productImage" 
          class="product-image"
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
import { getOrderById } from '@/api/order'
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