import Vue from 'vue'

import Vuex from 'vuex'
Vue.use(Vuex); //vue的插件机制
const store = new Vuex.Store({
	state: {
		token: '',
		config: {},
		userinfo: {},
		latilongi:{},
		//公共的变量，存储数据，这里的变量不能随便修改，只能通过触发mutations的方法才能改变
	},

	mutations: {
		token(state, newVal) {
			state.token = newVal
		},
		config(state, newVal) {
			state.config = newVal
		},
		userinfo(state, newVal) {
			state.userinfo = newVal
		},
		latilongi(state, newVal) {
			state.latilongi = newVal
		}
		//相当于同步的操作

	},

	actions: {

		//相当于异步的操作,不能直接改变state的值，只能通过触发mutations的方法才能改变

	}

})

export default store