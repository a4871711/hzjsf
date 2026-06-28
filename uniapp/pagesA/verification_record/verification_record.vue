<template>
	<view class="record">
		<view class="record-cont">
			<view v-if="recordListL.length>0">
				<view class="item-box" v-for="(item,index) in recordListL" :key="index">
					<view class="item-top flex_s">
						<view class="top-l">{{item.couponTitle}}</view>
						<view class="top-r">{{item.couponType == 1?"抖音券":'美团券'}}</view>
					</view>
					<view class="item-bott flex_s">
						<view class="bott-l">{{config.timestampToDateTime(item.createdDate)}}</view>
						<view class="bott-r">有效期：{{item.validityTime}}</view>
					</view>
				</view>
			</view>

			<view v-else>
				<view class="empty">
					<view class="txt">您还没有相关记录~</view>
					<view class="btn flex_1" @click="config.path('/pagesA/write_off/write_off')">返回继续兑换</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getCouponList
	} from '@/api/coupon.js'
	export default {
		data() {
			return {
				recordListL: [{
						coupon_code: '96598969989689',
						coupon_dk: '月卡20元抵扣券',
						time: '2024-01-0112:00',
						platform: '抖音'
					},
					{
						coupon_code: '96598969989689',
						coupon_dk: '月卡30元抵扣券',
						time: '2024-01-0112:00',
						platform: '美团'
					},
					{
						coupon_code: '96598969989689',
						coupon_dk: '月卡40元抵扣券',
						time: '2024-01-0112:00',
						platform: '抖音'
					},
					{
						coupon_code: '96598969989689',
						coupon_dk: '月卡50元抵扣券',
						time: '2024-01-0112:00',
						platform: '抖音'
					},

				]
			};
		},
		onLoad() {
			this.init()
		},
		methods: {
			async init() {
				const res = await getCouponList({ 
					couponType:`1,2`
				})
				if(res.code==1){
					this.recordListL=res.data
				}else{
					this.config.Toast(res.msg)
				}
			}
		}
	}
</script>

<style lang="scss" scoped>
	.record {
		.record-cont {
			.empty {
				position: absolute;
				top: 45%;
				left: 50%;
				transform: translate(-45%, -50%);

				.txt {
					font-weight: 500;
					font-size: 32rpx;
					color: #999999;
					line-height: 34rpx;
				}

				.btn {
					width: 269rpx;
					height: 91rpx;
					background: #373838;
					border-radius: 45rpx;
					color: #fff;
					font-size: 30rpx;
					margin-top: 40rpx;
				}
			}

			.item-box {
				margin: 20rpx 30rpx;
				padding: 20rpx;
				box-sizing: border-box;
				background: #FFFFFF;
				box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);
				border-radius: 20rpx;

				.item-top {
					font-weight: 500;

					.top-l {
						color: #333333;
						font-size: 32rpx;
						font-weight: bold;
					}

					.top-r {
						color: #E15B00;
						font-size: 24rpx;
					}
				}

				.item-bott {
					font-size: 24rpx;
					margin-top: 20rpx;

					.bott-l {

						color: #999999;
					}

					.bott-r {
						color: #333333;
						font-weight: bold;
					}
				}
			}
		}
	}
</style>