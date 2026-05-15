-- ============================================
-- 管理系统数据库初始化脚本
-- 数据库名称: example_db
-- MySQL 8.0+
-- ============================================

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS example_db
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE example_db;

-- 2. 删除已有表（如果需要重新初始化）
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;

-- 3. 创建用户表
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    email VARCHAR(100) NOT NULL COMMENT '邮箱',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 唯一约束
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    
    -- 索引
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 4. 创建任务表
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '任务ID',
    title VARCHAR(200) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    priority VARCHAR(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：HIGH-高，MEDIUM-中，LOW-低',
    status VARCHAR(20) NOT NULL DEFAULT 'TODO' COMMENT '状态：TODO-待办，DONE-已完成',
    deadline DATE COMMENT '截止日期',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 外键约束
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- 索引
    INDEX idx_tasks_user_id (user_id),
    INDEX idx_tasks_status (status),
    INDEX idx_tasks_priority (priority),
    INDEX idx_tasks_user_status (user_id, status),
    INDEX idx_tasks_deadline (deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='任务表';

-- 5. 创建通知表
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '通知ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    type VARCHAR(20) NOT NULL DEFAULT 'INFO' COMMENT '类型：INFO-信息，SUCCESS-成功，WARNING-警告，ERROR-错误',
    is_read BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已读',
    user_id BIGINT COMMENT '接收用户ID，NULL表示全局通知',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    
    -- 外键约束（允许NULL）
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    
    -- 索引
    INDEX idx_notifications_user_id (user_id),
    INDEX idx_notifications_user_read (user_id, is_read),
    INDEX idx_notifications_type (type),
    INDEX idx_notifications_created (created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ============================================
-- 插入测试数据
-- ============================================

-- 6. 插入用户数据
-- 注意：所有密码都是 "password123" 经过BCrypt加密后的结果
-- BCrypt加密说明：每次加密结果不同，但验证时使用matches()方法比对
INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES
-- 管理员账号
('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NOW(), NOW()),
-- 普通用户1 - 张三
('zhangsan', 'zhangsan@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW(), NOW()),
-- 普通用户2 - 李四
('lisi', 'lisi@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW(), NOW()),
-- 普通用户3 - 王五
('wangwu', 'wangwu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW(), NOW()),
-- 普通用户4 - 赵六
('zhaoliu', 'zhaoliu@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER', NOW(), NOW());

-- 7. 插入任务数据
-- 为管理员创建任务
INSERT INTO tasks (title, description, priority, status, deadline, user_id, created_at, updated_at) VALUES
('完成系统架构设计', '设计并文档化整个系统的技术架构，包括前端Vue、后端Spring Boot、数据库设计等', 'HIGH', 'DONE', '2024-01-15', 1, DATE_SUB(NOW(), INTERVAL 10 DAY), NOW()),
('优化数据库查询性能', '分析并优化慢查询，添加必要的索引', 'MEDIUM', 'DONE', '2024-01-20', 1, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),
('实现WebSocket消息推送', '使用STOMP协议实现实时消息推送功能', 'HIGH', 'TODO', '2024-02-01', 1, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('完善用户权限管理', '实现基于角色的访问控制', 'MEDIUM', 'TODO', '2024-02-10', 1, NOW(), NOW()),
('编写系统使用文档', '编写用户手册和开发文档', 'LOW', 'TODO', '2024-02-20', 1, NOW(), NOW());

-- 为用户张三创建任务
INSERT INTO tasks (title, description, priority, status, deadline, user_id, created_at, updated_at) VALUES
('准备周会汇报材料', '整理本周工作进度，准备PPT', 'HIGH', 'TODO', '2024-01-25', 2, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('代码Review', 'Review前端团队提交的PR', 'MEDIUM', 'DONE', '2024-01-22', 2, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),
('修复登录页面兼容性问题', '修复IE浏览器下的样式问题', 'HIGH', 'TODO', '2024-01-26', 2, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- 为用户李四创建任务
INSERT INTO tasks (title, description, priority, status, deadline, user_id, created_at, updated_at) VALUES
('开发用户注册功能', '实现用户注册页面和后端API', 'HIGH', 'DONE', '2024-01-18', 3, DATE_SUB(NOW(), INTERVAL 8 DAY), NOW()),
('设计用户中心界面', '设计用户中心的UI布局', 'MEDIUM', 'DONE', '2024-01-20', 3, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),
('集成第三方登录', '集成微信、QQ登录功能', 'MEDIUM', 'TODO', '2024-02-05', 3, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW()),
('优化图片上传功能', '实现图片压缩和裁剪', 'LOW', 'TODO', '2024-02-15', 3, NOW(), NOW());

-- 为用户王五创建任务
INSERT INTO tasks (title, description, priority, status, deadline, user_id, created_at, updated_at) VALUES
('数据库备份脚本', '编写自动化数据库备份脚本', 'HIGH', 'TODO', '2024-01-28', 4, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),
('监控系统部署', '部署Prometheus和Grafana监控', 'MEDIUM', 'TODO', '2024-02-01', 4, NOW(), NOW());

-- 为用户赵六创建任务
INSERT INTO tasks (title, description, priority, status, deadline, user_id, created_at, updated_at) VALUES
('编写单元测试', '为关键业务逻辑编写单元测试', 'MEDIUM', 'DONE', '2024-01-20', 5, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),
('API文档生成', '使用Swagger生成API文档', 'LOW', 'DONE', '2024-01-22', 5, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),
('性能测试报告', '编写系统性能测试报告', 'MEDIUM', 'TODO', '2024-02-01', 5, NOW(), NOW());

-- 8. 插入通知数据
-- 全局通知（user_id为NULL）
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('系统上线通知', '管理系统已正式上线！所有功能已开放使用，祝您使用愉快！', 'SUCCESS', FALSE, NULL, DATE_SUB(NOW(), INTERVAL 7 DAY)),
('版本更新公告', '系统将于本周日凌晨2:00-4:00进行版本更新，届时系统将暂时无法访问，请提前做好准备。', 'WARNING', FALSE, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('数据备份通知', '系统将于今晚23:00进行数据备份，预计持续30分钟。', 'INFO', FALSE, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('安全漏洞修复', '本次更新修复了已知的安全漏洞，建议所有用户及时更新密码。', 'INFO', FALSE, NULL, NOW());

-- 管理员发送给张三的通知
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('任务分配通知', '您已被分配负责"准备周会汇报材料"任务，请及时查看并处理。', 'INFO', FALSE, 2, DATE_SUB(NOW(), INTERVAL 5 DAY)),
('代码审核通过', '您提交的代码审核已通过，可以合并到主分支了。', 'SUCCESS', TRUE, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('Bug修复提醒', '关于登录兼容性问题的Bug已确认，请尽快修复。', 'WARNING', FALSE, 2, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 管理员发送给李四的通知
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('任务进度提醒', '您负责的第三方登录功能进度滞后，请加快开发速度。', 'WARNING', FALSE, 3, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('代码审核反馈', '您的代码存在一些规范性问题，请查看审核意见并修改。', 'INFO', TRUE, 3, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 管理员发送给王五的通知
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('监控任务分配', '请在本周内完成监控系统部署任务。', 'INFO', FALSE, 4, NOW());

-- 管理员发送给赵六的通知
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('测试报告审核', '您提交的测试报告已审核通过，工作很出色！', 'SUCCESS', TRUE, 5, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 李四发送给全局的通知
INSERT INTO notifications (title, content, type, is_read, user_id, created_at) VALUES
('技术分享邀请', '本周五下午3点将进行Vue3新特性技术分享，欢迎大家参加！', 'INFO', FALSE, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- ============================================
-- 验证数据
-- ============================================

-- 查看用户列表
SELECT id, username, email, role, created_at FROM users;

-- 查看各用户的任务数量
SELECT u.username, COUNT(t.id) as task_count, 
       SUM(CASE WHEN t.status = 'TODO' THEN 1 ELSE 0 END) as todo_count,
       SUM(CASE WHEN t.status = 'DONE' THEN 1 ELSE 0 END) as done_count
FROM users u
LEFT JOIN tasks t ON u.id = t.user_id
GROUP BY u.id, u.username
ORDER BY u.id;

-- 查看通知统计
SELECT 
    CASE WHEN user_id IS NULL THEN '全局通知' ELSE '个人通知' END as 类型,
    COUNT(*) as 数量,
    SUM(CASE WHEN is_read = FALSE THEN 1 ELSE 0 END) as 未读数量
FROM notifications
GROUP BY CASE WHEN user_id IS NULL THEN '全局通知' ELSE '个人通知' END;

-- ============================================
-- 完成提示
-- ============================================

SELECT '数据库初始化完成！' as 状态;
SELECT '测试账号：' as 信息;
SELECT '  管理员: admin / password123' as 账号;
SELECT '  用户1:  zhangsan / password123' as 账号;
SELECT '  用户2:  lisi / password123' as 账号;
SELECT '  用户3:  wangwu / password123' as 账号;
SELECT '  用户4:  zhaoliu / password123' as 账号;
