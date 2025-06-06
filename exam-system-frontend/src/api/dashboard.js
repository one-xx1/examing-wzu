import request from '@/utils/request'

// 获取仪表盘统计数据
export function getDashboardStats() {
  return request({
    url: '/api/dashboard/stats',
    method: 'get'
  })
}

// 获取考试统计数据
export function getExamStats() {
  return request({
    url: '/api/dashboard/exam-stats',
    method: 'get'
  })
} 