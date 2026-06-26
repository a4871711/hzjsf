import Vue from 'vue'
import axios from 'axios'
import router from '@/router'
import qs from 'qs'
import merge from 'lodash/merge'
import {
	clearLoginInfo
} from '@/utils'
import {
	MessageBox,
	Message,
	Loading
} from 'element-ui'

let requestingCount = 0;
let loading // 定义loading变量

function startLoading() { //使用Element loading-start 方法
	loading = Loading.service({
		lock: true,
		text: '加载中……',
		background: 'rgba(0, 0, 0, 0)'
	})
}

function stopLoading() { //使用Element loading-close 方法
	loading.close()
}

const handleRequestLoading = () => {
	if (requestingCount === 0) {
		startLoading()
	}
	requestingCount++
}
const handleResponseLoading = () => {
	//Loading.service().close();
	if (requestingCount <= 0) return
	requestingCount--
	if (requestingCount === 0) stopLoading()
}

const http = axios.create({
	timeout: 1000 * 15,
	// withCredentials: true, // send cookies when cross-domain requests
	headers: {
		'Content-Type': 'application/x-www-form-urlencoded; charset=utf-8'
	}
})

/**
 * 请求拦截
 */
http.interceptors.request.use(config => { 
	console.log(config);
	if (!config.noAuthorization) {
		config.headers['Authorization'] = localStorage.token // 请求头带上token
	}
	config.headers['token'] = localStorage.token // 请求头带上token
	// config.headers['token'] = Vue.cookie.get('token') // 请求头带上token
	config.headers['Content-Type'] = 'application/x-www-form-urlencoded'
	if (config.contentType == 'json') {
		config.headers['Content-Type'] = 'application/json; charset=utf-8';
	} else if (config.contentType == 'raw') {
		config.headers['Content-Type'] = 'application/json';
	}

	if (!config.noLoading) {
		handleRequestLoading();
	}

	for (var key in config.headers) {
		if (!config.headers[key]) {
			delete config.headers[key]
		}
	}

	return config
}, error => {
	return Promise.reject(error)
})

/**
 * 响应拦截
 */
var is401 = false;
http.interceptors.response.use(response => { 
	handleResponseLoading();
	// console.log(response);
	//TODO
	const res = response.data

	if (response.data && response.data.message === 'query ok') { // 腾讯api返回
		return response.data.result
	}

	// if (response.data && response.data.code === 401) { // 401, token失效
	//    clearLoginInfo()
	//    router.push({ name: 'login' })
	// 	return
	//  }

	if (res.code == 401) {
		if (is401) {
			return
		}
		is401 = true;
		MessageBox({
			message: '登录已过期，请重新登录！',
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning',
			closeOnClickModal: false,
			callback(res) {
				router.push({
					path: 'login'
				})
				clearLoginInfo()
			}
		})
		return Promise.reject(new Error(res.msg || 'Error'))
	} else if (res.code != 0) {
		if (response.request.responseURL.indexOf('sys/menu/list') != -1 || response.request.responseURL.indexOf('sys/dept/list') != -1) {
			return res
		}

		Message({
			message: res && res.msg ? res.msg : '出错了！',
			type: 'error',
			duration: 5 * 1000,
			customClass: 'zZindex'
		})
		return Promise.reject(new Error(res ? res.msg : '出错了' || 'Error'))
	} else {

		return res
	}
}, error => {
	handleResponseLoading();
	const res = error.response.data

	if (res && res.code == 401) {
		Message({
			message: '登录过期',
			type: 'error',
			duration: 5 * 1000,
			customClass: 'zZindex'
		})
		// clearLoginInfo()
		// router.push({ name: 'login' })
	} else {
		Message({
			message: '服务器异常',
			type: 'error',
			duration: 5 * 1000,
			customClass: 'zZindex'
		})
	}

	return Promise.reject(error)
})

/**
 * 请求地址处理
 * @param {*} actionName action方法名称
 */
http.adornUrl = (actionName) => {
	// 非生产环境 && 开启代理, 接口前缀统一使用[proxyApi/]前缀做代理拦截!
	//return (process.env.NODE_ENV !== 'production' && process.env.OPEN_PROXY ? 'proxyApi/' : process.env.VUE_APP_URL) + actionName
	if (process.env.NODE_ENV !== 'production') {
		return `proxyApi/${actionName}`
	} else {
		return process.env.VUE_APP_URL + actionName
	}
}

/**
 * get请求参数处理
 * @param {*} params 参数对象
 * @param {*} openDefultParams 是否开启默认参数?
 */
http.adornParams = (params = {}, openDefultParams = false) => {
	var defaults = {
		't': new Date().getTime()
	}
	return openDefultParams ? merge(defaults, params) : params
}

/**
 * post请求数据处理
 * @param {*} data 数据对象
 * @param {*} openDefultdata 是否开启默认数据?
 * @param {*} contentType 数据格式
 *  json: 'application/json; charset=utf-8'
 *  form: 'application/x-www-form-urlencoded; charset=utf-8'
 */
http.adornData = (data = {}, openDefultdata = false, contentType = 'form') => {
	var defaults = {
		't': new Date().getTime()
	}
	data = openDefultdata ? merge(defaults, data) : data
	return contentType === 'json' ? JSON.stringify(data) : qs.stringify(data)
}

http.get = (url, query) => {
	return http({
		url: http.adornUrl(url),
		method: 'get',
		params: query
	})
}

http.post = (url, data) => {
	return http({
		url: http.adornUrl(url),
		method: 'post',
		data: qs.stringify(data)
	})
}

http.body = (url, data) => {
	if (data.id) {
		return http({
			url: http.adornUrl(url),
			method: 'PUT',
			data: data
		})
	} else {
		return http({
			url: http.adornUrl(url),
			method: 'post',
			data: data
		})
	}
}

http.addOrEdit = (url, data, method = 'post') => {
	if (method == 'post') {
		return http({
			url: http.adornUrl(url),
			method: 'post',
			data: qs.stringify(data),
			headers: {
				'Content-Type': 'application/x-www-form-urlencoded',
			}
		})
	} else {
		return http({
			url: http.adornUrl(url),
			method: 'PUT',
			data: qs.stringify(data)
		})
	}
}

http.put = (url, data) => {
	return http({
		url: http.adornUrl(url),
		method: 'PUT',
		data: data
	})
}

http.del = (url, query) => {
	return http({
		url: http.adornUrl(url),
		method: 'delete',
		params: query
	})
}

http.upload = (url, data) => {
	return http({
		url: http.adornUrl(url),
		method: 'post',
		data: data,
	})
}

export default http
