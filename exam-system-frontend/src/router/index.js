import { createRouter, createWebHistory } from 'vue-router'

// 通用页面
import Login from '@/views/login/login.vue'
import Register from '@/views/login/register.vue'
import NotFound from '@/views/404/error.vue'

// 管理员端
import AdminLayout from '@/layouts/AdminLayout.vue'
import StudentLayout from '@/layouts/StudentLayout.vue'

// 学生端
import StudentExam from '@/views/student/StudentExam.vue'


const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/login/register.vue')
  },
  {
    path: '/admin',
    component: () => import('@/layouts/AdminLayout.vue'),
    meta: { requiresAuth: true, role: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'questions',
        name: 'QuestionManagement',
        component: () => import('@/views/admin/QuestionManagement.vue'),
        meta: {
          requiresAuth: true,
          role: 'ADMIN'
        }
      },
      {
        path: 'exams',
        name: 'ExamManagement',
        component: () => import('@/views/admin/ExamManagement.vue'),
        meta: {
          requiresAuth: true,
          role: 'ADMIN'
        }
      },
      {
        path: 'exam-results',
        name: 'ExamResults',
        component: () => import('@/views/admin/ExamResults.vue'),
        meta: {
          requiresAuth: true,
          role: 'ADMIN'
        }
      },
      {
        path: 'students',
        name: 'StudentManagement',
        component: () => import('@/views/admin/StudentManagement.vue'),
        meta: {
          requiresAuth: true,
          role: 'ADMIN'
        }
      },
      {
        path: 'settings',
        name: 'SystemSettings',
        component: () => import('@/views/admin/SystemSettings.vue'),
        meta: {
          requiresAuth: true,
          role: 'ADMIN'
        }
      }
    ]
  },
  {
    path: '/student',
    component: () => import('@/layouts/StudentLayout.vue'),
    meta: { requiresAuth: true, role: 'STUDENT' },
    children: [
      {
        path: 'dashboard',
        name: 'StudentDashboard',
        component: () => import('@/views/student/Dashboard.vue')
      },
      {
        path: 'exam',
        name: 'StudentExam',
        component: () => import('@/views/student/StudentExam.vue')
      },
      {
        path: 'exam-result/:resultId',
        name: 'ExamResult',
        component: () => import('@/views/student/ExamResult.vue')
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')

  if (to.meta.requiresAuth) {
    if (!token) {
      next('/login')
      return
    }

    if (to.meta.role && to.meta.role !== role) {
      next('/')
      return
    }
  }

  if (to.path === '/login' && token) {
    next(role === 'ADMIN' ? '/admin/dashboard' : '/student/dashboard')
    return
  }

  next()
})

export default router
