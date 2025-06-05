<template>  
  <div class="order-management-container">  
    <h2>订单管理</h2>  
    <!-- 订单过滤器 -->  
    <div class="filters">  
      <div class="filter-item">  
        <span>订单编号:</span>  
        <el-input v-model="orderNo" placeholder="输入订单编号" />  
        <el-button type="primary" @click="searchOrders">查询</el-button>  
      </div>  
    </div>  

    <!-- 订单列表表格 -->  
    <el-table :data="paginatedOrders" style="width: 100%">  
      <el-table-column prop="id" label="订单ID" width="120"></el-table-column>  
      <el-table-column prop="orderNumber" label="订单编号" width="180"></el-table-column>  
      <el-table-column prop="productName" label="交易商品" width="180"></el-table-column>  
      <!-- <el-table-column prop="productLink" label="商品链接" width="180">  
        <template #default="scope">  
          <a :href="scope.row.productLink" target="_blank">{{ scope.row.productLink }}</a>  
        </template>  
      </el-table-column>   -->
      <el-table-column prop="createTime" label="交易时间" width="180"></el-table-column>  
      <el-table-column prop="userName" label="买家" width="120"></el-table-column>  
      <el-table-column prop="status" label="订单状态" width="120">  
        <template #default="scope">  
          <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>  
        </template>  
      </el-table-column> 
    </el-table>  

    <!-- 分页控件 -->  
    <div class="pagination">  
      <el-pagination  
        v-model:current-page="currentPage"  
        v-model:page-size="pageSize"  
        :total="filteredOrders.length"  
        layout="prev, pager, next"  
        @current-change="handlePageChange"  
      />  
    </div>  
  </div>  
</template>  

<script setup>  
import { ref, computed ,onMounted} from 'vue';  
import { getAllOrder } from '@/api/order';
import { admingetalluser } from '@/api/user';

const orderList = ref([]);  
const merchantMap = ref([])
const productMap = ref([])
const productMap2 = ref([])
const userMap = ref([])
const getOrderList = async () =>{
  try {  
    const res = await getAllOrder();  
    console.log('获取订单成功:', res.data.data);  
    orderList.value.splice(0, orderList.value.length, ...res.data.data);
    const ordersWithNames = orderList.value.map(order => {  
      return {  
        ...order,    
        merchantName: merchantMap[order.merchantId] || '未知商家' ,
        productName: productMap[order.productId] || '未知商品',
        productLink: productMap2[order.productId],
        userName:userMap[order.userId]
      };  
    });
    orderList.value.splice(0, orderList.value.length, ...ordersWithNames);
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
}

//获取产品列表
const getAllProductList = async () => {  
   
};
//获取用户列表
const getAllUser = async () => {  
  try {  
    const res = await admingetalluser();  
    console.log('获取用户成功:', res.data.data);   
    userMap.value.splice(0, userMap.value.length, ...res.data.data);  
    res.data.data.forEach(user => {  
      userMap[user.id] = user.username;   
    });     
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
}; 


 
  

// 当前页码和每页数量  
const currentPage = ref(1);  
const pageSize = ref(10);  

// 订单编号过滤器  
const orderNo = ref('');  

// 根据订单编号过滤订单列表  
const filteredOrders = computed(() => {  
  let orders = [...orderList.value];  
  if (orderNo.value) {  
    orders = orders.filter((order) =>  
      order.orderNumber.toLowerCase().includes(orderNo.value.toLowerCase())  
    );  
  }  
  return orders;  
});  

// 分页后的订单列表  
const paginatedOrders = computed(() => {  
  const start = (currentPage.value - 1) * pageSize.value;  
  const end = start + pageSize.value;  
  return filteredOrders.value.slice(start, end);  
});  

//处理状态显示
const getStatusTagType = (status) => {  
  switch (status) {  
    case "1":  
      return 'success';  
    case 0:  
      return 'warning';  
    case -1:  
      return 'danger';  
    default:  
      return '';  
  }  
};  
const getStatusText = (status) => {  
    switch (status) {  
      case 1:  
        return '已完成';  
      case 0:  
        return '未支付';  
      case -1:  
        return '已取消';  
      default:  
        return '未知状态'; // 可选的默认状态  
    }  
  }  

// 点击查询按钮时的处理函数  
const searchOrders = () => {  
  currentPage.value = 1;  
};  

// 分页切换时的处理函数  
const handlePageChange = () => {  
  // 处理分页切换  
};  
onMounted(() => {  
  getAllProductList()  
    .then(getAllUser)  
    .then(getOrderList)
});  
</script>  

<style scoped>  
.order-management-container {  
  padding: 20px;  
}  
.filters {  
  display: flex;  
  justify-content: space-between;  
  align-items: center;  
  margin-bottom: 20px;  
}  
.filter-item {  
  display: flex;  
  align-items: center;  
}  
.filter-item span {  
  margin-right: 10px;  
  width: 120px;  
}  
.filter-item .el-input,  
.filter-item .el-select {  
  margin-right: 10px;  
}  
.pagination {  
  display: flex;  
  justify-content: center;  
  margin-top: 20px;  
}  
</style>