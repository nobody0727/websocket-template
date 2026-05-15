export interface Task {
  id: number
  title: string
  description: string
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  status: 'TODO' | 'DONE'
  deadline: string | null
  createdAt: string
  updatedAt: string
  userId: number
}

export interface Notification {
  id: number
  title: string
  content: string
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
  isRead: boolean
  createdAt: string
  userId: number | null
}

export interface User {
  id: number
  username: string
  email: string
  role: 'ADMIN' | 'USER'
  createdAt: string
}

export interface CreateTaskRequest {
  title: string
  description?: string
  priority: 'HIGH' | 'MEDIUM' | 'LOW'
  deadline?: string
}

export interface BroadcastRequest {
  title: string
  content: string
  type: 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'
  userId?: number
}