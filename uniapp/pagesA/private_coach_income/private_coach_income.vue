<template>
	<view class="income-page">
		<view class="summary-card">
			<view class="month-label">本月收入</view>
			<view class="month-value">¥{{ money(summary.monthIncome) }}</view>
			<view class="summary-row">
				<view class="summary-item">
					<text class="summary-value">¥{{ money(summary.monthLessonIncome) }}</text>
					<text class="summary-label">授课提成</text>
				</view>
				<view class="summary-line"></view>
				<view class="summary-item">
					<text class="summary-value">¥{{ money(summary.monthSaleIncome) }}</text>
					<text class="summary-label">销售提成</text>
				</view>
				<view class="summary-line"></view>
				<view class="summary-item">
					<text class="summary-value">¥{{ money(summary.totalIncome) }}</text>
					<text class="summary-label">累计收入</text>
				</view>
			</view>
		</view>

		<view class="tabs">
			<view class="tab" :class="{ active: tab.value === type }" v-for="tab in tabs" :key="tab.value" @click="changeType(tab.value)">
				{{ tab.label }}
			</view>
		</view>

		<view class="detail-list">
			<view class="detail-card" v-for="item in list" :key="item.id">
				<view class="detail-icon" :class="item.incomeType">
					<u-icon :name="item.incomeType === 'lesson' ? 'calendar' : 'shopping-cart'" color="#FF5617" size="28"></u-icon>
				</view>
				<view class="detail-main">
					<view class="detail-title">{{ typeLabel(item) }}</view>
					<view class="detail-time">{{ item.transactionTime || '-' }}</view>
					<view class="detail-order" v-if="item.orderNo">订单号：{{ item.orderNo }}</view>
				</view>
				<view class="detail-side">
					<text class="amount" :class="{ refund: Number(item.amount) < 0 }">{{ Number(item.amount) >= 0 ? '+' : '' }}¥{{ money(item.amount) }}</text>
					<text class="percent" v-if="item.percent !== null && item.percent !== undefined">提成 {{ item.percent }}%</text>
					<text class="status" :class="{ done: Number(item.status) === 1 }">{{ statusLabel(item.status) }}</text>
				</view>
			</view>
		</view>

		<view class="empty" v-if="loaded && !list.length">暂无{{ currentTabLabel }}明细</view>
		<view class="load-more" v-if="list.length">{{ hasMore ? (loading ? '加载中...' : '上拉加载更多') : '没有更多了' }}</view>
	</view>
</template>

<script>
	import { getPrivateCoachIncomeList } from '@/api/private-training.js'

	export default {
		data() {
			return {
				loaded: false,
				loading: false,
				page: 1,
				limit: 15,
				hasMore: false,
				type: '',
				summary: {},
				list: [],
				tabs: [
					{ value: '', label: '全部' },
					{ value: 'lesson', label: '授课提成' },
					{ value: 'sale', label: '销售提成' }
				]
			}
		},
		computed: {
			currentTabLabel() {
				const hit = this.tabs.find(item => item.value === this.type);
				return hit && hit.value ? hit.label : '收入';
			}
		},
		onLoad() {
			this.load(true);
		},
		onPullDownRefresh() {
			this.load(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (this.hasMore && !this.loading) this.load(false);
		},
		methods: {
			changeType(value) {
				if (value === this.type) return;
				this.type = value;
				this.load(true);
			},
			load(reset, done) {
				if (this.loading) {
					done && done();
					return;
				}
				if (reset) this.page = 1;
				this.loading = true;
				getPrivateCoachIncomeList({ page: this.page, limit: this.limit, type: this.type }).then(res => {
					const data = res.data || {};
					this.summary = data.summary || {};
					this.list = reset ? (data.list || []) : this.list.concat(data.list || []);
					this.hasMore = !!data.hasMore;
					if (this.hasMore) this.page += 1;
				}).catch(e => this.config.Toast((e && e.message) || '收入明细加载失败'))
					.then(() => {
						this.loaded = true;
						this.loading = false;
						done && done();
					});
			},
			typeLabel(item) {
				if (Number(item.tradeType) === 2) return '提成退款';
				return item.incomeType === 'lesson' ? '授课提成' : '销售提成';
			},
			statusLabel(status) {
				return ({ 0: '处理中', 1: '已入账', 2: '失败' })[Number(status)] || '未知';
			},
			money(value) {
				const amount = Number(value || 0);
				if (!Number.isFinite(amount)) return '0';
				return Number.isInteger(amount) ? String(amount) : amount.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.income-page { min-height: 100vh; padding: 28rpx; background: #F4F4F6; box-sizing: border-box; }
	.summary-card { padding: 34rpx 28rpx 28rpx; color: #FFF; background: linear-gradient(120deg, #FF5617, #FF803D); border-radius: 24rpx; }
	.month-label { color: rgba(255, 255, 255, .80); font-size: 22rpx; }
	.month-value { margin-top: 8rpx; font-size: 54rpx; font-weight: 900; }
	.summary-row { display: flex; align-items: center; margin-top: 30rpx; padding-top: 26rpx; border-top: 1rpx solid rgba(255, 255, 255, .22); }
	.summary-item { display: flex; min-width: 0; flex: 1; flex-direction: column; align-items: center; }
	.summary-value { font-size: 25rpx; font-weight: 900; }
	.summary-label { margin-top: 7rpx; color: rgba(255, 255, 255, .72); font-size: 18rpx; }
	.summary-line { width: 1rpx; height: 46rpx; background: rgba(255, 255, 255, .22); }
	.tabs { display: flex; margin-top: 26rpx; padding: 8rpx; background: #FFF; border-radius: 38rpx; }
	.tab { display: flex; height: 60rpx; flex: 1; align-items: center; justify-content: center; color: #898A91; border-radius: 30rpx; font-size: 22rpx; }
	.tab.active { color: #FFF; background: #FF5617; font-weight: 800; }
	.detail-list { margin-top: 22rpx; }
	.detail-card { display: flex; align-items: center; min-height: 128rpx; margin-bottom: 16rpx; padding: 22rpx; background: #FFF; border-radius: 20rpx; box-sizing: border-box; }
	.detail-icon { display: flex; width: 68rpx; height: 68rpx; flex: 0 0 auto; align-items: center; justify-content: center; background: #FFF0E8; border-radius: 18rpx; }
	.detail-icon.sale { background: #FFF5E4; }
	.detail-main { min-width: 0; margin-left: 18rpx; flex: 1; }
	.detail-title { color: #24252A; font-size: 26rpx; font-weight: 900; }
	.detail-time { margin-top: 7rpx; color: #9A9BA4; font-size: 19rpx; }
	.detail-order { overflow: hidden; margin-top: 6rpx; color: #B0B1B7; font-size: 18rpx; text-overflow: ellipsis; white-space: nowrap; }
	.detail-side { display: flex; margin-left: 16rpx; flex: 0 0 auto; flex-direction: column; align-items: flex-end; }
	.amount { color: #159658; font-size: 27rpx; font-weight: 900; }
	.amount.refund { color: #E0574F; }
	.percent { margin-top: 7rpx; color: #96979E; font-size: 18rpx; }
	.status { margin-top: 7rpx; color: #A2A3AA; font-size: 18rpx; }
	.status.done { color: #159658; }
	.empty { padding: 110rpx 20rpx; color: #A0A1A8; text-align: center; font-size: 24rpx; }
	.load-more { padding: 24rpx 0 40rpx; color: #A0A1A8; text-align: center; font-size: 20rpx; }
</style>
