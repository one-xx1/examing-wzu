<template>
  <div class="question-management">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>题目管理</span>
          <el-button type="primary" @click="handleAdd">新增题目</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="科目">
          <el-input v-model="searchForm.subject" placeholder="请输入科目" clearable />
        </el-form-item>
        <el-form-item label="题型">
          <el-select v-model="searchForm.type" placeholder="请选择题型" clearable>
            <el-option label="单选题" value="SINGLE_CHOICE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table
          v-loading="loading"
          :data="questionList"
          style="width: 100%"
          border
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="subject" label="科目" width="120" />
        <el-table-column prop="type" label="题型" width="100">
          <template #default="{ row }">
            <el-tag :type="getQuestionTypeTag(row.type)">
              {{ getQuestionTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="score" label="分值" width="80" />
        <el-table-column prop="difficulty" label="难度" width="120">
          <template #default="{ row }">
            <el-rate
                v-model="row.difficulty"
                disabled
                show-score
                text-color="#ff9900"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button-group>
              <el-button
                  type="primary"
                  size="small"
                  @click="handleEdit(row)"
              >
                编辑
              </el-button>
              <el-button
                  type="danger"
                  size="small"
                  @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑题目对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogType === 'add' ? '新增题目' : '编辑题目'"
        width="60%"
    >
      <el-form
          ref="formRef"
          :model="questionForm"
          :rules="rules"
          label-width="100px"
          @submit.prevent="submitForm"
      >
        <el-form-item label="所属考试" prop="examId">
          <el-select v-model="questionForm.examId" placeholder="请选择考试" style="width: 100%">
            <el-option 
              v-for="exam in examList" 
              :key="exam.id" 
              :label="exam.title" 
              :value="exam.id" 
            />
          </el-select>
        </el-form-item>

        <el-form-item label="题目内容" prop="content">
          <el-input
              v-model="questionForm.content"
              type="textarea"
              :rows="4"
              placeholder="请输入题目内容"
          />
        </el-form-item>

        <el-form-item label="选项A" prop="optionA">
          <el-input v-model="questionForm.optionA" placeholder="请输入选项A" />
        </el-form-item>

        <el-form-item label="选项B" prop="optionB">
          <el-input v-model="questionForm.optionB" placeholder="请输入选项B" />
        </el-form-item>

        <el-form-item label="选项C" prop="optionC">
          <el-input v-model="questionForm.optionC" placeholder="请输入选项C" />
        </el-form-item>

        <el-form-item label="选项D" prop="optionD">
          <el-input v-model="questionForm.optionD" placeholder="请输入选项D" />
        </el-form-item>

        <el-form-item label="正确答案" prop="answer">
          <el-radio-group v-model="questionForm.answer">
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
          <el-button type="primary" @click="submitForm">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getAdminQuestions, createAdminQuestion, updateAdminQuestion, deleteAdminQuestion } from '@/api/question'
import { getExams } from '@/api/exam'

// 状态定义
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add') // 'add' 或 'edit'
const formRef = ref(null)
const questionForm = ref({
  examId: '',
  content: '',
  optionA: '',
  optionB: '',
  optionC: '',
  optionD: '',
  answer: 'A'
})
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const questionList = ref([])
const examList = ref([])

// 搜索表单
const searchForm = reactive({
  subject: '',
  type: 'SINGLE_CHOICE'  // 默认只查询单选题
})

// 表单校验规则
const rules = {
  examId: [
    { required: true, message: '请选择所属考试', trigger: 'change' }
  ],
  content: [
    { required: true, message: '请输入题目内容', trigger: 'blur' }
  ],
  optionA: [
    { required: true, message: '请输入选项A', trigger: 'blur' }
  ],
  optionB: [
    { required: true, message: '请输入选项B', trigger: 'blur' }
  ],
  optionC: [
    { required: true, message: '请输入选项C', trigger: 'blur' }
  ],
  optionD: [
    { required: true, message: '请输入选项D', trigger: 'blur' }
  ],
  answer: [
    { required: true, message: '请选择正确答案', trigger: 'change' }
  ]
}

// 获取题目列表
const fetchQuestions = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1, // Spring Data JPA 分页从0开始
      size: pageSize.value,
      type: 'SINGLE_CHOICE'
    }
    const response = await getAdminQuestions(params)
    console.log('获取题目列表响应:', response)
    
    // 处理 Spring Boot 分页响应
    // axios 拦截器已经提取了 response.data
    if (response && response.content) {
      questionList.value = response.content
      total.value = response.totalElements
    } else {
      questionList.value = []
      total.value = 0
    }
  } catch (error) {
    console.error('获取题目列表失败:', error)
    ElMessage.error('获取题目列表失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 题型标签样式
const getQuestionTypeTag = (type) => {
  return type === 'SINGLE_CHOICE' ? 'success' : ''
}

// 题型文本
const getQuestionTypeText = (type) => {
  return '单选题'
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchQuestions()
}

// 重置搜索
const resetSearch = () => {
  searchForm.subject = ''
  searchForm.type = 'SINGLE_CHOICE'
  handleSearch()
}

// 处理分页
const handleSizeChange = (val) => {
  pageSize.value = val
  fetchQuestions()
}

const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchQuestions()
}

// 新增题目
const handleAdd = async () => {
  dialogType.value = 'add'
  
  // 确保考试列表已加载
  if (examList.value.length === 0) {
    try {
      loading.value = true
      await fetchExams()
    } catch (error) {
      console.error('加载考试列表失败:', error)
      ElMessage.error('加载考试列表失败')
      return
    } finally {
      loading.value = false
    }
  }
  
  // 重置表单
  questionForm.value = {
    examId: examList.value.length > 0 ? examList.value[0].id : '',
    content: '',
    optionA: '',
    optionB: '',
    optionC: '',
    optionD: '',
    answer: 'A'
  }
  
  // 等待DOM更新后再打开对话框
  nextTick(() => {
    dialogVisible.value = true
  })
}

// 编辑题目
const handleEdit = (row) => {
  dialogType.value = 'edit'
  questionForm.value = { ...row }
  dialogVisible.value = true
}

// 删除题目
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此题目吗?', '提示', {
      type: 'warning'
    })
    await deleteAdminQuestion(row.id)
    ElMessage.success('删除成功')
    fetchQuestions()
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  
  try {
    // 表单验证
    const valid = await formRef.value.validate()
    if (!valid) {
      console.log('表单验证不通过')
      return
    }
    
    // 准备提交数据
    const formData = {
      examId: questionForm.value.examId,
      content: questionForm.value.content,
      optionA: questionForm.value.optionA,
      optionB: questionForm.value.optionB,
      optionC: questionForm.value.optionC,
      optionD: questionForm.value.optionD,
      answer: questionForm.value.answer
    }
    
    console.log('提交的题目数据:', formData)
    
    // 显示加载中
    loading.value = true

    if (dialogType.value === 'add') {
      const response = await createAdminQuestion(formData)
      console.log('创建题目响应:', response)
      ElMessage.success('添加成功')
    } else {
      formData.id = questionForm.value.id
      const response = await updateAdminQuestion(questionForm.value.id, formData)
      console.log('更新题目响应:', response)
      ElMessage.success('更新成功')
    }
    dialogVisible.value = false
    fetchQuestions()
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('操作失败: ' + (error.response?.data?.message || error.message))
  } finally {
    loading.value = false
  }
}

// 获取所有考试
const fetchExams = async () => {
  try {
    const response = await getExams()
    console.log('获取到的考试列表原始响应:', response)
    
    // 处理 Spring Boot 响应
    if (Array.isArray(response)) {
      examList.value = response
    } else if (response && Array.isArray(response.content)) {
      examList.value = response.content
    } else {
      examList.value = []
    }
    
    console.log('处理后的考试列表:', examList.value)
    
    return examList.value
  } catch (error) {
    console.error('获取考试列表失败:', error)
    ElMessage.error('获取考试列表失败: ' + (error.response?.data?.message || error.message))
    return []
  }
}

// 页面加载时获取题目列表和考试列表
onMounted(() => {
  fetchQuestions()
  fetchExams()
})
</script>

<style scoped>
.option-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
</style>
