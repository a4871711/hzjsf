<template>
	<view class="gift-page">
		<view class="notice" v-if="unavailableMessage">{{ unavailableMessage }}</view>

		<view v-else>
			<view class="card">
				<view class="section-title">选择赠送权益</view>
				<picker :range="benefits" range-key="displayName" :value="benefitIndex" @change="onBenefitChange">
					<view class="picker-row">
						<text>来源权益</text>
						<text>{{ selectedBenefit ? selectedBenefit.displayName : '暂无可赠权益' }} ›</text>
					</view>
				</picker>
				<view class="hint" v-if="selectedBenefit">
					剩余 {{ selectedBenefit.remainingLessons }} 节 · {{ selectedBenefit.storeName || '适用门店' }} · 有效期 {{ expireText(selectedBenefit.expireAt) }}
				</view>
			</view>

			<view class="card">
				<view class="section-title">查找受赠会员</view>
				<view class="search-row">
					<input v-model.trim="keyword" placeholder="输入完整会员ID或手机号" @input="resetRequestNo" />
					<view class="search-btn" :class="{ disabled: lookingUp }" @click="lookupMember">{{ lookingUp ? '查询中' : '查询' }}</view>
				</view>
				<view class="member" v-if="member">
					<image :src="member.headImgUrl || '/static/image/my_img.png'" mode="aspectFill" />
					<view class="member-main">
						<view>{{ member.nickname || '未命名会员' }}</view>
						<text>ID {{ member.userId }} · {{ member.mobile || '--' }}</text>
					</view>
				</view>
			</view>

			<view class="card">
				<view class="section-title">赠送课时</view>
				<view class="lesson-row">
					<text>赠送数量</text>
					<input type="number" v-model="lessonCount" placeholder="请输入正整数" @input="resetRequestNo" />
					<text>节</text>
				</view>
				<view class="rule">赠送即时生效、不可撤销；受赠权益继承当前到期时间，且只能预约赠送教练。</view>
			</view>

			<view class="submit" :class="{ disabled: submitting || !canSubmit }" @click="confirmGift">
				{{ submitting ? '赠送中...' : '确认赠送' }}
			</view>

			<view class="history-card">
				<view class="section-title">赠送记录</view>
				<view class="history-item" v-for="item in history" :key="item.giftOrderNo">
					<view class="history-head"><text>{{ item.toMemberName || '未命名会员' }}</text><text>{{ item.lessonCount }} 节</text></view>
					<view>{{ item.toMemberMobile || '--' }} · {{ item.productName || '私教课程' }}</view>
					<view>{{ formatTime(item.createdAt) }} · 有效期 {{ expireText(item.expireAt) }}</view>
				</view>
				<view class="empty" v-if="loaded && !history.length">暂无赠送记录</view>
			</view>
		</view>

		<coach-tabbar active="gift" />
	</view>
</template>

