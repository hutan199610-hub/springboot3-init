# Spring Boot 3 生产级初始化框架 — AI 编码提示词

> **用途**：直接粘贴至 ClaudeCode / Codex / MiMoCode 的对话或系统提示中，生成一个可编译、可运行的 Spring Boot 3 多模块初始化工程。

---

## 一、角色设定

你是一位精通 Java 生态的资深架构师，擅长 Spring Boot 3 微服务与单体架构设计。
你的任务是：**从零生成一个生产级、可直接编译运行的 Spring Boot 3 初始化框架工程**，涵盖完整的模块划分、依赖管理、基础设施配置和可插拔组件集成。

---

## 二、硬性约束（必须严格遵守，违反任何一条即视为失败）

| 编号 | 约束项               | 说明                                                         |
| :--: | -------------------- | ------------------------------------------------------------ |
|  1   | **Java 版本**        | 必须使用 Java 17（Spring Boot 3.x 的硬性要求）               |
|  2   | **Spring Boot 版本** | 必须使用 3.2.x 或 3.3.x 的 GA（稳定发行）版本，不得使用 SNAPSHOT / RC / Milestone |
|  3   | **数据库**           | PostgreSQL（driver: `org.postgresql:postgresql`），禁止使用 MySQL 驱动 |
|  4   | **ORM 层**           | MyBatis-Plus，必须使用 `mybatis-plus-spring-boot3-starter`（适配 Boot 3，如 3.5.5+），禁止使用 `mybatis-plus-boot-starter`（仅适配 Boot 2） |
|  5   | **缓存**             | Redis，使用 Lettuce 连接池，必须自定义 `RedisTemplate` 序列化——Key 用 `StringRedisSerializer`，Value 用 `GenericJackson2JsonRedisSerializer`，杜绝 JDK 默认序列化导致的乱码 |
|  6   | **版本真实性**       | 所有依赖版本必须真实存在且互相兼容，禁止编造版本号。如不确定兼容版本，请标注 `[需确认版本]` 并给出最佳推测 |
|  7   | **编译通过**         | 项目必须能通过 `mvn clean compile` 零报错（假设本地已安装 JDK 17、Maven 3.8+） |
|  8   | **Jakarta 命名空间** | 代码中禁止出现 `javax.*` 包的 import，所有 Servlet / JPA / Validation 注解必须使用 `jakarta.*` |
|  9   | **配置格式**         | 配置文件使用 YAML 格式，区分 `application.yml`（主配置）和 `application-{profile}.yml`（环境配置） |

---

## 三、架构要求：同时支持单体与微服务

### 3.1 Maven 多模块结构

```
project-root/
├── pom.xml                          # 父 POM（统一依赖版本管理，BOM 导入）
├── project-common/                  # 公共模块：工具类、通用注解、统一响应、异常定义
│   └── pom.xml
├── project-core/                    # 核心模块：实体、DTO、Mapper 接口、Service 接口
│   └── pom.xml                      # （依赖 common）
├── project-admin/                   # 后台管理单体应用（Spring Boot 主启动类）
│   └── pom.xml                      # （依赖 core；可选依赖各可插拔组件 starter）
├── project-gateway/                 # [微服务模式] 网关（Spring Cloud Gateway）
│   └── pom.xml
├── project-auth/                    # [微服务模式] 认证授权服务
│   └── pom.xml
├── project-system/                  # [微服务模式] 系统管理服务（用户、角色、菜单、部门）
│   └── pom.xml
└── project-job/                     # [微服务模式] 定时任务服务（XXL-JOB）
    └── pom.xml
```

### 3.2 架构切换机制

- **单体模式**：仅启动 `project-admin`，它内部已包含所有功能模块的依赖和 Controller。通过 `spring.profiles.active=standalone` 激活。
- **微服务模式**：各服务独立启动，通过 Nacos 注册发现。网关路由配置在 `project-gateway` 的 YAML 中。通过 `spring.profiles.active=cloud` 激活。
- 两种模式共享同一套 `project-core` 和 `project-common` 代码，业务逻辑不重复。

---

## 四、父 POM 依赖版本矩阵

请在父 POM 中通过 `<dependencyManagement>` 统一管理以下版本：

