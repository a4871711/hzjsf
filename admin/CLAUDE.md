# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

基于 `renren-fast-vue` (v1.2.2) 定制的**矢历运动后台管理系统**——健身房业务后台。技术栈：Vue 2.5 + Element UI 2.13 + Vuex + vue-router，webpack 3 构建。后端是 renren-fast / renren-security（Java）。

业务域：门店(store)、教练(storecoach)、会员(storemember)、健身卡(fitcard)、优惠券(sysCoupon)、代理/合伙人(partner/agent5/agent6)、开门记录(openDoorRecord)、广告(advertising)、协议(protocol)、收支明细(incomepaydetail)，外加 renren 系统模块（user/role/menu/dept/log）。

## 常用命令

```bash
nvm use                 # Node 16（见 .nvmrc）。必须切到 16：webpack 3 在新版 Node（本机默认 v25）下会报 node-sass/构建错误
npm run dev             # 启动开发服务器，端口 8001，自动开浏览器
npm run build           # 生产构建：gulp → node build/build.js，产物到 dist/<版本号>/
npm run lint            # eslint --ext .js,.vue（standard 规则）
npm run unit            # jest 单元测试（test/unit/）
npm run unit -- -t "名称"   # 跑单个测试
npm run e2e             # nightwatch 端到端测试
```

**开发依赖后端**：dev server 把 `/proxyApi` 代理到 `http://127.0.0.1:8080/renren-security`（见 `config/index.js`），所以本地跑前端前要先起后端，否则所有接口 404。改代理目标也在这个文件。

环境变量：`.env`(dev) / `.env.production`(prod) 定义 `VUE_APP_URL`（接口域名）和 `VUE_APP_IMG`（图片/资源域名，组件里通过 mixin 暴露为 `this.baseUrl`）。

## 核心架构

### 动态菜单路由（关键，先理解这个）
路由不是静态写死的。`src/router/index.js` 的 `router.beforeEach` 里：登录后请求 `/sys/menu/nav` 拿菜单树，转换后调用 `fnAddDynamicMenuRoutes` 动态 `addRoutes`。每个菜单项的 `url`（如 `sys/store`）被映射成 `_import('modules/sys/store')` 即 `src/views/modules/sys/store.vue`，路由 name/path 是把 `url` 里的 `/` 换成 `-`。

**推论**：新建一个 `src/views/modules/...vue` 文件**不会**自己出现在系统里——必须后端菜单管理里有一条 url 指向它的菜单记录，前端才会注册路由并显示。开发非懒加载、生产懒加载（`import-development.js` / `import-production.js`）。

### HTTP 与 API 层
- `src/utils/httpRequest.js`：axios 实例，挂为 `this.$http`。`adornUrl()` 在 dev 加 `proxyApi/` 前缀、prod 加 `VUE_APP_URL`；`adornData()/adornParams()` 处理参数；另有 `http.get/post/put/del/body/addOrEdit/upload` 便捷方法。请求拦截器从 `localStorage.token` 注入 `token` 和 `Authorization` 头。响应拦截器：`code===0` 成功返回 `res`，`code===401` 弹框跳登录，其他码弹错误 toast。带全局 loading 遮罩（按并发请求计数）。
- `src/utils/apis.js`：**业务接口的集中定义**，挂为 `this.apis`。页面调后端基本都走 `this.apis.store_list(...)` 这种，而**不是** `src/api/system/*.js`（那批是 renren/RuoYi 遗留，业务代码基本不用）。加新接口就在 apis.js 里加一个方法。

### 配置驱动的 CRUD 页面（最高频的开发模式）
`src/components/global/` 下的组件由 `index.js` 用 `require.context` 自动注册，命名规则是 PascalCase → `r-` 前缀小驼峰：`r-search`、`r-table`、`r-form`、`r-formTable`、`r-list`、`r-showTable`、`r-treeTable`，以及图表组件和 `r-tinymce`。

业务页面（`src/views/modules/sys/*.vue`）几乎都是**纯配置**：在 `data()` 里声明 `searchForm`、`tableCols`、`tableHandles`、`formCols`、`formRules` 等数组，把按钮 `handle` 接到 `this.apis.*`，弹窗表单用 mixin 的 `this.elFormVisible()` 开关。

**新增一个 CRUD 页面的最快路径**：复制一个结构相近的现有模块（如 `store.vue`），改那几个配置数组和对应的 `this.apis` 方法名即可。

### 全局注入（在任意组件里直接可用）
`src/main.js` 往 `Vue.prototype` 挂了很多东西，组件里无需 import 直接 `this.xxx`：
- `this.apis` / `this.$http` / `this.dateutils`
- `this.parseTime`、`this.handleTree`、`this.download`、`this.resetForm`（来自 `utils/common.js`）
- `this.msgSuccess(msg)` / `this.msgError(msg)`、`this.checkBtn(perms)`、`this.isAuth(key)`
- 全局 mixin `src/mixins/utils.js`：`this.elFormVisible()`（开关 r-form 弹窗）、`this.baseUrl`（=`VUE_APP_IMG`）等

### 权限
- **按钮级**：`this.checkBtn('sys:store:update')`（`utils/common.js`，查 `window.btns_arr`），常用在配置项的 `isShow: e => this.checkBtn(...)`。`btns_arr` 在路由守卫里从 `/sys/menu/myList`（`type==2` 的 perms）加载并缓存到 localStorage。
- `isAuth(key)`（查 `localStorage.permissions`）是更老的方式。

### 登录与会话
`src/views/common/login.vue`：`apis.login` → `apis.getInfo`，然后 `localStorage.token` 和 `localStorage.role` **都被设成 `user.userId`**（非标准 JWT，注意这个 quirk）。`clearLoginInfo()`（`utils/index.js`）清 cookie + localStorage + 重置 vuex + 复位动态路由标志。注意 `router/index.js` 里 mainRoutes 的 token 校验目前是被注释掉的（TODO）。

## 约定与注意点
- **启动/排错先看 `启动文档.md`**：含从零启动步骤、登录方式（用户名+密码+后端 `captcha.jpg` 验证码）、以及常见报错排查表（页面能开但接口报错=后端 8080 没起；node-sass/webpack 报错=没切 Node 16；菜单空白/404=后端缺对应菜单记录）。
- 接口数据未跑通时优先用 mock：`src/mock/` 在非生产环境由 main.js 自动加载。
- Vuex 极薄：`store/modules/common.js`（菜单/tab 等 UI 状态）、`user.js`。`window.SITE_CONFIG`（来自 `static/config/index.js`）存全站初始状态用于重置。
- 主题：`element-ui-theme` 与 `src/assets/scss` 存在，但 main.js 里主题切换被注释，当前用 element-ui 默认 chalk 主题。
- 代码风格沿用 ES5 风格的 `var`、`for` 循环和回调，新代码贴合所在文件现有风格即可，不要顺手重构。
