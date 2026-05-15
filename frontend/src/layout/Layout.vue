<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useNotificationStore } from '@/stores/notification'
import { 
  LayoutDashboard, 
  ListTodo, 
  Bell, 
  MessageSquare, 
  Users, 
  LogOut,
  ChevronLeft,
  ChevronRight,
  Settings
} from 'lucide-vue-next'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const notificationStore = useNotificationStore()

const collapsed = ref(false)

const menuItems = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard },
  { name: '任务管理', path: '/tasks', icon: ListTodo },
  { name: '通知中心', path: '/notifications', icon: Bell },
  { name: '消息推送', path: '/messages', icon: MessageSquare }
]

const adminMenuItems = [
  { name: '用户管理', path: '/users', icon: Users }
]

const allMenuItems = computed(() => {
  if (authStore.user?.role === 'ADMIN') {
    return [...menuItems, ...adminMenuItems]
  }
  return menuItems
})

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

const isActive = (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>

<template>
  <div class="flex h-screen bg-gray-50">
    <aside 
      :class="[
        'bg-white border-r border-gray-200 transition-all duration-300 flex flex-col',
        collapsed ? 'w-16' : 'w-64'
      ]"
    >
      <div class="p-4 border-b border-gray-200 flex items-center justify-between">
        <div v-if="!collapsed" class="text-xl font-bold text-primary">管理系统</div>
        <div v-else class="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
          <Settings class="w-5 h-5 text-white" />
        </div>
        <button 
          @click="toggleSidebar"
          class="p-1 hover:bg-gray-100 rounded transition-colors"
        >
          <ChevronLeft v-if="!collapsed" class="w-5 h-5 text-gray-500" />
          <ChevronRight v-else class="w-5 h-5 text-gray-500" />
        </button>
      </div>
      
      <nav class="flex-1 py-4">
        <ul class="space-y-1 px-3">
          <li v-for="item in allMenuItems" :key="item.path">
            <router-link
              :to="item.path"
              :class="[
                'flex items-center px-3 py-2.5 rounded-lg transition-all duration-200',
                isActive(item.path) 
                  ? 'bg-primary/10 text-primary' 
                  : 'text-gray-600 hover:bg-gray-100'
              ]"
            >
              <component :is="item.icon" class="w-5 h-5 flex-shrink-0" />
              <span v-if="!collapsed" class="ml-3 text-sm font-medium">{{ item.name }}</span>
            </router-link>
          </li>
        </ul>
      </nav>
      
      <div class="p-3 border-t border-gray-200">
        <button 
          @click="handleLogout"
          class="flex items-center w-full px-3 py-2.5 rounded-lg text-gray-600 hover:bg-gray-100 transition-colors"
        >
          <LogOut class="w-5 h-5 flex-shrink-0" />
          <span v-if="!collapsed" class="ml-3 text-sm font-medium">退出登录</span>
        </button>
      </div>
    </aside>
    
    <div class="flex-1 flex flex-col overflow-hidden">
      <header class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
        <h1 class="text-lg font-semibold text-gray-800">{{ 
          allMenuItems.find(item => isActive(item.path))?.name || 'Dashboard' 
        }}</h1>
        <div class="flex items-center space-x-4">
          <router-link to="/notifications" class="relative">
            <Bell class="w-6 h-6 text-gray-500 hover:text-primary transition-colors cursor-pointer" />
            <span 
              v-if="notificationStore.unreadNotifications > 0"
              class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center"
            >
              {{ notificationStore.unreadNotifications }}
            </span>
          </router-link>
          <div class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center">
              <span class="text-primary font-medium">{{ authStore.user?.username?.charAt(0) }}</span>
            </div>
            <span class="text-sm text-gray-600">{{ authStore.user?.username }}</span>
          </div>
        </div>
      </header>
      
      <main class="flex-1 overflow-auto p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>