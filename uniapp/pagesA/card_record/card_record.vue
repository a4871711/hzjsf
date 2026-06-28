<template>
	<view>
		<view class="card-record" v-if="list.length > 0">
			<view class="item-box" v-for="(item,index) in list" :key="index">
				<view class="item">
					<text class="item-l">订单编号</text>
					<text class="item-r">{{item.orderNo}}</text>
				</view>
				<view class="item">
					<text class="item-l">购卡门店</text>
					<text class="item-r">{{item.storeName}}</text>
				</view>
				<view class="item">
					<text class="item-l">购卡套餐</text>
					<text class="item-r">{{item.cardTypeDescription}}</text>
				</view>
				<view class="item">
					<text class="item-l">优惠抵扣</text>
					<text class="item-r">￥{{item.couponDeduction}}</text>
				</view>
				<view class="item">
					<text class="item-l">实付金额</text>
					<text class="item-r">￥{{item.paySum}}</text>
				</view>
				<view class="item">
					<text class="item-l">支付方式</text>
					<text class="item-r">{{item.payType == 13 ? '自动续费':'微信支付'}}</text>
				</view>
				<view class="item">
					<text class="item-l">购卡时间</text>
					<text class="item-r">{{item.timer}}</text>
				</view>
			</view>
		</view>
		<view v-else style="margin: 200rpx auto;">
			<u-empty mode="list" icon="/static/image/history.png" text="暂无购买记录"></u-empty>
		</view>
	</view>

</template>

<script>
	import {
		cardRecord
	} from '@/api/my.js'
	export default {
		data() {
			return {
				list: [],
				page: 1,
				pagesize: 10,
				totalPage: 1,
			};
		},
		onShow() {
			this.list = []
			this.page = 1
			this.totalPage = 1
			this.cardRecord()
		},
		// 下拉加载更多
		onReachBottom() {
			if (this.page < this.totalPage) {
				this.page++
				this.cardRecord()
				// console.log('触底了', this.page);
			} else {
				uni.showToast({
					title: '最后一页了',
					icon: 'none'
				})
			}
		},

		onLoad() {
			// this.cardRecord()
		},
		methods: {
			// 根据卡类型判断
			getCardTypeDescription(type) {
				const cardTypes = {
					0: '月卡',
					1: '季卡',
					2: '半年卡',
					3: '年卡',
					7: '连续包月卡',
					10: '次卡'
				};
				return cardTypes[type] || '未知卡类型';
			},
			cardRecord() {
				let data = {
					page: this.page,
					limit: this.pagesize
				}
				cardRecord(data).then((res) => {
					const moreData = res.data.list.map(item => {
						return {
							...item,
							cardTypeDescription: this.getCardTypeDescription(item.type),
							couponDeduction: (item.paySum - item.realPayment).toFixed(2),
							timer: this.config.timestampToDateTime(item.createdDate)
						};
					})
					this.list = this.list.concat(moreData)
					this.totalPage = res.data.totalPage;
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.card-record {
		padding: 0 40rpx;
		.item-box {
			line-height: 50rpx;
			background: #FFFFFF;
			box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);
			border-radius: 20rpx;
			padding: 30rpx;
			box-sizing: border-box;
			display: flex;
			flex-direction: column;
			justify-content: space-between;
			margin-bottom: 20rpx;

			.item {
				display: flex;
				justify-content: space-between;

				.item-l {
					font-weight: 500;
					font-size: 26rpx;
					color: #999999;
				}

				.item-r {
					font-weight: bold;
					font-size: 26rpx;
					color: #333333;
				}
			}
		}
	}
</style>