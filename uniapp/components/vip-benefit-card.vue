<template>
	<scroll-view class="vip-benefit-card" scroll-x="true" :show-scrollbar="false">
		<view class="bc-item" v-for="item in cardList" :key="item.vipCardId"
			@click="goDetail(item)">
			<view class="bc-top">
				<view class="bc-name">{{ item.cardName || '--' }}</view>
				<view class="bc-desc">{{ item.benefitDesc || '专属会员权益' }}</view>
			</view>
			<view class="bc-price">
				<text class="sym">¥</text>
				<text class="num">{{ formatPrice(curPrice(item)) }}</text>
				<text class="base" v-if="isRaised(item)">首发¥{{ formatPrice(item.price) }}</text>
			</view>
			<view class="bc-foot flex_s">
				<text class="meta" v-if="item.showBuyCount == 1">已售{{ item.soldCount || 0 }}</text>
				<text class="meta" v-else>有效期{{ item.validityDays || 365 }}天</text>
				<view class="bc-btn">立即抢购</view>
			</view>
		</view>
	</scroll-view>
</template>

<script>
	export default {
		name: "vip-benefit-card",
		props: {
			cardList: {
				type: Array,
				default: () => []
			}
		},
		methods: {
			// 展示价:优先后端实时动态价 currentPrice,缺失时回退基础售价 price
			curPrice(item) {
				return item.currentPrice != null ? item.currentPrice : item.price;
			},
			// 当前价高于首发价 → 已触发动态涨价,展示首发价做对比
			isRaised(item) {
				return Number(this.curPrice(item)) > Number(item.price);
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			goDetail(item) {
				if (!item.vipCardId) return;
				this.config.path('/pagesA/vip_card_detail/vip_card_detail?vipCardId=' + item.vipCardId);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.vip-benefit-card {
		white-space: nowrap;
		width: 100%;

		.bc-item {
			display: inline-flex;
			flex-direction: column;
			justify-content: space-between;
			vertical-align: top;
			width: 320rpx;
			height: 308rpx;
			box-sizing: border-box;
			padding: 26rpx 24rpx;
			margin-right: 20rpx;
			border-radius: 20rpx;
			background: linear-gradient(135deg, #fbe7d9 0%, #f6d3b6 100%);

			&:last-child {
				margin-right: 0;
			}

			.bc-top {
				.bc-name {
					font-size: 30rpx;
					font-weight: 800;
					color: #3a2a1c;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.bc-desc {
					margin-top: 10rpx;
					font-size: 22rpx;
					color: #9a7b5f;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}
			}

			.bc-price {
				display: flex;
				align-items: baseline;
				color: #E15B00;

				.sym {
					font-size: 28rpx;
					font-weight: 800;
				}

				.num {
					font-size: 56rpx;
					font-weight: 900;
					line-height: 1;
				}

				.base {
					margin-left: 12rpx;
					font-size: 22rpx;
					color: #b08968;
					text-decoration: line-through;
				}
			}

			.bc-foot {
				.meta {
					font-size: 22rpx;
					color: #8a6d52;
				}

				.bc-btn {
					min-width: 132rpx;
					height: 48rpx;
					line-height: 48rpx;
					padding: 0 18rpx;
					box-sizing: border-box;
					text-align: center;
					background: #E15B00;
					border-radius: 24rpx;
					font-size: 24rpx;
					font-weight: 700;
					color: #ffffff;
				}
			}
		}
	}
</style>
