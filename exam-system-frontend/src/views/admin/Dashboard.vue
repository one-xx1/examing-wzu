<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>题目总数</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.totalQuestions">
              <template #suffix>
                <span>题</span>
              </template>
            </el-statistic>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="stat-card">
          <template #header>
            <div class="card-header">
              <span>考试总数</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.totalExams">
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
              <span>学生总数</span>
            </div>
          </template>
          <div class="stat-value">
            <el-statistic :value="stats.totalStudents">
              <template #suffix>
                <span>人</span>
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
              <span>考试成绩分布</span>
              <el-radio-group v-model="scoreChartPeriod" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
                <el-radio-button label="year">本年</el-radio-button>
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
              <span>题型分布</span>
            </div>
          </template>
          <div class="chart-container">
            <div ref="questionTypeChartRef" class="chart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近考试列表 -->
    <el-card class="recent-exams">
      <template #header>
        <div class="card-header">
          <span>最近考试</span>
          <el-button type="primary" link>查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentExams" style="width: 100%">
        <el-table-column prop="name" label="考试名称" />
        <el-table-column prop="date" label="考试日期" width="180" />
        <el-table-column prop="duration" label="考试时长" width="120" />
        <el-table-column prop="totalStudents" label="参考人数" width="120" />
        <el-table-column prop="averageScore" label="平均分" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === '进行中' ? 'success' : row.status === '已结束' ? 'info' : 'warning'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 最近添加的题目 -->
    <el-card class="recent-questions">
      <template #header>
        <div class="card-header">
          <span>最近添加的题目</span>
          <el-button type="primary" @click="router.push('/admin/questions')">
            查看全部
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="recentQuestions"
        style="width: 100%"
      >
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="type" label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="getQuestionTypeTag(row.type)">
              {{ getQuestionTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="题目内容" show-overflow-tooltip />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="difficulty" label="难度" width="120">
          <template #default="{ row }">
            <el-rate
              v-model="row.difficulty"
              disabled
              show-score
              text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click="router.push(`/admin/questions?edit=${row.id}`)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { User, Document, EditPen, DataLine } from '@element-plus/icons-vue'
import CountTo from 'vue-count-to'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)

// 统计数据
const stats = ref({
  totalQuestions: 0,
  totalExams: 0,
  totalStudents: 0
})

// 图表相关
const scoreChartRef = ref(null)
const questionTypeChartRef = ref(null)
const scoreChartPeriod = ref('month')

// 最近考试数据
const recentExams = ref([
  {
    name: '2024春季高等数学期末考试',
    date: '2024-01-15 14:00',
    duration: '120分钟',
    totalStudents: 120,
    averageScore: 85,
    status: '已结束'
  },
  {
    name: '2024春季Java程序设计期中考试',
    date: '2024-01-20 10:00',
    duration: '90分钟',
    totalStudents: 90,
    averageScore: 78,
    status: '进行中'
  },
  {
    name: '2024春季数据结构期末考试',
    date: '2024-02-01 09:00',
    duration: '120分钟',
    totalStudents: 150,
    status: '未开始'
  }
])

// 最近添加的题目
const recentQuestions = ref([])

// 获取统计数据
const fetchStats = async () => {
  try {
    const response = await fetch('/api/admin/stats')
    const data = await response.json()
    stats.value = data
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取最近添加的题目
const fetchRecentQuestions = async () => {
  loading.value = true
  try {
    const response = await fetch('/api/admin/questions/recent')
    const data = await response.json()
    recentQuestions.value = data
  } catch (error) {
    console.error('获取最近题目失败:', error)
  } finally {
    loading.value = false
  }
}

// 题型标签样式
const getQuestionTypeTag = (type) => {
  const types = {
    SINGLE_CHOICE: '',
    MULTIPLE_CHOICE: 'success',
    TRUE_FALSE: 'info',
    FILL_BLANK: 'warning',
    SHORT_ANSWER: 'danger'
  }
  return types[type] || ''
}

// 题型文本
const getQuestionTypeText = (type) => {
  const types = {
    SINGLE_CHOICE: '单选题',
    MULTIPLE_CHOICE: '多选题',
    TRUE_FALSE: '判断题',
    FILL_BLANK: '填空题',
    SHORT_ANSWER: '简答题'
  }
  return types[type] || '未知类型'
}

// 初始化成绩分布图表
const initScoreChart = () => {
  const chart = echarts.init(scoreChartRef.value)
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['优秀', '良好', '及格', '不及格']
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
      type: 'value'
    },
    series: [
      {
        name: '优秀',
        type: 'line',
        stack: 'Total',
        data: [12, 13, 10, 13, 9, 23, 21]
      },
      {
        name: '良好',
        type: 'line',
        stack: 'Total',
        data: [22, 18, 19, 23, 29, 33, 31]
      },
      {
        name: '及格',
        type: 'line',
        stack: 'Total',
        data: [15, 17, 20, 19, 17, 13, 15]
      },
      {
        name: '不及格',
        type: 'line',
        stack: 'Total',
        data: [2, 3, 3, 1, 2, 2, 1]
      }
    ]
  }
  chart.setOption(option)
}

// 初始化题型分布图表
const initQuestionTypeChart = () => {
  const chart = echarts.init(questionTypeChartRef.value)
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
        name: '题型分布',
        type: 'pie',
        radius: '50%',
        data: [
          { value: 1048, name: '单选题' },
          { value: 735, name: '多选题' },
          { value: 580, name: '判断题' },
          { value: 484, name: '填空题' },
          { value: 300, name: '简答题' }
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
  initQuestionTypeChart()
  fetchStats()
  fetchRecentQuestions()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stat-card {
  height: 100%;
}

.stat-value {
  text-align: center;
  padding: 20px 0;
}

.chart-row {
  margin-top: 20px;
}

.chart-card {
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

.recent-exams {
  margin-top: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recent-questions {
  margin-top: 20px;
}

:deep(.el-card__header) {
  padding: 15px 20px;
  border-bottom: 1px solid #EBEEF5;
}
</style>
