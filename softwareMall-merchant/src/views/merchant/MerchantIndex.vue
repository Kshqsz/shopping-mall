<script setup>  
import { ref, onMounted } from 'vue';  
import { useMerchantStore } from '@/stores';
import { merchantUpdateService, merchantGetByIdService, merchantUpdatePasswordService } from '@/api/merchant'
import { ElMessage } from 'element-plus';

// 店铺数据
const merchantStore = useMerchantStore()
const userInfo = ref(merchantStore.merchant);

// 对话框控制
const showEditDialog = ref(false);  
const showChangePasswordDialog = ref(false);  

// 统计信息数据（模拟）
const stats = ref({
  todayOrders: 18,
  monthlySales: 42800,
  productCount: 11,
  customerRating: 4.8
});

// 销售趋势数据（模拟）
const salesTrend = ref([
  { month: '1月', sales: 8200 },
  { month: '2月', sales: 9320 },
  { month: '3月', sales: 9010 },
  { month: '4月', sales: 9340 },
  { month: '5月', sales: 12900 },
  { month: '6月', sales: 13300 }
]);

// 热门商品（模拟）
const popularProducts = ref([
  { id: 1, name: '精品茶叶礼盒', sales: 156 },
  { id: 2, name: '手工陶瓷茶具', sales: 98 },
  { id: 3, name: '有机绿茶', sales: 87 },
  { id: 4, name: '紫砂茶壶', sales: 65 }
]);

// 表单数据
const passwordForm = ref({  
  oldPassword: '',  
  newPassword: '',  
  confirmPassword: '',  
});  

const editedUserInfo = ref({
  id: userInfo.value.id,
  avatar: userInfo.value.avatar,
  username: userInfo.value.username,
  phone: userInfo.value.phone,
  password: userInfo.value.password
});

// 方法
const updateAvatar = (response) => {
  editedUserInfo.value.avatar = response.data;
  ElMessage.success("上传头像成功~");
}

const getMerchant = async () => {
  const res = await merchantGetByIdService(userInfo.value.id);
  merchantStore.setMerchant(res.data.data);
  userInfo.value = merchantStore.merchant;
}

const saveUserInfo = async () => {  
  await merchantUpdateService(editedUserInfo.value);
  await getMerchant();
  ElMessage.success("修改个人信息成功~")
  showEditDialog.value = false;  
};  

const changePassword = async () => {  
  const res = await merchantUpdatePasswordService(passwordForm.value);
  if(res.data.code === 0){
    ElMessage.success("修改密码成功~")
    showChangePasswordDialog.value = false; 
  } else {
    ElMessage.error(res.data.data)
  }
};  

// 初始化加载数据
onMounted(() => {
  // 这里可以添加获取统计数据的API调用
});
</script>

