<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { connectWebSocket } from '@/utils/websocket'
import type { Notification } from '@/types'

const authStore = useAuthStore()
const notificationStore = useNotificationStore()

/**
 * 应用挂载时初始化
 */
onMounted(() => {
  // 从本地存储加载登录状态
  authStore.loadFromStorage()
  
  // 如果已登录，建立WebSocket连接
  if (authStore.isLoggedIn) {
    connectWebSocket(
      (message) => {
        // 收到全局通知
        notificationStore.addNotification(message as Notification)
      },
      (message) => {
        // 收到私有通知
        notificationStore.addNotification(message as Notification)
      }
    )
  }
})
</script>

<template>
  <router-view />
</template>