import App from './App'

// #ifndef VUE3
import Vue from 'vue'
import './uni.promisify.adaptor'
import config from 'common/config.js'
import store from 'store/index.js'
import common from 'common/common.js'
import uView from 'uview-ui'
import {router, RouterMount} from '@/common/utils/router'
import share from 'common/share.js'
Vue.use(uView)
Vue.use(router)
Vue.mixin(share)
Vue.prototype.config = config
Vue.prototype.common = common
Vue.config.productionTip = false

App.mpType = 'app'
const app = new Vue({
  ...App,
  store
  })

//v1.3.5起 H5端 你应该去除原有的app.$mount();使用路由自带的渲染方式
// #ifdef H5
RouterMount(app,router,'#app')
// #endif

// #ifndef H5
app.$mount(); //为了兼容小程序及app端必须这样写才有效果
// #endif



// 调用setConfig方法，方法内部会进行对象属性深度合并，可以放心嵌套配置
// 需要在Vue.use(uView)之后执行
uni.$u.setConfig({
	// 修改$u.config对象的属性
	config: {
		// 修改默认单位为rpx，相当于执行 uni.$u.config.unit = 'rpx'
		unit: 'px'
	},
	// 修改$u.props对象的属性
	props: {
		// 修改radio组件的size参数的默认值，相当于执行 uni.$u.props.radio.size = 30
		// radio: {
		// 	size: 15
		// }
		// 其他组件属性配置
		// ......
	}
})

// #endif

// #ifdef VUE3
import { createSSRApp } from 'vue'
import uView from 'uview-ui'
export function createApp() {
  const app = createSSRApp(App)
  app.use(uView)
  return {
    app
  }
}
// #endif