# 管理系统

基于 Spring Boot + Vue 3 的企业级管理系统，提供全局消息实时推送、任务管理和通知功能。

## 功能特性

### 核心功能
- **用户认证**: JWT令牌登录，支持管理员和普通用户角色
- **任务管理**: 待办/已办任务管理，支持创建、编辑、完成、删除
- **通知中心**: 全局通知和个人通知，支持标记已读
- **实时消息推送**: WebSocket + STOMP 协议实现实时消息广播
- **用户管理**: 管理员可管理所有用户（新增、编辑、删除）

### 技术栈

**后端**
- Spring Boot 2.7.18
- Spring Security (JWT认证)
- Spring WebSocket (STOMP协议)
- Spring Data JPA
- MySQL 8.0
- JJWT 0.11.5

**前端**
- Vue 3.4.21 + TypeScript
- Vite 5.1.4
- Pinia (状态管理)
- Vue Router 4.3
- Element Plus 2.6
- Tailwind CSS 3.4
- SockJS + STOMP.js

## 项目结构

```
├── backend/                         # Spring Boot 后端
│   ├── pom.xml                     # Maven 配置
│   ├── sql/
│   │   └── init_database.sql      # 数据库初始化脚本
│   ├── docs/
│   │   └── database-design.md     # 数据库设计文档
│   └── src/main/
│       ├── java/com/example/app/
│       │   ├── Application.java   # 启动类
│       │   ├── config/           # 配置类
│       │   │   ├── JwtTokenProvider.java
│       │   │   ├── JwtAuthenticationFilter.java
│       │   │   ├── WebSecurityConfig.java
│       │   │   └── WebSocketConfig.java
│       │   ├── controller/       # REST 控制器
│       │   │   ├── AuthController.java
│       │   │   ├── TaskController.java
│       │   │   ├── NotificationController.java
│       │   │   └── UserController.java
│       │   ├── service/          # 业务服务层
│       │   ├── repository/       # 数据访问层
│       │   ├── entity/           # 实体类
│       │   └── dto/              # 数据传输对象
│       └── resources/
│           └── application.yml    # 应用配置
│
└── frontend/                      # Vue 3 前端
    ├── package.json              # 依赖配置
    ├── vite.config.ts           # Vite 配置
    ├── src/
    │   ├── main.ts              # 入口文件
    │   ├── App.vue             # 根组件
    │   ├── router/             # 路由配置
    │   ├── stores/              # Pinia 状态管理
    │   ├── utils/              # 工具函数
    │   ├── views/              # 页面组件
    │   ├── layout/              # 布局组件
    │   └── types/              # TypeScript 类型定义
    └── index.html              # HTML 入口
```

## 环境要求

- **Java**: 8 或 17
- **Node.js**: 22.22+
- **MySQL**: 8.0+
- **Maven**: 3.6+

## 快速开始

### 1. 数据库配置

#### 创建数据库

```bash
# 登录 MySQL
mysql -u root -p

# 执行以下命令创建数据库和导入数据
source backend/sql/init_database.sql
```

或者手动执行 SQL 文件：

```bash
mysql -u root -p < backend/sql/init_database.sql
```

### 2. 修改数据库连接

编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/example_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username    # 修改为你的用户名
    password: your_password    # 修改为你的密码
```

### 3. 启动后端

```bash
cd backend

# 编译项目
mvn clean install

# 启动应用
mvn spring-boot:run

