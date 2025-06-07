<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="`编辑商品(${goodsId})`" 
    width="900px"
    top="5vh"
    class="goods-dialog"
    :close-on-click-modal="false"
  >
    <el-form 
      v-if="formData" 
      :model="formData" 
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
                    v-model="formData.name" 
                    placeholder="请输入名称" 
                    size="large"
                  />
                </el-form-item>
                
                <!-- 一级分类选择 -->
                <el-form-item label="一级分类" prop="level1Category" required>
                  <el-select 
                    v-model="formData.level1Category" 
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
                    v-model="formData.level2Category" 
                    placeholder="请选择二级分类" 
                    style="width: 100%"
                    :disabled="!formData.level1Category"
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
                    v-model="formData.price"
                    :min="0"
                    :precision="2"
                    :step="0.01"
                    controls-position="right"
                    size="large"
                    placeholder="请输入最低价格"
                  />
                </el-form-item>
                
                <el-form-item label="商品描述" prop="description">
                  <el-input 
                    v-model="formData.description"
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
                    <template v-if="formData.mainImage">
                      <img :src="formData.mainImage" class="upload-image" />
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
              <div v-for="(spec, specIndex) in formData.specs" :key="specIndex" class="spec-group">
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
                    v-for="(specName, index) in formData.specs.map(s => s.name)" 
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
              <div v-if="formData.video" class="video-preview-box">
                <video 
                  :src="formData.video" 
                  controls 
                  class="video-preview"
                ></video>
                <el-button 
                  class="delete-btn"
                  @click="formData.video = ''"
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
import { ref, nextTick, watch, onMounted } from 'vue'
import { Plus, VideoPlay, Delete, Picture } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { firstCategoryList, secondCategoryList } from '@/api/category'
import { uploadFile } from '@/api/file'
import { selectEditGood, updateGoods } from '@/api/product' 

const props = defineProps({
  goodsId: Number
})

const emit = defineEmits(['success'])
const dialogVisible = defineModel()

const formRef = ref(null)
const specInputRef = ref(null)
const activeTab = ref('basic')

// 表单数据结构
const formData = ref({
  name: '',
  level1Category: '',
  level2Category: '',
  mainImage: '',
  description: '',
  price: 0,
  video: '',
  specs: [],
})

// 一级分类数据
const level1Categories = ref([])
// 二级分类数据
const level2Categories = ref([])

// 加载一级分类
const fetchFirstCategoryList = async () => {
  try {
    const res = await firstCategoryList()
    if (res.data.code === 0) {
      level1Categories.value = res.data.data
    } else {
      ElMessage.error('加载一级分类失败')
    }
  } catch (error) {
    ElMessage.error('加载分类数据出错'+error)
  }
}

// 加载二级分类
const loadSecondCategories = async (level1Id) => {
  if (!level1Id) {
    level2Categories.value = []
    return
  }
  
  try {
    const res = await secondCategoryList(level1Id)
    if (res.data.code === 0) {
      level2Categories.value = res.data.data
    } else {
      ElMessage.error('加载二级分类失败')
    }
  } catch (error) {
    ElMessage.error('加载二级分类出错'+error)
  }
}

// 一级分类改变时更新二级分类选项
const handleLevel1Change = async (value) => {
  formData.value.level2Category = ''
  await loadSecondCategories(value)
}

