import request from '@/utils/request.js'

// login api
export const adminLoginService = ({ username, password }) => {
    return request.post("/admin/login", { username, password });
}




//查询商家拥有商品


//查询所有订单

//根据订单编号查询订单