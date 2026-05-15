import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from '@/utils/axios'
import type { Notification, BroadcastRequest } from '@/types'

/**
 * 通知状态管理Store
 * 管理通知列表和未读数量
 */
export const useNotificationStore = defineStore('notification', () => {
  // 通知列表
  const notifications = ref<Notification[]>([])
  // 未读数量
  const unreadCount = ref(0)
  
  // 计算未读通知数量
  const unreadNotifications = computed(() => 
    notifications.value.filter(n => !n.isRead).length
  )
  
  /**
   * 获取通知列表
   */
  const getNotifications = async (page = 0, size = 10) => {
    const response = await axios.get('/api/notifications', {
      params: { page, size }
    })
    notifications.value = response.data.content
    return response.data
  }
  
  /**
   * 获取未读通知数量
   */
  const getUnreadCount = async () => {
    const response = await axios.get('/api/notifications/count/unread')
    unreadCount.value = response.data
    return response.data
  }
  
  /**
   * 标记通知为已读
   */
  const markAsRead = async (id: number) => {
    await axios.put(`/api/notifications/${id}/read`)
    const index = notifications.value.findIndex(n => n.id === id)
    if (index !== -1) {
      notifications.value[index].isRead = true
    }
  }
  
  /**
   * 广播通知
   */
  const broadcast = async (request: BroadcastRequest) => {
    await axios.post('/api/notifications/broadcast', request)
  }
  
  /**
   * 添加新通知（WebSocket推送时调用）
   */
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