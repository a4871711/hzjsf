<template>
	<view class="write_off">
		<view class="title">
			<view style="display: flex;align-items: center;" @click="show=true">
				<view style="margin-right: 10rpx;">{{currentItem.storeName?currentItem.storeName:'选择门店'}}</view>
				<u-icon name="arrow-down" size="16"></u-icon>
			</view>
			<view></view>
		</view>
		<view class="input-box flex_s">
			<image class="icon-mt" v-if="type==1" src="../../static/image/index_mt.png"></image>
			<image class="icon-dy" v-else src="../../static/image/index_dy.png"></image>
			<input v-model="value" class="uni-input" focus :placeholder="type==1?'请输入美团券码':'请输入抖音券码'" />
			<image class="icon-sm" src="../../static/image/user_sm.png" @click="saoCode"></image>
		</view>

		<view class="btn flex_1" @click="submit">确认验券</view>

		<view class="record" @click="config.path('/pagesA/verification_record/verification_record')">查看核销记录</view>

		<view class="methods">
			<view class="title">抖音/美团验券使用方法 </view>
			<view class="steup">步骤一: 输入券码或者扫码完成验券</view>
			<view class="steup">步骤二: 验券成功后获得对应优惠券</view>
			<view class="steup">步骤三: 回到购卡界面，点击对应卡种</view>
			<view class="steup">步骤四: 点击优惠券管理，勾选优惠券</view>
			<view class="steup">步骤五: 支付0.01，成功支付即可扫码入场</view>
		</view>
		<u-picker :show="show" @confirm='confirm' @cancel="close" @close="close" close-on-click-overlay
			keyName="storeName" :columns="columns"></u-picker>
	</view>
</template>

<script>
	import {
		getStoreList
	} from '@/api/shop.js'
	import {
		getSendSysCoupon
	} from '@/api/coupon.js'
	import {
		getMyStore,
	} from '@/api/index'
	export default {
		data() {
			return {
				value: '',
				type: 1,
				columns: [],
				show: false,
				currentItem: {},
				latitude: null,
				longitude: null
			};
		},
		onLoad(options) {
			// 接收参数 1是美团  2 是抖音		
			this.type = options.type
			this.init()
			// this.getLocation()
			this.getMyStore()
		},
		methods: {
			// getLocation() {
			// 	uni.getLocation({
			// 		type: 'wgs84', // 返回 GPS 坐标（与接口数据一致）
			// 		success: (res) => {
			// 			console.log(res, 'e')
			// 			this.latitude = res.latitude
			// 			this.longitude = res.longitude
			// 			this.findNearestStore()
			// 		},
			// 		fail: (err) => {
			// 			console.log(err)
			// 		}
			// 	})
			// },

			confirm(e) {
				console.log(e, 'e')
				this.currentItem = e.value[0]
				this.show = false
			},
			close(e) {
				console.log(e, 'eeee')
				this.show = false
			},
			async init() {
				this.columns = []
				const res = await getStoreList({
					page: 1,
					limit: 9999
				})
				if (res.code == 1) {
					this.columns.push(res.page.list)
				}
			},
			async submit() {
				if (!this.currentItem.storeName) {
					this.config.Toast('请选择门店')
				}
				const res = await getSendSysCoupon({
					couponId: '',
					couponCode: this.value,
					goodsIdStoreId: this.currentItem.goodsIdStoreId,
					storeAddrId: this.currentItem.storeAddrId,
					verifyType: this.type == 2 ? 'douyin' : ''
				})
				if (res.code == 1) {
					this.config.Toast('验券成功')
					this.value = ''
				} else {
					this.config.Toast('验券失败')
				}
			},
			saoCode() {
				const that = this

				if (that.type == 1) {
					uni.scanCode({
						success: async (res) => {
							// 成功回调，res.result 是扫描结果
							console.log(res.result, 'jjjjjjjj');
							that.value = res.result
							// 这里可以根据扫描结果进行后续处理
						},

						// 可以指定来源是相册还是相机，默认二者都有
						sourceType: ['album', 'camera']
					});
				} else {
					if (!that.currentItem.storeName) {
						return that.config.Toast('请选择门店')
					}
					uni.scanCode({
						success: async (res) => {
							// 成功回调，res.result 是扫描结果
							console.log(res.result, 'jjjjjjjj');
							const k = await getSendSysCoupon({
								couponId: '',
								couponCode: res.result,
								goodsIdStoreId: that.currentItem.goodsIdStoreId,
								storeAddrId: that.currentItem.storeAddrId,
								verifyType: that.type == 2 ? 'douyin' : ''
							})
							if (k.code == 1) {
								that.config.Toast('验券成功')
							} else {
								that.config.Toast('验券失败')
							}
							// 这里可以根据扫描结果进行后续处理
						},

						// 可以指定来源是相册还是相机，默认二者都有
						sourceType: ['album', 'camera']
					});
				}

			},


			// 首页门店
			getMyStore() {
				let that = this;
				let data = {
					offset: 0,
					limit: 1,
					userLng: that.$store.state.latilongi.longitude,
					userLat: that.$store.state.latilongi.latitude,
				}
				getMyStore(data).then((res) => {
					let store = res.data[0];
					// store.storeImgUrl = store.storeImgUrl.split(',').map(url => url.trim());
					store.distance = parseFloat((store.distance / 1000).toFixed(2));
					console.log(store, 'store')
					that.currentItem = store
				});
			},
			// 从相册选择图片或拍照
			chooseImage() {
				uni.chooseImage({
					count: 1, // 默认9，设置为1表示只能选择一张图片
					sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
					sourceType: ['album', 'camera'], // 可以指定来源是相册还是相机，默认二者都有
					success: (res) => {
						// 成功回调，res.tempFilePaths 是图片的临时文件路径数组
						console.log(res.tempFilePaths);
						// 这里可以将照片显示在页面上，或者上传到服务器等操作
					},
					fail: (err) => {
						// 失败回调
						console.error(err);
					}
				});
			}
		}
	}
