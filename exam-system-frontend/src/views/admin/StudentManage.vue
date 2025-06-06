<template>
  <div class="student-manage">
    <div class="header">
      <h2>学生管理</h2>
      <el-button type="primary" @click="handleAdd">添加学生</el-button>
    </div>

    <el-table :data="students" style="width: 100%" v-loading="loading">
      <el-table-column prop="username" label="学号" width="180" />
      <el-table-column prop="password" label="密码" width="180" />
      <el-table-column prop="createdAt" label="注册时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加学生对话框 -->
    <el-dialog v-model="dialogVisible" title="添加学生" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="学号" prop="username">
          <el-input v-model="form.username" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const students = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()

const form = ref({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' },
    { pattern: /^\d+$/, message: '学号必须为数字', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能小于6位', trigger: 'blur' }
  ]
}

const loadStudents = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/admin/students')
    students.value = response
  } catch (error) {
    ElMessage.error('获取学生列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  form.value = {
    username: '',
    password: ''
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学生吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/api/admin/students/${row.id}`)
    ElMessage.success('删除成功')
    loadStudents()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    await request.post('/api/admin/students', form.value)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    loadStudents()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadStudents()
})
</script>

<style scoped>
.student-manage {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}
</style>
