<template>
	<view class="order-page">
		<scroll-view class="tabs" scroll-x>
			<view class="tab" :class="{ active: activeStatus === item.value }" v-for="item in tabs" :key="String(item.value)"
				@click="changeStatus(item.value)">{{ item.label }}</view>
		</scroll-view>

		<view class="list" v-if="list.length">
			<view class="card" v-for="item in list" :key="item.orderNo" @click="openDetail(item)">
				<view class="head">
					<text class="order-no">订单号 {{ item.orderNo }}</text>
					<text class="source" v-if="Number(item.orderSource) === 1">赠送</text>
					<text class="status" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="course-name">{{ item.productName || '私教课程' }}</view>
				<view class="meta">{{ serviceText(item.serviceType) }} · {{ item.lessonCount || 0 }}节 · {{ item.durationMinutes || 0 }}分钟/节</view>
				<view class="time">下单时间：{{ formatTime(item.createdAt) }}</view>
				<view class="foot">
					<view class="amount"><text>{{ Number(item.orderSource) === 1 ? '赠送' : '应付' }}</text> ¥{{ priceText(item.payableAmount) }}</view>
					<view class="actions" v-if="Number(item.orderSource) !== 1 && Number(item.orderStatus) === 0">
						<view class="btn ghost" @click.stop="confirmCancel(item)">取消订单</view>
						<view class="btn primary" @click.stop="continuePay(item)">继续支付</view>
					</view>
				</view>
			</view>
		</view>

		<view class="empty" v-else-if="loaded">
			<text>暂无私教订单</text>
			<view class="empty-btn" @click="goBuy">去选课</view>
		</view>
		<view class="more" v-if="list.length">{{ noMore ? '已经到底了' : '上拉加载更多' }}</view>
	</view>
</template>

