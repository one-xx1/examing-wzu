import request from '@/utils/request'

/**
 * 获取学生列表（分页）
 * @param {Object} params 查询参数
 * @returns {Promise} 返回学生列表数据
 */
export function getStudents(params) {
  return request({
    url: '/api/admin/students',
    method: 'get',
    params
  })
}

/**
 * 获取单个学生信息
 * @param {Number} id 学生ID
 * @returns {Promise} 返回学生信息
 */
export function getStudent(id) {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'get'
  })
}

/**
 * 创建学生
 * @param {Object} data 学生数据
 * @returns {Promise} 返回创建结果
 */
export function createStudent(data) {
  return request({
    url: '/api/admin/students',
    method: 'post',
    data
  })
}

/**
 * 更新学生信息
 * @param {Number} id 学生ID
 * @param {Object} data 学生数据
 * @returns {Promise} 返回更新结果
 */
export function updateStudent(id, data) {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除学生
 * @param {Number} id 学生ID
 * @returns {Promise} 返回删除结果
 */
export function deleteStudent(id) {
  return request({
    url: `/api/admin/students/${id}`,
    method: 'delete'
  })
}

/**
 * 重置学生密码
 * @param {Number} id 学生ID
 * @returns {Promise} 返回重置结果
 */
export function resetPassword(id) {
  return request({
    url: `/api/admin/students/${id}/reset-password`,
    method: 'post'
  })
}
