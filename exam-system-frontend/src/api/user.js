// src/api/user.js
import request from '@/utils/request'

export function verifyCode(code) {
    return request({
        url: '/auths/verify-code',
        method: 'post',
        data: { code }
    })
}
