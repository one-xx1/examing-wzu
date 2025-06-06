import request from '@/utils/request'

export function getExams() {
  return request({
    url: '/admin/exams',
    method: 'get'
  })
}

export function createExam(data) {
  return request({
    url: '/admin/exams',
    method: 'post',
    data
  })
}

export function updateExam(id, data) {
  return request({
    url: `/admin/exams/${id}`,
    method: 'put',
    data
  })
}

export function deleteExam(id) {
  return request({
    url: `/admin/exams/${id}`,
    method: 'delete'
  })
}

export function updateExamStatus(id, data) {
  return request({
    url: `/admin/exams/${id}/status`,
    method: 'put',
    data
  })
}
