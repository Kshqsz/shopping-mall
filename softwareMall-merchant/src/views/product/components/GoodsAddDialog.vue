<template>
  <el-dialog 
    v-model="dialogVisible" 
    title="新增商品" 
    width="900px"
    top="5vh"
    class="goods-dialog"
    :close-on-click-modal="false"
  >
    <el-form 
      :model="form" 
      ref="formRef" 
      label-width="120px"
      label-position="top"
    >
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <div class="dialog-content">
            <el-row :gutter="24">
              <el-col :span="12">
                <el-form-item label="商品名称" prop="name" required>
                  <el-input 
                    v-model="form.name" 
                    placeholder="请输入名称" 
                    size="large"
                  />
                </el-form-item>
                
                <!-- 一级分类选择 -->
                <el-form-item label="一级分类" prop="level1Category" required>
                  <el-select 
                    v-model="form.level1Category" 
                    placeholder="请选择一级分类" 
                    style="width: 100%"
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
                
                <!-- 二级分类选择 -->
                <el-form-item label="二级分类" prop="level2Category" required>
                  <el-select 
                    v-model="form.level2Category" 
                    placeholder="请选择二级分类" 
                    style="width: 100%"
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
                <el-form-item 
                  label="最低价格" 
                  prop="price"
                  required
                  >
                  <el-input-number 
                    v-model="form.price"
                    :min="0"
                    :precision="2"
                    :step="0.01"
                    controls-position="right"
                    size="large"
                    placeholder="请输入最低价格"
                  />
              </el-form-item>
                <el-form-item label="商品描述" prop="desc">
                  <el-input 
                    v-model="form.description"
                    type="textarea"
                    :rows="4"
                    placeholder="请输入描述"
                    maxlength="200"
                    show-word-limit
                  />
                </el-form-item>
              </el-col>
              
              <el-col :span="12">
                <el-form-item label="商品主图" prop="mainImage" required>
                  <el-upload
                    :show-file-list="false"
                    :http-request="uploadMainImage"
                    class="upload-box main-image-upload"
                  >
                    <template v-if="form.mainImage">
                      <img :src="form.mainImage" class="upload-image" />
                    </template>
                    <template v-else>
                      <div class="upload-placeholder">
                        <el-icon><Plus /></el-icon>
                        <div>上传主图</div>
                        <div class="upload-tip">建议尺寸：600 × 600 像素</div>
                      </div>
                    </template>
                  </el-upload>
                </el-form-item>
              </el-col>
            </el-row>
          </div>
        </el-tab-pane>
        
       <!-- 商品规格标签页 -->
        <el-tab-pane label="商品规格" name="specs">
          <div class="dialog-content">
            <div class="spec-form">
              <!-- 规格名 -->
              <div class="spec-name">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="addSpecName"
                >
                  添加规格
                </el-button>
              </div>

          <!-- 规格值编辑区域 -->
          <div v-for="(spec, specIndex) in form.specs" :key="specIndex" class="spec-group">
            <div class="spec-header">
              <el-input 
                v-model="spec.name" 
                placeholder="规格名称（如：颜色）" 
                style="width: 200px;"
              />
              <el-button 
                type="danger" 
                size="small" 
                icon="Delete" 
                circle 
                @click="removeSpecName(specIndex)"
                style="margin-left: 10px;"
              />
            </div>
            
            <div class="spec-values">
              <el-tag
                v-for="(value, valueIndex) in spec.values"
                :key="valueIndex"
                closable
                @close="removeSpecValue(specIndex, valueIndex)"
              >
                {{ value }}
              </el-tag>
              <el-input
                v-if="spec.inputVisible"
                ref="specInputRef"
                v-model="spec.inputValue"
                size="small"
                style="width: 100px;"
                @keyup.enter="handleInputConfirm(specIndex)"
                @blur="handleInputConfirm(specIndex)"
              />
              <el-button 
                v-else 
                size="small" 
                @click="showInput(specIndex)"
              >
                + 添加
              </el-button>
            </div>
          </div>
          
          <!-- 规格组合表格 -->
          <div v-if="specCombinations.length > 0" class="spec-table">
            <el-table 
              :data="specCombinations" 
              border
              style="width: 100%; margin-top: 20px;"
            >
              <el-table-column 
                v-for="(specName, index) in form.specs.map(s => s.name)" 
                :key="index"
                :label="specName"
              >
                <template #default="{ row }">
                  {{ row.specValues[index] }}
                </template>
              </el-table-column>
              
              <el-table-column label="价格" width="150">
                <template #default="{ row }">
                  <el-input-number 
                    v-model="row.price" 
                    :min="0" 
                    :precision="2" 
                    size="small"
                    controls-position="right"
                  />
                </template>
              </el-table-column>
              
              <el-table-column label="库存" width="150">
                <template #default="{ row }">
                  <el-input-number 
                    v-model="row.stock" 
                    :min="0" 
                    size="small"
                    controls-position="right"
                  />
                </template>
              </el-table-column>
              
              <!-- 图片列（带预览功能） -->
              <el-table-column label="图片" width="120">
                <template #default="{ row }">
                  <div class="image-cell">
                    <el-upload
                      :show-file-list="false"
                      :http-request="(file) => uploadSpecImage(file, row)"
                      class="upload-spec-image"
                    >
                      <el-image
                        v-if="row.image"
                        :src="row.image"
                        fit="cover"
                        class="spec-image"
                        :preview-src-list="[row.image]"
                        hide-on-click-modal
                      >
                        <template #error>
                          <div class="image-error">
                            <el-icon><Picture /></el-icon>
                          </div>
                        </template>
                      </el-image>
                      <div v-else class="upload-placeholder">
                        <el-icon><Plus /></el-icon>
                      </div>
                    </el-upload>
                    <el-button 
                      v-if="row.image"
                      class="delete-btn"
                      type="danger"
                      icon="Delete"
                      circle
                      size="small"
                      @click.stop="row.image = ''"
                    />
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </div>
        </el-tab-pane>
        
        <el-tab-pane label="媒体信息" name="media">
          <div class="dialog-content">
            <el-form-item label="商品视频" prop="video">
              <el-upload
                :http-request="uploadVideo"
                accept="video/*"
                :show-file-list="false"
              >
                <template #trigger>
                  <el-button type="primary" size="large">
                    <el-icon><VideoPlay /></el-icon>
                    <span>上传视频</span>
                  </el-button>
                </template>
              </el-upload>
              <div v-if="form.video" class="video-preview-box">
                <video 
                  :src="form.video" 
                  controls 
                  class="video-preview"
                ></video>
                <el-button 
                  class="delete-btn"
                  @click="form.video = ''"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
              <div class="upload-tip">支持 MP4 格式，时长不超过 5 分钟</div>
            </el-form-item>
          </div>
        </el-tab-pane>
      </el-tabs>
      
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit" size="large">
        保存商品信息
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive,computed,nextTick  } from 'vue'
import { Plus, VideoPlay, Delete, Picture } from '@element-plus/icons-vue'
import {firstCategoryList,secondCategoryList} from '@/api/category'
import { uploadFile } from '@/api/file'
import { addGoods } from '@/api/product' 