// 处理商品数据反显
const initializeFormData = async () => {
  if (!props.goodsId) return
  
  try {
    const res = await selectEditGood(props.goodsId)
    if (res.data.code === 0) {
      const detail = res.data.data
      console.log(detail)
      // 基本数据填充
      formData.value = {
        name: detail.name,
        level1Category: detail.level1Category,
        level2Category: detail.level2Category,
        description: detail.description,
        price: detail.price,
        mainImage: detail.mainImage,
        video: detail.video || '',
      }
      
      // 如果有一级分类ID，加载对应二级分类
      if (detail.level1Category) {
        await loadSecondCategories(detail.level1Category)
      }
      
       // 处理规格反显
      if (detail.specs && detail.specs.length > 0) {
        // 1. 转换规格数据
        formData.value.specs = detail.specs.map(spec => ({
          name: spec.name,
          values: [...spec.values],
          inputVisible: false,
          inputValue: ''
        }))
        
        // 2. 处理SKU数据 - 直接使用后端返回的SKUs
        formData.value.skus = detail.skus.map(sku => ({
          specValues: [...sku.specValues], // 复制数组
          price: sku.price,
          stock: sku.stock,
          image: sku.image || ''
        }))
        
        // 3. 根据规格生成笛卡尔积组合
        generateSpecCombinations();
      }
      
      // 默认打开规格标签页如果存在规格
      if (detail.specs?.length) {
        activeTab.value = 'specs'
      }
      
    } else {
      ElMessage.error('加载商品详情失败: ' + res.data.message)
    }
  } catch (error) {
    ElMessage.error('加载商品详情出错: ' + error.message)
  }
}
// 生成规格组合（笛卡尔积）
const generateSpecCombinations = () => {
  // 确保规格数组存在
  if (!formData.value.specs || !Array.isArray(formData.value.specs)) {
    formData.value.specs = []
  }

  // 如果规格为空，清空组合
  if (!formData.value.specs || formData.value.specs.length === 0) {
    specCombinations.value = [];
    return;
  }
  
   // 获取所有规格值的数组
  const specValuesArrays = formData.value.specs.map(spec => {
    if (!spec.values || !Array.isArray(spec.values)) {
      return []
    }
    return spec.values
  })
  
  // 计算笛卡尔积
  let combinations = [];
  
  const calculateCombinations = (current, index) => {
    if (index === specValuesArrays.length) {
      // 尝试找到现有的匹配项
      const existingCombination = specCombinations.value.find(item => 
        item.specValues.length === current.length &&
        item.specValues.every((v, i) => v === current[i])
      )
      
      combinations.push(
        existingCombination || {
          specValues: [...current],
          price: 0,
          stock: 0,
          image: '',
          id: null
        }
      )
      return
    }
    
    specValuesArrays[index].forEach(value => {
      calculateCombinations([...current, value], index + 1);
    });
  }
  
  calculateCombinations([], 0);
  
  // 设置规格组合到ref
  specCombinations.value = combinations;
  
  // 使用后端返回的SKU数据填充组合值
  populateCombinationValues();
}
// 使用后端SKU数据填充组合值
const populateCombinationValues = () => {
  // 如果没有SKU数据，结束填充
  if (!formData.value.skus || formData.value.skus.length === 0) {
    return;
  }
  
  // 遍历所有组合
  specCombinations.value.forEach(combo => {
    // 在SKUs中查找匹配的规格组合
    const matchedSku = formData.value.skus.find(sku => {
      // 比较规格值是否完全相同
      if (sku.specValues.length !== combo.specValues.length) {
        return false;
      }
      
      // 检查每个规格值是否匹配
      return sku.specValues.every((value, index) => 
        value === combo.specValues[index]
      );
    });
    
    // 如果找到匹配的SKU，填充值
    if (matchedSku) {
      combo.price = matchedSku.price;
      combo.stock = matchedSku.stock;
      combo.image = matchedSku.image;
    }
  });
}
// 计算所有规格组合（笛卡尔积）
const specCombinations = ref([])
// 创建独立的生成函数
const generateCombinations = () => {
  if (!formData.value.specs || formData.value.specs.length === 0) {
    specCombinations.value = []
    return
  }
  
  // 获取所有规格值的数组
  const specValuesArrays = formData.value.specs.map(spec => spec.values)
  
  // 创建一个临时数组用于存放当前规格组合
  let combinations = []
  
  // 计算笛卡尔积
  const calculateCombinations = (current, index) => {
    if (index === specValuesArrays.length) {
      // 尝试找到现有的匹配项
      const existingCombination = specCombinations.value.find(item => 
        item.specValues.length === current.length &&
        item.specValues.every((v, i) => v === current[i])
      )
      
      combinations.push(
        existingCombination || {
          specValues: [...current],
          price: 0,
          stock: 0,
          image: '',
          id: null
        }
      )
      return
    }
    
    specValuesArrays[index].forEach(value => {
      calculateCombinations([...current, value], index + 1)
    })
  }
  
  calculateCombinations([], 0)
  
  // 使用新数组替换原数组（避免触发全量重新渲染）
  specCombinations.value = combinations
}
// 添加规格
const addSpecName = () => {
  // 确保规格数组存在
  if (!formData.value.specs || !Array.isArray(formData.value.specs)) {
    formData.value.specs = []
  }
  formData.value.specs.push({
    name: '',
    values: [],
    inputVisible: false,
    inputValue: ''
  })
    // 生成新的规格组合
  generateCombinations()
}

