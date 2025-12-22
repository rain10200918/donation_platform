import { createRouter, createWebHistory } from 'vue-router'
import {ElMessage} from "element-plus";

const routes = [
    {
        path: '/',
        component: () => import('@/views/Layout.vue'), // 这里的父组件包含 Header
        redirect: '/home',
        children: [
            {
                path: 'home',
                name: 'Home',
                component: () => import('@/views/Home.vue'),
                meta: { requiresAuth: true, title: '首页' }
            },
            {
                path: 'projects',
                name: 'Projects',
                component: () => import('@/views/Projects.vue'),
                meta: { title: '项目浏览' }
            },
            {
                path: 'profile',
                name: 'Profile',
                component: () => import('@/views/Profile.vue'),
                meta: { requiresAuth: true, title: '个人中心' }
            },
            {
                path: 'my-projects',
                name: 'MyProjects',
                component: () => import('@/views/MyProjects.vue'),
                meta: { requiresAuth: true, title: '我的求助' }
            },
            {
                path: 'project/:id',
                name: 'ProjectDetail',
                component: () => import('@/views/ProjectDetail.vue'),
                meta: { title: '项目详情' }
            },
            {
                path: '/story/1',
                name: 'Story1',
                component: () => import('@/views/stories/StoryApple.vue'),
                meta: { title: '项目故事-小苹果的微笑' }
            },
            {
                path: '/story/2',
                name: 'Story2',
                component: () => import('@/views/stories/StoryBooks.vue'),
                meta: { title: '项目故事-大山里的书声' }
            },
            {
                path: '/update-progress',
                name: 'UpdateProgress',
                component: () => import('../views/UpdateProgress.vue')
            },
            {
                path: '/publish',
                name: 'Publish',
                component: () => import('@/views/Publish.vue'),
                meta: { requiresAuth: true }
            }
        ]
    },
    {
        path: '/admin',
        component: () => import('@/views/AdminLayout.vue'),
        meta: { requiresAuth: true, role: 1 }, // role: 1 表示只有管理员能进
        children: [
            { path: 'audit', component: () => import('@/views/admin/Audit.vue') },
            { path: 'users' , component: () => import('@/views/admin/UserManagement.vue')},
            { path: 'settings' , component: () => import('@/views/admin/Settings.vue')},
            { path: 'profile' ,component: () => import('@/views/admin/Profile.vue')}
        ]
    },

    // --- 2. 不需要通用导航栏的独立页面 ---
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue')
    },
    {
        path: '/user-auth',
        name: 'UserAuth',
        component: () => import('@/views/UserAuth.vue'),
        meta: { requiresAuth: true }
    },

]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 全局守卫逻辑保持不变（无需改动）
router.beforeEach((to, from, next) => {
    const token = sessionStorage.getItem('token')
    // 获取完整的用户信息对象
    const userInfo = JSON.parse(sessionStorage.getItem('userInfo') || '{}')

    // 调试日志：在开发阶段非常有用，能让你看清守卫里拿到了什么
    console.log('路由守卫检查:', {
        path: to.path,
        token: !!token,
        role: userInfo.role,
        roleType: typeof userInfo.role
    })

    // 判断逻辑：
    // 1. 如果访问的是后台页面 (以 /admin 开头)
    if (to.path.startsWith('/admin')) {
        // 使用 == 兼容字符串 "1" 和数字 1
        if (token && userInfo.role == 1) {
            next() // 权限匹配，放行
        } else {
            // 如果已经登录但不是管理员，提示权限不足
            if (token) {
                ElMessage.warning('您的账号没有管理员权限')
            }
            next('/home') // 踢回首页
        }
    }
    // 2. 如果是去登录页，但已经登录了，直接去首页
    else if (to.path === '/login' && token) {
        next('/home')
    }
    // 3. 其他情况正常放行
    else {
        next()
    }
})

export default router