<template>  
  <div class="merchant-dashboard">  
    <!-- 顶部信息卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="24">
        <el-card class="info-card">
          <div class="merchant-profile">
            <div class="avatar-wrapper">
              <el-avatar :size="100" :src="userInfo.avatar" />
              <div class="upload-tip">点击头像可更换</div>
            </div>
            <div class="merchant-info">
              <h2>{{ userInfo.username }}</h2>
              <div class="contact-info">
                <span><i class="el-icon-phone"></i> {{ userInfo.phone }}</span>
              </div>
              <div class="actions">
                <el-button type="primary" @click="showEditDialog = true" size="small">
                  编辑信息
                </el-button>
                <el-button @click="showChangePasswordDialog = true" size="small">
                  修改密码
                </el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计信息卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :xs="24" :sm="12" :md="6" v-for="(stat, index) in [
        { title: '今日订单', value: stats.todayOrders, icon: 'el-icon-s-order', color: '#409EFF' },
        { title: '销售额', value: `¥${stats.monthlySales}`, icon: 'el-icon-money', color: '#67C23A' },
        { title: '商品数量', value: stats.productCount, icon: 'el-icon-goods', color: '#E6A23C' },
        { title: '顾客评分', value: stats.customerRating, icon: 'el-icon-star-on', color: '#F56C6C' }
      ]" :key="index">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <i :class="stat.icon"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-title">{{ stat.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

   

    <!-- 编辑信息对话框 -->
    <el-dialog v-model="showEditDialog" title="编辑店铺信息" width="600px">  
      <el-form :model="editedUserInfo" label-width="100px">  
        <el-form-item label="店铺名称">  
          <el-input v-model="editedUserInfo.username" />  
        </el-form-item>  
        <el-form-item label="联系电话">  
          <el-input v-model="editedUserInfo.phone" />  
        </el-form-item>  
        <el-form-item label="店铺头像">  
          <el-upload
            action="/api/upload"
            :on-success="updateAvatar"
            :show-file-list="false"
          >
            <img v-if="editedUserInfo.avatar" :src="editedUserInfo.avatar" class="uploaded-avatar" />
            <div v-else class="avatar-placeholder">点击上传头像</div>
          </el-upload>
        </el-form-item>  
      </el-form>  
      <template #footer>  
        <el-button @click="showEditDialog = false">取消</el-button>  
        <el-button type="primary" @click="saveUserInfo">保存</el-button>  
      </template>  
    </el-dialog>  


<!-- 编辑信息对话框 -->
<el-dialog v-model="showEditDialog" title="编辑店铺信息" width="600px">  
  <el-form :model="editedUserInfo" label-width="100px">  
    <el-form-item label="联系电话">  
      <el-input v-model="editedUserInfo.phone" />  
    </el-form-item>  
    <el-form-item label="店铺头像">  
      <el-upload
        action="/api/upload"
        :on-success="updateAvatar"
        :show-file-list="false"
      >
        <img v-if="editedUserInfo.avatar" :src="editedUserInfo.avatar" class="uploaded-avatar" />
        <div v-else class="avatar-placeholder">点击上传头像</div>
      </el-upload>
    </el-form-item>  
  </el-form>  
  <template #footer>  
    <el-button @click="showEditDialog = false">取消</el-button>  
    <el-button type="primary" @click="saveUserInfo">保存</el-button>  
  </template>  
</el-dialog>  

<!-- 修改密码对话框 -->
<el-dialog v-model="showChangePasswordDialog" title="修改密码" width="500px">  
  <el-form :model="passwordForm" label-width="100px">  
    <el-form-item label="原密码">  
      <el-input v-model="passwordForm.oldPassword" type="password" show-password />  
    </el-form-item>  
    <el-form-item label="新密码">  
      <el-input v-model="passwordForm.newPassword" type="password" show-password />  
    </el-form-item>  
    <el-form-item label="确认密码">  
      <el-input v-model="passwordForm.confirmPassword" type="password" show-password />  
    </el-form-item>  
  </el-form>  
  <template #footer>  
    <el-button @click="showChangePasswordDialog = false">取消</el-button>  
    <el-button type="primary" @click="changePassword">保存</el-button>  
  </template>  
</el-dialog>  
</div>
</template>  

<style scoped lang="scss">
.merchant-dashboard {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.mb-20 {
  margin-bottom: 20px;
}

.info-card {
  .merchant-profile {
    display: flex;
    align-items: center;
    padding: 20px;
    
    .avatar-wrapper {
      position: relative;
      margin-right: 30px;
      cursor: pointer;
      
      .upload-tip {
        font-size: 12px;
        color: #999;
        text-align: center;
        margin-top: 5px;
      }
    }
    
    .merchant-info {
      flex: 1;
      
      h2 {
        margin: 0 0 10px 0;
        font-size: 24px;
        color: #333;
      }
      
      .contact-info {
        margin-bottom: 15px;
        
        span {
          display: inline-block;
          margin-right: 20px;
          color: #666;
          
          i {
            margin-right: 5px;
          }
        }
      }
    }
  }
}

.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    
    .stat-icon {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 15px;
      color: white;
      font-size: 20px;
    }
    
    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        margin-bottom: 5px;
      }
      
      .stat-title {
        font-size: 14px;
        color: #999;
      }
    }
  }
}

.chart-container {
  height: 300px;
  padding: 20px;
  
  .mock-chart {
    display: flex;
    align-items: flex-end;
    height: 100%;
    padding-top: 30px;
    
    .chart-bar {
      flex: 1;
      margin: 0 5px;
      background-color: #409EFF;
      position: relative;
      border-radius: 4px 4px 0 0;
      transition: height 0.5s;
      
      .chart-label {
        position: absolute;
        bottom: -25px;
        left: 0;
        right: 0;
        text-align: center;
        font-size: 12px;
      }
    }
  }
}

.uploaded-avatar {
  width: 150px;
  height: 150px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
  border: 1px dashed #dcdfe6;
}

.avatar-placeholder {
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background-color: #f5f5f5;
  color: #606266;
  font-size: 14px;
  cursor: pointer;
  border: 1px dashed #dcdfe6;
  transition: background-color 0.3s;
  
  &:hover {
    background-color: #e6f7ff;
  }
}

@media (max-width: 768px) {
  .merchant-profile {
    flex-direction: column;
    text-align: center;
    
    .avatar-wrapper {
      margin-right: 0;
      margin-bottom: 20px;
    }
    
    .actions {
      justify-content: center;
    }
  }
  
  .stat-content {
    justify-content: center;
    text-align: center;
  }
}
</style>