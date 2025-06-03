import request from '@/utils/request.js'

export const uploadFile = (file) => {
  const formData = new FormData();
  
  // 关键修改：直接追加文件本身（不是fileObj.file）
  formData.append('file', file);
  
  return request.post('/upload', formData, {
    headers: {
      // 必须设置这个头部
      'Content-Type': 'multipart/form-data'
    }
  });
};