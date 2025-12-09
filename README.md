<p align="center">
  <img src="logo.svg" alt="Rookie Blog Logo" width="120" height="120">
  <h1 align="center">Rookie Blog</h1>
  <p align="center">基于 Spring Boot 3 + Vue 3 的现代化全栈个人博客系统</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Vue.js-3.4+-4FC08D?style=flat-square&logo=vue.js&logoColor=white" alt="Vue 3">
  <img src="https://img.shields.io/badge/TypeScript-5.0+-3178C6?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript">
  <img src="https://img.shields.io/badge/JDK-21-E53525?style=flat-square&logo=openjdk&logoColor=white" alt="JDK">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Redis-7.0-DC382D?style=flat-square&logo=redis&logoColor=white" alt="Redis">
  <img src="https://img.shields.io/badge/Elasticsearch-8.13.2-005571?style=flat-square&logo=elasticsearch&logoColor=white" alt="Elasticsearch">
  <img src="https://img.shields.io/badge/Vite-5.0+-646CFF?style=flat-square&logo=vite&logoColor=white" alt="Vite">
  <img src="https://img.shields.io/badge/Sass-Latest-CC6699?style=flat-square&logo=sass&logoColor=white" alt="Sass">
  <img src="https://img.shields.io/badge/License-MIT-green?style=flat-square&logo=opensourceinitiative&logoColor=white" alt="License">
</p>

<br>

## <img src="https://api.iconify.design/mdi:information-variant.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 项目介绍

**Rookie Blog** 是一款采用前后端分离架构的现代化个人博客系统。

- **后端**：基于 **Spring Boot 3**、**MyBatis Plus** 与 **Sa-Token** 构建，集成了 Elasticsearch 全文检索、Redis 缓存与消息队列等主流技术，提供高性能、高可用的 API 服务。
- **前端**：基于 **Vue 3 (Composition API)**、**TypeScript** 与 **Vite** 开发，采用 Glassmorphism（毛玻璃）设计风格，实现了全端响应式布局、沉浸式 Markdown 阅读体验与流畅的交互动画。

项目前后端深度协同，代码规范严谨，注释详尽，旨在为开发者提供一个功能完整、设计优雅的全栈博客解决方案，也是学习现代化 Web 开发的绝佳实战案例。

---

## <img src="https://api.iconify.design/mdi:layers-triple.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 技术栈

<table align="center">
    <tr>
        <td align="center" width="33%">
            <img src="https://api.iconify.design/mdi:monitor-dashboard.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>前端 (Frontend)</b>
        </td>
        <td align="center" width="33%">
            <img src="https://api.iconify.design/mdi:server.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>后端 (Backend)</b>
        </td>
        <td align="center" width="33%">
            <img src="https://api.iconify.design/mdi:database.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>数据与中间件</b>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <ul>
                <li><b>Core</b>: Vue 3 (Composition API)</li>
                <li><b>Language</b>: TypeScript 5.0+</li>
                <li><b>Build</b>: Vite 5</li>
                <li><b>State</b>: Vue Reactive Store</li>
                <li><b>Router</b>: Vue Router 4</li>
                <li><b>Style</b>: SCSS (Sass)</li>
                <li><b>UI/Anim</b>: GSAP, Glassmorphism</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>Framework</b>: Spring Boot 3.5.7</li>
                <li><b>ORM</b>: MyBatis Plus 3.5.7</li>
                <li><b>Auth</b>: Sa-Token 1.44.0</li>
                <li><b>Search</b>: Elasticsearch 8.13.2</li>
                <li><b>Doc</b>: SpringDoc (OpenAPI 3)</li>
                <li><b>Utils</b>: Lombok, JBCrypt</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>Database</b>: MySQL 8.0.33</li>
                <li><b>Cache</b>: Redis 7.0</li>
                <li><b>Search</b>: Elasticsearch 8.13</li>
                <li><b>Environment</b>: JDK 21</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:star-four-points.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 核心功能

