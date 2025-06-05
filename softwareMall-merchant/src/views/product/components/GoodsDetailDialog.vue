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
          <div class="price">¥{{ formatPrice(goodsData.lowPrice) }}</div>
          
          <div class="meta-info">
            <div class="meta-item">
              <span class="label">一级分类：</span>
              <span class="value">{{ goodsData.level1CategoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">二级分类：</span>
              <span class="value">{{goodsData.level2CategoryName }}</span>
            </div>
            <div class="meta-item">
              <span class="label">总销量：</span>
              <span class="value">{{ goodsData.sales || 0 }}</span>
            </div>
            <div class="meta-item">
              <span class="label">商品状态：</span>
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
            {{ goodsData.description || '暂无描述' }}
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="商品规格" name="specs">
  <div v-if="goodsData.specItemVos && goodsData.specItemVos.length > 0">
    <!-- 显示规格名称（从第一个SKU的name中提取） -->
    <div class="spec-names">
      <h3>规格组合：</h3>
      <div class="spec-name-tags">
        <el-tag 
          v-for="(specName, index) in extractSpecNames(goodsData.specItemVos[0].name)" 
          :key="index"
          type="info"
          class="spec-name-tag"
        >
          {{ specName }}
        </el-tag>
      </div>
    </div>
    
    <!-- 显示SKU列表 -->
    <el-table 
      :data="goodsData.specItemVos" 
      border
      style="width: 100%; margin-top: 20px;"
    >
      <el-table-column 
        label="规格组合" 
        prop="name"
      >
        <template #default="{ row }">
          <div class="sku-name">{{ row.name }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="价格" width="150" align="center">
        <template #default="{ row }">
          <div class="sku-price">¥{{ formatPrice(row.price) }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="库存" width="150" align="center">
        <template #default="{ row }">
          <div class="sku-stock">{{ row.stock }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="销量" width="150" align="center">
        <template #default="{ row }">
          <div class="sku-sales">{{ row.sales }}</div>
        </template>
      </el-table-column>
      
      <el-table-column label="图片" width="120" align="center">
        <template #default="{ row }">
          <el-image
            v-if="row.image"
            :src="row.image"
            fit="cover"
            class="sku-image"
            :preview-src-list="[row.image]"
            hide-on-click-modal
          >
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
          <div v-else class="no-image">
            <el-icon><Picture /></el-icon>
          </div>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <div v-else class="no-specs">
    <el-empty description="该商品没有设置规格" />
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
import { VideoCameraFilled, Picture } from '@element-plus/icons-vue'
import { selectGoodDetail } from '@/api/product'

const props = defineProps({
  goodsId: {
    type: Number,
    required: true
  }
})

const dialogVisible = defineModel()

// 状态映射
const statusText = {
  0: '上架审核中',
  [-1]: '已下架',
  1: '已上架'
}
const statusType = {
  0: 'warning',
  [-1]: 'danger',
  1: 'success'
}

const activeTab = ref('desc')
const goodsData = ref(null)
const level2Categories = ref([])



// 获取商品详情
const fetchGoodsData = async () => {
  try {
    selectGoodDetail(props.goodsId)
    goodsData.value = (await selectGoodDetail(props.goodsId)).data.data
    
    
  } catch (error) {
    console.error('获取商品详情失败:', error)
  }
}

watch(dialogVisible, async (visible) => {
  if (visible && props.goodsId) {
    await fetchGoodsData()
  } else {
    goodsData.value = null
    level2Categories.value = []
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

// 从SKU名称中提取规格名称（例如："哑光黑/12+256" -> ["颜色", "内存"]）
const extractSpecNames = (skuName) => {
  if (!skuName) return []
  // 这里假设规格名称是用"/"分隔的
  const parts = skuName.split('/')
  // 根据您的实际业务逻辑返回规格名称
  return parts.length === 2 ? ['颜色', '内存'] : ['规格']
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
  white-space: pre-wrap;
}

.spec-group {
  margin-bottom: 20px;
}

.spec-title {
  font-size: 16px;
  margin-bottom: 10px;
  color: #606266;
}

.spec-values {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.spec-value-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}

.sku-price {
  color: #ff5000;
  font-weight: 600;
}

.sku-stock {
  font-weight: 500;
}

.sku-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  border: 1px solid #eee;
}

.no-image {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
  border: 1px dashed #ddd;
  border-radius: 4px;
}

.no-specs {
  padding: 40px 0;
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

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}
</style>