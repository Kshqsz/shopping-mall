<script setup>
import { ref, onMounted } from 'vue'
import { productGetAllService } from '@/api/product'
import { firstCategoryList, secondCategoryList} from '@/api/category'
import { useRouter } from 'vue-router';
import { ArrowRight } from '@element-plus/icons-vue'

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
const activeFirstCategory = ref(null); // 当前激活的一级分类
const showSubMenu = ref(false); // 是否显示二级菜单
const subMenuPosition = ref({ top: 0, left: 0 }); // 二级菜单位置

const searchQuery = {
  name:'',
  level1Category:null,
  level2Category:null
}
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
  const res = (await productGetAllService(searchQuery)).data.data;
  products.value = res;
})

// 图片轮播数据
const imageList = ref([
  {id: 2, url: home1},
  {id: 3, url: home2}
])

// 点击一级分类
const handleFirstCategoryClick = (category, event) => {
  if (activeFirstCategory.value?.id === category.id) {
    // 如果点击的是当前已激活的分类，则关闭二级菜单
    showSubMenu.value = false;
    activeFirstCategory.value = null;
  } else {
    // 否则打开二级菜单
    activeFirstCategory.value = category;
    showSubMenu.value = true;
    
    // 计算二级菜单位置
    const rect = event.currentTarget.getBoundingClientRect();
    subMenuPosition.value = {
      top: rect.top,
      left: rect.right + 5 // 稍微向右偏移一点
    };
  }
}

// 点击二级分类
const handleSecondCategoryClick = (categoryId) => {
  router.push(`/category/${categoryId}`);
  showSubMenu.value = false;
}

// 点击页面其他区域关闭二级菜单
const handleClickOutside = (event) => {
  const subMenu = document.querySelector('.sub-menu-container');
  const firstMenuItems = document.querySelectorAll('.first-menu-item');
  
  if (!subMenu.contains(event.target) && 
      !Array.from(firstMenuItems).some(item => item.contains(event.target))) {
    showSubMenu.value = false;
  }
}

// 添加全局点击事件监听
onMounted(() => {
  document.addEventListener('click', handleClickOutside);
});

// 移除事件监听
onMounted(() => {
  document.removeEventListener('click', handleClickOutside);
});
</script>

<template>
  <el-card class="box-card" shadow="never">
    <el-container>
      <!-- 左侧分类菜单 -->
      <el-aside width="200px" class="menu-bar">
        <div class="first-menu">
          <div 
            v-for="firstCat in menuItems"
            :key="firstCat.id"
            class="first-menu-item"
            :class="{ 'active': activeFirstCategory?.id === firstCat.id && showSubMenu }"
            @click.stop="handleFirstCategoryClick(firstCat, $event)"
          >
            <div class="menu-item-content">
              <span>{{ firstCat.name }}</span>
              <el-icon><arrow-right /></el-icon>
            </div>
          </div>
        </div>
      </el-aside>

      <!-- 二级菜单 -->
      <div 
        v-if="showSubMenu && activeFirstCategory"
        class="sub-menu-container"
        :style="{
          top: `${subMenuPosition.top}px`,
          left: `${subMenuPosition.left}px`
        }"
      >
        <div 
          v-for="secondCat in activeFirstCategory.children"
          :key="secondCat.id"
          class="second-menu-item"
          @click.stop="handleSecondCategoryClick(secondCat.id)"
        >
          {{ secondCat.name }}
        </div>
      </div>

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

    <!-- 推荐商品 -->
    <div class="recommend-title">
      <h3>推荐商品</h3>
      <el-divider />
    </div>
    <div class="product-list">
      <ProductCard
        v-for="(item) in products"
        :key="item.id"
        :product="item"
      />
    </div>
  </el-card>
</template>

<style scoped>
.box-card {
  padding: 20px 40px;
  margin: 0 80px;
  border: none;
}

.menu-bar {
  background-color: #f5f5f5;
  height: 510px;
  position: relative;
}

.first-menu {
  padding: 10px 0;
}

.first-menu-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.first-menu-item:hover {
  background-color: #e8f4ff;
  color: #409eff;
}

.first-menu-item.active {
  background-color: #e8f4ff;
  color: #409eff;
}

.menu-item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 二级菜单样式 */
.sub-menu-container {
  position: fixed;
  width: 200px;
  background-color: white;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  z-index: 2000;
  border-radius: 4px;
  padding: 10px 0;
  max-height: 400px;
  overflow-y: auto;
}

.second-menu-item {
  padding: 12px 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.second-menu-item:hover {
  background-color: #f5f7fa;
  color: #409eff;
}

.main-content {
  padding: 0;
  background-color: #ffffff;
}

.carousel-image-container {
  width: 100%;
  height: 100%;
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recommend-title {
  margin-top: 30px;
}

.recommend-title h3 {
  margin-bottom: 10px;
  font-size: 20px;
  color: #333;
}

.product-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: flex-start;
  margin-top: 20px;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .box-card {
    margin: 0 40px;
    padding: 20px;
  }
}

@media (max-width: 992px) {
  .box-card {
    margin: 0 20px;
  }
  
  .menu-bar {
    width: 160px;
  }
}

@media (max-width: 768px) {
  .box-card {
    margin: 0 10px;
    padding: 10px;
  }
  
  .menu-bar {
    display: none;
  }
  
  .sub-menu-container {
    display: none;
  }
}
</style>