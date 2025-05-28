import request from '@/utils/request.js'

export const categoryAddService = ({name}) => {
    return request.post("/category/add", {name});
}

export const categoryGetAllService = () => {
    return request.get("/category/all");
}