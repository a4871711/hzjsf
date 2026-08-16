<template>
	<view class="benefit-page">
		<scroll-view class="tabs" scroll-x>
			<view class="tab" :class="{ active: activeStatus === item.value }" v-for="item in tabs" :key="String(item.value)"
				@click="changeStatus(item.value)">{{ item.label }}</view>
		</scroll-view>

		<view class="list" v-if="list.length">
			<view class="card" v-for="item in list" :key="item.benefitId">
				<view class="head">
					<view class="name">{{ item.productName || '私教课程' }}</view>
					<text class="status" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="store">{{ item.storeName || '适用门店' }} · {{ Number(item.serviceType) === 2 ? '一对多' : '一对一' }}</view>
				<view class="lesson-box">
					<view><text>{{ item.remainingLessons || 0 }}</text><text>剩余可约</text></view>
					<view><text>{{ item.frozenLessons || 0 }}</text><text>已预约</text></view>
					<view><text>{{ item.usedLessons || 0 }}</text><text>已完成</text></view>
					<view><text>{{ item.totalLessons || 0 }}</text><text>总课时</text></view>
				</view>
				<view class="row"><text>权益编号</text><text>{{ item.benefitNo || '--' }}</text></view>
				<view class="row"><text>有效期至</text><text>{{ item.expireAtText }}</text></view>
				<view class="row"><text>上课规则</text><text>每日最多 {{ item.dailyLessonLimit || 1 }} 节，每节 {{ item.durationMinutes || 0 }} 分钟</text></view>
				<view class="foot" v-if="item.canBookFlag">
					<view class="book-btn" @click="goBook(item)">立即预约</view>
				</view>
			</view>
		</view>

		<view class="empty" v-else-if="loaded">
			<text>暂无私教课时权益</text>
			<view class="empty-btn" @click="goBuy">去购买课程</view>
		</view>
		<view class="more" v-if="list.length">{{ noMore ? '已经到底了' : '上拉加载更多' }}</view>
	</view>
</template>

<script>
	import { getPrivateBenefits } from '@/api/private-training.js'

	export default {
		data() {
			return {
				tabs: [
					{ label: '全部', value: '' },
					{ label: '生效中', value: 1 },
					{ label: '已用完', value: 2 },
					{ label: '已过期', value: 3 },
					{ label: '已退款', value: 4 }
				],
				activeStatus: '',
				list: [],
				page: 1,
				limit: 10,
				total: 0,
				loaded: false,
				loading: false,
				noMore: false
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
				if (this.activeStatus !== '') params.status = this.activeStatus;
				getPrivateBenefits(params).then((res) => {
					const data = res.data || {};
					const rows = (data.list || []).map((item) => Object.assign({}, item, {
						statusLabel: this.statusText(item),
						statusClassName: this.statusClass(item),
						canBookFlag: this.canBook(item),
						expireAtText: this.expireText(item.expireAt)
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
					this.config.Toast((e && e.message) || '权益加载失败');
					done && done();
				});
			},
			canBook(item) {
				return Number(item.status) === 1 && Number(item.remainingLessons) > 0 && !this.isExpired(item.expireAt);
			},
			goBook(item) {
				let url = '/pagesA/private_booking/private_booking?benefitId=' + item.benefitId +
					'&productId=' + item.productId + '&storeId=' + item.storeId +
					'&productName=' + encodeURIComponent(item.productName || '私教课程') +
					'&dailyLessonLimit=' + (item.dailyLessonLimit || 1);
				uni.navigateTo({ url });
			},
			statusText(item) {
				if (Number(item.status) === 1 && this.isExpired(item.expireAt)) return '已过期';
				return ({ 1: '生效中', 2: '已用完', 3: '已过期', 4: '已退款' })[Number(item.status)] || '未知';
			},
			statusClass(item) {
				return this.canBook(item) ? 'is-active' : 'is-gray';
			},
			isExpired(value) {
				if (!value) return false;
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				return !isNaN(d.getTime()) && d.getTime() < Date.now();
			},
			expireText(value) {
				if (!value) return '长期有效';
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(d.getTime())) return String(value);
				const p = (n) => n < 10 ? '0' + n : '' + n;
				return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
			},
			goBuy() {
				uni.switchTab({ url: '/pages/private_course/private_course' });
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.benefit-page { min-height: 100vh; background: #F4F4F4; padding-bottom: 30rpx; }
	.tabs { white-space: nowrap; padding: 20rpx 24rpx; background: #FFF; box-sizing: border-box; }
	.tab { display: inline-flex; min-width: 112rpx; height: 60rpx; margin-right: 14rpx; padding: 0 20rpx; align-items: center; justify-content: center; border-radius: 30rpx; background: #F5F5F5; color: #777; font-size: 24rpx; box-sizing: border-box; }
	.tab.active { color: #FFF; background: #E15B00; font-weight: 700; }
	.list { padding: 24rpx; }
	.card { margin-bottom: 20rpx; padding: 28rpx; background: #FFF; border-radius: 20rpx; }
	.head { display: flex; align-items: center; }
	.name { flex: 1; color: #222; font-size: 32rpx; font-weight: 900; }
	.status { font-size: 23rpx; font-weight: 700; }
	.status.is-active { color: #2FA65A; }
	.status.is-gray { color: #999; }
	.store { margin-top: 10rpx; color: #888; font-size: 23rpx; }
	.lesson-box { display: flex; margin: 28rpx 0 18rpx; padding: 24rpx 10rpx; background: #FFF7F1; border-radius: 14rpx; }
	.lesson-box view { width: 25%; display: flex; flex-direction: column; align-items: center; color: #999; font-size: 20rpx; }
	.lesson-box text:first-child { margin-bottom: 8rpx; color: #E15B00; font-size: 34rpx; font-weight: 900; }
	.row { display: flex; min-height: 54rpx; align-items: center; color: #999; font-size: 22rpx; }
	.row text:last-child { flex: 1; margin-left: 20rpx; color: #555; text-align: right; }
	.foot { display: flex; margin-top: 20rpx; padding-top: 20rpx; justify-content: flex-end; border-top: 1rpx solid #EEE; }
	.book-btn { display: flex; height: 64rpx; padding: 0 30rpx; align-items: center; justify-content: center; color: #FFF; background: #E15B00; border-radius: 34rpx; font-size: 25rpx; font-weight: 700; }
	.empty { min-height: 560rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999; font-size: 26rpx; }
	.empty-btn { margin-top: 25rpx; padding: 15rpx 40rpx; color: #FFF; background: #E15B00; border-radius: 38rpx; }
	.more { padding: 14rpx; color: #AAA; text-align: center; font-size: 22rpx; }
</style>
