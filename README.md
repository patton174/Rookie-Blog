<p align="center">
  <img src="logo.svg" alt="Rookie Blog Logo" width="120" height="120">
  <h1 align="center">Rookie Blog</h1>
  <p align="center">基于 Spring Boot 3 + Vue 3 的现代化全栈个人博客系统</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.7-000000?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue.js-3.4+-000000?style=flat-square&logo=vue.js&logoColor=white" alt="Vue.js">
  <img src="https://img.shields.io/badge/TypeScript-5.0+-000000?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript">
  <img src="https://img.shields.io/badge/MySQL-8.0-000000?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7.0-000000?style=flat-square&logo=redis&logoColor=white" alt="Redis">
  <img src="https://img.shields.io/badge/Elasticsearch-8.13.2-000000?style=flat-square&logo=elasticsearch&logoColor=white" alt="Elasticsearch">
</p>

<br>

## <img src="https://api.iconify.design/mdi:information-variant.svg?color=%23000000" width="24" height="24" valign="bottom"> 项目介绍

**Rookie Blog** 是一款采用前后端分离架构的现代化个人博客系统。后端基于 **Spring Boot 3**，前端基于 **Vue 3 + TypeScript**，旨在为开发者提供功能完整、性能优越、代码优雅的博客解决方案。

---

## <img src="https://api.iconify.design/mdi:star-four-points.svg?color=%23000000" width="24" height="24" valign="bottom"> 核心特性

<table align="center">
    <tr>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:monitor-dashboard.svg?color=%23000000" width="20" height="20" valign="middle">
                前端特性
            </h3>
            <ul>
                <li><b>架构</b>: Vue 3 Composition API + TypeScript</li>
                <li><b>设计</b>: 响应式设计，完美适配移动端/PC端</li>
                <li><b>主题</b>: 支持暗黑/亮色模式无缝切换</li>
                <li><b>交互</b>: 沉浸式 Markdown 编辑器与实时渲染</li>
                <li><b>语言</b>: 完整的国际化 (I18n) 多语言支持</li>
            </ul>
        </td>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:server.svg?color=%23000000" width="20" height="20" valign="middle">
                后端特性
            </h3>
            <ul>
                <li><b>框架</b>: Spring Boot 3 + MyBatis Plus</li>
                <li><b>安全</b>: 集成 Sa-Token 进行细粒度权限认证</li>
                <li><b>搜索</b>: 基于 Elasticsearch 的高性能全文检索</li>
                <li><b>缓存</b>: Redis 缓存策略与分布式 Session 管理</li>
                <li><b>规范</b>: 标准且优雅的 RESTful API 设计</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:sitemap.svg?color=%23000000" width="24" height="24" valign="bottom"> 项目结构

本项目采用多分支架构管理，前后端代码独立存储，保持仓库整洁：

| 分支名称 | 技术栈 | 说明 |
| :--- | :--- | :--- |
| **backend** | Spring Boot 3, MySQL, Redis, ES | 后端 API 服务核心代码 |
| **frontend** | Vue 3, TypeScript, Vite, Pinia | 前端 Web 应用源代码 |
| **main** | Markdown, Docs | 主分支，存放项目总文档与索引 |

## <img src="https://api.iconify.design/mdi:rocket-launch.svg?color=%23000000" width="24" height="24" valign="bottom"> 快速开始

我们提供了多种方式来获取代码，请根据您的习惯选择：

### 方式一：分别克隆（推荐）

```bash
# 1. 克隆后端代码
git clone -b backend https://github.com/patton174/Rookie-Blog.git backend
cd backend

# 2. 克隆前端代码 (在另一目录)
cd ..
git clone -b frontend https://github.com/patton174/Rookie-Blog.git frontend
```

### 方式二：单仓库切换

```bash
# 1. 克隆仓库
git clone https://github.com/patton174/Rookie-Blog.git

# 2. 切换后端分支
cd Rookie-Blog
git checkout backend

# 3. 切换前端分支 (需要时)
git checkout frontend
```

### 方式三：Git Worktree (高效)

```bash
# 1. 克隆主分支
git clone https://github.com/patton174/Rookie-Blog.git

# 2. 创建后端工作树
git worktree add ../rookie-blog-backend backend

# 3. 创建前端工作树
git worktree add ../rookie-blog-frontend frontend
```

---

## <img src="https://api.iconify.design/mdi:book-open-page-variant.svg?color=%23000000" width="24" height="24" valign="bottom"> 详细文档

