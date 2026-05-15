<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { ElTable, ElTableColumn, ElPagination } from 'element-plus'
import { CheckCircle, Info, AlertTriangle, XCircle } from 'lucide-vue-next'
import type { Notification } from '@/types'

const notificationStore = useNotificationStore()

// 通知列表和分页
const notifications = ref<Notification[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

/**
 * 获取通知列表
 */
const fetchNotifications = async () => {
  try {
    const response = await notificationStore.getNotifications(page.value - 1, size.value)
    notifications.value = response.content
    total.value = response.totalElements
  } catch (error) {
    console.error('获取通知列表失败:', error)
  }
}

/**
 * 标记通知为已读
 */
const markAsRead = async (notification: Notification) => {
  if (notification.isRead) return
  try {
    await notificationStore.markAsRead(notification.id)
    notification.isRead = true
  } catch (error) {
    console.error('标记已读失败:', error)
  }
}

/**
 * 获取通知类型图标
 */
const getTypeIcon = (type: string) => {
  switch (type) {
    case 'SUCCESS': return CheckCircle
    case 'WARNING': return AlertTriangle
    case 'ERROR': return XCircle
    default: return Info
  }
}

/**
 * 获取通知类型样式
 */
const getTypeClass = (type: string) => {
  switch (type) {
    case 'SUCCESS': return 'text-green-600 bg-green-100'
    case 'WARNING': return 'text-yellow-600 bg-yellow-100'
    case 'ERROR': return 'text-red-600 bg-red-100'
    default: return 'text-blue-600 bg-blue-100'
  }
}

/**
 * 获取通知类型文本
 */
const getTypeText = (type: string) => {
  switch (type) {
    case 'SUCCESS': return '成功'
    case 'WARNING': return '警告'
    case 'ERROR': return '错误'
    default: return '信息'
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

onMounted(() => {
  fetchNotifications()
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100">
    <!-- 页面头部 -->
    <div class="p-6 border-b border-gray-100">
      <div class="flex items-center justify-between">
        <h2 class="text-xl font-semibold text-gray-800">通知中心</h2>
        <div class="flex items-center gap-2">
          <span class="text-sm text-gray-500">未读: {{ notificationStore.unreadNotifications }}</span>
        </div>
      </div>
    </div>
    
    <!-- 通知列表 -->
    <div class="p-6">
      <div class="space-y-4">
        <div 
          v-for="notification in notifications" 
          :key="notification.id"
          class="p-4 rounded-lg border transition-all cursor-pointer"
          :class="[
            notification.isRead 
              ? 'bg-gray-50 border-gray-200 hover:bg-gray-100' 
              : 'bg-blue-50 border-blue-200 hover:bg-blue-100'
          ]"
          @click="markAsRead(notification)"
        >
          <div class="flex items-start gap-4">
            <!-- 通知类型图标 -->
            <div :class="['w-10 h-10 rounded-full flex items-center justify-center flex-shrink-0', getTypeClass(notification.type)]">
              <component :is="getTypeIcon(notification.type)" class="w-5 h-5" />
            </div>
            <!-- 通知内容 -->
            <div class="flex-1 min-w-0">
              <div class="flex items-center gap-2">
                <span :class="['px-2 py-0.5 rounded text-xs font-medium', getTypeClass(notification.type)]">
                  {{ getTypeText(notification.type) }}
                </span>
                <h3 class="font-medium text-gray-800">{{ notification.title }}</h3>
              </div>
              <p class="text-sm text-gray-600 mt-2">{{ notification.content }}</p>
              <div class="flex items-center gap-2 mt-3">
                <span class="text-xs text-gray-400">{{ formatDate(notification.createdAt) }}</span>
                <!-- 未读标记 -->
                <span 
                  v-if="!notification.isRead"
                  class="px-2 py-0.5 bg-primary/10 text-primary text-xs rounded-full"
                >
                  未读
                </span>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="notifications.length === 0" class="text-center py-12 text-gray-500">
          <Info class="w-12 h-12 mx-auto mb-4 text-gray-300" />
          <p>暂无通知</p>
        </div>
      </div>
      
      <!-- 分页组件 -->
      <div v-if="total > 0" class="flex justify-center mt-6">
        <ElPagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchNotifications"
          @current-change="fetchNotifications"
        />
      </div>
    </div>
  </div>
</template>