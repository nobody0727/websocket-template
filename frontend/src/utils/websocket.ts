import SockJS from 'sockjs-client'
import { Client } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

// STOMP客户端实例
let stompClient: Client | null = null

/**
 * WebSocket消息接口
 */
export interface WebSocketMessage {
  type: string
  payload: unknown
}

/**
 * 连接WebSocket
 * @param onNotification 收到通知时的回调函数
 * @param onMessage 收到私有消息时的回调函数
 */
export const connectWebSocket = (
  onNotification: (message: unknown) => void,
  onMessage: (message: unknown) => void
): void => {
  const authStore = useAuthStore()
  
  // 创建SockJS连接
  const socket = new SockJS('/ws')
  stompClient = Stomp.over(socket)
  
  // 连接WebSocket服务器
  stompClient.connect(
    { Authorization: `Bearer ${authStore.getToken()}` },
    () => {
      // 订阅全局通知主题
      stompClient?.subscribe('/topic/notifications', (message) => {
        onNotification(JSON.parse(message.body))
      })
      
      // 订阅当前用户的私有通知队列
      stompClient?.subscribe(`/user/${authStore.user?.id}/queue/notifications`, (message) => {
        onMessage(JSON.parse(message.body))
      })
    },
    (error) => {
      console.error('WebSocket连接错误:', error)
      // 连接失败时，5秒后重试
      setTimeout(() => connectWebSocket(onNotification, onMessage), 5000)
    }
  )
}

/**
 * 断开WebSocket连接
 */
export const disconnectWebSocket = (): void => {
  if (stompClient) {
    stompClient.disconnect()
    stompClient = null
  }
}

/**
 * 发送WebSocket消息
 * @param destination 目标地址
 * @param body 消息内容
 */
export const sendMessage = (destination: string, body: unknown): void => {
  if (stompClient && stompClient.connected) {
    stompClient.send(destination, {}, JSON.stringify(body))
  }
}