<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import axios from '@/utils/axios'
import { ElMessage, ElTable, ElTableColumn, ElPagination } from 'element-plus'
import { Plus, Edit, Trash2, CheckCircle, ArrowLeft } from 'lucide-vue-next'
import type { Task, CreateTaskRequest } from '@/types'

const route = useRoute()

// 任务列表和分页
const tasks = ref<Task[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)

// 模态框状态
const showModal = ref(false)
const editingTask = ref<Task | null>(null)

// 表单数据
const form = ref<CreateTaskRequest>({
  title: '',
  description: '',
  priority: 'MEDIUM',
  deadline: ''
})

/**
 * 根据路由路径判断当前显示的任务状态
 */
const statusFilter = computed(() => {
  if (route.path === '/tasks/todo') return 'TODO'
  if (route.path === '/tasks/done') return 'DONE'
  return ''
})

/**
 * 获取任务列表
 */
const fetchTasks = async () => {
  try {
    const params: Record<string, unknown> = {
      page: page.value - 1,
      size: size.value
    }
    if (statusFilter.value) {
      params.status = statusFilter.value
    }
    const response = await axios.get('/api/tasks', { params })
    tasks.value = response.data.content
    total.value = response.data.totalElements
  } catch (error) {
    console.error('获取任务列表失败:', error)
  }
}

/**
 * 打开新建任务模态框
 */
const openCreateModal = () => {
  editingTask.value = null
  form.value = {
    title: '',
    description: '',
    priority: 'MEDIUM',
    deadline: ''
  }
  showModal.value = true
}

/**
 * 打开编辑任务模态框
 */
const openEditModal = (task: Task) => {
  editingTask.value = task
  form.value = {
    title: task.title,
    description: task.description || '',
    priority: task.priority,
    deadline: task.deadline || ''
  }
  showModal.value = true
}

/**
 * 保存任务（新建或更新）
 */
