# 数据库架构设计文档

## 1. 数据库概述

### 1.1 数据库信息
- **数据库名称**: `example_db`
- **数据库类型**: MySQL 8.0+
- **字符集**: UTF-8 MB4

### 1.2 设计原则
- 使用JPA自动管理表结构（`ddl-auto: update`）
- 提供完整SQL脚本用于手动初始化
- 支持物理外键约束
- 使用合理的索引优化查询性能

## 2. ER图设计

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│     USERS       │       │     TASKS       │       │ NOTIFICATIONS   │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │──┐    │ id (PK)         │       │ id (PK)         │
│ username (UK)   │  │    │ title           │       │ title           │
│ email (UK)      │  └───►│ user_id (FK)    │       │ content         │
│ password        │       │ priority        │       │ type            │
│ role            │       │ status          │       │ is_read         │
│ created_at      │       │ deadline        │       │ user_id (FK)    │
│ updated_at      │       │ created_at      │       │ created_at      │
└─────────────────┘       │ updated_at      │       └─────────────────┘
        │                 └─────────────────┘                │
        │                                                  │
        │                 用户拥有多个任务                  │
        │                                                  │
        └─────────────── 用户接收多条通知 ─────────────────┘
                            (user_id可为NULL表示全局通知)
```

## 3. 表结构设计

### 3.1 用户表 (users)

#### 功能说明
存储系统用户信息，支持管理员和普通用户两种角色。

#### 表结构
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 用户ID |
| username | VARCHAR(50) | NOT NULL, UNIQUE | 用户名 |
| email | VARCHAR(100) | NOT NULL, UNIQUE | 邮箱 |
| password | VARCHAR(255) | NOT NULL | 密码（BCrypt加密） |
| role | VARCHAR(20) | NOT NULL, DEFAULT 'USER' | 角色：ADMIN/USER |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

#### 索引
- PRIMARY KEY: `id`
- UNIQUE INDEX: `uk_users_username` ON `username`
- UNIQUE INDEX: `uk_users_email` ON `email`

#### 说明
- `password` 字段存储BCrypt加密后的密码
- `role` 字段区分用户权限，ADMIN拥有管理权限
- `created_at` 和 `updated_at` 自动维护

### 3.2 任务表 (tasks)

#### 功能说明
存储用户任务信息，支持任务状态管理和优先级划分。

#### 表结构
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 任务ID |
| title | VARCHAR(200) | NOT NULL | 任务标题 |
| description | TEXT | NULL | 任务描述 |
| priority | VARCHAR(20) | NOT NULL, DEFAULT 'MEDIUM' | 优先级：HIGH/MEDIUM/LOW |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | 状态：TODO/DONE |
| deadline | DATE | NULL | 截止日期 |
| user_id | BIGINT | NOT NULL, FOREIGN KEY | 所属用户ID |
| created_at | DATETIME | NOT NULL | 创建时间 |
| updated_at | DATETIME | NOT NULL | 更新时间 |

#### 索引
- PRIMARY KEY: `id`
- INDEX: `idx_tasks_user_id` ON `user_id`
- INDEX: `idx_tasks_status` ON `status`
- INDEX: `idx_tasks_user_status` ON `user_id, status`
- FOREIGN KEY: `fk_tasks_user` ON `user_id` → `users(id)`

#### 说明
- `title` 是任务的核心标识
- `description` 支持长文本描述
- `priority` 和 `status` 用于任务分类和筛选
- `user_id` 关联到用户表，一个用户可拥有多个任务

### 3.3 通知表 (notifications)

#### 功能说明
存储系统通知和广播消息，支持全局通知和个人通知。

#### 表结构
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | BIGINT | PRIMARY KEY, AUTO_INCREMENT | 通知ID |
| title | VARCHAR(200) | NOT NULL | 通知标题 |
| content | TEXT | NOT NULL | 通知内容 |
| type | VARCHAR(20) | NOT NULL, DEFAULT 'INFO' | 类型：INFO/SUCCESS/WARNING/ERROR |
| is_read | BOOLEAN | NOT NULL, DEFAULT FALSE | 是否已读 |
| user_id | BIGINT | NULL | 接收用户ID（NULL为全局通知） |
| created_at | DATETIME | NOT NULL | 创建时间 |

#### 索引
- PRIMARY KEY: `id`
- INDEX: `idx_notifications_user_id` ON `user_id`
- INDEX: `idx_notifications_user_read` ON `user_id, is_read`
- INDEX: `idx_notifications_created` ON `created_at DESC`

#### 说明
- `user_id` 为NULL时表示全局通知，所有用户可见
- `user_id` 有值时表示个人通知，仅指定用户可见
- `is_read` 用于标记通知是否已读
- `type` 用于区分通知类型，展示不同样式

## 4. 关系说明

### 4.1 用户-任务关系
```
用户(1) ────< 任务(N)
```
- 一个用户可以拥有多个任务
- 一个任务只能属于一个用户
- 删除用户时，其所有任务一并删除（CASCADE）

### 4.2 用户-通知关系
```
用户(1) ────< 通知(N)
          或
通知(N) ────> NULL (全局通知)
```
- 一个用户可以接收多条通知
- 一条通知可以发送给一个用户或所有用户
- 删除用户时，其个人通知的user_id设为NULL（SET NULL）

## 5. 字段设计规范

### 5.1 命名规范
- 表名：英文小写，复数形式（users, tasks, notifications）
- 字段名：英文小写，下划线分隔（created_at, user_id）
- 主键：id
- 外键：`表名_id` 格式

### 5.2 数据类型选择
- ID：使用 BIGINT 自增
- 字符串：使用 VARCHAR，根据实际长度设置
- 布尔：使用 BOOLEAN
- 时间：使用 DATETIME
- 文本：使用 TEXT

### 5.3 约束使用
- NOT NULL：用于必填字段
- UNIQUE：用于唯一性字段
- DEFAULT：提供合理的默认值
- FOREIGN KEY：维护数据完整性

## 6. 索引设计

### 6.1 主键索引
所有表都有主键索引，用于唯一标识记录。

### 6.2 业务索引
- `tasks.user_id`: 用于查询用户的所有任务
- `tasks.status`: 用于筛选任务状态
- `tasks.user_id + status`: 联合索引，加速用户任务状态筛选
- `notifications.user_id`: 用于查询用户通知
- `notifications.is_read`: 用于筛选未读通知

### 6.3 排序索引
- `notifications.created_at DESC`: 用于按时间倒序查询通知

## 7. 安全性考虑

### 7.1 密码安全
- 密码字段使用BCrypt加密存储
- 永不以明文形式存储或传输密码

### 7.2 数据隔离
- 用户只能访问自己的任务
- 通知分为全局和个人，实现数据隔离
- 管理员可访问所有数据

### 7.3 SQL注入防护
- 使用JPA参数化查询
- 不直接拼接SQL语句

## 8. 性能考虑

### 8.1 分页查询
- 任务列表和通知列表使用分页
- 默认每页10条数据

### 8.2 索引优化
- 根据查询场景创建合适的索引
- 避免过度索引影响写入性能

### 8.3 异步处理
- WebSocket消息推送使用异步发送
- 不阻塞HTTP请求响应

## 9. 扩展性

### 9.1 预留字段
- 可根据需求添加扩展字段
- 如：任务的分类标签、通知的优先级等

### 9.2 软删除支持
- 当前设计使用硬删除
- 如需支持软删除，可添加 `deleted_at` 字段

### 9.3 分库分表
- 当前为单机数据库设计
- 如需扩展，可按用户ID或时间进行分表
