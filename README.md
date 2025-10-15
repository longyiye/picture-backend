# picture-backend

## 项目简介

`picture-backend` 是一套图片空间与图片管理的后端服务，提供空间管理、图片上传/检索、用户与空间成员管理、统计分析等能力，支持分表、缓存与会话、对象存储、接口文档与本地 HTTP 用例调试。

## 技术栈

- **框架**: Spring Boot 2.7.6, Spring Web, Spring AOP, Spring WebSocket
- **数据访问**: MyBatis-Plus 3.5.9（含分页与 SQLRunner）
- **数据库与分片**: MySQL 8.x，ShardingSphere 5.2.0（基于 `spaceId` 的标准分片，见 `com.yiye.manager.sharding.PictureShardingAlgorithm`）
- **缓存/会话**: Redis + Spring Session，Caffeine 本地缓存
- **对象存储**: 腾讯云 COS（见 `cos.client.*` 配置）
- **工具库**: Hutool、Jsoup、Disruptor
- **权限认证**: Sa-Token（整合 Redis Jackson 序列化）
- **接口文档**: Knife4j OpenAPI2（访问地址见下文）

## 目录结构（节选）

```
src/main/java/com/yiye
  ├─ controller           # 控制器：文件、图片、空间、空间成员、统计、用户
  ├─ manager              # 业务管理：COS、上传、分片算法、WebSocket 等
  ├─ mapper               # MyBatis-Plus Mapper 接口
  ├─ model                # DTO / Entity / VO / Enums
  ├─ service              # Service 接口与实现
  ├─ config               # CORS、JSON、MyBatis-Plus、请求包装等配置
  └─ PictureBackendApplication.java # 启动类

src/main/resources
  ├─ application.yml          # 基础配置（端口、上下文、分片、数据源、Redis、Knife4j）
  ├─ application-local.yml    # 本地覆盖（对象存储 / 第三方等私密信息）
  ├─ application-prod.yml     # 生产覆盖（如有）
  ├─ mapper/*.xml             # MyBatis XML 映射
  └─ biz/*.json               # 业务配置

sql/create_table.sql          # 初始化数据库脚本
httpTest/*.http               # 本地 HTTP 用例（IDEA/VSCode 可直接调用）
```

## 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.x（默认库名 `picture_project`）
- Redis 5.x/6.x

## 快速开始

1) 初始化数据库

- 创建数据库（默认 `picture_project`）并执行根目录或 `picture-backend-ddd/sql/` 下的 `create_table.sql`。

2) 配置应用

- 基础配置见 `src/main/resources/application.yml`：
  - 端口: `8123`
  - 上下文路径: `/api`
  - 激活环境: `spring.profiles.active=local`
  - 数据源与分片配置：`spring.shardingsphere.*` 与 `spring.datasource.*`
  - Redis 与 Session：`spring.redis.*` 与 `spring.session.*`
- 私密与本地变量建议放在 `application-local.yml`，避免提交到远端仓库：
  - `cos.client.{host,secretId,secretKey,region,bucket}`
  - `aliYunAi.apiKey`

重要提示：仓库中的任何密钥仅为示例或历史测试值，请务必在本地/服务器以安全方式配置并定期更换，不要将真实密钥提交到版本库。

3) 启动项目

- 方式一（推荐）：
  ```bash
  mvn spring-boot:run
  ```
- 方式二（打包运行）：
  ```bash
  mvn clean package -DskipTests
  java -jar target/picture-backend-0.0.1-SNAPSHOT.jar
  ```
- 方式三（IDE 运行）：运行 `com.yiye.PictureBackendApplication`。

启动后默认地址：`http://localhost:8123/api`

## 接口文档

- Knife4j 文档：`http://localhost:8123/api/doc.html`
- OpenAPI JSON：`http://localhost:8123/api/v2/api-docs`

> 文档扫描包：`com.yiye.controller`（见 `application.yml` 中 `knife4j.openapi.group.default.api-rule-resources`）。

## 主要能力概览

- 图片空间与成员管理（创建空间、成员授权、关联图片）
- 图片上传/解析（支持大文件、COS 对象存储）
- 图片检索与相似度能力（颜色相似度、图片搜索等）
- 统计分析（空间维度聚合统计）
- 登录鉴权与权限控制（基于 Sa-Token + Redis Session）
- 分库分表（按 `spaceId` 对 `picture` 表进行标准分片，算法类 `PictureShardingAlgorithm`）

## 本地调试

- 使用 `httpTest/` 与 `picture-backend-ddd/httpTest/` 目录内的 `.http` 用例进行接口调试。
- 开启 SQL 日志：`mybatis-plus.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl`
- WebSocket/异步组件与 Disruptor 仅在相关功能使用时生效。

## 常见问题

- 连接数据库失败：检查 `application.yml` 与 `application-local.yml` 的 `url/username/password`，确认数据库与网络连通。
- Redis 无法连接：确认本机 `127.0.0.1:6379` 可用或调整为你的 Redis 地址。
- 对象存储上传失败：检查 `cos.client.region/bucket/secretId/secretKey`，确认对应资源权限已开通。
- 文档无法访问：确认应用启动正常且使用路径 `http://localhost:8123/api/doc.html`。
