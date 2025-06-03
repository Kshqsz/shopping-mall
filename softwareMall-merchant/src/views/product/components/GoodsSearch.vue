<template>
  <div class="goods-search">
    <el-form :model="form" label-width="100px" label-position="top">
      <el-row :gutter="24">
        <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
          <el-form-item label="商品名称">
            <el-input 
              v-model="form.name" 
              placeholder="请输入名称" 
              clearable
              suffix-icon="Search"
            />
          </el-form-item>
        </el-col>
        
        <!-- 一级分类选择 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
          <el-form-item label="一级分类">
            <el-select 
              v-model="form.level1Category" 
              placeholder="请选择一级分类" 
              clearable
              @change="handleLevel1Change"
            >
              <el-option 
                v-for="item in level1Categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <!-- 二级分类选择 -->
        <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
          <el-form-item label="二级分类">
            <el-select 
              v-model="form.level2Category" 
              placeholder="请选择二级分类" 
              clearable
              :disabled="!form.level1Category"
            >
              <el-option 
                v-for="item in level2Categories"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
          <el-form-item label="状态筛选">
            <el-select 
              v-model="form.status" 
              placeholder="全部状态" 
              clearable
            >
              <el-option label="全部" :value=null />
              <el-option label="审核中" :value="0" />
              <el-option label="已下架" :value="-1" />
              <el-option label="已上架" :value="1" />
            </el-select>
          </el-form-item>
        </el-col>
        
        <el-col :xs="24" :sm="12" :md="8" :lg="8" :xl="6">
          <el-form-item label="排序方式">
            <el-select 
              v-model="form.sort" 
              placeholder="请选择排序"
            >
              <el-option label="销量从高到低" value="salesDesc" />
              <el-option label="销量从低到高" value="salesAsc" />
              <el-option label="价格从高到低" value="priceDesc" />
              <el-option label="价格从低到高" value="priceAsc" />
              <el-option label="最新上架" value="newest" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row>
        <el-col :span="24" style="text-align: right;">
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>
            <span>搜索</span>
          </el-button>
          <el-button @click="reset">
            <el-icon><Refresh /></el-icon>
            <span>重置</span>
          </el-button>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import {firstCategoryList,secondCategoryList} from '@/api/category'
const emit = defineEmits(['search', 'reset'])

// 一级分类数据
const level1Categories = ref([
])
//获取一级分类
const fetchFirstCategoryList = async () => {
  const result = (await firstCategoryList()).data.data
  level1Categories.value = result
}
fetchFirstCategoryList()
// 二级分类数据
const level2Categories = ref([])
// 一级分类改变时更新二级分类选项
const handleLevel1Change = async (value) => {
  form.level2Category = '' // 清空二级分类选择
  if (value) {
    const result = await (await secondCategoryList(value)).data.data
    console.log(result)
    level2Categories.value = result
  } else {
    level2Categories.value = []
  }
}

const form = reactive({
  name: '',
  level1Category: '',
  level2Category: '',
  status:'',
  sort: null
})

const search = () => {
  emit('search', { ...form })
}

const reset = () => {
  form.name = ''
  form.level1Category = ''
  form.level2Category = ''
  form.status = null
  level2Categories.value = []
  emit('reset')
}
</script>

<style scoped>
.goods-search {
  padding: 12px 0;
}

.el-form-item {
  margin-bottom: 16px;
}

.el-form-item__label {
  font-weight: 500;
  color: #606266;
  padding-bottom: 6px;
}
</style>