// 删除规格
const removeSpecName = (index) => {
  formData.value.specs.splice(index, 1)
   // 重新生成规格组合
  generateCombinations()
}

// 显示规格值输入框
const showInput = (specIndex) => {
  formData.value.specs[specIndex].inputVisible = true
  nextTick(() => {
    if (specInputRef.value) {
      specInputRef.value[specIndex]?.focus()
    }
  })
}

// 处理规格值输入确认
const handleInputConfirm = (specIndex) => {
  const inputValue = formData.value.specs[specIndex].inputValue
  if (inputValue) {
    formData.value.specs[specIndex].values.push(inputValue)
    formData.value.specs[specIndex].inputVisible = false
    formData.value.specs[specIndex].inputValue = ''
    
    // 添加新值后重新生成组合，同时保留已有数据
    generateCombinations()
  }
}

// 移除规格值
const removeSpecValue = (specIndex, valueIndex) => {
   // 先移除要删除的值
 formData.value.specs[specIndex].values.splice(valueIndex, 1)
  
  // 移除后重新生成组合，但保留其他规格组合的数据
  generateCombinations()
}

// 处理主图上传
const uploadMainImage = async (fileObj) => {
  try {
    const file = fileObj.file
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      formData.value.mainImage = res.data.data
      return true
    } else {
      ElMessage.error('上传失败: ' + res.data.message)
      return false
    }
  } catch (error) {
    ElMessage.error('上传出错: ' + error.message)
    return false
  }
}

// 处理规格图片上传
const uploadSpecImage = async (fileObj, row) => {
  try {
    const file = fileObj.file
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      row.image = res.data.data
      return true
    } else {
      ElMessage.error('上传失败: ' + res.data.message)
      return false
    }
  } catch (error) {
    ElMessage.error('上传出错: ' + error.message)
    return false
  }
}

// 处理视频上传
const uploadVideo = async (fileObj) => {
  try {
    const file = fileObj.file
    const res =  await uploadFile(file)
    if (res.data.code === 0) {
      formData.value.video = res.data.data
      return true
    } else {
      ElMessage.error('上传失败: ' + res.data.message)
      return false
    }
  } catch (error) {
    ElMessage.error('上传出错: ' + error.message)
    return false
  }
}

// 提交表单
const submit = async () => {
  try {
    await formRef.value.validate()
     // 确保规格组合数据是最新的
    // 整理数据
    const productData = {
      id: props.goodsId, // 保留商品ID用于更新
      name: formData.value.name,
      level1Category: formData.value.level1Category,
      level2Category: formData.value.level2Category,
      description: formData.value.description,
      price: formData.value.price,
      mainImage: formData.value.mainImage,
      video: formData.value.video,
      // 规格定义
      specs: formData.value.specs.map(spec => ({
        name: spec.name,
        values: spec.values
      })),
      // SKU列表
      skus: specCombinations.value.map(item => ({
        id: item.id, // 保留已有的SKU ID
        specValues: item.specValues,
        price: item.price,
        stock: item.stock,
        image: item.image || ''
      }))
    }

    // 调用更新商品API
    console.log()
    const res = await updateGoods(productData)
    if (res.data.code === 0) {
      ElMessage.success('商品更新成功！')
      dialogVisible.value = false
      emit('success')
    } else {
      ElMessage.error(`保存失败: ${res.data.message}`)
    }
  } catch (error) {
    console.error('表单验证失败', error)
  }
}

// 监听商品ID变化
watch(() => props.goodsId, (newId) => {
  if (newId && dialogVisible.value) {
    initializeFormData()
  }
})

// 监听对话框打开状态
watch(dialogVisible, (visible) => {
  if (visible && props.goodsId) {
    initializeFormData()
  }
})

// 初始化时加载一级分类
onMounted(() => {
  fetchFirstCategoryList()
})
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
  height: 480px;
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