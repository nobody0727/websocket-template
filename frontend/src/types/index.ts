/**
 * TypeScript类型定义文件
 * 定义项目中使用的所有数据类型
 */

// 任务优先级
export type Priority = 'HIGH' | 'MEDIUM' | 'LOW'

// 任务状态
export type TaskStatus = 'TODO' | 'DONE'

// 通知类型
export type NotificationType = 'INFO' | 'WARNING' | 'ERROR' | 'SUCCESS'

// 用户角色
export type UserRole = 'ADMIN' | 'USER'

/**
 * 任务接口
 */
export interface Task {
  id: number
  title: string
  description: string
  priority: Priority
  status: TaskStatus
  deadline: string | null
  createdAt: string
  updatedAt: string
  userId: number
}

/**
 * 通知接口
 */
export interface Notification {
  id: number
  title: string
  content: string
  type: NotificationType
  isRead: boolean
  createdAt: string
  userId: number | null
}

/**
 * 用户接口
 */
export interface User {
  id: number
  username: string
  email: string
  role: UserRole
  createdAt: string
}

/**
 * 创建任务请求接口
 */
export interface CreateTaskRequest {
  title: string
  description?: string
  priority: Priority
  deadline?: string
}

/**
 * 广播消息请求接口
 */
export interface BroadcastRequest {
  title: string
  content: string
  type: NotificationType
  userId?: number
}