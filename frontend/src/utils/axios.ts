import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

// 创建Axios实例
const instance = axios.create({
  baseURL: '/api',  // API基础路径，Vite代理会转发到后端
  timeout: 10000    // 请求超时时间10秒
})

/**
 * 请求拦截器
 * 在请求发送前添加JWT令牌到Header
 */
instance.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    const token = authStore.getToken()
    if (token) {
      // 将JWT令牌添加到Authorization Header
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

/**
 * 响应拦截器
 * 处理401未授权错误，自动退出登录
 */
instance.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    // 如果是401错误（未授权），清除登录状态并跳转到登录页
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      authStore.logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default instance