import request from '@/utils/request'

// 管理员接口
export function getAdminQuestions(params) {
  return request({
    url: '/admin/questions',
    method: 'get',
    params
  })
}

export function getAdminQuestion(id) {
  return request({
    url: `/admin/questions/${id}`,
    method: 'get'
  })
}

export function createAdminQuestion(data) {
  return request({
    url: '/admin/questions',
    method: 'post',
    data
  })
}

export function updateAdminQuestion(id, data) {
  return request({
    url: `/admin/questions/${id}`,
    method: 'put',
    data
  })
}

export function deleteAdminQuestion(id) {
  return request({
    url: `/admin/questions/${id}`,
    method: 'delete'
  })
}

// 学生接口
export function getQuestions() {
  return request({
    url: '/questions',
    method: 'get'
  })
}

export function getQuestion(id) {
  return request({
    url: `/questions/${id}`,
    method: 'get'
  })
}