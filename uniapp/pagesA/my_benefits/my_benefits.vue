<template>
	<view class="mb-page">
		<!-- 列表 -->
		<view v-if="list.length" class="mb-list">
			<view class="mb-card" v-for="item in list" :key="item.viewKey">
				<view class="mb-card__head">
					<text class="mb-card__name">{{ item.cardNameText }}</text>
					<text class="mb-card__tag" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="mb-card__row">
					<text class="mb-card__label">有效期至</text>
					<text class="mb-card__val">{{ item.expireTimeText }}</text>
				</view>
				<view class="mb-card__row">
					<text class="mb-card__label">购买价</text>
					<text class="mb-card__val">¥{{ item.originPriceText }}</text>
				</view>
				<view class="mb-card__row" v-if="item.transferCount > 0">
					<text class="mb-card__label">已转让</text>
					<text class="mb-card__val">{{ item.transferCount }} 次</text>
				</view>
			</view>
		</view>

		<!-- 空态 -->
		<view v-else-if="loaded" class="mb-empty">
			<text class="mb-empty__txt">还没有权益卡</text>
			<view class="mb-empty__btn" @click="goBuy">去看看权益卡</view>
		</view>

		<!-- 加载更多提示 -->
		<view v-if="list.length" class="mb-more">{{ noMore ? '没有更多了' : '加载中...' }}</view>
	</view>
</template>

<script>
	import {
		getMyBenefits
	} from '@/api/index'

	export default {
		data() {
			return {
				list: [],
				page: 1,
				limit: 10,
				total: 0,
				loaded: false,
				noMore: false,
				loading: false
			}
		},
		onLoad() {
			this.loadList(true);
		},
		onPullDownRefresh() {
			this.loadList(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (!this.noMore) {
				this.loadList(false);
			}
		},
		methods: {
			loadList(reset, done) {
				if (this.loading) {
					done && done();
					return;
				}
				if (reset) {
					this.page = 1;
					this.noMore = false;
				}
				this.loading = true;
				getMyBenefits({
					// status 不传则后端默认查 0 正常
					page: this.page,
					limit: this.limit
				}).then((res) => {
					const data = res.data || {};
					const rows = data.list || [];
					const baseIndex = reset ? 0 : this.list.length;
					const normalizedRows = rows.map((item, index) => this.normalizeBenefit(item, baseIndex + index));
					this.total = data.totalCount || 0;
					this.list = reset ? normalizedRows : this.list.concat(normalizedRows);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) {
						this.page += 1;
					}
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					this.loading = false;
					this.loaded = true;
					this.config.Toast((e && e.message) || '加载失败');
					done && done();
				});
			},
			normalizeBenefit(item, index) {
				const row = item || {};
				return Object.assign({}, row, {
					viewKey: row.vipBenefitId ? String(row.vipBenefitId) : 'benefit-' + index,
					cardNameText: row.cardName || '权益卡',
					expireTimeText: row.expireTime || '支付后生效',
					originPriceText: this.formatPrice(row.originPrice),
					statusLabel: this.statusText(row),
					statusClassName: this.statusClass(row)
				});
			},
			// 到期实时判断:status=0 且 expireTime < now 视为已过期
			isExpired(item) {
				if (!item.expireTime) return false;
				return new Date(item.expireTime.replace(/-/g, '/')).getTime() < Date.now();
			},
			statusText(item) {
				if (item.status === 9) return '待支付';
				if (item.status === 1) return '已转出';
				if (item.status === 2) return '已冻结';
				if (item.status === 3 || this.isExpired(item)) return '已过期';
				return '正常';
			},
			statusClass(item) {
				if (item.status === 3 || item.status === 1 || this.isExpired(item)) return 'is-gray';
				return 'is-on';
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			goBuy() {
				uni.switchTab({
					url: '/pages/index/index'
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.mb-page {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 20rpx 24rpx;
	}

	.mb-card {
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 28rpx 28rpx 12rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
	}

	.mb-card__head {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 18rpx;
	}

	.mb-card__name {
		font-size: 32rpx;
		font-weight: 600;
		color: #222;
	}

	.mb-card__tag {
		font-size: 22rpx;
		padding: 4rpx 16rpx;
		border-radius: 100rpx;
	}

	.mb-card__tag.is-on {
		color: #C8923B;
		background: #FBF0DC;
	}

	.mb-card__tag.is-gray {
		color: #999;
		background: #EEE;
	}

	.mb-card__row {
		display: flex;
		justify-content: space-between;
		padding: 10rpx 0;
		font-size: 26rpx;
	}

	.mb-card__label {
		color: #999;
	}

	.mb-card__val {
		color: #333;
	}

	.mb-empty {
		padding-top: 200rpx;
		text-align: center;
	}

	.mb-empty__txt {
		display: block;
		color: #999;
		font-size: 28rpx;
		margin-bottom: 40rpx;
	}

	.mb-empty__btn {
		display: inline-block;
		padding: 18rpx 56rpx;
		background: #C8923B;
		color: #FFF;
		font-size: 28rpx;
		border-radius: 100rpx;
	}

	.mb-more {
		text-align: center;
		color: #BBB;
		font-size: 24rpx;
		padding: 20rpx 0 40rpx;
	}
</style>