<table align="center">
    <tr>
        <td width="50%" valign="top">
            <h3>
                <img src="https://api.iconify.design/mdi:server-network.svg?color=%23000000" width="20" height="20" valign="middle">
                后端文档 (Backend)
            </h3>
            <p>详细配置、API文档及部署指南请查看 <b>backend</b> 分支。</p>
            <br>
            <b>主要技术栈：</b>
            <ul>
                <li>Spring Boot 3.5.7</li>
                <li>JDK 21</li>
                <li>MySQL 8.0 / Redis 7.0</li>
                <li>Elasticsearch 8.13</li>
                <li>MyBatis Plus 3.5.7</li>
                <li>Sa-Token 1.44</li>
            </ul>
            <b>启动步骤：</b>
            <ol>
                <li>安装数据库和中间件</li>
                <li>导入 SQL 脚本</li>
                <li>修改配置文件 (application.yml)</li>
                <li>启动应用</li>
                <li>访问 <a href="http://localhost:8080/api/doc">Swagger UI</a></li>
            </ol>
        </td>
        <td width="50%" valign="top">
            <h3>
                <img src="https://api.iconify.design/mdi:monitor.svg?color=%23000000" width="20" height="20" valign="middle">
                前端文档 (Frontend)
            </h3>
            <p>开发指南、构建部署等请查看 <b>frontend</b> 分支。</p>
            <br>
            <b>主要技术栈：</b>
            <ul>
                <li>Vue 3 (Composition API)</li>
                <li>TypeScript 5.0+</li>
                <li>Vite 5.0+</li>
                <li>Pinia (状态管理)</li>
                <li>Vue Router 4</li>
                <li>Sass/SCSS</li>
            </ul>
            <b>启动步骤：</b>
            <ol>
                <li>安装 Node.js (v18+)</li>
                <li>安装依赖: <code>npm install</code></li>
                <li>启动开发服务器: <code>npm run dev</code></li>
                <li>访问 <a href="http://localhost:5173">http://localhost:5173</a></li>
            </ol>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:tools.svg?color=%23000000" width="24" height="24" valign="bottom"> 开发环境要求

| 组件 | 后端要求 | 前端要求 |
| :--- | :--- | :--- |
| **运行环境** | JDK 21 或 17+ | Node.js 18+ |
| **包管理器** | Maven 3.8+ | npm 9+ / yarn / pnpm |
| **数据库** | MySQL 8.0+ | - |
| **缓存** | Redis 5.0+ | - |
| **搜索** | Elasticsearch 8.x (可选) | - |

## <img src="https://api.iconify.design/mdi:file-tree.svg?color=%23000000" width="24" height="24" valign="bottom"> 目录结构说明

```text
Rookie-Blog/
├── backend/              # 后端项目 (backend分支)
│   ├── rookie-blog-app/       # 启动模块
│   ├── rookie-blog-web/       # 接口层
│   ├── rookie-blog-service/   # 业务层
│   ├── rookie-blog-repository/# 持久层
│   ├── rookie-blog-domain/    # 领域层
│   └── rookie-blog-common/    # 公共模块
├── frontend/             # 前端项目 (frontend分支)
│   ├── src/
│   │   ├── api/         # API接口
│   │   ├── components/  # 公共组件
│   │   ├── views/       # 页面组件
│   │   ├── router/      # 路由配置
│   │   ├── store/       # 状态管理
│   │   └── styles/      # 样式文件
│   ├── public/          # 静态资源
│   └── package.json     # 依赖配置
└── README.md             # 本文档 (main分支)
```

## <img src="https://api.iconify.design/mdi:server-network.svg?color=%23000000" width="24" height="24" valign="bottom"> 部署与访问

### 部署流程
1.  **环境准备**: 安装 JDK 21, MySQL, Redis, ES, Node.js。
2.  **数据库初始化**:
    ```sql
    CREATE DATABASE rookie_blog CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
    -- 执行 backend 分支提供的 SQL 脚本
    ```
3.  **配置修改**: 更新 `application.yml` 中的数据源配置。
4.  **启动服务**:
    ```bash
    # 后端
    mvn spring-boot:run
    # 前端
    npm run dev
    ```

### 访问地址
| 服务 | 地址 | 说明 |
| :--- | :--- | :--- |
| **后端 API** | `http://localhost:8080` | Spring Boot 应用 |
| **API 文档** | `http://localhost:8080/api/doc` | Swagger UI |
| **前端应用** | `http://localhost:5173` | Vue 开发服务器 |
| **生产部署** | `http://localhost:8081` | Nginx 反向代理 |

## <img src="https://api.iconify.design/mdi:handshake.svg?color=%23000000" width="24" height="24" valign="bottom"> 贡献与支持

### 贡献指南
欢迎提交 Issue 和 Pull Request！
1.  **Fork** 本仓库
2.  创建功能分支 (`git checkout -b feature/AmazingFeature`)
3.  提交更改 (`git commit -m 'Add some AmazingFeature'`)
4.  推送到分支 (`git push origin feature/AmazingFeature`)
5.  开启 **Pull Request**

### 联系方式
*   **GitHub Issues**: [点击提问](https://github.com/patton174/Rookie-Blog/issues)
*   **Email**: [2037475020lixu@gmail.com]

---

## <img src="https://api.iconify.design/mdi:license.svg?color=%23000000" width="24" height="24" valign="bottom"> 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

<p align="center">
  <strong>Made by Rookie Blog Team</strong><br>
  <sub>让博客开发变得更简单</sub>
</p>
