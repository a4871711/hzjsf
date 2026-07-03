<template>
	<view class="my">
		<view class="my-top">
			<!-- 会员 -->
			<view class="login-box2" v-if="isVip && isLogin">
				<view class="hdimg flex">
					<image :src="userInfo.headImgUrl || '/static/image/my_img.png'" alt="" class="img" />
					<view class="flex_col">
						<text class="font_size_32">{{userInfo.nickname}}</text>
						<text class="font_size_22 clr_h user-id" @click="copyUserId">会员ID：{{userInfo.userId}}</text>
					</view>
				</view>

				<view class="flex-box flex_s">
					<view class="flex-l">
						<view class="flex">
							<image src="/static/image/icon-zs.png" alt="" class="icon-zs" />
							<!-- 卡片类型（0：月卡1:季卡2：半年卡3：年卡 10次卡） "wtState": 0 //1有自动续费 -->
							<view class="font_size_24" v-if="vipInfo.data.type == 10">次卡会员生效中</view>
							<view class="font_size_24"
								v-if="(vipInfo.wtState == 0 || vipInfo.wtState == 2) && vipInfo.data.type != 10">会员生效中
							</view>
							<view class="font_size_24" v-if="vipInfo.wtState == 1">会员连续包月中</view>

						</view>
						<view class="font_size clr_h">会员有效期至{{vipInfo.data.validityDate}}</view>
					</view>
					<view class="flex-r">
						<view class="font_size_22" v-show="vipInfo.data.type == 10">
							剩余{{vipInfo.data.useCount - vipInfo.data.usedCount}}次 </view>
						<view class="btn flex_a"
							@click="config.path('/pagesA/card_renewal/card_renewal?id='+vipInfo.data.storeId)">立即续费
						</view>
					</view>
				</view>
			</view>

			<!-- 非会员 -->
			<view class="login-box flex_s" v-else>
				<view class="hdimg flex_1">
					<image :src="userInfo.headImgUrl" v-if="isLogin && userInfo.headImgUrl" alt="" class="img" />
					<image src="/static/image/my_img.png" alt="" class="img" v-else />
					<view class="flex_col" v-if="isLogin">
						<text class="font_size_32">{{userInfo.nickname}}</text>
						<text class="font_size_22 clr_h user-id" @click="copyUserId">会员ID：{{userInfo.userId}}</text>
					</view>
					<text class="font_size_32" v-else>未登录</text>
				</view>

				<view class="btn-01 flex_1" v-if="isLogin"
					@click="config.path('/pagesA/card_renewal/card_renewal?openVip=1')">去开通
				</view>
				<view class="btn" v-else>
					<text class="font_size_24 clr_h" @click="config.path('/pagesA/login/login')">立即登录</text>
					<image src="/static/image/my_icon-sr.png" alt="" />
				</view>
			</view>
		</view>

		<view class="item-box">
			<view class="item flex_s" @click="handleEvents(item.url)" v-for="(item,index) in serviceList" :key="index">
				<view class="flex">
					<image :src="item.icon_img" alt="" class="img" />
					<text>{{item.title}}</text>
				</view>
				<image src="/static/image/my_icon-dr.png" alt="" class="icon-arrow" />
			</view>
		</view>

		<!-- 客服弹窗 -->
		<tip :show="show" content="客服电话13622620350" title="联系客服" @handlconfirm="handlconfirm"
			@cancellation="cancellation" confirm="拨打电话" cancel="取消" :showCancelButton="true"></tip>
	</view>
</template>

