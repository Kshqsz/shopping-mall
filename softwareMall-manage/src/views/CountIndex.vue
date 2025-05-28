<template>
  <div class="stats-container">
    <h1>数据统计</h1>
    <div class="stats-grid">
      <div class="stats-card">
        <h2>上架商品统计</h2>
        <p>{{ productStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>注册商家统计</h2>
        <p>{{ merchantStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>注册用户统计</h2>
        <p>{{ userStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>销售额统计</h2>
        <p>{{ salesStats.amount }}￥</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref ,onMounted} from 'vue';
import {getAllProduct} from '../api/product'
import { admingetalluser} from '@/api/user'; 
import { getAllMerchant } from '../api/merchant'
import { getOrderWithPrice } from '@/api/order';

// 上架商品数据
const productStats = ref({
  count:0
});

// 注册商家数据
const merchantStats = ref({
  count: 0
});

// 注册用户数据
const userStats = ref({
  count: 0
});

// 销售额数据
const salesStats = ref({
  amount: 0
});

//获取上架商品数
const getAllProductList = async () => {  
  try {  
    const res = await getAllProduct();  
    console.log('获取商品列表成功:', res.data.data);  

    // 确保响应式  
    const filteredProduct = res.data.data.filter(product => product.status == 1); 
     productStats.value.count = filteredProduct.length
  } catch (error) {  
    console.error('获取商品列表失败:', error);  
  }  
};

//获取所有用户
const getAllUser = async () => {  
  try {  
    const res = await admingetalluser();  
    console.log('获取用户成功:', res.data.data);  
    // 使用 userList.splice 来确保响应式  
    userStats.value.count=res.data.data.length
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  


//获取所有商家列表实现
    
const getMerchant = async () => {  
  try {  
    const res = await getAllMerchant();  
    console.log('获取用户成功:', res.data.data);  
    merchantStats.value.count = res.data.data.length
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
};  

const getOrderList = async () =>{
  try {  
    const res = await getOrderWithPrice();  
    console.log('获取订单成功:', res.data.data);  
    const filterOrder = res.data.data.filter(product => product.status == 1); 
    const prices = filterOrder.map(product => product.price); // 提取每个商品的价格
    const total = parseFloat(prices.reduce((sum, price) => sum + price, 0).toFixed(2)); // 计算价格的总和并保留两位小数  
    salesStats.value.amount = total; 
   
  } catch (error) {  
    console.error('获取用户失败:', error);  
  }  
}
onMounted(() => {  
 getAllProductList()
 getAllUser()
 getMerchant()
 getOrderList()
  
});
</script>

<style>
.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 20px;
}

.stats-card {
  background-color: #f7f7f7;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 5px rgba(0,0,0,0.2);
}

.stats-card h2 {
  margin: 0 0 10px 0;
  color: #333;
}

.stats-card p {
  margin: 0;
  font-size: 1.5em;
  color: #666;
}
</style>