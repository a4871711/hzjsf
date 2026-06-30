<template>
	<view class="home">
		<!-- 自定义标题导航栏开始 -->
		<u-navbar placeholder title="矢历健身" :safeAreaInsetTop="true">
			<view class="u-nav-slot" slot="left">
				<view class="qrcode" style=" background: #e15b00;padding:6rpx;border-radius: 10rpx;text-align: center;"
					@click="goCode">
					<image src="/static/image/index_ewm.png" alt=""
						style="width: 60rpx;height: 60rpx;border-radius: 10rpx;" />
					<!-- <view style="font-size: 14rpx;color: #fff;">开门码</view> -->
				</view>
				<!-- <view style="writing-mode: vertical-rl; font-size: 28rpx; font-weight: 700;">开门码</view> -->
				<view style="margin-left: 10rpx; font-size: 30rpx; font-weight: 700;">开门码</view>
			</view>
		</u-navbar>
		<!-- 自定义标题导航栏结束 -->

		<!-- 轮播 -->
		<view>
			<swiper class="my-swiper" circular :indicator-dots="indicatorDots" :autoplay="autoplay" :interval="interval"
				:duration="duration">
				<swiper-item v-for="(item, index) in imageList" :key="index">
					<view class="swiper-item">
						<image class="banner" :src="item" @click="previewSwiperImg(index)"></image>
					</view>
				</swiper-item>
			</swiper>
		</view>

		<!-- 核销 -->
		<view class="section flex">
			<!-- 	<view class="section-one flex_1">
				<view class="one-wrap flex_col_center" @click="config.path('/pagesA/open_code/open_code')">
					<image src="/static/image/index_ewm.png" alt="" class="ewm" />
					<view class="txt">开门码</view>
				</view>
			</view> -->

			<view class="section-two flex_a flx">
				<view class="img-txt flex">
					<image src="/static/image/index_mt.png" alt="" class="mt" />
					<view class="txt" @click="toWriteOff(1)">美团核销</view>
				</view>
				<view class="line"></view>
				<view class="img-txt flex">
					<image src="/static/image/index_dy.png" alt="" class="dy" />
					<view class="txt" @click="toWriteOff(2)">抖音核销</view>
				</view>
			</view>
		</view>

		<!-- vip -->
		<view class="vip-box">
			<view class="index-title flex_s">
				<view>VIP会员</view>
				<view class="grey" @click="switchTab">
					<text>全国{{total}}家门店适用</text>
					<image src="/static/image/index_more.png" alt="" class="qh" />
				</view>
			</view>
			<!-- 会员卡组件 -->
			<vip-card :card-list="cardList" :storeId="myStore.storeId"></vip-card>
		</view>

		<!-- VIP权益卡(独立于会员卡的可购买权益卡商品) -->
		<view class="vip-box" v-if="benefitCardList.length">
			<view class="index-title flex_s">
				<view>VIP权益卡</view>
			</view>
			<vip-benefit-card :card-list="benefitCardList"></vip-benefit-card>
		</view>

		<!-- 门店教练 -->
		<view class="shop-jl">
			<view class="index-title flex_s">
				<view>门店教练</view>
			</view>
			<view class="shop-scroll-view">
				<coach-scroll-view :coach-list="coachList"></coach-scroll-view>
			</view>
		</view>



		<!-- 我的门店 -->
		<view class="mystore">
			<view class="index-title flex_s">
				<view>我的门店</view>
				<view class="grey" @click="config.path('/pagesA/select_store/select_store?type=1')">
					<text>切换门店</text>
					<image src="/static/image/index_qh.png" alt="" class="qh" />
				</view>
			</view>
			<view class="store-wrap">
				<view class="scroll-box">
					<scroll-view class="scroll-view" scroll-x="true">
						<view class="scroll-view-item" v-for="(item, index) in myStore.storeImgUrl" :key="index">
							<image :src="item" class="image" @click="previewImage(index)"></image>
						</view>
					</scroll-view>

					<view class="store-block">
						<view class="block-01 flex" @click="toShopDetail(myStore.storeId)">
							<image src="/static/image/index_01.png" alt="" class="icon01" />
							<text>{{myStore.storeName}}</text>
						</view>
						<view class="flex">
							<view class="block-02" @click="toShopDetail(myStore.storeId)">
								{{myStore.storeStatus == 1 ? '营业中':'暂停营业'}}{{myStore.hours || '24小时营业'}}
							</view>
							<view class="font_size_26 block-02 marg_left_20">实时人数：{{myStore.onlineUser || 0}}</view>
						</view>
						<view class="block-03 flex_s" @click="openMap">
							<view class="flex_1">
								<u-icon name="map" size="12"></u-icon>
								<text>{{myStore.storeAddrDetail}}</text>
							</view>
							<view class="flex_1">
								<text>距您{{myStore.distance}}km</text>
								<image src="/static/image/index_wz.png" alt="" class="index_wz" />
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>




		<u-modal :show="showModal" title="提示" content='请先开启授权位置信息' @confirm="authorizedLocation()"></u-modal>
	</view>
