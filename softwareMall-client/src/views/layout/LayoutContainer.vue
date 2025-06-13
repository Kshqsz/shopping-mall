<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import { ArrowDown, Search, ShoppingCart, SwitchButton } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores';
import { ElMessage } from 'element-plus';

const searchKeyword = ref('') // 修改为字符串类型的搜索关键词
const dialogTableVisible = ref(false)
const navItems = ref([
  { name: "首页", path: "/home", isSearch: false },
  { name: "数码产品", path: "", isSearch: true },
  { name: "智能家居", path: "", isSearch: true },
  { name: "生活服务", path: "", isSearch: true },
  { name: "食品生鲜", path: "", isSearch: true },
  { name: "服装鞋靴", path: "", isSearch: true },
]);

const router = useRouter()
const userStore = useUserStore()
const username = userStore.username
const user = ref(userStore.user)

// 搜索方法
const search = () => {
  // 不再检查空值，直接搜索
  router.push({ 
    path: '/searchResult', 
    query: { 
      q: searchKeyword.value, // 使用q作为查询参数名
      t: Date.now() // 添加时间戳防止缓存
    } 
  });
};

// 导航点击事件
const handleNavClick = (item) => {
  if (item.isSearch) {
    searchKeyword.value = item.name // 填充搜索关键词
    search() // 立即搜索
  } else if (item.path) {
    router.push(item.path);
  }
};

const logout = () => {
  userStore.removeToken();
  userStore.removeUsername();
  router.push("/");
  ElMessage.success("退出登录成功~")
}

const goToUserCenter = () => {
  router.push("/userCenter")
} 

const goToHome = () => {
  router.push("/home")
}

const gridData = [
  { name: '韩守坤', from: '苏州科技大学', email: '2456480538@qq.com'},
  { name: '韦辉', from: '苏州科技大学', email: '3244432308@qq.com'},
  { name: '吴纡怀', from: '苏州科技大学', email: '1085406285@qq.com'},
  { name: '陈松', from: '苏州科技大学', email: '2220834872@qq.com'},
]

// 在原有代码中添加购物车相关逻辑
const cartItemCount = ref(0) // 购物车商品数量
// 获取购物车商品数量（假设有对应的API）
const getCartItemCount = async () => {
  try {
    // 这里假设有一个获取购物车商品数量的API
    // const res = await getCartCount()
    // cartItemCount.value = res.data.count
    cartItemCount.value = 3 // 模拟数据
  } catch (error) {
    console.error('获取购物车数量失败', error)
  }
}
// 跳转到购物车页面
const goToCart = () => {
  router.push('/cart')
}
const goToLogin = () => {
  router.push('/login')
}

getCartItemCount()
</script>

<template>
  <el-dialog v-model="dialogTableVisible" title="关于我们" width="800">
    <el-table :data="gridData" border>
      <el-table-column property="name" label="姓名" width="200" align="center" />
      <el-table-column property="from" label="学校" align="center" />
      <el-table-column property="email" label="联系方式" align="center"></el-table-column>
    </el-table>
  </el-dialog>

  <div class="page-layout">
    <!-- 顶部导航栏 -->
    <div class="top-nav">
      <div class="nav-left">
        <div class="logo-container" @click="goToHome">
          <img src="../../assets/img/logo.png" alt="软件商城" class="logo">
          <span class="site-name">购物商城</span>
        </div>
      </div>
      <!-- 导航菜单 -->
      <div class="nav-menu">
        <div 
          v-for="(item, index) in navItems" 
          :key="index" 
          class="nav-item"
          :class="{ 'active': $route.path === item.path }"
          @click="handleNavClick(item)"
        >
          {{ item.name }}
        </div>
        <div class="nav-item" @click="dialogTableVisible = true">关于我们</div>
      </div>

      <!-- 右侧功能区 -->
      <div class="nav-right">
        <!-- 购物车图标 -->
        <div class="cart-icon" @click="goToCart">
            <el-icon :size="24"><shopping-cart /></el-icon>
          <span class="cart-text">购物车</span>
        </div>
        <!-- 搜索框 - 添加按钮和回车事件 -->
        <div class="search-container">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品"
            :prefix-icon="Search"
            @keyup.enter="search"
            class="search-box"
            size="large"
          >
            <template #append>
              <el-button 
                :icon="Search" 
                @click="search"
                class="search-button"
              />
            </template>
          </el-input>
        </div>

        <!-- 用户信息 -->
        <!-- 用户信息或登录按钮 -->
        <div class="user-info">
          <el-dropdown v-if="userStore.token">
            <div class="user-dropdown">
              <el-avatar :src="userStore.user?.avatar" :size="40"></el-avatar>
              <span class="username">{{ username }}</span>
              <el-icon><arrow-down /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="goToUserCenter">
                  <el-icon><user /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="logout" divided>
                  <el-icon><switch-button /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button 
            v-else 
            type="primary" 
            plain 
            @click="goToLogin"
            class="login-btn"
          >
            去登录
          </el-button>
        </div>
      </div>
    </div>

    <!-- 页面内容 -->
    <div class="page-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.page-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  height: 80px;
  background-color: #fff;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.nav-left {
  display: flex;
  align-items: center;
  min-width: 280px;
}

.logo-container {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.logo {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}

.site-name {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.nav-menu {
  display: flex;
  justify-content: center;
  flex: 1;
}

.nav-item {
  overflow: hidden;
  margin-left: 40px;
  height: 80px;
  line-height: 80px;
  font-size: 16px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.nav-item:hover {
  color: #409eff;
  background-color: #f5f7fa;
}

.nav-item.active {
  color: #409eff;
  font-weight: bold;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20px;
  right: 20px;
  height: 3px;
  background-color: #409eff;
}

.nav-right {
  display: flex;
  align-items: center;
  min-width: 280px;
  justify-content: flex-end;
}

.search-container {
  display: flex;
  margin-right: 30px;
}

.search-box {
  width: 300px;
}

.search-button {
  height: 40px;
  background-color: #409eff;
  color: white;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

.search-button:hover {
  background-color: #79bbff;
}

.user-info {
  margin-left: 20px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 5px 10px;
  border-radius: 20px;
  transition: all 0.3s;
}

.user-dropdown:hover {
  background-color: #f5f7fa;
}

.username {
  margin: 0 8px 0 10px;
  font-size: 14px;
  color: #333;
}

.page-content {
  flex: 1;
  padding: 20px 40px;
  background-color: #f5f7fa;
}




/* 添加购物车相关样式 */
.cart-icon {
  display: flex;
  align-items: center;
  margin-right: 30px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: all 0.3s;
}

.cart-icon:hover {
  background-color: #f5f7fa;
}

.cart-text {
  margin-left: 8px;
  font-size: 14px;
  color: #333;
}

.cart-badge {
  margin-right: 10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .cart-icon {
    margin-right: 15px;
  }
  
  .cart-text {
    display: none;
  }
}
.login-btn {
  margin-left: 48px;
  margin-right:40px;
  padding: 8px 15px;
  border-radius: 4px;
  transition: all 0.3s;
}

.login-btn:hover {
  background-color: #409eff;
  color: white;
}

</style>