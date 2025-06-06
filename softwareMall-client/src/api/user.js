import request from '@/utils/request.js'

// login api
export const userLoginService = ({ username, password }) => {
    return request.post("/user/login", { username, password });
}

// register api
export const userRegisterService = ({ username, password, rePassword }) => {
    return request.post("/user/register", { username, password, rePassword})
}

export const userGetFavoriteService = (userId) => {
    return request.get(`/user/myFavorite/${userId}`)
}

export const userCountFavouriteService = (userId) => {
    return request.get(`/user/countFavourite/${userId}`)
}

export const userUpdatePasswordService = ({password, rePassword}) => {
    return request.post("/user/updatePassword", {password, rePassword})
}

export const userUpdateService = ({id, avatar, username, phone}) => {
    return request.put("/user/update", {id, avatar, username, phone})
}

export const userGetByIdService= (id) => {
    return request.get(`/user/${id}`)
}

export const userCountOrderService = ({userId, status}) => {
    return request.post("/user/countOrder", {userId, status})
}

// 新增地址
export const addAddress = (address) =>{
    return request.post("/user/addAdderss",address)
}

//查找所有地址
export const addressList = () => {
    return request.get("/user/addressList")
}

//设置默认地址
export const toDefault = (id) => {
    return request.post(`/user/address?id=${id}`)
}

// 删除地址
export const deleteAddress = (id) => {
    return request.delete(`/user/address?id=${id}`)
}

// 更新地址
export const updateAddress = (address) =>{
    return request.post("/user/updateAddress",address)
}