</template>

<script>
	import CoachScrollView from '@/components/coach-scroll-view.vue';
	import VipCard from '@/components/vip-card.vue';
	import VipBenefitCard from '@/components/vip-benefit-card.vue';

	import {
		getSwiper,
		getMyStore,
		getfitCardList,
		getCoachList,
		getStoreCount,
		getVipCardList,
	} from '@/api/index'
	export default {
		components: {
			CoachScrollView,
			VipCard,
			VipBenefitCard,
		},
		data() {
			return {
				navBarHeight: 0,
				indicatorDots: true,
				autoplay: true,
				interval: 3000,
				duration: 500,
				imageList: [], //轮播
				coachList: [], // 门店教练
				cardList: [], // vip卡片列表
				benefitCardList: [], // VIP权益卡列表
				myStore: {}, //我的门店
				latitude: '',
				longitude: '',
				total: 1, //门店总数
				showModal: false,
			}
		},
		onLoad() {
			//this.init();		

			// let capsule = wx.getMenuButtonBoundingClientRect(); // 获取胶囊信息
			// let capsuleHeight =  capsule.height;   //胶囊按钮的高度
			// let top =  capsule.top;   // 胶囊按钮距离顶部的距离
			// let statusBarHeight = 0;  // 状态栏高度
			// wx.getSystemInfo({   //获取状态栏信息
			// 	success:res=>{
			// 		statusBarHeight = res.statusBarHeight
			// 	}
			// }) 
			// // 导航栏高度=状态栏高度+胶囊按钮的高度+（胶囊按钮距离顶部的距离-状态栏的高度）*2。
			// this.navHeight = statusBarHeight + capsuleHeight + (top - statusBarHeight);

			//  console.log('navHeight==',navHeight)

		},
		onHide() {
			this.show = false;
		},
		onShow() {
			this.init();
		},
		methods: {
			authorizedLocation() {
				this.showModal = false
				this.common.againGetMyLocation(this)
			},
			// 进入二维码判断定位权限
			goCode() {
				this.common.getMyLocation(this).then((res) => {
					this.$store.commit('latilongi', {
						latitude: res.latitude,
						longitude: res.longitude
					})
					this.showModal = false;
					this.config.path('/pagesA/open_code/open_code')
				}).catch((e) => {
					this.showModal = true;
				});
			},
			// 获取当前位置
			async init() {
				this.getSwiper();
				this.getVipBenefitCardList();
				await this.common.getMyLocation(this).then((res) => {
					this.$store.commit('latilongi', {
						latitude: res.latitude,
						longitude: res.longitude
					})
					// console.log(this.$store.state.latilongi.latitude,this.$store.state.latilongi.longitude,'金纬度')
					if (res.errMsg === "getLocation:ok") {
						this.latitude = this.$store.state.latilongi.latitude;
						this.longitude = this.$store.state.latilongi.longitude;
						// if(this.$store.state.token){
						// 	this.getMyStore();
						// }
					}
				}).catch((e) => {

				});

				this.getMyStore();
			},
			// 跳转核销
			toWriteOff(type) {
				this.config.path('/pagesA/write_off/write_off?type=' + type);
			},
			// VIP权益卡列表(公开,取上架商品;后端按购买人数算动态价 currentPrice)
			getVipBenefitCardList() {
				getVipCardList({
					page: 1,
					limit: 6
				}).then((res) => {
					let page = res.data || {};
					this.benefitCardList = page.list || [];
				}).catch(() => {
					this.benefitCardList = [];
				});
			},
			// 首页轮播
			getSwiper() {
				getSwiper({
					advType: 1
				}).then((res) => {
					this.imageList = res.data.map((item) => {
						return item.advMainImg
					})
				});
			},
			// 首页门店
			getMyStore() {
				let data = {
					offset: 0,
					limit: 1,
					userLng: this.longitude,
					userLat: this.latitude
				}
				getMyStore(data).then((res) => {
					let store = res.data[0];
					store.storeImgUrl = store.storeImgUrl.split(',').map(url => url.trim());
					store.distance = parseFloat((store.distance / 1000).toFixed(2));
					this.myStore = store
					this.getCoachList(store.storeId);
					this.getfitCardList(store.storeAddrId);
					this.getStoreCount(this.latitude, this.longitude)
				});
			},
			// 教练列表
			getCoachList(storeId) {
				let data = {
					page: 1,
					limit: 10,
					storeId: storeId
				}
				getCoachList(data).then((res) => {
					this.coachList = res.page.list;
				});
			},
			// 门店数量
			getStoreCount(latitude, longitude) {
				let data = {
					page: 1,
					limit: 10,
					userLat: latitude,
					userLng: longitude,
				}
				getStoreCount(data).then((res) => {
					this.total = res.total
					this.$store.state.total = res.total;
				});
			},
			// vip会员套餐
			getfitCardList(storeAddrId) {
				let data = {
					storeId: storeAddrId
					// cardType:3
				}
				getfitCardList(data).then((res) => {
					res.data = res.data.slice(0, 3);
					this.cardList = res.data.map((item) => {
						return {
							...item,
							cardPrice: item.cardPrice
						}
					});
				});
			},
			// 图片预览
			previewImage(index) {
				let storeImgUrl = this.myStore.storeImgUrl
				uni.previewImage({
					current: index,
					urls: storeImgUrl
				});
			},
			// 轮播图预览
			previewSwiperImg(index) {
				uni.previewImage({
					current: index,
					urls: this.imageList
				});
			},
			toShopDetail(storeId) {
				this.config.path('/pagesA/shop_detail/shop_detail?id=' + storeId)
			},
			// 打开地图导航
			openMap() {
				const that = this;
				uni.openLocation({
					latitude: +that.myStore.latitude,
					longitude: +that.myStore.longitude,
					name: that.myStore.storeName,
					success() {
						console.log('导航已启动');
					},
					fail(error) {
						console.error('调用地图失败：', error);
					}
				});
			},
			// 跳门店
			switchTab() {
				uni.switchTab({
					url: '/pages/shop/shop'
				});
			}
		}
	}
