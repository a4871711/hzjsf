<template>
	<view class="coupon">
		<view class="navbar">
			<u-subsection :list="list" mode="subsection" :current="curNow" activeColor="#dd541a"
				@change="sectionChange"></u-subsection>
		</view>

		<view v-if="couponList.length != 0" style="padding-top: 80rpx;">
			<view class="item-box flex" v-for="(item, index) in couponList" @click="changeCoupon(item)"
				:class="item.couponStatus == 2 ?'item-gray':''">
				<view class="item-l flex_col_center">
					<view class="price">
						<text class="font_size_24">¥</text>
						<text class="font_size_34 bold">{{item.couponPrice}}</text>
					</view>
					<view class="font_size_22">满{{item.limitPrice || '--'}}元可用</view>
				</view>
				<view class="item-r">
					<view class="item-r-t marg_bottom_20">
						<view class="font_size_32 bold" style="width: 300rpx;">{{item.couponTitle || '--'}}</view>
						<view class="tips flex_1" v-if="item.couponType == 0">普通优惠券</view>
						<view class="tips flex_1" v-if="item.couponType == 1">抖音优惠券</view>
						<view class="tips flex_1" v-if="item.couponType == 2">美团优惠券</view>
					</view>
					<view>
						<view class="clr_h font_size_22">有效期至：{{item.validityTime}}</view>
						<view></view>
					</view>
				</view> 
			</view>
		</view>
		<view v-else style="margin: 200rpx auto;">
			<u-empty mode="couponList" icon="/static/image/coupon.png" text="暂无"></u-empty>
		</view>

		<!-- 弹窗 -->
		<tip :show="show" content="发起团购退款后对金额将原路返回 确定要团购退款吗？" title="团购退款" @handlconfirm="handlconfirm"
			@cancellation="cancellation" confirm="确定关闭" cancel="我再想想" :showCancelButton="true"></tip>

	</view>
</template>

<script>
	import {
		getCouponList
	} from '@/api/coupon.js'
	import tip from '@/components/tip.vue';
	export default {
		components: {
			tip
		},
		data() {
			return {
				show: false,
				couponList: [],
				list: ['正常', '已使用', '已过期'],
				// 或者如下，也可以配置keyName参数修改对象键名
				// list: [{name: '未付款'}, {name: '待评价'}, {name: '已付款'}],
				curNow: 0,
				couponStatus: '', //全部
				storeAddrId: '', // 门店ID
				fitCardId: '', // 会员ID
				paySum: '', // 金额
			};
		},
		onLoad(opts) {
			this.storeAddrId = opts.storeAddrId || '';
			this.fitCardId = opts.fitCardId || '';
			this.paySum = opts.paySum || '';
			this.storeId = opts.storeId || '';

		},
		onShow() {
			this.getCoupon();
		},
		methods: {
			// 切换
			sectionChange(index) {
				this.curNow = index;
				this.getCoupon();
			},

			// 选中优惠券
			changeCoupon(row) {
				if (this.curNow == 0) {
					row.fitCardId = this.fitCardId;
					uni.setStorageSync('coupon_info', row)
					// 4. 返回上一页面
					uni.navigateBack({
						delta: 1 // 返回的页面数
					})
				} else {
					this.config.Toast('暂不可用');
				}

			},
			// 获取优惠券
			getCoupon() {
				let data = {
					couponStatus: this.curNow,
					storeAddrId: this.storeAddrId,
					// storeAddrId:this.storeId,
					fitCardId: this.fitCardId,
					paySum: this.paySum,
				}
				getCouponList(data).then((res) => {
					this.couponList = res.data
				})
			},
			cancellation() {
				this.show = false
			},
			handlconfirm() {
				console.log('确定')
				this.show = false
			},

			handleRefund() {
				this.show = true
			}
		}
	}
</script>

<style lang="scss" scoped>
	.coupon {
		margin: 0rpx auto;
		padding: 0 40rpx;
		box-sizing: border-box;

		.navbar {
			width: 90%;
			margin: auto;
			position: fixed;
			top: 0;
			left: 0;
			right: 0;
			z-index: 10;
			height: 80rpx;
			background: #fff;
		}

		.item-box {
			background-repeat: no-repeat;
			background-image: url('https://shilijsf.shilisports.com/h5/bg_img/coupons_bg.png');
			background-size: 100% 100%;
			height: 160rpx;
			border-radius: 20rpx;
			margin-bottom: 18rpx;

			.item-l {
				width: 160rpx;
				height: 160rpx;
				border-radius: 20rpx 0rpx 0rpx 20rpx;
				color: #fff;
			}

			.item-r {
				flex: 1;
				padding: 0 37rpx 0 32rpx;
				box-sizing: border-box;

				.item-r-t {
					display: flex;
					justify-content: space-between;
					align-items: center;

					.tips {
						width: 130rpx;
						height: 40rpx;
						border-radius: 10rpx;
						border: 1px solid #DD541A;
						font-weight: 500;
						font-size: 22rpx;
						color: #DD541A;
					}
				}

				.btn {
					width: 119rpx;
					height: 45rpx;
					background: linear-gradient(94deg, #FFB25F, #FF6E20);
					border-radius: 22rpx;
					color: #fff;
					font-size: 22rpx;
				}
			}
		}

		.item-gray {
			filter: grayscale(100%);
		}
	}
</style>