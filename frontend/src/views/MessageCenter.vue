<script setup lang="ts">
import { ref, onMounted, nextTick, watch } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { ElMessage, ElModal, ElForm, ElFormItem, ElInput, ElSelect, ElButton } from 'element-plus'
import { Send, Bell, Users } from 'lucide-vue-next'
import type { Notification, BroadcastRequest } from '@/types'

const notificationStore = useNotificationStore()

// 消息列表
const messages = ref<Notification[]>([])
// 广播模态框状态
const showBroadcastModal = ref(false)
// 广播表单数据
const broadcastForm = ref<BroadcastRequest>({
  title: '',
  content: '',
  type: 'INFO'
})

/**
 * 获取消息列表
 */
const fetchMessages = async () => {
  try {
    const response = await notificationStore.getNotifications(0, 50)
    messages.value = response.content
    await nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    console.error('获取消息列表失败:', error)
  }
}

/**
 * 滚动到底部
 */
const scrollToBottom = () => {
  const container = document.querySelector('.message-container')
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

/**
 * 发送广播消息
 */
const handleBroadcast = async () => {
  if (!broadcastForm.value.title || !broadcastForm.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  
  try {
    await notificationStore.broadcast(broadcastForm.value)
    ElMessage.success('消息已广播')
    showBroadcastModal.value = false
    broadcastForm.value = {
      title: '',
      content: '',
      type: 'INFO'
    }
  } catch (error) {
    ElMessage.error('广播失败')
  }
}

/**
 * 获取通知类型样式
 */
const getTypeClass = (type: string) => {
  switch (type) {
    case 'SUCCESS': return 'text-green-600 bg-green-100'
    case 'WARNING': return 'text-yellow-600 bg-yellow-100'
    case 'ERROR': return 'text-red-600 bg-red-100'
    default: return 'text-blue-600 bg-blue-100'
  }
}

/**
 * 获取通知类型文本
 */
const getTypeText = (type: string) => {
  switch (type) {
    case 'SUCCESS': return '成功'
    case 'WARNING': return '警告'
    case 'ERROR': return '错误'
    default: return '信息'
  }
}

/**
 * 格式化时间
 */
const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
}

// 监听消息数量变化，自动滚动
watch(() => messages.value.length, () => {
  nextTick(() => scrollToBottom())
})

onMounted(() => {
  fetchMessages()
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100 h-full flex flex-col">
    <!-- 页面头部 -->
    <div class="p-6 border-b border-gray-100 flex items-center justify-between">
      <div class="flex items-center gap-3">
        <div class="w-10 h-10 bg-blue-100 rounded-xl flex items-center justify-center">
          <Bell class="w-5 h-5 text-blue-600" />
        </div>
        <div>
          <h2 class="text-xl font-semibold text-gray-800">实时消息推送</h2>
          <p class="text-sm text-gray-500">接收系统广播和通知</p>
        </div>
      </div>
      <!-- 广播按钮 -->
      <button 
        @click="showBroadcastModal = true"
        class="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-blue-600 transition-colors"
      >
        <Send class="w-4 h-4" />
        发送广播
      </button>
    </div>
    
    <!-- 消息列表 -->
    <div class="flex-1 overflow-auto p-6 message-container">
      <div class="space-y-4">
        <div 
          v-for="message in messages" 
          :key="message.id"
          class="p-4 bg-gray-50 rounded-lg border border-gray-100"
        >
          <div class="flex items-start gap-3">
            <!-- 用户图标 -->
            <div :class="['w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0', getTypeClass(message.type)]">
              <Users class="w-4 h-4" />
            </div>
            <!-- 消息内容 -->
            <div class="flex-1">
              <div class="flex items-center gap-2">
                <span :class="['px-2 py-0.5 rounded text-xs font-medium', getTypeClass(message.type)]">
                  {{ getTypeText(message.type) }}
                </span>
                <span class="text-xs text-gray-400">{{ formatDate(message.createdAt) }}</span>
              </div>
              <h3 class="font-medium text-gray-800 mt-1">{{ message.title }}</h3>
              <p class="text-sm text-gray-600 mt-1">{{ message.content }}</p>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div v-if="messages.length === 0" class="text-center py-12 text-gray-500">
          <Bell class="w-12 h-12 mx-auto mb-4 text-gray-300" />
          <p>暂无消息</p>
        </div>
      </div>
    </div>
    
    <!-- 连接状态 -->
    <div class="p-4 border-t border-gray-100">
      <div class="flex items-center gap-2 text-sm text-gray-500">
        <span class="w-2 h-2 bg-green-500 rounded-full animate-pulse"></span>
        <span>WebSocket已连接</span>
      </div>
    </div>
  </div>
  
  <!-- 广播消息模态框 -->
  <ElModal 
    v-model="showBroadcastModal" 
    title="发送广播消息"
    @close="showBroadcastModal = false"
  >
    <ElForm :model="broadcastForm" label-width="80px">
      <ElFormItem label="消息标题" prop="title">
        <ElInput v-model="broadcastForm.title" placeholder="请输入消息标题" />
      </ElFormItem>
      <ElFormItem label="消息类型" prop="type">
        <ElSelect v-model="broadcastForm.type" placeholder="请选择消息类型">
          <ElSelectOption label="信息" value="INFO" />
          <ElSelectOption label="成功" value="SUCCESS" />
          <ElSelectOption label="警告" value="WARNING" />
          <ElSelectOption label="错误" value="ERROR" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="消息内容" prop="content">
        <ElInput v-model="broadcastForm.content" type="textarea" :rows="4" placeholder="请输入消息内容" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="showBroadcastModal = false">取消</ElButton>
      <ElButton type="primary" @click="handleBroadcast">发送广播</ElButton>
    </template>
  </ElModal>
</template>