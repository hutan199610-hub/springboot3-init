<<<<<<< HEAD
# Spring Boot 3 生产级初始化框架

一个同时支持**单体架构**与**微服务架构**的 Spring Boot 3 初始化框架，开箱即用，覆盖企业级开发所需的基础设施、可插拔组件和开发规范。

---

## 目录

- [技术栈](#技术栈)
- [项目结构](#项目结构)
- [模块说明](#模块说明)
- [架构模式](#架构模式)
- [依赖版本矩阵](#依赖版本矩阵)
- [快速开始](#快速开始)
- [配置说明](#配置说明)
- [可插拔组件](#可插拔组件)
- [API 文档](#api-文档)
- [数据库](#数据库)
- [开发指南](#开发指南)
- [部署指南](#部署指南)
- [常见问题](#常见问题)

---

## 技术栈

| 类别 | 技术 | 版本 |
|------|------|------|
| **语言** | Java | 17 |
| **框架** | Spring Boot | 3.2.5 |
| **微服务** | Spring Cloud | 2023.0.1 |
| **微服务** | Spring Cloud Alibaba | 2023.0.1.0 |
| **数据库** | PostgreSQL | 16 |
| **ORM** | MyBatis-Plus | 3.5.5 |
| **缓存** | Redis + Lettuce | 7.x |
| **分布式锁** | Redisson | 3.27.2 |
| **API 文档** | SpringDoc (OpenAPI 3) | 2.3.0 |
| **安全** | Spring Security + OAuth2 | 随 Boot BOM |
| **注册/配置中心** | Nacos | 2.3.1 |
| **网关** | Spring Cloud Gateway | 随 Cloud BOM |
| **对象存储** | MinIO | 8.5.7 |
| **搜索引擎** | Elasticsearch | 8.12.2 |
| **工作流** | Flowable | 7.0.1 |
| **任务调度** | XXL-JOB | 2.4.0 |
| **消息队列** | RocketMQ | 2.3.0 |
| **AI 集成** | Spring AI | 1.0.0+ |
| **工具库** | Hutool | 5.8.25 |

---

## 项目结构

```
project-root/
├── pom.xml                              # 父 POM（统一依赖版本管理，BOM 导入）
├── docker-compose.yml                   # Docker 编排（PostgreSQL、Redis、Nacos、MinIO、ES）
├── Dockerfile                           # 应用 Docker 镜像构建
├── sql/
│   └── init.sql                         # 数据库初始化脚本
│
├── project-common/                      # 公共模块
│   └── src/main/java/com/example/common/
│       ├── base/
│       │   ├── BaseEntity.java          # 实体基类（id、createTime、updateTime、isDeleted）
│       │   └── PageQuery.java           # 分页查询基类
│       ├── config/
│       │   ├── CorsConfig.java          # 跨域配置
│       │   ├── ElasticsearchConfig.java # Elasticsearch 客户端配置
│       │   ├── FlowableConfig.java      # 工作流引擎配置
│       │   ├── MetaObjectHandlerImpl.java # MyBatis-Plus 自动填充
│       │   ├── MinioConfig.java         # MinIO 客户端配置
│       │   ├── MinioService.java        # MinIO 文件操作服务
│       │   ├── MybatisPlusConfig.java   # MyBatis-Plus 分页插件
│       │   ├── OpenFeignConfig.java     # Feign 拦截器（Token 透传）
│       │   ├── RedisConfig.java         # RedisTemplate 自定义序列化
│       │   ├── RedissonConfig.java      # Redisson 分布式锁配置
│       │   ├── RocketMQConfig.java      # RocketMQ 生产者配置
│       │   ├── SpringAIConfig.java      # Spring AI ChatClient 配置
│       │   ├── SwaggerConfig.java       # Swagger/OpenAPI 文档配置
│       │   └── XxlJobConfig.java        # XXL-JOB 执行器配置
│       ├── exception/
│       │   ├── BusinessException.java   # 业务异常
│       │   └── GlobalExceptionHandler.java # 全局异常处理器
│       └── result/
│           └── Result.java              # 统一响应体
│
├── project-core/                        # 核心业务模块
│   └── src/main/java/com/example/core/
│       ├── dto/
│       │   └── SysUserDTO.java          # 用户 DTO（含参数校验）
│       ├── entity/
│       │   └── SysUser.java             # 用户实体
│       ├── mapper/
│       │   └── SysUserMapper.java       # MyBatis-Plus Mapper
│       └── service/
│           ├── SysUserService.java      # 用户服务接口
│           └── impl/
│               └── SysUserServiceImpl.java # 用户服务实现（含 Redis 缓存）
│
├── project-admin/                       # 后台管理单体应用
│   └── src/main/
│       ├── java/com/example/admin/
│       │   ├── AdminApplication.java    # Spring Boot 主启动类
│       │   ├── config/
│       │   │   ├── RedisCacheManager.java # 缓存管理器
│       │   │   └── SecurityConfig.java  # Spring Security 配置
│       │   ├── controller/
│       │   │   └── SysUserController.java # 用户 CRUD 接口
│       │   ├── handler/
│       │   │   └── SampleJobHandler.java # XXL-JOB 示例任务
│       │   └── interceptor/
│       │       └── JwtAuthInterceptor.java # JWT 认证拦截器
│       └── resources/
│           ├── application.yml          # 主配置
│           ├── application-standalone.yml # 单体模式配置
│           ├── application-cloud.yml    # 微服务模式配置
│           └── logback-spring.xml       # 日志配置
│
├── project-gateway/                     # 微服务网关
│   └── src/main/java/com/example/gateway/
│       ├── GatewayApplication.java      # 网关启动类
│       └── filter/
│           └── AuthGlobalFilter.java    # 全局认证过滤器
│
├── project-auth/                        # 认证授权服务
│   └── src/main/java/com/example/auth/
│       └── AuthApplication.java         # 认证服务启动类
│
├── project-system/                      # 系统管理服务
│   └── src/main/java/com/example/system/
│       └── SystemApplication.java       # 系统服务启动类
│
└── project-job/                         # 定时任务服务
    └── src/main/java/com/example/job/
        └── JobApplication.java          # 任务服务启动类
```

---

## 模块说明

### project-common（公共模块）

所有模块的公共依赖，包含：

- **统一响应体** `Result<T>`：标准化 API 响应格式
- **全局异常处理** `GlobalExceptionHandler`：统一异常捕获和响应
- **业务异常** `BusinessException`：自定义业务错误码
- **实体基类** `BaseEntity`：通用字段（id、createTime、updateTime、isDeleted）
- **分页查询** `PageQuery`：分页参数封装
- **Redis 配置**：自定义 `RedisTemplate` 序列化（Key → String，Value → JSON）
- **MyBatis-Plus 配置**：PostgreSQL 分页插件、自动填充、逻辑删除
- **跨域配置**：全局 CORS 支持
- **Swagger 配置**：OpenAPI 3 文档 + JWT Bearer 认证
- 所有可插拔组件的配置类（通过 `@ConditionalOnProperty` 控制加载）

### project-core（核心业务模块）

业务代码的核心层，包含：

- **实体类**：数据库表映射
- **DTO**：数据传输对象（含 Jakarta Validation 校验）
- **Mapper**：MyBatis-Plus 数据访问接口
- **Service**：业务逻辑接口和实现

### project-admin（后台管理应用）

单体模式下的主应用，包含：

- Spring Boot 主启动类
- REST Controller（用户管理 CRUD 示例）
- Spring Security 安全配置
- JWT 认证拦截器
- 缓存管理器（支持 `@Cacheable` 注解）
- XXL-JOB 示例任务处理器

### project-gateway（微服务网关）

基于 Spring Cloud Gateway 的 API 网关：

- 路由转发（lb 负载均衡）
- 全局认证过滤器（Token 校验与透传）
- 白名单机制（登录/注册接口免认证）
- 跨域支持

### project-auth / project-system / project-job

微服务模式下的独立服务模块，分别负责认证授权、系统管理、定时任务。

---

## 架构模式

本框架支持两种部署模式，通过 Spring Profile 切换：

### 单体模式（Standalone）

```
┌─────────────────────────────────────┐
│          project-admin              │
│  ┌─────────┐ ┌─────────┐ ┌──────┐  │
│  │ Auth    │ │ System  │ │ Job  │  │
│  │ Module  │ │ Module  │ │Module│  │
│  └─────────┘ └─────────┘ └──────┘  │
│              ↓                      │
│     PostgreSQL / Redis              │
└─────────────────────────────────────┘
```

- 仅启动 `project-admin` 一个进程
- 所有功能模块作为内部依赖打包
- 适合中小项目、快速开发

### 微服务模式（Cloud）

```
┌──────────────┐
│   Gateway    │ ← 统一入口、路由转发
└──────┬───────┘
       │
  ┌────┴────┐
  │  Nacos  │ ← 注册中心 + 配置中心
  └────┬────┘
       │
  ┌────┴────────────────────┐
  │                         │
  ▼                         ▼
┌──────────┐  ┌──────────┐  ┌──────────┐
│  Auth    │  │  System  │  │   Job    │
│ Service  │  │ Service  │  │ Service  │
└──────────┘  └──────────┘  └──────────┘
  │             │              │
  └─────────────┴──────────────┘
         PostgreSQL / Redis
```

- 各服务独立部署、独立扩展
- 通过 Nacos 注册发现
- 网关统一入口，路由转发

---

## 依赖版本矩阵

父 POM 通过 `<properties>` 统一管理所有版本号：

| 组件 | GroupId:ArtifactId | 版本 |
|------|-------------------|------|
| Spring Boot | `org.springframework.boot:spring-boot-starter-parent` | 3.2.5 |
| Spring Cloud | `org.springframework.cloud:spring-cloud-dependencies` | 2023.0.1 |
| Spring Cloud Alibaba | `com.alibaba.cloud:spring-cloud-alibaba-dependencies` | 2023.0.1.0 |
| MyBatis-Plus | `com.baomidou:mybatis-plus-spring-boot3-starter` | 3.5.5 |
| Redisson | `org.redisson:redisson-spring-boot-starter` | 3.27.2 |
| SpringDoc | `org.springdoc:springdoc-openapi-starter-webmvc-ui` | 2.3.0 |
| Elasticsearch | `co.elastic.clients:elasticsearch-java` | 8.12.2 |
| MinIO | `io.minio:minio` | 8.5.7 |
| Flowable | `org.flowable:flowable-spring-boot-starter` | 7.0.1 |
| XXL-JOB | `com.xuxueli:xxl-job-core` | 2.4.0 |
| RocketMQ | `org.apache.rocketmq:rocketmq-spring-boot-starter` | 2.3.0 |
| Hutool | `cn.hutool:hutool-all` | 5.8.25 |
| Lombok | `org.projectlombok:lombok` | 1.18.30 |

所有版本均为 GA 稳定版本，无 SNAPSHOT / RC / Milestone。

---

## 快速开始

### 环境要求

- **JDK**: 17+
- **Maven**: 3.8+
- **Docker**: 20.10+（可选，用于快速启动基础设施）
- **IDE**: IntelliJ IDEA 2023+（推荐）

### 方式一：Docker 一键启动（推荐）

```bash
# 1. 启动基础设施（PostgreSQL、Redis、Nacos、MinIO、Elasticsearch）
docker-compose up -d

# 2. 等待所有服务就绪后，构建并启动应用
mvn clean package -DskipTests
java -jar project-admin/target/project-admin-1.0.0.jar --spring.profiles.active=standalone
```

### 方式二：本地开发

```bash
# 1. 先在本地安装并启动 PostgreSQL、Redis 等服务

# 2. 创建数据库并初始化
psql -U postgres -d project_db -f sql/init.sql

# 3. 修改项目配置（数据库连接、Redis 地址等）
#    编辑 project-admin/src/main/resources/application.yml

# 4. 构建并运行
mvn clean compile
mvn spring-boot:run -pl project-admin
```

### 方式三：IntelliJ IDEA

1. 打开项目根目录
2. 等待 Maven 索引完成
3. 找到 `AdminApplication.java`，右键运行
4. 访问 http://localhost:8080/api/swagger-ui.html

---

## 配置说明

### 主配置文件

`project-admin/src/main/resources/application.yml` 包含公共配置：

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

spring:
  datasource:
    driver-class-name: org.postgresql.Driver
    url: jdbc:postgresql://localhost:5432/project_db
    username: postgres
    password: postgres
  data:
    redis:
      host: localhost
      port: 6379
```

### Profile 配置

| Profile | 文件 | 用途 |
|---------|------|------|
| `standalone` | `application-standalone.yml` | 单体模式，关闭微服务组件 |
| `cloud` | `application-cloud.yml` | 微服务模式，启用 Nacos 注册发现 |

### 配置切换示例

```bash
# 单体模式（默认）
java -jar project-admin.jar --spring.profiles.active=standalone

# 微服务模式
java -jar project-admin.jar --spring.profiles.active=cloud

# 自定义端口和数据库
java -jar project-admin.jar --server.port=9090 --spring.datasource.url=jdbc:postgresql://remote-host:5432/db
```

---

## 可插拔组件

每个组件通过 `@ConditionalOnProperty` 控制加载，在 `application.yml` 中设置对应属性即可启用：

### Redis + Redisson（分布式锁）

```yaml
# project-admin/src/main/resources/application.yml
redisson:
  enabled: true    # 默认 false，设置为 true 启用
```

- `RedisConfig`：自定义 `RedisTemplate` 序列化（Key → String，Value → JSON）
- `RedissonConfig`：`RedissonClient` Bean，用于分布式锁和高级数据结构
- `RedisCacheManager`：支持 `@Cacheable` 注解，缓存过期时间 15~60 分钟

### Elasticsearch（全文搜索）

```yaml
elasticsearch:
  enabled: true
```

- `ElasticsearchConfig`：注入 `ElasticsearchClient` Bean
- 使用官方 `elasticsearch-java` 客户端（非旧版 RestHighLevelClient）

### MinIO（对象存储）

```yaml
minio:
  enabled: true
```

- `MinioConfig`：注入 `MinioClient` Bean
- `MinioService`：封装文件上传、下载、预签名 URL、桶管理

### Flowable（工作流/审批流）

```yaml
flowable:
  enabled: true
```

- `FlowableConfig`：配置流程引擎，数据库自动更新
- 适用于审批流程、请假流程等业务场景

### XXL-JOB（分布式任务调度）

```yaml
xxljob:
  enabled: true
```

- `XxlJobConfig`：注册 `XxlJobSpringExecutor`
- `SampleJobHandler`：示例任务处理器

### RocketMQ（消息队列）

```yaml
rocketmq:
  enabled: true
```

- `RocketMQConfig`：配置 `DefaultMQProducer`
- 适用于异步解耦、事件驱动架构

### Spring AI（AI 集成）

```yaml
springai:
  enabled: true
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
```

- `SpringAIConfig`：配置 `ChatClient`
- 需要 OpenAI API Key

---

## API 文档

启动应用后，访问 Swagger UI：

```
http://localhost:8080/api/swagger-ui.html
```

OpenAPI 3.0 规范文档：

```
http://localhost:8080/api/v3/api-docs
```

Swagger UI 已配置 JWT Bearer 认证按钮，可直接在页面上输入 Token 进行接口测试。

### API 接口示例

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/api/sys/user/{id}` | 获取用户详情 |
| `GET` | `/api/sys/user/page` | 分页查询用户 |
| `POST` | `/api/sys/user` | 新增用户 |
| `PUT` | `/api/sys/user` | 更新用户 |
| `DELETE` | `/api/sys/user/{id}` | 删除用户 |

### 统一响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... }
}
```

错误响应：

```json
{
  "code": 400,
  "msg": "用户名不能为空",
  "data": null
}
```

---

## 数据库

### 连接信息

| 参数 | 值 |
|------|-----|
| 主机 | `localhost` |
| 端口 | `5432` |
| 数据库 | `project_db` |
| 用户名 | `postgres` |
| 密码 | `postgres` |

### 初始化

```bash
# 通过 Docker Compose 自动初始化
docker-compose up -d postgres

# 或手动执行 SQL 脚本
psql -U postgres -d project_db -f sql/init.sql
```

### 数据表

#### sys_user（系统用户表）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGSERIAL | 主键，雪花算法 |
| username | VARCHAR(50) | 用户名，唯一 |
| password | VARCHAR(100) | 密码，BCrypt 加密 |
| nickname | VARCHAR(50) | 昵称 |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| status | SMALLINT | 状态：1=正常，0=禁用 |
| is_deleted | SMALLINT | 逻辑删除：0=正常，1=删除 |
| create_time | TIMESTAMP | 创建时间，自动填充 |
| update_time | TIMESTAMP | 更新时间，自动填充 |

### 测试数据

| 用户名 | 密码 | 说明 |
|--------|------|------|
| admin | 123456 | 管理员账号 |
| test | 123456 | 测试账号 |

> 密码已通过 BCrypt 加密存储，框架内置 `PasswordEncoder` 进行校验。

---

## 开发指南

### 新增业务模块

1. **创建数据库表**：在 `sql/` 目录下添加 DDL 脚本
2. **创建实体类**：在 `project-core/src/main/java/com/example/core/entity/` 下创建实体，继承 `BaseEntity`
3. **创建 Mapper**：在 `project-core/src/main/java/com/example/core/mapper/` 下创建 Mapper 接口
4. **创建 Service**：在 `project-core/src/main/java/com/example/core/service/` 下创建接口和实现
5. **创建 Controller**：在 `project-admin/src/main/java/com/example/admin/controller/` 下创建 REST 接口
6. **创建 DTO**：在 `project-core/src/main/java/com/example/core/dto/` 下创建参数校验对象

### 代码规范

- **包命名**：`com.example.{模块名}`
- **命名约定**：
  - 实体类：`SysXxx`（对应 `sys_xxx` 表）
  - Mapper：`SysXxxMapper`
  - Service：`SysXxxService` / `SysXxxServiceImpl`
  - Controller：`SysXxxController`
  - DTO：`SysXxxDTO`

- **Jakarta 命名空间**：所有 import 使用 `jakarta.*`，禁止 `javax.*`
- **参数校验**：DTO 使用 `@NotBlank`、`@Size` 等 Jakarta Validation 注解
- **响应格式**：统一使用 `Result<T>` 包装返回值

### Redis 缓存使用

```java
@Service
public class SysUserServiceImpl {
    private final RedisTemplate<String, Object> redisTemplate;

    // 手动缓存
    public SysUser getById(Long id) {
        String cacheKey = "sys:user:" + id;
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached instanceof SysUser user) {
            return user;
        }
        SysUser user = sysUserMapper.selectById(id);
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, 30, TimeUnit.MINUTES);
        }
        return user;
    }
}
```

### 缓存管理器使用

```java
// 使用 Spring Cache 注解
@Service
public class DictService {
    @Cacheable(value = "dict", key = "#dictType")
    public List<DictData> getDictByType(String dictType) {
        // 查询数据库
    }

    @CacheEvict(value = "dict", key = "#dictType")
    public void clearCache(String dictType) {
        // 清除缓存
    }
}
```

### 分页查询

```java
// Controller
@GetMapping("/page")
public Result<Page<SysUser>> page(
        @RequestParam(defaultValue = "1") int pageNum,
        @RequestParam(defaultValue = "10") int pageSize,
        @RequestParam(required = false) String username) {
    return Result.success(sysUserService.page(pageNum, pageSize, username));
}

// Service
public Page<SysUser> page(int pageNum, int pageSize, String username) {
    Page<SysUser> page = new Page<>(pageNum, pageSize);
    LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
    if (StringUtils.hasText(username)) {
        wrapper.like(SysUser::getUsername, username);
    }
    wrapper.orderByDesc(SysUser::getCreateTime);
    return sysUserMapper.selectPage(page, wrapper);
}
```

### 全局异常处理

```java
// 抛出业务异常
throw new BusinessException("用户名已存在");
throw new BusinessException(400, "参数校验失败");

// 框架自动捕获并返回统一格式
{
    "code": 400,
    "msg": "用户名已存在",
    "data": null
}
```

---

## 部署指南

### 单机 Docker 部署

```bash
# 1. 构建项目
mvn clean package -DskipTests

# 2. 启动所有服务
docker-compose up -d

# 3. 查看状态
docker-compose ps

# 4. 查看日志
docker-compose logs -f admin
```

### Kubernetes 部署

```yaml
# deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: project-admin
spec:
  replicas: 2
  selector:
    matchLabels:
      app: project-admin
  template:
    metadata:
      labels:
        app: project-admin
    spec:
      containers:
      - name: project-admin
        image: project-admin:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "cloud"
        - name: NACOS_ADDR
          value: "nacos-service:8848"
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
        livenessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
        readinessProbe:
          httpGet:
            path: /api/actuator/health
            port: 8080
          initialDelaySeconds: 20
          periodSeconds: 10
---
apiVersion: v1
kind: Service
metadata:
  name: project-admin-service
spec:
  selector:
    app: project-admin
  ports:
  - port: 8080
    targetPort: 8080
  type: ClusterIP
```

### 环境变量配置

| 变量 | 说明 | 默认值 |
|------|------|--------|
| `SPRING_PROFILES_ACTIVE` | 运行模式 | `standalone` |
| `NACOS_ADDR` | Nacos 地址 | `localhost:8848` |
| `NACOS_NAMESPACE` | Nacos 命名空间 | 空（public） |

### JVM 调优参数

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar project-admin-1.0.0.jar
```

---

## 常见问题

### Q: 为什么选择 MyBatis-Plus 而不是 JPA？

MyBatis-Plus 对国内开发者更友好，学习曲线平缓，SQL 控制力强，且 `mybatis-plus-spring-boot3-starter` 已原生适配 Spring Boot 3。

### Q: Redis 序列化为什么不用 JDK 默认的？

JDK 默认序列化会导致：存储内容不可读、跨语言不兼容、体积大。自定义为 `StringRedisSerializer` + `GenericJackson2JsonRedisSerializer` 可解决这些问题。

### Q: 如何禁用某个可插拔组件？

确保对应的 `@ConditionalOnProperty` 属性为 `false`（或不设置），并且 POM 中将依赖设为 `<optional>true</optional>`。

### Q: 如何切换到 MySQL？

1. 修改 `application.yml` 中的 `driver-class-name` 为 `com.mysql.cj.jdbc.Driver`
2. 修改 `url` 为 MySQL 连接格式
3. 将 `MybatisPlusConfig` 中的 `DbType.POSTGRE_SQL` 改为 `DbType.MYSQL`
4. 替换 `pom.xml` 中的 PostgreSQL 驱动为 MySQL 驱动

### Q: 如何新增一个微服务模块？

1. 在根目录下创建新模块目录和 `pom.xml`
2. 在父 POM 的 `<modules>` 中添加新模块
3. 创建 Spring Boot 启动类
4. 添加 Nacos 依赖和配置
5. 在 Gateway 的路由配置中添加路由规则

### Q: 生产环境需要注意什么？

1. 关闭 Swagger UI（生产 Profile 中设置 `springdoc.api-docs.enabled=false`）
2. 关闭 SQL 日志输出（`mybatis-plus.configuration.log-impl` 注释掉）
3. 配置正确的 Redis 密码和数据库密码
4. 使用环境变量或 Nacos 管理敏感配置
5. 设置合理的 JVM 堆内存大小
6. 配置日志级别为 INFO 或 WARN
=======
# springboot3-init
Spring Boot 3 生产级初始化框架 一个同时支持单体架构与微服务架构的 Spring Boot 3 初始化框架，开箱即用，覆盖企业级开发所需的基础设施、可插拔组件和开发规范。
>>>>>>> 3281dab05928f0a732269d48f884436662b230cd
