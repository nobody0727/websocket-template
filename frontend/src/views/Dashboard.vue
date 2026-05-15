<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import axios from '@/utils/axios'
import { ListTodo, CheckCircle2, Bell, TrendingUp, Plus, ArrowRight } from 'lucide-vue-next'
import type { Task, Notification } from '@/types'

const router = useRouter()
const notificationStore = useNotificationStore()

// 统计数据
const todoCount = ref(0)
const doneCount = ref(0)
const notificationCount = ref(0)

// 近期数据
const recentTasks = ref<Task[]>([])
const recentNotifications = ref<Notification[]>([])

/**
 * 获取统计数据（待办数量、已完成数量、未读通知数量）
 */
const fetchStats = async () => {
  try {
    const [todoRes, doneRes, notifRes] = await Promise.all([
      axios.get('/api/tasks/count', { params: { status: 'TODO' } }),
      axios.get('/api/tasks/count', { params: { status: 'DONE' } }),
      axios.get('/api/notifications/count/unread')
    ])
    todoCount.value = todoRes.data
    doneCount.value = doneRes.data
    notificationCount.value = notifRes.data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

/**
 * 获取近期任务
 */
const fetchRecentTasks = async () => {
  try {
    const response = await axios.get('/api/tasks', { params: { page: 0, size: 5 } })
    recentTasks.value = response.data.content
  } catch (error) {
    console.error('获取近期任务失败:', error)
  }
}

/**
 * 获取近期通知
 */
const fetchRecentNotifications = async () => {
  try {
    const response = await axios.get('/api/notifications', { params: { page: 0, size: 5 } })
    recentNotifications.value = response.data.content
  } catch (error) {
    console.error('获取近期通知失败:', error)
  }
}

/**
 * 跳转到任务管理页面
 */
const goToTasks = () => {
  router.push('/tasks')
}

/**
 * 跳转到通知中心
 */
const goToNotifications = () => {
  router.push('/notifications')
}

/**
 * 获取优先级样式
 */
const getPriorityClass = (priority: string) => {
  switch (priority) {
    case 'HIGH': return 'bg-red-100 text-red-600'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-600'
    case 'LOW': return 'bg-green-100 text-green-600'
    default: return 'bg-gray-100 text-gray-600'
  }
}

/**
 * 获取优先级文本
 */
const getPriorityText = (priority: string) => {
  switch (priority) {
    case 'HIGH': return '高'
    case 'MEDIUM': return '中'
    case 'LOW': return '低'
    default: return priority
  }
}

/**
 * 获取通知类型样式
 */
const getNotificationTypeClass = (type: string) => {
  switch (type) {
    case 'SUCCESS': return 'bg-green-100 text-green-600'
    case 'WARNING': return 'bg-yellow-100 text-yellow-600'
    case 'ERROR': return 'bg-red-100 text-red-600'
    default: return 'bg-blue-100 text-blue-600'
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

/**
 * 计算任务完成率
 */
const completionRate = () => {
  const total = todoCount.value + doneCount.value
  return total > 0 ? Math.round((doneCount.value / total) * 100) : 0
}

// 页面加载时获取数据
onMounted(() => {
  fetchStats()
  fetchRecentTasks()
  fetchRecentNotifications()
})
</script>

<template>
  <div class="space-y-6">
    <!-- 统计卡片区域 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
      <!-- 待办任务卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">待办任务</p>
            <p class="text-3xl font-bold text-gray-800">{{ todoCount }}</p>
          </div>
          <div class="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
            <ListTodo class="w-6 h-6 text-blue-600" />
          </div>
        </div>
        <button @click="goToTasks" class="mt-4 text-sm text-primary hover:text-blue-700 flex items-center">
          查看全部 <ArrowRight class="w-4 h-4 ml-1" />
        </button>
      </div>
      
      <!-- 已完成卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">已完成</p>
            <p class="text-3xl font-bold text-gray-800">{{ doneCount }}</p>
          </div>
          <div class="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
            <CheckCircle2 class="w-6 h-6 text-green-600" />
          </div>
        </div>
        <!-- 完成率进度条 -->
        <div class="mt-4">
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div 
              class="bg-green-500 h-2 rounded-full transition-all"
              :style="{ width: completionRate() + '%' }"
            ></div>
          </div>
          <p class="text-xs text-gray-500 mt-2">完成率: {{ completionRate() }}%</p>
        </div>
      </div>
      
      <!-- 未读通知卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">未读通知</p>
            <p class="text-3xl font-bold text-gray-800">{{ notificationCount }}</p>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center relative">
            <Bell class="w-6 h-6 text-purple-600" />
            <!-- 未读数量徽章 -->
            <span 
              v-if="notificationCount > 0"
              class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center"
            >
              {{ notificationCount }}
            </span>
          </div>
        </div>
        <button @click="goToNotifications" class="mt-4 text-sm text-primary hover:text-blue-700 flex items-center">
          查看全部 <ArrowRight class="w-4 h-4 ml-1" />
        </button>
      </div>
      
      <!-- 总任务数卡片 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">总任务数</p>
            <p class="text-3xl font-bold text-gray-800">{{ todoCount + doneCount }}</p>
          </div>
          <div class="w-12 h-12 bg-orange-100 rounded-xl flex items-center justify-center">
            <TrendingUp class="w-6 h-6 text-orange-600" />
          </div>
        </div>
        <button 
          @click="goToTasks"
          class="mt-4 text-sm bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-600 transition-colors flex items-center"
        >
          <Plus class="w-4 h-4 mr-1" /> 新建任务
        </button>
      </div>
    </div>
    
    <!-- 近期数据区域 -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
      <!-- 近期任务列表 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-800">最近任务</h2>
          <button @click="goToTasks" class="text-sm text-primary hover:text-blue-700">查看全部</button>
        </div>
        <div class="space-y-3">
          <div 
            v-for="task in recentTasks" 
            :key="task.id"
            class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
            @click="goToTasks"
          >
            <div class="flex items-center justify-between">
              <div class="flex-1">
                <p class="font-medium text-gray-800">{{ task.title }}</p>
                <p class="text-sm text-gray-500 mt-1">{{ formatDate(task.createdAt) }}</p>
              </div>
              <span :class="['px-2 py-1 rounded-full text-xs font-medium', getPriorityClass(task.priority)]">
                {{ getPriorityText(task.priority) }}
              </span>
            </div>
          </div>
          <!-- 空状态 -->
          <div v-if="recentTasks.length === 0" class="text-center py-8 text-gray-500">
            暂无任务
          </div>
        </div>
      </div>
      
      <!-- 近期通知列表 -->
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
        <div class="flex items-center justify-between mb-4">
          <h2 class="text-lg font-semibold text-gray-800">最近通知</h2>
          <button @click="goToNotifications" class="text-sm text-primary hover:text-blue-700">查看全部</button>
        </div>
        <div class="space-y-3">
          <div 
            v-for="notification in recentNotifications" 
            :key="notification.id"
            class="p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors cursor-pointer"
            :class="{ 'opacity-60': notification.isRead }"
            @click="goToNotifications"
          >
            <div class="flex items-start justify-between">
              <div class="flex-1">
                <div class="flex items-center gap-2">
                  <span :class="['px-2 py-0.5 rounded text-xs font-medium', getNotificationTypeClass(notification.type)]">
                    {{ notification.type === 'SUCCESS' ? '成功' : notification.type === 'WARNING' ? '警告' : notification.type === 'ERROR' ? '错误' : '信息' }}
                  </span>
                  <p class="font-medium text-gray-800">{{ notification.title }}</p>
                </div>
                <p class="text-sm text-gray-500 mt-1 line-clamp-2">{{ notification.content }}</p>
              </div>
            </div>
            <p class="text-xs text-gray-400 mt-2">{{ formatDate(notification.createdAt) }}</p>
          </div>
          <!-- 空状态 -->
          <div v-if="recentNotifications.length === 0" class="text-center py-8 text-gray-500">
            暂无通知
          </div>
        </div>
      </div>
    </div>
  </div>
</template>