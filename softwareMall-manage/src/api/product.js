import request from '@/utils/request.js'


// 查询所有商品

export const getAllProduct = () =>{
  return request.get("/product/all")
}

//处理审核，同意或拒绝  下架商品
export const updateStatus = ({id,status}) => {
  return request.put("/product/status",{id,status})
}