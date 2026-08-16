<template>
	<view class="workbench-page">
		<view class="coach-header" v-if="coach">
			<image class="avatar" :src="coach.avatarUrl || '/static/image/my_img.png'" mode="aspectFill" />
			<view class="coach-main">
				<view class="coach-title">
					<text class="coach-name">{{ coach.coachName || '教练' }}</text>
					<text class="coach-level" v-if="coach.coachLevel">{{ coach.coachLevel }}</text>
				</view>
				<view class="coach-meta">{{ coach.storeNames || '暂未配置服务门店' }}<text v-if="coach.coachNo"> · 工号 {{ coach.coachNo }}</text></view>
			</view>
			<view class="identity-switch" @click="switchToMember">⇄ 切换</view>
		</view>

		<view class="summary-card" v-if="isCoach">
			<view class="summary-item">
				<text class="summary-value">{{ stats.monthFinishedLessons }}</text>
				<text class="summary-label">本月已上课时</text>
			</view>
			<view class="summary-divider"></view>
			<view class="summary-item">
				<text class="summary-value">{{ stats.pendingLessons }}</text>
				<text class="summary-label">待上课时</text>
			</view>
			<view class="summary-divider"></view>
			<view class="summary-item">
				<text class="summary-value fee">¥{{ formatMoney(stats.expectedLessonFee) }}</text>
				<text class="summary-label">预计课时费</text>
			</view>
		</view>

		<view class="course-section" v-if="isCoach">
			<view class="section-head">
				<text>今日课程 · {{ todayLabel }}</text>
			</view>
			<view class="course-card" v-for="item in todayAppointments" :key="item.appointmentNo">
				<view class="course-time">
					<text class="start-time">{{ item.startTime || '--:--' }}</text>
					<text class="end-time">{{ item.endTime || '--:--' }}</text>
				</view>
				<view class="course-line"></view>
				<view class="course-main">
					<view class="course-name">
						{{ item.productName || '私教课程' }}
						<text v-if="item.packageLessonCount"> · {{ item.packageLessonCount }} 节课包</text>
					</view>
					<view class="member-line">
						<text class="private-tag">私教</text>
						<text>{{ item.memberName || '未命名会员' }}</text>
						<text v-if="item.memberMobile"> · {{ maskMobile(item.memberMobile) }}</text>
					</view>
					<view class="store-line" v-if="item.storeName">{{ item.storeName }}</view>
				</view>
				<text class="status" :class="item.statusClassName">{{ item.statusLabel }}</text>
			</view>
			<view class="empty" v-if="loaded && !todayAppointments.length">今天暂无私教课程</view>
		</view>

		<view class="empty identity-empty" v-if="loaded && !isCoach">
			<text>当前账号未绑定正常状态的私教</text>
			<text class="empty-tip">请联系管理员在后台“教练管理”中绑定会员账号</text>
		</view>

		<coach-tabbar v-if="isCoach" active="home" />

	</view>
</template>