<script>
	import CoachTabbar from '@/components/coach-tabbar/coach-tabbar.vue'
	import {
		getGiftablePrivateBenefits,
		lookupPrivateGiftMember,
		giftPrivateLessons,
		getPrivateGiftHistory
	} from '@/api/private-training.js'

	export default {
		components: { CoachTabbar },
		data() {
			return {
				benefits: [],
				benefitIndex: 0,
				keyword: '',
				member: null,
				lessonCount: '',
				lookingUp: false,
				submitting: false,
				pendingRequestNo: '',
				history: [],
				loaded: false,
				unavailableMessage: ''
			}
		},
		computed: {
			selectedBenefit() {
				return this.benefits[this.benefitIndex] || null;
			},
			canSubmit() {
				const n = Number(this.lessonCount);
				return !!this.selectedBenefit && !!this.member && Number.isInteger(n) && n > 0 && n <= Number(this.selectedBenefit.remainingLessons || 0);
			}
		},
		onShow() {
			this.loadPage();
		},
		onPullDownRefresh() {
			this.loadPage(() => uni.stopPullDownRefresh());
		},
		methods: {
			loadPage(done) {
				this.unavailableMessage = '';
				Promise.all([
					getGiftablePrivateBenefits(),
					getPrivateGiftHistory({ page: 1, limit: 50 })
				]).then((results) => {
					this.benefits = (results[0].data || []).map(item => Object.assign({}, item, {
						displayName: (item.productName || '私教课程') + '（剩余' + Number(item.remainingLessons || 0) + '节）'
					}));
					this.history = ((results[1].data || {}).list) || [];
					if (this.benefitIndex >= this.benefits.length) this.benefitIndex = 0;
				}).catch((e) => {
					this.benefits = [];
					this.history = [];
					this.unavailableMessage = (e && e.message) || '赠课功能暂不可用';
				}).then(() => {
					this.loaded = true;
					done && done();
				});
			},
			onBenefitChange(e) {
				this.benefitIndex = Number(e.detail.value || 0);
				this.lessonCount = '';
				this.resetRequestNo();
			},
			lookupMember() {
				if (this.lookingUp || !this.keyword) {
					if (!this.keyword) this.config.Toast('请输入完整会员ID或手机号');
					return;
				}
				this.lookingUp = true;
				this.member = null;
				this.resetRequestNo();
				lookupPrivateGiftMember({ keyword: this.keyword }).then(res => {
					this.member = res.data || null;
				}).catch(e => this.config.Toast((e && e.message) || '会员查询失败')).then(() => {
					this.lookingUp = false;
				});
			},
			confirmGift() {
				if (this.submitting || !this.canSubmit) {
					if (!this.canSubmit) this.config.Toast('请完整选择权益、会员和有效课时数');
					return;
				}
				const benefit = this.selectedBenefit;
				uni.showModal({
					title: '确认赠送',
					content: '赠送给 ' + (this.member.nickname || ('会员' + this.member.userId)) + ' 共 ' + this.lessonCount + ' 节课？\n赠送后不可撤销。',
					confirmText: '确认赠送',
					success: res => { if (res.confirm) this.submitGift(benefit); }
				});
			},
			submitGift(benefit) {
				if (!this.pendingRequestNo) {
					this.pendingRequestNo = 'GIFT_' + Date.now() + '_' + Math.random().toString(36).slice(2, 10);
				}
				this.submitting = true;
				giftPrivateLessons({
					sourceBenefitId: benefit.sourceBenefitId,
					toMemberId: this.member.userId,
					lessonCount: Number(this.lessonCount),
					requestNo: this.pendingRequestNo
				}).then(() => {
					this.config.Toast('赠送成功');
					this.lessonCount = '';
					this.member = null;
					this.keyword = '';
					this.pendingRequestNo = '';
					this.loadPage();
				}).catch(e => this.config.Toast((e && e.message) || '赠送失败')).then(() => {
					this.submitting = false;
				});
			},
			resetRequestNo() {
				if (!this.submitting) this.pendingRequestNo = '';
			},
			expireText(value) {
				if (!value) return '长期有效';
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(d.getTime())) return String(value);
				return this.padDate(d, false);
			},
			formatTime(value) {
				if (!value) return '--';
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(d.getTime())) return String(value);
				return this.padDate(d, true);
			},
			padDate(d, withTime) {
				const p = n => n < 10 ? '0' + n : '' + n;
				const date = d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate());
				return withTime ? date + ' ' + p(d.getHours()) + ':' + p(d.getMinutes()) : date;
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.gift-page { min-height: 100vh; padding: 24rpx 24rpx calc(130rpx + env(safe-area-inset-bottom)); background: #F4F4F6; box-sizing: border-box; }
	.notice, .card, .history-card { margin-bottom: 20rpx; padding: 28rpx; background: #FFF; border-radius: 20rpx; }
	.notice { margin-top: 120rpx; color: #E15B00; text-align: center; line-height: 44rpx; }
	.section-title { margin-bottom: 20rpx; padding-left: 14rpx; border-left: 6rpx solid #FF5617; color: #222; font-size: 29rpx; font-weight: 900; }
	.picker-row, .lesson-row { display: flex; min-height: 76rpx; align-items: center; color: #777; font-size: 25rpx; }
	.picker-row text:last-child { min-width: 0; margin-left: 22rpx; flex: 1; color: #222; text-align: right; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
	.hint, .rule { margin-top: 12rpx; color: #999; font-size: 21rpx; line-height: 36rpx; }
	.search-row { display: flex; align-items: center; }
	.search-row input { height: 76rpx; padding: 0 20rpx; flex: 1; background: #F6F6F7; border-radius: 12rpx; font-size: 24rpx; }
	.search-btn { display: flex; height: 76rpx; margin-left: 16rpx; padding: 0 28rpx; align-items: center; color: #FFF; background: #FF5617; border-radius: 12rpx; font-size: 24rpx; }
	.disabled { opacity: .5; }
	.member { display: flex; margin-top: 20rpx; padding: 18rpx; align-items: center; background: #FFF6F1; border-radius: 14rpx; }
	.member image { width: 76rpx; height: 76rpx; border-radius: 50%; }
	.member-main { margin-left: 18rpx; color: #222; font-size: 26rpx; }
	.member-main text { display: block; margin-top: 8rpx; color: #888; font-size: 21rpx; }
	.lesson-row input { width: 180rpx; height: 68rpx; margin-left: auto; padding: 0 15rpx; color: #222; background: #F6F6F7; border-radius: 10rpx; text-align: right; }
	.lesson-row text:last-child { margin-left: 12rpx; }
	.submit { display: flex; height: 88rpx; margin: 28rpx 0; align-items: center; justify-content: center; color: #FFF; background: linear-gradient(90deg, #FF8A3D, #E15B00); border-radius: 44rpx; font-size: 29rpx; font-weight: 900; }
	.history-head { display: flex; color: #222; font-size: 25rpx; font-weight: 800; }
	.history-head text:first-child { flex: 1; }
	.history-head text:last-child { color: #FF5617; }
	.history-item { padding: 20rpx 0; border-bottom: 1rpx solid #EEE; color: #888; font-size: 21rpx; line-height: 38rpx; }
	.history-item:last-child { border-bottom: 0; }
	.empty { padding: 45rpx 0; color: #999; text-align: center; }
</style>
