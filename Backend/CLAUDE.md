# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

健身房管理系统（hzjsf），基于 Spring MVC + MyBatis-Plus + Shiro 的传统 Java Web 应用，打包为 WAR 部署在 Tomcat 上。提供后台管理（`sys` 模块）和移动端 API（`api` 模块）两套接口。

## 技术栈

- Java 8, Spring 4.3.15, MyBatis 3.4.1 + MyBatis-Plus 1.5
- 数据库: MySQL 8.0.33（mysql-connector-java 8.0.33，Druid 连接池）
- 缓存: Redis（Jedis 2.9）
- 权限: 后台管理端用 Apache Shiro 1.8，配置在 Java `@Configuration` 类 `com.dlc.modules.sys.shiro.ShiroConfig`（**没有 spring-shiro.xml**），经 `web.xml` 的 `shiroFilter`（DelegatingFilterProxy）接入；移动端 API 的 token 校验**不在拦截器**里，而在 `BaseController` 的方法中按需调用（见下「API 鉴权」）
- 视图: FreeMarker（主） + JSP（备），模板目录 `/WEB-INF/views/`
- JSON 序列化: FastJSON（HTTP 消息转换器），部分场景使用 Jackson
- 构建: Maven，输出 WAR 包

## 构建命令

```bash
# 编译打包（surefire 配了 skipTests=true，默认跳过测试）；产物为 target/hzjsf.war
mvn clean package

# 使用内嵌 Tomcat 运行（端口 8080，上下文路径 /renren-security）
mvn tomcat7:run

# MyBatis Generator 生成代码（注意：generatorConfig.xml 内有硬编码的 RDS 连接和
# Windows 本地驱动 jar 路径，本机直接跑会失败，需先改连接与驱动路径）
mvn mybatis-generator:generate
```

> `src/test/java` 下有 4 个文件（IDcard、IdentityUtil、StreamTest、UUIDUtil），是工具/示例代码而非单元测试，项目实际没有测试套件。

## 代码架构

根包: `com.dlc`

### 模块划分

- `com.dlc.modules.sys` — 后台管理模块（管理员通过 Shiro 认证，`AbstractController` 提供 `getUser()/getUserId()/getDeptId()`；`sys.shiro` 子包含 `ShiroConfig`、`UserRealm`、`UnifiedAuthFilter`、`TokenService`、`ShiroUtils`、`RedisShiroSessionDAO` 等）
- `com.dlc.modules.api` — 移动端 API 模块（`BaseController` 提供 token→userId 解析，详见「API 鉴权」；`api.schedule` 子包放定时任务类）
- `com.dlc.modules.qd` — 友盟推送（UPush）及微信支付 SDK 工具（`qd.utils.WxPayUtils` 等）
- `com.dlc.common` — 公共组件（工具类、XSS 过滤、百度人脸 SDK、拦截器、自定义注解等）

### 每个模块内部结构

```
controller/  — 接口层
service/     — 业务接口
service/impl — 业务实现
dao/         — MyBatis Mapper 接口
entity/      — 数据库实体
vo/          — 视图对象
```

### API 响应约定

移动端 API 统一使用 `R` 类返回（`com.dlc.common.utils.R`，继承 HashMap）：
- `R.reOk(data)` — 成功，code=1
- `R.reError(msg)` — 失败，code=0
- `R.reError(CodeAndMsg)` / 抛 `RRException(CodeAndMsg)` — 失败码取自 `CodeAndMsg` 枚举（如 `101=未登录`、`102=账号被禁`，另有 -1~-36 等业务码）；异常由 `RRExceptionHandler`（`@ExceptionHandler`）兜底转成带该 code 的 `R`
- 后台管理端使用 `R.ok()` / `R.error()` — 成功 code=0，失败 code=-99

> 注意：移动端的「失败」并非只有 code=0，要按 `CodeAndMsg` 区分；不要假设 code∈{0,1}。

### API 鉴权（重点：不在拦截器里）

`spring-mvc.xml` 对 `/api/**` 注册了两个拦截器 `LoginInterceptor` 和 `OnLineInterceptor`，但**两者目前都是空壳**（`preHandle` 直接 `return true`，校验逻辑被注释）。因此 `exclude-mapping` 列表当前**不产生实际拦截效果**，仅作为「未来若启用拦截器」的占位说明。

真正的 token 校验在 `com.dlc.modules.api.controller.BaseController`，由各 Controller **按需调用**（非全局强制）：
- 取 token：先读 header `token`，为空再读请求参数 `token`
- 查 Redis：`redisUtils.get(ConfigConstant.USER + token)`（key 形如 `hzjsf_user_<token>`）拿到 userId 字符串
- `getUserId(req)` → 返回 `Long` userId；token 空或失效抛 `RRException(ERROR_USER_NOT_LOGIN)`
- `getUserVo(req)` → 在上面基础上查 `UserInfoVo` 并校验 `auditStatus`（=2 抛「已被禁用」`ERROR_USER_IS_LOCK`）
- `getUserVoIgnore(req)` → 同上但任何失败**静默返回 null**（用于可匿名访问的接口）

