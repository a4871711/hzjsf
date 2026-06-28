<template>
	<view class="shop">
		<view class="flex_s top-box">
			<view class="selection flex_1">
				<view class="txt-box flex_1" @click="handleSelect">
					<view class="txt">{{address.zone || '请选择'}}</view>
					<u-icon name="arrow-down-fill" color="#000000" size="8"></u-icon>
				</view>
			</view>
			<view class="search">
				<u--input placeholder="搜索门店名称" prefixIcon="search" prefixIconStyle="font-size: 22px;color: #999999"
					shape="circle" v-model="keyword" @confirm="handleSearch"></u--input>
			</view>
		</view>

		<view class="shop-item">
			<view v-for="(item,index) in storeList" :key="index">
				<view class="item flex" @click="toShopDetail(item.storeId)">
					<view class="item-l">
						<image :src="item.storeImgUrl" class="img"></image>
					</view>
					<view class="item-r">
						<view class="item-r-1 flex_s">
							<view class="r-left flex">
								<image class="icon-shop" src="/static/image/index_01.png"></image>
								<text>{{item.storeName}}</text>
							</view>
							<view class="distance">{{item.distance}}km</view>
						</view>
						<view class="icon-text flex">
							<u-icon name="map" size="12"></u-icon>
							<text>{{item.storeAddrDetail}}</text>
						</view>
						<view class="icon-text flex">
							<u-icon name="clock" size="12"></u-icon>
							<text>{{item.hours}}</text>
						</view>
						<view class="icon-text flex">
							<u-icon name="phone" size="12"></u-icon>
							<text>{{item.phone}}</text>
						</view>
					</view>
				</view>
			</view>
		</view>

		<u-modal :show="showModal" title="提示" content='请先开启授权位置信息' @confirm="authorizedLocation()"></u-modal>

		<fit-shop :show="show" @hanleClose="hanleClose" :address.sync="address"></fit-shop>
	</view>
</template>

<script>
	import {
		getStoreList
	} from '@/api/shop.js'
	import FitShop from '@/components/fit-shop.vue';
	export default {
		components: {
			FitShop
		},
		data() {
			return {
				show: false,
				storeList: [], //门店列表
				latitude: '',
				longitude: '',
				showModal: false,
				address: {
					province: '',
					city: '',
					zone: ''
				},
				keyword: '', // 搜索值
			}
		},
		watch: {
			address() {
				this.getStoreList();
			}
		},
		onLoad() {
			//this.getStoreList();
		},
		onShow() {
			this.common.getMyLocation(this).then((res) => {
				this.$store.commit('latilongi', {
					latitude: res.latitude,
					longitude: res.longitude
				})
				this.latitude = res.latitude,
					this.longitude = res.longitude
				this.showModal = false;
				this.getStoreList();
			}).catch((e) => {
				this.latitude = null;
				this.longitude = null
				this.showModal = true;
			});
		},
		onHide() {
			this.show = false;
		},
		methods: {
			// 搜索
			handleSearch(val) {
				this.keyword = val;
				this.getStoreList();
			},
			authorizedLocation() {
				this.showModal = false
				this.common.againGetMyLocation(this)
			},
			toShopDetail(storeId) {
				this.config.path('/pagesA/shop_detail/shop_detail?id=' + storeId)
			},
			// 打开可用门店弹窗
			handleSelect() {
				this.show = true
			},
			// 关闭可用门店弹窗
			hanleClose() {
				this.show = false
			},
			// 门店列表
			getStoreList() {
				let data = {
					page: 1,
					limit: 10,
					userLng: this.longitude,
					userLat: this.latitude,
					keywords: this.keyword,
					...this.address
				}
				getStoreList(data).then((res) => {
					this.storeList = res.page.list;
				});
			},

		}
	}
</script>

<style scoped lang="scss">
	page {
		background: #F4F4F4;
	}

	.shop {
		.top-box {
			padding: 10rpx 40rpx;
			box-sizing: border-box;
			margin-bottom: 20rpx;

			.txt-box {
				.txt {
					margin-right: 12rpx;
					color: #333333;
					font-size: 28rpx;
				}
			}

			.search {
				width: 45%;
				// height: 73rpx;

				.u-input {
					background-color: #fff;
					border: none;
				}
			}
		}


		.shop-item {
			.item {

				background: #fff;
				padding: 10rpx 40rpx;
				box-sizing: border-box;
				margin-bottom: 18rpx;

				.item-l {
					.img {
						width: 182rpx;
						height: 182rpx;
						border-radius: 10rpx;
						margin-right: 15rpx;

					}
				}

				.item-r {
					flex: 1;
					height: 182rpx;
					display: flex;
					flex-direction: column;
					justify-content: space-around;

					.item-r-1 {
						.r-left {
							.icon-shop {
								width: 33rpx;
								height: 33rpx;
								margin-right: 12rpx;
							}

							text {
								font-weight: bold;
								font-size: 30rpx;
								color: #333333;
								line-height: 34rpx;
							}
						}

						.distance {
							font-weight: 500;
							font-size: 24rpx;
							color: #999999;
							line-height: 34rpx;
						}
					}

					.icon-text {
						line-height: 34rpx;

						text {
							font-weight: 400;
							font-size: 22rpx;
							color: #999999;
							margin-left: 5rpx;
						}
					}
				}
			}
		}
	}
</style>