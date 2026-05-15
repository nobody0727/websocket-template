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

// 侧边栏折叠状态
const collapsed = ref(false)

// 基础菜单项
const menuItems = [
  { name: 'Dashboard', path: '/', icon: LayoutDashboard },
  { name: '任务管理', path: '/tasks', icon: ListTodo },
  { name: '通知中心', path: '/notifications', icon: Bell },
  { name: '消息推送', path: '/messages', icon: MessageSquare }
]

// 管理员专属菜单项
const adminMenuItems = [
  { name: '用户管理', path: '/users', icon: Users }
]

/**
 * 根据用户角色计算全部菜单项
 * 管理员显示所有菜单，普通用户只显示基础菜单
 */
const allMenuItems = computed(() => {
  if (authStore.user?.role === 'ADMIN') {
    return [...menuItems, ...adminMenuItems]
  }
  return menuItems
})

/**
 * 退出登录
 */
const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

/**
 * 切换侧边栏折叠状态
 */
const toggleSidebar = () => {
  collapsed.value = !collapsed.value
}

/**
 * 判断菜单项是否激活
 */
const isActive = (path: string) => {
  return route.path === path || route.path.startsWith(path + '/')
}
</script>

<template>
  <div class="flex h-screen bg-gray-50">
    <!-- 侧边栏 -->
    <aside 
      :class="[
        'bg-white border-r border-gray-200 transition-all duration-300 flex flex-col',
        collapsed ? 'w-16' : 'w-64'
      ]"
    >
      <!-- 侧边栏头部 -->
      <div class="p-4 border-b border-gray-200 flex items-center justify-between">
        <div v-if="!collapsed" class="text-xl font-bold text-primary">管理系统</div>
        <div v-else class="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
          <Settings class="w-5 h-5 text-white" />
        </div>
        <!-- 折叠/展开按钮 -->
        <button 
          @click="toggleSidebar"
          class="p-1 hover:bg-gray-100 rounded transition-colors"
        >
          <ChevronLeft v-if="!collapsed" class="w-5 h-5 text-gray-500" />
          <ChevronRight v-else class="w-5 h-5 text-gray-500" />
        </button>
      </div>
      
      <!-- 导航菜单 -->
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
      
      <!-- 退出登录按钮 -->
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
    
    <!-- 主内容区域 -->
    <div class="flex-1 flex flex-col overflow-hidden">
      <!-- 顶部栏 -->
      <header class="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
        <!-- 页面标题 -->
        <h1 class="text-lg font-semibold text-gray-800">{{ 
          allMenuItems.find(item => isActive(item.path))?.name || 'Dashboard' 
        }}</h1>
        <!-- 右侧用户信息和通知 -->
        <div class="flex items-center space-x-4">
          <!-- 通知图标 -->
          <router-link to="/notifications" class="relative">
            <Bell class="w-6 h-6 text-gray-500 hover:text-primary transition-colors cursor-pointer" />
            <!-- 未读通知数量徽章 -->
            <span 
              v-if="notificationStore.unreadNotifications > 0"
              class="absolute -top-1 -right-1 w-5 h-5 bg-red-500 text-white text-xs rounded-full flex items-center justify-center"
            >
              {{ notificationStore.unreadNotifications }}
            </span>
          </router-link>
          <!-- 用户信息 -->
          <div class="flex items-center space-x-2">
            <div class="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center">
              <span class="text-primary font-medium">{{ authStore.user?.username?.charAt(0) }}</span>
            </div>
            <span class="text-sm text-gray-600">{{ authStore.user?.username }}</span>
          </div>
        </div>
      </header>
      
      <!-- 页面内容 -->
      <main class="flex-1 overflow-auto p-6">
        <router-view />
      </main>
    </div>
  </div>
</template>