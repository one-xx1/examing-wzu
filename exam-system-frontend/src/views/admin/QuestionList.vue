<template>
  <div class="question-list">
    <div class="header">
      <h2>单选题管理</h2>
      <el-button type="primary" @click="handleAdd">新增单选题</el-button>
    </div>

    <el-table :data="questions" style="width: 100%" v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
      <el-table-column label="选项" width="400">
        <template #default="{ row }">
          <div class="option-item">
            <span class="option-label">A.</span>
            <span class="option-content">{{ row.optionA }}</span>
          </div>
          <div class="option-item">
            <span class="option-label">B.</span>
            <span class="option-content">{{ row.optionB }}</span>
          </div>
          <div class="option-item">
            <span class="option-label">C.</span>
            <span class="option-content">{{ row.optionC }}</span>
          </div>
          <div class="option-item">
            <span class="option-label">D.</span>
            <span class="option-content">{{ row.optionD }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="正确答案" width="100">
        <template #default="{ row }">
          <el-tag type="success">{{ row.answer }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 题目表单对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="60%"
      :close-on-click-modal="false"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="题目内容" prop="content">
          <el-input type="textarea" v-model="form.content" rows="3" placeholder="请输入题目内容" />
        </el-form-item>
        <el-form-item label="选项A" prop="optionA">
          <el-input v-model="form.optionA" placeholder="请输入选项A的内容" />
        </el-form-item>
        <el-form-item label="选项B" prop="optionB">
          <el-input v-model="form.optionB" placeholder="请输入选项B的内容" />
        </el-form-item>
        <el-form-item label="选项C" prop="optionC">
          <el-input v-model="form.optionC" placeholder="请输入选项C的内容" />
        </el-form-item>
        <el-form-item label="选项D" prop="optionD">
          <el-input v-model="form.optionD" placeholder="请输入选项D的内容" />
        </el-form-item>
        <el-form-item label="正确答案" prop="answer">
          <el-radio-group v-model="form.answer">
            <el-radio label="A">A</el-radio>
            <el-radio label="B">B</el-radio>
            <el-radio label="C">C</el-radio>
            <el-radio label="D">D</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getQuestions, createQuestion, updateQuestion, deleteQuestion } from '@/api/question'

export default {
  name: 'QuestionList',
  setup() {
    const questions = ref([])
    const loading = ref(false)
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const formRef = ref(null)
    const form = ref({
      content: '',
      optionA: '',
      optionB: '',
      optionC: '',
      optionD: '',
      answer: ''
    })
    const isEdit = ref(false)
    const currentId = ref(null)

    const rules = {
      content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }],
      optionA: [{ required: true, message: '请输入选项A', trigger: 'blur' }],
      optionB: [{ required: true, message: '请输入选项B', trigger: 'blur' }],
      optionC: [{ required: true, message: '请输入选项C', trigger: 'blur' }],
      optionD: [{ required: true, message: '请输入选项D', trigger: 'blur' }],
      answer: [{ required: true, message: '请选择正确答案', trigger: 'change' }]
    }

    const fetchQuestions = async () => {
      loading.value = true
      try {
        const response = await getQuestions()
        questions.value = response.data
      } catch (error) {
        ElMessage.error('获取题目列表失败')
      }
      loading.value = false
    }

    const resetForm = () => {
      form.value = {
        content: '',
        optionA: '',
        optionB: '',
        optionC: '',
        optionD: '',
        answer: ''
      }
      if (formRef.value) {
        formRef.value.resetFields()
      }
    }

    const handleAdd = () => {
      isEdit.value = false
      dialogTitle.value = '新增单选题'
      resetForm()
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      currentId.value = row.id
      dialogTitle.value = '编辑单选题'
      form.value = { ...row }
      dialogVisible.value = true
    }

    const handleDelete = (row) => {
      ElMessageBox.confirm('确定要删除这道题目吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteQuestion(row.id)
          ElMessage.success('删除成功')
          fetchQuestions()
        } catch (error) {
          ElMessage.error('删除失败')
        }
      })
    }

    const handleSubmit = async () => {
      if (!formRef.value) return
      
      await formRef.value.validate(async (valid) => {
        if (valid) {
          try {
            if (isEdit.value) {
              await updateQuestion(currentId.value, form.value)
              ElMessage.success('更新成功')
            } else {
              await createQuestion(form.value)
              ElMessage.success('创建成功')
            }
            dialogVisible.value = false
            fetchQuestions()
          } catch (error) {
            ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
          }
        }
      })
    }

    onMounted(() => {
      fetchQuestions()
    })

    return {
      questions,
      loading,
      dialogVisible,
      dialogTitle,
      form,
      formRef,
      rules,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit
    }
  }
}
</script>

<style scoped>
.question-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.option-item {
  display: flex;
  margin-bottom: 8px;
}

.option-label {
  font-weight: bold;
  margin-right: 8px;
  min-width: 20px;
}

.option-content {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.el-dialog__body) {
  padding-top: 20px;
}
</style> 