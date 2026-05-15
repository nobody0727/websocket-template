import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    // 路径别名配置
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,  // 前端开发服务器端口
    proxy: {
      // 代理API请求到后端
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // 代理WebSocket请求到后端
      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true
      }
    }
  }
})