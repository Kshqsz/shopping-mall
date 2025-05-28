import request from '@/utils/request.js'

//查询所有用户
export const admingetalluser = () =>{
  return request.get("/user", {})
}

//修改用户状态
export const adminChangeStatus = ({id,status}) =>{
  return request.put("/user", {id,status})
}