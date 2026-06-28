import {
	API_URL
} from '@/env'

/**
 * 公共请求类
 */
class Request {
	constructor() {
		// 请求地址
		this.config.url = API_URL;
		// 请求拦截
		this.requestInterceptors();
	}

	config = {
		url: null,
		header: {
			'Content-Type': '	application/x-www-form-urlencoded',
			'Accept': 'application/json, text/plain, */*',
			//'Authorization': uni.getStorageSync('token')
		},
		method: 'GET',
		dataType: 'json',
		responseType: 'text',
	}

	// 是否验证登录
	isLogin = true;

	// 是否验证错误信息
	isError = true;

	/**
	 * get请求
	 * @param {string} url
	 * @param {object} params
	 */
	async get(url, params = {}) {
		this.config.data = params;
		return this.request(url);
	}

	/**
	 * post请求
	 * @param {string} url
	 * @param {object} data
	 */
	async post(url, data = {}, header = null) {
		this.config.method = 'POST';
		this.config.data = data;
		if (header) {
			this.config.header = header;
		}
		return this.request(url);
	}

	/**
	 * put请求
	 * @param {string} url
	 * @param {object} data
	 */
	async put(url, data = {}) {
		this.config.method = 'PUT';
		this.config.data = data;
		return this.request(url);
	}

	/**
	 * delete请求
	 * @param {string} url
	 */
	async delete(url) {
		this.config.method = 'DELETE';
		return this.request(url);
	}

	/**
	 * 请求
	 * @param {string} url
	 * @param {object} params
	 */
	async request(url, params = {}) {
		const newConfig = JSON.parse(JSON.stringify(this.config))
		newConfig.url = this.config.url + url;
		return await uni.request(newConfig);
	}

	/**
	 * 拦截
	 */
	async requestInterceptors() {
		let _this = this;
		uni.addInterceptor('request', {
			// 请求前
			async invoke(args) {
				// 验证是否登录
				if (_this.isLogin) {
					let check = await _this.checkLogin();
					if (!check) {
						// 关闭请求
						throw new Error('用户未登录')
					}
				}

				args.data.token = uni.getStorageSync('token');

				return args;
			},
			// 请求后
			async success(args) {
				console.log(args, 'zheskj1')
				// TODO: 请求后的处理，登录失效等
				if (args.statusCode === 200) {
					if (args.data.code == 101) { // 去登录
						uni.showToast({
							title: '请先登录',
							icon: 'none'
						});

						// TODO: 跳转登录页等处理
						setTimeout(() => {
							uni.navigateTo({
								url: '/pagesA/login/login'
							});
						}, 1500);
					} else if (args.data.code === -1 && _this.isError) {
						uni.showToast({
							title: args.data.msg,
							icon: 'none'
						});
					} else if (args.data.code === -99) {
						// uni.showToast({
						// 	title: args.data.msg,
						// 	icon: 'none'
						// });
						uni.showModal({
							title: '提示',
							content:args.data.msg,
							showCancel:false,
							success: function (res) {
								if (res.confirm) {
									console.log('用户点击确定');
								} else if (res.cancel) {
									console.log('用户点击取消');
								}
							}
						});
					}
				} else {
					uni.showToast({
						title: '请求失败',
						icon: 'none'
					});
				}

				return args;
			},
			// 请求失败
			async fail(err) {
				uni.showToast({
					title: '请求失败',
					icon: 'none'
				});
			},
			// 请求完成
			async complete(res) {
				// console.log('interceptor-complete')
			}
		});
	}

	/**
	 * 验证登录
	 */
	async checkLogin() {
		const token = uni.getStorageSync('token');
		console.log(token, '你要的')
		const userinfo = uni.getStorageSync('userinfo');
		if (!token || !userinfo.phone) {
			uni.showToast({
				title: '请先登录',
				icon: 'none'
			});

			// TODO: 跳转登录页等处理
			setTimeout(() => {
				uni.navigateTo({
					url: '/pagesA/login/login'
				});
			}, 1500);

			return false;
		}

		return token;
	}

}

export default new Request();