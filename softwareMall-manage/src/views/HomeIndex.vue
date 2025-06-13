<script setup>  
import { ref } from 'vue';  
import { useRouter } from 'vue-router';  
import { useAdminStore } from '@/stores';  
// 导入 Element Plus 中的图标
import { ShoppingCart, Document, User, DataLine, Box, Edit, Folder } from '@element-plus/icons-vue';

const router = useRouter();  
const activeIndex = ref('audit');  
const showLogoutMenu = ref(false);  

const navigateTo = (path) => {  
  router.push(path);  
};  

const handleSelect = (index) => {  
  activeIndex.value = index;  
  const routes = {   
    product: '/home/product',  
    merchant: '/home/merchant',  
    user: '/home/user',  
    order: '/home/order',  
    category: '/home/category',  
    count:'/home/count'  
  };  
  router.push(routes[index]);  
};  

const handleLogout = () => {  
  // 实现退出登录的逻辑 
  useAdminStore().removeToken(); // 删除token
  router.push('/'); 
  console.log('退出登录');  
};  
</script>  


<template>
  <div class="home-container">
    <header class="header">
      <div class="logo">
        <img src="../assets/img/logo.png" alt="Logo"/>
      </div>
      <div class="title">软件管理平台</div>
      <div class="profile">
        <div class="admin-avatar" @click="showLogoutMenu = !showLogoutMenu">
          管理员
        </div>
        <div v-if="showLogoutMenu" class="logout-menu">
          <button @click="handleLogout">退出登录</button>
        </div>
      </div>
    </header>
    <el-row class="row">
      <el-col :span="5" class="menu-column">
        <el-menu
          :default-active="activeIndex"
          class="side-menu"
          @select="handleSelect"
          background-color="#f9f9f9"
          text-color="#545c64"
          active-text-color="#409EFF"
        >
        <el-menu-item index="count" @click="navigateTo('/home/count')">
            <el-icon><DataLine /></el-icon>数据统计
          </el-menu-item>
          <el-menu-item index="product" @click="navigateTo('/home/product')">
            <el-icon><Box /></el-icon>软件产品管理
          </el-menu-item>
          <el-menu-item index="category" @click="navigateTo('/home/category')">
            <el-icon><Folder /></el-icon>软件分类管理
          </el-menu-item>
          <el-menu-item index="user" @click="navigateTo('/home/user')">
            <el-icon><User /></el-icon>用户管理
          </el-menu-item>
          <el-menu-item index="merchant" @click="navigateTo('/home/merchant')">
            <el-icon><ShoppingCart /></el-icon>商家管理
          </el-menu-item>
          <el-menu-item index="order" @click="navigateTo('/home/order')">
            <el-icon><Document /></el-icon>查看订单
          </el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="19" class="content-column">
        <div class="content">
          <router-view></router-view>
        </div>
      </el-col>
    </el-row>
  </div>
</template>


<style scoped>  
.home-container {  
  height: 100vh;  
}  

.header {  
  display: flex;  
  align-items: center;  
  justify-content: space-between;  
  background-color: #409eff;  
  color: #fff;  
  padding: 0 20px;  
  height: 60px;  
}  

.logo {  
  height: 40px;  
}  

.logo img {  
  height: 100%;  
}  

.title {  
  font-size: 20px;  
  font-weight: bold;  
}  

.profile {  
  position: relative;  
}  

.admin-avatar {  
  background-color: #fff;  
  color: #409eff;  
  padding: 8px 16px;  
  border-radius: 20px;  
  cursor: pointer;  
}  

.logout-menu {  
  position: absolute;  
  top: 50px;  
  right: 0;  
  background-color: #fff;  
  border: 1px solid #eaeaea;  
  padding: 10px 20px;  
  z-index: 1;  
}  

.logout-menu button {  
  border: none;  
  background-color: transparent;  
  cursor: pointer;  
}  

.row {  
  height: calc(100% - 60px);  
}  

.menu-column {  
  border-right: 1px solid #eaeaea;  
  height: 100%;  
}  

.side-menu {  
  height: 100%;  
}  

.content-column {  
  padding: 20px;  
  height: 100%;  
  overflow-y: auto;  
  background-color: #ffffff;  
}  

.content {  
  min-height: 100%;  
}  
</style>