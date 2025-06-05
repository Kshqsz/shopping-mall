<script setup>
import { watch } from 'vue';
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router';
import {
  Document,
  User,
  Star,
  Location,
  List,
  Setting
} from '@element-plus/icons-vue';

// 左侧导航项 - 添加图标
const menuItems = ref([
  {
    category: '订单中心',
    icon: List,
    items: [
      { label: '我的订单', route: '/orders', icon: Document },
    ]
  },
  {
    category: '个人中心',
    icon: User,
    items: [
      { label: '我的个人中心', route: '/profile', icon: Setting },
      { label: '喜欢的商品', route: '/favorites', icon: Star },
      { label: '收货地址管理', route: '/address', icon: Location }  
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
          <div class="category-title">
            <el-icon class="category-icon"><component :is="menu.icon" /></el-icon>
            <h3>{{ menu.category }}</h3>
          </div>
          <ul>
            <li 
              v-for="(item, idx) in menu.items" 
              :key="idx"
              :class="{ active: item.route === currentRoute }"
              @click="navigateTo(item.route)"
            >
              <el-icon class="item-icon"><component :is="item.icon" /></el-icon>
              <span class="item-label">{{ item.label }}</span>
            </li>
          </ul>
        </div>
      </el-card>
      
      <!-- 右侧内容 -->
      <el-card class="content" shadow="never">
        <router-view />
      </el-card>
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
  width: 280px;
  background: #fff;
  border-radius: 12px;
  padding: 16px 0;
}

.menu-section {
  margin-bottom: 24px;
}

.category-title {
  display: flex;
  align-items: center;
  padding: 0 20px 12px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
}

.category-icon {
  font-size: 18px;
  color: var(--el-color-primary);
  margin-right: 8px;
}

.menu-section h3 {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.menu-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-section li {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  color: #606266;
  font-size: 14px;
  border-left: 3px solid transparent;
  margin: 4px 0;
}

.menu-section li:hover {
  background-color: #f5f7fa;
  color: var(--el-color-primary);
}

.menu-section li.active {
  background-color: #f0f7ff;
  color: var(--el-color-primary);
  border-left-color: var(--el-color-primary);
  font-weight: 500;
}

.menu-section li.active .item-icon {
  color: var(--el-color-primary);
}

.item-icon {
  font-size: 16px;
  margin-right: 10px;
  color: #909399;
}

.item-label {
  flex: 1;
}

.content {
  flex: 1;
  border-radius: 12px;
  min-height: 70vh;
  padding: 24px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .user-center {
    padding: 20px;
  }
  
  .user-center-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
    margin-bottom: 20px;
  }
}
</style>