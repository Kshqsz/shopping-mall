<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router';
import { productGetByIdService } from '@/api/product';
import { useUserStore } from '@/stores';
import { merchantGetByIdService } from '@/api/merchant'
import { orderCancelService, orderPayService } from '@/api/order'

// 获取路由参数
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
const user = userStore.user;
const dialogVisible = ref(false);
const merchant = ref(null)
const product = ref(null)
const price = ref(0)
const order = JSON.parse(route.query.order);

const getMerchant = async () => {
  const id = order.merchantId;
  const response = await merchantGetByIdService(id);
  merchant.value = response.data.data;
}

const pay = async () => {
  const number = order.orderNumber;
  await orderPayService(number);
  ElMessage.success("支付成功~");
  dialogVisible.value = true;
}
const getProduct = async () => {
  const id = order.productId;
  const response = await productGetByIdService(id);
  product.value = response.data.data;
  price.value = product.value.price;
}

const download = () => {
  const url = product.value.source;
    // 创建一个隐藏的 <a> 元素
  const link = document.createElement('a');
  link.href = url;
  link.download = product.name; // 可选，设置下载文件的默认文件名
  //link.target = '_blank'; // 可选，避免干扰用户当前页面
  document.body.appendChild(link); // 将 <a> 添加到文档中
  link.click(); // 模拟点击下载
  document.body.removeChild(link); // 下载完成后移除 <a>
  router.push("/home")
}
// 解析订单对象
onMounted(async () => {
  await getMerchant();
  await getProduct();
})

// 取消订单函数（你可以根据需要实现）
const cancelOrder = () => {
  ElMessageBox.confirm(
    '你确定要取消订单吗？',
    '确认',
    {
      confirmButtonText: 'OK',
      cancelButtonText: 'Cancel',
      type: 'warning',
    }
  )
    .then(async () => {
      await orderCancelService(order.orderNumber);
      router.go(-1);
      ElMessage({
        type: 'success',
        message: '订单取消成功',
      })
    })
}
</script>

<template>
  <div class="payment-page">
    <!-- 页面标题 -->
    <h1 class="title">订单支付</h1>

    <!-- 订单信息展示 -->
    <el-card class="order-card" shadow="always">
      <div class="order-info">
        <el-row gutter={20}>
          <!-- 左侧商品图片，只有在 product 数据加载完成后才渲染 -->
          <el-col :span="8" class="product-image" v-if="product && product.image">
            <img :src="product.image" alt="商品图片" class="product-img" />
          </el-col>

          <!-- 右侧订单信息 -->
          <el-col :span="16">
            <el-row>
              <el-col :span="6"><strong>订单号：</strong></el-col>
              <el-col :span="18">{{ order.orderNumber }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="6"><strong>用户名：</strong></el-col>
              <el-col :span="18">{{ user.username }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="6"><strong>商品名：</strong></el-col>
              <el-col :span="18">{{ product ? product.name : '加载中...' }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="6"><strong>商品价格：</strong></el-col>
              <el-col :span="18">{{ price }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="6"><strong>商家名：</strong></el-col>
              <el-col :span="18">{{ merchant ? merchant.username : '加载中...' }}</el-col>
            </el-row>
            <el-row>
              <el-col :span="6"><strong>订单状态：</strong></el-col>
              <el-col :span="18">{{ order.status === 0 ? '待支付' : '已完成' }}</el-col>
            </el-row>
          </el-col>
        </el-row>
      </div>

      <!-- 支付和取消按钮区域 -->
      <div class="payment-actions">
        <el-button type="danger" size="large" class="cancel-button" @click="cancelOrder">
          取消订单
        </el-button>
        <el-button type="primary" size="large" class="pay-button" @click="pay">
          <i class="fab fa-weixin wechat-icon" style="padding-top: 10px;"></i> 使用微信支付
        </el-button>
      </div>
    </el-card>
                <!-- 支付成功后弹窗 -->
    <el-dialog v-model="dialogVisible" title="支付成功" width="400px">
      <p>支付成功！现在可以下载软件。</p>
      <div class="download-actions">
        <el-button type="primary" size="large" @click="download">
          下载软件
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<style scoped>

/* 弹窗的样式 */
.download-actions {
  text-align: center;
  margin-top: 20px;
}
.payment-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.title {
  margin-bottom: 20px;
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.order-card {
  display: flex;
  flex-direction: column;
  margin-bottom: 40px;
}

.order-info {
  font-size: 16px;
  line-height: 1.8;
}

.product-image {
  text-align: center;
}

.product-img {
  width: 100%;
  height: auto;
  max-height: 200px;
  object-fit: cover;
  border-radius: 10px;
}

.payment-actions {
  margin-left: 30%;
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.pay-button {
  background-color: #1aad19;
  color: white;
  font-size: 18px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 15px 40px;
  border-radius: 30px;
}

.pay-button:hover {
  background-color: #128a13;
}

.wechat-icon {
  width: 30px;
  height: 30px;
  margin-right: 10px;
}

.cancel-button {
  background-color: #f56c6c;
  color: white;
  font-size: 18px;
  padding: 15px 40px;
  border-radius: 30px;
}

.cancel-button:hover {
  background-color: #f44336;
}
</style>
