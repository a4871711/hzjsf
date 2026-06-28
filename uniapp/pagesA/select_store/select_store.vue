<template>
	<view class="select-box">
		<view class="flex">
			<view class="city flex_s" @click="handleOpen">{{address.city || '请选择市'}}<u-icon name="arrow-down"
					color="#666" size="16"></u-icon>
			</view>
			<view class="city flex_s">{{address.zone || '请选择区'}}<u-icon name="arrow-down" color="#666"
					size="16"></u-icon></view>
		</view>
		<view class="item-box">
			<view class="item flex_s marg_bottom_20" v-for="(item,index) in shopList" :key="index"
				@click="handleSetNowStore(item.storeAddrId,item.storeId)">
				<view>
					<view class="flex_2 marg_bottom_20">
						<image src="/static/image/index_01.png" alt="" class="icon-img" />
						<view>{{item.storeName}}</view>
					</view>
					<!-- <view class="clr_h font_size_24">{{item.province}}{{item.city}}{{item.zone}}</view> -->
					<view class="clr_h font_size_24">{{item.storeAddrDetail}}</view>
				</view>
				<view>{{item.distance}}km</view>
			</view>
		</view>

		<u-modal :show="showModal" title="提示" content='请先开启授权位置信息' @confirm="authorizedLocation()"></u-modal>

		<!-- 顶部弹出窗 -->
		<view>
			<area-list :show="show" @hanleClose="hanleClose" :address.sync="address"></area-list>
		</view>
	</view>
</template>

<script>
	import {
		getStoreList,
		getAreaList
	} from '@/api/shop.js'
	import {
		setNowStore
	} from '@/api/my.js'
	import AreaList from '@/components/area-list.vue';
	export default {
		components: {
			AreaList
		},
		data() {
			return {
				show: false, //弹窗
				shopList: [],
				latitude: this.$store.state.latilongi.latitude,
				longitude: this.$store.state.latilongi.longitude,
				address: {
					province: '',
					city: '',
					zone: ''
				},
				showModal: false
			};
		},
		onLoad(options) {
			this.type = options.type;
			this.getShopList();
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
				this.getShopList();
			}).catch((e) => {
				this.latitude = null;
				this.longitude = null
				this.showModal = true;
			});
		},
		watch: {
			address(val) {
				this.getShopList();
			}
		},
		methods: {
			authorizedLocation() {
				this.showModal = false
				this.common.againGetMyLocation(this)
			},
			handleOpen() {
				this.show = true
			},
			// 关闭可用门店弹窗
			hanleClose() {
				this.show = false
			},
			// 门店列表
			getShopList() {
				let that = this;
				let data = {
					page: 1,
					limit: 10,
					userLng: that.longitude,
					userLat: that.latitude,
					...this.address
				}
				getStoreList(data).then((res) => {
					that.shopList = res.page.list;
				});
			},
			// 切换门店保存
			handleSetNowStore(storeAddrId, storeId) {
				let data = {
					token: this.$store.state.token,
					storeId: storeAddrId
				}
				setNowStore(data).then((r) => {
					if (this.type == 1) {
						uni.navigateBack({
							delta: 1
						});
					} else {
						uni.navigateTo({
							url: '/pagesA/card_renewal/card_renewal?id=' + storeId
						})
					}
					// uni.switchTab({
					// 	url: '/pages/index/index?id='+storeId
					// })
				});
			}
		},
	}
</script>

<style scoped lang="scss">
	.icon-img {
		width: 40rpx;
		height: 40rpx;
		margin-right: 10rpx;
	}

	.select-box {
		padding: 10rpx 40rpx;
		box-sizing: border-box;
		background: #fcede3;
		height: 100vh;
	}

	.city {
		margin-right: 40rpx;
	}

	.item-box {
		margin-top: 39rpx;

		.item {
			background: #FFFFFF;
			border-radius: 20rpx;
			padding: 25rpx;
			box-sizing: border-box;
		}
	}

	.u-icon__icon {
		margin-left: 10rpx;
	}
</style>