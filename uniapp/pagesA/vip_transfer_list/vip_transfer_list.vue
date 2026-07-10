<template>
	<view class="tl-page">
		<!-- 角色切换 -->
		<view class="tl-tabs">
			<view class="tl-tab" :class="{ 'is-on': role === '' }" @click="switchRole('')">全部</view>
			<view class="tl-tab" :class="{ 'is-on': role === 1 }" @click="switchRole(1)">我发起的</view>
			<view class="tl-tab" :class="{ 'is-on': role === 2 }" @click="switchRole(2)">我接收的</view>
		</view>

		<view v-if="list.length" class="tl-list">
			<view class="tl-card" v-for="(item, idx) in list" :key="item.viewKey">
				<view class="tl-card__head">
					<text class="tl-card__name">{{ item.cardNameText }}</text>
					<text class="tl-card__tag" :class="item.statusClassName">{{ item.statusLabel }}</text>
				</view>
				<view class="tl-row">
					<text class="tl-label">角色</text>
					<text class="tl-val">{{ item.roleLabel }}</text>
				</view>
				<view class="tl-row">
					<text class="tl-label">服务费</text>
					<text class="tl-val">{{ item.feeText }}</text>
				</view>
				<view class="tl-row">
					<text class="tl-label">发起时间</text>
					<text class="tl-val">{{ item.createdDateText || '-' }}</text>
				</view>
				<view class="tl-row" v-if="item.auditRemark">
					<text class="tl-label">审核备注</text>
					<text class="tl-val">{{ item.auditRemark }}</text>
				</view>
				<!-- 操作区:我受让+待确认 → 确认/拒绝;我转出+待审核或待确认 → 撤回(data-idx 传参,勿内联传对象) -->
				<view class="tl-btns" v-if="item.canAccept || item.canWithdraw">
					<view v-if="item.canAccept" class="tl-btn is-primary" :data-idx="idx" @click="onConfirm">确认接收</view>
					<view v-if="item.canAccept" class="tl-btn" :data-idx="idx" @click="onReject">拒绝</view>
					<view v-if="item.canWithdraw" class="tl-btn" :data-idx="idx" @click="onWithdraw">撤回</view>
				</view>
			</view>
		</view>

		<view v-else-if="loaded" class="tl-empty">
			<text class="tl-empty__txt">暂无转让记录</text>
		</view>

		<view v-if="list.length" class="tl-more">{{ noMore ? '没有更多了' : '加载中...' }}</view>
	</view>
</template>

<script>
	import {
		getMyTransferList,
		confirmVipTransfer,
		rejectVipTransfer,
		withdrawVipTransfer
	} from '@/api/index'

	export default {
		data() {
			return {
				list: [],
				role: '',
				page: 1,
				limit: 10,
				total: 0,
				loaded: false,
				noMore: false,
				loading: false,
				// 确认/拒绝/撤回提交中标记(与 loading 分开:loading 是拉列表专用,不能阻止操作请求的重复点击)
				submitting: false,
				myUserId: null
			}
		},
		onLoad() {
			const info = uni.getStorageSync('userinfo') || {};
			this.myUserId = info.userId != null ? Number(info.userId) : null;
			this.loadList(true);
		},
		onPullDownRefresh() {
			this.loadList(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (!this.noMore) {
				this.loadList(false);
			}
		},
		methods: {
			switchRole(role) {
				if (this.role === role) {
					return;
				}
				this.role = role;
				this.loadList(true);
			},
			loadList(reset, done) {
				if (this.loading) {
					done && done();
					return;
				}
				if (reset) {
					this.page = 1;
					this.noMore = false;
				}
				this.loading = true;
				const params = {
					page: this.page,
					limit: this.limit
				};
				if (this.role !== '') {
					params.role = this.role;
				}
				getMyTransferList(params).then((res) => {
					const data = res.data || {};
					const rows = data.list || [];
					const baseIndex = reset ? 0 : this.list.length;
					const normalized = rows.map((item, index) => this.normalize(item, baseIndex + index));
					this.total = data.totalCount || 0;
					this.list = reset ? normalized : this.list.concat(normalized);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) {
						this.page += 1;
					}
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					this.loading = false;
					this.loaded = true;
					this.config.Toast((e && e.message) || '加载失败');
					done && done();
				});
			},
			normalize(item, index) {
				const row = item || {};
				const isFrom = this.myUserId != null && Number(row.fromUserId) === this.myUserId;
				const status = Number(row.status);
				return Object.assign({}, row, {
					viewKey: row.transferId ? String(row.transferId) : 'tf-' + index,
					cardNameText: row.cardName || '权益卡',
					roleLabel: isFrom ? '我转出' : '我受让',
					feeText: this.feeText(row.serviceFee),
					createdDateText: this.formatDateTime(row.createdDate),
					statusLabel: this.statusText(row.status),
					statusClassName: this.statusClass(row.status),
					// 操作资格:我受让+40待确认可 确认/拒绝;我转出+20待审核或40待确认可撤回
					canAccept: !isFrom && status === 40,
					canWithdraw: isFrom && (status === 20 || status === 40)
				});
			},
			// 受让人确认接收(data-idx 取项,规避小程序 v-if 元素内联传参取参错乱)
			onConfirm(e) {
				const item = this.list[Number(e.currentTarget.dataset.idx)];
				if (!item || this.submitting) return;
				const that = this;
				uni.showModal({
					title: '确认接收',
					content: '确认接收该权益转让？确认后权益立即过户到你名下，继承剩余有效期。',
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						confirmVipTransfer({
							transferId: item.transferId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已确认，权益已到账');
							that.loadList(true);
						}).catch((err) => {
							that.submitting = false;
							that.config.Toast((err && err.message) || '操作失败');
						});
					}
				});
			},
			// 受让人拒绝接收
			onReject(e) {
				const item = this.list[Number(e.currentTarget.dataset.idx)];
				if (!item || this.submitting) return;
				const that = this;
				uni.showModal({
					title: '拒绝接收',
					content: '确认拒绝该权益转让？拒绝后转让关闭，服务费原路退回转让人。',
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						rejectVipTransfer({
							transferId: item.transferId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已拒绝');
							that.loadList(true);
						}).catch((err) => {
							that.submitting = false;
							that.config.Toast((err && err.message) || '操作失败');
						});
					}
				});
			},
			// 转让人撤回(20待审核撤回退服务费,40待确认撤回不退,文案区分)
			onWithdraw(e) {
				const item = this.list[Number(e.currentTarget.dataset.idx)];
				if (!item || this.submitting) return;
				const that = this;
				const content = Number(item.status) === 20 ?
					'确认撤回该转让申请？撤回后服务费原路退回。' :
					'确认撤回该转让？审核已通过，撤回后服务费不予退还。';
				uni.showModal({
					title: '撤回转让',
					content: content,
					success: (r) => {
						if (!r.confirm) return;
						that.submitting = true;
						withdrawVipTransfer({
							transferId: item.transferId
						}).then(() => {
							that.submitting = false;
							that.config.Toast('已撤回');
							that.loadList(true);
						}).catch((err) => {
							that.submitting = false;
							that.config.Toast((err && err.message) || '操作失败');
						});
					}
				});
			},
			feeText(v) {
				const n = Number(v);
				if (isNaN(n) || n <= 0) return '免费';
				return '¥' + (Number.isInteger(n) ? String(n) : n.toFixed(2));
			},
			// 后端 FastJSON 把 Date 序列化成毫秒时间戳数字,兼容 数字/字符串 → 'YYYY-MM-DD HH:mm'
			formatDateTime(v) {
				if (!v) return '';
				const t = typeof v === 'number' ? v : new Date(String(v).replace(/-/g, '/')).getTime();
				if (!t || isNaN(t)) return '';
				const d = new Date(t);
				const p = (n) => (n < 10 ? '0' + n : '' + n);
				return d.getFullYear() + '-' + p(d.getMonth() + 1) + '-' + p(d.getDate()) +
					' ' + p(d.getHours()) + ':' + p(d.getMinutes());
			},
			statusText(status) {
				const map = {
					10: '待付费',
					20: '待审核',
					31: '已驳回',
					40: '待确认',
					51: '已拒绝',
					52: '已超时',
					60: '已撤回',
					70: '已生效'
				};
				return map[status] || '未知';
			},
			statusClass(status) {
				if (status === 70) return 'is-done';
				if (status === 10 || status === 20 || status === 40) return 'is-on';
				return 'is-gray';
			}
		}
	}
