<template>
  <div class="exam-result-container">
    <el-card class="result-card">
      <template #header>
        <div class="card-header">
          <span>考试结果</span>
          <el-button @click="goBack" type="primary" size="small">
            返回考试列表
          </el-button>
        </div>
      </template>
      
      <div v-if="loading" class="loading">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span>加载中...</span>
      </div>
      
      <div v-else-if="examResult" class="result-content">
        <!-- 考试基本信息 -->
        <div class="exam-info">
          <h2>{{ examResult.examTitle }}</h2>
          <div class="info-grid">
            <div class="info-item">
              <span class="label">考试得分:</span>
              <span class="score" :class="getScoreClass(examResult.score)">
                {{ examResult.score }} / {{ examResult.totalScore }}
              </span>
            </div>
            <div class="info-item">
              <span class="label">考试时长:</span>
              <span class="value">{{ formatDuration(examResult.duration) }}</span>
            </div>
            <div class="info-item">
              <span class="label">提交时间:</span>
              <span class="value">{{ formatDateTime(examResult.submittedAt) }}</span>
            </div>
            <div class="info-item">
              <span class="label">答题情况:</span>
              <span class="value">
                {{ correctCount }} / {{ examResult.answers.length }} 题正确
              </span>
            </div>
          </div>
        </div>
        
        <!-- 答题详情 -->
        <div class="answers-section">
          <h3>答题详情</h3>
          <div class="answer-list">
            <div 
              v-for="(answer, index) in examResult.answers" 
              :key="answer.questionId"
              class="answer-item"
              :class="{ 'correct': answer.isCorrect, 'incorrect': !answer.isCorrect }"
            >
              <div class="question-header">
                <span class="question-number">第 {{ index + 1 }} 题</span>
                <el-tag :type="answer.isCorrect ? 'success' : 'danger'" size="small">
                  {{ answer.isCorrect ? '正确' : '错误' }}
                </el-tag>
              </div>
              
              <div class="question-content">
                <p class="question-text">{{ answer.question }}</p>
                
                <div class="options">
                  <div 
                    v-for="(option, optIndex) in getOptions(answer)" 
                    :key="optIndex"
                    class="option"
                    :class="{
                      'selected': option === answer.selectedAnswer,
                      'correct': option === answer.correctAnswer,
                      'wrong': option === answer.selectedAnswer && option !== answer.correctAnswer
                    }"
                  >
                    <span class="option-label">{{ String.fromCharCode(65 + optIndex) }}.</span>
                    <span class="option-text">{{ option }}</span>
                    <span v-if="option === answer.selectedAnswer" class="mark selected-mark">您的答案</span>
                    <span v-if="option === answer.correctAnswer" class="mark correct-mark">正确答案</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div v-else class="error">
        <el-result
          icon="error"
          title="加载失败"
          sub-title="无法获取考试结果，请稍后重试"
        >
          <template #extra>
            <el-button type="primary" @click="fetchResult">重新加载</el-button>
          </template>
        </el-result>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const examResult = ref(null)

// 正确答案数量
const correctCount = computed(() => {
  if (!examResult.value || !examResult.value.answers) return 0
  return examResult.value.answers.filter(answer => answer.isCorrect).length
})

// 获取考试结果
const fetchResult = async () => {
  const resultId = route.params.resultId
  if (!resultId) {
    ElMessage.error('缺少考试结果ID')
    goBack()
    return
  }
  
  loading.value = true
  try {
    const response = await request({
      url: `/student/results/${resultId}`,
      method: 'get'
    })
    
    examResult.value = response
  } catch (error) {
    console.error('获取考试结果失败:', error)
    ElMessage.error('获取考试结果失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '0分0秒'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
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
    minute: '2-digit',
    second: '2-digit'
  })
}

// 获取分数样式类
const getScoreClass = (score) => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
}

// 获取选项数组
const getOptions = (answer) => {
  return [answer.optionA, answer.optionB, answer.optionC, answer.optionD]
}

// 返回考试列表
const goBack = () => {
  router.push('/student/exam')
}

// 组件挂载时获取结果
onMounted(() => {
  fetchResult()
})
</script>

<style scoped>
.exam-result-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.result-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.loading {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 50px;
  color: #909399;
}

.result-content {
  padding: 20px 0;
}

.exam-info {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.exam-info h2 {
  margin: 0 0 20px 0;
  color: #303133;
  text-align: center;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
}

.label {
  font-weight: bold;
  color: #606266;
}

.value {
  color: #303133;
}

.score {
  font-size: 18px;
  font-weight: bold;
}

.score.excellent {
  color: #67c23a;
}

.score.good {
  color: #409eff;
}

.score.pass {
  color: #e6a23c;
}

.score.fail {
  color: #f56c6c;
}

.answers-section {
  margin-top: 30px;
}

.answers-section h3 {
  margin: 0 0 20px 0;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 10px;
}

.answer-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.answer-item {
  padding: 20px;
  border: 2px solid #e4e7ed;
  border-radius: 8px;
  background: white;
}

.answer-item.correct {
  border-color: #67c23a;
  background: #f0f9ff;
}

.answer-item.incorrect {
  border-color: #f56c6c;
  background: #fef0f0;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-number {
  font-weight: bold;
  color: #303133;
}

.question-content {
  margin-top: 15px;
}

.question-text {
  margin: 0 0 15px 0;
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  padding: 15px;
  background: #fafafa;
  border-left: 4px solid #409eff;
  border-radius: 4px;
}

.options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: white;
  position: relative;
}

.option.selected {
  border-color: #409eff;
  background: #ecf5ff;
}

.option.correct {
  border-color: #67c23a;
  background: #f0f9ff;
}

.option.wrong {
  border-color: #f56c6c;
  background: #fef0f0;
}

.option-label {
  font-weight: bold;
  color: #409eff;
  min-width: 20px;
}

.option-text {
  flex: 1;
  color: #303133;
}

.mark {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 4px;
  font-weight: bold;
}

.selected-mark {
  background: #409eff;
  color: white;
}

.correct-mark {
  background: #67c23a;
  color: white;
}

.error {
  padding: 50px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .exam-result-container {
    padding: 10px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .info-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>
