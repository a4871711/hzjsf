<template>
	<view class="vc-detail">
		<!-- 顶部权益卡主信息 -->
		<view class="vc-hero">
			<view class="vc-hero-top flex_s">
				<view class="vc-kicker">VIP 权益卡</view>
				<view class="vc-buy-btn-top" @click="onBuy">立即购买</view>
			</view>
			<view class="vc-title">{{ card.cardName || '--' }}</view>
			<view class="vc-price-row">
				<text class="now"><text class="sym">¥</text>{{ formatPrice(curPrice) }}</text>
				<text class="base" v-if="isRaised">首发价 ¥{{ formatPrice(card.price) }}</text>
			</view>
			<view class="vc-tags">
				<text class="tag">有效期 {{ card.validityDays || 365 }} 天</text>
				<text class="tag" v-if="storeNames.length">适用门店：{{ storeNames.join('、') }}</text>
			</view>
			<view class="vc-raise-tip" v-if="isRaised && card.stepNum > 0">
				首发优惠递减：每 {{ card.stepNum }} 人涨 ¥{{ formatPrice(card.stepAddPrice) }}<text
					v-if="card.priceCap">，封顶 ¥{{ formatPrice(card.priceCap) }}</text>，早买更划算
			</view>
		</view>

		<!-- 权益详情 -->
		<view class="vc-section">
			<view class="vc-sec-title">权益详情</view>
			<view class="vc-desc">{{ card.benefitDesc || '暂无权益描述' }}</view>
		</view>

		<!-- 实时购买 + 动态涨价进度(仅 showBuyCount=1 展示) -->
		<view class="vc-section" v-if="card.showBuyCount == 1">
			<view class="vc-sec-title">实时购买</view>
			<view class="vc-stat flex_s">
				<view class="stat-col">
					<view class="stat-label">当前购买人数</view>
					<view class="stat-num">{{ buyCount }}</view>
				</view>
				<view class="stat-col stat-col-r" v-if="showRaiseProgress">
					<view class="stat-label">下次涨价目标</view>
					<view class="stat-num">{{ nextTarget }}</view>
				</view>
				<view class="stat-col stat-col-r" v-else-if="isCapped">
					<view class="stat-label">当前状态</view>
					<view class="stat-num stat-capped">已封顶</view>
				</view>
			</view>
			<template v-if="showRaiseProgress">
				<view class="vc-progress">
					<view class="vc-progress-in" :style="'width:' + progressPercent + '%'"></view>
				</view>
				<view class="vc-remain">距下次涨价还剩 <text class="num">{{ remainCount }}</text> 人</view>
			</template>
		</view>

		<!-- 可购买会员卡(该权益卡绑定的专属会员卡,仅上架) -->
		<view class="vc-section" v-if="card.bindFitCards && card.bindFitCards.length">
			<view class="vc-sec-title">可购买会员卡</view>
			<view class="vc-fc-tip" v-if="!card.hasBenefit">购买以下会员卡需先开通本权益卡</view>
			<view class="vc-fc-list">
				<view class="fc-item" v-for="fc in card.bindFitCards" :key="fc.fitCardId" @click="onBuyFitCard(fc)">
					<view class="fc-name">{{ fc.cardName || '--' }}</view>
					<view class="fc-price"><text class="sym">¥</text>{{ formatPrice(fcPrice(fc)) }}</view>
					<view class="fc-unit" v-if="fc.cardType == 10">{{ fc.useCount || 0 }}次</view>
					<view class="fc-unit" v-else>有效期{{ fc.validity || 0 }}天</view>
					<view class="fc-btn">立即抢购</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	import {
		getVipCardDetail,
		buyVipCard
	} from '@/api/index'
	import {
		Wxpay
	} from '@/api/my'

	export default {
		data() {
			return {
				vipCardId: null,
				storeId: '',
				card: {}
			}
		},
		computed: {
			// 当前购买人数(后端已按 showBuyCount 脱敏)
			buyCount() {
				return Number(this.card.soldCount) || 0;
			},
			// 是否开启动态涨价
			raiseEnabled() {
				return Number(this.card.stepNum) > 0 && Number(this.card.stepAddPrice) > 0;
			},
			// 是否已达封顶价(封顶后不再涨价)
			isCapped() {
				const cap = this.card.priceCap;
				if (cap == null || cap === '') return false;
				return Number(this.curPrice) >= Number(cap);
			},
			// 是否展示"下次涨价目标+进度"(开启涨价 且 未封顶)
			showRaiseProgress() {
				return this.raiseEnabled && !this.isCapped;
			},
			// 下次涨价目标人数(对齐后端算法:涨价临界点 = baseBuyCount + k*stepNum)
			nextTarget() {
				const base = Number(this.card.baseBuyCount) || 0;
				const step = Number(this.card.stepNum) || 0;
				if (step <= 0) return 0;
				const n = this.buyCount;
				const tier = n <= base ? 0 : Math.floor((n - base) / step);
				return base + (tier + 1) * step;
			},
			// 距下次涨价还剩人数
			remainCount() {
				const r = this.nextTarget - this.buyCount;
				return r > 0 ? r : 0;
			},
			// 当前档进度百分比(0~100)
			progressPercent() {
				const step = Number(this.card.stepNum) || 0;
				if (step <= 0) return 0;
				const start = this.nextTarget - step;
				let p = ((this.buyCount - start) / step) * 100;
				if (p < 0) p = 0;
				if (p > 100) p = 100;
				return Math.round(p);
			},
			// 展示价:优先后端实时动态价 currentPrice
			curPrice() {
				return this.card.currentPrice != null ? this.card.currentPrice : this.card.price;
			},
			// 当前价高于首发价 → 已触发动态涨价
			isRaised() {
				return Number(this.curPrice) > Number(this.card.price);
			},
			// 适用门店名列表(后端 detail 按 storeAddrIds 查出)
			storeNames() {
				return this.card.storeNames || [];
			}
		},
		onLoad(option) {
			this.vipCardId = option.vipCardId;
			// 入口页(首页)带来的当前门店 storeId,点击绑定会员卡时透传给下单页
			this.storeId = option.storeId || '';
			this.getDetail();
		},
		methods: {
			// 绑定会员卡展示价(详情页不判新人身份,按基础售价 cardPrice)
			fcPrice(fc) {
				return fc.cardPrice != null ? fc.cardPrice : 0;
			},
			// 点击绑定会员卡去购买:未持有效权益先提示;已持则跳下单页
			onBuyFitCard(fc) {
				if (!fc || !fc.fitCardId) return;
				if (!this.card.hasBenefit) {
					this.config.Toast('请先购买权益卡后再购买该会员卡');
					return;
				}
				// id 与 openVip 互斥(card_renewal.vue 内两者是两条门店解析路径:id=精确门店,
				// openVip=1=定位取最近门店;同时传会触发双重请求竞态,后返回的可能覆盖成错误门店的卡列表,
				// 导致这张卡从列表中"消失"),storeId 缺失时才兜底用 openVip=1
				const q = this.storeId ? ('id=' + this.storeId) : 'openVip=1';
				// single=1:下单页"选择会员套餐"只展示这一张绑定卡,不展示该门店其它套餐(否则混在
				// 横向滚动列表里容易被滑出屏幕外,看起来像"没有这张卡")
				this.config.path('/pagesA/card_renewal/card_renewal?' + q + '&fitCardId=' + fc.fitCardId +
					'&single=1');
			},
			getDetail() {
				if (!this.vipCardId) {
					this.config.Toast('缺少权益卡参数');
					return;
				}
				getVipCardDetail({
					vipCardId: this.vipCardId
				}).then((res) => {
					this.card = res.data || {};
					if (this.card.cardName) {
						uni.setNavigationBarTitle({
							title: this.card.cardName
						});
					}
				}).catch((e) => {
					// 下架/不存在等:提示后返回
					this.config.Toast((e && e.message) || '权益卡不存在或已下架');
					setTimeout(() => {
						uni.navigateBack();
					}, 1500);
				});
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			onBuy() {
				if (!this.vipCardId) {
					this.config.Toast('缺少权益卡参数');
					return;
				}
				const that = this;
				// 1) 后端下单(重算动态价、建待支付权益),拿订单号 + 应付金额
				buyVipCard({
					vipCardId: this.vipCardId
				}).then((res) => {
					const orderNo = res.data.orderNo;
					const paySum = res.data.paySum;
					// 2) 复用小程序统一支付 /wx/proPay 取调起参数
					Wxpay({
						orderNo: orderNo,
						paySum: paySum
					}).then((r) => {
						if (r.code == 1) {
							// 3) 调起微信支付(成功后由 /wx/proPayNotify 回调激活权益)
							uni.requestPayment({
								appId: r.params.appId,
								nonceStr: r.params.nonceStr,
								package: r.params.package,
								paySign: r.params.paySign,
								signType: r.params.signType,
								timeStamp: r.params.timeStamp,
								success: () => {
									that.config.Toast('购买成功');
									setTimeout(() => {
										uni.redirectTo({
											url: '/pagesA/my_benefits/my_benefits'
										});
									}, 1000);
								},
								fail: () => {
									that.config.Toast('支付已取消');
								}
							});
						} else {
							that.config.Toast(r.msg || '发起支付失败');
						}
					});
				}).catch((e) => {
					// 卡已下架/未登录等业务码
					that.config.Toast((e && e.message) || '下单失败');
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.vc-detail {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 24rpx 28rpx 0;
		box-sizing: border-box;
	}

	.vc-hero {
		border-radius: 24rpx;
		background: linear-gradient(135deg, #ff8a3d 0%, #E15B00 100%);
		color: rgba(255, 255, 255, 0.9);
		padding: 38rpx 36rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;

		.vc-hero-top {
			align-items: center;
		}

		.vc-kicker {
			display: inline-block;
			padding: 6rpx 20rpx;
			border-radius: 22rpx;
			background: rgba(255, 255, 255, 0.25);
			color: #ffffff;
			font-size: 22rpx;
		}

		.vc-buy-btn-top {
			padding: 18rpx 48rpx;
			border-radius: 36rpx;
			background: #ffffff;
			color: #E15B00;
			font-size: 32rpx;
			font-weight: 900;
			box-shadow: 0rpx 4rpx 12rpx 0rpx rgba(0, 0, 0, 0.15);
		}

		.vc-title {
			margin-top: 24rpx;
			font-size: 44rpx;
			line-height: 54rpx;
			font-weight: 800;
			color: #ffffff;
		}

		.vc-price-row {
			margin-top: 22rpx;
			display: flex;
			align-items: baseline;

			.now {
				font-size: 60rpx;
				font-weight: 900;
				color: #ffffff;
				line-height: 1;

				.sym {
					font-size: 30rpx;
					font-weight: 800;
				}
			}

			.base {
				margin-left: 16rpx;
				font-size: 24rpx;
				color: rgba(255, 255, 255, 0.6);
				text-decoration: line-through;
			}
		}

		.vc-tags {
			margin-top: 22rpx;
			display: flex;
			flex-wrap: wrap;

			.tag {
				max-width: 100%;
				margin: 0 14rpx 12rpx 0;
				padding: 6rpx 18rpx;
				border-radius: 20rpx;
				background: rgba(255, 255, 255, 0.22);
				font-size: 22rpx;
				color: #ffffff;
				word-break: break-all;
				box-sizing: border-box;
			}
		}

		.vc-raise-tip {
			margin-top: 12rpx;
			font-size: 22rpx;
			line-height: 32rpx;
			color: #fff2cc;
		}
	}

	.vc-section {
		background: #ffffff;
		border-radius: 20rpx;
		padding: 28rpx;
		box-sizing: border-box;
		margin-bottom: 24rpx;
		box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.06);

		.vc-sec-title {
			font-size: 32rpx;
			font-weight: 800;
			color: #111111;
			margin-bottom: 18rpx;
		}

		.vc-desc {
			font-size: 26rpx;
			line-height: 42rpx;
			color: #555555;
			white-space: pre-wrap;
			word-break: break-all;
		}

	}

	/* 实时购买统计 + 动态涨价进度(图2) */
	.vc-stat {
		.stat-col {
			.stat-label {
				font-size: 24rpx;
				color: #999999;
			}

			.stat-num {
				margin-top: 10rpx;
				font-size: 48rpx;
				font-weight: 900;
				color: #E15B00;
				line-height: 1;
			}

			&.stat-col-r {
				text-align: right;

				.stat-num {
					color: #333333;
				}

				.stat-capped {
					color: #999999;
					font-size: 32rpx;
				}
			}
		}
	}

	.vc-progress {
		margin-top: 24rpx;
		height: 16rpx;
		border-radius: 8rpx;
		background: #f0f0f0;
		overflow: hidden;

		.vc-progress-in {
			height: 100%;
			border-radius: 8rpx;
			background: linear-gradient(90deg, #ff8a3d 0%, #E15B00 100%);
		}
	}

	.vc-remain {
		margin-top: 16rpx;
		font-size: 24rpx;
		color: #999999;

		.num {
			color: #E15B00;
			font-weight: 800;
			margin: 0 4rpx;
		}
	}

	/* 可购买会员卡(图1) */
	.vc-fc-tip {
		margin-bottom: 20rpx;
		font-size: 24rpx;
		color: #E15B00;
	}

	.vc-fc-list {
		display: flex;
		flex-wrap: wrap;

		.fc-item {
			width: 31%;
			margin: 0 3.5% 20rpx 0;
			padding: 26rpx 0;
			box-sizing: border-box;
			border-radius: 16rpx;
			background: #FBE7D9;
			text-align: center;

			&:nth-child(3n) {
				margin-right: 0;
			}

			.fc-name {
				font-size: 26rpx;
				color: #333333;
				font-weight: 600;
				white-space: nowrap;
				overflow: hidden;
				text-overflow: ellipsis;
				padding: 0 8rpx;
			}

			.fc-price {
				margin: 16rpx 0 12rpx;
				font-size: 44rpx;
				font-weight: 900;
				color: #E15B00;
				line-height: 1;

				.sym {
					font-size: 24rpx;
				}
			}

			.fc-unit {
				font-size: 22rpx;
				color: #999999;
			}

			.fc-btn {
				display: inline-flex;
				align-items: center;
				justify-content: center;
				width: 144rpx;
				height: 46rpx;
				margin: 18rpx auto 0;
				background: #E15B00;
				border-radius: 23rpx;
				font-size: 24rpx;
				color: #FFFFFF;
			}
		}
	}
</style>
