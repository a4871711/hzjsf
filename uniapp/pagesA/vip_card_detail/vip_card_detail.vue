<template>
	<view class="vc-detail">
		<!-- 顶部权益卡主信息 -->
		<view class="vc-hero">
			<view class="vc-kicker">VIP 权益卡</view>
			<view class="vc-title">{{ card.cardName || '--' }}</view>
			<view class="vc-price-row">
				<text class="now"><text class="sym">¥</text>{{ formatPrice(curPrice) }}</text>
				<text class="base" v-if="isRaised">首发价 ¥{{ formatPrice(card.price) }}</text>
			</view>
			<view class="vc-tags">
				<text class="tag">有效期 {{ card.validityDays || 365 }} 天</text>
				<text class="tag" v-if="card.showBuyCount == 1">已售 {{ card.soldCount || 0 }} 份</text>
				<text class="tag" v-if="storeCount">适用 {{ storeCount }} 家门店</text>
			</view>
			<view class="vc-raise-tip" v-if="isRaised && card.stepNum > 0">
				首发优惠递减：每 {{ card.stepNum }} 人涨 ¥{{ formatPrice(card.stepAddPrice) }}<text
					v-if="card.priceCap">，封顶 ¥{{ formatPrice(card.priceCap) }}</text>，早买更划算
			</view>
		</view>

		<!-- 权益详情 -->
		<view class="vc-section">
			<view class="vc-sec-title">权益详情</view>
			<view class="vc-desc">{{ card.benefitDesc || '暂无权益描述' }}</view>
		</view>

		<!-- 实时购买人数(仅 showBuyCount=1 展示) -->
		<view class="vc-section" v-if="card.showBuyCount == 1">
			<view class="vc-sec-title">实时购买</view>
			<view class="vc-buy-count">已有 <text class="num">{{ card.soldCount || 0 }}</text> 人购买</view>
		</view>

		<!-- 占位:底部安全区,避免内容被购买栏遮挡 -->
		<view class="vc-bar-holder"></view>

		<!-- 底部购买栏 -->
		<view class="vc-bar flex_s">
			<view class="vc-bar-price">
				<text class="label">合计</text>
				<text class="amt"><text class="sym">¥</text>{{ formatPrice(curPrice) }}</text>
			</view>
			<view class="vc-bar-btn" @click="onBuy">立即购买</view>
		</view>
	</view>
</template>

<script>
	import {
		getVipCardDetail
	} from '@/api/index'

	export default {
		data() {
			return {
				vipCardId: null,
				card: {}
			}
		},
		computed: {
			// 展示价:优先后端实时动态价 currentPrice
			curPrice() {
				return this.card.currentPrice != null ? this.card.currentPrice : this.card.price;
			},
			// 当前价高于首发价 → 已触发动态涨价
			isRaised() {
				return Number(this.curPrice) > Number(this.card.price);
			},
			// 适用门店数量(store_addr_ids 逗号分隔)
			storeCount() {
				if (!this.card.storeAddrIds) return 0;
				return String(this.card.storeAddrIds).split(',').filter(Boolean).length;
			}
		},
		onLoad(option) {
			this.vipCardId = option.vipCardId;
			this.getDetail();
		},
		methods: {
			getDetail() {
				if (!this.vipCardId) {
					this.config.Toast('缺少权益卡参数');
					return;
				}
				getVipCardDetail({
					vipCardId: this.vipCardId
				}).then((res) => {
					this.card = res.data || {};
					if (this.card.cardName) {
						uni.setNavigationBarTitle({
							title: this.card.cardName
						});
					}
				}).catch((e) => {
					// 下架/不存在等:提示后返回
					this.config.Toast((e && e.message) || '权益卡不存在或已下架');
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				});
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			onBuy() {
				// 购买接口(/api/vipCard/buy)尚未实现,先占位提示,不伪造下单
				this.config.Toast('购买功能即将开放，敬请期待');
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.vc-detail {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 24rpx 28rpx 0;
		box-sizing: border-box;
	}

	.vc-hero {
		border-radius: 24rpx;
		background: linear-gradient(135deg, #ff8a3d 0%, #E15B00 100%);
		color: rgba(255, 255, 255, 0.9);
		padding: 38rpx 36rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;

		.vc-kicker {
			display: inline-block;
			padding: 6rpx 20rpx;
			border-radius: 22rpx;
			background: rgba(255, 255, 255, 0.25);
			color: #ffffff;
			font-size: 22rpx;
		}

		.vc-title {
			margin-top: 24rpx;
			font-size: 44rpx;
			line-height: 54rpx;
			font-weight: 800;
			color: #ffffff;
		}

		.vc-price-row {
			margin-top: 22rpx;
			display: flex;
			align-items: baseline;

			.now {
				font-size: 60rpx;
				font-weight: 900;
				color: #ffffff;
				line-height: 1;

				.sym {
					font-size: 30rpx;
					font-weight: 800;
				}
			}

			.base {
				margin-left: 16rpx;
				font-size: 24rpx;
				color: rgba(255, 255, 255, 0.6);
				text-decoration: line-through;
			}
		}

		.vc-tags {
			margin-top: 22rpx;
			display: flex;
			flex-wrap: wrap;

			.tag {
				margin: 0 14rpx 12rpx 0;
				padding: 6rpx 18rpx;
				border-radius: 20rpx;
				background: rgba(255, 255, 255, 0.22);
				font-size: 22rpx;
				color: #ffffff;
			}
		}

		.vc-raise-tip {
			margin-top: 12rpx;
			font-size: 22rpx;
			line-height: 32rpx;
			color: #fff2cc;
		}
	}

	.vc-section {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 28rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
		box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.06);

		.vc-sec-title {
			font-size: 32rpx;
			font-weight: 800;
			color: #111111;
			margin-bottom: 18rpx;
		}

		.vc-desc {
			font-size: 26rpx;
			line-height: 42rpx;
			color: #555555;
			white-space: pre-wrap;
			word-break: break-all;
		}

		.vc-buy-count {
			font-size: 26rpx;
			color: #555555;

			.num {
				font-size: 40rpx;
				font-weight: 900;
				color: #E15B00;
				margin: 0 6rpx;
			}
		}
	}

	.vc-bar-holder {
		height: 132rpx;
	}

	.vc-bar {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		height: 110rpx;
		padding: 0 32rpx calc(env(safe-area-inset-bottom));
		box-sizing: content-box;
		background: #ffffff;
		box-shadow: 0rpx -2rpx 16rpx rgba(0, 0, 0, 0.06);

		.vc-bar-price {
			display: flex;
			align-items: baseline;

			.label {
				font-size: 24rpx;
				color: #999999;
				margin-right: 10rpx;
			}

			.amt {
				font-size: 44rpx;
				font-weight: 900;
				color: #E15B00;

				.sym {
					font-size: 26rpx;
				}
			}
		}

		.vc-bar-btn {
			width: 260rpx;
			height: 78rpx;
			line-height: 78rpx;
			text-align: center;
			border-radius: 39rpx;
			background: #E15B00;
			color: #ffffff;
			font-size: 30rpx;
			font-weight: 800;
		}
	}
</style>