# 或打包后运行
mvn clean package
java -jar target/management-system-1.0.0.jar
```

后端启动后运行在 http://localhost:8080

### 4. 启动前端

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端启动后运行在 http://localhost:5173

### 5. 访问系统

打开浏览器访问 http://localhost:5173

**测试账号**

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | password123 | 管理员 |
| zhangsan | password123 | 普通用户 |
| lisi | password123 | 普通用户 |
| wangwu | password123 | 普通用户 |
| zhaoliu | password123 | 普通用户 |

> **注意**: 所有测试账号的密码均为 `password123`

登录页面已自动填充管理员账号，点击"登录"按钮即可进入系统。

## 功能说明

### Dashboard（仪表盘）
- 显示待办任务数量、已完成任务数量
- 显示未读通知数量
- 显示任务完成率统计
- 快速查看最近任务和通知

### 任务管理
- **待办任务**: 查看所有待完成的任务
- **已完成**: 查看所有已完成的任务
- **新建任务**: 创建新任务，设置标题、描述、优先级、截止日期
- **完成任务**: 将待办任务标记为已完成
- **编辑任务**: 修改任务信息
- **删除任务**: 删除不需要的任务

### 通知中心
- 查看所有通知（全局通知 + 个人通知）
- 点击通知标记为已读
- 按类型筛选通知（信息、成功、警告、错误）
- 未读通知数量徽章提醒

### 消息推送
- 实时接收系统广播和个人通知
- 管理员可发送广播消息给所有用户
- 支持多种消息类型（信息、成功、警告、错误）
- WebSocket 连接状态显示

### 用户管理（管理员）
- 查看所有用户列表
- 创建新用户
- 编辑用户信息（用户名、邮箱、角色）
- 删除普通用户（不能删除管理员）
- 分配用户角色（管理员/普通用户）

## API 文档

### 认证接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |

### 任务接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/tasks | 获取任务列表 |
| GET | /api/tasks/{id} | 获取任务详情 |
| POST | /api/tasks | 创建任务 |
| PUT | /api/tasks/{id} | 更新任务 |
| PUT | /api/tasks/{id}/complete | 完成任务 |
| DELETE | /api/tasks/{id} | 删除任务 |
| GET | /api/tasks/count | 统计任务数量 |

### 通知接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/notifications | 获取通知列表 |
| GET | /api/notifications/{id} | 获取通知详情 |
| PUT | /api/notifications/{id}/read | 标记已读 |
| POST | /api/notifications/broadcast | 广播通知 |
| GET | /api/notifications/count/unread | 未读数量 |

### 用户接口（管理员）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users | 获取用户列表 |
| GET | /api/users/{id} | 获取用户详情 |
| POST | /api/users | 创建用户 |
| PUT | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

## WebSocket 配置

- **连接端点**: `/ws`
- **STOMP协议**: 启用
- **消息代理**:
  - `/topic/notifications` - 全局通知主题
  - `/user/{userId}/queue/notifications` - 用户私有队列

## 数据库设计

详见 [数据库设计文档](backend/docs/database-design.md)

## 开发指南

### 添加新功能

1. **后端开发**
   - 在 `entity` 包添加实体类
   - 在 `repository` 包添加数据访问接口
   - 在 `service` 包添加业务逻辑
   - 在 `controller` 包添加REST控制器
   - 添加相应的DTO类

2. **前端开发**
   - 在 `types` 目录添加TypeScript类型定义
   - 在 `stores` 目录添加Pinia状态管理
   - 在 `views` 目录添加页面组件
   - 在 `router` 配置路由

### 代码规范

- 后端: 遵循Spring Boot最佳实践，添加中文注释
- 前端: 使用TypeScript，遵循Vue 3组合式API规范
- 命名: 使用有意义的英文命名

## 注意事项

1. **安全提示**
   - 生产环境请修改 `jwt.secret` 为更复杂的密钥
   - 生产环境请启用HTTPS
   - 定期备份数据库

2. **性能优化**
   - 合理使用数据库索引
   - 大数据量时使用分页查询
   - 前端注意组件懒加载

3. **部署建议**
   - 后端可打包为JAR部署
   - 前端可打包为静态资源由Nginx托管
   - 使用反向代理配置HTTPS

## 许可证

MIT License

## 技术支持

如有问题，请提交 Issue 或联系开发者。
