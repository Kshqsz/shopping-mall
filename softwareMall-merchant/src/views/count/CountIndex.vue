<template>
  <div class="stats-container">
    <h1>数据统计</h1>
    <div class="stats-grid">
      <div class="stats-card">
        <h2>商品数量统计</h2>
        <p>{{ productStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>粉丝数量统计</h2>
        <p>{{ merchantStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>订单数据统计</h2>
        <p>{{ orderStats.count }}</p>
      </div>
      <div class="stats-card">
        <h2>销售额统计</h2>
        <p>{{ salesStats.amount }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref ,onMounted} from 'vue';
import { jwtDecode } from 'jwt-decode'   
import {useMerchantStore} from '@/stores/merchant'

const merchantID = ref()

//获取当前用户的id
const  decodeToken = () =>{  
  try {  
    console.log(useMerchantStore().token)
    const token = jwtDecode(useMerchantStore().token)
    merchantID.value = token.claims.merchantId
    console.log(token)
    console.log(merchantID.value)  
  } catch (error) {  
    console.error('Error decoding token:', error)  
 
  }  
}  
// 商品统计数据
const productStats = ref({
  count: 0
});

// 商家统计数据
const merchantStats = ref({
  count: 0
});

// 用户统计数据
const orderStats = ref({
  count: 0
});

// 销售额统计数据
const salesStats = ref({
  amount: 0
});

//获取上架商品数
const getAllProductList = async () => {  
 
    
    
};

//获取所有订单
const getOrderList = async () =>{
 
}

onMounted(()=>{
  
  decodeToken()
  getAllProductList()
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