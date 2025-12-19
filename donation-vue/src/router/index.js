import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    // --- 1. 需要导航栏的页面组 (父子结构) ---
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
            }
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
    {
        path: '/publish',
        name: 'Publish',
        component: () => import('@/views/Publish.vue'),
        meta: { requiresAuth: true }
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 全局守卫逻辑保持不变（无需改动）
router.beforeEach((to, from, next) => {
    const token = sessionStorage.getItem('token')
    if (to.meta.title) document.title = to.meta.title

    if (to.meta.requiresAuth && !token) {
        next({ path: '/login', query: { redirect: to.fullPath } })
    } else if (token && (to.path === '/login' || to.path === '/register')) {
        next('/home')
    } else {
        next()
    }
})

export default router