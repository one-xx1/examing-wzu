<template>
  <div class="exam-results">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>考试成绩管理</span>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="考试名称">
          <el-select v-model="searchForm.examId" placeholder="请选择考试" clearable>
            <el-option
              v-for="exam in examList"
              :key="exam.id"
              :label="exam.title"
              :value="exam.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学生姓名">
          <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 成绩列表 -->
      <el-table
        v-loading="loading"
        :data="resultList"
        style="width: 100%"
        border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="examTitle" label="考试名称" min-width="180" />
        <el-table-column prop="studentName" label="学生姓名" width="120" />
        <el-table-column prop="score" label="得分" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-success': row.score >= 60, 'text-danger': row.score < 60 }">
              {{ row.score }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="用时" width="120">
          <template #default="{ row }">
            {{ formatDuration(row.duration) }}
          </template>
        </el-table-column>
        <el-table-column prop="submittedAt" label="完成时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.submittedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">查看详情</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="成绩详情"
      width="700px"
    >
      <div v-if="currentResult">
        <h3>{{ currentResult.examTitle }}</h3>
        <div class="result-info">
          <p><strong>学生：</strong> {{ currentResult.studentName }}</p>
          <p><strong>得分：</strong> {{ currentResult.score }}</p>
          <p><strong>用时：</strong> {{ formatDuration(currentResult.duration) }}</p>
          <p><strong>完成时间：</strong> {{ formatDateTime(currentResult.submittedAt) }}</p>
        </div>

        <div class="answer-list">
          <h4>答题详情</h4>
          <el-table :data="currentResult.answers || []" border>
            <el-table-column prop="question" label="题目" min-width="300" />
            <el-table-column prop="selectedAnswer" label="学生答案" width="100" />
            <el-table-column prop="correctAnswer" label="正确答案" width="100" />
            <el-table-column prop="isCorrect" label="是否正确" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isCorrect ? 'success' : 'danger'">
                  {{ row.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExams } from '@/api/exam'
import request from '@/utils/request'

// 搜索表单
const searchForm = reactive({
  examId: '',
  studentName: ''
})

// 分页相关
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)

// 数据列表
const examList = ref([])
const resultList = ref([])

// 详情对话框
const dialogVisible = ref(false)
const currentResult = ref(null)

// 获取考试列表
const fetchExams = async () => {
  try {
    const response = await getExams()
    examList.value = response.data || []
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败')
  }
}

// 获取成绩列表
const fetchResults = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1,
      size: pageSize.value
    }
    
    if (searchForm.examId) {
      params.examId = searchForm.examId
    }
    
    if (searchForm.studentName) {
      params.studentName = searchForm.studentName
    }
    
    const response = await request({
      url: '/admin/exams/results',
      method: 'get',
      params
    })
    
    console.log('获取成绩列表响应:', response)
    
    // 处理 Spring Boot 分页响应
    // axios 拦截器已经提取了 response.data
    if (response && response.content) {
      resultList.value = response.content
      total.value = response.totalElements
    } else {
      resultList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取成绩列表失败:', error)
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

// 格式化考试用时
const formatDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = seconds % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 查看详情
const viewDetail = async (row) => {
  try {
    const response = await request({
      url: `/admin/exams/results/${row.id}/detail`,
      method: 'get'
    })

    currentResult.value = response
    
    dialogVisible.value = true
  } catch (error) {
    console.error('获取成绩详情失败:', error)
    ElMessage.error('获取成绩详情失败')
  }
}

// 删除成绩
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除学生 ${row.studentName} 的 ${row.examTitle} 考试成绩吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request({
        url: `/admin/exams/results/${row.id}`,
        method: 'delete'
      })
      ElMessage.success('删除成功')
      fetchResults()
    } catch (error) {
      console.error('删除成绩失败:', error)
      ElMessage.error('删除成绩失败')
    }
  })
}

// 搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchResults()
}

// 重置搜索
const resetSearch = () => {
  searchForm.examId = ''
  searchForm.studentName = ''
  handleSearch()
}

// 分页大小改变
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchResults()
}

// 当前页改变
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchResults()
}

// 页面加载时获取数据
onMounted(() => {
  fetchExams()
  fetchResults()
})
</script>

<style scoped>
.exam-results {
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

.text-success {
  color: #67c23a;
  font-weight: bold;
}

.text-danger {
  color: #f56c6c;
  font-weight: bold;
}

.result-info {
  background-color: #f8f8f8;
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.answer-list {
  margin-top: 20px;
}

h3 {
  margin-top: 0;
  margin-bottom: 15px;
}

h4 {
  margin-bottom: 10px;
}
</style>
