<template>
  <div class="exam-container">
    <!-- 考试列表页面 -->
    <div v-if="!currentExam">
      <el-card class="exam-list-card">
        <template #header>
          <div class="card-header">
            <span>可参加的考试</span>
            <el-button @click="fetchExams" :loading="loading" type="primary" size="small">
              刷新
            </el-button>
          </div>
        </template>

        <el-table
          v-loading="loading"
          :data="examList"
          style="width: 100%"
          empty-text="暂无可参加的考试"
        >
          <el-table-column prop="title" label="考试名称" min-width="200" />
          <el-table-column prop="description" label="描述" min-width="200" />
          <el-table-column prop="duration" label="考试时长" width="120">
            <template #default="{ row }">
              {{ row.duration || 60 }} 分钟
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" fixed="right">
            <template #default="{ row }">
              <el-button
                type="primary"
                size="small"
                @click="startExam(row)"
                :disabled="!canTakeExam(row)"
              >
                {{ getActionText(row) }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @size-change="fetchExams"
            @current-change="fetchExams"
          />
        </div>
      </el-card>
    </div>

    <!-- 考试进行页面 -->
    <div v-else class="exam-content">
      <el-card class="exam-info-card">
        <div class="exam-header">
          <div class="exam-title">
            <h2>{{ currentExam.title }}</h2>
            <p class="exam-description">{{ currentExam.description }}</p>
          </div>
          <div class="exam-timer">
            <el-icon><Timer /></el-icon>
            剩余时间: {{ formatTime(remainingTime) }}
          </div>
        </div>

        <div class="question-navigation">
          <div class="nav-title">题目导航:</div>
          <el-button-group class="question-buttons">
            <el-button
              v-for="(q, index) in currentExam.questions"
              :key="index"
              size="small"
              :type="currentQuestionIndex === index ? 'primary' :
                    (userAnswers[index] !== undefined ? 'success' : 'default')"
              @click="currentQuestionIndex = index"
            >
              {{ index + 1 }}
            </el-button>
          </el-button-group>
        </div>

        <div class="question-container" v-if="currentQuestion">
          <div class="question-content">
            <div class="question-header">
              <h3>第 {{ currentQuestionIndex + 1 }} 题 / 共 {{ currentExam.questions.length }} 题</h3>
              <div class="question-score">分值: {{ currentQuestion.score || 5 }} 分</div>
            </div>

            <div class="question-text">
              {{ currentQuestion.content }}
            </div>

            <el-radio-group v-model="userAnswers[currentQuestionIndex]" class="options-group">
              <div v-for="(option, optIndex) in currentQuestion.options" :key="optIndex" class="option-item">
                <el-radio :label="optIndex" class="option-radio">
                  <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                  <span class="option-text">{{ option }}</span>
                </el-radio>
              </div>
            </el-radio-group>
          </div>

          <div class="question-actions">
            <div class="nav-buttons">
              <el-button @click="prevQuestion" :disabled="currentQuestionIndex === 0">
                <el-icon><ArrowLeft /></el-icon>
                上一题
              </el-button>
              <el-button @click="nextQuestion" :disabled="currentQuestionIndex === currentExam.questions.length - 1">
                下一题
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
            <div class="submit-area">
              <el-button type="warning" @click="exitExam">退出考试</el-button>
              <el-button type="primary" @click="submitExam">
                提交考试 ({{ answeredCount }}/{{ currentExam.questions.length }})
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 提交确认对话框 -->
    <el-dialog
      v-model="submitDialogVisible"
      title="确认提交考试"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="submit-dialog-content">
        <el-icon class="warning-icon"><Warning /></el-icon>
        <div class="dialog-text">
          <p>您即将提交考试，请确认以下信息：</p>
          <ul>
            <li>总题数: {{ currentExam?.questions?.length || 0 }} 题</li>
            <li>已答题数: {{ answeredCount }} 题</li>
            <li>未答题数: {{ (currentExam?.questions?.length || 0) - answeredCount }} 题</li>
          </ul>
          <p class="warning-text">提交后将无法修改答案，确定要提交吗？</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="submitDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmit" :loading="submitting">
            确认提交
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 退出确认对话框 -->
    <el-dialog
      v-model="exitDialogVisible"
      title="确认退出考试"
      width="300px"
    >
      <span>确定要退出当前考试吗？退出后答案将不会保存。</span>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="exitDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmExit">确认退出</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Timer, ArrowLeft, ArrowRight, Warning } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const examList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 当前考试相关
