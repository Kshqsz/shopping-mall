<template>
  <div class="address-management">
    <div class="header">
      <div class="header-left">
        <el-icon class="header-icon"><Location /></el-icon>
        <h2>收货地址管理</h2>
      </div>
      <el-button type="primary" size="medium" @click="openDialog">
        <el-icon class="el-icon--left"><Plus /></el-icon>
        添加新地址
      </el-button>
    </div>
    <el-card shadow="never" class="address-card">
      <el-table :data="addresses" style="width: 100%" :empty-text="'暂无收货地址'">
        <el-table-column prop="name" label="收货人" width="100">
          <template #default="{row}">
            <div class="cell-content">
              <el-icon><User /></el-icon>
              <span>{{ row.name }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150">
          <template #default="{row}">
            <div class="cell-content">
              <el-icon><Iphone /></el-icon>
              <span>{{ row.phone }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="收货地址" width="350">
          <template #default="{row}">
            <div class="cell-content">
              <el-icon><LocationFilled /></el-icon>
              <span>{{ formatRegion(row) }} {{ row.detail }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="默认地址" width="80" align="center">
          <template #default="{row}">
            <el-tag v-if="row.isDefault" type="success" effect="dark">
              默认
            </el-tag>
            <el-tag v-else type="info" effect="plain">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="230" align="center">
          <template #default="{row}">
            <div class="action-buttons">
              <div class="left-buttons">
                <el-button 
                  type="primary" 
                  size="small" 
                  @click="editAddress(row)" 
                  :icon="Edit" 
                  link
                >
                  编辑
                </el-button>
                <el-button 
                  type="danger" 
                  size="small" 
                  :disabled="row.isDefault" 
                  @click="deleteTheAddress(row.id)"
                  :icon="Delete" 
                  link
                >
                  删除
                </el-button>
              </div>
              <el-button 
                class="set-default-btn"
                type="warning" 
                size="small" 
                @click="setDefault(row.id)"
                v-if="!row.isDefault"
                :icon="StarFilled" 
                link
              >
                设默认
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑地址对话框 -->
    <el-dialog 
      :title="dialogType === 'add' ? '添加收货地址' : '修改收货地址'" 
      v-model="dialogVisible"
      width="600px"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入收货人手机号">
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-cascader
            v-model="form.region"
            :options="regionOptions"
            placeholder="请选择省市区"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input v-model="form.detail" placeholder="街道、小区、楼栋号等">
            <template #prefix>
              <el-icon><LocationFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item label="默认地址" prop="isDefault">
          <el-switch v-model="form.isDefault" active-text="设为默认地址" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveAddress">
            <el-icon class="el-icon--left"><Check /></el-icon>
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive , onMounted } from 'vue';
// import { ElMessage } from 'element-plus';
import { regionData,codeToText } from 'element-china-area-data';
import { addAddress ,deleteAddress,toDefault,addressList,updateAddress} from '@/api/user';
import {
  Location,
  Plus,
  User,
  Iphone,
  LocationFilled,
  Check,
  Edit,
  Delete,
  StarFilled
} from '@element-plus/icons-vue';

// 地址数据
const addresses = ref([
]);

// 表单相关状态
const form = reactive({
  id: null,
  name: '',
  phone: '',
  region: [],
  detail: '',
  isDefault: false
});

const rules = reactive({
  name: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请选择所在地区', trigger: 'change' }
  ],
  detail: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
});

// 地区选择器选项
const regionOptions = ref(regionData);

// 对话框状态
const dialogVisible = ref(false);
const dialogType = ref('add'); // 'add' 或 'edit'
const formRef = ref(null);

// 打开添加对话框
const openDialog = () => {
  dialogType.value = 'add';
  // 重置表单
  Object.assign(form, {
    id: null,
    name: '',
    phone: '',
    region: [],
    detail: '',
    isDefault: false
  });
  dialogVisible.value = true;
};

// 编辑地址
const editAddress = async(address) => {
  dialogType.value = 'edit';
  // 填充表单数据
  Object.assign(form, {
    id: address.id,
    name: address.name,
    phone: address.phone,
    region: address.region,
    detail: address.detail,
    isDefault: address.isDefault
  });
  dialogVisible.value = true;
  
};

// 保存地址
const saveAddress = async() => {
  try {
    await formRef.value.validate();
    
    // 准备提交数据
    const addressData = {
      name: form.name,
      phone: form.phone,
      region: form.region,
      detail: form.detail,
      isDefault: form.isDefault
    };
    
    if (dialogType.value === 'add') {
      // 添加新地址

      await addAddress(addressData);
      // eslint-disable-next-line no-undef
      ElMessage.success('地址添加成功');
      getAddressList()
    } else {
      // 更新地址
      await updateAddress(form)
      // eslint-disable-next-line no-undef
      ElMessage.success('地址更新成功');
      getAddressList()
    }
    
    dialogVisible.value = false;
  } catch (error) {
    if (error.response) {
      // eslint-disable-next-line no-undef
      ElMessage.error(error.response.data.message || '操作失败');
    } else if (error.message) {
      // eslint-disable-next-line no-undef
      ElMessage.warning(error.message);
    }
  }
};

// 删除地址
const deleteTheAddress = async (address) => {
  // eslint-disable-next-line no-undef
  ElMessageBox.confirm(
    `确定要删除 ${address.name} 的地址吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    await deleteAddress(address)
    // eslint-disable-next-line no-undef
    ElMessage.success('地址已删除');
    getAddressList()
  }).catch(() => {});
};

// 设置为默认地址
const setDefault = async (id) => {
  await toDefault(id)
  // eslint-disable-next-line no-undef
  ElMessage.success('默认地址设置成功');
  getAddressList()
};


const formatRegion = (row) => {
  // 提取地区编码
  const provinceCode = row.province;
  const cityCode = row.city;
  const districtCode = row.district;
  
  // 查找地区名称
  const province = codeToText[provinceCode] ?? provinceCode;
const city = codeToText[cityCode] ?? cityCode;
const district = codeToText[districtCode] ?? districtCode;
  
  return ` ${city} / ${province} / ${district}`;
};
// 查询地址列表
const getAddressList = async () => {
  try {
    const  data = (await addressList()).data.data
    console.log(data)
    addresses.value = data.map(item => ({
      ...item,
      // 确保region是数组格式
      province: item.province || "",
      city: item.city || "",
      district: item.district || ""
    }));
  } catch (error) {
    ElMessage.error('获取地址列表失败: ' + (error.message || error));
  }
};
onMounted(() => {
  getAddressList();
});
</script>

<style scoped>
.address-management {
  padding: 20px;
  box-sizing: border-box;
  min-height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 200px;
}

.address-card {
  width: 100%;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  border: none;
  flex: 1;
  overflow: hidden;
}

/* 响应式调整 */
@media (max-width: 992px) {
  .address-management {
    padding: 16px;
  }
  
  .header-left h2 {
    font-size: 18px;
  }
  
  .el-button {
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .address-management {
    padding: 12px;
  }
  
  .header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .cell-content {
    flex-wrap: wrap;
  }
  
  .action-buttons {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .left-buttons {
    width: 100%;
    justify-content: space-between;
  }
  
  .set-default-btn {
    margin-left: 0;
    width: 100%;
  }
}

@media (max-width: 576px) {
  .address-card {
    border-radius: 0;
    box-shadow: none;
    border: 1px solid var(--el-border-color-light);
  }
  
  .el-table-column {
    padding: 8px 0;
  }
  
  .el-tag {
    font-size: 12px;
  }
}

/* 表格单元格优化 */
.cell-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.cell-content span {
  word-break: break-word;
}

/* 操作按钮优化 */
.action-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  gap: 8px;
}

.left-buttons {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.set-default-btn {
  white-space: nowrap;
}

/* 滚动条优化 */
:deep(.el-table__body-wrapper) {
  scrollbar-width: thin;
  scrollbar-color: var(--el-color-primary-light-5) var(--el-color-primary-light-9);
}

:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  height: 6px;
  width: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background-color: var(--el-color-primary-light-5);
  border-radius: 3px;
}
</style>