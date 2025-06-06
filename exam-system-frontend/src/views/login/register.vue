<template>
  <div class="bj">
    <div style="width: 500px;height: 450px;background-color: #7cade2;border-radius: 10px;padding: 30px;">
      <!-- 添加 label-width 统一对齐 -->
      <el-form ref="formRef" :model="data.form" :rules="data.rules" label-width="100px">
        <div style="margin-bottom: 40px;text-align: center; font-weight: bold;font-size:35px; color: white;">
          考试系统账号注册
        </div>

        <el-form-item label="用户名" prop="username">
          <el-input
              size="large"
              v-model="data.form.username"
              autocomplete="off"
              :prefix-icon="User"
              placeholder="请输入用户名"
          />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input
              size="large"
              v-model="data.form.password"
              autocomplete="off"
              :prefix-icon="Lock"
              placeholder="请输入密码"
              show-password
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input
              size="large"
              v-model="data.form.confirmPassword"
              autocomplete="off"
              :prefix-icon="Check"
              placeholder="请再次输入密码"
              show-password
          />
        </el-form-item>

        <div style="margin-bottom: 20px">
          <el-button style="width: 100%;" size="large" type="primary" @click="submitForm">注    册</el-button>
        </div>
        <el-dialog
            v-model="passwordErrorVisible"
            title="提示"
            width="30%"
        >
          <span>两次输入的密码不一致，请重新输入</span>
          <template #footer>
            <el-button @click="passwordErrorVisible = false">确定</el-button>
          </template>
        </el-dialog>
        <div style="text-align: right; margin-top: 10px;">
          已有账号？
          <router-link to="/login" style="color: #ffffff">去登录</router-link>
        </div>

      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock, Check } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

const router = useRouter()
const formRef = ref(null)
const passwordErrorVisible = ref(false)

const data = reactive({
  form: {
    username: '',
    password: '',
    confirmPassword: ''
  },
  rules: {
    username: [
      { required: true, message: '请输入用户名', trigger: 'blur' },
      { min: 3, message: '用户名长度不能小于3个字符', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' },
      { min: 6, message: '密码长度不能小于6个字符', trigger: 'blur' }
    ],
    confirmPassword: [
      { required: true, message: '请再次输入密码', trigger: 'blur' },
      {
        validator: (rule, value, callback) => {
          if (value !== data.form.password) {
            passwordErrorVisible.value = true
            callback(new Error('两次输入密码不一致'))
          } else {
            callback()
          }
        },
        trigger: 'blur'
      }
    ]
  }
})

const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    
    // 检查两次密码是否一致
    if (data.form.password !== data.form.confirmPassword) {
      passwordErrorVisible.value = true
      return
    }

    // 发送注册请求
    await request({
      url: '/auth/register',
      method: 'post',
      data: {
        username: data.form.username,
        password: data.form.password
      }
    })

    ElMessage.success('注册成功')
    router.push('/login')
  } catch (error) {
    console.error('注册错误:', error)
    let errorMessage = '注册失败'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          errorMessage = error.response.data?.message || '注册信息有误'
          break
        case 409:
          errorMessage = '用户名已存在'
          break
        default:
          errorMessage = error.response.data?.message || '注册失败，请稍后重试'
    }
    }
    ElMessage.error(errorMessage)
  }
}
</script>

<style scoped>
.bj {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url("@/assets/img/bj2.png");
  background-size: cover;
  background-position: center;
}

/* 表单容器 */
.login-container {
  width: 500px;
  padding: 40px;
  background-color: #7cade2;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

/* 修改 label 标签颜色 */
:deep(.el-form-item__label) {
  color: white;
  font-size: 16px;
  background-color: #7cade2;
}

/* 修改输入框 placeholder 文字颜色 */
:deep(.el-input__inner::placeholder) {
  color: #433434;
}

/* 修改输入框样式 */
:deep(.el-input__inner) {
  background-color: rgba(255, 255, 255, 0.8);
  color: #333;
  border-radius: 8px;
  padding-left: 40px;
  transition: all 0.3s ease;
}

/* 输入框聚焦时效果 */
:deep(.el-input__inner:focus) {
  border-color: #ffffff;
  background-color: #fff;
}

/* 按钮样式 */
:deep(.el-button--primary span) {
  color: white;
}

:deep(.el-button--primary) {
  background-color: #2a9efb;
  border-color: #2a9efb;
  transition: all 0.3s ease-in-out;
}

:deep(.el-button--primary:hover) {
  background-color: #1c8df4;
  border-color: #1c8df4;
}
</style>