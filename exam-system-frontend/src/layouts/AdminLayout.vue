<template>
  <el-container class="layout-container">
    <el-aside width="200px">
      <el-menu
        :default-active="route.path"
        class="el-menu-vertical"
        :router="true"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>管理员首页</span>
        </el-menu-item>
        
        <el-sub-menu index="1">
          <template #title>
            <el-icon><Document /></el-icon>
            <span>考试管理</span>
          </template>
          <el-menu-item index="/admin/exams">
            <el-icon><List /></el-icon>
            <span>考试列表</span>
          </el-menu-item>
          <el-menu-item index="/admin/exam-results">
            <el-icon><DataAnalysis /></el-icon>
            <span>考试成绩</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/admin/questions">
          <el-icon><Files /></el-icon>
          <span>题目管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/students">
          <el-icon><User /></el-icon>
          <span>学生管理</span>
        </el-menu-item>
        
        <el-menu-item index="/admin/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-left">
          <el-icon
            class="fold-icon"
            @click="isCollapse = !isCollapse"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <span class="header-title">在线考试系统 - 管理端</span>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import {
  Document,
  Odometer,
  Fold,
  Expand,
  UserFilled,
  List,
  DataAnalysis,
  Files,
  User,
  Setting
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const isCollapse = ref(false)
const username = ref(localStorage.getItem('username') || '管理员')

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确认退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      
      localStorage.removeItem('token')
      localStorage.removeItem('role')
      localStorage.removeItem('username')
      router.push('/login')
    } catch {
      // 用户取消操作
    }
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-aside {
  background-color: #304156;
  transition: width 0.3s;
}

.el-menu {
  border-right: none;
}

.el-header {
  background-color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
}

.fold-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin-left: 8px;
  font-size: 14px;
  color: #606266;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style> 