| 组件                      | Maven Artifact（Boot 3 适配版）                              | 预期兼容版本区间                       |
| ------------------------- | ------------------------------------------------------------ | -------------------------------------- |
| Spring Boot               | `org.springframework.boot:spring-boot-starter-parent`        | 3.2.x ~ 3.3.x (GA)                     |
| Spring Cloud              | `org.springframework.cloud:spring-cloud-dependencies`        | 与 Boot 版本匹配的 release train       |
| Spring Cloud Alibaba      | `com.alibaba.cloud:spring-cloud-alibaba-dependencies`        | 2022.0.0.0+ 或 2023.0.x                |
| MyBatis-Plus              | `com.baomidou:mybatis-plus-spring-boot3-starter`             | 3.5.5+                                 |
| Redisson                  | `org.redisson:redisson-spring-boot-starter`                  | 3.23.x ~ 3.25.x                        |
| SpringDoc (Swagger)       | `org.springdoc:springdoc-openapi-starter-webmvc-ui`          | 2.3.x+                                 |
| Elasticsearch Java Client | `co.elastic.clients:elasticsearch-java`                      | 8.x                                    |
| MinIO                     | `io.minio:minio`                                             | 8.5.x+                                 |
| Flowable（工作流/审批流） | `org.flowable:flowable-spring-boot-starter`                  | 7.0.x+（适配 Boot 3）                  |
| XXL-JOB                   | `com.xuxueli:xxl-job-core`                                   | 2.4.x                                  |
| RocketMQ                  | `org.apache.rocketmq:rocketmq-spring-boot-starter`           | 2.3.x                                  |
| OpenFeign                 | `org.springframework.cloud:spring-cloud-starter-openfeign`   | 随 Spring Cloud BOM                    |
| Spring Security + OAuth2  | `spring-boot-starter-security` + `spring-boot-starter-oauth2-resource-server` | 随 Boot BOM                            |
| Spring AI                 | `org.springframework.ai:spring-ai-bom`                       | 1.0.0+（若已 GA，否则标注 `[需确认]`） |
| Lombok                    | `org.projectlombok:lombok`                                   | 1.18.30+                               |
| MapStruct                 | `org.mapstruct:mapstruct`                                    | 1.5.x+（可选，DTO 转换）               |

> **要求**：父 POM 中将上述版本声明为 `<properties>` 属性，子模块引用时不再硬编码版本号。

---

## 五、各组件集成规范（可插拔设计）

> **核心原则**：每个可插拔组件默认不激活，通过 Profile / 条件注解 / 模块依赖三选一来启用。

### 5.1 Redis + Redisson

- `project-common` 中配置 `RedisTemplate`（自定义序列化）。
- `RedissonClient` 通过 YAML 配置（`spring.redis.redisson.config`）初始化。
- 提供 `RedissonConfig` 配置类，使用 `@ConditionalOnProperty(name="redisson.enabled", havingValue="true")` 控制加载。
- 提供 `RedisCacheManager`，支持 `@Cacheable` 注解自动缓存。
- 缓存过期时间加入随机偏移量（0~300 秒），防止缓存雪崩。

### 5.2 Spring Security + OAuth2 + JWT

- `project-auth`（微服务模式）或 `project-admin`（单体模式）提供登录接口。
- 实现 RSA 公私钥加密传输密码（前端公钥加密，后端私钥解密）。
- 登录成功后生成 JWT Token，双重缓存到 Redis：
    - Token → UserInfo（用于鉴权）
    - UserId → Token（用于踢人下线）
- 实现 `HandlerInterceptor`，拦截请求、解析 Token、自动续期、注入 SecurityContext 或 ThreadLocal。
- 提供白名单配置（如 `/api/auth/login`、`/actuator/**`）。

### 5.3 Nacos（注册中心 + 配置中心）

- 通过 `spring.config.import=nacos:xxx` 方式导入远程配置（废弃 bootstrap.yml）。
- 提供 `application.yml` 模板，包含 Nacos 地址、命名空间、分组占位符。
- 关键配置（数据源、RSA 密钥）放入 Nacos 远程配置，本地只保留最小引导配置。
- 标注哪些类使用 `@RefreshScope` 支持动态刷新。

### 5.4 ElasticSearch

- 使用官方 `elasticsearch-java` 客户端（非旧版 Rest High Level Client）。
- 提供 `ElasticsearchConfig` 配置类，注入 `ElasticsearchClient` Bean。
- 通过 `@ConditionalOnProperty(name="elasticsearch.enabled", havingValue="true")` 控制加载。
- 提供示例 Repository（如用户搜索），演示索引创建、文档 CRUD、分页查询。

### 5.5 MinIO

