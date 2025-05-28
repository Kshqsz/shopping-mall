import request from '@/utils/request.js'

export const favouriteAddService = (productId) => {
    return request.post(`/favourite/${productId}`);
}

export const favouriteDeleteService = (productId) => {
    return request.delete(`/favourite/${productId}`);
}

