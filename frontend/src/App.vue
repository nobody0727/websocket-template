<script setup lang="ts">
import { onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { connectWebSocket } from '@/utils/websocket'
import type { Notification } from '@/types'

const authStore = useAuthStore()
const notificationStore = useNotificationStore()

onMounted(() => {
  authStore.loadFromStorage()
  
  if (authStore.isLoggedIn) {
    connectWebSocket(
      (message) => {
        notificationStore.addNotification(message as Notification)
      },
      (message) => {
        notificationStore.addNotification(message as Notification)
      }
    )
  }
})
</script>

<template>
  <router-view />
</template>