import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'

/**
 * 认证状态管理Store
 * 管理用户登录状态和JWT令牌
 */
export const useAuthStore = defineStore('auth', () => {
  // JWT令牌
  const token = ref<string>('')
  // 用户信息
  const user = ref<User | null>(null)
  
  // 是否已登录
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  
  /**
   * 用户登录
   * @param username 用户名
   * @param password 密码
   */
  const login = async (username: string, password: string) => {
    const response = await axios.post('/api/auth/login', { username, password })
    token.value = response.data.token
    user.value = response.data.user
    // 存储到本地，用于页面刷新后恢复登录状态
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  /**
   * 退出登录
   */
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  /**
   * 从本地存储加载登录状态
   */
  const loadFromStorage = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }
  
  /**
   * 获取JWT令牌
   */
  const getToken = () => token.value
  
  return {
    token,
    user,
    isLoggedIn,
    login,
    logout,
    loadFromStorage,
    getToken
  }
})

// 用户类型定义（用于Store内部）
interface User {
  id: number
  username: string
  email: string
  role: string
}