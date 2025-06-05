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
// const userStore = useUserStore();
// if (userStore.token != '') {
//   navItems.value.push("个人中心")
// }
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
      <!-- Logo -->
      <img src="../../assets/img/logo.png" alt="" width="90px" style="padding-left: 120px; padding-top: 35px; cursor: pointer;" @click="goToHome">
      <h2 style="padding-top: 20px; padding-right: 20px;">软件商城</h2>
      <!-- <h1 class="title" @click="goToHome">SoftWare</h1> -->
      <!-- 导航文字居中 -->
      <div class="nav-items">
        <span v-for="(item, index) in navItems" :key="index" @click="handleNavClick(item)">{{ item.name }}</span>
        <span @click="dialogTableVisible = true;">关于我们</span>
      </div>
      <div  class="dropdown-container">
        <el-dropdown class="welcome" >
          <span> {{ welcome }}</span>
        </el-dropdown>
        <el-dropdown class="welcome">
          <span> {{ username }}</span>
        </el-dropdown>
        <el-dropdown class="user-dropdown">
          <span>
            <el-avatar :src="userStore.user.avatar" :size="55">
            </el-avatar>
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
      <!-- 搜索框 -->
      <el-input
        v-model="searchInfo"
        placeholder="搜索商品"
        :prefix-icon="Search"
        @keydown.enter="search()"
        class="search-box"
        size="small"
      ></el-input>
    </el-header>
    <!-- 在顶部导航栏下方添加文字 -->
        <div class="software-center">
          <span>苏科大软件工程中心</span>
        </div>
    <div>
      <router-view></router-view>
    </div>
  </div>
</template>

<style scoped>
.top-nav {
  align-items: center;
  padding: 0 20px;
  background-color: #ffffff;
  margin-bottom: 10px;
}

.title {
  padding-top: 15px;
  margin-left: 120px;
  cursor: pointer;
}

.nav-items {
  padding-left: 100px;
  padding-top: 30px;
  display: flex;
  gap: 15px;
  font-size: 18px;
  justify-content: center;
  cursor: pointer;
}
.welcome span {
  padding-top: 30px;
  cursor: default;
  display: inline-flex;
  align-items: center;
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
  margin-right: 10px; 
}
.search-box {
  margin-top: 30px;
  margin-right: 120px;
  width: 250px;
  height: 45px;
}
/* 去除个人中心按钮的边框和轮廓 */
.user-dropdown span {
  margin-top: 10px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  outline: none !important;
  border: none !important;
  box-shadow: none !important;
  margin-right: 10px;
}
.el-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.dropdown-container {
  display: flex; /* 将三个下拉框包裹在一个 flex 容器内 */
  align-items: center;
  gap: 5px; /* 控制间距 */
  margin-left: 0;
}

.avatar-icon {
  margin-right: 5px; /* 调整头像和文字间距 */
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
  text-decoration: none; /* 取消下划线 */
}
.software-center {
  text-align: left;
  font-size: 14px;
  padding-left: 230px;
}
</style>