<script>
	import {
		getPrivateOrders,
		repayPrivateOrder,
		confirmPrivateOrderWechatPay,
		cancelPrivateOrder
	} from '@/api/private-training.js'

	export default {
		data() {
			return {
				tabs: [
					{ label: '全部', value: '' },
					{ label: '待支付', value: 0 },
					{ label: '已结清', value: 2 },
					{ label: '已取消', value: 3 },
					{ label: '已退款', value: 4 }
				],
				activeStatus: '',
				list: [],
				page: 1,
				limit: 10,
				total: 0,
				loaded: false,
				loading: false,
				noMore: false,
				paying: false
			}
		},
		onShow() {
			this.loadList(true);
		},
		onPullDownRefresh() {
			this.loadList(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (!this.noMore) this.loadList(false);
		},
		methods: {
			changeStatus(status) {
				if (this.activeStatus === status) return;
				this.activeStatus = status;
				this.loadList(true);
			},
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
				const params = { page: this.page, limit: this.limit };
				if (this.activeStatus !== '') params.orderStatus = this.activeStatus;
				getPrivateOrders(params).then((res) => {
					const data = res.data || {};
					const rows = (data.list || []).map((item) => Object.assign({}, item, {
						statusLabel: Number(item.orderSource) === 1 ? '赠送' : this.statusText(item.orderStatus),
						statusClassName: this.statusClass(item.orderStatus)
					}));
					this.total = Number(data.totalCount || 0);
					this.list = reset ? rows : this.list.concat(rows);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) this.page += 1;
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					this.loaded = true;
					this.loading = false;
					this.config.Toast((e && e.message) || '订单加载失败');
					done && done();
				});
			},
			openDetail(item) {
				uni.navigateTo({
					url: '/pagesA/private_order_detail/private_order_detail?orderNo=' + encodeURIComponent(item.orderNo)
				});
			},
			continuePay(item) {
				if (this.paying) return;
				this.paying = true;
				repayPrivateOrder({ orderNo: item.orderNo }).then((res) => {
					const data = res.data || {};
					if (data.paid) return { paid: true };
					return this.requestWechatPay(data.payParams || {}).then(() => {
						return this.waitForWechatConfirmation(item.orderNo);
					});
				}).then((state) => {
					this.paying = false;
					this.config.Toast(state && state.paid ? '支付成功' : '支付已提交，订单确认中');
					setTimeout(() => this.loadList(true), 600);
				}).catch((e) => {
					this.paying = false;
					const message = e && e.errMsg && e.errMsg.indexOf('cancel') !== -1 ? '支付已取消' : ((e && e.message) || '支付失败');
					this.config.Toast(message);
				});
			},
			confirmCancel(item) {
				uni.showModal({
					title: '取消订单',
					content: '取消后，本单占用的优惠券将自动退回。',
					confirmText: '确认取消',
					success: (r) => {
						if (!r.confirm) return;
						cancelPrivateOrder({ orderNo: item.orderNo }).then(() => {
							this.config.Toast('订单已取消');
							this.loadList(true);
						}).catch((e) => this.config.Toast((e && e.message) || '取消失败'));
					}
				});
			},
			requestWechatPay(params) {
				return new Promise((resolve, reject) => {
					if (!params || !params.timeStamp || !params.nonceStr || !params.package || !params.paySign) {
						reject(new Error('微信支付参数不完整'));
						return;
					}
					uni.requestPayment({
						appId: params.appId,
						timeStamp: params.timeStamp,
						nonceStr: params.nonceStr,
						package: params.package,
						signType: params.signType,
						paySign: params.paySign,
						success: resolve,
						fail: reject
					});
				});
			},
			waitForWechatConfirmation(orderNo, attempt) {
				const current = Number(attempt || 0);
				return confirmPrivateOrderWechatPay({ orderNo }).then((res) => {
					const state = res.data || {};
					if (state.paid || current >= 4) return state;
					return this.delay(800).then(() => this.waitForWechatConfirmation(orderNo, current + 1));
				}).catch(() => {
					if (current >= 4) return { paid: false };
					return this.delay(800).then(() => this.waitForWechatConfirmation(orderNo, current + 1));
				});
			},
			delay(ms) {
				return new Promise((resolve) => setTimeout(resolve, ms));
			},
			statusText(status) {
				const map = { 0: '待支付', 1: '首付已付', 2: '已结清', 3: '已取消', 4: '已退款' };
				return map[Number(status)] || '未知';
			},
			statusClass(status) {
				if (Number(status) === 0) return 'is-wait';
				if (Number(status) === 2) return 'is-done';
				return 'is-gray';
			},
			serviceText(type) {
				return Number(type) === 2 ? '一对多' : '一对一';
			},
			priceText(value) {
				const n = Number(value);
				return isNaN(n) ? '0.00' : n.toFixed(2);
			},
			formatTime(value) {
				if (!value) return '--';
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(d.getTime())) return String(value);
				const p = (n) => n < 10 ? '0' + n : '' + n;
				return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes());
			},
			goBuy() {
				uni.switchTab({ url: '/pages/private_course/private_course' });
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.order-page { min-height: 100vh; padding-bottom: 30rpx; background: #F4F4F4; }
	.tabs { white-space: nowrap; padding: 20rpx 24rpx; background: #FFF; box-sizing: border-box; }
	.tab { display: inline-flex; min-width: 112rpx; height: 60rpx; margin-right: 14rpx; padding: 0 20rpx; align-items: center; justify-content: center; border-radius: 30rpx; background: #F5F5F5; color: #777; font-size: 24rpx; box-sizing: border-box; }
	.tab.active { color: #FFF; background: #E15B00; font-weight: 700; }
	.list { padding: 24rpx; }
	.card { margin-bottom: 20rpx; padding: 26rpx; background: #FFF; border-radius: 18rpx; }
	.head { display: flex; padding-bottom: 20rpx; border-bottom: 1rpx solid #EEE; align-items: center; }
	.order-no { flex: 1; color: #999; font-size: 21rpx; }
	.status { font-size: 24rpx; font-weight: 700; }
	.source { margin-right: 14rpx; padding: 5rpx 13rpx; color: #E15B00; background: #FFF1E8; border-radius: 18rpx; font-size: 20rpx; font-weight: 700; }
	.status.is-wait { color: #E15B00; }
	.status.is-done { color: #2FA65A; }
	.status.is-gray { color: #999; }
	.course-name { margin-top: 22rpx; color: #222; font-size: 31rpx; font-weight: 800; }
	.meta, .time { margin-top: 13rpx; color: #888; font-size: 23rpx; }
	.foot { display: flex; margin-top: 24rpx; align-items: center; }
	.amount { color: #E15B00; font-size: 30rpx; font-weight: 800; }
	.amount text { color: #777; font-size: 22rpx; font-weight: 400; }
	.actions { display: flex; margin-left: auto; }
	.btn { display: flex; height: 58rpx; margin-left: 14rpx; padding: 0 22rpx; align-items: center; justify-content: center; border-radius: 30rpx; font-size: 23rpx; }
	.btn.ghost { color: #777; border: 1rpx solid #CCC; }
	.btn.primary { color: #FFF; background: #E15B00; }
	.empty { min-height: 560rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999; font-size: 26rpx; }
	.empty-btn { margin-top: 25rpx; padding: 15rpx 44rpx; color: #FFF; background: #E15B00; border-radius: 36rpx; }
	.more { padding: 14rpx; color: #AAA; text-align: center; font-size: 22rpx; }
</style>
