<template>
	<view class="cp-page">
		<!-- 免费额度状态 -->
		<view class="cp-quota" :class="{ 'is-plain': !precheck.freeEntitled }" v-if="precheckLoaded">
			<view v-if="!precheck.freeEntitled" class="cp-quota__txt is-none">
				未开通免费停卡权益
			</view>
			<view v-else-if="precheck.freeAvailable" class="cp-quota__txt is-ok">
				免费停卡额度：可用（每次最长{{ precheck.maxFreeDays || 7 }}天）
			</view>
			<view v-else class="cp-quota__txt">
				本期免费额度已用，下次可免费停卡时间：{{ nextFreeDateText }}
			</view>
		</view>

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
					<view>
						<text class="cp-rec__type" :class="rec.pauseType === 1 ? 'is-paid' : 'is-free'">
							{{ rec.pauseType === 1 ? ('付费¥' + formatPrice(rec.amount)) : '免费' }}
						</text>
					</view>
					<text class="cp-rec__tag" :class="tagClass(rec.displayStatus)">{{ statusText(rec.displayStatus) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.pauseDays != null">
					<text class="cp-rec__label">停卡天数</text>
					<text class="cp-rec__val">{{ rec.pauseDays }} 天</text>
				</view>
				<view class="cp-rec__line">
					<text class="cp-rec__label">开始时间</text>
					<text class="cp-rec__val">{{ fmtDate(rec.startTime) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.endTime">
					<text class="cp-rec__label">计划结束时间</text>
					<text class="cp-rec__val">{{ fmtDate(rec.endTime) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.cancelTime">
					<text class="cp-rec__label">取消时间</text>
					<text class="cp-rec__val">{{ fmtDate(rec.cancelTime) }}</text>
				</view>
				<view class="cp-rec__line" v-if="rec.actualDays != null && (rec.displayStatus === 2 || rec.displayStatus === 99 || rec.displayStatus === 1)">
					<text class="cp-rec__label">实际停卡天数</text>
					<text class="cp-rec__val">{{ rec.actualDays }} 天</text>
				</view>
				<view v-if="rec.displayStatus === 0" class="cp-rec__btn" @click="onCancel(rec)">取消停卡</view>
			</view>
			<view v-else-if="loaded" class="cp-tip">还没有停卡记录</view>
		</view>

		<view class="cp-note">
			规则：权益会员每30天可免费停卡1次（每次最长7天）；免费额度用完可按档位付费停卡；停卡立即生效、到期自动恢复；停卡期间可手动取消，未使用天数从顺延的有效期中扣回，付费停卡不退款。
		</view>

		<!-- 申请停卡弹层 -->
		<view class="cp-mask" v-if="showApply" @click="closeApply"></view>
		<view class="cp-pop" v-if="showApply">
			<view class="cp-pop__title">申请停卡</view>
			<view class="cp-pop__card" v-if="applyCard">{{ cardTypeText(applyCard.type) }} · 有效期至 {{ fmtDate(applyCard.validityDate) }}</view>

			<!-- 方式一:免费停卡 -->
			<view class="cp-opt" :class="{ 'is-active': pauseMode === 'free', 'is-disabled': !applyPrecheck.freeAvailable }"
				@click="pickMode('free')">
				<view class="cp-opt__head">
					<text class="cp-opt__name">免费停卡</text>
					<text class="cp-opt__desc">{{ freeOptionDesc }}</text>
				</view>
				<view class="cp-days" v-if="pauseMode === 'free' && applyPrecheck.freeAvailable">
					<view class="cp-days__label">停卡天数</view>
					<view class="cp-days__list">
						<view class="cp-days__item" :class="{ 'is-on': freeDays === d }" v-for="d in freeDayOptions" :key="d"
							@click.stop="freeDays = d">{{ d }}天</view>
					</view>
				</view>
			</view>

			<!-- 方式二:付费停卡 -->
			<view class="cp-opt" v-if="applyPrecheck.tiers && applyPrecheck.tiers.length"
				:class="{ 'is-active': pauseMode === 'paid' }" @click="pickMode('paid')">
				<view class="cp-opt__head">
					<text class="cp-opt__name">付费停卡</text>
					<text class="cp-opt__desc">按档位付费，不退款</text>
				</view>
				<view class="cp-tiers" v-if="pauseMode === 'paid'">
					<view class="cp-tiers__item" :class="{ 'is-on': selectedTier === tier.index }"
						v-for="tier in applyPrecheck.tiers" :key="tier.index" @click.stop="selectedTier = tier.index">
						<text>{{ tier.days }}天</text>
						<text class="cp-tiers__price">¥{{ formatPrice(tier.price) }}</text>
					</view>
				</view>
			</view>
			<view class="cp-pop__none" v-else-if="noneTip">{{ noneTip }}</view>

			<view class="cp-pop__btn" :class="{ 'is-disabled': submitting || !canSubmit }" @click="onSubmit">
				{{ submitting ? '提交中...' : (pauseMode === 'paid' ? '确认并支付' : '确认停卡') }}
			</view>
		</view>
	</view>
</template>

<script>
	import {
		cardRecord,
		Wxpay
	} from '@/api/my'
	import {
		getCardPausePrecheck,
		applyCardPause,
		cancelCardPause,
		getCardPauseList
	} from '@/api/index'

	export default {
		data() {
			return {
				cards: [],
				records: [],
				loaded: false,
				submitting: false,
				// 页面级预检(免费额度横幅)
				// freeEntitled 默认 true 兜底:后端老版本无此字段时不影响原有三态逻辑
				precheck: {
					freeEntitled: true,
					freeAvailable: false,
					nextFreeDate: null,
					maxFreeDays: 7,
					tiers: []
				},
				precheckLoaded: false,
				// 申请弹层
				showApply: false,
				applyCard: null,
				applyPrecheck: {
					freeEntitled: true,
					freeAvailable: false,
					nextFreeDate: null,
					maxFreeDays: 7,
					tiers: []
				},
				pauseMode: '', // 'free' | 'paid'
				freeDays: 7,
				selectedTier: null
			}
		},
		computed: {
			nextFreeDateText() {
				const v = this.showApply ? this.applyPrecheck.nextFreeDate : this.precheck.nextFreeDate;
				if (!v) return '—';
				// 只取日期部分(yyyy-MM-dd HH:mm:ss → yyyy-MM-dd)
				return String(v).split(' ')[0];
			},
			freeDayOptions() {
				const max = Number(this.applyPrecheck.maxFreeDays) || 7;
				const arr = [];
				for (let i = 1; i <= max; i++) arr.push(i);
				return arr;
			},
			// 免费停卡选项的副标题:区分"未开通权益"/"额度可用"/"本期已用"三态
			freeOptionDesc() {
				if (!this.applyPrecheck.freeEntitled) return '未开通免费停卡权益';
				if (this.applyPrecheck.freeAvailable) return '每次最长' + (Number(this.applyPrecheck.maxFreeDays) || 7) + '天';
				return '本期额度已用' + (this.nextFreeDateText !== '—' ? '，' + this.nextFreeDateText + ' 后可用' : '');
			},
			// 无付费档位时的兜底提示:既无权益又无档位=不支持停卡;有权益但档位空=仅未开通付费
			noneTip() {
				if (this.applyPrecheck.tiers && this.applyPrecheck.tiers.length) return '';
				if (!this.applyPrecheck.freeEntitled) return '该卡暂不支持停卡';
				if (!this.applyPrecheck.freeAvailable) return '该卡未开通付费停卡';
				return '';
			},
			canSubmit() {
				if (this.pauseMode === 'free') {
					return this.applyPrecheck.freeAvailable && this.freeDays >= 1;
				}
				if (this.pauseMode === 'paid') {
					return this.selectedTier != null;
				}
				return false;
			}
		},
		onShow() {
			this.loadAll();
		},
		onPullDownRefresh() {
			this.loadAll(() => uni.stopPullDownRefresh());
		},
		methods: {
			loadAll(done) {
				this.loadCards();
				this.loadPrecheck();
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
			loadPrecheck() {
				getCardPausePrecheck({}).then((res) => {
					this.precheck = Object.assign({}, this.precheck, res.data || {});
					this.precheckLoaded = true;
				}).catch(() => {
					this.precheckLoaded = false;
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
			// 打开申请弹层:按卡再做一次预检(拿该卡的付费档位)
			onApply(card) {
				if (this.submitting) return;
				const that = this;
				getCardPausePrecheck({
					cardOrderId: card.cardOrderId
				}).then((res) => {
					const d = res.data || {};
					that.applyCard = card;
					that.applyPrecheck = Object.assign({
						freeEntitled: true,
						freeAvailable: false,
						nextFreeDate: null,
						maxFreeDays: 7,
						tiers: []
					}, d);
					that.pauseMode = that.applyPrecheck.freeAvailable ? 'free' :
						(that.applyPrecheck.tiers && that.applyPrecheck.tiers.length ? 'paid' : '');
					that.freeDays = Number(that.applyPrecheck.maxFreeDays) || 7;
					that.selectedTier = null;
					that.showApply = true;
				}).catch((e) => {
					that.config.Toast((e && e.message) || '加载停卡信息失败');
				});
			},
			closeApply() {
				if (this.submitting) return;
				this.showApply = false;
			},
			pickMode(mode) {
				if (mode === 'free') {
					if (!this.applyPrecheck.freeEntitled) {
						this.config.Toast('未开通免费停卡权益');
						return;
					}
					if (!this.applyPrecheck.freeAvailable) {
						this.config.Toast('本期免费额度已用' + (this.nextFreeDateText !== '—' ? '，下次可免费停卡时间：' + this.nextFreeDateText : ''));
						return;
					}
				}
				this.pauseMode = mode;
			},
			onSubmit() {
				if (this.submitting || !this.canSubmit) return;
				if (this.pauseMode === 'free') {
					this.submitFree();
				} else if (this.pauseMode === 'paid') {
					this.submitPaid();
				}
			},
			// 免费停卡
			submitFree() {
				const that = this;
				this.submitting = true;
				applyCardPause({
					cardOrderId: this.applyCard.cardOrderId,
					pauseType: 0,
					pauseDays: this.freeDays
				}).then(() => {
					that.submitting = false;
					that.showApply = false;
					that.config.Toast('停卡成功，已生效');
					that.loadRecords();
					that.loadPrecheck();
				}).catch((e) => {
					that.submitting = false;
					that.config.Toast((e && e.message) || '申请失败');
				});
			},
			// 付费停卡:下单后复用小程序统一支付拉起微信支付
			submitPaid() {
				const that = this;
				this.submitting = true;
				applyCardPause({
					cardOrderId: this.applyCard.cardOrderId,
					pauseType: 1,
					tierIndex: this.selectedTier
				}).then((res) => {
					const d = res.data || {};
					if (!d.needPay || !d.orderNo) {
						// 兜底:后端未要求支付则视为直接生效
						that.submitting = false;
						that.showApply = false;
						that.config.Toast('停卡成功，已生效');
						that.loadRecords();
						that.loadPrecheck();
						return;
					}
					Wxpay({
						orderNo: d.orderNo,
						paySum: d.paySum
					}).then((r) => {
						that.submitting = false;
						if (r.code == 1) {
							uni.requestPayment({
								appId: r.params.appId,
								nonceStr: r.params.nonceStr,
								package: r.params.package,
								paySign: r.params.paySign,
								signType: r.params.signType,
								timeStamp: r.params.timeStamp,
								success: () => {
									that.showApply = false;
									that.config.Toast('支付成功，停卡生效中');
									// 支付回调异步落库,延时刷新
									setTimeout(() => {
										that.loadRecords();
										that.loadPrecheck();
									}, 1500);
								},
								fail: () => {
									that.config.Toast('支付已取消，停卡未生效');
									that.loadRecords();
								}
							});
						} else {
							that.config.Toast(r.msg || '发起支付失败');
						}
					}).catch(() => {
						that.submitting = false;
						that.config.Toast('发起支付失败');
					});
				}).catch((e) => {
					that.submitting = false;
					that.config.Toast((e && e.message) || '申请失败');
				});
			},
			// 提前取消停卡
			onCancel(rec) {
				if (this.submitting) return;
				const that = this;
				uni.showModal({
					title: '取消停卡',
					content: '确认取消本次停卡？取消后未使用天数将从有效期中扣回，付费停卡费用不退还。',
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						cancelCardPause({
							pauseId: rec.pauseId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已取消停卡');
							that.loadRecords();
							that.loadPrecheck();
						}).catch((e) => {
							that.submitting = false;
							that.config.Toast((e && e.message) || '取消失败');
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
			statusText(displayStatus) {
				const map = {
					10: '待支付',
					0: '停卡中',
					99: '已结束',
					1: '已恢复',
					2: '已取消',
					3: '已关闭'
				};
				return map[displayStatus] || '';
			},
			tagClass(displayStatus) {
				if (displayStatus === 0) return 'is-on';
				if (displayStatus === 10) return 'is-wait';
				return 'is-gray';
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
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

	.cp-quota {
		background: #FBF0DC;
		border-radius: 16rpx;
		padding: 22rpx 28rpx;
		margin-bottom: 24rpx;
	}

	.cp-quota__txt {
		font-size: 26rpx;
		color: #A67A2A;
		line-height: 1.5;
	}

	.cp-quota__txt.is-ok {
		color: #C8923B;
		font-weight: 600;
	}

	/* 未开通免费停卡权益:中性灰底提示样式 */
	.cp-quota.is-plain {
		background: #F0F0F0;
	}

	.cp-quota__txt.is-none {
		color: #999;
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

	.cp-rec__type {
		font-size: 24rpx;
		padding: 4rpx 16rpx;
		border-radius: 8rpx;
	}

	.cp-rec__type.is-free {
		color: #2E9E5B;
		background: #E5F6EC;
	}

	.cp-rec__type.is-paid {
		color: #C8923B;
		background: #FBF0DC;
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

	.cp-rec__tag.is-wait {
		color: #E6544F;
		background: #FDEBEA;
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

	/* 申请停卡弹层 */
	.cp-mask {
		position: fixed;
		left: 0;
		top: 0;
		right: 0;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		z-index: 98;
	}

	.cp-pop {
		position: fixed;
		left: 0;
		right: 0;
		bottom: 0;
		background: #FFFFFF;
		border-radius: 24rpx 24rpx 0 0;
		padding: 32rpx 28rpx calc(32rpx + env(safe-area-inset-bottom));
		z-index: 99;
	}

	.cp-pop__title {
		text-align: center;
		font-size: 32rpx;
		font-weight: 600;
		color: #222;
		margin-bottom: 16rpx;
	}

	.cp-pop__card {
		text-align: center;
		font-size: 24rpx;
		color: #999;
		margin-bottom: 24rpx;
	}

	.cp-opt {
		border: 2rpx solid #EEE;
		border-radius: 16rpx;
		padding: 24rpx;
		margin-bottom: 20rpx;
	}

	.cp-opt.is-active {
		border-color: #C8923B;
		background: #FFFDF8;
	}

	.cp-opt.is-disabled {
		opacity: 0.55;
	}

	.cp-opt__head {
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.cp-opt__name {
		font-size: 28rpx;
		font-weight: 600;
		color: #222;
	}

	.cp-opt__desc {
		font-size: 22rpx;
		color: #999;
	}

	.cp-days {
		margin-top: 20rpx;
	}

	.cp-days__label {
		font-size: 24rpx;
		color: #999;
		margin-bottom: 12rpx;
	}

	.cp-days__list {
		display: flex;
		flex-wrap: wrap;
	}

	.cp-days__item {
		padding: 10rpx 24rpx;
		margin: 0 12rpx 12rpx 0;
		border: 1rpx solid #DDD;
		border-radius: 100rpx;
		font-size: 24rpx;
		color: #666;
	}

	.cp-days__item.is-on {
		border-color: #C8923B;
		color: #C8923B;
		background: #FBF0DC;
	}

	.cp-tiers {
		margin-top: 20rpx;
	}

	.cp-tiers__item {
		display: flex;
		justify-content: space-between;
		align-items: center;
		border: 1rpx solid #DDD;
		border-radius: 12rpx;
		padding: 20rpx 24rpx;
		margin-bottom: 12rpx;
		font-size: 26rpx;
		color: #333;
	}

	.cp-tiers__item.is-on {
		border-color: #C8923B;
		background: #FBF0DC;
	}

	.cp-tiers__price {
		color: #C8923B;
		font-weight: 600;
	}

	.cp-pop__none {
		text-align: center;
		font-size: 24rpx;
		color: #E6544F;
		padding: 8rpx 0 20rpx;
	}

	.cp-pop__btn {
		margin-top: 12rpx;
		text-align: center;
		padding: 22rpx 0;
		background: #C8923B;
		color: #FFF;
		font-size: 30rpx;
		border-radius: 100rpx;
	}

	.cp-pop__btn.is-disabled {
		opacity: 0.5;
	}
</style>
