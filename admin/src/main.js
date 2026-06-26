import Vue from 'vue'
import App from '@/App'
import router from '@/router'                 // api: https://github.com/vuejs/vue-router
import store from '@/store'                   // api: https://github.com/vuejs/vuex
import VueCookie from 'vue-cookie'            // api: https://github.com/alfhen/vue-cookie
import ElementUI from 'element-ui'                       // api: https://github.com/ElemeFE/element
import '@/icons'                              // api: http://www.iconfont.cn/
//import '@/element-ui-theme'
import 'element-ui/lib/theme-chalk/index.css';
import '@/assets/scss/index.scss'
import httpRequest from '@/utils/httpRequest' // api: https://github.com/axios/axios
import { isAuth } from '@/utils'
import cloneDeep from 'lodash/cloneDeep'
import mixins from "./mixins/utils.js"
import '@/components/global'
import VueJsonp from 'vue-jsonp'
Vue.use(VueCookie).mixin(mixins).use(ElementUI).use(VueJsonp)
Vue.config.productionTip = false

// 非生产环境, 适配mockjs模拟数据                 // api: https://github.com/nuysoft/Mock
if (process.env.NODE_ENV !== 'production') {
  require('@/mock')
}
import * as filters from './filters'
Object.keys(filters).forEach(key=>{
    Vue.filter(key,filters[key])//插入过滤器名和对应方法
})
// 挂载全局
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, download, handleTree, checkBtn } from "@/utils/common";
// Vue.prototype.getDicts = getDicts
// Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
Vue.prototype.checkBtn = checkBtn

Vue.prototype.msgSuccess = function (msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
}

Vue.prototype.msgError = function (msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
}

Vue.prototype.$http = httpRequest // ajax请求方法
Vue.prototype.isAuth = isAuth     // 权限方法

import apis from './utils/apis';
Vue.prototype.apis=apis;
import dateutils from './utils/dateutils';
Vue.prototype.dateutils=dateutils;

// 保存整站vuex本地储存初始状态
window.SITE_CONFIG['storeState'] = cloneDeep(store.state)

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  template: '<App/>',
  components: { App }
})
