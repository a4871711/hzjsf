<template>
	<view class="tf-page">
		<!-- 权益 + 费用试算 -->
		<view class="tf-card">
			<view class="tf-card__name">{{ cardName || '权益卡' }}</view>
			<view class="tf-row">
				<text class="tf-label">本次转让</text>
				<text class="tf-val">第 {{ thisTransferNo }} 次</text>
			</view>
			<view class="tf-row">
				<text class="tf-label">应缴服务费</text>
				<text class="tf-fee">{{ feeText }}</text>
			</view>
			<view class="tf-tip">服务费由转让人支付；受让人确认接收即生效，继承剩余有效期。</view>
		</view>

		<!-- 受让人 -->
		<view class="tf-card">
			<view class="tf-row tf-row--input">
				<text class="tf-label">受让人会员ID</text>
				<input class="tf-input" type="number" v-model="toUserId" placeholder="请输入受让人的会员ID" />
			</view>
			<view class="tf-tip">受让人需已归属该权益适用门店，且账号正常。</view>
		</view>

		<view class="tf-btn" :class="{ 'is-disabled': submitting }" @click="onApply">
			{{ submitting ? '提交中...' : '发起转让' }}
		</view>
		<view class="tf-link" @click="goList">查看我的转让记录 ›</view>
	</view>
</template>

<script>
	import {
		quoteVipTransfer,
		applyVipTransfer
	} from '@/api/index'
	import {
		Wxpay
	} from '@/api/my'

	export default {
		data() {
			return {
				vipBenefitId: null,
				cardName: '',
				thisTransferNo: 1,
				serviceFee: 0,
				toUserId: '',
				submitting: false
			}
		},
		computed: {
			feeText() {
				const n = Number(this.serviceFee);
				return (isNaN(n) || n <= 0) ? '免费' : ('¥' + this.formatPrice(this.serviceFee));
			}
		},
		onLoad(options) {
			this.vipBenefitId = options.vipBenefitId || null;
			this.cardName = options.cardName ? decodeURIComponent(options.cardName) : '';
			if (this.vipBenefitId) {
				this.loadQuote();
			}
		},
		methods: {
			loadQuote() {
				quoteVipTransfer({
					vipBenefitId: this.vipBenefitId
				}).then((res) => {
					const d = res.data || {};
					this.thisTransferNo = d.thisTransferNo || 1;
					this.serviceFee = d.serviceFee || 0;
				}).catch((e) => {
					this.config.Toast((e && e.message) || '试算失败');
				});
			},
			onApply() {
				if (!this.vipBenefitId) {
					this.config.Toast('缺少权益参数');
					return;
				}
				const toUserId = String(this.toUserId).trim();
				if (!toUserId) {
					this.config.Toast('请输入受让人会员ID');
					return;
				}
				if (this.submitting) {
					return;
				}
				this.submitting = true;
				const that = this;
				applyVipTransfer({
					vipBenefitId: this.vipBenefitId,
					toUserId: toUserId
				}).then((res) => {
					const d = res.data || {};
					// 服务费=0(或后端未返回下单单号):直接进待审核
					if (!d.orderNo || Number(d.serviceFee) <= 0) {
						that.submitting = false;
						that.afterSubmit('已提交，等待门店审核');
						return;
					}
					// 服务费>0:复用小程序统一支付 /wx/proPay 拉起微信付服务费
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
									// 支付成功由 /wx/proPayNotify(后缀7) 置转让单待审核
									that.afterSubmit('服务费已支付，等待门店审核');
								},
								fail: () => {
									// 本步暂无「继续支付」入口:未支付的转让单30分钟后自动释放,可稍后重新发起
									that.config.Toast('支付已取消，转让未提交');
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
					// 前置校验/在途占用等业务码
					that.config.Toast((e && e.message) || '发起失败');
				});
			},
			afterSubmit(msg) {
				this.config.Toast(msg);
				setTimeout(() => this.goList(), 1200);
			},
			goList() {
				uni.redirectTo({
					url: '/pagesA/vip_transfer_list/vip_transfer_list'
				});
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.tf-page {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 20rpx 24rpx;
	}

	.tf-card {
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 28rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
	}

	.tf-card__name {
		font-size: 32rpx;
		font-weight: 600;
		color: #222;
		margin-bottom: 18rpx;
	}

	.tf-row {
		display: flex;
		justify-content: space-between;
		align-items: center;
		padding: 12rpx 0;
		font-size: 28rpx;
	}

	.tf-row--input {
		padding: 6rpx 0;
	}

	.tf-label {
		color: #999;
	}

	.tf-val {
		color: #333;
	}

	.tf-fee {
		color: #E8541E;
		font-size: 32rpx;
		font-weight: 600;
	}

	.tf-input {
		flex: 1;
		text-align: right;
		font-size: 28rpx;
		color: #333;
	}

	.tf-tip {
		margin-top: 12rpx;
		font-size: 22rpx;
		color: #BBB;
		line-height: 1.6;
	}

	.tf-btn {
		margin-top: 40rpx;
		height: 88rpx;
		line-height: 88rpx;
		text-align: center;
		background: #E8541E;
		color: #FFF;
		font-size: 30rpx;
		border-radius: 100rpx;
	}

	.tf-btn.is-disabled {
		opacity: 0.6;
	}

	.tf-link {
		margin-top: 28rpx;
		text-align: center;
		color: #C8923B;
		font-size: 26rpx;
	}
</style>
