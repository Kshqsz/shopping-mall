<script setup>  
import { ref } from 'vue';  
import { useRouter } from 'vue-router';  
import { useMerchantStore } from '@/stores';
import { Document, User, Box } from '@element-plus/icons-vue';

// 店铺数据
const merchantStore = useMerchantStore()
const userInfo = ref(merchantStore.merchant);

const router = useRouter();  
const activeIndex = ref('product');  
const showLogoutMenu = ref(false);  

const navigateTo = (path) => {  
  router.push(path);  
};  

const handleSelect = (index) => {  
  activeIndex.value = index;  
  const routes = {  
    product: '/home/product',  
    order: '/home/order',  
    merchant: '/home/merchant',
    count:'/home/count'  
  };  
  router.push(routes[index]);  
};  

const handleLogout = () => {  
  // 实现退出登录的逻辑  
  router.push("/")
};  
</script>  

<template>  
  <div class="home-container">  
    <header class="header">  
      <div class="logo">  
        <img src="../../assets/img/logo.png" alt="Logo" />  
      </div>  
      <div class="title">店铺管理平台</div>  
      <div class="profile">  
        <el-dropdown trigger="click" @visible-change="showLogoutMenu = $event">
          <div class="admin-info">
            <el-avatar 
              :size="40" 
              :src="userInfo.avatar" 
              class="header-avatar"
            />
            <span class="username">{{ userInfo.username }}</span>
            <el-icon class="arrow-icon">
              <arrow-down />
            </el-icon>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="navigateTo('/home/merchant')">
                <el-icon><User /></el-icon>店铺信息
              </el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>  
    </header>  
    <el-row class="row">  
      <el-col :span="3" class="menu-column">  
        <el-menu  
          :default-active="activeIndex"  
          class="side-menu"  
          @select="handleSelect"  
          background-color="#f9f9f9"  
          text-color="#545c64"  
          active-text-color="#409EFF"  
        >  
          <el-menu-item index="product" @click="navigateTo('/home/product')">  
            <el-icon><Box/></el-icon>
            软件产品管理  
          </el-menu-item>  
          <el-menu-item index="order" @click="navigateTo('/home/order')">  
            <el-icon><Document/></el-icon>
            查看订单  
          </el-menu-item>  
          <el-menu-item index="merchant" @click="navigateTo('/home/merchant')">  
            <el-icon><User/></el-icon>
            店铺信息  
          </el-menu-item>  
        </el-menu>  
      </el-col>  
      <el-col :span="21" class="content-column">  
        <div class="content">  
          <router-view></router-view>  
        </div>  
      </el-col>  
    </el-row>  
  </div>  
</template>  

<style scoped lang="scss">
.home-container {  
  height: 98vh;  
}  

.header {  
  display: flex;  
  align-items: center;  
  justify-content: space-between;  
  background-color: #409eff;  
  color: #fff;  
  padding: 0 20px;  
  height: 60px;  
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
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

.admin-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s;
  
  &:hover {
    background-color: rgba(255, 255, 255, 0.2);
  }
}

.header-avatar {
  margin-right: 10px;
  border: 2px solid rgba(255, 255, 255, 0.5);
  transition: all 0.3s;
  
  &:hover {
    transform: scale(1.05);
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  }
}

.username {
  margin-right: 8px;
  font-size: 14px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  transition: transform 0.3s;
  
  .el-dropdown.is-active & {
    transform: rotate(180deg);
  }
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

/* 响应式调整 */
@media (max-width: 768px) {
  .username {
    display: none;
  }
  
  .header {
    padding: 0 10px;
  }
  
  .title {
    font-size: 16px;
  }
}
</style>