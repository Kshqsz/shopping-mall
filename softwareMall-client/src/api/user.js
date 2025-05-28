import request from '@/utils/request.js'

// login api
export const userLoginService = ({ username, password }) => {
    return request.post("/user/login", { username, password });
}

// register api
export const userRegisterService = ({ username, password, rePassword }) => {
    return request.post("/user/register", { username, password, rePassword})
}

export const userGetFavouriteService = (userId) => {
    return request.get(`/user/myFavourite/${userId}`)
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