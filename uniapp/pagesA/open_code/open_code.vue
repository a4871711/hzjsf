<template>
	<view class="open-code">
		<!-- 非会员 -->
		<view class="no-vip flex_col_center" v-if="!isVip">
			<view class="clr_h font_size_32 marg_bottom_40">请先成为会员</view>
			<view class="btn flex_1" @click="config.path('/pagesA/card_renewal/card_renewal?openVip=1')">立即购卡</view>
		</view>

		<!-- 月卡会员二维码 -->
		<view class="y-vip vip-box" v-if="codeInfo.data.type != 10 && isVip">
			<view class="code-img flex_col_center">
				<!-- <image src="/static/code.png" class="img"></image> -->
				<ikun-qrcode width="400" height="400" unit="rpx" color="#000000" :data="qrcodeUrl"></ikun-qrcode>
			</view>
			<view class="sx flex_col_center clr_h font_size_30">
				<view>二维码4秒刷新一次</view>
				<view>请打开页面后迅速放置在开门器前</view>
				<view>二维码不可分享</view>
			</view>
		</view>

		<!-- 次卡会员二维码 -->
		<view class="c-vip vip-box" v-if="isVip && codeInfo.data.type === 10">
			<view class="flex_col_center">
				<view class="txt-top flex_col_center border_r_20">
					<text>二维码4秒刷新一次</text>
					<text>请打开页面后迅速放置在开门器前</text>
					<text>二维码不可分享</text>
				</view>
				<view v-if="isEffective" class="ewm-box">
					<view class="code-img" style="filter: grayscale(100%);">
						<image src="/static/code1.png" class="img" style="filter: grayscale(100%);"></image>
					</view>
					<view class="show-qrcode" @click="handleShowQrcode">展示二维码</view>
				</view>

				<view v-if="!isEffective">
					<view class="code-img">
						<view class="content">
							<ikun-qrcode width="400" height="400" unit="rpx" color="#000000"
								:data="qrcodeUrl"></ikun-qrcode>
						</view>
					</view>
				</view>

				<view class="time">
					<view class="sx flex_col_center clr_h font_size_30" v-if="isEffective">
						<view>展示二维码后，次卡剩余次数减<text>1</text></view>
						<view>展示二维码后<text>120</text>分钟内</view>
						<view>凭借二维码可<text>无限次</text>进出健身房</view>
					</view>
					<view v-if="!isEffective">
						<view class="yx flex_s clr_h font_size_30">
							<text>亮码时间：</text>
							<text>{{codeInfo.createtime}}</text>
						</view>
						<view class="flex_s clr_h font_size_30">
							<text>当前二维码有效期至：</text>
							<text>{{codeInfo.lasttime}}</text>
						</view>
					</view>
				</view>
				<view class="bt-box flex_col border_r_20">
					<text class="clr_h">次卡剩余次数：{{codeInfo.data.useCount - codeInfo.data.usedCount}}次</text>
					<text class="clr_h">有效期至：{{codeInfo.data.validityDate}}</text>
				</view>
			</view>
		</view>
	</view>
</template>


<script>
	import {
		getOpenDoorQR
	} from '@/api/my.js'
	export default {
		data() {
			return {
				isVip: true, //是否vip
				type: 0, //0是月卡 10是次卡
				isEffective: true, //是否效期		
				latitude: this.$store.state.latilongi.latitude,
				longitude: this.$store.state.latilongi.longitude,
				qrcodeUrl: null, //二维码
				intervalId: null, // 定时器ID
				codeInfo: {}, //开门二维码/当前有效会员卡
			}
		},
		onLoad() {
			// 页面加载时立即刷新二维码，并启动定时器
			this.getUserVipInfo();
			// this.startInterval();
		},
		onUnload() {
			// 页面卸载时清除定时器
			this.clearInterval();
		},
		methods: {
			// 展示事件
			handleShowQrcode() {
				// if ((this.codeInfo.data.useCount - this.codeInfo.data.usedCount) === 0) {
				// 	this.config.Toast('没有可用次数')
				// 	return;
				// }
				this.isEffective = false;
				this.startInterval();
				// console.log('展示事件');
			},
			// 开始定时请求接口 每3秒请求一次
			startInterval() {
				this.clearInterval();
				this.intervalId = setInterval(() => {
					this.getUserVipInfo();
				}, 4000);
			},
			// 清除定时器
			clearInterval() {
				if (this.intervalId) {
					clearInterval(this.intervalId);
					this.intervalId = null;
				}
			},
			// 获取会员信息
			getUserVipInfo() {
				// console.log(this.$store.state.latilongi.latitude,this.$store.state.latilongi.longitude,'金纬度')
				getOpenDoorQR({
					// userLat: this.latitude,
					// userLng: this.longitude
					userLat: this.$store.state.latilongi.latitude,
					userLng: this.$store.state.latilongi.longitude
				}).then((r) => {
					if (r.code == 1) {
						this.isVip = true;
						// 判断是否有亮码时间和次卡有效结束时间,还需要判断type=10为次卡
						if (r.data.type == 10) {
							// this.isEffective = true;
						} else {
							this.startInterval();
							this.isEffective = false;
						}
						this.qrcodeUrl = r.qrCode;
						r.data.validityDate = this.config.timestampToDateTime(r.data.validityDate, 'date')
						r.data.onLineTime = this.config.timestampToDateTime(r.data.onLineTime, 'dateTime')
						this.codeInfo = r;
					} else {
						this.isVip = false;
						this.isEffective = false;
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.open-code {
		overflow: hidden;

		.no-vip {
			position: absolute;
			left: 50%;
			top: 50%;
			transform: translate(-50%, -50%);

			.btn {
				width: 269rpx;
				height: 91rpx;
				background: #373838;
				border-radius: 45rpx;
				color: #ffffff;
				font-size: 30.32rpx;
			}
		}

		.y-vip {
			position: relative;

			.code-img {
				margin-top: 290rpx;
				margin-bottom: 60rpx;

				.img {
					width: 495rpx;
					height: 495rpx;
				}
			}
		}

		.c-vip {
			width: 90%;
			margin: 100rpx auto;
			box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);

			.txt-top {
				width: 100%;
				background: rgb(244, 244, 244);
				height: 173rpx;

				text {
					font-size: 30.32rpx;
					color: #999999;
					line-height: 40rpx;
				}
			}

			.ewm-box {
				position: relative;

				.show-qrcode {
					width: 269rpx;
					height: 91rpx;
					line-height: 91rpx;
					text-align: center;
					background: #373838;
					border-radius: 45rpx;
					color: #fff;
					position: absolute;
					left: 50%;
					top: 50%;
					transform: translate(-50%, -50%);
					z-index: 1;
				}
			}

			.code-img {
				margin: 60rpx;

				.img {
					width: 495rpx;
					height: 495rpx;
				}
			}

			.time {
				margin-bottom: 60rpx;

				.sx {
					text {
						color: #E15B00;
					}
				}
			}

			.bt-box {
				width: 100%;
				height: 112rpx;
				background: #000;
				padding-left: 46rpx;
				box-sizing: border-box;

				text {
					font-size: 24.26rpx;
				}
			}
		}
	}
</style>