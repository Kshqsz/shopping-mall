import axios from 'axios'
import { useMerchantStore } from '@/stores'
import { ElMessage } from 'element-plus'
import router from '@/router'
const baseURL = '/api'

const instance = axios.create({
    // TODO 1. 基础地址，超时时间
    baseURL,
    timeout: 10000
})

// 请求拦截器
instance.interceptors.request.use(
    (config) => {
        // TODO 2. 携带token
        const merchantStore = useMerchantStore()
        if (merchantStore.token) {
            config.headers.Authorization = merchantStore.token
        }
        return config
    },
    (err) => Promise.reject(err)
)

// 响应拦截器
instance.interceptors.response.use(
    (res) => {
        // TODO 3. 处理业务失败
        // TODO 4. 摘取核心响应数据
        if (res.data.code === 0) {
            return res
        }
        // 处理业务失败
        ElMessage.error(res.data.message || '服务错误')
        return Promise.reject(res.data)
    },
    (err) => {
        // TODO 5. 处理401错误
        if (err.response?.status === 401) {
            router.push("/login")
        }

        // 错误默认提示
        ElMessage.error(err.response.data.message || '服务错误')
        return Promise.reject(err)
    }
)

export default instance
export { baseURL }