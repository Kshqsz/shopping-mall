import request from '@/utils/request.js'

// login api
export const merchantLoginService = ({ username, password }) => {
    return request.post("/merchant/login", { username, password });
}

//注册账号 /merchant/regeits  post
export const merchantRegisterService = ({ username, password, rePassword }) => {
    return request.post("/merchant/register", { username, password, rePassword });
}
export const  merchantUpdatePasswordService = (passwordDto) => {
    return request.post("/merchant/updatePassword", passwordDto)
}
export const merchantUpdateService = ({id, avatar, username, phone}) => {
    return request.put("/merchant/update", {id, avatar, username, phone})
}
export const merchantGetByIdService = (id) => {
    return request.get(`/merchant/${id}`)
}
//查询拥有的所有软件 /mrechant get


//编辑商品信息  /merchant put

//下架商品   /merchant post 修改状态即可

//添加新商品  

//条件查询商品

//根据id查询商品


//查询所有订单信息

//根据订单查询