<template>
	<view class="coach-tabbar" v-if="identityReady">
		<view
			class="coach-tab"
			:class="{ active: item.key === active }"
			v-for="item in visibleTabs"
			:key="item.key"
			@click="navigate(item)"
		>
			<u-icon
				:name="item.key === active ? item.activeIcon : item.icon"
				:color="item.key === active ? '#FF5617' : '#A7A8AF'"
				size="24"
			></u-icon>
			<text class="coach-tab-label">{{ item.label }}</text>
		</view>
	</view>
</template>

<script>
	import { getPrivateCoachWorkbench } from '@/api/private-training.js'

	export default {
		props: {
			active: {
				type: String,
				default: 'home'
			},
			coachType: {
				type: [Number, String],
				default: null
			}
		},
		data() {
			return {
				identityCoachType: null,
				identityResolved: false,
				tabs: [
					{ key: 'home', label: '首页', icon: 'home', activeIcon: 'home-fill', url: '/pagesA/private_coach_workbench/private_coach_workbench' },
					{ key: 'schedule', label: '日程', icon: 'calendar', activeIcon: 'calendar-fill', url: '/pagesA/private_coach_schedule/private_coach_schedule' },
					{ key: 'members', label: '会员', icon: 'account', activeIcon: 'account-fill', url: '/pagesA/private_coach_members/private_coach_members' },
					{ key: 'gift', label: '赠课', icon: 'gift', activeIcon: 'gift-fill', url: '/pagesA/private_coach_gift/private_coach_gift' },
					{ key: 'mine', label: '我的', icon: 'setting', activeIcon: 'setting-fill', url: '/pagesA/private_coach_mine/private_coach_mine' }
				]
			}
		},
		computed: {
			identityReady() {
				return this.hasCoachType(this.coachType) || this.identityResolved;
			},
			visibleTabs() {
				const source = this.hasCoachType(this.coachType) ? this.coachType : this.identityCoachType;
				// 自由教练只有赠课；普通私教保留原导航并恢复会员入口。
				if (Number(source) === 2) {
					return this.tabs.filter(item => item.key === 'gift');
				}
				return this.tabs.filter(item => item.key !== 'gift');
			}
		},
		created() {
			if (this.hasCoachType(this.coachType)) {
				this.identityResolved = true;
				return;
			}
			this.loadCoachType();
		},
		methods: {
			hasCoachType(value) {
				return value !== null && value !== undefined && value !== '';
			},
			loadCoachType() {
				getPrivateCoachWorkbench().then(res => {
					const data = res.data || {};
					this.identityCoachType = data.coach ? data.coach.coachType : null;
				}).catch(() => {
					// 身份接口失败时保留原导航，避免影响其他类型教练。
					this.identityCoachType = null;
				}).then(() => {
					this.identityResolved = true;
				});
			},
			navigate(item) {
				if (!item || item.key === this.active) return;
				if (!item.url) {
					this.config.Toast(`${item.label}功能后续开放`);
					return;
				}
				uni.redirectTo({ url: item.url });
			}
		}
	}
</script>

<style lang="scss" scoped>
	.coach-tabbar { position: fixed; z-index: 99; right: 0; bottom: 0; left: 0; display: flex; height: 88rpx; padding: 6rpx 12rpx constant(safe-area-inset-bottom); padding: 6rpx 12rpx env(safe-area-inset-bottom); background: #FFF; border-top: 1rpx solid #EEEFF2; box-shadow: 0 -5rpx 16rpx rgba(30, 32, 38, .04); box-sizing: content-box; }
	.coach-tab { display: flex; flex: 1; flex-direction: column; align-items: center; justify-content: center; color: #A7A8AF; }
	.coach-tab.active { color: #FF5617; }
	.coach-tab-label { margin-top: 3rpx; font-size: 20rpx; line-height: 26rpx; }
</style>
