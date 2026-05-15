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

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isLoggedIn) {
    next('/login')
  } else if (to.meta.requiresAdmin && authStore.user?.role !== 'ADMIN') {
    next('/')
  } else if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router