const dialogVisible = defineModel()
const formRef = ref(null)
const specInputRef = ref(null)
// 表单数据
const form = reactive({
  name: '',
  level1Category: '',
  level2Category: '',
  mainImage: '',
  description:'',
  price: 0,
  video: '',
  // 规格数据
  specs: [], // 规格定义
  skus: []   // 将计算的规格组合存储在这里
})

// 计算所有规格组合（笛卡尔积）
const specCombinations = computed(() => {
  if (form.specs.length === 0) {
    return []
  }
  
  // 获取所有规格值的数组
  const specValuesArrays = form.specs.map(spec => spec.values)
  
  // 计算笛卡尔积
  let combinations = []
  
  const calculateCombinations = (current, index) => {
    if (index === specValuesArrays.length) {
      combinations.push({
        specValues: [...current],
        price: 0,
        stock: 0,
        sku: '',
        image: ''
      })
      return
    }
    
    specValuesArrays[index].forEach(value => {
      calculateCombinations([...current, value], index + 1)
    })
  }
  
  calculateCombinations([], 0)
  return combinations
})

// 添加规格
const addSpecName = () => {
  form.specs.push({
    name: '',
    values: [],
    inputVisible: false,
    inputValue: ''
  })
}

// 删除规格
const removeSpecName = (index) => {
  form.specs.splice(index, 1)
}

// 显示规格值输入框
const showInput = (specIndex) => {
  form.specs[specIndex].inputVisible = true
  nextTick(() => {
    if (specInputRef.value) {
      specInputRef.value[specIndex]?.focus()
    }
  })
}

