<template>
	<view class="flash-sale">
		<!-- 标题行:⚡限时秒杀 + 距开抢/距结束 + HH:MM:SS 倒计时 + 更多 -->
		<view class="fs-head">
			<view class="fs-head-l">
				<text class="fs-title">⚡ 限时秒杀</text>
				<text class="fs-count-label" v-if="countLabel">{{ countLabel }}</text>
				<view class="fs-timer" v-if="showTimer">
					<text class="fs-block">{{ hh }}</text>
					<text class="fs-colon">:</text>
					<text class="fs-block">{{ mm }}</text>
					<text class="fs-colon">:</text>
					<text class="fs-block">{{ ss }}</text>
				</view>
			</view>
			<view class="fs-more" @click="onMore">更多 ›</view>
		</view>

		<!-- 商品横排,每卡=一个秒杀商品 -->
		<scroll-view class="fs-scroll" scroll-x="true">
			<view class="fs-item"
				:class="{ 'fs-item--preheat': item.status === 'preheat', 'fs-item--soldout': item.status === 'soldout' }"
				v-for="(item, index) in list" :key="index">
				<!-- 预热角标 -->
				<view class="fs-corner" v-if="item.status === 'preheat'">预热</view>
				<!-- 封面 -->
				<image class="fs-cover" :src="item.coverUrl" mode="aspectFill" v-if="item.coverUrl"></image>
				<view class="fs-name">{{ item.productName || '--' }}</view>
				<!-- 价格:秒杀价大号橙 + 划线原价 -->
				<view class="fs-price-row">
					<text class="fs-flash"><text class="fs-sym">¥</text>{{ formatPrice(item.flashSalePrice) }}</text>
				</view>
				<view class="fs-origin" v-if="item.originPrice != null">¥{{ formatPrice(item.originPrice) }}</view>
				<!-- 已售进度条(设计:已售 80%);预热态未开抢不展示 -->
				<view class="fs-progress" v-if="item.status !== 'preheat'">
					<view class="fs-progress-bar">
						<view class="fs-progress-fill" :style="{ width: (item.soldPct || 0) + '%' }"></view>
					</view>
					<text class="fs-progress-txt">已售{{ item.soldPct || 0 }}%</text>
				</view>
				<!-- 按钮:三态(data-idx 取参规避 mp-weixin v-for 内 item 丢失) -->
				<view class="fs-btn" :class="{ 'fs-btn--disabled': item.status !== 'ongoing' }" :data-idx="index"
					@click="onBtn">{{ btnText(item) }}</view>
			</view>
		</scroll-view>
	</view>
</template>

<script>
	export default {
		name: "flash-sale-card",
		props: {
			// 秒杀商品数组(见接口契约),空数组时父级不渲染本组件
			list: {
				type: Array,
				default: () => []
			}
		},
		data() {
			return {
				remainSec: 0, // 当前倒计时剩余秒数(驱动 HH:MM:SS)
				timer: null
			};
		},
		computed: {
			// 驱动头部倒计时的"主商品":优先进行中(倒计到结束),其次预热(倒计到开抢)
			primary() {
				if (!this.list || !this.list.length) return null;
				return this.list.find((i) => i.status === 'ongoing') ||
					this.list.find((i) => i.status === 'preheat') ||
					this.list[0];
			},
			countLabel() {
				const p = this.primary;
				if (!p) return '';
				if (p.status === 'preheat') return '距开抢';
				if (p.status === 'ongoing') return '距结束';
				return '';
			},
			// 进行中始终倒计「距结束」;预热态由后端按 countdownEnabled 预热窗控制是否下发,到这里直接倒计「距开抢」
			showTimer() {
				const p = this.primary;
				if (!p) return false;
				return (p.status === 'preheat' || p.status === 'ongoing') && this.remainSec > 0;
			},
			hh() {
				return this.pad(Math.floor(this.remainSec / 3600));
			},
			mm() {
				return this.pad(Math.floor((this.remainSec % 3600) / 60));
			},
			ss() {
				return this.pad(this.remainSec % 60);
			}
		},
		watch: {
			// 父级刷新(onShow 重新拉取)后重置倒计时基准
			list: {
				handler() {
					this.initTimer();
				},
				immediate: true
			}
		},
		beforeDestroy() {
			this.clearTimer();
		},
		methods: {
			pad(n) {
				n = Number(n) || 0;
				return n < 10 ? '0' + n : String(n);
			},
			formatPrice(v) {
				const n = Number(v);
				if (isNaN(n)) return '0';
				return Number.isInteger(n) ? String(n) : n.toFixed(2);
			},
			btnText(item) {
				if (!item) return '';
				if (item.status === 'preheat') return '即将开始';
				if (item.status === 'soldout') return '已抢光';
				return '立即抢购';
			},
			// 'yyyy-MM-dd HH:mm:ss' → 毫秒时间戳(mp-weixin/iOS 对 '-' 解析不稳,统一替换为 '/')
			parseTime(s) {
				if (!s) return 0;
				return new Date(String(s).replace(/-/g, '/')).getTime();
			},
			clearTimer() {
				if (this.timer) {
					clearInterval(this.timer);
					this.timer = null;
				}
			},
			// 以 serverTime 为基准 + 本地时钟增量递减,规避 setInterval 漂移与本机时间不准
			initTimer() {
				this.clearTimer();
				const p = this.primary;
				if (!p) {
					this.remainSec = 0;
					return;
				}
				const base = this.parseTime(p.serverTime);
				const target = p.status === 'preheat' ? this.parseTime(p.startTime) : this.parseTime(p.endTime);
				const localStart = Date.now();
				const compute = () => {
					const cur = base + (Date.now() - localStart);
					const sec = Math.floor((target - cur) / 1000);
					return sec > 0 ? sec : 0;
				};
				this.remainSec = compute();
				// 初始即 0(数据本身已过期)不起 timer 也不通知,避免刷新死循环
				if (this.remainSec <= 0) return;
				this.timer = setInterval(() => {
					const sec = compute();
					this.remainSec = sec;
					if (sec <= 0) {
						this.clearTimer();
						// 归零=预热转开抢/进行中结束,通知父级重拉最新状态
						this.$emit('expired');
					}
				}, 1000);
			},
			onMore() {
				this.$emit('more');
			},
			// data-idx 取参:v-for 内直接 @click="fn(item)" 在 mp-weixin 会拿到 undefined
			onBtn(e) {
				const idx = e && e.currentTarget && e.currentTarget.dataset ? e.currentTarget.dataset.idx : -1;
				const item = this.list[Number(idx)];
				if (!item) return;
				// 只有进行中可抢购;预热/售罄按钮置灰不响应
				if (item.status !== 'ongoing') return;
				this.$emit('buy', item);
			}
		}
	};