<script>
	import {
		getUserInfo
	} from '@/api/login.js'
	import {
		getOpenDoorQR
	} from '@/api/my.js'
	import tip from '@/components/tip.vue';
	export default {
		components: {
			tip
		},
		data() {
			return {
				isLogin: false,
				isVip: false,
				show: false,
				phone: '',
				userInfo: null,
				serviceList: [{
						id: 1,
						url: '/pagesA/card_record/card_record',
						title: '购卡记录',
						icon_img: '/static/image/my_icon01.png'
					},
					{
						id: 6,
						url: '/pagesA/my_benefits/my_benefits',
						title: '我的权益',
						icon_img: '/static/image/my_icon01.png'
					},
					{
						id: 7,
						url: '/pagesA/card_pause/card_pause',
						title: '停卡管理',
						icon_img: '/static/image/my_icon03.png'
					},
					{
						id: 2,
						url: '/pagesA/coupon/coupon',
						title: '优惠券',
						icon_img: '/static/image/my_icon02.png'
					},
					{
						id: 3,
						url: '/pagesA/renew/renew',
						title: '自动续费管理',
						icon_img: '/static/image/my_icon03.png'
					},
					{
						id: 4,
						url: 'service',
						title: '联系客服',
						icon_img: '/static/image/my_icon04.png'
					},
					{
						id: 5,
						url: '/pagesA/setup_center/setup_center',
						title: '设置中心',
						icon_img: '/static/image/my_icon05.png'
					}
				],
				latitude: this.$store.state.latilongi.latitude,
				longitude: this.$store.state.latilongi.longitude,
				vipInfo: {}, //vip信息
			}
		},
		onReady() {
			if (!this.isVip) {
				//动态修改状态栏的颜色
				uni.setNavigationBarColor({
					frontColor: '#000000',
					backgroundColor: '#F4F4F4'
				})
			}

		},
		async onShow() {
			if (this.$store.state.token) {
				this.getUserVipInfo(); //判断是不是会员
				this.isLogin = true;
				await this.common.getUserInfo(this);
				this.userInfo = this.$store.state.userinfo;
				this.userInfo.nickname = this.$store.state.userinfo.nickname || "SL" + this.$store.state.userinfo.phone
					.toString().slice(-4)
			} else {
				this.isLogin = false;
				this.userInfo = null;
			}

		},
		methods: {
			getPhoneNumber(e) {
				console.log(e.detail.code) // 动态令牌
				console.log(e.detail.errMsg) // 回调信息（成功失败都会返回）
				console.log(e.detail.errno) // 错误码（失败时返回）
			},
			cancellation() {
				this.show = false
			},
			// 复制会员ID(转让权益卡等场景需要告知对方自己的会员ID)
			copyUserId() {
				if (!this.userInfo || !this.userInfo.userId) return;
				uni.setClipboardData({
					data: String(this.userInfo.userId),
					success: () => {
						this.config.Toast('会员ID已复制');
					}
				});
			},
			handlconfirm() {
				this.show = false
				uni.makePhoneCall({
					phoneNumber: '13622620350'
				});
			},
			// 登录后页面跳转
			handleEvents(url) {
				if (!this.isLogin) {
					this.config.Toast('请先登录');
					setTimeout(() => {
						this.config.path('/pagesA/login/login')
					}, 1000)
				} else {
					if (url == 'service') {
						this.show = true
					} else {
						this.config.path(url)
					}
				}
			},
			// 获取会员信息
			getUserVipInfo() {
				getOpenDoorQR({
					userLat: this.latitude,
					userLng: this.longitude
				}).then((r) => {
					if (r.code == 1) {
						this.isVip = true;
						r.data.validityDate = this.config.timestampToDateTime(r.data.validityDate, 'date')
						this.vipInfo = r;
						// this.nextValidityDate:r.nextValidityDate;   //会员到期时间						
						// this.wtState = r.wtState;   
					} else {
						this.isVip = false;
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.my {
		height: 100vh;
		background: #F4F4F4;

		.my-top {
			.login-box {
				padding-top: 40rpx;
				width: 88%;
				margin: 0 auto;
			}

			.login-box2 {
				padding: 40rpx 40rpx 0 40rpx;
				background-image: linear-gradient(to bottom, #F3E6D1, rgba(243, 238, 230, 0.1));

				.flex-box {
					height: 113rpx;
					background: linear-gradient(180deg, #4E4438, #2F2820);
					border-radius: 20rpx;
					padding: 19rpx 35rpx;
					box-sizing: border-box;
					margin-top: 26rpx;

					.flex-l {
						color: #fff;

						.icon-zs {
							width: 53rpx;
							height: 50rpx;
						}
					}

					.flex-r {
						color: #fff;
						text-align: center;

						.btn {
							width: 164rpx;
							height: 60rpx;
							background: linear-gradient(90deg, #EAB96D, #FEEBCD);
							border-radius: 30rpx;
							border: 1px solid #7C4E0C;
							font-weight: bold;
							font-size: 27rpx;
							color: #2F2820;
						}
					}

				}
			}

			.hdimg {
				.img {
					width: 107rpx;
					height: 113rpx;
					margin-right: 15rpx;
					border-radius: 50%;
				}

				text {
					color: #333;
					font-weight: bold;
				}

				.user-id {
					color: #999999;
					font-weight: normal;
					margin-top: 8rpx;
				}
			}

			.btn {
				image {
					width: 9rpx;
					height: 17rpx;
					margin-left: 10rpx;
				}
			}

			.btn-01 {
				width: 151rpx;
				height: 56rpx;
				background: #FF6E20;
				border-radius: 28rpx;
				font-weight: 500;
				font-size: 28rpx;
				color: #FFFFFF;
			}
		}

		.item-box {
			width: 88%;
			height: 555rpx;
			margin: 50rpx auto;
			background: #FFFFFF;
			border-radius: 20rpx;
			box-sizing: border-box;
			line-height: 110rpx;

			.item {
				padding: 0 25rpx;

				.img {
					width: 42rpx;
					height: 40rpx;
					margin-right: 27rpx;
				}

				.icon-arrow {
					width: 12rpx;
					height: 23rpx;
				}
			}
		}

	}
</style>