<template>
	<view class="login">
		<view class="logo">
			<image src="/static/login_logo.png" class="img"></image>
		</view>

		<view>
			<button v-if="checked" class="flex_col_center btn font_size_30 active" open-type="getPhoneNumber"
				@getphonenumber="handlePhoneLogin">一键授权登录</button>
			<button :class="checked?'active': ''" class="flex_col_center btn font_size_30" @click="handlePhoneLogin"
				v-if="!checked">一键授权登录</button>
		</view>

		<view class="tips-txt">
			<u-checkbox-group activeColor="#DD541A">
				<u-checkbox :checked="checked" shape="circle" activeColor="#DD541A" labelSize="14" label="我已阅读并同意"
					@change="getchecked"></u-checkbox><text class="text_red"
					@click="config.path('/pagesA/agreement/agreement?type=0')">《用户协议》</text>、<text class="text_red"
					@click="config.path('/pagesA/agreement/agreement?type=1')">《隐私政策》</text>
			</u-checkbox-group>

		</view>

	</view>
</template>

<script>
	import {
		wxLogin,
		wxBindPhone
	} from '@/api/login.js'
	import {
		getMyStore,
	} from '@/api/index'
	import {
		setNowStore
	} from '@/api/my.js'
	export default {
		data() {
			return {
				checked: false, // 用户是否同意协议
				isShowPhone: false, // 是否要绑定手机号
				isLogin: false, // 用户是否完成登录
			}
		},
		methods: {
			getchecked(val) {
				this.checked = val
			},
			// 手机号授权登录
			handlePhoneLogin(e) {
				if (!this.checked) {
					this.config.Toast('请先阅读并同意用户协议！')
					return;
				}
				const that = this;
				uni.login({
					provider: 'weixin', //使用微信登录
					onlyAuthorize: true,
					success: function(loginRes) {
						console.log(loginRes, 'loginRes')
						if (loginRes.errMsg === 'login:ok') {
							let wxCode = loginRes.code
							// 请求后端微信登录接口
							wxLogin({
								wxCode: wxCode
							}).then((res) => {
								// res.data.userInfo.phone = null; //这里假设没有手机号，是新用户
								that.$store.commit('token', res.data.userInfo.token)
								uni.setStorageSync('token', res.data.userInfo.token);
								if (e.detail.errMsg = 'getPhoneNumber:ok') {
									let wxPhoneCode = e.detail.code
									// 用户绑定手机号
									wxBindPhone({
										wxCode: wxPhoneCode
									}).then((r) => {
										console.log(r, 'eeee')
										// 下一步跳转到首页
										if (r.code == 1) {
											that.config.Toast('登录成功')
											res.data.userInfo.phone = r.data.phone_info
												.phoneNumber
											that.$store.commit('userinfo', res.data.userInfo)
											uni.setStorageSync('userinfo', res.data.userInfo);
											that.isLogin = true;
											// 重新获取用户信息
											that.common.getUserInfo(that);
											// 切换门店
											that.getMyStore();
											// 有绑定手机号直接跳转个人界面
											setTimeout(() => {
												uni.switchTab({
													url: '/pages/my/my'
												})
											}, 1000)
										} else {
											// that.config.Toast()
											uni.showModal({
												title: '提示',
												content: r.msg,
												success: function(res) {
													if (res.confirm) {
														uni.makePhoneCall({
															phoneNumber: '13622620350'
														});
													} else if (res.cancel) {
														console.log('用户点击取消');
													}
												}
											});
										}
									});
								} else {
									that.$store.commit('token', null);
									uni.setStorageSync('token', null);
									that.config.Toast('已拒绝获取手机号登录！')
								}
							});
						} else {
							that.config.Toast('登录失败')
						}
					},
					fail(e) {
						console.log(e)
					}
				});
			},
			// 首页门店
			getMyStore() {
				let data = {
					offset: 0,
					limit: 1,
					userLng: this.$store.state.latilongi.longitude,
					userLat: this.$store.state.latilongi.latitude
				}
				getMyStore(data).then((res) => {
					let store = res.data[0];
					this.setNowStore(store.storeAddrId)
				});
			},
			// 切换门店保存
			setNowStore(storeAddrId) {
				let data = {
					token: this.$store.state.token,
					storeId: storeAddrId
				}
				setNowStore(data).then((r) => {

				});
			},
		},

		// 页面卸载，并且登录问完成情况下，清除token
		onUnload() {
			if (!this.isLogin) {
				this.$store.commit('token', null)
			}
		}
	}
</script>

<style scoped lang="scss">
	.login {
		text-align: center;
		position: absolute;
		top: 50%;
		left: 50%;
		transform: translate(-50%, -50%);
		width: 100%;

		.logo {
			margin-bottom: 50%;

			.img {
				width: 538rpx;
				height: 255rpx;
			}
		}

		.btn {
			width: 526rpx;
			height: 91rpx;
			background: #C3C3C3;
			border-radius: 45rpx;
			color: #FFFFFF;
			margin: auto;
		}

		.active {
			background: #DD541A;
		}

		.btn-red {
			background-color: #DD541A;
		}

		.tips-txt {
			margin-top: 60rpx;
			font-size: 26rpx !important;
			color: #999999;
			margin-left: 95rpx;

			.u-checkbox-group--row {
				justify-content: center;
			}

			.text_red {
				color: #DD541A;
			}

		}
	}
</style>