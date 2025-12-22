import { createApp } from 'vue'
import App from './App.vue'
import router from './router' // 如果你有路由

// 1. 导入 Element Plus 库和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 2. 导入图标库
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 3. 注册所有图标（解决 User, Lock 等图标不显示的问题）
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

// 4. 全局注册 Element Plus
app.use(ElementPlus)

app.use(router)
app.mount('#app')