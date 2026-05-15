<script setup lang="ts">
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue')
    },
    {
      path: '/',
      name: 'Layout',
      component: () => import('@/layout/Layout.vue'),
      meta: { requiresAuth: true },
      children: [
        {
          path: '',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue')
        },
        {
          path: 'tasks',
          name: 'Tasks',
          component: () => import('@/views/TaskList.vue')
        },
        {
          path: 'tasks/todo',
          name: 'TodoTasks',
          component: () => import('@/views/TaskList.vue')
        },
        {
          path: 'tasks/done',
          name: 'DoneTasks',
          component: () => import('@/views/TaskList.vue')
        },
        {
          path: 'notifications',
          name: 'Notifications',
          component: () => import('@/views/NotificationCenter.vue')
        },
        {
          path: 'messages',
          name: 'Messages',
          component: () => import('@/views/MessageCenter.vue')
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/UserList.vue'),
          meta: { requiresAdmin: true }
        }
      ]
    }
  ]
})

/**
 * 路由守卫
 * 验证用户是否已登录，是否有权限访问该路由
 */
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  // 检查路由是否需要登录
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } 
  // 检查路由是否需要管理员权限
  else if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    next('/')
  } 
  // 如果已登录且访问登录页，跳转到首页
  else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
  } 
  else {
    next()
  }
})

export default router