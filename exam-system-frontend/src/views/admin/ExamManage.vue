<template>
  <div class="exam-manage">
    <div class="header">
      <h2>考试管理</h2>
      <el-button type="primary" @click="handleAdd">新增题库</el-button>
    </div>

    <el-table :data="exams" style="width: 100%" v-loading="loading">
      <el-table-column prop="title" label="题库名称" width="180" />
      <el-table-column prop="createdAt" label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createdAt) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="scope">
          <el-switch
            v-model="scope.row.isActive"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加/编辑题库对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="isEdit ? '编辑题库' : '新增题库'" 
      width="500px"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题库名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入题库名称" />
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

const exams = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)

const form = ref({
  id: null,
  title: ''
})

const rules = {
  title: [
    { required: true, message: '请输入题库名称', trigger: 'blur' }
  ]
}

const loadExams = async () => {
  loading.value = true
  try {
    const response = await request.get('/api/admin/exams')
    exams.value = response
  } catch (error) {
    ElMessage.error('获取题库列表失败')
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  form.value = {
    id: null,
    title: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  form.value = {
    id: row.id,
    title: row.title
  }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该题库吗？', '提示', {
      type: 'warning'
    })
    await request.delete(`/api/admin/exams/${row.id}`)
    ElMessage.success('删除成功')
    loadExams()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleStatusChange = async (row) => {
  try {
    await request.put(`/api/admin/exams/${row.id}/status`, {
      isActive: row.isActive
    })
    ElMessage.success('状态更新成功')
  } catch (error) {
    row.isActive = !row.isActive // 恢复状态
    ElMessage.error('状态更新失败')
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await request.put(`/api/admin/exams/${form.value.id}`, form.value)
    } else {
      await request.post('/api/admin/exams', form.value)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '添加成功')
    dialogVisible.value = false
    loadExams()
  } catch (error) {
    ElMessage.error(isEdit.value ? '编辑失败' : '添加失败')
  }
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

onMounted(() => {
  loadExams()
})
</script>

<style scoped>
.exam-manage {
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
