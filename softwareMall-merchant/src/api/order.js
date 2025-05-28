import request from '@/utils/request.js'

//查询所有订单信息
export const getAllOrder = () =>{
  return request.get("/order/all")
}

export const getOrderWithPrice = () => {
  return request.get("/order")
}   