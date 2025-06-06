<template>
  <div class="dashboard-container">
    <!-- 欢迎卡片 -->
    <el-card class="welcome-card">
      <div class="welcome-content">
        <el-avatar :size="64" :src="studentInfo.avatar" />
        <div class="welcome-text">
          <h2>欢迎回来，{{ username }}</h2>
          <p>今天是你在线学习的第 {{ studentInfo.onlineDays }} 天</p>
        </div>
        <div class="study-time">
          <div class="time-item">
            <div class="time-value">{{ formatTime(studentInfo.todayStudyTime) }}</div>
            <div class="time-label">今日学习时长</div>
          </div>
          <div class="time-item">
            <div class="time-value">{{ formatTime(studentInfo.totalStudyTime) }}</div>
            <div class="time-label">总学习时长</div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>待考试数</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.pendingExams">
              <template #suffix>
                <span>场</span>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>已完成考试</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.completedExams">
              <template #suffix>
                <span>场</span>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>平均分数</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.averageScore" :precision="1">
              <template #suffix>
                <span>分</span>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>成绩趋势</span>
              <el-radio-group v-model="chartPeriod" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="semester">本学期</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <div ref="scoreChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <div class="chart-header">
              <span>知识点掌握情况</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="knowledgeChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近考试记录 -->
    <el-card class="exam-history">
      <template #header>
        <div class="card-header">
          <span>近期考试</span>
          <el-button type="primary" @click="router.push('/student/exam')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table
        v-loading="loading"
        :data="upcomingExams"
        style="width: 100%"
      >
        <el-table-column prop="title" label="考试名称" />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="startTime" label="开始时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="考试时长" width="120">
          <template #default="{ row }">
            {{ row.duration }} 分钟
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
              @click="router.push('/student/exam')"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Document, Timer, Warning } from '@element-plus/icons-vue'
import CountTo from 'vue-count-to'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '同学')
const chartPeriod = ref('month')
const loading = ref(false)

// 学生信息
const studentInfo = reactive({
  avatar: 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png',
  onlineDays: 1,
  todayStudyTime: 7200, // 秒
  totalStudyTime: 3600 // 秒
})

// 统计数据
const stats = ref({
  pendingExams: 0,
  completedExams: 0,
  averageScore: 0
})

// 考试历史
const examHistory = ref([
  {
    name: '高等数学期末考试',
    date: '2024-01-15',
    score: 88,
    rank: '12/150',
    status: '已完成'
  },
  {
    name: 'Java程序设计期中考试',
    date: '2024-01-10',
    score: 92,
    rank: '5/120',
    status: '已完成'
  },
  {
    name: '数据结构期末考试',
    date: '2024-02-01',
    status: '待考试'
  }
])

// 图表引用
const scoreChartRef = ref(null)
const knowledgeChartRef = ref(null)

// 近期考试列表
const upcomingExams = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    // 获取考试结果统计
    const resultsResponse = await request({
      url: '/student/results',
      method: 'get',
      params: { page: 0, size: 100 }
    })

    const results = resultsResponse.content || []
    stats.value.completedExams = results.length

    if (results.length > 0) {
      const scores = results.map(r => r.score)
      stats.value.averageScore = Math.round(scores.reduce((a, b) => a + b, 0) / scores.length)
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取近期考试
const fetchUpcomingExams = async () => {
  loading.value = true
  try {
    const response = await request({
      url: '/student/exams',
      method: 'get',
      params: { page: 0, size: 5 }
    })

    upcomingExams.value = response.content || []
    stats.value.pendingExams = response.totalElements || 0
  } catch (error) {
    console.error('获取近期考试失败:', error)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (seconds) => {
  const hours = Math.floor(seconds / 3600)
  const minutes = Math.floor((seconds % 3600) / 60)
  return `${hours}小时${minutes}分钟`
}

// 格式化日期时间
const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return ''
  const date = new Date(dateTimeStr)
  return date.toLocaleString()
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = {
    NOT_STARTED: 'info',
    IN_PROGRESS: 'warning',
    FINISHED: 'success',
    EXPIRED: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    NOT_STARTED: '未开始',
    IN_PROGRESS: '进行中',
    FINISHED: '已完成',
    EXPIRED: '已过期'
  }
  return texts[status] || '未知状态'
}

// 初始化成绩趋势图表
const initScoreChart = () => {
  const chart = echarts.init(scoreChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: {
      type: 'value',
      min: 0,
      max: 100
    },
    series: [
      {
        name: '成绩',
        type: 'line',
        data: [85, 90, 78, 95, 88, 92, 85],
        smooth: true,
        lineStyle: {
          width: 3,
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: 'rgba(64,158,255,0.3)'
              },
              {
                offset: 1,
                color: 'rgba(64,158,255,0.1)'
              }
            ]
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

// 初始化知识点掌握情况图表
const initKnowledgeChart = () => {
  const chart = echarts.init(knowledgeChartRef.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '知识点掌握',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 35, name: '已掌握' },
          { value: 15, name: '待巩固' },
          { value: 10, name: '需加强' }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }
  chart.setOption(option)
}

onMounted(() => {
  initScoreChart()
  initKnowledgeChart()
  fetchStats()
  fetchUpcomingExams()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.welcome-text {
  margin-left: 24px;
  flex: 1;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.welcome-text p {
  margin: 0;
  color: #909399;
}

.study-time {
  display: flex;
  gap: 40px;
}

.time-item {
  text-align: center;
}

.time-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 4px;
}

.time-label {
  color: #909399;
  font-size: 14px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  height: 100%;
}

.stat-value {
  text-align: center;
  padding: 20px 0;
}

.chart-row {
  margin-bottom: 20px;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 350px;
}

.chart {
  width: 100%;
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exam-history {
  margin-top: 20px;
}

.score {
  font-weight: bold;
}

.score.pass {
  color: #67C23A;
}

.score.fail {
  color: #F56C6C;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
}
</style>