新增需要登录的 API 时，应在 Controller 方法里调用上述方法获取身份，而不是依赖拦截器。

### 关键配置文件

| 文件 | 用途 |
|------|------|
| `src/main/webapp/WEB-INF/web.xml` | Spring 上下文入口：`contextConfigLocation` 仅加载 `spring-mvc.xml` + `spring-jdbc.xml` + `spring-redis.xml`；注册 `shiroFilter`（DelegatingFilterProxy）与 Druid 监控 servlet `/druid/*`（账号 admin/xprs1218） |
| `src/main/resources/db.properties` | 数据库连接（多环境注释切换） |
| `src/main/resources/config.properties` | Redis、阿里云短信、抖音团购配置 |
| `src/main/resources/comm.properties` | 项目 URL、融云、百度人脸、二维码路径 |
| `src/main/resources/spring-mvc.xml` | MVC、消息转换器、两个 `/api/**` 拦截器、定时任务 |
| `src/main/resources/spring-jdbc.xml` | 数据源、事务 AOP、SqlSessionFactory、Mapper 热刷新；并通过 `context:property-placeholder` 加载上述三个 `.properties` |
| `src/main/resources/spring-redis.xml` | Redis 连接配置 |
| `src/main/resources/mybatis.xml` | MyBatis 全局设置（下划线转驼峰已开启） |
| `com.dlc.modules.sys.shiro.ShiroConfig`（Java 类） | Shiro 配置（SecurityManager、SessionManager、`ShiroFilterFactoryBean` 过滤链、可选 Redis 会话），**取代 XML** |
| `src/main/resources/logback.xml` | 运行时实际生效的日志配置（同时存在 `log4j.properties`，但 logback 为主） |
| `src/main/resources/generatorConfig.xml` | MyBatis Generator 配置；**内含硬编码 RDS 账号密码与 Windows 本地驱动 jar 路径**，`mvn mybatis-generator:generate` 在本机直接跑会失败，需先改驱动路径/连接 |

### MyBatis Mapper XML 位置

`src/main/resources/mapper/sys/` — 后台管理
`src/main/resources/mapper/api/` — 移动端 API

### 定时任务（`api.schedule` 包，`spring-mvc.xml` 中 `task:scheduled-tasks` 注册）

- `UpdateOrderStatusTask#updateOrderStatus` — `0 0 0/1 * * ?` 每小时更新过期订单状态（卡、商城、私教课、团体课）
- `DeleteFaceRecordTask#deleteFaceRecord` — `0 0 2 * * ?` 每天凌晨 2 点清理人脸识别数据
- `UpdateClassStatusTask#updateMemberStatus` — `0 0 0 * * ?` 每天凌晨 0 点处理会员过期
- `AutoPayTask#papAutoPay` — `0 0 8 * * ?` 每天早上 8 点发起微信委托代扣自动续费
- `EnergyToIntegralTask` 也在该包下，但**未加 `@Scheduled`、未在 XML 注册，当前不会自动执行**

### API 拦截器排除路径

`spring-mvc.xml` 的两个 `/api/**` 拦截器里定义了大量 `exclude-mapping`（注册/登录/支付回调/H5/设备/人脸等）。**但拦截器是空壳，这些排除项当前不生效**（见「API 鉴权」）。它们表达的是「设计上哪些路径应免登录」，若日后启用拦截器需照此维护；现阶段某接口是否需要登录，取决于其 Controller 是否调用了 `BaseController.getUserId/getUserVo`。

### 第三方集成

- 微信支付 / 支付宝支付（回调路径: `/api/pay/wxNotify`, `/api/pay/zfbNotify` 等）
- 微信委托代扣（`/api/wx/` 路径）
- 阿里云短信
- 抖音团购核销
- 百度人脸识别
- 融云 IM
- 七牛 / 阿里 OSS / 腾讯 COS 文件上传
- 友盟推送

### 前端 H5 页面

`h5/` 目录包含独立的移动端 H5 页面（jQuery + AUI 框架），用于分享/绑定/订单等场景，通过 `/api/h5/**` 和 `/api/share/**` 路径访问，不经过 token 校验。

## 注意事项

- 环境切换通过注释/取消注释 `db.properties` 中的 JDBC 连接来实现，没有 Maven profile
- 事务切面（`spring-jdbc.xml` 的 `txPointcut`）**只覆盖 `com.dlc.modules.api.service.impl` 和 `sys.service.impl`**（**不含 `qd`**）。`tx:advice` 列了 save/add/update/del 等前缀，但末尾还有 `<tx:method name="*" propagation="REQUIRED"/>` 兜底，所以这两个 impl 包下**所有方法**都是 REQUIRED，不限前缀；qd 模块的 service 不受事务管理
- Mapper XML 加载与热刷新是两回事：`SqlSessionFactory` 启动时加载**全部** `classpath:mapper/**/*.xml`；而 `MybatisMapperRefresh` 热刷新**只监听 `classpath:mapper/sys/*.xml`**（`delaySeconds=1, sleepSeconds=2`）。所以改 **sys** 的 Mapper XML 无需重启，改 **api** 的 Mapper XML **需要重启**才生效
- `sql/` 目录存放数据库增量变更脚本