// 处理规格值输入确认
const handleInputConfirm = (specIndex) => {
  const inputValue = form.specs[specIndex].inputValue
  if (inputValue) {
    form.specs[specIndex].values.push(inputValue)
    form.specs[specIndex].inputVisible = false
    form.specs[specIndex].inputValue = ''
  }
}

// 移除规格值
const removeSpecValue = (specIndex, valueIndex) => {
  form.specs[specIndex].values.splice(valueIndex, 1)
}


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


// 处理主图上传
const uploadMainImage = async (fileObj) => {
  try {
    const file = fileObj.file
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      form.mainImage = res.data.data // 赋值给表单数据
      return true
    } else {
      console.error('上传失败', res.message)
      return false
    }
  } catch (error) {
    console.error('上传出错', error)
    return false
  }
}

// 处理规格图片上传
const uploadSpecImage = async (fileObj, row) => {
  try {
    const file = fileObj.file
    // 调用API上传
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      row.image = res.data.data // 直接赋值给当前行的图片字段
      return true
    } else {
      console.error('上传失败', res.message)
      return false
    }
  } catch (error) {
    console.error('上传出错', error)
    return false
  }
}

// 处理视频上传
const uploadVideo = async (fileObj) => {
  try {
    const file = fileObj.file
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      form.video = res.data.data // 赋值给表单数据
      return true
    } else {
      console.error('上传失败', res.message)
      return false
    }
  } catch (error) {
    console.error('上传出错', error)
    return false
  }
}


const submit = async () => {
  await formRef.value.validate()
  
  // 整理数据
  const productData = {
    name: form.name,
    level1Category: form.level1Category,
    level2Category: form.level2Category,
    description: form.description,
    price: form.price,
    mainImage: form.mainImage,
    video: form.video,
    // 规格定义
    specs: form.specs.map(spec => ({
      name: spec.name,
      values: spec.values
    })),
    // SKU列表
    skus: specCombinations.value.map(item => ({
      specValues: item.specValues,
      price: item.price,
      stock: item.stock,
      image: item.image || '' // 如果没有图片则为空字符串
    }))
  }

  
  // 调用新增商品API
  console.log('发送的商品数据:', productData)
  const res = await addGoods(productData)
  if (res.data.code === 0) {
      // 4. 保存成功后操作
      ElMessage.success('商品添加成功！')
      resetForm()
      dialogVisible.value = false
    } else {
      ElMessage.error(`保存失败: ${res.data.message}`)
    }
  
}
const resetForm = async() => {
  // 1. 重置表单字段
  formRef.value.resetFields()
  
  // 2. 重置规格相关数据
  form.specs = []
  
  // 3. 重置所有分类选择
  level1Categories.value = await fetchFirstCategoryList()
  level2Categories.value = []
}

</script>

<style scoped>
.goods-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.dialog-content {
  padding: 0 20px;
}

.upload-box {
  border: 1px dashed #d9d9d9;
  border-radius: 8px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s;
}

.upload-box:hover {
  border-color: #409eff;
}

.main-image-upload {
  width: 100%;
  height: 280px;
}

.upload-placeholder {
  text-align: center;
  color: #8c939d;
}

.upload-placeholder .el-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.upload-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-tip {
  font-size: 13px;
  color: #8c939d;
  margin-top: 10px;
}

.video-preview-box {
  position: relative;
  margin-top: 16px;
}

.video-preview {
  width: 100%;
  max-height: 300px;
  border-radius: 8px;
  background: #000;
}

.delete-btn {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(0,0,0,0.5);
  color: #fff;
  border: none;
}

.delete-btn:hover {
  background: rgba(0,0,0,0.7);
}

.detail-upload {
  margin-top: 10px;
}

/* 规格表单样式 */
.spec-form {
  margin-top: 15px;
}

.spec-group {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.spec-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.spec-values {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-items: center;
}

.spec-image {
  width: 50px;
  height: 50px;
  object-fit: cover;
  border-radius: 4px;
  cursor: pointer;
}

.upload-spec-image {
  cursor: pointer;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.upload-spec-image:hover {
  border-color: #409eff;
}

/* 表单间距优化 */
.el-form-item {
  margin-bottom: 20px;
}

/* 分类选择器样式 */
.el-select {
  width: 100%;
}
</style>