<template>
	<view class="member-card-page">
		<view class="hero">
			<view class="hero-kicker">矢历健身</view>
			<view class="hero-title">会员卡</view>
			<view class="hero-desc">会员卡 / 康复卡</view>
		</view>

		<view class="store-bar flex_s">
			<view>
				<view class="store-label">当前门店</view>
				<view class="store-name">{{myStore.storeName || '请选择门店'}}</view>
			</view>
			<view class="store-action" @click="switchStore">切换门店</view>
		</view>

		<view class="section">
			<view class="section-head flex_s">
				<view>
					<view class="section-title">会员卡</view>
					<view class="section-subtitle">月卡 / 季卡 / 半年卡 / 年卡</view>
				</view>
				<view class="section-count">{{memberCards.length}}项</view>
			</view>

			<view v-if="memberCards.length">
				<view class="card-item" v-for="(item,index) in memberCards" :key="item.fitCardId || index"
					@click="goBuy(item)">
					<view class="card-main">
						<view class="card-name">{{item.cardName || '--'}}</view>
						<view class="card-meta">{{item.validity || 0}}天</view>
					</view>
					<view class="card-side">
						<view class="price"><text>¥</text>{{formatPrice(item)}}</view>
						<view class="buy-btn">立即购买</view>
					</view>
				</view>
			</view>
			<view class="empty" v-else>{{loading ? '加载中...' : '暂无会员卡'}}</view>
		</view>

		<view class="section">
			<view class="section-head flex_s">
				<view>
					<view class="section-title">康复卡</view>
					<view class="section-subtitle">按次使用</view>
				</view>
				<view class="section-count">{{recoveryCards.length}}项</view>
			</view>

			<view v-if="recoveryCards.length">
				<view class="card-item recovery" v-for="(item,index) in recoveryCards" :key="item.fitCardId || index"
					@click="goBuy(item)">
					<view class="card-main">
						<view class="card-name">{{item.cardName || '--'}}</view>
						<view class="card-meta">{{item.useCount || 0}}次</view>
					</view>
					<view class="card-side">
						<view class="price"><text>¥</text>{{formatPrice(item)}}</view>
						<view class="buy-btn">立即购买</view>
					</view>
				</view>
			</view>
			<view class="empty" v-else>{{loading ? '加载中...' : '暂无康复卡'}}</view>
		</view>
	</view>
</template>

<script>
	import {
		getMyStore,
		getfitCardList
	} from '@/api/index'

	export default {
		data() {
			return {
				loading: false,
				myStore: {},
				cardList: [],
				latitude: this.$store.state.latilongi.latitude,
				longitude: this.$store.state.latilongi.longitude,
			}
		},
		computed: {
			memberCards() {
				return this.cardList.filter(item => Number(item.cardType) !== 10)
			},
			recoveryCards() {
				return this.cardList.filter(item => Number(item.cardType) === 10)
			}
		},
		onShow() {
			this.init()
		},
		methods: {
			async init() {
				this.loading = true
				await this.common.getMyLocation(this).then((res) => {
					this.$store.commit('latilongi', {
						latitude: res.latitude,
						longitude: res.longitude
					})
					this.latitude = res.latitude
					this.longitude = res.longitude
				}).catch(() => {})
				this.getCurrentStore()
			},
			getCurrentStore() {
				getMyStore({
					offset: 0,
					limit: 1,
					userLng: this.longitude,
					userLat: this.latitude
				}).then((res) => {
					this.myStore = res.data && res.data.length ? res.data[0] : {}
					if (this.myStore.storeAddrId) {
						this.getCardList(this.myStore.storeAddrId)
					} else {
						this.cardList = []
						this.loading = false
					}
				}).catch(() => {
					this.cardList = []
					this.loading = false
				})
			},
			getCardList(storeAddrId) {
				getfitCardList({
					storeId: storeAddrId
				}).then((res) => {
					this.cardList = (res.data || []).map(item => {
						return {
							...item,
							cardPrice: item.cardPrice
						}
					})
				}).finally(() => {
					this.loading = false
				})
			},
			formatPrice(item) {
				return item.isNewUser == 1 ? (item.newUserPrice || item.cardPrice) : item.cardPrice
			},
			goBuy(item) {
				const storeId = this.myStore.storeId || this.myStore.storeAddrId
				if (!storeId) {
					this.config.Toast('请先选择门店')
					return
				}
				this.config.path('/pagesA/card_renewal/card_renewal?id=' + storeId + '&fitCardId=' + item.fitCardId)
			},
			switchStore() {
				this.config.path('/pagesA/select_store/select_store?type=1')
			}
		}
	}
