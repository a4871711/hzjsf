<template>
	<view class="detail-page" v-if="loaded">
		<view class="hero-wrap">
			<image class="hero" :src="product.coverUrl || fallbackCover" mode="aspectFill"></image>
			<view class="hero-mask"></view>
			<view class="hero-label">私教课程</view>
		</view>

		<view class="price-card">
			<view class="price-main">
				<text class="price-label">{{ Number(marketingType) !== 0 ? (quote.marketingActivityName || '活动价') : '课程价' }}</text>
				<text class="price-symbol">¥</text>
				<text class="price-number">{{ priceText(currentPrice) }}</text>
				<text class="origin" v-if="Number(product.originalPrice) > Number(currentPrice)">原价 ¥{{ priceText(product.originalPrice) }}</text>
			</view>
			<view class="price-meta">
				<text v-if="isLessonCountVisible">{{ product.lessonCount || 0 }} 课时 · </text>{{ validityText(product.validityDays) }}有效
			</view>
			<view class="discount" v-if="Number(quote.discountAmount) > 0">已优惠 ¥{{ priceText(quote.discountAmount) }}</view>
		</view>

		<view class="course-summary">
			<view class="title">{{ product.productName || '私教课程' }}</view>
			<view class="subtitle">{{ product.productSubtitle || product.productIntro || '专业私教课程' }}</view>
			<view class="tag-row">
				<text class="tag tag-primary">{{ product.typeName || '私教课程' }}</text>
				<text class="tag">{{ serviceTypeText(product.serviceType) }}</text>
				<text class="tag" v-if="product.categoryName">{{ product.categoryName }}</text>
			</view>
		</view>

		<view class="section info-card">
			<view class="info-row" v-if="isLessonCountVisible"><text>课时数量</text><text>{{ product.lessonCount || 0 }} 节</text></view>
			<view class="info-row"><text>单次时长</text><text>{{ product.durationMinutes || 0 }} 分钟 / 节</text></view>
			<view class="info-row"><text>有效期</text><text>{{ validityDetailText(product.validityDays) }}</text></view>
			<view class="info-row store-info-row">
				<text>适用门店</text>
				<text>{{ product.storeNames || (selectedStore && selectedStore.storeName) || '暂无可用门店' }}</text>
			</view>
		</view>

		<view class="section">
			<view class="section-title">购买选择</view>
			<picker :range="stores" range-key="storeName" :value="storeIndex" @change="onStoreChange">
				<view class="picker-row">
					<text class="label">购买门店</text>
					<view class="picker-value">{{ selectedStore ? selectedStore.storeName : '暂无可用门店' }} <text>›</text></view>
				</view>
			</picker>
			<view class="store-address" v-if="selectedStore">{{ selectedStore.storeAddress || '门店详情请咨询客服' }}</view>

			<picker v-if="Number(marketingType) === 0 && isLoggedIn()" :range="couponOptions" range-key="label"
				:value="couponIndex" @change="onCouponChange">
				<view class="picker-row coupon-row">
					<text class="label">优惠券</text>
					<view class="picker-value">{{ selectedCoupon.label }} <text>›</text></view>
				</view>
			</picker>
		</view>

		<view class="section coach-section">
			<view class="section-head">
				<view class="section-title">可预约教练</view>
				<text class="section-extra" v-if="coaches.length">共 {{ coaches.length }} 位</text>
			</view>
			<scroll-view v-if="coaches.length" class="coach-scroll" scroll-x :show-scrollbar="false">
				<view class="coach-list">
					<view class="coach-card" v-for="coach in coaches" :key="coach.id">
						<image v-if="coach.avatarUrl" class="coach-avatar" :src="coach.avatarUrl" mode="aspectFill"></image>
						<view v-else class="coach-avatar coach-avatar-fallback">{{ coachInitial(coach.coachName) }}</view>
						<view class="coach-name">{{ coach.coachName || '教练' }}</view>
						<view class="coach-level">{{ coach.coachLevel || '专业教练' }}</view>
					</view>
				</view>
			</scroll-view>
			<view v-else-if="coachesLoaded" class="coach-empty">当前暂无符合预约条件的教练</view>
			<view v-else class="coach-empty">教练信息加载中...</view>
			<view class="coach-tip">未指定教练的课程，可选择适用门店内符合预约条件的教练</view>
		</view>

		<view class="section" v-if="product.targetDesc">
			<view class="section-title">课程简介</view>
			<view class="paragraph">{{ product.targetDesc }}</view>
		</view>

		<view class="section">
			<view class="section-title">课程详情</view>
			<rich-text v-if="product.productDetail" :nodes="product.productDetail"></rich-text>
			<view class="paragraph" v-else>{{ product.productIntro || '暂无更多详情' }}</view>
		</view>

		<view class="section gift-section" v-if="Number(product.groupBenefitEnabled) === 1">
			<view class="section-title">附赠团课权益</view>
			<view class="gift-card">
				<view class="gift-icon">礼</view>
				<view class="gift-content">
					<view>赠送团课 {{ product.groupBenefitGiftCount || 0 }} 次</view>
					<text>权益有效期 {{ product.groupBenefitValidityDays || 0 }} 天</text>
				</view>
			</view>
		</view>

		<view class="section">
			<view class="section-title">预约与取消</view>
			<view class="notice-list">
				<view><text>·</text>最晚需提前 {{ product.latestBookingHours || 0 }} 小时预约</view>
				<view><text>·</text>请在开课前 {{ product.latestFreeCancelHours || 0 }} 小时以外取消</view>
				<view><text>·</text>{{ Number(product.noShowDeduct) === 1 ? '爽约将扣除 1 节课' : '爽约不扣课' }}</view>
			</view>
		</view>

		<view class="section">
			<view class="section-title">退款说明</view>
			<view class="paragraph refund-text">{{ refundText(product) }}</view>
		</view>

		<view class="bottom-bar">
			<view class="pay-info">
				<text>应付</text>
				<view>¥{{ priceText(currentPrice) }}</view>
			</view>
			<view class="pay-btn" :class="{ disabled: submitting || !stores.length || isUpcoming }" @click="buyNow">
				{{ submitting ? '处理中...' : (isUpcoming ? '待开售' : '立即购买') }}
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
		getUsablePrivateCoupons
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
				coaches: [],
				coachesLoaded: false,
				storeIndex: 0,
				quote: {},
				coupons: [],
				couponIndex: 0,
				loaded: false,
				submitting: false,
				fallbackCover: '/static/image/card_bg.png'
			}
		},
		computed: {
			isLessonCountVisible() {
				// 兼容后端升级前未返回该字段的情况：只有明确配置为 0 时才隐藏。
				return Number(this.product.lessonCountVisible) !== 0;
			},
			selectedStore() {
				return this.stores[this.storeIndex] || null;
			},
			couponOptions() {
				const first = [{ memberCouponId: '', label: '不使用优惠券' }];
				return first.concat(this.coupons.map((item) => Object.assign({}, item, {
					label: (item.couponName || '优惠券') + ' -¥' + this.priceText(item.calcDiscountAmount)
				})));
			},
			selectedCoupon() {
				return this.couponOptions[this.couponIndex] || this.couponOptions[0];
			},
			currentPrice() {
				if (this.quote.payableAmount !== undefined && this.quote.payableAmount !== null) {
					return this.quote.payableAmount;
				}
				return this.product.salePrice || 0;
			},
			isUpcoming() {
				if (!this.product.listingAt) return false;
				const value = typeof this.product.listingAt === 'string' ? this.product.listingAt.replace(/-/g, '/') : this.product.listingAt;
				const time = new Date(value).getTime();
				return !isNaN(time) && time > Date.now();
			}
		},
		onLoad(options) {
			this.productId = options.id || options.productId || '';
			this.preferredStoreId = options.storeId || '';
			this.marketingType = Number(options.marketingType || 0);
			this.marketingActivityId = options.marketingActivityId || '';
			this.loadPage();
		},
		onShow() {
			if (this.loaded && this.isLoggedIn() && this.selectedStore && !this.quote.payableAmount) {
				this.refreshPricing(true);
			}
		},
		methods: {
			loadPage() {
				if (!this.productId) {
					this.config.Toast('缺少商品信息');
					return;
				}
				uni.showLoading({ title: '加载中' });
				getPrivateProductDetail({ id: this.productId }).then((res) => {
					this.product = res.data || {};
					return getPrivateProductStores({ id: this.productId });
				}).then((res) => {
					this.stores = res.data || [];
					const preferred = String(this.preferredStoreId || '');
					const index = this.stores.findIndex((item) => String(item.storeId) === preferred);
					this.storeIndex = index >= 0 ? index : 0;
					this.loaded = true;
					uni.hideLoading();
					this.loadCoaches().then(() => {
						if (this.isLoggedIn() && this.selectedStore) this.refreshPricing(true);
					});
				}).catch((e) => {
					uni.hideLoading();
					this.loaded = true;
					this.config.Toast((e && e.message) || '商品加载失败');
				});
			},
			loadCoaches() {
				this.coachesLoaded = false;
				return getProductBookableCoaches({
					productId: this.productId,
					storeId: this.selectedStore && this.selectedStore.storeId
				}).then((res) => {
					this.coaches = res.data || [];
					this.coachesLoaded = true;
				}).catch(() => {
					this.coaches = [];
					this.coachesLoaded = true;
				});
			},
			isLoggedIn() {
				return !!uni.getStorageSync('token');
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
				if (!this.selectedStore || !this.isLoggedIn()) return Promise.resolve();
				return quotePrivateOrder(this.baseOrderParams(true)).then((res) => {
					this.quote = res.data || {};
					if (loadCoupons && Number(this.marketingType) === 0) return this.loadCoupons();
				}).catch((e) => {
					this.config.Toast((e && e.message) || '价格试算失败');
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
					this.loadCoaches();
					if (this.isLoggedIn()) this.refreshPricing(true);
			},
			onCouponChange(e) {
				this.couponIndex = Number(e.detail.value || 0);
				this.refreshPricing(false);
			},
			buyNow() {
				if (this.submitting || !this.stores.length) return;
				if (this.isUpcoming) {
					this.config.Toast('课程尚未开售');
					return;
				}
				if (!this.isLoggedIn()) {
					this.config.Toast('请先登录');
					setTimeout(() => uni.navigateTo({ url: '/pagesA/login/login' }), 800);
					return;
				}
				if (!this.selectedStore) {
					this.config.Toast('请选择门店');
					return;
				}
					const query = [
						'productId=' + encodeURIComponent(this.productId),
						'storeId=' + encodeURIComponent(this.selectedStore.storeId),
						'marketingType=' + encodeURIComponent(this.marketingType)
					];
					if (this.marketingActivityId) {
						query.push('marketingActivityId=' + encodeURIComponent(this.marketingActivityId));
					}
					uni.navigateTo({
						url: '/pagesA/private_order_confirm/private_order_confirm?' + query.join('&')
					});
				},
			serviceTypeText(value) {
				return Number(value) === 2 ? '一对多' : '一对一';
			},
			validityText(value) {
				return Number(value) === -1 ? '长期' : (Number(value || 0) + '天');
			},
			validityDetailText(value) {
				return Number(value) === -1 ? '购买后长期有效' : ('购买后 ' + Number(value || 0) + ' 天内有效');
			},
			coachInitial(name) {
				const value = String(name || '教');
				return value.slice(0, 1);
			},
			refundText(product) {
				if (product.refundRule) return product.refundRule;
				if (Number(product.refundType) === 1) return '本课程支持退款，具体条件以门店审核为准。';
				if (Number(product.refundType) === 3) return '本课程退款需提交申请并由工作人员审核。';
				return '本课程不支持退款，购买前请确认课程和适用门店。';
			},
			priceText(value) {
				const n = Number(value);
				return isNaN(n) ? '0.00' : n.toFixed(2);
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F5F5F7; }
	.detail-page { min-height: 100vh; padding-bottom: 170rpx; background: #F5F5F7; color: #202124; }
	.hero-wrap { position: relative; height: 430rpx; overflow: hidden; background: linear-gradient(135deg, #FFE5D0, #FFF5EC); }
	.hero { width: 100%; height: 100%; }
	.hero-mask { position: absolute; left: 0; right: 0; bottom: 0; height: 130rpx; background: linear-gradient(transparent, rgba(0,0,0,.16)); }
	.hero-label { position: absolute; left: 28rpx; bottom: 28rpx; padding: 9rpx 18rpx; color: #FFF; background: rgba(255,86,22,.92); border-radius: 22rpx; font-size: 22rpx; }
	.price-card { position: relative; z-index: 2; margin: -34rpx 24rpx 0; padding: 26rpx 28rpx; background: #FFF; border-radius: 24rpx; box-shadow: 0 10rpx 30rpx rgba(30,30,30,.08); }
	.price-main { display: flex; align-items: baseline; flex-wrap: wrap; }
	.price-label { margin-right: 14rpx; padding: 6rpx 12rpx; color: #FFF; background: #FF571A; border-radius: 8rpx; font-size: 21rpx; }
	.price-symbol { color: #FF4F13; font-size: 27rpx; font-weight: 800; }
	.price-number { color: #FF4F13; font-size: 50rpx; font-weight: 900; }
	.price-meta { position: absolute; right: 28rpx; top: 42rpx; color: #72747C; font-size: 23rpx; }
	.origin { margin-left: 16rpx; color: #A3A5AD; font-size: 22rpx; text-decoration: line-through; }
	.discount { margin-top: 8rpx; color: #FF571A; font-size: 22rpx; }
	.course-summary { padding: 36rpx 30rpx 12rpx; }
	.title { font-size: 38rpx; line-height: 52rpx; color: #202124; font-weight: 900; }
	.subtitle { margin-top: 12rpx; color: #7E8089; font-size: 25rpx; line-height: 38rpx; }
	.tag-row { display: flex; flex-wrap: wrap; margin-top: 22rpx; }
	.tag { margin: 0 14rpx 10rpx 0; padding: 7rpx 16rpx; color: #777A84; background: #ECEDEF; border-radius: 8rpx; font-size: 22rpx; }
	.tag-primary { color: #FF571A; background: #FFF0E7; }
	.section { margin: 20rpx 24rpx; padding: 28rpx; background: #FFF; border-radius: 22rpx; }
	.section-title { padding-left: 15rpx; border-left: 6rpx solid #FF571A; color: #222; font-size: 30rpx; font-weight: 800; }
	.section > .section-title { margin-bottom: 26rpx; }
	.info-card { padding-top: 8rpx; padding-bottom: 8rpx; }
	.info-row { display: flex; min-height: 84rpx; align-items: center; border-bottom: 1rpx solid #EEEFF2; font-size: 25rpx; }
	.info-row:last-child { border-bottom: none; }
	.info-row text:first-child { width: 170rpx; color: #7A7C85; }
	.info-row text:last-child { flex: 1; color: #24252A; text-align: right; font-weight: 600; }
	.store-info-row { padding: 18rpx 0; align-items: flex-start; }
	.store-info-row text:last-child { line-height: 38rpx; }
	.picker-row { display: flex; min-height: 80rpx; align-items: center; border-bottom: 1rpx solid #EEE; }
	.coupon-row { border-bottom: none; }
	.label { color: #555; font-size: 26rpx; }
	.picker-value { flex: 1; margin-left: 30rpx; color: #222; text-align: right; font-size: 26rpx; }
	.picker-value text { margin-left: 12rpx; color: #AAA; font-size: 34rpx; }
	.store-address { padding: 15rpx 0 20rpx; color: #999; font-size: 22rpx; border-bottom: 1rpx solid #EEE; }
	.paragraph { color: #666; font-size: 26rpx; line-height: 44rpx; white-space: pre-wrap; }
	.section-head { display: flex; align-items: center; margin-bottom: 26rpx; }
	.section-extra { margin-left: auto; color: #A0A2AA; font-size: 22rpx; }
	.coach-scroll { width: 100%; white-space: nowrap; }
	.coach-list { display: inline-flex; padding-right: 14rpx; }
	.coach-card { width: 180rpx; margin-right: 18rpx; padding: 22rpx 14rpx; text-align: center; background: #F7F7F9; border-radius: 18rpx; box-sizing: border-box; }
	.coach-avatar { width: 104rpx; height: 104rpx; border-radius: 52rpx; background: #FFE8D9; }
	.coach-avatar-fallback { display: flex; margin: 0 auto; align-items: center; justify-content: center; color: #8C4C2D; font-size: 42rpx; font-weight: 800; }
	.coach-name { margin-top: 14rpx; overflow: hidden; color: #24252A; font-size: 25rpx; font-weight: 800; text-overflow: ellipsis; }
	.coach-level { margin-top: 7rpx; overflow: hidden; color: #999BA3; font-size: 21rpx; text-overflow: ellipsis; }
	.coach-empty { padding: 30rpx 0; color: #A2A4AC; text-align: center; font-size: 24rpx; }
	.coach-tip { margin-top: 22rpx; padding: 16rpx 18rpx; color: #8A6B57; background: #FFF7F0; border-radius: 12rpx; font-size: 21rpx; line-height: 34rpx; }
	.gift-card { display: flex; padding: 24rpx; align-items: center; background: linear-gradient(135deg, #FFF6EE, #FFEBDD); border: 1rpx solid #FFD1B5; border-radius: 18rpx; }
	.gift-icon { width: 72rpx; height: 72rpx; display: flex; align-items: center; justify-content: center; color: #FF571A; background: #FFF; border-radius: 18rpx; font-size: 27rpx; font-weight: 800; }
	.gift-content { margin-left: 20rpx; color: #8B441E; font-size: 27rpx; font-weight: 800; }
	.gift-content text { display: block; margin-top: 8rpx; color: #AA765A; font-size: 21rpx; font-weight: 400; }
	.notice-list { color: #666970; font-size: 24rpx; line-height: 46rpx; }
	.notice-list text { margin-right: 14rpx; color: #FF571A; font-weight: 900; }
	.refund-text { padding: 20rpx; background: #F7F7F9; border-radius: 14rpx; }
	.bottom-bar { position: fixed; z-index: 20; left: 0; right: 0; bottom: 0; display: flex; height: 120rpx; padding: 14rpx 24rpx calc(14rpx + env(safe-area-inset-bottom)); align-items: center; background: #FFF; box-shadow: 0 -4rpx 18rpx rgba(0,0,0,.07); box-sizing: content-box; }
	.pay-info { flex: 1; color: #999; font-size: 22rpx; }
	.pay-info view { margin-top: 4rpx; color: #E15B00; font-size: 38rpx; font-weight: 900; }
	.pay-btn { width: 330rpx; height: 88rpx; display: flex; align-items: center; justify-content: center; color: #FFF; background: linear-gradient(90deg, #FF7C31, #FF4D0B); border-radius: 44rpx; font-size: 30rpx; font-weight: 800; }
	.pay-btn.disabled { opacity: .5; }
</style>
