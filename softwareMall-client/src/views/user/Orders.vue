<script setup>
import { ref, onMounted } from 'vue';
import { useUserStore } from '@/stores'; // 用户store（可根据需要修改）
import { productGetByIdService } from '@/api/product'; // 获取产品详情的API
import { orderAllService } from '@/api/order'; // 获取订单API
import { merchantGetByIdService } from '@/api/merchant'; // 获取商家信息API
import { useRouter } from 'vue-router';

const userStore = useUserStore();
const router = useRouter();
const user = userStore.user;
const orders = ref([]); // 存储订单数据
const filteredOrders = ref([]); // 用于存储筛选后的订单
const currentPage = ref(1); // 当前分页页码
const pageSize = ref(5); // 每页展示数量

// 筛选条件
const orderStatus = ref('all'); // 默认显示所有订单

// 获取订单列表并填充商品和商家信息
const getOrders = async () => {
  const res = await orderAllService();
  let rawOrders = res.data.data.filter(item => item.userId === user.id);

  // 并行获取商品和商家信息
  const enrichedOrders = await Promise.all(
    rawOrders.map(async (order) => {
      const [productRes, merchantRes] = await Promise.all([
        productGetByIdService(order.productId),
        merchantGetByIdService(order.merchantId),
      ]);
      return {
        ...order,
        productImage: productRes.data.data.image,
        productName: productRes.data.data.name,
        merchantName: merchantRes.data.data.username,
      };
    })
  );

  orders.value = enrichedOrders;
  console.log('Orders:', orders.value); // 调试输出所有订单
  applyFilter(); // 获取订单后应用筛选条件
};

// 筛选订单状态
const applyFilter = () => {
  if (orderStatus.value === 'all') {
    filteredOrders.value = orders.value;
  } else {
    filteredOrders.value = orders.value.filter(order => order.status === parseInt(orderStatus.value));
  }
  console.log('Filtered Orders:', filteredOrders.value); // 调试输出筛选后的订单
};

// 处理状态变化，更新筛选后的订单
const handleStatusChange = () => {
  applyFilter();
};

// 分页改变时触发
const handlePageChange = (page) => {
  currentPage.value = page;
};

// 支付操作
const handlePay = (order) => {
  console.log('支付订单', order);
  router.push({
    path: '/payment',
    query: {
      order: JSON.stringify(order), // 序列化对象
    },
  });
};

// 下载操作
const handleDownload = async (order) => {
  const res = await productGetByIdService(order.productId);
  const product = res.data.data;
  const url = product.source;
    // 创建一个隐藏的 <a> 元素
  const link = document.createElement('a');
  link.href = url;
  link.download = product.name; // 可选，设置下载文件的默认文件名
  //link.target = '_blank'; // 可选，避免干扰用户当前页面
  document.body.appendChild(link); // 将 <a> 添加到文档中
  link.click(); // 模拟点击下载
  document.body.removeChild(link); // 下载完成后移除 <a>
  console.log('下载软件', order);
};

// 重新查看商品操作
const handleViewProduct = (order) => {
  router.push(`/productDetail/${order.productId}`)
  console.log('查看商品', order);
};

// 获取订单状态的文本描述
const getOrderStatus = (status) => {
  switch (status) {
    case 0:
      return '待支付';
    case 1:
      return '已完成';
    case -1:
      return '已取消';
    default:
      return '未知状态';
  }
};

// 页面加载时获取数据
onMounted(() => {
  getOrders();
});
</script>

<template>
  <el-card class="content" shadow="never">
    <h3>我的订单</h3>
    <hr style="margin-top: 40px; opacity: 0.4" />
    
    <!-- 筛选订单 -->
    <el-select v-model="orderStatus" placeholder="筛选订单" @change="handleStatusChange" style="width: 200px; margin-bottom: 20px;">
      <el-option label="全部" value="all"></el-option>
      <el-option label="待支付" value="0"></el-option>
      <el-option label="已完成" value="1"></el-option>
      <el-option label="已取消" value="-1"></el-option>
    </el-select>
    
    <el-table :data="filteredOrders.slice((currentPage-1) * pageSize, currentPage * pageSize)" style="width: 100%">
      <el-table-column label="商品图片" :width="  100">
        <template #default="{ row }">
          <img :src="row.productImage" alt="商品图片" style="width: 80px; height: 80px; object-fit: cover; display: block;" />
        </template>
      </el-table-column>
      <el-table-column label="订单号" prop="orderNumber" />
      <el-table-column label="商品名" prop="productName" />
      <el-table-column label="商家名" prop="merchantName" />
      <el-table-column label="订单状态" :formatter="(row) => getOrderStatus(row.status)" />
      <el-table-column label="操作">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" @click="handlePay(row)" size="mini" type="primary">去支付</el-button>
          <el-button v-if="row.status === 1" @click="handleDownload(row)" size="mini" type="success">下载软件</el-button>
          <el-button v-if="row.status === -1" @click="handleViewProduct(row)" size="mini" type="warning">查看商品</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页 -->
    <div style="text-align: center; margin-top: 20px;">
      <el-pagination
        :current-page="currentPage"
        :page-size="pageSize"
        :total="filteredOrders.length"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      ></el-pagination>
    </div>
  </el-card>
</template>

<style scoped>
.no-data {
  text-align: center;
  color: #999;
  font-size: 18px;
  margin-top: 50px;
}
</style>