</script>

<style lang="scss">
	.title {
		display: flex;
		justify-content: space-between;
		margin: 0 30rpx 30rpx;
		font-size: 28rpx;
	}

	.write_off {
		padding: 35rpx;
		box-sizing: border-box;

		.input-box {
			width: 90%;
			margin: auto;
			padding: 22rpx;
			box-sizing: border-box;
			height: 74rpx;
			background: #E9E9E9;
			border-radius: 20rpx;

			.icon-dy {
				width: 31rpx;
				height: 36rpx;
			}

			.icon-mt {
				width: 48rpx;
				height: 36rpx;
			}

			.icon-sm {
				width: 31rpx;
				height: 31rpx;
			}

			.uni-input {
				border-left: 1px solid #999999;
				flex: 1;
				margin: 0 30rpx;
				padding-left: 15rpx;
			}
		}

		.btn {
			margin: auto;
			width: 90%;
			height: 91rpx;
			background: #373838;
			border-radius: 45rpx;
			margin-top: 95rpx;
			margin-bottom: 46rpx;
			color: #fff;
			font-size: 30rpx;
		}

		.record {
			color: #999999;
			text-decoration-line: underline;
			font-size: 30rpx;
			text-align: center;
			line-height: 36rpx;
		}

		.methods {
			width: 90%;
			margin: 33rpx auto;
			height: 228rpx;
			background: #FFFFFF;
			box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);
			border-radius: 20rpx;
			padding: 25rpx 22rpx;
			box-sizing: border-box;
			line-height: 34rpx;

			.title {
				font-size: 32rpx;
				color: #373838;
				font-weight: bold;
				margin-bottom: 18rpx;
			}

			.steup {
				font-weight: 500;
				font-size: 24rpx;
				color: #999999;
			}
		}
	}
</style>