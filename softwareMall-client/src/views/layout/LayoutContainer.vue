<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router';
import { ArrowDown, Search } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores';
import { ElMessage } from 'element-plus';

const searchInfo = ref('')
// 顶部导航文字
const dialogTableVisible = ref(false)
const navItems = ref([
  { name: "首页", path: "/home", isSearch: false },
  { name: "管理系统", path: "", isSearch: true },
  { name: "开发工具", path: "", isSearch: true },
  { name: "操作系统", path: "", isSearch: true },
  { name: "设计工具", path: "", isSearch: true },
  { name: "办公与协作", path: "", isSearch: true },
]);
const router = useRouter()
const userStore = useUserStore()
const username = userStore.username
const user = ref(userStore.user)
const search = (keyword = '') => {
  searchInfo.value = keyword || searchInfo.value;
  router.push({ path: '/searchResult', query: { query: searchInfo.value, t: Date.now() } });
};

const handleNavClick = (item) => {
  if (item.isSearch) {
    // 如果是搜索项，调用搜索方法
    search(item.name);
    searchInfo.value = ''
  } else if (item.path) {
    // 如果是普通导航项，跳转到指定路径
    router.push(item.path);
  }
};
const welcome = ref("欢迎你~")
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
  searchInfo.value = ''
  router.push("/home")
}
const gridData = [
  { name: '韩守坤', from: '苏州科技大学', email: '2456480538@qq.com'},
  { name: '郁竹一', from: '苏州科技大学', email: '2949621931@qq.com'},
  { name: '吴纡怀', from: '苏州科技大学', email: '1085406285@qq.com'},
  { name: '陈松',   from: '苏州科技大学', email: '2220834872@qq.com'},
]
</script>

<template>
  <el-dialog v-model="dialogTableVisible" title="About us" width="800">
    <el-table :data="gridData">
      <el-table-column property="name" label="Name" width="200" />
      <el-table-column property="from" label="From" />
      <el-table-column property="email" label="Contact us"></el-table-column>
    </el-table>
    <!-- 指导老师信息 -->
    <div class="mentor-info">
      <span>
        指导老师:
        <a 
          class="director" 
          href="https://eie.usts.edu.cn/info/1120/2941.htm" 
          target="_blank" 
        >
          奚雪峰教授
        </a>
        （电子与信息工程学院）
      </span>
    </div>
  </el-dialog>
  <div class="page-layout">
    <!-- 顶部导航栏 -->
    <el-header height="60px" class="top-nav">
      <div class="nav-left">
        <img src="../../assets/img/logo.png" alt="" width="60px" style="cursor: pointer;" @click="goToHome">
        <h2 class="mall-title">软件商城</h2>
      </div>
      <div class="nav-items">
        <span v-for="(item, index) in navItems" :key="index" @click="handleNavClick(item)">{{ item.name }}</span>
        <span @click="dialogTableVisible = true;">关于我们</span>
      </div>
      <div class="dropdown-container">
        <el-dropdown class="welcome">
          <span> {{ welcome }}</span>
        </el-dropdown>
        <el-dropdown class="welcome">
          <span> {{ username }}</span>
        </el-dropdown>
        <el-dropdown class="user-dropdown">
          <span>
            <el-avatar :src="userStore.user.avatar" :size="55"></el-avatar>
            <el-icon>
              <arrow-down />
            </el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="goToUserCenter">个人中心</el-dropdown-item>
              <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <el-input
        v-model="searchInfo"
        placeholder="搜索商品"
        :prefix-icon="Search"
        @keydown.enter="search()"
        class="search-box"
        size="small"
      ></el-input>
    </el-header>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.top-nav {
  display: flex;
  align-items: center;
  width: 100%;
  background-color: #fff;
  padding: 0 20px;
  min-width: 1200px;
  box-sizing: border-box;
}

.nav-left {
  padding-left: 120px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.mall-title {
  font-size: 22px;
  font-weight: bold;
}

.nav-items {
  display: flex;
  align-items: center;
  gap: 18px;
  font-size: 16px;
  cursor: pointer;
  flex: 1;
  justify-content: center;
  min-width: 400px;
}

.dropdown-container {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: 0;
}

.welcome span {
  cursor: default;
  display: inline-flex;
  align-items: center;
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
  margin-right: 10px; 
}

.user-dropdown span {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
  margin-right: 10px;
}

.search-box {
  margin-left: 20px;
  width: 250px;
  height: 45px;
  margin-top: 0;
  margin-right: 0;
}

.mentor-info {
  margin-right: 100px;
  margin-top: 10px;
  text-align: center;
  font-size: 16px;
  color: #333;
}
.director {
  color: #0073e6;
  cursor: pointer;
  text-decoration: none;
}
</style>