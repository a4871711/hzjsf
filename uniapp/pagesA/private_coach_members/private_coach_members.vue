<template>
	<view class="members-page">
		<view class="search-box">
			<input v-model.trim="keyword" placeholder="搜索会员姓名、手机号或会员ID" confirm-type="search" @confirm="search" />
			<view class="search-button" @click="search">搜索</view>
		</view>

		<view class="summary" v-if="loaded">
			<text>服务会员</text>
			<text>{{ total }} 人</text>
		</view>

		<view class="member-card" v-for="item in members" :key="item.memberId">
			<view class="member-head">
				<image :src="item.headImgUrl || '/static/image/my_img.png'" mode="aspectFill" />
				<view class="member-main">
					<view class="name-line">
						<text class="member-name">{{ item.memberName || '未命名会员' }}</text>
						<text class="active-tag" :class="{ empty: Number(item.activeBenefitCount || 0) === 0 }">
							{{ Number(item.activeBenefitCount || 0) > 0 ? '服务中' : '暂无有效权益' }}
						</text>
					</view>
					<view class="member-meta">ID {{ item.memberId }}<text v-if="item.memberMobile"> · {{ item.memberMobile }}</text></view>
				</view>
			</view>

			<view class="benefit-summary">
				<view>
					<text class="value">{{ Number(item.activeBenefitCount || 0) }}</text>
					<text class="label">有效权益</text>
				</view>
				<view>
					<text class="value orange">{{ Number(item.remainingLessons || 0) }}</text>
					<text class="label">剩余课时</text>
				</view>
				<view>
					<text class="value">{{ Number(item.frozenLessons || 0) }}</text>
					<text class="label">已约课时</text>
				</view>
			</view>

			<view class="benefit-section">
				<view class="benefit-title">课程权益（{{ Number(item.benefitCount || 0) }}）</view>
				<view class="benefit-item" v-for="benefit in item.benefits" :key="benefit.benefitId">
					<view class="benefit-head">
						<text class="product-name">{{ benefit.productName || '私教课程' }}</text>
						<text class="benefit-status" :class="'status-' + benefitStatusClass(benefit)">
							{{ benefitStatusText(benefit) }}
						</text>
					</view>
					<view class="benefit-meta">{{ benefit.storeName || '未配置门店' }} · {{ expireText(benefit.expireAt) }}</view>
					<view class="benefit-metrics">
						<view><text>{{ Number(benefit.totalLessons || 0) }}</text><text>总课时</text></view>
						<view><text class="orange">{{ Number(benefit.remainingLessons || 0) }}</text><text>剩余</text></view>
						<view><text>{{ Number(benefit.frozenLessons || 0) }}</text><text>已预约</text></view>
						<view><text>{{ Number(benefit.usedLessons || 0) }}</text><text>已使用</text></view>
					</view>
					<view class="benefit-no">权益编号 {{ benefit.benefitNo || '-' }}</view>
				</view>
			</view>
		</view>

		<view class="empty" v-if="loaded && !members.length">
			<view class="empty-title">暂无服务会员</view>
			<view class="empty-tip">会员权益中的服务教练设置为你后，会显示在这里</view>
		</view>
		<view class="loading-tip" v-if="loading && members.length">加载中...</view>
		<view class="loading-tip" v-else-if="loaded && members.length && !hasMore">没有更多会员了</view>

		<coach-tabbar active="members" :coach-type="1" />
	</view>
</template>

