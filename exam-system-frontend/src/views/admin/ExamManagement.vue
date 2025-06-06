<template>
  <div class="exam-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试管理</span>
          <el-button type="primary" @click="showCreateDialog">创建考试</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试名称">
          <el-input v-model="searchForm.name" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="未开始" value="pending" />
            <el-option label="进行中" value="ongoing" />
            <el-option label="已结束" value="finished" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 考试列表 -->
      <el-table :data="examList" style="width: 100%">
        <el-table-column prop="title" label="考试名称" min-width="200" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column prop="isActive" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isActive ? 'success' : 'info'">
              {{ row.isActive ? '活动' : '非活动' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleQuestions(row)">题目管理</el-button>
            <el-button type="warning" link @click="handleResults(row)">成绩查看</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
    
    <!-- 创建考试对话框 -->
    <el-dialog
      title="创建考试"
      v-model="createDialogVisible"
      width="50%"
    >
      <el-form :model="examForm" label-width="120px" :rules="rules" ref="examFormRef">
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="examForm.title" placeholder="请输入考试标题"></el-input>
        </el-form-item>
        <el-form-item label="考试描述" prop="description">
          <el-input v-model="examForm.description" type="textarea" placeholder="请输入考试描述"></el-input>
        </el-form-item>
        <el-form-item label="考试时长(分钟)" prop="durationMinutes">
          <el-input-number v-model="examForm.durationMinutes" :min="1" :max="240"></el-input-number>
        </el-form-item>
        <el-form-item label="及格分数" prop="passingScore">
          <el-input-number v-model="examForm.passingScore" :min="0" :max="100"></el-input-number>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="examForm.totalScore" :min="1" :max="200"></el-input-number>
        </el-form-item>
        <el-form-item label="是否启用" prop="isActive">
          <el-switch v-model="examForm.isActive"></el-switch>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="createDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleAdd">创建</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExams, createExam, updateExam, updateExamStatus, deleteExam } from '@/api/exam'

// 创建考试对话框相关数据
const createDialogVisible = ref(false)
const examFormRef = ref(null)
const examForm = reactive({
  title: '',
  description: '',
  durationMinutes: 60,
  passingScore: 60,
  totalScore: 100,
  isActive: false
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入考试标题', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入考试描述', trigger: 'blur' }
  ],
  durationMinutes: [
    { required: true, message: '请设置考试时长', trigger: 'change' }
  ],
  passingScore: [
    { required: true, message: '请设置及格分数', trigger: 'change' }
  ],
  totalScore: [
    { required: true, message: '请设置总分', trigger: 'change' }
  ]
}

// 搜索表单
const searchForm = reactive({
  name: '',
  status: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 考试列表数据
const examList = ref([])
const loading = ref(false)

// 获取考试列表
const fetchExams = async () => {
  loading.value = true
  try {
    const response = await getExams()
    console.log('获取考试列表响应:', response)
    // Spring Boot 直接返回数据，不是包装在 data 字段中
    examList.value = Array.isArray(response) ? response : []
    total.value = examList.value.length
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchExams()
}

// 重置搜索
const resetSearch = () => {
  searchForm.name = ''
  searchForm.status = ''
  handleSearch()
}

// 显示创建对话框
const showCreateDialog = () => {
  // 重置表单
  examForm.title = ''
  examForm.description = ''
  examForm.durationMinutes = 60
  examForm.passingScore = 60
  examForm.totalScore = 100
  examForm.isActive = false
  
  // 显示对话框
  createDialogVisible.value = true
}

// 创建考试
const handleAdd = async () => {
  if (!examFormRef.value) return
  
  await examFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await createExam(examForm)
        console.log('创建考试响应:', response)
        ElMessage.success('创建考试成功')
        createDialogVisible.value = false
        fetchExams()
      } catch (error) {
        console.error('创建考试失败:', error)
        ElMessage.error('创建考试失败: ' + (error.response?.data?.message || error.message))
      }
    } else {
      return false
    }
  })
}

// 编辑考试
const handleEdit = (row) => {
  // TODO: 实现编辑考试逻辑
}

// 题目管理
const handleQuestions = (row) => {
  // TODO: 实现题目管理逻辑
}

// 成绩查看
const handleResults = (row) => {
  // TODO: 实现成绩查看逻辑
}

// 删除考试
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除考试 ${row.title} 吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await deleteExam(row.id)
      console.log('删除考试响应:', response)
      ElMessage.success('删除成功')
      fetchExams()
    } catch (error) {
      console.error('删除考试失败:', error)
      ElMessage.error('删除考试失败: ' + (error.response?.data?.message || error.message))
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
  handleSearch()
}
// 页面加载时获取考试列表
onMounted(() => {
  fetchExams()
})
</script>

<style scoped>
.exam-management {
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