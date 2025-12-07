<p align="center">
  <img src="logo.svg" alt="Rookie Blog Logo" width="120" height="120">
  <h1 align="center">Rookie Blog Server</h1>
  <p align="center">基于 Spring Boot 3 的现代化个人博客后端服务</p>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.7-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/JDK-21-E53525?style=flat-square&logo=openjdk&logoColor=white" alt="JDK">
  <img src="https://img.shields.io/badge/MySQL-8.0-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/MyBatis%20Plus-3.5.7-004088?style=flat-square&logo=mybatis&logoColor=white" alt="MyBatis Plus">
  <img src="https://img.shields.io/badge/Sa--Token-1.44.0-9370DB?style=flat-square&logo=auth0&logoColor=white" alt="Sa-Token">
  <img src="https://img.shields.io/badge/Elasticsearch-8.13.2-005571?style=flat-square&logo=elasticsearch&logoColor=white" alt="Elasticsearch">
</p>

<br>

## <img src="https://api.iconify.design/mdi:information-variant.svg?color=%23000000" width="24" height="24" valign="bottom"> 项目介绍

**Rookie Blog Server** 是一款专为开发者打造的现代化个人博客后端服务，采用前后端分离架构。项目不仅具备完善的文章管理、评论互动、用户权限等基础功能，还集成了全文搜索、国际化支持等进阶特性。代码结构清晰，模块化程度高，既适合作为个人博客后端使用，也是学习 Spring Boot 3 服务端开发的绝佳实战案例。

---

## <img src="https://api.iconify.design/mdi:layers-triple.svg?color=%23000000" width="24" height="24" valign="bottom"> 技术栈

<table align="center">
    <tr>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:code-braces.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>后端核心</b>
        </td>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:database-outline.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>数据与中间件</b>
        </td>
        <td align="center" width="250">
            <img src="https://api.iconify.design/mdi:tools.svg?color=%23000000" width="20" height="20" valign="middle">
            <b>工具与规范</b>
        </td>
    </tr>
    <tr>
        <td valign="top">
            <ul>
                <li><b>Framework</b>: Spring Boot 3.5.7</li>
                <li><b>ORM</b>: MyBatis Plus 3.5.7</li>
                <li><b>Auth</b>: Sa-Token 1.44.0</li>
                <li><b>Search</b>: Elasticsearch 8.13.2</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>Database</b>: MySQL 8.0.33</li>
                <li><b>Cache</b>: Redis</li>
                <li><b>Search Engine</b>: Elasticsearch</li>
                <li><b>Mail</b>: JavaMailSender</li>
            </ul>
        </td>
        <td valign="top">
            <ul>
                <li><b>API Docs</b>: SpringDoc (OpenAPI 3)</li>
                <li><b>Utils</b>: Lombok, Apache Commons</li>
                <li><b>Encryption</b>: JBCrypt</li>
                <li><b>Validation</b>: Hibernate Validator</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:sitemap.svg?color=%23000000" width="24" height="24" valign="bottom"> 模块结构

项目采用 Maven 多模块分层架构，遵循 **DDD (领域驱动设计)** 思想进行分包，降低耦合，提升可维护性：

| 模块名称 | 职责说明 | 关键依赖 |
| :--- | :--- | :--- |
| **rookie-blog-app** | **启动层**<br>应用入口、配置文件 (Profile)、资源整合 | `rookie-blog-web`, `mysql` |
| **rookie-blog-web** | **接口层 (Controller)**<br>RESTful API 定义、参数校验、身份认证 | `spring-boot-starter-web`, `sa-token` |
| **rookie-blog-service** | **业务层 (Service)**<br>核心业务逻辑、事务控制 (含 user/article/search 等子模块) | `rookie-blog-repository`, `rookie-blog-common` |
| **rookie-blog-repository** | **持久层 (Repository)**<br>DAO 接口、MyBatis Mapper XML (含 user/article 子模块) | `mybatis-plus`, `mysql` |
| **rookie-blog-domain** | **领域层 (Domain)**<br>实体类 (Entity)、数据传输对象 (DTO)、视图对象 (VO) | `springdoc`, `validation` |
| **rookie-blog-common** | **基础设施层 (Infrastructure)**<br>通用配置 (Redis/ES/Mail)、全局异常、AOP、工具类 | `commons-lang3`, `redis`, `elasticsearch`, `mail` |

