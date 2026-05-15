import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

// 注册Pinia状态管理
app.use(pinia)
// 注册Vue Router路由
app.use(router)
// 注册Element Plus UI组件库
app.use(ElementPlus)

// 挂载应用到#app元素
app.mount('#app')