import request from '@/utils/request'

export function getStudents(params) {
  return request({
    url: '/admin/students',
    method: 'get',
    params
  })
}

export function getStudent(id) {
  return request({
    url: `/admin/students/${id}`,
    method: 'get'
  })
}

export function createStudent(data) {
  return request({
    url: '/admin/students',
    method: 'post',
    data
  })
}

export function updateStudent(id, data) {
  return request({
    url: `/admin/students/${id}`,
    method: 'put',
    data
  })
}

export function deleteStudent(id) {
  return request({
    url: `/admin/students/${id}`,
    method: 'delete'
  })
}

export function resetPassword(id) {
  return request({
    url: `/admin/students/${id}/reset-password`,
    method: 'post'
  })
}
