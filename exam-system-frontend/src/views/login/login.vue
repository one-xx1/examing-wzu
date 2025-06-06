<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <h2 class="login-title">在线考试系统</h2>
      </template>
      
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="rules"
        label-width="0"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
          >
            <template #prefix>
              <el-icon><UserFilled /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="role">
          <el-radio-group v-model="loginForm.role" class="role-select">
            <el-radio label="STUDENT">学生</el-radio>
            <el-radio label="ADMIN">管理员</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>

        <div class="register-link">
          还没有账号？
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Lock } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loginFormRef = ref(null)
const loginForm = ref({
  username: '',
  password: '',
  role: 'STUDENT'
})

const loading = ref(false)

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        loading.value = true
        const response = await request({
          url: '/auth/login',
          method: 'post',
          data: loginForm.value
        })

        // 保存登录信息
        localStorage.setItem('token', response.token)
        localStorage.setItem('role', response.role)
        localStorage.setItem('username', response.username)
        
        ElMessage.success('登录成功')
        
        // 根据角色跳转到对应页面
        if (response.role === 'ADMIN') {
          router.push('/admin/dashboard')
        } else if (response.role === 'STUDENT') {
          router.push('/student/dashboard')
        }
      } catch (error) {
        console.error('登录错误:', error)
        let errorMessage = '登录失败'
        if (error.response) {
          switch (error.response.status) {
            case 401:
              errorMessage = '用户名或密码错误'
              break
            case 403:
              errorMessage = '用户角色不匹配'
              break
            default:
              errorMessage = error.response.data?.message || '登录失败，请稍后重试'
          }
        }
        ElMessage.error(errorMessage)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-image: url("@/assets/img/bj1.png");
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 400px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.9);
}

.login-title {
  text-align: center;
  color: #303133;
  margin: 0;
  font-size: 24px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
}

.role-select {
  width: 100%;
  display: flex;
  justify-content: center;
  gap: 30px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #606266;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
}

.register-link a:hover {
  color: #66b1ff;
}

:deep(.el-input__wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-input__inner) {
  height: 40px;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-radio__label) {
  font-size: 14px;
}

:deep(.el-input__prefix) {
  margin-right: 8px;
}
</style>