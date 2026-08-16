<template>
	<view class="confirm-page" v-if="loaded">
		<view class="product-card">
			<image class="product-cover" :src="product.coverUrl || fallbackCover" mode="aspectFill"></image>
			<view class="product-main">
				<view class="product-head">
					<text class="product-name">{{ product.productName || '私教课程' }}</text>
					<text class="product-price">¥{{ priceText(quote.originalAmount || product.salePrice) }}</text>
				</view>
				<view class="tag-row">
					<text class="tag primary">私教课程</text>
					<text class="tag">{{ serviceTypeText(product.serviceType) }}</text>
				</view>
				<view class="product-meta">{{ product.lessonCount || 0 }} 课时 · 单次 {{ product.durationMinutes || 0 }} 分钟 · 有效期 {{ validityText(product.validityDays) }}</view>
			</view>
		</view>

		<view class="section-title">选择教练 <text>* 必选</text></view>
		<view class="coach-panel">
			<scroll-view v-if="coaches.length" class="coach-scroll" scroll-x :show-scrollbar="false">
				<view class="coach-list">
					<view class="coach-item" v-for="coach in coaches" :key="coach.id" @click="selectCoach(coach)">
						<view class="avatar-wrap" :class="{ selected: String(selectedCoachId) === String(coach.id) }">
							<image v-if="coach.avatarUrl" class="coach-avatar" :src="coach.avatarUrl" mode="aspectFill"></image>
							<text v-else>{{ coachInitial(coach.coachName) }}</text>
							<view class="selected-mark" v-if="String(selectedCoachId) === String(coach.id)">✓</view>
						</view>
						<view class="coach-name" :class="{ active: String(selectedCoachId) === String(coach.id) }">{{ coach.coachName || '教练' }}</view>
						<view class="coach-level">{{ coach.coachLevel || '专业教练' }}</view>
					</view>
				</view>
			</scroll-view>
			<view class="empty-coach" v-else>{{ coachesLoaded ? '当前门店暂无可预约教练' : '教练加载中...' }}</view>
		</view>

		<view class="info-card">
			<picker :range="stores" range-key="storeName" :value="storeIndex" @change="onStoreChange">
				<view class="info-row clickable">
					<text class="row-label">购买门店</text>
					<text class="row-value strong">{{ selectedStore ? selectedStore.storeName : '请选择' }} ›</text>
				</view>
			</picker>
			<view class="info-row no-border">
				<text class="row-label">适用门店</text>
				<text class="row-value muted">{{ product.storeNames || (selectedStore && selectedStore.storeName) || '--' }}</text>
			</view>
		</view>

		<view class="price-card">
			<picker v-if="Number(marketingType) === 0" :range="couponOptions" range-key="label" :value="couponIndex" @change="onCouponChange">
				<view class="price-row clickable">
					<text>优惠券</text>
					<text class="muted">{{ selectedCoupon.label }} ›</text>
				</view>
			</picker>
			<view class="price-row"><text>商品价格</text><text class="strong">¥{{ priceText(quote.originalAmount || product.salePrice) }}</text></view>
			<view class="price-row"><text>优惠金额</text><text class="discount">- ¥{{ priceText(quote.discountAmount) }}</text></view>
			<view class="price-row total no-border"><text>应付金额</text><text>¥{{ priceText(payableAmount) }}</text></view>
		</view>

		<view class="section-title payment-title">支付方式</view>
		<view class="payment-card">
			<view class="payment-row" @click="selectPayMethod(1)">
				<view class="pay-icon wechat">微</view>
				<view class="pay-copy"><view>微信支付 <text>推荐使用</text></view></view>
				<view class="radio" :class="{ checked: payMethod === 1 }"><text v-if="payMethod === 1">✓</text></view>
			</view>
			<view class="payment-row" @click="selectPayMethod(3)">
				<view class="pay-icon balance">储</view>
				<view class="pay-copy"><view>储值支付 <text>余额 ¥{{ priceText(wallet.balanceAmount) }}</text></view></view>
				<view class="radio" :class="{ checked: payMethod === 3 }"><text v-if="payMethod === 3">✓</text></view>
			</view>
			<view class="payment-row no-border" :class="{ disabled: !installmentAvailable }" @click="selectPayMethod(4)">
				<view class="pay-icon installment">期</view>
				<view class="pay-copy">
					<view>分期付款
						<text v-if="installmentAvailable">首付 ¥{{ priceText(quote.installmentDownPaymentAmount) }} · 共 {{ quote.installmentCount }} 期</text>
						<text v-else>该商品不可分期</text>
					</view>
				</view>
				<view class="radio" :class="{ checked: payMethod === 4 }"><text v-if="payMethod === 4">✓</text></view>
			</view>
		</view>

		<view class="refund-tip">
			<text class="shield">◇</text>
			<text>{{ refundText(product) }}</text>
		</view>

		<view class="bottom-bar">
			<view class="bottom-amount">
				<text>{{ payMethod === 4 ? '本次首付' : '应付' }}</text>
				<view>¥{{ priceText(currentPaymentAmount) }}</view>
			</view>
			<view class="submit-btn" :class="{ disabled: submitting || !canSubmit }" @click="submitOrder">
				{{ submitting ? '处理中...' : '确认支付' }}
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getPrivateProductDetail,
		getPrivateProductStores,
		getProductBookableCoaches,
		quotePrivateOrder,
		createPrivateOrder,
		confirmPrivateOrderWechatPay,
		getUsablePrivateCoupons,
		getPrivateWalletAccount
	} from '@/api/private-training.js'

	export default {
		data() {
			return {
				productId: '',
				preferredStoreId: '',
				marketingType: 0,
				marketingActivityId: '',
				product: {},
				stores: [],
				storeIndex: 0,
				coaches: [],
				coachesLoaded: false,
				selectedCoachId: '',
				coupons: [],
				couponIndex: 0,
				quote: {},
				wallet: { balanceAmount: 0, status: 1 },
				payMethod: 1,
				loaded: false,
				submitting: false,
				fallbackCover: '/static/image/card_bg.png'
			}
		},
		computed: {
			selectedStore() {
				return this.stores[this.storeIndex] || null;
			},
			couponOptions() {
				const count = this.coupons.length;
				const first = [{ memberCouponId: '', label: count ? (count + ' 张可用') : '暂无可用' }];
				return first.concat(this.coupons.map((item) => Object.assign({}, item, {
					label: (item.couponName || '优惠券') + ' -¥' + this.priceText(item.calcDiscountAmount)
				})));
			},
			selectedCoupon() {
				return this.couponOptions[this.couponIndex] || this.couponOptions[0];
			},
			payableAmount() {
				return this.quote.payableAmount !== undefined ? this.quote.payableAmount : (this.product.salePrice || 0);
			},
			installmentAvailable() {
				return this.quote.installmentAvailable === true;
			},
			currentPaymentAmount() {
				return this.payMethod === 4 && this.installmentAvailable
					? this.quote.installmentDownPaymentAmount
					: this.payableAmount;
			},
			canSubmit() {
				return !!this.selectedStore && !!this.selectedCoachId && this.coaches.length > 0;
			}
		},
		onLoad(options) {
			this.productId = options.productId || options.id || '';
			this.preferredStoreId = options.storeId || '';
			this.marketingType = Number(options.marketingType || 0);
			this.marketingActivityId = options.marketingActivityId || '';
			this.loadPage();
		},
		methods: {
			loadPage() {
				if (!this.productId) {
					this.config.Toast('缺少商品信息');
					return;
				}
				uni.showLoading({ title: '加载中' });
				Promise.all([
					getPrivateProductDetail({ id: this.productId }),
					getPrivateProductStores({ id: this.productId }),
					getPrivateWalletAccount().catch(() => ({ data: { balanceAmount: 0, status: 0 } }))
				]).then((values) => {
					this.product = values[0].data || {};
					this.stores = values[1].data || [];
					this.wallet = values[2].data || { balanceAmount: 0, status: 1 };
					const preferred = String(this.preferredStoreId || '');
					const index = this.stores.findIndex((item) => String(item.storeId) === preferred);
					this.storeIndex = index >= 0 ? index : 0;
					return this.refreshStoreData(true);
				}).then(() => {
					this.loaded = true;
					uni.hideLoading();
				}).catch((e) => {
					this.loaded = true;
					uni.hideLoading();
					this.config.Toast((e && e.message) || '确认订单加载失败');
				});
			},
			refreshStoreData(loadCoupons) {
				if (!this.selectedStore) return Promise.resolve();
				this.selectedCoachId = '';
				return Promise.all([this.loadCoaches(), this.refreshPricing(loadCoupons)]);
			},
			loadCoaches() {
				this.coachesLoaded = false;
				return getProductBookableCoaches({
					productId: this.productId,
					storeId: this.selectedStore.storeId
				}).then((res) => {
					this.coaches = res.data || [];
					this.coachesLoaded = true;
				}).catch((e) => {
					this.coaches = [];
					this.coachesLoaded = true;
					throw e;
				});
			},
			baseOrderParams(includeCoupon) {
				const params = {
					productId: this.productId,
					storeId: this.selectedStore && this.selectedStore.storeId,
					quantity: 1,
					marketingType: this.marketingType
				};
				if (this.marketingActivityId) params.marketingActivityId = this.marketingActivityId;
				if (includeCoupon && this.selectedCoupon.memberCouponId) {
					params.memberCouponId = this.selectedCoupon.memberCouponId;
				}
				return params;
			},
			refreshPricing(loadCoupons) {
				return quotePrivateOrder(this.baseOrderParams(true)).then((res) => {
					this.quote = res.data || {};
					if (this.payMethod === 4 && !this.installmentAvailable) this.payMethod = 1;
					if (loadCoupons && Number(this.marketingType) === 0) return this.loadCoupons();
				});
			},
			loadCoupons() {
				return getUsablePrivateCoupons({
					productId: this.productId,
					storeId: this.selectedStore.storeId,
					amount: this.quote.originalAmount || this.product.salePrice,
					marketingType: this.marketingType
				}).then((res) => {
					this.coupons = res.data || [];
					this.couponIndex = 0;
				}).catch(() => {
					this.coupons = [];
					this.couponIndex = 0;
				});
			},
			onStoreChange(e) {
				this.storeIndex = Number(e.detail.value || 0);
				this.couponIndex = 0;
				this.quote = {};
				uni.showLoading({ title: '更新中' });
				this.refreshStoreData(true).then(() => uni.hideLoading()).catch((err) => {
					uni.hideLoading();
					this.config.Toast((err && err.message) || '门店信息更新失败');
				});
			},
			onCouponChange(e) {
				this.couponIndex = Number(e.detail.value || 0);
				this.refreshPricing(false).catch((err) => {
					this.config.Toast((err && err.message) || '价格试算失败');
				});
			},
			selectCoach(coach) {
				this.selectedCoachId = coach.id;
			},
			selectPayMethod(method) {
				if (method === 4 && !this.installmentAvailable) {
					this.config.Toast('当前商品不支持分期付款');
					return;
				}
				this.payMethod = method;
			},
			submitOrder() {
				if (this.submitting) return;
				if (!this.selectedCoachId) {
					this.config.Toast('请选择教练');
					return;
				}
				if (this.payMethod === 3) {
					if (Number(this.wallet.status) !== 1) {
						this.config.Toast('储值账户当前不可用');
						return;
					}
					if (Number(this.wallet.balanceAmount || 0) < Number(this.payableAmount || 0)) {
						this.config.Toast('储值余额不足，请先充值或选择其他支付方式');
						return;
					}
				}
				if (this.payMethod === 4 && !this.installmentAvailable) {
					this.config.Toast('当前商品不支持分期付款');
					return;
				}
				this.submitting = true;
				const params = this.baseOrderParams(true);
				params.coachId = this.selectedCoachId;
				params.payMethod = this.payMethod;
				createPrivateOrder(params).then((res) => {
					const data = res.data || {};
					if (data.paid) return { paid: true };
					return this.requestWechatPay(data.payParams || {}).then(() => {
						return this.waitForWechatConfirmation(data.orderNo);
					});
				}).then((state) => {
					this.submitting = false;
					this.config.Toast(state && state.paid ? '支付成功' : '支付已提交，订单确认中');
					setTimeout(() => uni.redirectTo({ url: '/pagesA/private_order/private_order' }), 800);
				}).catch((e) => {
					this.submitting = false;
					const canceled = e && e.errMsg && e.errMsg.indexOf('cancel') !== -1;
					this.config.Toast(canceled ? '支付已取消，可在私教订单继续支付' : ((e && e.message) || '下单失败'));
				});
			},
			requestWechatPay(params) {
				return new Promise((resolve, reject) => {
					if (!params || !params.timeStamp || !params.nonceStr || !params.package || !params.paySign) {
						reject(new Error('微信支付参数不完整'));
						return;
					}
					uni.requestPayment({
						appId: params.appId,
						timeStamp: params.timeStamp,
						nonceStr: params.nonceStr,
						package: params.package,
						signType: params.signType,
						paySign: params.paySign,
						success: resolve,
						fail: reject
					});
				});
			},
			waitForWechatConfirmation(orderNo, attempt) {
				const current = Number(attempt || 0);
				return confirmPrivateOrderWechatPay({ orderNo }).then((res) => {
					const state = res.data || {};
					if (state.paid || current >= 4) return state;
					return this.delay(800).then(() => this.waitForWechatConfirmation(orderNo, current + 1));
				}).catch(() => {
					if (current >= 4) return { paid: false };
					return this.delay(800).then(() => this.waitForWechatConfirmation(orderNo, current + 1));
				});
			},
			delay(ms) {
				return new Promise((resolve) => setTimeout(resolve, ms));
			},
			coachInitial(name) {
				return String(name || '教').slice(0, 1);
			},
			serviceTypeText(value) {
				return Number(value) === 2 ? '一对多' : '一对一';
			},
			validityText(value) {
				return Number(value) === -1 ? '长期' : (Number(value || 0) + ' 天');
			},
			refundText(product) {
				if (product.refundRule) return product.refundRule;
				if (Number(product.refundType) === 1) return '未开始的课时支持按课程退款规则申请退款，已预约或已核销课时按剩余课时计算。';
				if (Number(product.refundType) === 3) return '退款需提交申请并由门店工作人员审核，实际退款金额以剩余权益为准。';
				return '本课程不支持退款，购买前请确认课程、教练和适用门店。';
			},
			priceText(value) {
				const n = Number(value);
				return isNaN(n) ? '0.00' : n.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #f5f5f7; }
	.confirm-page { min-height: 100vh; padding: 24rpx 24rpx 190rpx; box-sizing: border-box; background: #f5f5f7; color: #202124; }
	.product-card, .coach-panel, .info-card, .price-card, .payment-card { background: #fff; border-radius: 28rpx; }
	.product-card { display: flex; padding: 28rpx; }
	.product-cover { width: 166rpx; height: 166rpx; border-radius: 24rpx; background: #ffeadb; flex-shrink: 0; }
	.product-main { flex: 1; min-width: 0; margin-left: 24rpx; }
	.product-head { display: flex; align-items: flex-start; justify-content: space-between; gap: 16rpx; }
	.product-name { font-size: 30rpx; line-height: 42rpx; font-weight: 700; }
	.product-price { color: #ff4d16; font-size: 32rpx; font-weight: 800; white-space: nowrap; }
	.tag-row { display: flex; gap: 12rpx; margin: 16rpx 0; }
	.tag { padding: 6rpx 16rpx; border-radius: 10rpx; background: #f3f3f5; color: #777985; font-size: 22rpx; }
	.tag.primary { color: #ff5418; background: #fff0e7; }
	.product-meta { color: #7d7f89; font-size: 24rpx; line-height: 36rpx; }
	.section-title { margin: 44rpx 2rpx 20rpx; font-size: 32rpx; font-weight: 800; }
	.section-title text { color: #ff5418; font-size: 23rpx; font-weight: 600; }
	.coach-panel { min-height: 210rpx; padding: 26rpx 18rpx; }
	.coach-scroll { width: 100%; white-space: nowrap; }
	.coach-list { display: inline-flex; gap: 34rpx; padding: 0 10rpx; }
	.coach-item { width: 126rpx; text-align: center; }
	.avatar-wrap { position: relative; width: 100rpx; height: 100rpx; margin: 0 auto 14rpx; border: 6rpx solid transparent; border-radius: 50%; box-sizing: border-box; display: flex; align-items: center; justify-content: center; background: #ffeadb; color: #5e493e; font-size: 42rpx; font-weight: 800; }
	.avatar-wrap.selected { border-color: #ff5418; background: #f1eaff; }
	.coach-avatar { width: 88rpx; height: 88rpx; border-radius: 50%; }
	.selected-mark { position: absolute; right: -8rpx; bottom: -2rpx; width: 34rpx; height: 34rpx; line-height: 34rpx; border-radius: 50%; color: #fff; background: #ff5418; font-size: 20rpx; }
	.coach-name { overflow: hidden; text-overflow: ellipsis; font-size: 27rpx; font-weight: 700; }
	.coach-name.active { color: #ff5418; }
	.coach-level { margin-top: 6rpx; color: #999ba4; font-size: 21rpx; }
	.empty-coach { padding: 54rpx 0; text-align: center; color: #999ba4; font-size: 25rpx; }
	.info-card, .price-card { margin-top: 24rpx; padding: 12rpx 28rpx; }
	.info-row, .price-row { min-height: 86rpx; display: flex; align-items: center; justify-content: space-between; gap: 30rpx; border-bottom: 1rpx solid #ececf0; font-size: 27rpx; }
	.row-label { color: #7c7e88; }
	.row-value { flex: 1; text-align: right; }
	.strong { color: #25262b; font-weight: 700; }
	.muted { color: #9b9da7; }
	.clickable:active { opacity: .65; }
	.discount { color: #ff4d16; font-weight: 700; }
	.price-row.total { min-height: 104rpx; font-weight: 800; font-size: 30rpx; }
	.price-row.total text:last-child { color: #ff4d16; font-size: 38rpx; }
	.payment-title { margin-top: 48rpx; }
	.payment-card { padding: 8rpx 28rpx; }
	.payment-row { min-height: 98rpx; display: flex; align-items: center; border-bottom: 1rpx solid #ececf0; }
	.payment-row.disabled { opacity: .45; }
	.pay-icon { width: 48rpx; height: 48rpx; border-radius: 12rpx; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22rpx; font-weight: 800; }
	.pay-icon.wechat { background: #20aa67; }
	.pay-icon.balance { background: #ff5b18; }
	.pay-icon.installment { background: #8b60ec; }
	.pay-copy { flex: 1; margin-left: 22rpx; font-size: 29rpx; font-weight: 700; }
	.pay-copy text { margin-left: 12rpx; color: #a2a4ad; font-size: 22rpx; font-weight: 500; }
	.radio { width: 38rpx; height: 38rpx; border: 4rpx solid #d3d5dc; border-radius: 50%; box-sizing: border-box; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 22rpx; }
	.radio.checked { border-color: #ff5418; background: #ff5418; }
	.no-border { border-bottom: 0; }
	.refund-tip { display: flex; gap: 16rpx; padding: 28rpx 10rpx; color: #858791; font-size: 23rpx; line-height: 36rpx; }
	.shield { color: #9ca0aa; font-size: 32rpx; }
	.bottom-bar { position: fixed; z-index: 20; left: 0; right: 0; bottom: 0; padding: 20rpx 28rpx calc(20rpx + env(safe-area-inset-bottom)); display: flex; align-items: center; background: rgba(255,255,255,.98); box-shadow: 0 -8rpx 30rpx rgba(0,0,0,.06); }
	.bottom-amount { flex: 1; color: #7b7d87; font-size: 22rpx; }
	.bottom-amount view { margin-top: 4rpx; color: #ff4d16; font-size: 38rpx; font-weight: 800; }
	.submit-btn { width: 330rpx; height: 86rpx; border-radius: 46rpx; display: flex; align-items: center; justify-content: center; color: #fff; background: linear-gradient(90deg, #ff721a, #ff4b0a); font-size: 30rpx; font-weight: 800; }
	.submit-btn.disabled { opacity: .45; }
</style>