const saveTask = async () => {
  if (!form.value.title) {
    ElMessage.warning('请输入任务标题')
    return
  }
  
  try {
    if (editingTask.value) {
      await axios.put(`/api/tasks/${editingTask.value.id}`, form.value)
      ElMessage.success('任务更新成功')
    } else {
      await axios.post('/api/tasks', form.value)
      ElMessage.success('任务创建成功')
    }
    showModal.value = false
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 完成任务
 */
const completeTask = async (task: Task) => {
  try {
    await axios.put(`/api/tasks/${task.id}/complete`)
    ElMessage.success('任务已完成')
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 删除任务
 */
const deleteTask = async (task: Task) => {
  try {
    await axios.delete(`/api/tasks/${task.id}`)
    ElMessage.success('任务已删除')
    fetchTasks()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

/**
 * 获取优先级样式
 */
const getPriorityClass = (priority: string) => {
  switch (priority) {
    case 'HIGH': return 'text-red-600 bg-red-100'
    case 'MEDIUM': return 'text-yellow-600 bg-yellow-100'
    case 'LOW': return 'text-green-600 bg-green-100'
    default: return 'text-gray-600 bg-gray-100'
  }
}

/**
 * 获取优先级文本
 */
const getPriorityText = (priority: string) => {
  switch (priority) {
    case 'HIGH': return '高优先级'
    case 'MEDIUM': return '中优先级'
    case 'LOW': return '低优先级'
    default: return priority
  }
}

/**
 * 格式化日期
 */
const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 监听路由变化，重新加载任务
watch(() => route.path, () => {
  page.value = 1
  fetchTasks()
})

onMounted(() => {
  fetchTasks()
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100">
    <!-- 页面头部 -->
    <div class="p-6 border-b border-gray-100">
      <div class="flex items-center justify-between">
        <div class="flex items-center gap-4">
          <!-- 返回按钮 -->
          <button 
            v-if="statusFilter"
            @click="$router.push('/tasks')"
            class="flex items-center text-gray-600 hover:text-primary transition-colors"
          >
            <ArrowLeft class="w-5 h-5" />
          </button>
          <h2 class="text-xl font-semibold text-gray-800">
            {{ statusFilter === 'TODO' ? '待办任务' : statusFilter === 'DONE' ? '已完成任务' : '任务管理' }}
          </h2>
        </div>
        <div class="flex items-center gap-3">
          <!-- 状态切换按钮 -->
          <router-link 
            v-if="statusFilter !== 'TODO'"
            to="/tasks/todo"
            class="px-4 py-2 text-sm text-gray-600 hover:text-primary hover:bg-primary/5 rounded-lg transition-colors"
          >
            待办任务
          </router-link>
          <router-link 
            v-if="statusFilter !== 'DONE'"
            to="/tasks/done"
            class="px-4 py-2 text-sm text-gray-600 hover:text-primary hover:bg-primary/5 rounded-lg transition-colors"
          >
            已完成
          </router-link>
          <!-- 新建任务按钮 -->
          <button 
            @click="openCreateModal"
            class="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-blue-600 transition-colors"
          >
            <Plus class="w-4 h-4" />
            新建任务
          </button>
        </div>
      </div>
    </div>
    
    <!-- 任务表格 -->
    <div class="p-6">
      <ElTable 
        :data="tasks" 
        border 
        class="w-full"
        :empty-text="'暂无任务'"
      >
        <ElTableColumn prop="title" label="任务标题" min-width="200" />
        <ElTableColumn prop="description" label="描述" min-width="250" show-overflow-tooltip />
        <ElTableColumn prop="priority" label="优先级" width="120">
          <template #default="scope">
            <span :class="['px-2 py-1 rounded-full text-xs font-medium', getPriorityClass(scope.row.priority)]">
              {{ getPriorityText(scope.row.priority) }}
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="deadline" label="截止日期" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.deadline) }}
          </template>
        </ElTableColumn>
        <ElTableColumn prop="createdAt" label="创建时间" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="200">
          <template #default="scope">
            <div class="flex items-center gap-2">
              <!-- 完成按钮（仅待办任务显示） -->
              <button 
                v-if="scope.row.status === 'TODO'"
                @click="completeTask(scope.row)"
                class="flex items-center gap-1 px-3 py-1.5 text-sm bg-green-100 text-green-600 rounded-lg hover:bg-green-200 transition-colors"
              >
                <CheckCircle class="w-4 h-4" />
                完成
              </button>
              <!-- 编辑按钮 -->
              <button 
                @click="openEditModal(scope.row)"
                class="flex items-center gap-1 px-3 py-1.5 text-sm bg-blue-100 text-blue-600 rounded-lg hover:bg-blue-200 transition-colors"
              >
                <Edit class="w-4 h-4" />
                编辑
              </button>
              <!-- 删除按钮 -->
              <button 
                @click="deleteTask(scope.row)"
                class="flex items-center gap-1 px-3 py-1.5 text-sm bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors"
              >
                <Trash2 class="w-4 h-4" />
                删除
              </button>
            </div>
          </template>
        </ElTableColumn>
      </ElTable>
      
      <!-- 分页组件 -->
      <div class="flex justify-center mt-6">
        <ElPagination
          v-model:current-page="page"
          v-model:page-size="size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchTasks"
          @current-change="fetchTasks"
        />
      </div>
    </div>
  </div>
  
  <!-- 任务编辑模态框 -->
  <ElModal 
    v-model="showModal" 
    :title="editingTask ? '编辑任务' : '新建任务'"
    @close="showModal = false"
  >
    <ElForm :model="form" label-width="80px">
      <ElFormItem label="任务标题" prop="title">
        <ElInput v-model="form.title" placeholder="请输入任务标题" />
      </ElFormItem>
      <ElFormItem label="任务描述" prop="description">
        <ElInput v-model="form.description" type="textarea" :rows="3" placeholder="请输入任务描述" />
      </ElFormItem>
      <ElFormItem label="优先级" prop="priority">
        <ElSelect v-model="form.priority" placeholder="请选择优先级">
          <ElSelectOption label="高优先级" value="HIGH" />
          <ElSelectOption label="中优先级" value="MEDIUM" />
          <ElSelectOption label="低优先级" value="LOW" />
        </ElSelect>
      </ElFormItem>
      <ElFormItem label="截止日期" prop="deadline">
        <ElDatePicker v-model="form.deadline" type="date" placeholder="请选择截止日期" />
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="showModal = false">取消</ElButton>
      <ElButton type="primary" @click="saveTask">保存</ElButton>
    </template>
  </ElModal>
</template>