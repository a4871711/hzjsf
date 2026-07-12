<template>
	<view class="vip-benefit-card">
		<view class="bc-item" v-for="item in cardList" :key="item.vipCardId"
			@click="goDetail(item)">
			<!-- 顶部:图标 + 卡名 + 限时特惠徽章 / 右侧标语 -->
			<view class="bc-head">
				<view class="bc-head-l">
					<view class="bc-icon"><text class="vip-txt">VIP</text></view>
					<text class="bc-name">{{ item.cardName || '--' }}</text>
					<text class="bc-badge" v-if="isRaised(item)">限时特惠</text>
				</view>
				<text class="bc-slogan">享专属会员权益</text>
			</view>

			<!-- 价格行 + 立即抢购按钮 -->
			<view class="bc-price-row">
				<view class="bc-price">
					<text class="sym">¥</text>
					<text class="num">{{ formatPrice(curPrice(item)) }}</text>
					<text class="early" v-if="isRaised(item)">早买更优惠</text>
				</view>
				<!-- heldThis=持有的就是这张卡(列表此时只返回它,下架也返回)→ 入口变"查看" -->
				<view class="bc-btn">{{ item.heldThis ? '查看' : '立即抢购' }}</view>
			</view>

			<!-- 副标题(权益描述) -->
			<view class="bc-sub">{{ item.benefitDesc || '开通权益卡 · 享受专属优惠价格' }}</view>

			<view class="bc-divider"></view>

			<!-- 底部标签:用权益卡真实字段(有效期/已售/适用门店) -->
			<view class="bc-tags">
				<text class="bc-tag">有效期{{ item.validityDays || 365 }}天</text>
				<text class="bc-tag" v-if="item.showBuyCount == 1">已售{{ item.soldCount || 0 }}份</text>
				<text class="bc-tag" v-if="storeCount(item)">适用{{ storeCount(item) }}家门店</text>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "vip-benefit-card",
		props: {
			cardList: {
				type: Array,
				default: () => []
			},
			// 当前门店 storeId,点进详情页后带给下单页(card_renewal 需 id=storeId)
			storeId: {
				type: [String, Number],
				default: ''
			}
		},
		methods: {
			// 展示价:优先后端实时动态价 currentPrice,缺失时回退基础售价 price
			curPrice(item) {
				return item.currentPrice != null ? item.currentPrice : item.price;
			},
			// 当前价高于首发价 → 已触发动态涨价(越晚买越贵)
			isRaised(item) {
				return Number(this.curPrice(item)) > Number(item.price);
			},
			// 适用门店数量(store_addr_ids 逗号分隔;列表接口未返回该字段时取 0,标签自动隐藏)
			storeCount(item) {
				if (!item.storeAddrIds) return 0;
				return String(item.storeAddrIds).split(',').filter(Boolean).length;
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			goDetail(item) {
				if (!item.vipCardId) return;
				const sid = this.storeId ? ('&storeId=' + this.storeId) : '';
				this.config.path('/pagesA/vip_card_detail/vip_card_detail?vipCardId=' + item.vipCardId + sid);
			}
		}
	}
</script>

<style lang="scss" scoped>
	.vip-benefit-card {
		width: 100%;

		.bc-item {
			box-sizing: border-box;
			padding: 28rpx 30rpx;
			margin-bottom: 24rpx;
			border-radius: 20rpx;
			background: #ffffff;
			border: 3rpx solid #E15B00;

			&:last-child {
				margin-bottom: 0;
			}

			// 顶部行
			.bc-head {
				display: flex;
				align-items: center;
				justify-content: space-between;

				.bc-head-l {
					display: flex;
					align-items: center;
					flex: 1;
					min-width: 0;

					.bc-icon {
						flex-shrink: 0;
						width: 56rpx;
						height: 56rpx;
						margin-right: 16rpx;
						border-radius: 50%;
						background: linear-gradient(135deg, #ff8a3d 0%, #E15B00 100%);
						display: flex;
						align-items: center;
						justify-content: center;

						.vip-txt {
							font-size: 22rpx;
							font-weight: 800;
							color: #ffffff;
						}
					}

					.bc-name {
						font-size: 34rpx;
						font-weight: 800;
						color: #1f1f1f;
						white-space: nowrap;
						overflow: hidden;
						text-overflow: ellipsis;
					}

					.bc-badge {
						flex-shrink: 0;
						margin-left: 14rpx;
						padding: 4rpx 14rpx;
						border-radius: 16rpx;
						background: #fdeee2;
						color: #E15B00;
						font-size: 20rpx;
					}
				}

				.bc-slogan {
					flex-shrink: 0;
					margin-left: 12rpx;
					font-size: 22rpx;
					color: #999999;
				}
			}

			// 价格行
			.bc-price-row {
				margin-top: 22rpx;
				display: flex;
				align-items: center;
				justify-content: space-between;

				.bc-price {
					display: flex;
					align-items: baseline;
					min-width: 0;

					.sym {
						font-size: 28rpx;
						font-weight: 800;
						color: #E15B00;
					}

					.num {
						font-size: 56rpx;
						font-weight: 900;
						color: #E15B00;
						line-height: 1;
					}

					.early {
						margin-left: 12rpx;
						font-size: 20rpx;
						color: #3aa53a;
						white-space: nowrap;
					}
				}

				.bc-btn {
					flex-shrink: 0;
					height: 64rpx;
					line-height: 64rpx;
					padding: 0 36rpx;
					box-sizing: border-box;
					text-align: center;
					border-radius: 32rpx;
					background: #E15B00;
					color: #ffffff;
					font-size: 28rpx;
					font-weight: 700;
				}
			}

			// 副标题
			.bc-sub {
				margin-top: 10rpx;
				font-size: 22rpx;
				color: #999999;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
			}

			.bc-divider {
				height: 1rpx;
				background: #eeeeee;
				margin: 24rpx 0;
			}

			// 底部标签
			.bc-tags {
				display: flex;
				flex-wrap: wrap;
				margin-bottom: -12rpx;

				.bc-tag {
					margin: 0 14rpx 12rpx 0;
					padding: 6rpx 20rpx;
					border-radius: 18rpx;
					background: #fdeee2;
					border: 1rpx solid #f5c8ab;
					color: #E15B00;
					font-size: 22rpx;
				}
			}
		}
	}
</style>
