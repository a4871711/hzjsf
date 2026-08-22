<template>
	<view class="detail-page" v-if="loaded">
		<view class="status-card">
			<view class="status">{{ statusText(order.orderStatus, order.orderSource) }}</view>
			<view class="status-tip">{{ statusTip(order.orderStatus, order.orderSource) }}</view>
		</view>

		<view class="card">
			<view class="course-name">{{ order.productName || '私教课程' }}</view>
			<view class="meta">{{ serviceText(order.serviceType) }} · {{ order.lessonCount || 0 }}节 · {{ order.durationMinutes || 0 }}分钟/节</view>
			<view class="row"><text>商品类型</text><text>{{ order.productTypeName || '--' }}</text></view>
			<view class="row"><text>有效期</text><text>{{ validityText(order.validityDays) }}</text></view>
		</view>

		<view class="card">
			<view class="section-title">费用明细</view>
			<view class="row"><text>商品原价</text><text>¥{{ priceText(order.originalAmount) }}</text></view>
			<view class="row" v-if="Number(order.discountAmount) > 0"><text>优惠金额</text><text class="red">-¥{{ priceText(order.discountAmount) }}</text></view>
			<view class="row" v-if="couponRel"><text>优惠券</text><text>{{ couponRel.couponName || '优惠券' }}</text></view>
			<view class="row total"><text>应付金额</text><text>¥{{ priceText(order.payableAmount) }}</text></view>
			<view class="row" v-if="Number(order.paidAmount) > 0"><text>实付金额</text><text>¥{{ priceText(order.paidAmount) }}</text></view>
			<view class="row" v-if="Number(order.refundAmount) > 0"><text>已退金额</text><text>¥{{ priceText(order.refundAmount) }}</text></view>
		</view>

		<view class="card">
			<view class="section-title">订单信息</view>
			<view class="row"><text>订单号</text><text class="copy" @click="copyNo">{{ order.orderNo }} 复制</text></view>
			<view class="row"><text>订单来源</text><text>{{ Number(order.orderSource) === 1 ? '赠送' : '正常购买' }}</text></view>
			<view class="row" v-if="order.remark"><text>备注</text><text>{{ order.remark }}</text></view>
			<view class="row"><text>下单时间</text><text>{{ formatTime(order.createdAt) }}</text></view>
			<view class="row" v-if="order.paidAt"><text>支付时间</text><text>{{ formatTime(order.paidAt) }}</text></view>
			<view class="row"><text>支付方式</text><text>{{ Number(order.orderSource) === 1 ? '赠送' : payMethodText(order.payMethod) }}</text></view>
		</view>

		<view class="button" v-if="Number(order.orderStatus) === 2" @click="goBenefits">查看私教权益</view>
		<view class="button ghost" @click="goOrders">返回订单列表</view>
	</view>
</template>

<script>
	import { getPrivateOrderDetail } from '@/api/private-training.js'

	export default {
		data() {
			return {
				orderNo: '',
				order: {},
				couponRel: null,
				loaded: false
			}
		},
		onLoad(options) {
			this.orderNo = decodeURIComponent(options.orderNo || '');
			this.loadDetail();
		},
		methods: {
			loadDetail() {
				getPrivateOrderDetail({ orderNo: this.orderNo }).then((res) => {
					const data = res.data || {};
					this.order = data.order || {};
					this.couponRel = data.couponRel || null;
					this.loaded = true;
				}).catch((e) => {
					this.loaded = true;
					this.config.Toast((e && e.message) || '订单详情加载失败');
				});
			},
			copyNo() {
				uni.setClipboardData({ data: String(this.order.orderNo || '') });
			},
			statusText(status, source) {
				if (Number(source) === 1) return '赠送订单';
				return ({ 0: '等待支付', 1: '首付已付', 2: '订单已结清', 3: '订单已取消', 4: '订单已退款' })[Number(status)] || '订单状态未知';
			},
			statusTip(status, source) {
				if (Number(source) === 1) return '赠送已生效，无需支付且不支持退款';
				return ({
					0: '可返回订单列表继续支付或取消',
					1: '分期订单已完成首付',
					2: '支付成功后已生成对应课时权益',
					3: '本订单不再占用优惠券',
					4: '退款结果以原支付渠道到账为准'
				})[Number(status)] || '';
			},
			serviceText(type) {
				return Number(type) === 2 ? '一对多' : '一对一';
			},
			validityText(days) {
				return Number(days) === -1 ? '长期有效' : (Number(days || 0) + '天');
			},
			payMethodText(method) {
				return ({ 1: '微信支付', 2: '支付宝', 3: '会员储值', 4: '分期支付', 9: '其他' })[Number(method)] || '--';
			},
			priceText(value) {
				const n = Number(value);
				return isNaN(n) ? '0.00' : n.toFixed(2);
			},
			formatTime(value) {
				if (!value) return '--';
				const d = new Date(typeof value === 'string' ? value.replace(/-/g, '/') : value);
				if (isNaN(d.getTime())) return String(value);
				const p = (n) => n < 10 ? '0' + n : '' + n;
				return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) + ' ' + p(d.getHours()) + ':' + p(d.getMinutes());
			},
			goBenefits() {
				uni.navigateTo({ url: '/pagesA/private_benefit/private_benefit' });
			},
			goOrders() {
				uni.navigateBack();
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.detail-page { min-height: 100vh; padding: 24rpx 24rpx 50rpx; background: #F4F4F4; box-sizing: border-box; }
	.status-card { padding: 38rpx 30rpx; color: #FFF; background: linear-gradient(135deg, #FF8A3D, #E15B00); border-radius: 20rpx; }
	.status { font-size: 38rpx; font-weight: 900; }
	.status-tip { margin-top: 14rpx; opacity: .9; font-size: 23rpx; line-height: 36rpx; }
	.card { margin-top: 20rpx; padding: 28rpx; background: #FFF; border-radius: 18rpx; }
	.course-name { color: #222; font-size: 33rpx; font-weight: 800; }
	.meta { margin: 12rpx 0 20rpx; color: #888; font-size: 23rpx; }
	.section-title { margin-bottom: 16rpx; color: #222; font-size: 29rpx; font-weight: 800; }
	.row { display: flex; min-height: 66rpx; align-items: center; color: #888; font-size: 24rpx; }
	.row text:last-child { flex: 1; margin-left: 30rpx; color: #333; text-align: right; word-break: break-all; }
	.row.total { margin-top: 8rpx; padding-top: 12rpx; border-top: 1rpx solid #EEE; }
	.row.total text:last-child, .row .red { color: #E15B00; font-size: 30rpx; font-weight: 800; }
	.row .copy { color: #E15B00; }
	.button { height: 82rpx; margin-top: 26rpx; display: flex; align-items: center; justify-content: center; color: #FFF; background: #E15B00; border-radius: 42rpx; font-size: 28rpx; font-weight: 700; }
	.button.ghost { color: #E15B00; background: #FFF; border: 1rpx solid #E15B00; }
</style>
