<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from '@/utils/axios'
import { ElMessage, ElModal, ElForm, ElFormItem, ElInput, ElSelect, ElButton, ElTable, ElTableColumn } from 'element-plus'
import { Plus, Edit, Trash2, User } from 'lucide-vue-next'
import type { User as UserType } from '@/types'

const users = ref<UserType[]>([])
const showModal = ref(false)
const editingUser = ref<UserType | null>(null)
const form = ref({
  username: '',
  email: '',
  password: '',
  role: 'USER'
})

const fetchUsers = async () => {
  try {
    const response = await axios.get('/api/users')
    users.value = response.data
  } catch (error) {
    console.error('Failed to fetch users:', error)
  }
}

const openCreateModal = () => {
  editingUser.value = null
  form.value = {
    username: '',
    email: '',
    password: '',
    role: 'USER'
  }
  showModal.value = true
}

const openEditModal = (user: UserType) => {
  editingUser.value = user
  form.value = {
    username: user.username,
    email: user.email,
    password: '',
    role: user.role
  }
  showModal.value = true
}

const saveUser = async () => {
  if (!form.value.username || !form.value.email) {
    ElMessage.warning('请输入用户名和邮箱')
    return
  }
  
  if (!editingUser.value && !form.value.password) {
    ElMessage.warning('请输入密码')
    return
  }
  
  try {
    const data = { ...form.value }
    if (!data.password) {
      delete data.password
    }
    
    if (editingUser.value) {
      await axios.put(`/api/users/${editingUser.value.id}`, data)
      ElMessage.success('用户更新成功')
    } else {
      await axios.post('/api/users', data)
      ElMessage.success('用户创建成功')
    }
    showModal.value = false
    fetchUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteUser = async (user: UserType) => {
  if (user.role === 'ADMIN') {
    ElMessage.warning('不能删除管理员')
    return
  }
  
  try {
    await axios.delete(`/api/users/${user.id}`)
    ElMessage.success('用户已删除')
    fetchUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const getRoleClass = (role: string) => {
  return role === 'ADMIN' ? 'text-red-600 bg-red-100' : 'text-green-600 bg-green-100'
}

const getRoleText = (role: string) => {
  return role === 'ADMIN' ? '管理员' : '普通用户'
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchUsers()
})
</script>

<template>
  <div class="bg-white rounded-xl shadow-sm border border-gray-100">
    <div class="p-6 border-b border-gray-100">
      <div class="flex items-center justify-between">
        <h2 class="text-xl font-semibold text-gray-800">用户管理</h2>
        <button 
          @click="openCreateModal"
          class="flex items-center gap-2 px-4 py-2 bg-primary text-white rounded-lg hover:bg-blue-600 transition-colors"
        >
          <Plus class="w-4 h-4" />
          新建用户
        </button>
      </div>
    </div>
    
    <div class="p-6">
      <ElTable 
        :data="users" 
        border 
        class="w-full"
        :empty-text="'暂无用户'"
      >
        <ElTableColumn prop="id" label="ID" width="80" />
        <ElTableColumn prop="username" label="用户名" min-width="150">
          <template #default="scope">
            <div class="flex items-center gap-2">
              <div class="w-8 h-8 bg-primary/10 rounded-full flex items-center justify-center">
                <User class="w-4 h-4 text-primary" />
              </div>
              <span>{{ scope.row.username }}</span>
            </div>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="email" label="邮箱" min-width="200" />
        <ElTableColumn prop="role" label="角色" width="120">
          <template #default="scope">
            <span :class="['px-2 py-1 rounded-full text-xs font-medium', getRoleClass(scope.row.role)]">
              {{ getRoleText(scope.row.role) }}
            </span>
          </template>
        </ElTableColumn>
        <ElTableColumn prop="createdAt" label="创建时间" width="150">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </ElTableColumn>
        <ElTableColumn label="操作" width="180">
          <template #default="scope">
            <div class="flex items-center gap-2">
              <button 
                @click="openEditModal(scope.row)"
                class="flex items-center gap-1 px-3 py-1.5 text-sm bg-blue-100 text-blue-600 rounded-lg hover:bg-blue-200 transition-colors"
              >
                <Edit class="w-4 h-4" />
                编辑
              </button>
              <button 
                v-if="scope.row.role !== 'ADMIN'"
                @click="deleteUser(scope.row)"
                class="flex items-center gap-1 px-3 py-1.5 text-sm bg-red-100 text-red-600 rounded-lg hover:bg-red-200 transition-colors"
              >
                <Trash2 class="w-4 h-4" />
                删除
              </button>
            </div>
          </template>
        </ElTableColumn>
      </ElTable>
    </div>
  </div>
  
  <ElModal 
    v-model="showModal" 
    :title="editingUser ? '编辑用户' : '新建用户'"
    @close="showModal = false"
  >
    <ElForm :model="form" label-width="80px">
      <ElFormItem label="用户名" prop="username">
        <ElInput v-model="form.username" placeholder="请输入用户名" />
      </ElFormItem>
      <ElFormItem label="邮箱" prop="email">
        <ElInput v-model="form.email" type="email" placeholder="请输入邮箱" />
      </ElFormItem>
      <ElFormItem label="密码" prop="password">
        <ElInput v-model="form.password" type="password" :placeholder="editingUser ? '留空表示不修改密码' : '请输入密码'" />
      </ElFormItem>
      <ElFormItem label="角色" prop="role">
        <ElSelect v-model="form.role" placeholder="请选择角色">
          <ElSelectOption label="管理员" value="ADMIN" />
          <ElSelectOption label="普通用户" value="USER" />
        </ElSelect>
      </ElFormItem>
    </ElForm>
    <template #footer>
      <ElButton @click="showModal = false">取消</ElButton>
      <ElButton type="primary" @click="saveUser">保存</ElButton>
    </template>
  </ElModal>
</template>