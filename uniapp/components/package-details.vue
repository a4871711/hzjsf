<template>
	<view class="details">
		<u-popup :show="show" :round="15" mode="center" @close="close" @open="open"
			customStyle="width:593rpx;height:991rpx;">
			<view>
				<view class="details-title">购买后即刻生效</view>
				<view class="cont-box">
					<view class="sub-title" v-if="detailInfo.ctName">连续包{{detailInfo.ctName.substring(0,1)}}更优惠</view>
					<view class="cont">
						<view class="clr_h font_size_24 text_left">
							第{{detailInfo.nextPriceTitle}}个{{detailInfo.ctName.substring(0,1)}}</view>
						<view class="month-box">
							<view><text>￥</text><text
									class="price">{{detailInfo.nextPrice}}</text><text>/{{detailInfo.ctName.substring(0,1)}}</text>
							</view>
							<view class="month-y">第{{detailInfo.nextPriceTitle2}}个{{detailInfo.ctName.substring(0,1)}}
							</view>
						</view>
						<view class="steup-img">
							<view class="text-01">
								<text>￥</text>
								<text class="price">{{detailInfo.nextPrice2}}</text>
								<text>/{{detailInfo.ctName.substring(0,1)}}</text>
							</view>
							<view class="text-02">
								<view>第{{detailInfo.nextPriceTitle3}}个{{detailInfo.ctName.substring(0,1)}}及以后</view>
								<view class="text-price">
									<text>￥</text>
									<text class="price">{{detailInfo.nextPrice3}}</text>
									<text>/{{detailInfo.ctName.substring(0,1)}}</text>
								</view>
							</view>
						</view>
						<view class="steup-text">
							<text>首次续费</text>
							<text></text>
							<text></text>
						</view>
					</view>
				</view>
				<view class="cont-note">
					<view class="note-box">
						<view class="title">如何扣费</view>
						<view class="font_size">会员到期当天扣费,扣费前5天有消息通知</view>
					</view>
					<view class="note-box">
						<view class="title">如何取消</view>
						<view class="font_size">可在 我的-自动续费管理一键取消</view>
					</view>
				</view>
				<view class="custom-box">
					<button class="custom-btn custom-qx-style" @click="close">取消支付</button>
					<button class="custom-btn custom-kt-style" @click="handleCeateOrder">开通并购买</button>
				</view>
			</view>
		</u-popup>
	</view>
</template>

<script>
	import {
		getFitCardInfo
	} from '@/api/index'
	import {
		createOrder,
		getWxpayData
	} from '@/api/my.js'
	export default {
		name: "package-details",
		data() {
			return {
				detailInfo: {}
			};
		},
		props: {
			show: {
				type: Boolean,
				default: false
			},
			fitCardId: {
				type: Number,
				default: null
			},
			price: {
				type: String,
				default: null
			},
			storeAddrId: {
				type: Number,
				default: null
			},
			couponId: {
				type: String,
				default: ''
			}
		},
		watch: {
			fitCardId(value) {
				if (!value) {
					this.detailInfo = {};
					return;
				}
				this.getDetail();
			}
		},
		methods: {
			// 获取详情
			getDetail() {
				getFitCardInfo({
					id: this.fitCardId
				}).then((res) => {
					this.detailInfo = res.data;
				})
			},
			open() {
				console.log('open');
			},
			close() {
				this.$emit('handleClose')
				console.log('close');
			},
			// 创建购买订单
			handleCeateOrder() {
				let data = {
					paySum: this.price,
					fitCardId: this.fitCardId,
					couponId: this.couponId,
					storeAddressId: this.storeAddrId
				}
				createOrder(data).then((res) => {
					if (res.code == 1) {
						this.config.Toast('订单创建成功，准备签约')
						getWxpayData({
							orderNo: res.data.orderNo
						}).then((r) => {
							r.params.timestamp = r.params.timestamp * 1
							r.params.request_serial = r.params.request_serial * 1
							console.log('r.params====', r.params)
							uni.navigateToMiniProgram({
								appId: "wxbd687630cd02ce1d",
								path: 'pages/index/index',
								extraData: r.params,
								success(res) {
									console.log('res===', res);
									setTimeout(() => {
										uni.switchTab({
											url: '/pages/my/my'
										})
									}, 1000)

								},
								fail(e) {
									console.log('e===', e);
								}
							})

						});
					}
				});
			}
		}
	}
</script>

<style scoped>
	.cont-box {
		width: 523rpx;
		margin: 0 auto;
	}

	.details-title {
		color: red;
		width: 594rpx;
		height: 92rpx;
		line-height: 92rpx;
		background: linear-gradient(94deg, #FFB25F, #FF6E20);
		border-radius: 28rpx 28rpx 0rpx 0rpx;
		font-weight: bold;
		font-size: 33rpx;
		color: #FFFFFF;
	}

	.sub-title {
		font-weight: 500;
		font-size: 30rpx;
		color: #333333;
		margin: 53rpx 0 72rpx 0;
	}

	.month-box {
		color: #999999;
		display: flex;
		margin-top: 27rpx;
		font-size: 18rpx;
	}

	.price {
		font-weight: bold;
		font-size: 32rpx;
	}

	.month-y {
		margin: 13rpx 0 0 57rpx;
	}

	.steup-img {
		width: 514rpx;
		height: 214rpx;
		background-repeat: no-repeat;
		background-image: url('https://shilijsf.shilisports.com/h5/bg_img/steup.png');
		background-size: 100% 100%;
		position: relative;
	}

	.text-01 {
		position: absolute;
		left: 180rpx;
		top: 18rpx;
	}

	.text-01 text {
		color: #999999;
		margin-top: 27rpx;
		font-size: 18rpx;
	}

	.text-01 .price {
		color: #FFB187;
		font-size: 32rpx;
	}

	.text-02 {
		position: absolute;
		right: 0;
		top: 36rpx;
		color: #999999;
		font-size: 24rpx;
	}

	.text-02 text {
		margin-top: 27rpx;
		font-size: 18rpx;
	}

	.text-02 .text-price {
		margin-top: 10rpx;
	}

	.text-02 .text-price .price {
		color: #FF6E21;
		font-size: 32rpx;
	}

	.steup-text {
		font-size: 24rpx;
		color: #999;
		display: flex;
		justify-content: space-between;
	}

	.cont-note {
		width: 523rpx;
		height: 212rpx;
		background: #fff2e4;
		border-radius: 20rpx;
		padding: 26rpx;
		box-sizing: border-box;
		color: #999;
		text-align: left;
		margin: 40rpx auto 30rpx auto;
	}

	.cont-note .note-box {
		margin-bottom: 30rpx;
	}

	.note-box .title {
		font-size: 24rpx;
		font-weight: 500;
		color: #666;
		margin-bottom: 7rpx;
	}

	.custom-btn {
		width: 238rpx;
		height: 68rpx;
		border-radius: 34rpx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 25rpx;
	}

	.custom-qx-style {
		color: #FF6E21;
		border: 1px solid #FF6E21;
		background: #fff;
	}

	.custom-kt-style {
		background: #FF6E21;
		border: none;
		color: #fff;
	}

	.custom-box {
		width: 523rpx;
		margin: auto;
		display: flex;
		justify-content: space-between;
	}
</style>