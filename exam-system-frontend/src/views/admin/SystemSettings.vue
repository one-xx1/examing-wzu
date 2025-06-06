<template>
  <div class="system-settings">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统设置</span>
        </div>
      </template>

      <el-form 
        ref="formRef" 
        :model="settingsForm" 
        :rules="rules" 
        label-width="180px"
        class="settings-form"
      >
        <h3>基本设置</h3>
        <el-form-item label="系统名称" prop="systemName">
          <el-input v-model="settingsForm.systemName" />
        </el-form-item>
        
        <el-form-item label="系统描述">
          <el-input v-model="settingsForm.systemDescription" type="textarea" :rows="3" />
        </el-form-item>
        
        <el-form-item label="管理员邮箱" prop="adminEmail">
          <el-input v-model="settingsForm.adminEmail" />
        </el-form-item>
        
        <h3>考试设置</h3>
        <el-form-item label="默认考试时长(分钟)" prop="defaultExamDuration">
          <el-input-number v-model="settingsForm.defaultExamDuration" :min="10" :max="180" />
        </el-form-item>
        
        <el-form-item label="默认及格分数" prop="defaultPassScore">
          <el-input-number v-model="settingsForm.defaultPassScore" :min="0" :max="100" />
        </el-form-item>
        
        <el-form-item>
          <el-button type="primary" @click="saveSettings" :loading="loading">保存设置</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, saveSystemSettings } from '@/api/settings'

const loading = ref(false)

// 设置表单
const settingsForm = reactive({
  systemName: '在线考试系统',
  systemDescription: '为学生提供便捷的在线考试体验',
  adminEmail: 'admin@example.com',
  defaultExamDuration: 60,
  defaultPassScore: 60
})

// 表单校验规则
const rules = {
  systemName: [
    { required: true, message: '请输入系统名称', trigger: 'blur' }
  ],
  adminEmail: [
    { required: true, message: '请输入管理员邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  defaultExamDuration: [
    { required: true, message: '请设置默认考试时长', trigger: 'blur' }
  ],
  defaultPassScore: [
    { required: true, message: '请设置默认及格分数', trigger: 'blur' }
  ]
}

const formRef = ref(null)

// 获取系统设置
const fetchSettings = async () => {
  loading.value = true
  try {
    const response = await getSystemSettings()
    
    if (response.data) {
      Object.assign(settingsForm, response.data)
    }
  } catch (error) {
    console.error('获取系统设置失败:', error)
    ElMessage.warning('获取系统设置失败，显示默认设置')
  } finally {
    loading.value = false
  }
}

// 保存系统设置
const saveSettings = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('表单验证失败，请检查输入')
      return
    }
    
    loading.value = true
    try {
      await saveSystemSettings(settingsForm)
      
      ElMessage.success('系统设置保存成功')
    } catch (error) {
      console.error('保存系统设置失败:', error)
      ElMessage.error('保存系统设置失败')
    } finally {
      loading.value = false
    }
  })
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  fetchSettings()
}

// 页面加载时获取系统设置
onMounted(() => {
  fetchSettings()
})
</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.settings-form {
  max-width: 800px;
  margin: 0 auto;
}

h3 {
  margin-top: 30px;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
  color: #303133;
}

.el-form-item {
  margin-bottom: 22px;
}
</style>
