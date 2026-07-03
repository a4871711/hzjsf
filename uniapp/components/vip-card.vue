<template>
	<view class="vip-card">
		<view class="item flex_col_center" v-for="(item, index) in cardList" :key="index"
			@click="config.path('/pagesA/card_renewal/card_renewal?id='+storeId+'&fitCardId='+ item.fitCardId)">
			<!-- <view class="item-title">{{item.autoPay != 0 ?'连续包':''}}{{item.ctName}}</view> -->
			<view class="item-title">{{item.cardName||'--'}}</view>
			<view class="price">
				<text class="text">¥</text>{{calcPrice(item)}}
			</view>
			<view class="btn flex_1">立即抢购</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: "vip-card",
		props: {
			cardList: {
				type: Array,
				default: []
			},
			storeId: {
				type: String,
				default: 0
			},
		},
		data() {
			return {

			};
		},
		methods: {
			// 应付价：权益会员且卡配了权益卡价 → benefitPrice；新人 → 新人价；否则单价
			calcPrice(item) {
				if (item.isBenefitMember == 1 && Number(item.benefitPrice) > 0) {
					return item.benefitPrice;
				}
				return item.isNewUser == 1 ? (item.newUserPrice || item.cardPrice) : item.cardPrice;
			},
		}
	}
</script>

<style lang="scss" scoped>
	.vip-card {
		display: flex;

		.item {
			width: 32%;
			height: 302rpx;
			background: #FBE7D9;
			border-radius: 20rpx;
			box-sizing: border-box;
			margin-right: 15rpx;

			.item-title {
				font-size: 30rpx;
				color: #333333;
				font-weight: 500;
			}

			.price {
				font-weight: 800;
				font-size: 60.65rpx;
				color: #E15B00;
				line-height: 34rpx;
				margin-top: 47.51rpx;
				margin-bottom: 48.52rpx;

				.text {
					font-size: 30.32rpx;
				}
			}

			.btn {
				width: 144rpx;
				height: 46rpx;
				background: #E15B00;
				border-radius: 23rpx;
				font-size: 24rpx;
				color: #FFFFFF;
			}
		}
	}
</style>