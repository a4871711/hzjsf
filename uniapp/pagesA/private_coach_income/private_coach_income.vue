<template>
	<view class="income-page">
		<view class="summary-card">
			<picker mode="date" fields="month" :value="selectedMonth" :end="currentMonth" @change="changeMonth">
				<view class="month-label">
					<text>{{ selectedMonthLabel }}收入</text>
					<view class="month-arrow">
						<u-icon name="arrow-down-fill" color="rgba(255, 255, 255, .80)" size="16"></u-icon>
					</view>
				</view>
			</picker>
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

		<view class="withdraw-card">
			<view class="withdraw-balance">
				<text class="withdraw-label">可提现金额</text>
				<text class="withdraw-value">¥{{ money(summary.availableAmount) }}</text>
			</view>
			<view class="withdraw-meta">
				<text>冻结中 ¥{{ money(summary.frozenAmount) }}</text>
				<text>已结算 ¥{{ money(summary.settledAmount) }}</text>
			</view>
			<view class="withdraw-button" :class="{ disabled: !canWithdraw }" @click="openWithdraw">发起提现</view>
		</view>

		<view class="tabs">
			<view class="tab" :class="{ active: tab.value === type }" v-for="tab in tabs" :key="tab.value" @click="changeType(tab.value)">
				{{ tab.label }}
			</view>
		</view>

		<view class="detail-list" v-if="type !== 'withdrawal'">
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
		<view class="withdrawal-list" v-else>
			<view class="withdrawal-record" v-for="item in withdrawalList" :key="item.id">
				<view class="withdrawal-record-main">
					<text class="withdrawal-title">提现申请</text>
					<text class="withdrawal-time">{{ item.createdAt || '-' }}</text>
					<text class="withdrawal-bank">{{ item.bankName || '-' }} · {{ item.bankCardNo || '-' }}</text>
				</view>
				<view class="withdrawal-record-side">
					<text class="withdrawal-amount">¥{{ money(item.actualSettlementAmount || item.settlementAmount || item.requestedAmount) }}</text>
					<text class="withdrawal-status" :class="{ success: Number(item.status) === 2, failed: Number(item.status) === 1 }">{{ withdrawalStatusLabel(item.status) }}</text>
				</view>
			</view>
		</view>

		<view class="empty" v-if="type !== 'withdrawal' && loaded && !list.length">暂无{{ currentTabLabel }}明细</view>
		<view class="empty" v-if="type === 'withdrawal' && withdrawalLoaded && !withdrawalList.length">暂无提现记录</view>
		<view class="load-more" v-if="type !== 'withdrawal' && list.length">{{ hasMore ? (loading ? '加载中...' : '上拉加载更多') : '没有更多了' }}</view>
		<view class="load-more" v-if="type === 'withdrawal' && withdrawalList.length">{{ withdrawalHasMore ? (withdrawalLoading ? '加载中...' : '上拉加载更多') : '没有更多了' }}</view>

		<view class="withdraw-mask" v-if="withdrawVisible" @click="withdrawVisible = false">
			<view class="withdraw-dialog" @click.stop>
				<view class="withdraw-dialog-title">发起提现</view>
				<view class="withdraw-form-row">
					<text class="withdraw-form-label">提现金额</text>
					<input class="withdraw-form-input" type="digit" v-model.trim="withdrawForm.amount" placeholder="请输入金额" />
				</view>
				<view class="withdraw-form-row">
					<text class="withdraw-form-label">收款人</text>
					<input class="withdraw-form-input" v-model.trim="withdrawForm.accountName" placeholder="请输入银行卡持有人姓名" />
				</view>
				<view class="withdraw-form-row">
					<text class="withdraw-form-label">开户行</text>
					<input class="withdraw-form-input" v-model.trim="withdrawForm.bankName" placeholder="请输入开户行名称" />
				</view>
				<view class="withdraw-form-row">
					<text class="withdraw-form-label">银行卡号</text>
					<input class="withdraw-form-input" type="number" maxlength="19" v-model.trim="withdrawForm.bankCardNo" placeholder="请输入银行卡号" />
				</view>
				<view class="withdraw-form-tip">提交后金额会先冻结，审核通过后才按实际结算金额扣减。</view>
				<view class="withdraw-dialog-actions">
					<view class="withdraw-cancel" @click="withdrawVisible = false">取消</view>
					<view class="withdraw-submit" :class="{ disabled: withdrawSubmitting }" @click="submitWithdraw">{{ withdrawSubmitting ? '提交中...' : '确认提现' }}</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	function currentMonthValue() {
		const now = new Date();
		const month = now.getMonth() + 1;
		return String(now.getFullYear()) + '-' + (month < 10 ? '0' : '') + month;
	}

	import {
		getPrivateCoachIncomeList,
		getPrivateCoachWithdrawalList,
		applyPrivateCoachWithdrawal
	} from '@/api/private-training.js'

	export default {
		data() {
			const currentMonth = currentMonthValue();
			return {
				currentMonth,
				selectedMonth: currentMonth,
				loaded: false,
				loading: false,
				withdrawalLoaded: false,
				withdrawalLoading: false,
				withdrawalPage: 1,
				withdrawalLimit: 10,
				withdrawalHasMore: false,
				withdrawalList: [],
				page: 1,
				limit: 15,
				hasMore: false,
				type: '',
				summary: {},
				list: [],
				withdrawVisible: false,
				withdrawSubmitting: false,
				withdrawForm: { amount: '', accountName: '', bankName: '', bankCardNo: '' },
				tabs: [
				{ value: '', label: '全部' },
				{ value: 'lesson', label: '授课提成' },
				{ value: 'sale', label: '销售提成' },
				{ value: 'withdrawal', label: '提现记录' }
			]
			}
		},
		computed: {
			selectedMonthLabel() {
				const parts = String(this.selectedMonth || '').split('-');
				return parts.length === 2 ? parts[0] + '年' + Number(parts[1]) + '月' : '本月';
			},
			currentTabLabel() {
				const hit = this.tabs.find(item => item.value === this.type);
				return hit && hit.value ? hit.label : '收入';
			},
			canWithdraw() {
				return Number(this.summary.availableAmount || 0) > 0 && !this.withdrawSubmitting;
			}
		},
		onLoad() {
			this.load(true);
		},
			onPullDownRefresh() {
			if (this.type === 'withdrawal') {
				this.loadWithdrawals(true, () => uni.stopPullDownRefresh());
				return;
			}
			this.load(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (this.type === 'withdrawal') {
				if (this.withdrawalHasMore && !this.withdrawalLoading) this.loadWithdrawals(false);
				return;
			}
			if (this.hasMore && !this.loading) this.load(false);
		},
		methods: {
			changeMonth(event) {
				const month = event && event.detail && event.detail.value;
				if (!/^\d{4}-\d{2}$/.test(month) || month === this.selectedMonth) return;
				this.selectedMonth = month;
				this.type === 'withdrawal' ? this.loadWithdrawals(true) : this.load(true);
			},
			changeType(value) {
				if (value === this.type) return;
				this.type = value;
				value === 'withdrawal' ? this.loadWithdrawals(true) : this.load(true);
			},
			load(reset, done) {
				if (this.loading) {
					done && done();
					return;
				}
				if (reset) this.page = 1;
				this.loading = true;
				getPrivateCoachIncomeList({
					page: this.page,
					limit: this.limit,
					type: this.type,
					month: this.selectedMonth
				}).then(res => {
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
			loadWithdrawals(reset, done) {
				if (this.withdrawalLoading) {
					done && done();
					return;
				}
				if (reset) this.withdrawalPage = 1;
				this.withdrawalLoading = true;
				getPrivateCoachWithdrawalList({
					page: this.withdrawalPage,
					limit: this.withdrawalLimit,
					month: this.selectedMonth
				}).then(res => {
					const data = res.data || {};
					this.summary = data.summary || {};
					this.withdrawalList = reset ? (data.list || []) : this.withdrawalList.concat(data.list || []);
					this.withdrawalHasMore = !!data.hasMore;
					if (this.withdrawalHasMore) this.withdrawalPage += 1;
				}).catch(e => this.config.Toast((e && e.message) || '提现记录加载失败'))
					.then(() => {
						this.withdrawalLoaded = true;
						done && done();
						this.withdrawalLoading = false;
					});
			},
			openWithdraw() {
				if (!this.canWithdraw) {
					this.config.Toast('当前没有可提现金额');
					return;
				}
				this.withdrawForm.amount = this.money(this.summary.availableAmount);
				this.withdrawVisible = true;
			},
			submitWithdraw() {
				if (this.withdrawSubmitting) return;
				const amount = Number(this.withdrawForm.amount);
				const accountName = String(this.withdrawForm.accountName || '').trim();
				const bankName = String(this.withdrawForm.bankName || '').trim();
				const bankCardNo = String(this.withdrawForm.bankCardNo || '').replace(/[\s-]/g, '');
				if (!Number.isFinite(amount) || amount <= 0) {
					this.config.Toast('请输入正确的提现金额');
					return;
				}
				if (amount > Number(this.summary.availableAmount || 0)) {
					this.config.Toast('提现金额不能超过可提现金额');
					return;
				}
				if (!accountName || !bankName || !/^\d{12,19}$/.test(bankCardNo)) {
					this.config.Toast('请填写正确的收款人、开户行和银行卡号');
					return;
				}
				this.withdrawSubmitting = true;
				applyPrivateCoachWithdrawal({
					amount: amount.toFixed(2),
					accountName,
					bankName,
					bankCardNo
				}).then(() => {
					this.withdrawVisible = false;
					this.config.Toast('提现已提交，金额已冻结，等待审核');
					if (this.type === 'withdrawal') this.loadWithdrawals(true);
					else this.load(true);
				}).catch(e => this.config.Toast((e && e.message) || '提现提交失败'))
					.then(() => { this.withdrawSubmitting = false; });
			},
			withdrawalStatusLabel(status) {
				return ({ 0: '待审核', 1: '已驳回', 2: '已通过' })[Number(status)] || '未知';
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
	.month-label { display: flex; align-items: center; color: rgba(255, 255, 255, .80); font-size: 22rpx; }
	.month-arrow { margin-left: 10rpx; }
	.month-value { margin-top: 8rpx; font-size: 54rpx; font-weight: 900; }
	.summary-row { display: flex; align-items: center; margin-top: 30rpx; padding-top: 26rpx; border-top: 1rpx solid rgba(255, 255, 255, .22); }
	.summary-item { display: flex; min-width: 0; flex: 1; flex-direction: column; align-items: center; }
	.summary-value { font-size: 25rpx; font-weight: 900; }
	.summary-label { margin-top: 7rpx; color: rgba(255, 255, 255, .72); font-size: 18rpx; }
	.summary-line { width: 1rpx; height: 46rpx; background: rgba(255, 255, 255, .22); }
	.withdraw-card { margin-top: 22rpx; padding: 24rpx 26rpx; background: #FFF; border-radius: 20rpx; }
	.withdraw-balance { display: flex; align-items: baseline; justify-content: space-between; }
	.withdraw-label { color: #666870; font-size: 23rpx; }
	.withdraw-value { color: #FF5617; font-size: 38rpx; font-weight: 900; }
	.withdraw-meta { display: flex; justify-content: space-between; margin-top: 12rpx; color: #A0A1A8; font-size: 20rpx; }
	.withdraw-button { display: flex; height: 68rpx; margin-top: 22rpx; align-items: center; justify-content: center; color: #FFF; background: #FF5617; border-radius: 34rpx; font-size: 24rpx; font-weight: 800; }
	.withdraw-button.disabled { opacity: .45; }
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
	.withdrawal-list { margin-top: 22rpx; }
	.withdrawal-record { display: flex; align-items: center; min-height: 128rpx; margin-bottom: 16rpx; padding: 22rpx; background: #FFF; border-radius: 20rpx; box-sizing: border-box; }
	.withdrawal-record-main { min-width: 0; flex: 1; }
	.withdrawal-title { display: block; color: #24252A; font-size: 26rpx; font-weight: 900; }
	.withdrawal-time, .withdrawal-bank { display: block; overflow: hidden; margin-top: 7rpx; color: #9A9BA4; font-size: 19rpx; text-overflow: ellipsis; white-space: nowrap; }
	.withdrawal-bank { color: #B0B1B7; font-size: 18rpx; }
	.withdrawal-record-side { display: flex; margin-left: 16rpx; align-items: flex-end; flex-direction: column; }
	.withdrawal-amount { color: #24252A; font-size: 27rpx; font-weight: 900; }
	.withdrawal-status { margin-top: 7rpx; color: #E6A23C; font-size: 19rpx; }
	.withdrawal-status.success { color: #159658; }
	.withdrawal-status.failed { color: #E0574F; }
	.withdraw-mask { position: fixed; z-index: 10; top: 0; right: 0; bottom: 0; left: 0; display: flex; align-items: center; justify-content: center; padding: 28rpx; background: rgba(0, 0, 0, .45); box-sizing: border-box; }
	.withdraw-dialog { width: 100%; padding: 30rpx 26rpx 24rpx; background: #FFF; border-radius: 22rpx; box-sizing: border-box; }
	.withdraw-dialog-title { margin-bottom: 12rpx; color: #24252A; font-size: 31rpx; font-weight: 900; }
	.withdraw-form-row { display: flex; min-height: 88rpx; align-items: center; border-bottom: 1rpx solid #EFF0F2; }
	.withdraw-form-label { width: 145rpx; flex: 0 0 auto; color: #4F5057; font-size: 23rpx; }
	.withdraw-form-input { min-width: 0; flex: 1; color: #222328; font-size: 23rpx; }
	.withdraw-form-tip { margin-top: 18rpx; color: #A0A1A8; font-size: 19rpx; line-height: 30rpx; }
	.withdraw-dialog-actions { display: flex; margin-top: 26rpx; gap: 18rpx; }
	.withdraw-cancel, .withdraw-submit { display: flex; height: 76rpx; flex: 1; align-items: center; justify-content: center; border-radius: 38rpx; font-size: 24rpx; }
	.withdraw-cancel { color: #666870; background: #F1F2F4; }
	.withdraw-submit { color: #FFF; background: #FF5617; font-weight: 800; }
	.withdraw-submit.disabled { opacity: .55; }
</style>