<script>
	import CoachTabbar from '@/components/coach-tabbar/coach-tabbar.vue'
	import { getPrivateCoachMembers } from '@/api/private-training.js'

	export default {
		components: { CoachTabbar },
		data() {
			return {
				keyword: '',
				members: [],
				total: 0,
				page: 1,
				limit: 15,
				hasMore: true,
				loading: false,
				loaded: false
			}
		},
		onShow() {
			this.loadMembers(true);
		},
		onPullDownRefresh() {
			this.loadMembers(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (this.hasMore) this.loadMembers(false);
		},
		methods: {
			search() {
				this.loadMembers(true);
			},
			loadMembers(reset, done) {
				if (this.loading) {
					done && done();
					return;
				}
				if (reset) {
					this.page = 1;
					this.hasMore = true;
				}
				this.loading = true;
				getPrivateCoachMembers({
					page: this.page,
					limit: this.limit,
					keyword: this.keyword
				}).then(res => {
					const data = res.data || {};
					const list = data.list || [];
					this.members = reset ? list : this.members.concat(list);
					this.total = Number(data.total || 0);
					this.hasMore = !!data.hasMore;
					if (this.hasMore) this.page += 1;
				}).catch(e => {
					if (reset) {
						this.members = [];
						this.total = 0;
					}
					this.config.Toast((e && e.message) || '会员列表加载失败');
				}).then(() => {
					this.loaded = true;
					this.loading = false;
					done && done();
				});
			},
			benefitStatusText(item) {
				if (Number(item.status) === 1 && this.isExpired(item.expireAt)) return '已过期';
				return ({ 1: '生效中', 2: '已用完', 3: '已过期', 4: '已退款' })[Number(item.status)] || '未知状态';
			},
			benefitStatusClass(item) {
				if (Number(item.status) === 1 && !this.isExpired(item.expireAt)) return 'active';
				if (Number(item.status) === 4) return 'refunded';
				return 'inactive';
			},
			isExpired(value) {
				if (!value) return false;
				const date = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				return !isNaN(date.getTime()) && date.getTime() <= Date.now();
			},
			expireText(value) {
				if (!value) return '长期有效';
				const date = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(date.getTime())) return '有效期 ' + value;
				const pad = n => n < 10 ? '0' + n : '' + n;
				return '有效期至 ' + date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate());
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.members-page { min-height: 100vh; padding: 24rpx 24rpx calc(130rpx + env(safe-area-inset-bottom)); background: #F4F4F6; box-sizing: border-box; }
	.search-box { display: flex; align-items: center; padding: 18rpx 20rpx; background: #FFF; border-radius: 20rpx; }
	.search-box input { height: 66rpx; padding: 0 20rpx; flex: 1; background: #F5F5F7; border-radius: 33rpx; font-size: 23rpx; }
	.search-button { display: flex; height: 66rpx; margin-left: 14rpx; padding: 0 27rpx; align-items: center; color: #FFF; background: #FF5617; border-radius: 33rpx; font-size: 23rpx; font-weight: 800; }
	.summary { display: flex; justify-content: space-between; padding: 28rpx 6rpx 18rpx; color: #24252A; font-size: 27rpx; font-weight: 900; }
	.summary text:last-child { color: #FF5617; font-size: 23rpx; }
	.member-card { margin-bottom: 20rpx; padding: 26rpx; background: #FFF; border-radius: 22rpx; box-shadow: 0 8rpx 24rpx rgba(28, 30, 36, .04); }
	.member-head { display: flex; align-items: center; }
	.member-head image { width: 86rpx; height: 86rpx; flex: 0 0 auto; border-radius: 50%; }
	.member-main { min-width: 0; margin-left: 20rpx; flex: 1; }
	.name-line { display: flex; align-items: center; min-width: 0; }
	.member-name { overflow: hidden; max-width: 270rpx; color: #222328; font-size: 29rpx; font-weight: 900; text-overflow: ellipsis; white-space: nowrap; }
	.active-tag { margin-left: 14rpx; padding: 5rpx 11rpx; color: #168F56; background: #E8F7EF; border-radius: 15rpx; font-size: 18rpx; }
	.active-tag.empty { color: #909199; background: #F0F1F3; }
	.member-meta { margin-top: 9rpx; color: #92939A; font-size: 21rpx; }
	.benefit-summary { display: flex; margin-top: 24rpx; padding: 20rpx 0; background: #F8F8F9; border-radius: 16rpx; }
	.benefit-summary view { display: flex; min-width: 0; flex: 1; flex-direction: column; align-items: center; }
	.value { color: #26272C; font-size: 30rpx; font-weight: 900; }
	.value.orange { color: #FF5617; }
	.label { margin-top: 6rpx; color: #9A9BA3; font-size: 19rpx; }
	.benefit-section { margin-top: 22rpx; padding-top: 18rpx; border-top: 1rpx solid #EFF0F2; }
	.benefit-title { color: #777880; font-size: 22rpx; font-weight: 800; }
	.benefit-item { margin-top: 16rpx; padding: 20rpx; background: #F8F8F9; border-radius: 16rpx; }
	.benefit-head { display: flex; align-items: center; }
	.product-name { overflow: hidden; min-width: 0; flex: 1; color: #27282D; font-size: 24rpx; font-weight: 900; text-overflow: ellipsis; white-space: nowrap; }
	.benefit-status { margin-left: 14rpx; padding: 5rpx 11rpx; border-radius: 14rpx; font-size: 18rpx; }
	.status-active { color: #168F56; background: #E8F7EF; }
	.status-inactive { color: #888A91; background: #EBECEF; }
	.status-refunded { color: #D85B52; background: #FCECEA; }
	.benefit-meta { margin-top: 9rpx; color: #8F9097; font-size: 20rpx; }
	.benefit-metrics { display: flex; margin-top: 17rpx; padding: 15rpx 0; background: #FFF; border-radius: 12rpx; }
	.benefit-metrics view { display: flex; min-width: 0; flex: 1; flex-direction: column; align-items: center; }
	.benefit-metrics text:first-child { color: #292A2F; font-size: 24rpx; font-weight: 900; }
	.benefit-metrics text:first-child.orange { color: #FF5617; }
	.benefit-metrics text:last-child { margin-top: 4rpx; color: #A1A2A8; font-size: 17rpx; }
	.benefit-no { margin-top: 12rpx; color: #B0B1B7; font-size: 17rpx; }
	.empty { margin-top: 120rpx; color: #999; text-align: center; }
	.empty-title { color: #777980; font-size: 28rpx; font-weight: 800; }
	.empty-tip { margin-top: 14rpx; font-size: 21rpx; line-height: 34rpx; }
	.loading-tip { padding: 25rpx 0; color: #A2A3AA; text-align: center; font-size: 21rpx; }
</style>
