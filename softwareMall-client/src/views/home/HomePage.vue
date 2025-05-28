<script setup>
import { ref, onMounted } from 'vue'
import { productGetAllService } from '@/api/product'
import { categoryGetAllService} from '@/api/category'
import { useRouter } from 'vue-router';

import home1 from '@/assets/home1.png'
import home2 from '@/assets/home2.png'

const router = useRouter()
const detail = (id) => {
  router.push(`/productDetail/${id}`);
};
const products = ref([])
const categories = ref([]);
const getCategoryName = (id) => {
  const category = categories.value.find(item => item.id === id)
  return category ? category.name : "未知分类"
}
onMounted( async() => {
  const category_res = await categoryGetAllService();
  categories.value = category_res.data.data;
  const res = await productGetAllService();
  products.value = res.data.data;
  products.value = products.value.filter(item => item.status === 1).slice(0, 5);
  getMenuItems();
})
// 图片轮播数据
const imageList = ref([
  {id: 2, url: home1},
  {id: 3, url: home2}
])
const menuItems = ref([
  { id: 0, title: '', subItems: [] },
  { id: 1, title: '', subItems: [] },
  { id: 2, title: '', subItems: [] },
  { id: 3, title: '', subItems: [] },
  { id: 4, title: '', subItems: [] },
  { id: 5, title: '', subItems: [] },
  { id: 6, title: '', subItems: [] },
  { id: 7, title: '', subItems: [] }
])
const getMenuItems = () => {
  // 遍历分类数组，生成菜单项
  categories.value.forEach((category, index) => {
    // 防止超过菜单的长度
    if (index >= 8) return;

    // 从 products 中筛选出属于当前分类的商品
    const relatedProducts = products.value.filter(
      (product) => product.categoryId === category.id
    );

    // 构造菜单项
    menuItems.value[index] = {
      id: category.id,
      title: category.name, // 分类名称
      subItems: relatedProducts.slice(0, 3), // 存储完整的商品对象
    };
  });
};
// // 侧边栏菜单数据
// const menuItems = ref([
//   { id: 0, title: '开发工具', subItems: ['代码编辑器', 'IDE集成开发环境', '版本控制工具'] },
//   { id: 1, title: '设计工具', subItems: ['平面设计', 'UI/UX设计', '3D建模'] },
//   { id: 2, title: '办公与协作', subItems: ['文档编辑', '项目管理', '在线会议'] },
//   { id: 3, title: '教育与学习', subItems: ['语言学习', '编程课程', '在线教育平台'] },
//   { id: 4, title: '安全与优化', subItems: ['杀毒软件', '系统清理工具', '密码管理器'] },
//   { id: 5, title: '操作系统', subItems: ['Windows', 'Linux', 'macOS'] },
//   { id: 6, title: '企业软件', subItems: ['ERP系统', 'CRM系统', '数据分析工具'] },
//   { id: 7, title: '游戏与娱乐', subItems: ['PC游戏', '模拟器', '多媒体工具'] }
// ]);
</script>


<template>
  <el-card class="box-card" shadow="never">
    <el-container>
      <!-- 左侧分类菜单 -->
      <el-aside width="200px" class="menu-bar">
        <el-menu default-active="0" class="el-menu-vertical-demo">
          <el-sub-menu
            v-for="item in menuItems"
            :key="item.id"
            :index="item.id.toString()"
            class="sub_menu"
          >
            <template #title>{{ item.title }}</template>
            <!-- 展开子菜单项 -->
            <el-menu-item
              v-for="(subItem, subIndex) in item.subItems"
              :key="subIndex"
              :index="`${item.id}-${subIndex}`"
              @click="detail(subItem.id)"
            >
            <div class="menu-item-content">
              <img :src="subItem.image" alt="Item Image" class="menu-item-image" />
              <span>{{ subItem.name }}</span>
            </div>
            </el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>
      <!-- 主内容区域 -->
      <el-main class="main-content">
        <!-- 图片轮播 -->
        <el-carousel height="510px" indicator-position="outside">
          <el-carousel-item v-for="item in imageList" :key="item.id">
            <div class="carousel-image-container">
              <img :src="item.url" alt="" class="carousel-image" />
            </div>
          </el-carousel-item>
        </el-carousel>
      </el-main>
    </el-container>
    <h3>推荐商品</h3>
    <div class="product-list">
      <ProductCard
        v-for="(item, index) in products"
        :key="item.id"
        :product="item"
        :categoryName="getCategoryName(item.categoryId)"
      />
    </div>
  </el-card>
</template>


<style scoped>
.product-list {
  display: flex;
  flex-wrap: wrap;  /* 允许换行 */
  gap: 14px;  /* 设置卡片之间的间距 */
  justify-content: left;  /* 每一行的卡片居中 */
}
.sub_menu {
  background-color: #f5f5f5;
}
.box-card {
  padding-left: 40px;
  padding-right: 40px;
  margin: 0 80px;
  border: none; /* 取消边框 */
}
.menu-bar {
  background-color: #f5f5f5;
  height: 510px;
}

.main-content {
  padding: 0;
  background-color: #ffffff;
}

/* 图片轮播的图片容器，保证图片适应容器 */
.carousel-image-container {
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.product-item {
  padding: 10px;
  background-color: #eeeeee;
  text-align: center;
}
.menu-item-content {
  display: flex;
  align-items: center;  /* 垂直居中对齐图片和文本 */
  gap: 10px;  /* 图片和文本之间的间距 */
}

.menu-item-image {
  width: 40px;   /* 设置图片宽度 */
  height: 40px;  /* 设置图片高度 *//* 保持图片的比例，避免拉伸 */
}
</style>