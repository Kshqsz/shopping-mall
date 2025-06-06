import request from '@/utils/request.js'

export const favoriteAddService = (productId) => {
    return request.post(`/favorite/${productId}`);
}

export const favoriteDeleteService = (productIds) => {
    return request.delete(`/favorite?ids=${productIds}`);
}
export const getFavoriteGoods = () => {
    return request.get('/favorite')
}
export const checkFavoriteStatus = (id) =>{
    return request.post(`/favorite/status?id=${id}`)
}