- 提供 `MinioConfig` 配置类，注入 `MinioClient` Bean。
- 提供 `MinioService` 工具类，封装文件上传（支持分片）、下载、预签名 URL 生成、桶管理。
- 通过 `@ConditionalOnProperty` 控制加载。

### 5.6 工作流 / 审批流（Flowable）

- 使用 Flowable 7.x（适配 Spring Boot 3），而非 Activiti（对 Boot 3 支持较差）。
- 提供示例 BPMN XML 文件（如请假审批流程）。
- 提供 `FlowableConfig` 配置类，配置数据源和流程引擎。
- 封装：流程定义部署、启动流程实例、查询待办任务、审批（通过/驳回）、委托待办等 API。
- 通过 `@ConditionalOnProperty(name="flowable.enabled", havingValue="true")` 控制。

### 5.7 XXL-JOB

- 提供 `XxlJobConfig` 配置类，注册 `XxlJobSpringExecutor`。
- 提供示例 JobHandler，演示任务注册和执行。
- 通过 `@ConditionalOnProperty(name="xxljob.enabled", havingValue="true")` 控制。

### 5.8 RocketMQ

- 提供 `RocketMQConfig` 配置类。
- 提供示例 Producer（发送消息）和 Consumer（`@RocketMQMessageListener` 监听消息）。
- 通过 Maven Profile 控制是否引入依赖。

### 5.9 OpenFeign

- 在微服务模块中提供 Feign Client 接口示例（如调用 `project-system` 的用户服务）。
- 可选集成 Sentinel 作为熔断降级方案。
- 配置 Feign 请求拦截器，自动传递 Token 到下游服务。

### 5.10 Spring AI

- 若版本已 GA：提供 `SpringAIConfig` 配置类和一个 ChatClient 示例。
- 若版本未 GA：在 POM 中标注 `[需确认版本]`，提供占位模块和升级指南。

---

## 六、基础设施配置（必须包含）

以下配置类必须在 `project-common` 或 `project-admin` 中实现：

| 编号 | 配置项                     | 说明                                                         |
| :--: | -------------------------- | ------------------------------------------------------------ |
|  1   | **统一响应体 `Result<T>`** | 包含 `code`、`msg`、`data` 字段，提供 `success(T)` 和 `error(String)` 静态工厂方法 |
|  2   | **全局异常处理器**         | 处理 `MethodArgumentNotValidException`（→400）、`BusinessException`（→对应 code）、兜底 `Exception`（→500 + 日志） |
|  3   | **跨域配置 `CorsConfig`**  | `allowedOriginPattern("*")`，`allowCredentials(true)`，注册为最高优先级 Filter |
|  4   | **MyBatis-Plus 分页插件**  | 配置 `MybatisPlusInterceptor` + `PaginationInnerInterceptor(DbType.POSTGRE_SQL)` |
|  5   | **自动填充**               | 实现 `MetaObjectHandler`，自动填充 `createTime`、`updateTime` |
|  6   | **参数校验**               | DTO 使用 Jakarta Validation 注解，Controller 参数加 `@Valid` |
|  7   | **逻辑删除**               | 全局配置 `logic-delete-field`、`logic-delete-value`、`logic-not-delete-value` |
|  8   | **SpringDoc (Swagger UI)** | 配置 OpenAPI 信息、JWT Bearer 认证按钮                       |
|  9   | **Actuator**               | 暴露 health、info、metrics 端点                              |
|  10  | **Dockerfile**             | 基础镜像 `openjdk:17-jdk-alpine`，配置 JVM 参数和时区        |

---

## 七、代码生成输出规范

### 7.1 文件生成顺序

#### 第一阶段：POM 文件（5 个）

1. 父 `pom.xml`（BOM 管理、模块声明）
2. `project-common/pom.xml`
3. `project-core/pom.xml`
4. `project-admin/pom.xml`
5. `project-gateway/pom.xml`

#### 第二阶段：配置文件（4 个）

1. `application.yml`（公共配置）
2. `application-standalone.yml`（单体模式）
3. `application-cloud.yml`（微服务模式）
4. `logback-spring.xml`（日志配置）

#### 第三阶段：基础配置类（8 个）

1. `RedisConfig.java`
2. `MybatisPlusConfig.java`
3. `CorsConfig.java`
4. `SwaggerConfig.java`
5. `SecurityConfig.java`（基础框架）
6. `GlobalExceptionHandler.java`
7. `RedissonConfig.java`
8. `MetaObjectHandlerImpl.java`

#### 第四阶段：公共组件（4 个）

