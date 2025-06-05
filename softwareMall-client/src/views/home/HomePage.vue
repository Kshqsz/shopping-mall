<script setup>
import { ref, onMounted } from 'vue'
import { productGetAllService } from '@/api/product'
import { firstCategoryList, secondCategoryList} from '@/api/category'
import { useRouter } from 'vue-router';

import home1 from '@/assets/home1.png'
import home2 from '@/assets/home2.png'

const router = useRouter()
const detail = (id) => {
  router.push(`/productDetail/${id}`);
};
const products = ref([])
const firstCategories = ref([]); // 一级分类列表
const secondCategories = ref({}); // 存储二级分类，key为一级分类ID
const menuItems = ref([]); // 分类菜单项

onMounted( async() => {
  // 获取一级分类
  const firstRes = await firstCategoryList();
  firstCategories.value = firstRes.data.data;
  
  // 获取所有二级分类
  for (const category of firstCategories.value) {
    const secondRes = await secondCategoryList(category.id);
    secondCategories.value[category.id] = secondRes.data.data;
  }
  // 构建菜单项 - 只取前8个一级分类
  menuItems.value = firstCategories.value.slice(0, 8).map(cat => ({
    ...cat,
    children: secondCategories.value[cat.id] || []
  }));
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


</script>


<template>
  <el-card class="box-card" shadow="never">
    <el-container>
      <!-- 左侧分类菜单 -->
      <el-aside width="200px" class="menu-bar">
        <el-menu default-active="0" class="el-menu-vertical-demo">
          <el-sub-menu
            v-for="firstCat in menuItems"
            :key="firstCat.id"
            :index="firstCat.id.toString()"
            class="sub_menu"
          >
            <template #title>{{ firstCat.name }}</template>
            
            <!-- 二级分类菜单项 -->
            <el-menu-item
              v-for="secondCat in firstCat.children"
              :key="secondCat.id"
              :index="`${firstCat.id}-${secondCat.id}`"
              @click="goToCategory(secondCat.id)"
            >
              <span>{{ secondCat.name }}</span>
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