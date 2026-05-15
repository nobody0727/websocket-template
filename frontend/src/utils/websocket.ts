import SockJS from 'sockjs-client'
import { Client, Stomp } from 'stompjs'
import { useAuthStore } from '@/stores/auth'

let stompClient: Client | null = null

export interface WebSocketMessage {
  type: string
  payload: unknown
}

export const connectWebSocket = (
  onNotification: (message: unknown) => void,
  onMessage: (message: unknown) => void
): void => {
  const authStore = useAuthStore()
  
  const socket = new SockJS('/ws')
  stompClient = Stomp.over(socket)
  
  stompClient.connect(
    { Authorization: `Bearer ${authStore.getToken()}` },
    () => {
      stompClient?.subscribe('/topic/notifications', (message) => {
        onNotification(JSON.parse(message.body))
      })
      
      stompClient?.subscribe(`/user/${authStore.user?.id}/queue/notifications`, (message) => {
        onMessage(JSON.parse(message.body))
      })
    },
    (error) => {
      console.error('WebSocket connection error:', error)
      setTimeout(() => connectWebSocket(onNotification, onMessage), 5000)
    }
  )
}

export const disconnectWebSocket = (): void => {
  if (stompClient) {
    stompClient.disconnect()
    stompClient = null
  }
}

export const sendMessage = (destination: string, body: unknown): void => {
  if (stompClient && stompClient.connected) {
    stompClient.send(destination, {}, JSON.stringify(body))
  }
}