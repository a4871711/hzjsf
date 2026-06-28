<template>
	<view class="renew">
		<view class="renew-top flex_col_center" v-if="isVip">
			<view class="font_size_32 clr_h" v-if="vipInfo.wtState == 1">连续包月会员签约中</view>
			<view class="font_size_32 clr_h" v-if="vipInfo.wtState == 0">连续包月会员已关闭自动续费</view>
			<view class="font_size_32 clr_h">会员有效期至{{vipInfo.data.validityDate}}</view>
			<!-- <view class="font_size_32 clr_h">自动续费时间{{vipInfo.nextValidityDate}}</view> -->
		</view>

		<view class="renew-top flex_col_center" v-if="isVip && vipInfo.wtState == 1">
			<view class="botton btn" @click="show = true">关闭自动续费</view>
		</view>

		<view class="renew-top flex_col_center" v-if="isVip && (vipInfo.wtState == 0 || vipInfo.wtState == 2)">
			<view class="botton btn" @click="hanleRenew">购卡续期</view>
		</view>
		<view class="renew-top flex_col_center" v-if="!isVip">
			<view class="font_size_32 clr_h">连续包月享优惠</view>
			<view class="botton btn" @click="config.path('/pagesA/card_renewal/card_renewal?openVip=1')">去开通</view>
		</view>
		<view class="item-box">
			<view class="title">自动续费服务承诺</view>
			<view class="container">
				<view class="item flex_col_center">
					<image src="/static/image/renew_01.png" alt="" class="img" />
					<view>享超值优惠</view>
				</view>
				<view class="item flex_col_center">
					<image src="/static/image/renew_02.png" alt="" class="img" />
					<view>会员尊享</view>
				</view>
				<view class="item flex_col_center">
					<image src="/static/image/renew_03.png" alt="" class="img" />
					<view>更加省心</view>
				</view>
				<view class="item flex_col_center">
					<image src="/static/image/renew_04.png" alt="" class="img" />
					<view>安全高效</view>
				</view>
			</view>
		</view>
		<view class="tips-box">
			<view class="title">其他退订方式：</view>
			<view class="small">1、点击产品内“自动续费管理”—“立即退订”按钮进行退订</view>
			<view class="small">2、微信固定入口解约：微信右下角“我”服务—钱包—支付设置—自动续费—点击对应项日—点击“关闭扣费服务"，即可进行退订解约</view>
			<view class="small">3、人工退订，拨打我司客服电话进行退订：13622620350</view>
		</view>

		<!-- 弹窗 -->
		<tip :show="show" content="关闭自动续费后<br>下月起您将无法享受优惠价格续费会员<br>并且会员到期后将自动失效<br>确定要关闭自动续费吗？" title="关闭自动续费"
			@handlconfirm="handlconfirm" @cancellation="cancellation" confirm="确定关闭" cancel="我再想想"
			:showCancelButton="true">
		</tip>
	</view>
</template>

<script>
	import tip from '@/components/tip.vue';
	import {
		getOpenDoorQR,
		deleteContractTest,
		createOrder
	} from '@/api/my.js'
	export default {
		components: {
			tip
		},
		data() {
			return {
				show: false, //弹窗显示
				isVip: false, //是不是会员
				vipInfo: {},
			};
		},
		onShow() {
			this.getUserVipInfo();
		},
		methods: {

			cancellation() {
				this.show = false
			},
			// 关闭自动续费
			handlconfirm() {
				deleteContractTest().then((r) => {
					if (r.code == 1) {
						this.config.Toast('关闭成功');
						this.getUserVipInfo();
						this.show = false;
					}
				})
				// setTimeout(() => {
				// 	uni.switchTab({
				// 		url: '/pages/my/my'
				// 	})
				// }, 1000)
			},

			// 获取会员信息
			getUserVipInfo() {
				getOpenDoorQR().then((r) => {
					if (r.code == 1) {
						this.isVip = true;
						r.data.validityDate = this.config.timestampToDateTime(r.data.validityDate, 'date')
						this.vipInfo = r
					} else {
						this.isVip = false;
					}
				});
			},
			// 立即退订
			handleRetreat() {
				uni.showToast({
					title: '已退订成功',
					icon: 'success',
					duration: 1000
				});
			},
			// 购卡续期
			hanleRenew() {
				// this.vipInfo.data.storeAddrId
				// pagesA/card_renewal/card_renewal?id=
				this.config.path('/pagesA/card_renewal/card_renewal?id=' + this.vipInfo.data.storeId)

				// let data = {
				// 	paySum: -1,
				// 	fitCardId: this.vipInfo.data.fitCardId,
				// 	couponId: '',
				// 	storeAddressId:this.vipInfo.data.storeAddrId
				// }
				// createOrder(data).then((res) => {
				// 	if (res.code == 1) {
				// 		this.config.Toast('订单创建成功，准备签约')
				// 		getWxpayData({
				// 			orderNo: res.data.orderNo
				// 		}).then((r) => {
				// 			r.params.timestamp = r.params.timestamp * 1
				// 			r.params.request_serial = r.params.request_serial * 1							
				// 			console.log('r.params====',r.params)
				// 			uni.navigateToMiniProgram({
				// 				appId: "wxbd687630cd02ce1d",
				// 				path: 'pages/index/index',
				// 				extraData: r.params,
				// 				success(res) {
				// 					console.log('res===',res);
				// 				},
				// 				fail(e) {
				// 					console.log('e===',e);
				// 				}
				// 			})

				// 		});
				// 	}
				// });
			}
		}
	}
</script>

<style lang="scss" scoped>
	.renew {
		height: 100vh;
		background: #F2F2F2;
	}

	.tips-box {
		width: 669rpx;
		margin: 25rpx auto;
		padding: 20rpx;
		box-sizing: border-box;
		border-radius: 10rpx;
		color: #666666;

		.title {
			color: #333333;
			font-weight: bold;
			margin-bottom: 15rpx;
		}

		.small {
			font-size: 26rpx;
			margin-bottom: 10rpx;
		}
	}

	.item-box {
		background: #fff;
		width: 669rpx;
		height: 439rpx;
		margin: 25rpx auto;
		padding: 33rpx 40rpx;
		box-sizing: border-box;
		border-radius: 10rpx;

		.title {
			margin-bottom: 28rpx;
			font-weight: 500;
			font-size: 32rpx;
			color: #999999;
		}

		.container {
			display: flex;
			flex-wrap: wrap;
			height: 85%;

			.item {
				display: flex;
				justify-content: center;
				align-items: center;
				box-sizing: border-box;
				width: 50%;
				height: 50%;
				border-right: 1rpx solid #ccc;
				border-top: 1rpx solid #ccc;
				font-weight: bold;
				font-size: 30rpx;
				color: #333333;

				.img {
					width: 78rpx;
					height: 78rpx;
				}

				// 父元素中的偶数个子元素
				&:nth-child(2n) {
					border-right: 0 none;
				}

				// 父元素的前两个子元素(即第1和第2个item)
				&:nth-child(-n + 2) {
					border-top: 0 none;
				}
			}
		}
	}



	.renew {
		padding-top: 25rpx;
		box-sizing: border-box;

		.renew-top {
			width: 90%;
			margin: auto;
			background: #FFFFFF;
			border-radius: 10rpx;
			padding: 28rpx 0;
			line-height: 48rpx;

			.btn {
				// width: 175rpx;
				height: 61rpx;
				border-radius: 31rpx;
				padding: 15rpx 43rpx;
				box-sizing: border-box;
			}
		}


	}
</style>