<script>
	import CoachTabbar from '@/components/coach-tabbar/coach-tabbar.vue'
	import { getPrivateCoachWorkbench } from '@/api/private-training.js'

	export default {
		components: { CoachTabbar },
		data() {
			return {
				loaded: false,
				loading: false,
				isCoach: false,
				coach: null,
				stats: {
					monthFinishedLessons: 0,
					pendingLessons: 0,
					expectedLessonFee: 0
				},
				todayAppointments: []
			}
		},
		computed: {
			todayLabel() {
				const now = new Date();
				return `${now.getMonth() + 1}/${now.getDate()}`;
			}
		},
		onShow() {
			this.loadWorkbench();
		},
		onPullDownRefresh() {
			this.loadWorkbench(() => uni.stopPullDownRefresh());
		},
		methods: {
			switchToMember() {
				const pages = getCurrentPages();
				if (pages.length > 1) {
					uni.navigateBack();
					return;
				}
				uni.switchTab({ url: '/pages/my/my' });
			},
			loadWorkbench(done) {
				if (this.loading) {
					done && done();
					return;
				}
				this.loading = true;
				getPrivateCoachWorkbench().then((res) => {
					const data = res.data || {};
					this.isCoach = !!data.isCoach;
					this.coach = data.coach || null;
					this.stats = Object.assign({
						monthFinishedLessons: 0,
						pendingLessons: 0,
						expectedLessonFee: 0
					}, data.stats || {});
					this.todayAppointments = (data.todayAppointments || []).map(item => Object.assign({}, item, {
						statusLabel: this.statusText(item.appointmentStatus),
						statusClassName: this.statusClass(item.appointmentStatus)
					}));
				}).catch((e) => {
					this.isCoach = false;
					this.coach = null;
					this.todayAppointments = [];
					this.config.Toast((e && e.message) || '教练首页加载失败');
				}).then(() => {
					this.loaded = true;
					this.loading = false;
					done && done();
				});
			},
			statusText(status) {
				return ({ 1: '已预约', 2: '已取消', 3: '已完成', 4: '爽约' })[Number(status)] || '未知';
			},
			statusClass(status) {
				if (Number(status) === 1) return 'is-booked';
				if (Number(status) === 3) return 'is-done';
				if (Number(status) === 4) return 'is-warning';
				return 'is-gray';
			},
			formatMoney(value) {
				const amount = Number(value || 0);
				if (!Number.isFinite(amount)) return '0';
				return Number.isInteger(amount) ? String(amount) : amount.toFixed(2);
			},
			maskMobile(value) {
				const mobile = String(value || '');
				if (mobile.length < 7) return mobile;
				return `${mobile.slice(0, 3)}****${mobile.slice(-4)}`;
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.workbench-page { min-height: 100vh; padding-bottom: calc(120rpx + constant(safe-area-inset-bottom)); padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); background: #F4F4F6; box-sizing: border-box; }
	.coach-header { position: relative; display: flex; align-items: center; min-height: 155rpx; padding: 34rpx 34rpx 64rpx; color: #FFF; background: linear-gradient(120deg, #FF5617, #FF803D); }
	.avatar { width: 104rpx; height: 104rpx; flex: 0 0 auto; border: 4rpx solid rgba(255, 255, 255, .42); border-radius: 50%; }
	.coach-main { min-width: 0; margin-left: 22rpx; padding-right: 140rpx; }
	.coach-title { display: flex; align-items: center; min-width: 0; }
	.coach-name { overflow: hidden; max-width: 220rpx; font-size: 36rpx; font-weight: 900; text-overflow: ellipsis; white-space: nowrap; }
	.coach-level { margin-left: 14rpx; padding: 7rpx 13rpx; color: #FFF; background: rgba(255, 255, 255, .22); border-radius: 22rpx; font-size: 20rpx; }
	.coach-meta { overflow: hidden; margin-top: 12rpx; color: rgba(255, 255, 255, .88); font-size: 22rpx; text-overflow: ellipsis; white-space: nowrap; }
	.identity-switch { position: absolute; top: 55rpx; right: 28rpx; padding: 15rpx 20rpx; color: #FFF; background: rgba(255, 255, 255, .20); border: 1rpx solid rgba(255, 255, 255, .28); border-radius: 32rpx; font-size: 22rpx; font-weight: 800; }
	.summary-card { position: relative; z-index: 2; display: flex; align-items: center; margin: -36rpx 28rpx 0; padding: 34rpx 6rpx; background: #FFF; border-radius: 22rpx; box-shadow: 0 12rpx 30rpx rgba(24, 27, 35, .07); }
	.summary-item { display: flex; flex: 1; flex-direction: column; align-items: center; min-width: 0; }
	.summary-value { color: #1C1D21; font-size: 41rpx; font-weight: 900; line-height: 52rpx; }
	.summary-value.fee { color: #FF5617; font-size: 36rpx; }
	.summary-label { margin-top: 8rpx; color: #9A9BA4; font-size: 22rpx; white-space: nowrap; }
	.summary-divider { width: 1rpx; height: 62rpx; background: #EEEFF2; }
	.course-section { margin: 36rpx 28rpx 0; }
	.section-head { padding-bottom: 22rpx; color: #1C1D21; font-size: 32rpx; font-weight: 900; }
	.course-card { display: flex; align-items: center; min-height: 128rpx; margin-bottom: 18rpx; padding: 20rpx 24rpx; background: #FFF; border-radius: 22rpx; box-sizing: border-box; }
	.course-time { display: flex; width: 92rpx; flex: 0 0 auto; flex-direction: column; align-items: center; }
	.start-time { color: #1C1D21; font-size: 30rpx; font-weight: 900; }
	.end-time { margin-top: 6rpx; color: #A8A9B1; font-size: 22rpx; }
	.course-line { width: 7rpx; height: 76rpx; margin: 0 22rpx 0 8rpx; flex: 0 0 auto; background: #FF5617; border-radius: 8rpx; }
	.course-main { min-width: 0; flex: 1; }
	.course-name { overflow: hidden; color: #222328; font-size: 27rpx; font-weight: 900; text-overflow: ellipsis; white-space: nowrap; }
	.member-line { display: flex; align-items: center; overflow: hidden; margin-top: 11rpx; color: #85868E; font-size: 22rpx; white-space: nowrap; }
	.private-tag { margin-right: 10rpx; padding: 4rpx 9rpx; color: #FF5617; background: #FFF0E8; border-radius: 9rpx; font-size: 19rpx; }
	.store-line { overflow: hidden; margin-top: 8rpx; color: #A8A9B1; font-size: 20rpx; text-overflow: ellipsis; white-space: nowrap; }
	.status { margin-left: 16rpx; padding: 9rpx 14rpx; flex: 0 0 auto; color: #909399; background: #F4F4F5; border-radius: 18rpx; font-size: 20rpx; }
	.status.is-booked { color: #E15B00; background: #FFF0E6; }
	.status.is-done { color: #159658; background: #E9F8F0; }
	.status.is-warning { color: #C77B00; background: #FFF5DD; }
	.empty { padding: 70rpx 20rpx; color: #999; text-align: center; font-size: 25rpx; }
	.identity-empty { display: flex; flex-direction: column; margin: 24rpx; background: #FFF; border-radius: 20rpx; }
	.empty-tip { margin-top: 16rpx; color: #BBB; font-size: 21rpx; line-height: 34rpx; }
</style>