</script>

<style lang="scss" scoped>
	.flash-sale {
		width: 100%;

		.fs-head {
			display: flex;
			align-items: center;
			justify-content: space-between;
			margin-bottom: 24rpx;

			.fs-head-l {
				display: flex;
				align-items: center;
				flex: 1;
				min-width: 0;

				.fs-title {
					font-size: 32rpx;
					font-weight: 800;
					color: #FF3D00;
				}

				.fs-count-label {
					margin-left: 16rpx;
					font-size: 24rpx;
					color: #666666;
				}

				.fs-timer {
					display: flex;
					align-items: center;
					margin-left: 12rpx;

					.fs-block {
						min-width: 36rpx;
						height: 36rpx;
						line-height: 36rpx;
						padding: 0 6rpx;
						box-sizing: border-box;
						text-align: center;
						background: #2b2b2b;
						color: #ffffff;
						font-size: 24rpx;
						border-radius: 6rpx;
					}

					.fs-colon {
						color: #2b2b2b;
						font-size: 24rpx;
						font-weight: 700;
						margin: 0 4rpx;
					}
				}
			}

			.fs-more {
				flex-shrink: 0;
				margin-left: 12rpx;
				font-size: 24rpx;
				color: #999999;
			}
		}

		.fs-scroll {
			white-space: nowrap;
			width: 100%;

			.fs-item {
				display: inline-block;
				vertical-align: top;
				width: 220rpx;
				box-sizing: border-box;
				margin-right: 18rpx;
				padding: 20rpx;
				border-radius: 20rpx;
				background: linear-gradient(180deg, #FFF1EA 0%, #FFE3D4 100%);
				border: 2rpx solid #FFB58F;
				position: relative;
				text-align: center;

				&:last-child {
					margin-right: 0;
				}

				// 预热态:整体略灰
				&.fs-item--preheat {
					opacity: 0.72;
				}

				// 售罄态:置灰
				&.fs-item--soldout {
					background: #f2f2f2;
					border-color: #e0e0e0;
				}

				.fs-corner {
					position: absolute;
					top: 0;
					left: 0;
					padding: 4rpx 16rpx;
					border-radius: 20rpx 0 20rpx 0;
					background: #FF9800;
					color: #ffffff;
					font-size: 20rpx;
				}

				.fs-cover {
					width: 100%;
					height: 160rpx;
					border-radius: 12rpx;
					margin-bottom: 12rpx;
				}

				.fs-name {
					font-size: 26rpx;
					font-weight: 600;
					color: #333333;
					white-space: nowrap;
					overflow: hidden;
					text-overflow: ellipsis;
				}

				.fs-price-row {
					margin-top: 14rpx;

					.fs-flash {
						font-size: 44rpx;
						font-weight: 900;
						color: #FF3D00;
						line-height: 1;

						.fs-sym {
							font-size: 26rpx;
							font-weight: 800;
						}
					}
				}

				.fs-origin {
					margin-top: 6rpx;
					font-size: 22rpx;
					color: #999999;
					text-decoration: line-through;
				}

				.fs-btn {
					margin: 18rpx auto 0;
					height: 52rpx;
					line-height: 52rpx;
					border-radius: 26rpx;
					background: #FF3D00;
					color: #ffffff;
					font-size: 24rpx;
					font-weight: 700;

					&.fs-btn--disabled {
						background: #cccccc;
						color: #ffffff;
					}
				}
			}
		}
	}
</style>