1. `Result.java`（统一响应）
2. `BusinessException.java`（业务异常）
3. `PageQuery.java`（分页查询基类）
4. `BaseEntity.java`（实体基类：id、createTime、updateTime、isDeleted）

#### 第五阶段：示例业务模块（5 个）

以"系统用户管理"为例：

1. `SysUser.java`（实体类，映射 `sys_user` 表）
2. `SysUserMapper.java`（Mapper 接口）
3. `SysUserService.java` + `SysUserServiceImpl.java`（含 Redis 缓存）
4. `SysUserController.java`（REST 接口，CRUD + 分页）
5. `SysUserDTO.java`（参数校验 DTO）

#### 第六阶段：可插拔组件配置类

每个组件一个配置类 + 一个示例使用类 + `@ConditionalOnProperty` 控制。

#### 第七阶段：Docker 部署文件

1. `Dockerfile`
2. `docker-compose.yml`（含 PostgreSQL、Redis、Nacos、MinIO、Elasticsearch 编排）

#### 第八阶段：SQL 初始化脚本

1. `init.sql`（PostgreSQL 语法，创建 sys_user 表、插入测试数据）

### 7.2 数据库建表示例（PostgreSQL）

```sql
CREATE TABLE sys_user (
    id          BIGSERIAL       PRIMARY KEY,
    username    VARCHAR(50)     NOT NULL UNIQUE,
    password    VARCHAR(100)    NOT NULL,
    nickname    VARCHAR(50),
    phone       VARCHAR(20),
    email       VARCHAR(100),
    status      SMALLINT        NOT NULL DEFAULT 1,
    is_deleted  SMALLINT        NOT NULL DEFAULT 0,
    create_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  sys_user            IS '系统用户表';
COMMENT ON COLUMN sys_user.id         IS '用户ID';
COMMENT ON COLUMN sys_user.username   IS '用户名';
COMMENT ON COLUMN sys_user.password   IS '密码（BCrypt 加密）';
COMMENT ON COLUMN sys_user.nickname   IS '昵称';
COMMENT ON COLUMN sys_user.phone      IS '手机号';
COMMENT ON COLUMN sys_user.email      IS '邮箱';
COMMENT ON COLUMN sys_user.status     IS '状态：1=正常，0=禁用';
COMMENT ON COLUMN sys_user.is_deleted IS '逻辑删除：0=正常，1=删除';
```

---

## 八、输出格式要求

每个文件按如下格式输出，便于直接按文件名创建：

```
## 文件路径: `project-admin/src/main/java/com/example/config/RedisConfig.java`
​```java
（完整代码，禁止 // ...省略 或 // TODO 占位）
```
```

文件之间用分隔线 `---` 隔开。

全部文件输出完毕后，附上一份 **"启动指南"**，包含：
- Maven 构建命令
- Profile 切换说明
- 默认账号密码
- 各服务访问地址

---

## 九、质量检查清单（生成完毕后自检）

- [ ] 所有 POM 的 `<parent>` 指向正确版本的 `spring-boot-starter-parent`
- [ ] 所有 import 使用 `jakarta.*` 而非 `javax.*`
- [ ] MyBatis-Plus 使用 `mybatis-plus-spring-boot3-starter` 而非旧版
- [ ] Redis 序列化已自定义，不使用 JDK 默认序列化
- [ ] 分页插件已配置，数据库类型指定为 `DbType.POSTGRE_SQL`
- [ ] 可插拔组件均使用 `@ConditionalOnProperty` 或 Maven Profile 控制
- [ ] SQL 脚本使用 PostgreSQL 语法（`BIGSERIAL`、`SMALLINT`、`COMMENT ON`）
- [ ] 无任何 SNAPSHOT / RC / Milestone 版本依赖
- [ ] 单体和微服务模式可通过 Profile 切换
- [ ] Dockerfile 基础镜像为 `openjdk:17-jdk-alpine` 或同级

---

## 十、使用建议

| 场景 | 建议 |
|---|---|
| **首次生成** | 直接使用本提示词，先生成全部文件，确认能编译通过 |
| **分阶段生成** | Token 限制时，可要求"先输出 POM + 配置 + 基础配置类"，确认后再要求"输出业务模块和可插拔组件" |
| **版本升级** | 替换版本表为你需要的具体版本，或要求 AI 自行查询最新兼容版本 |
| **定制业务** | 将"示例业务模块"部分替换为实际的业务表结构和接口需求 |
| **精简模式** | 删除不需要的可插拔组件行，减少生成量和依赖冲突风险 |
```