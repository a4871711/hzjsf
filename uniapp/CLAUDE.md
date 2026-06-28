# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 这是什么

健身房系统 **hzjsf** 的**移动端会员端「矢历健身」**(manifest `name: sljs`)——给最终用户(会员)用的那一半前端,跟 `admin/`(管理员后台)是两个完全独立的工程,只是都连同一个 Backend。本工程走后端 **`api` 模块**(`/api/**`),不碰 `sys`。

用 **uni-app** 一套代码多端发布:**微信小程序为主**(manifest 里有小程序 appid `wxdbb61533c8e04999`),兼容 H5 与 5+App(Android 权限已配)。

## 技术栈

- **uni-app + Vue 2**(`manifest.json` 的 `vueVersion: "2"`,代码里大量 `// #ifdef H5` / `#ifndef VUE3` 条件编译)
- **uView UI 2.x**(`uview-ui`)——通过 `pages.json` 的 `easycom` 规则,模板里直接写 `<u-xxx>` 即自动注册,无需 import
- **uni-simple-router 2.x**——前端路由 + 登录守卫(见下)
- **Vuex**(`new Vuex.Store`,经典写法)
- 样式:SCSS(`sass` / `sass-loader` 是仅有的 devDependencies)
- 二维码:`uni_modules/ikun-qrcode`

## 怎么跑(关键:没有命令行)

⚠️ **本工程不是 npm 项目**——`package.json` 里**没有 `scripts`**,不能 `npm run dev/build`。它靠 **HBuilderX** 跑:

1. HBuilderX 打开 `uniapp/` 目录
2. 「运行」→ 运行到微信小程序模拟器 / H5 / App
3. 编译产物落在 `unpackage/`(已被构建工具管理,别手改)

`vue.config.js` 里有个 devServer 代理(`/api` → `fmapi-test.bgzyedu.com`),但**实际请求不走它**:请求层用 `env.js` 里的**绝对** URL 直接拼接(见下),代理形同虚设。

### 接口域名在 `env.js`(默认直连线上)

```js
const ENV_BASE_URL = {
  development: 'https://shilijsf.shilisports.com/api',   // ← 默认就用这个(线上)
}
export const API_URL = ENV_BASE_URL[process.env.NODE_ENV || 'development'];
```

- **默认直连线上 API** `https://shilijsf.shilisports.com/api`,**不连本地 8080**。本地起好 Backend 也不会自动连过来——要联调本地后端,改 `env.js` 的 `development` 值。
- ⚠️ **`production` 这一项被注释掉了**:生产构建(`NODE_ENV=production`)会取到 `undefined` → `API_URL` 为空。正式发布前必须把 `production` 补回来。
- 路径已含 `/api` 前缀,所以 api 层里写 `/userInfo/proLogin`,完整请求是 `…/api/userInfo/proLogin`,对应后端 `api` 模块的 `/api/userInfo/proLogin`。

## 核心架构

### 请求层 `common/utils/request.js`(先理解这个)

导出一个**单例** `Request`(`export default new Request()`),基于 `uni.request` + `uni.addInterceptor('request', …)`。几个一定要知道的点:

- **token 放在 body/参数里,不是 header**:请求拦截器 `invoke` 里 `args.data.token = uni.getStorageSync('token')`。这对应后端 `BaseController` 先读 header 再读参数 token 的逻辑——本端走的是参数那条路。
- **登录校验靠 `Request.isLogin` 这个可变实例字段**,默认 `true`。`true` 时拦截器先 `checkLogin()`(查 storage 的 `token` 和 `userinfo.phone`),没登录就 toast 并跳 `/pagesA/login/login`、`throw` 掉请求。
- ⚠️ **两个坑叠在一起**:
  1. **注释是反话**——api 里满屏 `Request.isLogin = false; // 需要登录`,`false` 其实是**不校验**登录,注释写反了,别被骗。
  2. **单例状态污染**——`isLogin` / `isError` / `config` 都是单例上的字段,跨调用共享。某个 api 设了 `isLogin = true` 后,下一个**忘记显式设置**的 api 会**继承**上一次的值。新增 api 时务必在函数开头显式写明 `Request.isLogin = true/false`,不要依赖默认。
