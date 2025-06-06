<template>
  <div class="student-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>学生管理</span>
          <el-button type="primary" @click="handleAdd">添加学生</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentId" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 学生列表 -->
      <el-table 
        v-loading="loading"
        :data="studentList" 
        style="width: 100%"
        border
      >
        <el-table-column prop="username" label="学号" width="180" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            <el-button type="warning" link @click="handleResetPassword(row)">重置密码</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>

  <!-- 学生表单对话框 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogType === 'add' ? '添加学生' : '编辑学生'"
    width="50%"
  >
    <el-form
      ref="studentForm"
      :model="studentForm"
      :rules="rules"
      label-width="80px"
    >
      <el-form-item label="学号" prop="username">
        <el-input v-model="studentForm.username" :disabled="dialogType === 'edit'" />
      </el-form-item>
      <el-form-item label="密码" prop="password" v-if="dialogType === 'add'">
        <el-input v-model="studentForm.password" type="password" />
      </el-form-item>
      <el-form-item label="角色" prop="role">
        <el-select v-model="studentForm.role" placeholder="请选择角色" style="width: 100%">
          <el-option label="学生" value="STUDENT" />
        </el-select>
      </el-form-item>
    </el-form>
    
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudents, getStudent, createStudent, updateStudent, deleteStudent, resetPassword } from '@/api/student'

// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString()
}

// 搜索表单
const searchForm = reactive({
  studentId: '',
  name: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 学生列表数据
const studentList = ref([])
const loading = ref(false)

// 对话框相关
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const studentForm = ref({
  username: '',
  password: '',
  role: 'STUDENT'
})

// 表单校验规则
const rules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ]
}

// 获取学生列表
const fetchStudents = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value,
      username: searchForm.studentId || undefined,
      name: searchForm.name || undefined
    }
    
    const response = await getStudents(params)
    console.log('获取学生列表响应:', response)
    
    // 处理 Spring Boot 分页响应
    // axios 拦截器已经提取了 response.data
    if (response && response.content) {
      studentList.value = response.content
      total.value = response.totalElements
    } else {
      studentList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取学生列表失败:', error)
    ElMessage.error('获取学生列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchStudents()
}

// 重置搜索
const resetSearch = () => {
  searchForm.studentId = ''
  searchForm.name = ''
  handleSearch()
}

// 添加学生
const handleAdd = () => {
  dialogType.value = 'add'
  studentForm.value = {
    username: '',  // 学号
    password: '123456',  // 默认密码
    name: '',  // 姓名
    gender: '',  // 性别
    role: 'STUDENT'  // 角色固定为学生
  }
  dialogVisible.value = true
}

// 编辑学生
const handleEdit = (row) => {
  dialogType.value = 'edit'
  studentForm.value = {
    id: row.id,
    username: row.username,
    name: row.name,
    gender: row.gender,
    className: row.className,
    email: row.email,
    phone: row.phone
  }
  dialogVisible.value = true
}

// 删除学生
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除学生 ${row.name} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteStudent(row.id)
      ElMessage.success('删除成功')
      fetchStudents()
    } catch (error) {
      console.error('删除学生失败:', error)
      ElMessage.error('删除学生失败')
    }
  })
}

// 重置密码
const handleResetPassword = (row) => {
  ElMessageBox.confirm(
    `确定要重置学生 ${row.name} 的密码吗？重置后密码将变为 123456`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await resetPassword(row.id)
      ElMessage.success('密码重置成功')
    } catch (error) {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  })
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  handleSearch()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchStudents()
}

// 提交表单
const submitForm = async () => {
  try {
    // 确保角色为 STUDENT
    const formData = { ...studentForm.value, role: 'STUDENT' }
    console.log('提交的学生数据:', formData)
    
    if (dialogType.value === 'add') {
      const response = await createStudent(formData)
      console.log('创建学生响应:', response)
      ElMessage.success('添加学生成功')
    } else {
      const response = await updateStudent(formData.id, formData)
      console.log('更新学生响应:', response)
      ElMessage.success('更新学生成功')
    }
    dialogVisible.value = false
    fetchStudents()
  } catch (error) {
    console.error('提交学生表单失败:', error)
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  }
}

// 页面加载时获取学生列表
onMounted(() => {
  fetchStudents()
})
</script>

<style scoped>
.student-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style> 