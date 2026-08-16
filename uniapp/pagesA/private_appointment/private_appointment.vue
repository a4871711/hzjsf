<template>
	<view class="appointment-page">
		<view class="top-link" @click="goComments">我的教练评价 ›</view>
		<scroll-view class="tabs" scroll-x>
			<view class="tab" :class="{ active: activeStatus === item.value }" v-for="item in tabs" :key="String(item.value)"
				@click="changeStatus(item.value)">{{ item.label }}</view>
		</scroll-view>

		<view class="list" v-if="list.length">
			<view class="card" v-for="item in list" :key="item.appointmentNo">
				<view class="head">
					<view class="name">{{ item.productName || '私教课程' }}</view>
					<text class="status" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="datetime">{{ item.appointmentDate }} {{ item.startTime }}-{{ item.endTime }}</view>
				<view class="row"><text>教练</text><text>{{ item.coachName || '--' }}</text></view>
				<view class="row"><text>上课门店</text><text>{{ item.storeName || '--' }}</text></view>
				<view class="row"><text>预约编号</text><text>{{ item.appointmentNo }}</text></view>
				<view class="row" v-if="item.cancelReason"><text>取消原因</text><text>{{ item.cancelReason }}</text></view>
				<view class="foot" v-if="Number(item.appointmentStatus) === 1 || Number(item.appointmentStatus) === 3">
					<view class="btn ghost" v-if="Number(item.appointmentStatus) === 1" @click="confirmCancel(item)">取消预约</view>
					<view class="btn primary" v-if="Number(item.appointmentStatus) === 3 && !isReviewed(item.id)" @click="goComment(item)">评价教练</view>
					<view class="reviewed" v-if="Number(item.appointmentStatus) === 3 && isReviewed(item.id)">已评价</view>
				</view>
			</view>
		</view>

		<view class="empty" v-else-if="loaded">
			<text>暂无私教预约</text>
			<view class="empty-btn" @click="goBenefits">去预约课程</view>
		</view>
		<view class="more" v-if="list.length">{{ noMore ? '已经到底了' : '上拉加载更多' }}</view>
	</view>
</template>

<script>
	import {
		getPrivateAppointments,
		cancelPrivateAppointment,
		getPrivateComments
	} from '@/api/private-training.js'

	export default {
		data() {
			return {
				tabs: [
					{ label: '全部', value: '' },
					{ label: '已预约', value: 1 },
					{ label: '已完成', value: 3 },
					{ label: '已取消', value: 2 },
					{ label: '已爽约', value: 4 }
				],
				activeStatus: '',
				list: [],
				reviewedAppointmentIds: [],
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
				getPrivateAppointments(params).then((res) => {
					const data = res.data || {};
					const rows = (data.list || []).map((item) => Object.assign({}, item, {
						statusLabel: this.statusText(item.appointmentStatus),
						statusClassName: this.statusClass(item.appointmentStatus)
					}));
					this.total = Number(data.totalCount || 0);
					this.list = reset ? rows : this.list.concat(rows);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) this.page += 1;
					if (reset) return this.loadReviewedIds();
				}).then(() => {
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					this.loaded = true;
					this.loading = false;
					this.config.Toast((e && e.message) || '预约加载失败');
					done && done();
				});
			},
			loadReviewedIds() {
				return getPrivateComments({ page: 1, limit: 1000 }).then((res) => {
					const rows = ((res.data || {}).list) || [];
					this.reviewedAppointmentIds = rows.map((item) => String(item.appointmentId));
				}).catch(() => {
					this.reviewedAppointmentIds = [];
				});
			},
			isReviewed(appointmentId) {
				return this.reviewedAppointmentIds.indexOf(String(appointmentId)) !== -1;
			},
			confirmCancel(item) {
				uni.showModal({
					title: '取消预约',
					content: '只有在商品配置的无责取消时间之前才能取消，取消成功后课时会自动解冻。',
					confirmText: '确认取消',
					success: (res) => {
						if (!res.confirm) return;
						cancelPrivateAppointment({
							appointmentNo: item.appointmentNo,
							cancelReason: '会员在App主动取消'
						}).then(() => {
							this.config.Toast('预约已取消，课时已解冻');
							this.loadList(true);
						}).catch((e) => this.config.Toast((e && e.message) || '取消失败'));
					}
				});
			},
			goComment(item) {
				uni.navigateTo({
					url: '/pagesA/private_comment/private_comment?appointmentId=' + item.id +
						'&coachName=' + encodeURIComponent(item.coachName || '教练') +
						'&appointmentDate=' + encodeURIComponent(item.appointmentDate || '')
				});
			},
			goComments() {
				uni.navigateTo({ url: '/pagesA/private_comment/private_comment' });
			},
			goBenefits() {
				uni.navigateTo({ url: '/pagesA/private_benefit/private_benefit' });
			},
			statusText(status) {
				return ({ 1: '已预约', 2: '已取消', 3: '已完成', 4: '已爽约' })[Number(status)] || '未知';
			},
			statusClass(status) {
				if (Number(status) === 1) return 'is-booked';
				if (Number(status) === 3) return 'is-done';
				return 'is-gray';
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.appointment-page { min-height: 100vh; padding-bottom: 30rpx; background: #F4F4F4; }
	.top-link { padding: 22rpx 30rpx 0; color: #E15B00; background: #FFF; text-align: right; font-size: 23rpx; }
	.tabs { white-space: nowrap; padding: 18rpx 24rpx 22rpx; background: #FFF; box-sizing: border-box; }
	.tab { display: inline-flex; min-width: 112rpx; height: 60rpx; margin-right: 14rpx; padding: 0 20rpx; align-items: center; justify-content: center; border-radius: 30rpx; background: #F5F5F5; color: #777; font-size: 24rpx; box-sizing: border-box; }
	.tab.active { color: #FFF; background: #E15B00; font-weight: 700; }
	.list { padding: 24rpx; }
	.card { margin-bottom: 20rpx; padding: 28rpx; background: #FFF; border-radius: 20rpx; }
	.head { display: flex; align-items: center; }
	.name { flex: 1; color: #222; font-size: 31rpx; font-weight: 900; }
	.status { font-size: 23rpx; font-weight: 700; }
	.status.is-booked { color: #E15B00; }
	.status.is-done { color: #2FA65A; }
	.status.is-gray { color: #999; }
	.datetime { margin: 22rpx 0 12rpx; padding: 20rpx; color: #E15B00; background: #FFF5EF; border-radius: 12rpx; font-size: 30rpx; font-weight: 800; }
	.row { display: flex; min-height: 54rpx; align-items: center; color: #999; font-size: 22rpx; }
	.row text:last-child { flex: 1; margin-left: 25rpx; color: #555; text-align: right; word-break: break-all; }
	.foot { display: flex; margin-top: 20rpx; padding-top: 20rpx; justify-content: flex-end; align-items: center; border-top: 1rpx solid #EEE; }
	.btn { display: flex; height: 62rpx; padding: 0 28rpx; align-items: center; justify-content: center; border-radius: 32rpx; font-size: 24rpx; }
	.btn.ghost { color: #777; border: 1rpx solid #CCC; }
	.btn.primary { color: #FFF; background: #E15B00; }
	.reviewed { color: #999; font-size: 23rpx; }
	.empty { min-height: 560rpx; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #999; font-size: 26rpx; }
	.empty-btn { margin-top: 25rpx; padding: 15rpx 40rpx; color: #FFF; background: #E15B00; border-radius: 38rpx; }
	.more { padding: 14rpx; color: #AAA; text-align: center; font-size: 22rpx; }
</style>
