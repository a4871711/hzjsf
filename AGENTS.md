# AGENTS.md

This file provides guidance to Codex (Codex.ai/code) when working with code in this repository.

## 这是什么

健身房管理系统 **hzjsf**,前后端一体的 monorepo,三个独立子项目各自有更详细的 AGENTS.md——**动到哪个子项目就先读它的 AGENTS.md**,本文件只讲跨项目的大局,不重复其中细节。

| 子目录 | 角色 | 技术栈 | 权威文档 |
|--------|------|--------|----------|
| `Backend/` | Java 后端,出 WAR 部署到 Tomcat。提供后台管理(`sys`)+ 移动端(`api`)两套接口 | Spring MVC 4 + MyBatis-Plus + Shiro,Java 8 | `Backend/AGENTS.md`、`Backend/启动说明.md` |
| `admin/` | 后台管理前端,Vue SPA(renren-fast-vue 定制版「矢历运动后台」) | Vue 2 + Element UI,webpack 3,Node 16 | `admin/AGENTS.md`、`admin/启动文档.md` |
| `uniapp/` | 移动端会员端「矢历健身」(微信小程序为主,兼 H5 / App),即用户那一半前端 | uni-app + Vue 2 + uView UI + uni-simple-router,HBuilderX 构建 | `uniapp/AGENTS.md` |

## 系统全貌(读多文件才能拼出的关系)

后端一个 WAR 同时服务三类客户端:

- **`admin/` 这个 Vue 后台** → 走后端 **`sys` 模块**的 JSON 接口(`/sys/**`,如 `/sys/menu/nav`、`/sys/login`)。管理员身份由 **Shiro** 认证。
- **移动端会员端**(`uniapp/`,微信小程序 / H5 / App)→ 走后端 **`api` 模块**(`/api/**`)。鉴权**不在拦截器**,而在 `BaseController` 里按 token 查 Redis(详见 `Backend/AGENTS.md`「API 鉴权」);uniapp 侧把 token 塞进每个请求的 body/参数里(详见 `uniapp/AGENTS.md`)。此外 `Backend/h5/` 还有若干独立 H5 页面(分享/绑定等),与 uniapp 工程是两套东西。
- **后端自带的 FreeMarker/JSP 服务端页面**(`/renren-security/login.html` 等)与 `admin/` 是两套并存的后台入口,别混淆。

**一句话**:`admin/` 是「管理员后台」的前端,`uniapp/` 是「会员移动端」的前端,两者共用同一个 Backend WAR(分别走 `sys` / `api` 模块)。

## 本地起整套(前端依赖后端,顺序不能反)

后端的 Tomcat 上下文路径是 **`/renren-security`**(注意不是 war 名 `hzjsf`),前端 dev server 把 `/proxyApi` 代理到 `http://127.0.0.1:8080/renren-security`。**后端不先起,前端登录和所有接口都 404。**

```bash
# 1) 先起后端(JDK 8;首次需配好 db.properties / config.properties,见 Backend/启动说明.md)
cd Backend && mvn tomcat7:run        # → http://localhost:8080/renren-security

# 2) 再起后台前端(必须 Node 16,webpack 3 在新版 Node 下会挂)
cd admin && nvm use && npm run dev    # → http://localhost:8001(自动开浏览器)
```

> 移动端 `uniapp/` 不在这套命令行流程里:它用 **HBuilderX** 打开目录、运行到微信小程序/H5/App,且默认直连**线上** API(`uniapp/env.js`),所以改 uniapp 通常**不需要**本地起 Backend(详见 `uniapp/AGENTS.md`)。

| | 端口 / 路径 | 备注 |
|---|---|---|
| 后端 Tomcat | `:8080/renren-security` | 上下文路径 `/renren-security`,WAR 名却是 `hzjsf.war` |
| 后端自带后台 | `:8080/renren-security/login.html` | FreeMarker 服务端页面 |
| 移动端 API | `:8080/renren-security/api/` | |
| 前端 admin | `:8001` | 代理 `/proxyApi` → 后端 |
| 移动端 uniapp | HBuilderX 运行 | 默认直连**线上** API `https://shilijsf.shilisports.com/api`,不连本地 8080(见 `uniapp/env.js`) |
| 数据库 | MySQL,库名 **`shilijsf`** | 连接在 `Backend/src/main/resources/db.properties` |
| Redis | `127.0.0.1:6379` db=6 | 配置在 `Backend/.../config.properties` |

## 跨项目最容易踩的坑

- **改前端没生效 / 路由 404**:本前端路由是**动态菜单驱动**——新建 `.vue` 不会自动出现,要后端「菜单管理」里有一条 url 指向它的菜单记录才会注册路由(详见 `admin/AGENTS.md`)。
- **改后端 Mapper XML**:`sys` 的 XML 有热刷新、改完免重启;`api` 的 XML **必须重启**才生效(详见 `Backend/AGENTS.md`)。
- **首次编译后端报错**:`TeamClassOrderController.java` 与 `IncomePayDetailServiceImpl.java` 里有无效的 `import sun.dc.pr.PRError;`,删掉才能编译(见 `Backend/启动说明.md`)。
- **环境/版本切换靠手改**:后端无 Maven profile,DB 连接靠注释切换;admin 前端务必 `nvm use` 切 Node 16。
- **uniapp 不是命令行项目**:它用 HBuilderX 编译运行(`package.json` 没有 `scripts`),且默认连**线上** API 而非本地后端——本地起好 Backend 也不会让 uniapp 自动连过来,要改 `uniapp/env.js`(详见 `uniapp/AGENTS.md`)。
- **既无测试套件也无 CI**:后端 `src/test/java` 是工具示例非测试,`mvn package` 默认 `skipTests`;前端有 jest/nightwatch 但非常规跑。改动靠手动起服务验证,不要假设有测试网兜底。

## 进行中的工作

`Backend/需求文档/VIP会员权益转让_需求梳理与实现方案.md` —— 正在做的「VIP 权益卡(可独立购买、可整体过户)」需求:发起转让 → 转让人缴服务费 → 后台审核 → 受让人确认接收 → 受让人继承剩余有效期。系统里几乎全新(现有 `transferCard` 只是后台管理员直接转卡的影子功能,仅供参考、不可直接复用)。改这块前先读该文档。
