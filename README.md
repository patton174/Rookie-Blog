<p align="center">
  <img src="public/logo.svg" alt="Rookie Blog Logo" width="120" height="120">
  <h1 align="center">Rookie Blog Web</h1>
  <p align="center">基于 Vue 3 + TypeScript 的现代化个人博客前端系统</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Vue.js-3.4+-000000?style=flat-square&logo=vue.js&logoColor=white" alt="Vue 3">
  <img src="https://img.shields.io/badge/TypeScript-5.0+-000000?style=flat-square&logo=typescript&logoColor=white" alt="TypeScript">
  <img src="https://img.shields.io/badge/Vite-5.0+-000000?style=flat-square&logo=vite&logoColor=white" alt="Vite">
  <img src="https://img.shields.io/badge/Sass-Latest-000000?style=flat-square&logo=sass&logoColor=white" alt="Sass">
  <img src="https://img.shields.io/badge/License-MIT-000000?style=flat-square&logo=opensourceinitiative&logoColor=white" alt="License">
</p>

<br>

## <img src="https://api.iconify.design/mdi:information-variant.svg?color=%23000000" width="24" height="24" valign="bottom"> 项目介绍

**Rookie Blog Web** 是 Rookie Blog 的前端部分，采用当下流行的 **Vue 3 (Composition API)** + **TypeScript** 技术栈开发。项目秉持“简约而不简单”的设计理念，实现了全端响应式布局、沉浸式阅读体验、流畅的交互动画以及暗黑模式支持。它与后端 API 深度协同，共同构建了一个功能完备、体验优秀的现代化博客平台。

---

## <img src="https://api.iconify.design/mdi:layers-triple.svg?color=%23000000" width="24" height="24" valign="bottom"> 技术栈

<table align="center">
    <tr>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:monitor-dashboard.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>核心框架</b>
        </td>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:hammer-wrench.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>构建与工具</b>
        </td>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:palette-swatch.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>UI 与 交互</b>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <ul>
                <li><b>Core</b>: Vue 3 (Composition API)</li>
                <li><b>Language</b>: TypeScript 5.x</li>
                <li><b>Router</b>: Vue Router 4</li>
                <li><b>State</b>: Pinia / Reactive Store</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>Build</b>: Vite 5</li>
                <li><b>Network</b>: Axios</li>
                <li><b>I18n</b>: Vue I18n</li>
                <li><b>Lint</b>: Eslint + Prettier</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>Style</b>: SCSS (Sass)</li>
                <li><b>Animation</b>: GSAP</li>
                <li><b>Markdown</b>: Md-Editor-V3</li>
                <li><b>Design</b>: Glassmorphism</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:sitemap.svg?color=%23000000" width="24" height="24" valign="bottom"> 目录结构

项目结构清晰，遵循 Vue 3 最佳实践与模块化开发规范：

| 目录名称 | 职责说明 | 关键内容 |
| :--- | :--- | :--- |
| **src/api** | **接口层**<br>封装 Axios 请求，集中管理 API 定义 | `article.ts`, `user.ts` |
| **src/components** | **组件层**<br>通用的 UI 组件，复用性强 | `CommentItem`, `NavBar` |
| **src/views** | **视图层**<br>页面级组件，承载业务逻辑 | `ArticleDetail`, `HomeView` |
| **src/store** | **状态层**<br>全局状态管理，响应式数据流 | `userStore`, `appStore` |
| **src/router** | **路由层**<br>页面路由定义、权限守卫 | `index.ts` |
| **src/styles** | **样式层**<br>全局 SCSS 变量、混合与主题定义 | `variables.scss`, `theme.css` |
| **src/locales** | **语言包**<br>国际化资源文件 (JSON/TS) | `zh.ts`, `en.ts` |
| **src/utils** | **工具层**<br>通用辅助函数与类库 | `request.ts`, `format.ts` |

## <img src="https://api.iconify.design/mdi:star-four-points.svg?color=%23000000" width="24" height="24" valign="bottom"> 核心功能

<table align="center">
    <tr>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:book-open-page-variant-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                阅读体验
            </h3>
            <ul>
                <li><b>Markdown 渲染</b>: 支持代码高亮、公式、图表</li>
                <li><b>目录生成</b>: 自动生成文章侧边栏目录 (TOC)</li>
                <li><b>暗黑模式</b>: 一键切换深色/浅色主题，自动持久化</li>
                <li><b>响应式布局</b>: 完美适配 Mobile, Tablet, Desktop</li>
            </ul>
        </td>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:forum-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                互动交流
            </h3>
            <ul>
                <li><b>多级评论</b>: 支持无限级嵌套回复与展开</li>
                <li><b>懒加载</b>: 评论回复按需加载，优化首屏性能</li>
                <li><b>点赞收藏</b>: 文章/评论点赞、收藏互动</li>
                <li><b>分享功能</b>: 快捷分享文章至社交平台</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td>
            <h3>
                <img src="https://api.iconify.design/mdi:account-circle-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                用户中心
            </h3>
            <ul>
                <li><b>身份认证</b>: JWT 登录注册，Token 自动续期</li>
                <li><b>个人资料</b>: 头像上传、昵称修改、简介编辑</li>
                <li><b>历史记录</b>: 本地存储阅读历史与浏览记录</li>
                <li><b>交互反馈</b>: 优雅的 Loading 骨架屏与 Toast 提示</li>
            </ul>
        </td>
        <td>
            <h3>
                <img src="https://api.iconify.design/mdi:code-tags-check.svg?color=%23000000" width="20" height="20" valign="middle">
                工程特性
            </h3>
            <ul>
                <li><b>类型安全</b>: 全 TypeScript 开发，类型提示友好</li>
                <li><b>国际化</b>: 完整的中英文语言包支持 (i18n)</li>
                <li><b>组件化</b>: 高内聚低耦合的 Vue 组件设计</li>
                <li><b>性能优化</b>: 路由懒加载、资源预加载、Gzip 压缩</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:rocket-launch-outline.svg?color=%23000000" width="24" height="24" valign="bottom"> 快速开始

### 环境准备
*   **Node.js**: 16+ (推荐 18+)
*   **Package Manager**: npm, yarn, or pnpm

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/yourusername/rookie-blog-web.git
cd rookie-blog-web
```

#### 2. 安装依赖
```bash
npm install
# 或者
yarn install
```

#### 3. 启动开发服务器
```bash
npm run dev
```
启动后访问：[http://localhost:5173](http://localhost:5173)

#### 4. 构建生产环境
```bash
npm run build
```
构建产物将输出到 `dist` 目录。

---

## <img src="https://api.iconify.design/mdi:license.svg?color=%23000000" width="24" height="24" valign="bottom"> 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

<p align="center">
  Made by Rookie Blog Team
</p>