</script>

<style scoped lang="scss">
	page {
		background: #F4F4F4;
	}

	.member-card-page {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 30rpx 32rpx 60rpx;
		box-sizing: border-box;
	}

	.hero {
		height: 238rpx;
		border-radius: 24rpx;
		background: linear-gradient(135deg, #111111, #303030);
		color: #ffffff;
		padding: 38rpx 36rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;

		.hero-kicker {
			display: inline-block;
			padding: 8rpx 22rpx;
			border-radius: 24rpx;
			background: rgba(255, 255, 255, 0.14);
			font-size: 24rpx;
			line-height: 30rpx;
			color: rgba(255, 255, 255, 0.86);
		}

		.hero-title {
			margin-top: 28rpx;
			font-size: 48rpx;
			line-height: 56rpx;
			font-weight: 800;
		}

		.hero-desc {
			margin-top: 14rpx;
			font-size: 26rpx;
			line-height: 34rpx;
			color: rgba(255, 255, 255, 0.7);
		}
	}

	.store-bar {
		min-height: 112rpx;
		background: #ffffff;
		border-radius: 20rpx;
		padding: 24rpx 28rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
		box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.06);

		.store-label {
			font-size: 22rpx;
			line-height: 28rpx;
			color: #999999;
		}

		.store-name {
			margin-top: 8rpx;
			font-size: 30rpx;
			line-height: 38rpx;
			color: #222222;
			font-weight: 800;
		}

		.store-action {
			width: 142rpx;
			height: 58rpx;
			line-height: 58rpx;
			text-align: center;
			border-radius: 29rpx;
			background: #111111;
			color: #ffffff;
			font-size: 24rpx;
			font-weight: 700;
		}
	}

	.section {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 28rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
		box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.06);

		.section-head {
			margin-bottom: 22rpx;
		}

		.section-title {
			font-size: 34rpx;
			line-height: 42rpx;
			color: #111111;
			font-weight: 800;
		}

		.section-subtitle {
			margin-top: 8rpx;
			font-size: 22rpx;
			line-height: 28rpx;
			color: #999999;
		}

		.section-count {
			font-size: 24rpx;
			color: #999999;
		}
	}

	.card-item {
		display: flex;
		align-items: center;
		justify-content: space-between;
		min-height: 148rpx;
		padding: 26rpx;
		box-sizing: border-box;
		border: 2rpx solid #eeeeee;
		border-radius: 18rpx;
		margin-bottom: 20rpx;
		background: #fbfbfb;

		&:last-child {
			margin-bottom: 0;
		}

		.card-name {
			font-size: 32rpx;
			line-height: 40rpx;
			color: #111111;
			font-weight: 800;
		}

		.card-meta {
			margin-top: 12rpx;
			font-size: 26rpx;
			line-height: 34rpx;
			color: #888888;
		}

		.card-side {
			text-align: right;
		}

		.price {
			font-size: 40rpx;
			line-height: 48rpx;
			color: #111111;
			font-weight: 900;

			text {
				font-size: 24rpx;
				margin-right: 2rpx;
			}
		}

		.buy-btn {
			margin-top: 16rpx;
			width: 136rpx;
			height: 54rpx;
			line-height: 54rpx;
			background: #111111;
			border-radius: 27rpx;
			text-align: center;
			color: #ffffff;
			font-size: 24rpx;
			font-weight: 800;
		}
	}

	.recovery {
		background: #fffaf2;
		border-color: #f3dfc2;
	}

	.empty {
		height: 112rpx;
		line-height: 112rpx;
		text-align: center;
		color: #999999;
		font-size: 26rpx;
		background: #fafafa;
		border-radius: 16rpx;
	}
</style>
