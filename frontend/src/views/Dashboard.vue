<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import axios from '@/utils/axios'
import { ListTodo, CheckCircle2, Bell, TrendingUp, Plus, ArrowRight } from 'lucide-vue-next'
import type { Task, Notification } from '@/types'

const router = useRouter()
const notificationStore = useNotificationStore()

const todoCount = ref(0)
const doneCount = ref(0)
const notificationCount = ref(0)
const recentTasks = ref<Task[]>([])
const recentNotifications = ref<Notification[]>([])

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
    console.error('Failed to fetch stats:', error)
  }
}

const fetchRecentTasks = async () => {
  try {
    const response = await axios.get('/api/tasks', { params: { page: 0, size: 5 } })
    recentTasks.value = response.data.content
  } catch (error) {
    console.error('Failed to fetch tasks:', error)
  }
}

const fetchRecentNotifications = async () => {
  try {
    const response = await axios.get('/api/notifications', { params: { page: 0, size: 5 } })
    recentNotifications.value = response.data.content
  } catch (error) {
    console.error('Failed to fetch notifications:', error)
  }
}

const goToTasks = () => {
  router.push('/tasks')
}

const goToNotifications = () => {
  router.push('/notifications')
}

const getPriorityClass = (priority: string) => {
  switch (priority) {
    case 'HIGH': return 'bg-red-100 text-red-600'
    case 'MEDIUM': return 'bg-yellow-100 text-yellow-600'
    case 'LOW': return 'bg-green-100 text-green-600'
    default: return 'bg-gray-100 text-gray-600'
  }
}

const getPriorityText = (priority: string) => {
  switch (priority) {
    case 'HIGH': return '高'
    case 'MEDIUM': return '中'
    case 'LOW': return '低'
    default: return priority
  }
}

const getNotificationTypeClass = (type: string) => {
  switch (type) {
    case 'SUCCESS': return 'bg-green-100 text-green-600'
    case 'WARNING': return 'bg-yellow-100 text-yellow-600'
    case 'ERROR': return 'bg-red-100 text-red-600'
    default: return 'bg-blue-100 text-blue-600'
  }
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
}

onMounted(() => {
  fetchStats()
  fetchRecentTasks()
  fetchRecentNotifications()
})
</script>

<template>
  <div class="space-y-6">
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
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
        <div class="mt-4">
          <div class="w-full bg-gray-200 rounded-full h-2">
            <div 
              class="bg-green-500 h-2 rounded-full transition-all"
              :style="{ width: `${doneCount + todoCount > 0 ? (doneCount / (doneCount + todoCount)) * 100 : 0}%` }"
            ></div>
          </div>
          <p class="text-xs text-gray-500 mt-2">完成率: {{ doneCount + todoCount > 0 ? Math.round((doneCount / (doneCount + todoCount)) * 100) : 0 }}%</p>
        </div>
      </div>
      
      <div class="bg-white rounded-xl p-6 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
        <div class="flex items-center justify-between">
          <div>
            <p class="text-gray-500 text-sm mb-1">未读通知</p>
            <p class="text-3xl font-bold text-gray-800">{{ notificationCount }}</p>
          </div>
          <div class="w-12 h-12 bg-purple-100 rounded-xl flex items-center justify-center relative">
            <Bell class="w-6 h-6 text-purple-600" />
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
    
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
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
          <div v-if="recentTasks.length === 0" class="text-center py-8 text-gray-500">
            暂无任务
          </div>
        </div>
      </div>
      
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
          <div v-if="recentNotifications.length === 0" class="text-center py-8 text-gray-500">
            暂无通知
          </div>
        </div>
      </div>
    </div>
  </div>
</template>