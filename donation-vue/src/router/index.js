import { createRouter, createWebHistory } from 'vue-router'

// 使用路由懒加载，提高首屏加载速度
const routes = [
    {
        path: '/',
        redirect: '/home' // 建议默认去首页，由守卫决定是否跳回登录
    },
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
        path: '/home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        // meta 字段：requiresAuth 表示这个页面必须登录才能看
        meta: { requiresAuth: true, title: '公益众筹-首页' }
    },
    {
        path: '/profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
    },
    {
        path: '/user-auth',
        name: 'UserAuth',
        component: () => import('@/views/UserAuth.vue')
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 全局前置守卫
router.beforeEach((to, from, next) => {
    const token = sessionStorage.getItem('token')

    // 设置页面标题（可选）
    if (to.meta.title) {
        document.title = to.meta.title
    }

    // 逻辑 A：如果页面需要登录 (requiresAuth)，但用户没 Token，直接踢回登录页
    if (to.meta.requiresAuth && !token) {
        next({
            path: '/login',
            query: { redirect: to.fullPath } // 保存用户刚才想去的页面，登录后可以跳回来
        })
    }
    // 逻辑 B：如果用户已经登录了，还想去登录或注册页，直接送回首页
    else if (token && (to.path === '/login' || to.path === '/register')) {
        next('/home')
    }
    // 逻辑 C：其他情况正常放行
    else {
        next()
    }
})

export default router