- **响应码约定**(拦截器 `success` 里处理,与后端 `api` 模块的 `CodeAndMsg` 对齐):
  - `code == 1` 成功
  - `code == 101` 未登录 → toast「请先登录」并跳登录页
  - `code == -1` → toast 错误 `msg`
  - `code == -99` → `showModal` 弹框提示 `msg`
  - 业务函数(`api/*.js`)里再额外判一次 `code === 1` 才返回数据,否则 reject

### API 层 `api/*.js`

按域拆分:`index.js`(首页/门店/会员卡)、`login.js`、`my.js`、`shop.js`、`coupon.js`。每个导出函数都是同一个模板:设 `Request.isLogin`,`await Request.post(path, params)`,判 `code === 1` 返回 `res.data` 否则 reject。**加新接口就照这个模板在对应文件加一个 async 函数。**

### 路由与分包

- 路由用 **uni-simple-router**(`common/utils/router.js`),`routes` 来自 `vue.config.js` 用 `uni-read-pages` 把 `pages.json` 注入的全局 `ROUTES`。`main.js` 里 `Vue.use(router)` + H5 用 `RouterMount` 挂载。
- **守卫**:`router.beforeEach` 对 `meta.isLogin` 为真的页面调 `checkLogin()`。注意 `request.js` 里也有一套 `checkLogin`——**两处各管一摊**(路由进入 vs 发请求),逻辑相似但不是同一个函数。
- **分包**(`pages.json` + `manifest.json` 的 `optimization.subPackages: true`):
  - `pages/`(主包):4 个 tabBar 页 `index` / `shop` / `member-card` / `my`
  - `pagesA/`(分包):其余页面(`login`、`open_code` 开门码、`card_renewal` 购卡续费、`coupon`、`renew` 自动续费、`vipAgreement` 会员协议、`write_off`/`verification_record` 团购核销、`select_store` 等)
  - 新增页面记得在 `pages.json` 注册,跨主/分包放对位置。

### 全局注入(`main.js`,组件里直接 `this.xxx`)

- `Vue.prototype.config` = `common/config.js`:`this.config.Toast(msg)`、`this.config.path(url, isReLaunch)`(封装 navigateTo/reLaunch)、`this.config.timestampToDateTime(ts, type)`
- `Vue.prototype.common` = `common/common.js`:`this.common.getUserInfo(this)`(拉用户信息存 vuex+storage)、`this.common.getMyLocation(this)`(定位)
- 全局 mixin `common/share.js`:给每个页面注入 `onShareAppMessage` / `onShareTimeline` 默认分享卡片
- `Vue.use(uView)`:uView 组件与 `uni.$u.*` 工具全局可用

### 状态与存储(双写)

`store/index.js` 是个薄 Vuex:`token` / `config` / `userinfo` / `latilongi`,只有 mutations。**登录态同时写在 Vuex 和 `uni.getStorageSync`**(`token`、`userinfo`)——请求/守卫读的是 **storage**,页面渲染常读 vuex,改登录态时两边都要更新,否则会出现"页面显示已登录但请求被判未登录"之类的不一致。

## 与后端的对接关系

| 本端 | 后端 |
|------|------|
| `env.js` 的 `API_URL`(含 `/api`) | Backend `api` 模块,`/api/**` |
| 请求 body 里的 `token` | `BaseController.getUserId/getUserVo` 从参数取 token 查 Redis |
| 响应 `code`(1/101/-1/-99…) | `com.dlc.common.utils.R` + `CodeAndMsg` 枚举 |
| 微信登录 `/userInfo/proLogin`、绑手机 `/userInfo/proPhone` | api 模块对应 Controller |

改后端接口契约时,这两端要一起看(后端细节见 `Backend/CLAUDE.md`「API 响应约定」「API 鉴权」)。

## 进行中的工作

「VIP 权益卡(可独立购买、可整体过户)」需求在三端联动(后端 `api`/`sys`、`admin/`、本端)。本端相关页面:`pages/member-card`、`pagesA/card_renewal`、`pagesA/vipAgreement`、`pagesA/renew`。需求文档见 `Backend/需求文档/VIP会员权益转让_需求梳理与实现方案.md`,改这块前先读它。
