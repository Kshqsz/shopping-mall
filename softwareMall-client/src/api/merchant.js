import request from '@/utils/request.js'
export const merchantGeyByProductIdService = (id) => {
    return request.get(`/merchant/getByProductId/${id}`);
}

export const merchantGetByIdService = (id) => {
    return request.get(`/merchant/${id}`);
}