## <img src="https://api.iconify.design/mdi:star-four-points.svg?color=%23000000" width="24" height="24" valign="bottom"> 核心功能

<table align="center">
    <tr>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:notebook-edit-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                内容管理
            </h3>
            <ul>
                <li><b>文章发布</b>: 支持 Markdown 内容存储与解析</li>
                <li><b>分类标签</b>: 多级分类与自定义标签管理</li>
                <li><b>全文检索</b>: 基于 Elasticsearch 的高性能文章搜索</li>
                <li><b>数据统计</b>: 文章阅读量、点赞数、收藏数实时统计</li>
            </ul>
        </td>
        <td width="50%">
            <h3>
                <img src="https://api.iconify.design/mdi:comment-text-multiple-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                社区互动
            </h3>
            <ul>
                <li><b>评论系统</b>: 支持无限级嵌套回复与楼层管理</li>
                <li><b>态度表达</b>: 评论点赞、踩 (Reaction) 功能</li>
                <li><b>置顶功能</b>: 支持优质评论置顶</li>
                <li><b>防刷机制</b>: 基于 IP 的频率限制与校验</li>
            </ul>
        </td>
    </tr>
    <tr>
        <td>
            <h3>
                <img src="https://api.iconify.design/mdi:shield-check-outline.svg?color=%23000000" width="20" height="20" valign="middle">
                用户与权限
            </h3>
            <ul>
                <li><b>注册登录</b>: 邮箱验证码注册、密码加密存储</li>
                <li><b>权限控制</b>: 基于 Sa-Token 的 RBAC 权限管理</li>
                <li><b>会话管理</b>: Token 认证与 Redis 会话共享</li>
                <li><b>操作日志</b>: AOP 记录用户敏感操作日志</li>
            </ul>
        </td>
        <td>
            <h3>
                <img src="https://api.iconify.design/mdi:web.svg?color=%23000000" width="20" height="20" valign="middle">
                系统级特性
            </h3>
            <ul>
                <li><b>国际化 (I18n)</b>: 后端接口多语言响应支持</li>
                <li><b>API 文档</b>: 集成 Swagger UI (OpenAPI 3) 自动生成文档</li>
                <li><b>统一响应</b>: 标准化的 JSON 结果封装与异常处理</li>
                <li><b>邮件服务</b>: 异步邮件发送支持</li>
            </ul>
        </td>
    </tr>
</table>

## <img src="https://api.iconify.design/mdi:rocket-launch.svg?color=%23000000" width="24" height="24" valign="bottom"> 快速开始

### 环境准备
*   **JDK**: 21 (推荐) / 17+
*   **Maven**: 3.8+
*   **MySQL**: 8.0+
*   **Redis**: 5.0+
*   **Elasticsearch**: 8.x (可选，搜索功能依赖)

### 安装步骤

#### 1. 克隆项目
```bash
git clone https://github.com/yourusername/rookie-blog.git
cd rookie-blog
```

#### 2. 数据库初始化
请在 MySQL 中创建数据库 `rookie_blog`，并依次执行以下 SQL 脚本：
1.  `rookie-blog-Repository/article-repository/src/main/resources/sql/article.sql`
2.  `rookie-blog-Repository/user-repository/src/main/resources/sql/user.sql`

#### 3. 配置修改
打开 `rookie-blog-app/src/main/resources/application.yml` (或对应环境配置文件)，配置您的服务连接：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/rookie_blog?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
  elasticsearch:
    uris: http://localhost:9200
```

#### 4. 启动运行
运行 `rookie-blog-app/src/main/java/com/lx/blog/app/RookieBlogApplication.java`。

启动成功后，访问以下地址：
*   **API 文档**: [http://localhost:8080/api/doc](http://localhost:8080/api/doc) (Swagger UI)
*   **API 接口前缀**: [http://localhost:8080/api](http://localhost:8080/api)

---

## <img src="https://api.iconify.design/mdi:license.svg?color=%23000000" width="24" height="24" valign="bottom"> 开源协议

本项目采用 [MIT License](LICENSE) 开源协议。

<p align="center">
  Made by Rookie Blog Team
</p>
