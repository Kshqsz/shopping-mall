<script setup>
import { watch } from 'vue';
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router';

// 左侧导航项
const menuItems = ref([
  {
    category: '订单中心',
    items: [
      { label: '我的订单', route: '/orders' },
    ]
  },
  {
    category: '个人中心',
    items: [
      { label: '我的个人中心', route: '/profile' },
      { label: '喜欢的商品', route: '/favorites' },
    ]
  }
])
const route = useRoute()
const router = useRouter()
const currentRoute = ref(route.path)

watch(() => route.path, (newPath) => {
  currentRoute.value = newPath;
});

// 跳转到指定路由
const navigateTo = (route) => {
  router.push(route)
  currentRoute.value = route
}
</script>

<template>
  <div class="user-center">
    <!-- 页面布局 -->
    <div class="user-center-layout">
      <!-- 左侧导航 -->
      <el-card class="sidebar" shadow="never">
        <div v-for="(menu, index) in menuItems" :key="index" class="menu-section">
          <h3>{{ menu.category }}</h3>
          <ul>
            <li 
              v-for="(item, idx) in menu.items" 
              :key="idx"
              :class="{ active: item.route === currentRoute }"
              @click="navigateTo(item.route)"
            >
              {{ item.label }}
            </li>
          </ul>
        </div>
      </el-card>
      <!-- 右侧内容 -->
        <router-view />
    </div>
  </div>
</template>

<style scoped>
.user-center {
  padding: 30px 136px;
  background-color: #f5f5f5;
  box-sizing: border-box;
  min-height: 90vh;
}

.user-center-layout {
  display: flex;
  gap: 20px;
}

.sidebar {
  width: 250px;
  background: #fff;
}

.menu-section {
  margin-bottom: 20px;
}

.menu-section h3 {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 10px;
}

.menu-section ul {
  list-style: none;
  padding: 0;
}

.menu-section li {
  padding: 5px 0;
  cursor: pointer;
  transition: all 0.3s;
}

.menu-section li:hover {
  color: #409eff;
}

.content {
  flex: 1;
  padding: 20px;
  
}
.menu-section li {
  padding: 5px 0;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
}

.menu-section li.active {
  color: #409eff;
}

.menu-section li:hover {
  color: #409eff;
}
</style>
