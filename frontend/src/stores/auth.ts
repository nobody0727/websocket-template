import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'

export interface User {
  id: number
  username: string
  email: string
  role: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const user = ref<User | null>(null)
  
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  
  const login = async (username: string, password: string) => {
    const response = await axios.post('/api/auth/login', { username, password })
    token.value = response.data.token
    user.value = response.data.user
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }
  
  const logout = () => {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }
  
  const loadFromStorage = () => {
    const savedToken = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }
  
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