<table align="center">
    <tr>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:monitor-cellphone.svg?color=%23000000" width="20" height="20" valign="middle">
                用户体验 (前端)
            </h3>
            <ul>
                <li><b>沉浸阅读</b>: Markdown 实时渲染、代码高亮、公式图表支持</li>
                <li><b>流畅交互</b>: 优雅的骨架屏 Loading、Toast 提示与过渡动画</li>
                <li><b>响应式设计</b>: 完美适配 Desktop, Tablet, Mobile 全终端</li>
                <li><b>暗黑模式</b>: 一键切换深色/浅色主题，自动持久化配置</li>
                <li><b>国际化</b>: 完整的中英文多语言支持 (i18n)</li>
            </ul>
        </td>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:server-network.svg?color=%23000000" width="20" height="20" valign="middle">
                服务能力 (后端)
            </h3>
            <ul>
                <li><b>全文检索</b>: 基于 Elasticsearch 的高性能文章/内容搜索</li>
                <li><b>社区互动</b>: 无限级评论回复、点赞、收藏与防刷机制</li>
                <li><b>权限管理</b>: 基于 Sa-Token 的细粒度 RBAC 权限控制</li>
                <li><b>数据统计</b>: 文章阅读量、点赞数实时统计与热度分析</li>
                <li><b>系统服务</b>: 邮件异步发送、操作日志记录、全局异常处理</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:sitemap.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 项目结构

本项目采用多分支架构管理，前后端代码独立存储：

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
│   │   ├── composables/ # 组合式函数 (Hooks)
│   │   ├── locales/     # 国际化资源 (i18n)
│   │   ├── router/      # 路由配置
│   │   ├── store/       # 状态管理 (Pinia/Store)
│   │   ├── styles/      # 全局样式 (SCSS)
│   │   ├── utils/       # 工具函数
│   │   └── views/       # 页面组件
│   ├── public/          # 静态资源
│   └── package.json     # 依赖配置
└── README.md             # 本文档 (main分支)
```

| 分支名称 | 技术栈 | 说明 |
| :--- | :--- | :--- |
| **backend** | Spring Boot 3, MySQL, Redis, ES | 后端 API 服务核心代码 |
| **frontend** | Vue 3, TypeScript, Vite | 前端 Web 应用源代码 |
| **main** | Markdown, Docs | 主分支，存放项目总文档与索引 |

## <img src="https://api.iconify.design/mdi:rocket-launch.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 快速开始

### 1. 获取代码
建议分别克隆前后端分支到本地：

```bash
# 1. 克隆后端代码
git clone -b backend https://github.com/patton174/Rookie-Blog.git backend

# 2. 克隆前端代码
git clone -b frontend https://github.com/patton174/Rookie-Blog.git frontend
```

### 2. 环境要求
| 组件 | 后端要求 | 前端要求 |
| :--- | :--- | :--- |
| **运行环境** | JDK 21 或 17+ | Node.js 18+ |
| **数据库** | MySQL 8.0+, Redis 5.0+ | - |
| **其他** | Elasticsearch 8.x (可选) | - |

### 3. 启动运行

**后端启动 (Backend):**
1.  初始化数据库：执行 `sql` 目录下的 `article.sql` 和 `user.sql`。
2.  修改 `application.yml` 配置数据库与 Redis 连接。
3.  运行 `RookieBlogApplication.java`。
4.  接口文档地址：`http://localhost:8080/api/doc`

**前端启动 (Frontend):**
1.  进入前端目录：`cd frontend`
2.  安装依赖：`npm install`
3.  启动服务：`npm run dev`
4.  访问地址：`http://localhost:5173`

---

<!-- CONTRIBUTOR_STATS_START -->

<br>

## <img src="https://api.iconify.design/mdi:account-group-outline.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 贡献者风采

感谢每一位参与 **Rookie Blog** 开发的贡献者，是你们让这个项目变得更好。


<div align="left">
<br/>
<p style="text-align: left;">
<a href="https://github.com/mianqiqi" title="mianqiqi (1 contributions)">
    <img src="https://wsrv.nl/?url=github.com/mianqiqi.png&h=120&w=120&fit=cover&mask=circle&q=80" width="60" height="60" alt="mianqiqi" style="margin-right: 15px; margin-bottom: 15px; display: inline-block;" />
</a><a href="https://github.com/1529119384" title="1529119384 (1 contributions)">
    <img src="https://wsrv.nl/?url=github.com/1529119384.png&h=120&w=120&fit=cover&mask=circle&q=80" width="60" height="60" alt="1529119384" style="margin-right: 15px; margin-bottom: 15px; display: inline-block;" />
</a>
</p>
<br/>
</div>

<!-- CONTRIBUTOR_STATS_END -->

## <img src="https://api.iconify.design/mdi:license.svg?color=%23000000" width="24" height="24" style="vertical-align: -5px;"> 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

<p align="center">
  Made by Rookie Blog Team
</p>