</script>

<style lang="scss" scoped>
	page {
		background: #F4F4F4;
	}

	.tl-page {
		min-height: 100vh;
		background: #F4F4F4;
		padding: 0 24rpx 20rpx;
	}

	.tl-tabs {
		display: flex;
		background: #FFFFFF;
		border-radius: 16rpx;
		margin: 20rpx 0;
		padding: 8rpx;
	}

	.tl-tab {
		flex: 1;
		text-align: center;
		padding: 16rpx 0;
		font-size: 26rpx;
		color: #666;
		border-radius: 12rpx;
	}

	.tl-tab.is-on {
		background: #FBF0DC;
		color: #C8923B;
		font-weight: 600;
	}

	.tl-card {
		background: #FFFFFF;
		border-radius: 16rpx;
		padding: 28rpx 28rpx 12rpx;
		margin-bottom: 20rpx;
		box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.04);
	}

	.tl-card__head {
		display: flex;
		justify-content: space-between;
		align-items: center;
		margin-bottom: 18rpx;
	}

	.tl-card__name {
		font-size: 32rpx;
		font-weight: 600;
		color: #222;
	}

	.tl-card__tag {
		font-size: 22rpx;
		padding: 4rpx 16rpx;
		border-radius: 100rpx;
	}

	.tl-card__tag.is-on {
		color: #E8541E;
		background: #FDEDE4;
	}

	.tl-card__tag.is-done {
		color: #2BA471;
		background: #E6F4EC;
	}

	.tl-card__tag.is-gray {
		color: #999;
		background: #EEE;
	}

	.tl-row {
		display: flex;
		justify-content: space-between;
		padding: 10rpx 0;
		font-size: 26rpx;
	}

	.tl-btns {
		display: flex;
		justify-content: flex-end;
		padding: 16rpx 0;
		border-top: 1rpx solid #F2F2F2;
		margin-top: 8rpx;
	}

	.tl-btn {
		padding: 12rpx 36rpx;
		margin-left: 20rpx;
		border: 1rpx solid #C8923B;
		color: #C8923B;
		font-size: 26rpx;
		border-radius: 100rpx;
	}

	.tl-btn.is-primary {
		background: #C8923B;
		color: #FFF;
	}

	.tl-label {
		color: #999;
	}

	.tl-val {
		color: #333;
	}

	.tl-empty {
		padding-top: 200rpx;
		text-align: center;
	}

	.tl-empty__txt {
		color: #999;
		font-size: 28rpx;
	}

	.tl-more {
		text-align: center;
		color: #BBB;
		font-size: 24rpx;
		padding: 20rpx 0 40rpx;
	}
</style>
