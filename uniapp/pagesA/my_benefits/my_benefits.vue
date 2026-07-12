<template>
	<view class="mb-page">
		<!-- 顶部入口:我的转让记录 -->
		<view class="mb-topbar">
			<text class="mb-topbar__link" @click="goTransferList">我的转让记录 ›</text>
		</view>
		<!-- 列表 -->
		<view v-if="list.length" class="mb-list">
			<view class="mb-card" v-for="item in list" :key="item.viewKey">
				<view class="mb-card__head">
					<text class="mb-card__name">{{ item.cardNameText }}</text>
					<text class="mb-card__tag" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="mb-card__row">
					<text class="mb-card__label">生效日期</text>
					<text class="mb-card__val">{{ item.startTimeText }}</text>
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
				<!-- 仅正常且可转的权益展示试算/发起入口 -->
				<view class="mb-card__foot" v-if="item.canTransfer">
					<view class="mb-card__btn mb-card__btn--ghost" @click="quoteTransfer(item)">费用试算</view>
					<view class="mb-card__btn mb-card__btn--primary" @click="goTransfer(item)">发起转让</view>
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
		getMyBenefits,
		quoteVipTransfer
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
				loading: false,
				quoting: false
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
					startTimeText: this.formatDate(row.startTime) || '支付后生效',
					expireTimeText: this.formatDate(row.expireTime) || '支付后生效',
					originPriceText: this.formatPrice(row.originPrice),
					statusLabel: this.statusText(row),
					statusClassName: this.statusClass(row),
					// 正常 + 可转 + 未过期 才允许试算/转让
					canTransfer: row.status === 0 && row.transferable === 1 && !this.isExpired(row)
				});
			},
			// 试算本次转让应缴服务费(只读,不落单)
			quoteTransfer(item) {
				if (this.quoting) {
					return;
				}
				this.quoting = true;
				quoteVipTransfer({
					vipBenefitId: item.vipBenefitId
				}).then((res) => {
					this.quoting = false;
					const d = res.data || {};
					const fee = Number(d.serviceFee);
					const feeText = (isNaN(fee) || fee <= 0) ? '免费' : ('¥' + this.formatPrice(d.serviceFee));
					uni.showModal({
						title: '转让费用试算',
						content: '本次为第 ' + d.thisTransferNo + ' 次转让，应缴服务费：' + feeText,
						showCancel: false,
						confirmText: '我知道了'
					});
				}).catch((e) => {
					this.quoting = false;
					this.config.Toast((e && e.message) || '试算失败');
				});
			},
			// 跳转发起转让页(带权益ID与卡名)
			goTransfer(item) {
				uni.navigateTo({
					url: '/pagesA/vip_transfer_apply/vip_transfer_apply?vipBenefitId=' + item.vipBenefitId +
						'&cardName=' + (item.cardNameText || '')
				});
			},
			// 跳转我的转让记录
			goTransferList() {
				uni.navigateTo({
					url: '/pagesA/vip_transfer_list/vip_transfer_list'
				});
			},
			// 到期实时判断:status=0 且 expireTime < now 视为已过期
			isExpired(item) {
				const t = this.toTime(item.expireTime);
				if (!t) return false;
				return t < Date.now();
			},
			// 后端 FastJSON 把 Date 序列化成毫秒时间戳数字,这里兼容 数字/字符串 两种形态
			toTime(v) {
				if (!v) return 0;
				if (typeof v === 'number') return v;
				const t = new Date(String(v).replace(/-/g, '/')).getTime();
				return isNaN(t) ? 0 : t;
			},
			// 时间戳/日期字符串 → 'YYYY-MM-DD' 展示
			formatDate(v) {
				const t = this.toTime(v);
				if (!t) return '';
				const d = new Date(t);
				const p = (n) => (n < 10 ? '0' + n : '' + n);
				return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
			},
			statusText(item) {
				if (item.status === 9) return '待支付';
				if (item.status === 1) return '已转出';
				if (item.status === 2) return '已冻结';
				if (item.status === 4) return '已注销';
				if (item.status === 3 || this.isExpired(item)) return '已过期';
				return '正常';
			},
			statusClass(item) {
				if (item.status === 3 || item.status === 1 || item.status === 4 || this.isExpired(item)) return 'is-gray';
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

	.mb-topbar {
		display: flex;
		justify-content: flex-end;
		padding: 4rpx 4rpx 16rpx;
	}

	.mb-topbar__link {
		font-size: 26rpx;
		color: #C8923B;
	}

	.mb-card__foot {
		display: flex;
		justify-content: flex-end;
		padding: 16rpx 0 12rpx;
		border-top: 1rpx solid #F2F2F2;
		margin-top: 8rpx;
	}

	.mb-card__btn {
		padding: 12rpx 36rpx;
		font-size: 26rpx;
		border-radius: 100rpx;
	}

	.mb-card__btn--ghost {
		color: #C8923B;
		border: 1rpx solid #C8923B;
		margin-right: 20rpx;
	}

	.mb-card__btn--primary {
		color: #FFFFFF;
		background: #E8541E;
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
