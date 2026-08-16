<template>
	<view class="mine-page">
		<view class="profile-head" v-if="coach">
			<image class="avatar" :src="coach.avatarUrl || '/static/image/my_img.png'" mode="aspectFill" />
			<view class="profile-main">
				<view class="name-line">
					<text class="coach-name">{{ coach.coachName || '教练' }}</text>
					<text class="level" v-if="coach.coachLevel">{{ coach.coachLevel }}</text>
				</view>
				<view class="coach-no">{{ coach.coachNo || '-' }}</view>
				<view class="stores">{{ coach.storeNames || '暂未配置服务门店' }}</view>
			</view>
		</view>

		<view class="income-card">
			<view class="income-item">
				<text class="income-value orange">¥{{ money(incomeSummary.monthIncome) }}</text>
				<text class="income-label">本月收入</text>
			</view>
			<view class="divider"></view>
			<view class="income-item">
				<text class="income-value">¥{{ money(incomeSummary.totalIncome) }}</text>
				<text class="income-label">累计收入</text>
			</view>
			<view class="divider"></view>
			<view class="income-item">
				<text class="income-value">{{ workbenchStats.monthFinishedLessons || 0 }}</text>
				<text class="income-label">本月课时</text>
			</view>
		</view>

		<view class="menu-card">
			<view class="menu-item" @click="go('/pagesA/private_coach_profile/private_coach_profile')">
				<view class="menu-icon"><u-icon name="account" color="#FF5617" size="31"></u-icon></view>
				<view class="menu-main">
					<text class="menu-title">个人资料</text>
					<text class="menu-tip">头像、姓名、手机号和简介</text>
				</view>
				<text class="arrow">›</text>
			</view>
			<view class="menu-item" @click="go('/pagesA/private_coach_income/private_coach_income')">
				<view class="menu-icon"><u-icon name="rmb-circle" color="#FF5617" size="31"></u-icon></view>
				<view class="menu-main">
					<text class="menu-title">收入明细</text>
					<text class="menu-tip">查看授课提成与销售提成</text>
				</view>
				<text class="arrow">›</text>
			</view>
			<view class="menu-item last" @click="go('/pagesA/setup_center/setup_center')">
				<view class="menu-icon"><u-icon name="setting" color="#FF5617" size="31"></u-icon></view>
				<view class="menu-main">
					<text class="menu-title">设置</text>
					<text class="menu-tip">账号设置与退出登录</text>
				</view>
				<text class="arrow">›</text>
			</view>
		</view>

		<view class="switch-member" @click="switchToMember">⇄ 切换回用户端</view>
		<view class="empty" v-if="loaded && !coach">当前账号未绑定正常状态的私教</view>

		<coach-tabbar active="mine" />
	</view>
</template>

<script>
	import CoachTabbar from '@/components/coach-tabbar/coach-tabbar.vue'
	import { getPrivateCoachMine } from '@/api/private-training.js'

	export default {
		components: { CoachTabbar },
		data() {
			return {
				loaded: false,
				loading: false,
				coach: null,
				incomeSummary: {},
				workbenchStats: {}
			}
		},
		onShow() {
			this.loadMine();
		},
		onPullDownRefresh() {
			this.loadMine(() => uni.stopPullDownRefresh());
		},
		methods: {
			loadMine(done) {
				if (this.loading) {
					done && done();
					return;
				}
				this.loading = true;
				getPrivateCoachMine().then(res => {
					const data = res.data || {};
					this.coach = data.coach || null;
					this.incomeSummary = data.incomeSummary || {};
					this.workbenchStats = data.workbenchStats || {};
				}).catch(e => {
					this.coach = null;
					this.config.Toast((e && e.message) || '教练资料加载失败');
				}).then(() => {
					this.loaded = true;
					this.loading = false;
					done && done();
				});
			},
			go(url) {
				uni.navigateTo({ url });
			},
			switchToMember() {
				uni.switchTab({ url: '/pages/my/my' });
			},
			money(value) {
				const amount = Number(value || 0);
				return Number.isInteger(amount) ? String(amount) : amount.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.mine-page { min-height: 100vh; padding-bottom: calc(130rpx + constant(safe-area-inset-bottom)); padding-bottom: calc(130rpx + env(safe-area-inset-bottom)); background: #F4F4F6; box-sizing: border-box; }
	.profile-head { display: flex; align-items: center; min-height: 170rpx; padding: 34rpx 34rpx 72rpx; color: #FFF; background: linear-gradient(120deg, #FF5617, #FF803D); }
	.avatar { width: 112rpx; height: 112rpx; flex: 0 0 auto; border: 4rpx solid rgba(255, 255, 255, .42); border-radius: 50%; }
	.profile-main { min-width: 0; margin-left: 22rpx; }
	.name-line { display: flex; align-items: center; }
	.coach-name { overflow: hidden; max-width: 300rpx; font-size: 37rpx; font-weight: 900; text-overflow: ellipsis; white-space: nowrap; }
	.level { margin-left: 12rpx; padding: 6rpx 13rpx; background: rgba(255, 255, 255, .22); border-radius: 20rpx; font-size: 19rpx; }
	.coach-no { margin-top: 8rpx; color: rgba(255, 255, 255, .82); font-size: 21rpx; }
	.stores { overflow: hidden; max-width: 500rpx; margin-top: 7rpx; color: rgba(255, 255, 255, .82); font-size: 21rpx; text-overflow: ellipsis; white-space: nowrap; }
	.income-card { position: relative; z-index: 2; display: flex; align-items: center; margin: -42rpx 28rpx 0; padding: 32rpx 4rpx; background: #FFF; border-radius: 22rpx; box-shadow: 0 12rpx 30rpx rgba(24, 27, 35, .07); }
	.income-item { display: flex; min-width: 0; flex: 1; flex-direction: column; align-items: center; }
	.income-value { color: #202126; font-size: 32rpx; font-weight: 900; }
	.income-value.orange { color: #FF5617; }
	.income-label { margin-top: 8rpx; color: #9A9BA4; font-size: 20rpx; }
	.divider { width: 1rpx; height: 58rpx; background: #EEEFF2; }
	.menu-card { margin: 30rpx 28rpx 0; padding: 0 24rpx; background: #FFF; border-radius: 22rpx; }
	.menu-item { display: flex; align-items: center; min-height: 124rpx; border-bottom: 1rpx solid #EFF0F2; }
	.menu-item.last { border-bottom: 0; }
	.menu-icon { display: flex; width: 66rpx; height: 66rpx; flex: 0 0 auto; align-items: center; justify-content: center; background: #FFF1E9; border-radius: 18rpx; }
	.menu-main { display: flex; min-width: 0; margin-left: 20rpx; flex: 1; flex-direction: column; }
	.menu-title { color: #232429; font-size: 27rpx; font-weight: 900; }
	.menu-tip { margin-top: 7rpx; color: #A1A2A9; font-size: 20rpx; }
	.arrow { color: #B2B3B9; font-size: 40rpx; }
	.switch-member { display: flex; height: 78rpx; margin: 30rpx 28rpx 0; align-items: center; justify-content: center; color: #FF5617; background: #FFF; border: 2rpx solid #FF5617; border-radius: 40rpx; font-size: 24rpx; font-weight: 800; }
	.empty { padding: 90rpx 30rpx; color: #999; text-align: center; font-size: 24rpx; }
</style>
