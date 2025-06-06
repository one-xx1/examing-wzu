import request from '@/utils/request'

/**
 * 获取系统设置
 * @returns {Promise} 返回系统设置数据
 */
export function getSystemSettings() {
  return request({
    url: '/admin/settings',
    method: 'get'
  })
}

/**
 * 保存系统设置
 * @param {Object} data 系统设置数据
 * @returns {Promise} 返回保存结果
 */
export function saveSystemSettings(data) {
  return request({
    url: '/admin/settings',
    method: 'post',
    data
  })
}