</script>

<style scoped lang="scss">
	.home {
		width: 750rpx;
		margin: 0 auto;
		padding: 40rpx;
		box-sizing: border-box;

		.u-nav-slot {
			display: flex;
			align-items: center;
		}

		.qrcode {
			width: 70rpx;
			height: 70rpx;
			display: flex;
			justify-content: center;
			align-items: center;
		}
	}

	.my-swiper {
		width: 100%;
		height: 308rpx;
	}

	.banner {
		width: 100%;
		height: 308rpx;
		border-radius: 20rpx;
	}

	/* 指示点颜色与位置 */
	:deep(.uni-swiper-dot) {
		background: #E15B00 !important;
		opacity: 0.3;
	}

	:deep(.uni-swiper-dot-active) {
		background: #E15B00 !important;
		opacity: 1;
	}

	:deep(.wx-swiper-dot) {
		background: #E15B00 !important;
		opacity: 0.3;
	}

	:deep(.wx-swiper-dot-active) {
		background: #E15B00 !important;
		opacity: 1;
	}

	:deep(.uni-swiper-dots-horizontal) {
		top: 284rpx !important;
		left: 25rpx !important;
		transform: translate(0%) !important;
		text-align: left;
		height: 0rpx;
	}

	:deep(.wx-swiper-dots-horizontal) {
		top: 284rpx !important;
		left: 25rpx !important;
		transform: translate(0%) !important;
		text-align: left;
		height: 0rpx;
	}

	/* 核销 */
	.section {
		height: 113rpx;
		background: #F5F5F5;
		border-radius: 20rpx;
		margin-top: 43.46rpx;
		padding: 16rpx;
		box-sizing: border-box;
		margin-bottom: 38.41rpx;

		.section-one {
			width: 88rpx;
			height: 88rpx;
			background: #EBCAB4;
			border-radius: 13rpx;

			.one-wrap {
				width: 79rpx;
				height: 79rpx;
				background: #E15B00;
				border-radius: 13rpx;

				.ewm {
					width: 35.38rpx;
					height: 35.38rpx;
					margin-bottom: 8rpx;
					margin-top: 10rpx;
				}

				.txt {
					font-size: 18.85rpx;
					color: #fff;
				}
			}
		}

		.section-two {
			.line {
				width: 1rpx;
				height: 29rpx;
				background: #999999;
			}

			.img-txt {
				.mt {
					width: 57.61rpx;
					height: 35.38rpx;
					margin-right: 11rpx;
				}

				.dy {
					width: 31.33rpx;
					height: 36.39rpx;
					margin-right: 18rpx;
				}

				.txt {
					font-size: 24rpx;
					font-weight: 800;
				}
			}
		}

	}

	// 公共标题
	.index-title {
		font-size: 30.32rpx;
		color: #000;
		font-weight: 800;
		margin-bottom: 38.41rpx;

		.qh {
			width: 21.23rpx;
			height: 18.19rpx;
			margin-left: 19.2rpx;
		}

		.grey {
			color: #999999;
			font-size: 24.26rpx;
			font-weight: 500;
		}

		.icon-arrow {
			width: 15rpx;
			height: 15rpx;
			margin: 20rpx 0 20rpx 30rpx;
			border-top: 2rpx solid #000000;
			border-right: 2rpx solid #000000;
			transform: rotate(45deg);
		}
	}

	// 我的门店
	.mystore {
		// margin-top: 50rpx;

		.store-wrap {
			background: #FFFFFF;
			box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);
			border-radius: 20rpx;

			.scroll-box {
				.scroll-view {
					white-space: nowrap;
					width: 100%;

					.scroll-view-item {
						display: inline-block;
						width: 471rpx;
						height: 260rpx;
						margin-right: 10px;

						.image {
							width: 100%;
							height: 100%;
							border-radius: 20rpx 0rpx 0rpx 0rpx;
						}
					}
				}
			}

			.store-block {
				padding: 0 21rpx 0 25rpx;
				box-sizing: border-box;
				height: 219.34rpx;

				.block-01 {
					font-size: 34rpx;
					color: #272727;
					font-weight: bold;
					padding-top: 30rpx;

					.icon01 {
						width: 38.41rpx;
						height: 38.41rpx;
						margin-right: 6rpx;
					}
				}

				.block-02 {
					margin: 23rpx 0 26rpx 0;
					font-family: PingFang SC;
					font-weight: 500;
					font-size: 26rpx;
					color: #666666;
					line-height: 17rpx;
					padding-left: 12.13rpx;
				}

				.block-03 {
					// height: 53rpx;
					background: #FAFAFA;
					padding: 0 9rpx 0 13rpx;
					border-radius: 10rpx;
					font-size: 24.26rpx;
					color: #999999;

					.index_wz {
						width: 58.63rpx;
						height: 59.64rpx;
						margin-top: 15rpx;
					}
				}
			}
		}
	}

	.vip-box {
		margin-top: 50rpx;
	}

	// 门店教练
	.shop-jl {
		margin-top: 47rpx;
	}
</style>