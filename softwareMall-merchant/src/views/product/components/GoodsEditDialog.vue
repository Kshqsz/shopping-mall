<template>
  <el-dialog 
    v-model="dialogVisible" 
    :title="`编辑商品(${goodsId})`" 
    width="800px"
  >
    <el-form 
      v-if="formData" 
      :model="formData" 
      ref="formRef" 
      label-width="100px"
    >
      <!-- 与新增商品表单结构相同 -->
      <!-- 为了节省篇幅，此处省略重复的表单代码 -->
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
// import { fetchGoodsDetail, updateGoods } from '@/api/goods'
const emit = defineEmits(['success'])
const props = defineProps({
  goodsId: Number
})

const dialogVisible = defineModel()
const formRef = ref(null)
const formData = ref(null)

watch(() => props.goodsId, async () => {
  if (props.goodsId && dialogVisible.value) {
    // 获取商品详情
    //const res = await fetchGoodsDetail(props.goodsId)
    //formData.value = res.data
  }
})

const submit = async () => {
  await formRef.value.validate()
  
  // 调用更新商品API
  //await updateGoods(props.goodsId, formData.value)
  
  dialogVisible.value = false
  emit('success')
}


</script>