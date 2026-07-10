<template>
	<view class="shop-detail">
		<view class="shop-img">
			<image :src="storeInfo.mainImg"></image>
		</view>

		<view class="shop-info">
			<view class="info-box flex_s">
				<view class="flex">
					<image class="img" :src="storeInfo.mainImg"></image>
					<view class="info-lr">
						<view class="name">{{storeInfo.storeName}}</view>
						<view @click="handleCall" class="tell">
							<image class="icon_tell" src="/static/image/icon_tell.png"></image>
							<text class="clr_h font_size_24">{{storeInfo.phone}}</text>
						</view>
					</view>
				</view>
				<view class="tips-box">
					<view class="tips text_center">{{storeInfo.status == 1?'营业中':'休息中'}}</view>
					<view class="clr_h font_size_24">{{storeInfo.hours}}</view>
				</view>


			</view>
			<view class="block-03 flex_s" @click="openMap">
				<view class="flex_1">
					<image src="/static/image/index_wz2.png" alt="" class="index_wz2" />
					<text>{{storeInfo.storeAddrDetail}}</text>
				</view>
				<view class="flex_1">
					<text>距您{{storeInfo.distanceKm}}km</text>
					<image src="/static/image/index_wz.png" alt="" class="index_wz" />
				</view>
			</view>

		</view>

		<view class="coach">
			<view class="title">门店教练</view>
			<view class="shop-scroll-view">
				<coach-scroll-view :coach-list="coachList"></coach-scroll-view>
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
			<vip-card :card-list="cardList" :storeId="storeId"></vip-card>
		</view>
	</view>
</template>

<script>
	import {
		getfitCardList,
		getCoachList,
		getStoreInfo
	} from '@/api/index'
	import CoachScrollView from '@/components/coach-scroll-view.vue';
	import VipCard from '@/components/vip-card.vue';
	export default {
		components: {
			CoachScrollView,
			VipCard,
		},
		data() {
			return {
				coachList: [], //教练列表
				cardList: [], //vip卡片列表
				storeId: null, //门店id
				storeInfo: {}, //门店详情
				total: this.$store.state.total, //门店总数
				longitude: this.$store.state.latilongi.longitude,
				latitude: this.$store.state.latilongi.latitude,
			}
		},
		onLoad: function(option) {
			this.storeId = option.id;
			console.log(this.storeId, 'this.storeId')
			this.getStoreInfo(option.id)
			this.getCoachList(option.id)
		},
		methods: {
			handleCall() {
				uni.makePhoneCall({
					phoneNumber: this.storeInfo.phone,
					success: () => {
						console.log('拨打电话成功！');
					},
					fail: () => {
						console.error('已取消');
					}
				})
			},
			// 门店详情
			getStoreInfo(storeId) {
				let data = {
					storeId: storeId,
					userLng: this.longitude,
					userLat: this.latitude,
				}
				getStoreInfo(data).then((res) => {
					res.info.distanceKm = (res.info.distance / 1000).toFixed(2);
					res.info.mainImg = res.info.storeImgUrl.split(',')[0];
					this.storeInfo = res.info;
					this.getfitCardList(res.info.storeAddrId)
					uni.setNavigationBarTitle({
						title: res.info.storeName
					});
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
			// vip会员套餐(门店详情页只展示非权益卡:cardNature != 1,与首页口径一致)
			getfitCardList(storeAddrId) {
				let data = {
					storeId: storeAddrId
				}
				getfitCardList(data).then((res) => {
					let list = (res.data || []).filter((item) => Number(item.cardNature) !== 1);
					list = list.slice(0, 3);
					this.cardList = list.map((item) => {
						return {
							...item,
							cardPrice: item.cardPrice
						}
					});
				});
			},
			// 跳门店
			switchTab() {
				uni.switchTab({
					url: '/pages/shop/shop'
				});
			},
			// 打开地图导航
			openMap() {
				uni.openLocation({
					latitude: Number(this.storeInfo.latitude),
					longitude: Number(this.storeInfo.longitude),
					name: this.storeInfo.storeAddrDetail,
					success() {
						console.log('导航已启动');
					},
					fail(error) {
						console.error('调用地图失败：', error);
					}
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.shop-detail {
		.shop-img {
			height: 500rpx;

			image {
				width: 100%;
				height: 100%;
			}
		}

		.shop-info {
			padding: 40rpx;
			box-sizing: border-box;

			.block-03 {
				height: 53rpx;
				background: #FAFAFA;
				padding: 0 9rpx 0 13rpx;
				border-radius: 10rpx;
				font-size: 24.26rpx;
				color: #999999;

				.index_wz2 {
					width: 20.22rpx;
					height: 24.26rpx;
					margin-right: 9rpx;
				}

				.index_wz {
					width: 58.63rpx;
					height: 59.64rpx;
					margin-top: 15rpx;
				}
			}

			.info-box {
				margin-bottom: 27rpx;

				.img {
					width: 71rpx;
					height: 71rpx;
					margin-right: 17rpx;
				}

				.icon_tell {
					width: 25rpx;
					height: 25rpx;
					margin-right: 7rpx;
				}

				.info-lr {
					display: flex;
					flex-direction: column;
					justify-content: space-around;

					.name {
						font-weight: bold;
						font-size: 34rpx;
						color: #272727;
					}

					.tell {
						display: flex;
						align-items: center;
					}
				}

				.tips-box {
					display: flex;
					flex-direction: column;
					align-items: flex-end;

					.tips {
						font-size: 26rpx;
						color: #45B12F;
						width: 100rpx;
						height: 41rpx;
						border-radius: 10rpx;
						border: 1px solid #45B12F;
						margin-bottom: 15rpx;
					}
				}
			}
		}

		.coach {
			padding: 0 40rpx;
			box-sizing: border-box;

			.title {
				font-weight: 800;
				font-size: 30rpx;
				color: #000000;
				margin-bottom: 25rpx;
			}
		}

		.vip-box {
			padding: 0 40rpx 40rpx 40rpx;
			box-sizing: border-box;
		}

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
	}
</style>