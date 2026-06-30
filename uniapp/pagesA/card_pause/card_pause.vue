<template>
	<view class="cp-page">
		<!-- 可停的卡 -->
		<view class="cp-sec">
			<view class="cp-sec__title">我的会员卡</view>
			<view v-if="cards.length" class="cp-card" v-for="(card, idx) in cards" :key="card.cardOrderId || idx">
				<view class="cp-card__info">
					<text class="cp-card__name">{{ cardTypeText(card.type) }}</text>
					<text class="cp-card__sub">有效期至 {{ fmtDate(card.validityDate) }}</text>
				</view>
				<view class="cp-card__btn" @click="onApply(card)">申请停卡</view>
			</view>
			<view v-else-if="loaded" class="cp-tip">暂无生效中的会员卡</view>
		</view>

		<!-- 停卡记录 -->
		<view class="cp-sec">
			<view class="cp-sec__title">停卡记录</view>
			<view v-if="records.length" class="cp-rec" v-for="(rec, idx) in records" :key="rec.pauseId || idx">
				<view class="cp-rec__row">
					<text class="cp-rec__month">{{ rec.pauseMonth || '' }}</text>
					<text class="cp-rec__tag" :class="rec.status === 0 ? 'is-on' : 'is-gray'">{{ statusText(rec.status) }}</text>
				</view>
				<view class="cp-rec__line">
					<text class="cp-rec__label">停卡时间</text>
					<text class="cp-rec__val">{{ fmtDate(rec.startTime) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.endTime">
					<text class="cp-rec__label">恢复时间</text>
					<text class="cp-rec__val">{{ fmtDate(rec.endTime) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.pauseDays != null">
					<text class="cp-rec__label">停卡天数</text>
					<text class="cp-rec__val">{{ rec.pauseDays }} 天(已顺延有效期)</text>
				</view>
				<view v-if="rec.status === 0" class="cp-rec__btn" @click="onResume(rec)">恢复停卡</view>
			</view>
			<view v-else-if="loaded" class="cp-tip">还没有停卡记录</view>
		</view>

		<view class="cp-note">规则:每张卡每自然月可停 1 次、全年最多 12 次;停卡期间不可入场,恢复时按实际停卡天数顺延有效期。</view>
	</view>
</template>

<script>
	import {
		cardRecord
	} from '@/api/my'
	import {
		applyCardPause,
		resumeCardPause,
		getCardPauseList
	} from '@/api/index'

	export default {
		data() {
			return {
				cards: [],
				records: [],
				loaded: false,
				submitting: false
			}
		},
		onLoad() {
			this.loadAll();
		},
		onPullDownRefresh() {
			this.loadAll(() => uni.stopPullDownRefresh());
		},
		methods: {
			loadAll(done) {
				this.loadCards();
				this.loadRecords(done);
			},
			loadCards() {
				cardRecord({
					page: 1,
					limit: 50
				}).then((res) => {
					this.cards = (res.data && res.data.list) || [];
				}).catch(() => {
					this.cards = [];
				});
			},
			loadRecords(done) {
				getCardPauseList({
					page: 1,
					limit: 50
				}).then((res) => {
					this.records = (res.data && res.data.list) || [];
					this.loaded = true;
					done && done();
				}).catch((e) => {
					this.loaded = true;
					this.config.Toast((e && e.message) || '加载失败');
					done && done();
				});
			},
			onApply(card) {
				if (this.submitting) return;
				const that = this;
				uni.showModal({
					title: '申请停卡',
					content: '确认对该卡申请停卡?停卡期间不可入场,恢复时按实际停卡天数顺延有效期。',
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						applyCardPause({
							cardOrderId: card.cardOrderId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已申请停卡');
							that.loadRecords();
						}).catch((e) => {
							that.submitting = false;
							that.config.Toast((e && e.message) || '申请失败');
						});
					}
				});
			},
			onResume(rec) {
				if (this.submitting) return;
				const that = this;
				uni.showModal({
					title: '恢复停卡',
					content: '确认恢复该卡?将按实际停卡天数顺延会员卡有效期。',
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						resumeCardPause({
							pauseId: rec.pauseId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已恢复');
							that.loadRecords();
						}).catch((e) => {
							that.submitting = false;
							that.config.Toast((e && e.message) || '恢复失败');
						});
					}
				});
			},
			cardTypeText(type) {
				const map = {
					0: '月卡',
					1: '季卡',
					2: '半年卡',
					3: '年卡',
					10: '次卡'
				};
				return map[type] || '会员卡';
			},
			statusText(status) {
				if (status === 0) return '停卡中';
				if (status === 1) return '已恢复';
				if (status === 2) return '已取消';
				return '';
			},
			fmtDate(v) {
				if (v == null || v === '') return '—';
				// 时间戳(数字/纯数字串)统一格式化;已是日期串则原样
				if (typeof v === 'number' || /^\d+$/.test(String(v))) {
					return this.config.timestampToDateTime(Number(v));
				}
				return String(v);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.cp-page {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 20rpx 24rpx 60rpx;
	}

	.cp-sec {
		margin-bottom: 28rpx;
	}

	.cp-sec__title {
		font-size: 28rpx;
		font-weight: 600;
		color: #333;
		margin: 12rpx 4rpx 16rpx;
	}

	.cp-card {
		display: flex;
		align-items: center;
		justify-content: space-between;
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 28rpx;
		margin-bottom: 18rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
	}

	.cp-card__name {
		display: block;
		font-size: 30rpx;
		font-weight: 600;
		color: #222;
		margin-bottom: 8rpx;
	}

	.cp-card__sub {
		font-size: 24rpx;
		color: #999;
	}

	.cp-card__btn {
		padding: 14rpx 32rpx;
		background: #C8923B;
		color: #FFF;
		font-size: 26rpx;
		border-radius: 100rpx;
	}

	.cp-rec {
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 24rpx 28rpx 16rpx;
		margin-bottom: 18rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
	}

	.cp-rec__row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 12rpx;
	}

	.cp-rec__month {
		font-size: 28rpx;
		font-weight: 600;
		color: #222;
	}

	.cp-rec__tag {
		font-size: 22rpx;
		padding: 4rpx 16rpx;
		border-radius: 100rpx;
	}

	.cp-rec__tag.is-on {
		color: #C8923B;
		background: #FBF0DC;
	}

	.cp-rec__tag.is-gray {
		color: #999;
		background: #EEE;
	}

	.cp-rec__line {
		display: flex;
		justify-content: space-between;
		padding: 8rpx 0;
		font-size: 26rpx;
	}

	.cp-rec__label {
		color: #999;
	}

	.cp-rec__val {
		color: #333;
	}

	.cp-rec__btn {
		margin-top: 12rpx;
		text-align: center;
		padding: 16rpx 0;
		border: 1rpx solid #C8923B;
		color: #C8923B;
		font-size: 26rpx;
		border-radius: 100rpx;
	}

	.cp-tip {
		text-align: center;
		color: #BBB;
		font-size: 26rpx;
		padding: 40rpx 0;
		background: #FFFFFF;
		border-radius: 16rpx;
	}

	.cp-note {
		color: #BBB;
		font-size: 22rpx;
		line-height: 1.6;
		padding: 20rpx 8rpx;
	}
</style>
