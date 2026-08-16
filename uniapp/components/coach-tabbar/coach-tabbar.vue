<template>
	<view class="coach-tabbar">
		<view
			class="coach-tab"
			:class="{ active: item.key === active }"
			v-for="item in tabs"
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
	export default {
		props: {
			active: {
				type: String,
				default: 'home'
			}
		},
		data() {
			return {
				tabs: [
					{ key: 'home', label: '首页', icon: 'home', activeIcon: 'home-fill', url: '/pagesA/private_coach_workbench/private_coach_workbench' },
					{ key: 'schedule', label: '日程', icon: 'calendar', activeIcon: 'calendar-fill', url: '/pagesA/private_coach_schedule/private_coach_schedule' },
					{ key: 'members', label: '会员', icon: 'account', activeIcon: 'account-fill' },
					{ key: 'mine', label: '我的', icon: 'setting', activeIcon: 'setting-fill', url: '/pagesA/private_coach_mine/private_coach_mine' }
				]
			}
		},
		methods: {
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
