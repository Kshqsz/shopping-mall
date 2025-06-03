<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="`商品详情 - ${goodsData?.name || ''}`" 
    width="900px"
    top="5vh"
    class="goods-detail-dialog"
    :close-on-click-modal="false"
  >
    <div v-if="goodsData" class="detail-container">
      <div class="header-info">
        <div class="main-image">
          <el-image 
            :src="goodsData.mainImage" 
            fit="cover" 
            class="product-image"
            :preview-src-list="[goodsData.mainImage]"
          />
        </div>
        <div class="basic-info">
          <h2 class="name">{{ goodsData.name }}</h2>
          <div class="price">¥{{ formatPrice(goodsData.price) }}</div>
          
          <div class="meta-info">
            <div class="meta-item">
              <span class="label">分类：</span>
              <span class="value">{{ goodsData.categoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">库存：</span>
              <span class="value">{{ goodsData.stock }}</span>
            </div>
            <div class="meta-item">
              <span class="label">销量：</span>
              <span class="value">{{ goodsData.sales }}</span>
            </div>
            <div class="meta-item">
              <span class="label">状态：</span>
              <el-tag 
                size="medium" 
                :type="statusType[goodsData.status]"
              >
                {{ statusText[goodsData.status] }}
              </el-tag>
            </div>
            <div class="meta-item">
              <span class="label">创建时间：</span>
              <span class="value">{{ formatDate(goodsData.createTime) }}</span>
            </div>
            <div class="meta-item">
              <span class="label">更新时间：</span>
              <span class="value">{{ formatDate(goodsData.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <el-tabs v-model="activeTab" class="detail-tabs">
        <el-tab-pane label="商品描述" name="desc">
          <div class="description">
            {{ goodsData.desc || '暂无描述' }}
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="商品图片" name="images">
          <div class="image-gallery">
            <el-image 
              v-for="(img, index) in goodsData.detailImages" 
              :key="index"
              :src="img" 
              fit="cover" 
              class="detail-image"
              :preview-src-list="goodsData.detailImages"
            />
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="商品视频" name="video">
          <div v-if="goodsData.video" class="video-container">
            <video 
              :src="goodsData.video" 
              controls 
              class="video-player"
            ></video>
          </div>
          <div v-else class="no-video">
            <el-icon><VideoCameraFilled /></el-icon>
            <div>暂无视频</div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    
    <template #footer>
      <el-button @click="dialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
//import { fetchGoodsDetail } from '@/api/goods'
import { VideoCameraFilled } from '@element-plus/icons-vue'

const props = defineProps({
  goodsId: {
    type: Number,
    required: true
  }
})

const dialogVisible = defineModel()

// 状态映射
const statusText = {
  1: '审核中',
  2: '已下架',
  3: '已上架'
}
const statusType = {
  1: 'warning',
  2: 'info',
  3: 'success'
}

const activeTab = ref('desc')
const goodsData = ref(null)

watch(dialogVisible, async (visible) => {
  if (visible && props.goodsId) {
    // 获取商品详情
   /*  try {
      const res = await fetchGoodsDetail(props.goodsId)
      goodsData.value = res.data
    } catch (error) {
      console.error('Failed to fetch goods detail:', error)
    } */
  } else {
    goodsData.value = null
  }
})

// 辅助函数
const formatPrice = (price) => {
  return Number(price).toFixed(2)
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN', { 
    year: 'numeric', 
    month: '2-digit', 
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.goods-detail-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.detail-container {
  padding: 0 20px;
}

.header-info {
  display: flex;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.main-image {
  width: 240px;
  height: 240px;
  margin-right: 24px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #eee;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.basic-info {
  flex: 1;
}

.basic-info .name {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 16px;
}

.basic-info .price {
  font-size: 24px;
  color: #ff5000;
  font-weight: 700;
  margin-bottom: 24px;
}

.meta-info {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.meta-item .label {
  color: #909399;
  min-width: 70px;
}

.meta-item .value {
  color: #606266;
  font-weight: 500;
}

.detail-tabs {
  margin-top: 20px;
}

.description {
  line-height: 1.8;
  color: #606266;
  padding: 10px 0;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 16px;
  margin-top: 16px;
}

.detail-image {
  width: 100%;
  height: 180px;
  border-radius: 6px;
  border: 1px solid #eee;
  cursor: pointer;
  transition: transform 0.2s;
}

.detail-image:hover {
  transform: scale(1.02);
}

.video-container {
  margin-top: 16px;
}

.video-player {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  background: #000;
}

.no-video {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 200px;
  color: #909399;
  font-size: 16px;
}

.no-video .el-icon {
  font-size: 48px;
  margin-bottom: 16px;
}
</style>