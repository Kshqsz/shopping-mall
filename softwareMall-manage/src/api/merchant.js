import request from '@/utils/request.js'

//查询所有商家

export const getAllMerchant = () =>{
  return request.get("/merchant")
}


//修改商家状态
export const ChangeMerchantStatus = ({id,status}) =>{
  return request.put("/merchant/status",{id,status})
}