const currentExam = ref(null)
const currentQuestionIndex = ref(0)
const userAnswers = ref({})
const remainingTime = ref(0)
const timer = ref(null)
const submitDialogVisible = ref(false)
const exitDialogVisible = ref(false)

// 获取当前问题
const currentQuestion = computed(() => {
  if (!currentExam.value || !currentExam.value.questions) return null
  return currentExam.value.questions[currentQuestionIndex.value]
})

// 已回答题目数量
const answeredCount = computed(() => {
  return Object.keys(userAnswers.value).length
})

// 判断是否所有题目都已回答
const isAllQuestionsAnswered = computed(() => {
  if (!currentExam.value || !currentExam.value.questions) return false
  return currentExam.value.questions.every((_, index) => userAnswers.value[index] !== undefined)
})

// 获取考试列表
const fetchExams = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/student/exams',
      method: 'get',
      params: {
        page: currentPage.value - 1,
        size: pageSize.value
      }
    })

    if (response && response.content) {
      examList.value = response.content
      total.value = response.totalElements
    } else {
      examList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
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

// 格式化剩余时间
const formatTime = (seconds) => {
  if (seconds < 0) return '00:00:00'
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  const secs = seconds % 60
  return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// 获取考试状态类型
const getStatusType = (status) => {
  switch (status) {
    case 'NOT_STARTED': return 'info'
    case 'IN_PROGRESS': return 'warning'
    case 'COMPLETED': return 'success'
    case 'EXPIRED': return 'danger'
    default: return 'info'
  }
}

// 获取考试状态文本
const getStatusText = (status) => {
  switch (status) {
    case 'IN_PROGRESS': return '可参加'
    case 'COMPLETED': return '已完成'
    default: return '可参加'
  }
}

// 获取操作按钮文本
const getActionText = (exam) => {
  switch (exam.status) {
    case 'IN_PROGRESS': return '开始考试'
    case 'COMPLETED': return '查看结果'
    default: return '开始考试'
  }
}

// 判断是否可以参加考试
const canTakeExam = (exam) => {
  return exam.status !== 'COMPLETED'
}

// 开始考试
const startExam = async (exam) => {
  if (exam.status === 'COMPLETED') {
    // 查看考试结果 - 这里可以跳转到结果页面
    ElMessage.info('您已完成此考试')
    return
  }

  if (!canTakeExam(exam)) {
    ElMessage.warning('当前无法参加此考试')
    return
  }

  try {
    loading.value = true
    const response = await request({
      url: `/student/exams/${exam.id}`,
      method: 'get'
    })

    currentExam.value = response
    remainingTime.value = (exam.duration || 60) * 60 // 转换为秒
    startTimer()

    // 初始化答案对象
    userAnswers.value = {}
    currentQuestionIndex.value = 0

    ElMessage.success('考试已开始，请认真答题')
  } catch (error) {
    console.error('获取考试详情失败:', error)
    ElMessage.error('获取考试详情失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 开始计时器
const startTimer = () => {
  timer.value = setInterval(() => {
    if (remainingTime.value > 0) {
      remainingTime.value--

      // 时间不足5分钟时提醒
      if (remainingTime.value === 300) {
        ElMessage.warning('考试时间还剩5分钟，请抓紧时间')
      }
    } else {
      clearInterval(timer.value)
      ElMessage.error('考试时间已结束，系统将自动提交')
      confirmSubmit()
    }
  }, 1000)
}

// 上一题
const prevQuestion = () => {
  if (currentQuestionIndex.value > 0) {
    currentQuestionIndex.value--
  }
}

// 下一题
const nextQuestion = () => {
  if (currentQuestionIndex.value < currentExam.value.questions.length - 1) {
    currentQuestionIndex.value++
  }
}

// 退出考试
const exitExam = () => {
  exitDialogVisible.value = true
}

// 确认退出
const confirmExit = () => {
  clearInterval(timer.value)
  currentExam.value = null
  userAnswers.value = {}
  currentQuestionIndex.value = 0
  remainingTime.value = 0
  exitDialogVisible.value = false
  ElMessage.info('已退出考试')
}

// 提交考试
const submitExam = () => {
  if (!isAllQuestionsAnswered.value) {
    ElMessageBox.confirm('您还有题目未作答，确定要提交吗？', '提示', {
      confirmButtonText: '确定提交',
      cancelButtonText: '继续作答',
      type: 'warning'
    }).then(() => {
      submitDialogVisible.value = true
    }).catch(() => {})
  } else {
    submitDialogVisible.value = true
  }
}

// 根据选项索引获取答案
const getAnswerByIndex = (question, optionIndex) => {
  const options = ['A', 'B', 'C', 'D']
  return options[optionIndex] || 'A'
}

// 确认提交
const confirmSubmit = async () => {
  try {
    submitting.value = true

    // 将答案转换为后端需要的格式
    const answers = Object.entries(userAnswers.value).map(([questionIndex, optionIndex]) => {
      const question = currentExam.value.questions[questionIndex]
      const selectedAnswer = getAnswerByIndex(question, optionIndex)
      return {
        questionId: question.id,
        selectedOption: optionIndex,
        selectedAnswer: selectedAnswer
      }
    })

    await request({
      url: '/student/exams/submit',
      method: 'post',
      data: {
        examId: currentExam.value.id,
        answers: answers
      }
    })

    ElMessage.success('考试提交成功')
    clearInterval(timer.value)

    // 重置状态并返回考试列表
    currentExam.value = null
    userAnswers.value = {}
    currentQuestionIndex.value = 0
    remainingTime.value = 0

    // 重新获取考试列表以更新状态
    fetchExams()
  } catch (error) {
    console.error('提交考试失败:', error)
    ElMessage.error('提交考试失败: ' + (error.response?.data?.message || error.message))
  } finally {
    submitting.value = false
    submitDialogVisible.value = false
  }
}

// 组件挂载时获取考试列表
onMounted(() => {
  fetchExams()
})

// 组件卸载前清除定时器
onBeforeUnmount(() => {
  if (timer.value) {
    clearInterval(timer.value)
  }
})
</script>

<style scoped>
.exam-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.exam-list-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.exam-content {
  max-width: 1000px;
  margin: 0 auto;
}

.exam-info-card {
  margin-bottom: 20px;
}

.exam-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #ebeef5;
}

.exam-title h2 {
  margin: 0 0 5px 0;
  color: #303133;
  font-size: 24px;
}

.exam-description {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.exam-timer {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: bold;
  color: #f56c6c;
  background: #fef0f0;
  padding: 10px 15px;
  border-radius: 6px;
  border: 1px solid #fbc4c4;
}

.question-navigation {
  margin-bottom: 25px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.nav-title {
  margin-bottom: 10px;
  font-weight: bold;
  color: #606266;
}

.question-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.question-container {
  margin-top: 20px;
}

.question-content {
  margin-bottom: 30px;
  padding: 20px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #409eff;
}

.question-header h3 {
  margin: 0;
  color: #303133;
  font-size: 18px;
}

.question-score {
  color: #67c23a;
  font-weight: bold;
  background: #f0f9ff;
  padding: 4px 8px;
  border-radius: 4px;
  border: 1px solid #b3d8ff;
}

.question-text {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  margin-bottom: 20px;
  padding: 15px;
  background: #fafafa;
  border-left: 4px solid #409eff;
}

.options-group {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.option-item {
  margin: 0;
}

.option-radio {
  width: 100%;
  margin: 0;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  transition: all 0.3s;
}

.option-radio:hover {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-radio.is-checked {
  border-color: #409eff;
  background: #ecf5ff;
}

.option-label {
  font-weight: bold;
  color: #409eff;
  margin-right: 8px;
}

.option-text {
  color: #303133;
  line-height: 1.5;
}

.question-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.nav-buttons {
  display: flex;
  gap: 10px;
}

.submit-area {
  display: flex;
  gap: 10px;
  align-items: center;
}

.submit-dialog-content {
  display: flex;
  align-items: flex-start;
  gap: 15px;
}

.warning-icon {
  color: #e6a23c;
  font-size: 24px;
  margin-top: 2px;
}

.dialog-text {
  flex: 1;
}

.dialog-text p {
  margin: 0 0 10px 0;
  line-height: 1.5;
}

.dialog-text ul {
  margin: 10px 0;
  padding-left: 20px;
}

.dialog-text li {
  margin-bottom: 5px;
}

.warning-text {
  color: #e6a23c;
  font-weight: bold;
  margin-top: 15px !important;
}

.dialog-footer {
  display: flex;
  gap: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-container {
    padding: 10px;
  }

  .exam-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .question-buttons {
    justify-content: center;
  }

  .question-actions {
    flex-direction: column;
    gap: 15px;
  }

  .nav-buttons,
  .submit-area {
    width: 100%;
    justify-content: center;
  }
}
</style>