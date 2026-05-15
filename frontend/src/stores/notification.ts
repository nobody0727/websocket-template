import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'
import type { Notification, BroadcastRequest } from '@/types'

export const useNotificationStore = defineStore('notification', () => {
  const notifications = ref<Notification[]>([])
  const unreadCount = ref(0)
  
  const unreadNotifications = computed(() => 
    notifications.value.filter(n => !n.isRead).length
  )
  
  const getNotifications = async (page = 0, size = 10) => {
    const response = await axios.get('/api/notifications', {
      params: { page, size }
    })
    notifications.value = response.data.content
    return response.data
  }
  
  const getUnreadCount = async () => {
    const response = await axios.get('/api/notifications/count/unread')
    unreadCount.value = response.data
    return response.data
  }
  
  const markAsRead = async (id: number) => {
    await axios.put(`/api/notifications/${id}/read`)
    const index = notifications.value.findIndex(n => n.id === id)
    if (index !== -1) {
      notifications.value[index].isRead = true
    }
  }
  
  const broadcast = async (request: BroadcastRequest) => {
    await axios.post('/api/notifications/broadcast', request)
  }
  
  const addNotification = (notification: Notification) => {
    notifications.value.unshift(notification)
    unreadCount.value++
  }
  
  return {
    notifications,
    unreadCount,
    unreadNotifications,
    getNotifications,
    getUnreadCount,
    markAsRead,
    broadcast,
    addNotification
  }
})