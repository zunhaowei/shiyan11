# Spring Boot 用户认证与博客文章系统

## 项目概述

基于 Spring Boot 3.2.5 构建的全栈 Web 应用，实现用户身份认证（JWT + Spring Security）和博客文章管理功能，前后端分离架构。

**访问地址：** `http://localhost:8080/login.html`

---

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 基础框架 |
| Spring Security | 6.x | 安全框架，权限控制 |
| Spring Data JPA | - | ORM 数据访问层 |
| JWT (jjwt) | 0.12.5 | 无状态 Token 认证 |
| BCrypt | - | 密码加密存储 |
| MySQL | 8.x | 关系型数据库 |
| Maven | 3.9+ | 项目构建管理 |
| Java | 17+ | 运行环境 |

---

## 项目结构

```
wzh11/
├── pom.xml                                    # Maven 依赖配置
├── README.md                                  # 项目文档
├── src/main/java/com/example/auth/
│   ├── AuthApplication.java                   # Spring Boot 启动类
│   ├── config/
│   │   └── SecurityConfig.java                # Spring Security 安全配置
│   ├── controller/
│   │   ├── AuthController.java                # 用户认证 API 控制器
│   │   └── PostController.java                # 博客文章 API 控制器
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java       # JWT 认证过滤器
│   ├── model/
│   │   ├── User.java                          # 用户实体类
│   │   ├── Post.java                          # 博客文章实体类
│   │   └── dto/
│   │       ├── ApiResponse.java               # 统一 API 响应格式
│   │       ├── LoginRequest.java              # 登录请求 DTO
│   │       ├── RegisterRequest.java           # 注册请求 DTO
│   │       ├── CreatePostRequest.java         # 创建文章请求 DTO
│   │       └── UpdatePostRequest.java         # 更新文章请求 DTO
│   ├── repository/
│   │   ├── UserRepository.java                # 用户数据访问层
│   │   └── PostRepository.java                # 文章数据访问层
│   ├── service/
│   │   ├── UserService.java                   # 用户业务逻辑层
│   │   └── PostService.java                   # 文章业务逻辑层
│   └── util/
│       └── JwtUtil.java                       # JWT 工具类
└── src/main/resources/
    ├── application.yml                        # 应用配置文件
    └── static/
        ├── css/style.css                      # 全局样式
        ├── login.html                         # 用户登录页面
        ├── register.html                      # 用户注册页面
        ├── home.html                          # 登录后首页
        └── blog.html                          # 博客管理页面
```

---

## 部署环境

```yaml
# application.yml 核心配置
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/auth_demo
    username: root
    password: 123456

jwt:
  secret: (Base64 编码密钥)
  expiration: 86400000    # Token 过期时间 24 小时
```

### 部署步骤

1. 确保 MySQL 服务已启动，创建数据库 `auth_demo`
2. 修改 `application.yml` 中的数据库密码
3. 运行 `mvn spring-boot:run` 启动项目
4. 浏览器访问 `http://localhost:8080/login.html`

---

## API 接口文档

### 用户认证模块

| 方法 | 路径 | 说明 | 认证 |
|------|------|------|------|
| POST | `/api/auth/register` | 用户注册 | 无需 |
| POST | `/api/auth/login` | 用户登录，返回 JWT | 无需 |
| GET | `/api/auth/me` | 获取当前用户信息 | Bearer Token |

#### 注册请求示例

```json
POST /api/auth/register
{
    "username": "zhangsan",
    "password": "123456",
    "email": "zhangsan@example.com"
}
```

#### 登录请求示例

```json
POST /api/auth/login
{
    "username": "zhangsan",
    "password": "123456"
}
```

#### 登录响应示例

```json
{
    "code": 200,
    "message": "登录成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "username": "zhangsan",
        "email": "zhangsan@example.com"
    }
}
```

### 博客文章模块

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/api/posts` | 创建文章 | 需登录 |
| GET | `/api/posts` | 查看全部文章 | 需登录 |
| GET | `/api/posts/mine` | 查看我的文章 | 需登录 |
| GET | `/api/posts/{id}` | 查看文章详情 | 需登录 |
| PUT | `/api/posts/{id}` | 编辑文章 | 仅作者本人 |
| DELETE | `/api/posts/{id}` | 删除文章 | 仅作者本人 |

#### 创建文章请求示例

```json
POST /api/posts
Authorization: Bearer <token>
{
    "title": "Spring Boot 入门教程",
    "content": "Spring Boot 是一个简化 Spring 应用开发的框架..."
}
```

---

## 核心功能说明

### 1. 用户认证流程

1. 用户注册：密码通过 **BCrypt** 加密后存入数据库
2. 用户登录：验证密码后，服务端生成 **JWT Token** 返回前端
3. 前端将 Token 存入 `localStorage`
4. 后续请求在 `Authorization` 头中携带 `Bearer <token>`
5. **JwtAuthenticationFilter** 拦截请求，验证 Token 并设置认证上下文

### 2. 博客文章权限控制

- 文章与用户通过 `@ManyToOne` 建立关联
- **创建文章**：自动绑定当前登录用户为作者
- **编辑文章**：`PostService.updatePost()` 中校验 `post.author.username == currentUsername`
- **删除文章**：`PostService.deletePost()` 中校验作者身份
- 前端页面：仅文章作者能看到"编辑"和"删除"按钮

### 3. 安全架构

```
客户端请求
    │
    ▼
JwtAuthenticationFilter（提取 Token → 验证 → 设置 SecurityContext）
    │
    ▼
SecurityConfig（URL 权限匹配：/api/auth/** 放行，其余需认证）
    │
    ▼
Controller（处理业务请求）
    │
    ▼
Service（业务逻辑 + 权限校验）
    │
    ▼
Repository（JPA 数据访问）
```

---

## 数据库表结构

### users 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(255) | BCrypt 加密密码 |
| email | VARCHAR(100) | 邮箱 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### posts 表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 主键，自增 |
| title | VARCHAR(200) | 文章标题 |
| content | TEXT | 文章内容 |
| author_id | BIGINT (FK) | 作者 ID，外键关联 users |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

---

## 页面路由

| 页面 | URL | 说明 |
|------|-----|------|
| 登录页 | `/login.html` | 用户登录 |
| 注册页 | `/register.html` | 用户注册 |
| 首页 | `/home.html` | 登录后显示 Token，可跳转博客 |
| 博客管理 | `/blog.html` | 文章列表、创建、编辑、删除 |

---

## 常用命令

```bash
# 编译项目
mvn compile

# 启动项目
mvn spring-boot:run

# 打包
mvn package

# Git 操作
git add .
git commit -m "提交说明"
git push
```