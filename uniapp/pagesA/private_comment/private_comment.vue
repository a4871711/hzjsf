<template>
	<view class="comment-page">
		<view class="form-card" v-if="appointmentId">
			<view class="form-title">评价 {{ coachName }}</view>
			<view class="form-subtitle">{{ appointmentDate }} 已完成课程</view>
			<view class="score-row">
				<text>课程评分</text>
				<u-rate v-model="score" activeColor="#E15B00" inactiveColor="#DDDDDD" size="28"></u-rate>
				<text>{{ score }}分</text>
			</view>
			<textarea class="textarea" v-model="content" maxlength="500" placeholder="说说教练的专业度和上课体验（选填）"></textarea>
			<view class="count">{{ content.length }}/500</view>
			<view class="submit" :class="{ disabled: submitting }" @click="submit">{{ submitting ? '提交中...' : '提交评价' }}</view>
		</view>

		<view class="list-title">我的评价</view>
		<view class="list" v-if="list.length">
			<view class="card" v-for="item in list" :key="item.id">
				<view class="head">
					<view class="coach">{{ item.coachName || '教练' }}</view>
					<u-rate :value="Number(item.score || 0)" readonly activeColor="#E15B00" size="18"></u-rate>
				</view>
				<view class="meta">{{ item.storeName || '--' }} · {{ item.appointmentDate || '--' }} {{ item.startTime || '' }}</view>
				<view class="content">{{ item.commentContent || '未填写文字评价' }}</view>
				<view class="time">{{ item.commentTime || '' }}</view>
				<view class="reply" v-if="item.replyContent">
					<text>门店回复</text>
					<view>{{ item.replyContent }}</view>
					<view class="reply-time">{{ item.replyTime || '' }}</view>
				</view>
			</view>
		</view>
		<view class="empty" v-else-if="loaded">还没有评价记录</view>
		<view class="more" v-if="list.length">{{ noMore ? '已经到底了' : '上拉加载更多' }}</view>
	</view>
</template>

<script>
	import { submitPrivateComment, getPrivateComments } from '@/api/private-training.js'

	export default {
		data() {
			return {
				appointmentId: '',
				coachName: '教练',
				appointmentDate: '',
				score: 5,
				content: '',
				submitting: false,
				list: [],
				page: 1,
				limit: 10,
				total: 0,
				loaded: false,
				loading: false,
				noMore: false
			}
		},
		onLoad(options) {
			this.appointmentId = options.appointmentId || '';
			this.coachName = decodeURIComponent(options.coachName || '教练');
			this.appointmentDate = decodeURIComponent(options.appointmentDate || '');
		},
		onShow() {
			this.loadList(true);
		},
		onPullDownRefresh() {
			this.loadList(true, () => uni.stopPullDownRefresh());
		},
		onReachBottom() {
			if (!this.noMore) this.loadList(false);
		},
		methods: {
			submit() {
				if (this.submitting) return;
				if (Number(this.score) < 1 || Number(this.score) > 5) {
					this.config.Toast('请选择1-5星评分');
					return;
				}
				this.submitting = true;
				submitPrivateComment({
					appointmentId: this.appointmentId,
					score: this.score,
					commentContent: this.content.trim()
				}).then(() => {
					this.submitting = false;
					this.config.Toast('评价已提交');
					this.appointmentId = '';
					this.content = '';
					this.loadList(true);
				}).catch((e) => {
					this.submitting = false;
					this.config.Toast((e && e.message) || '评价提交失败');
				});
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
				getPrivateComments({ page: this.page, limit: this.limit }).then((res) => {
					const data = res.data || {};
					const rows = data.list || [];
					this.total = Number(data.totalCount || 0);
					this.list = reset ? rows : this.list.concat(rows);
					this.noMore = this.list.length >= this.total;
					if (!this.noMore) this.page += 1;
					this.loaded = true;
					this.loading = false;
					done && done();
				}).catch((e) => {
					this.loaded = true;
					this.loading = false;
					this.config.Toast((e && e.message) || '评价加载失败');
					done && done();
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.comment-page { min-height: 100vh; padding: 24rpx; background: #F4F4F4; box-sizing: border-box; }
	.form-card, .card { padding: 28rpx; background: #FFF; border-radius: 20rpx; }
	.form-title { color: #222; font-size: 34rpx; font-weight: 900; }
	.form-subtitle { margin-top: 10rpx; color: #999; font-size: 23rpx; }
	.score-row { display: flex; margin-top: 28rpx; align-items: center; }
	.score-row > text:first-child { margin-right: 24rpx; color: #555; font-size: 26rpx; }
	.score-row > text:last-child { margin-left: auto; color: #E15B00; font-size: 24rpx; }
	.textarea { width: 100%; height: 220rpx; margin-top: 26rpx; padding: 20rpx; color: #333; background: #F7F7F7; border-radius: 14rpx; font-size: 25rpx; line-height: 40rpx; box-sizing: border-box; }
	.count { margin-top: -38rpx; padding-right: 16rpx; color: #AAA; text-align: right; font-size: 20rpx; }
	.submit { height: 80rpx; margin-top: 32rpx; display: flex; align-items: center; justify-content: center; color: #FFF; background: #E15B00; border-radius: 40rpx; font-size: 28rpx; font-weight: 800; }
	.submit.disabled { opacity: .5; }
	.list-title { margin: 34rpx 6rpx 18rpx; color: #222; font-size: 30rpx; font-weight: 900; }
	.card { margin-bottom: 20rpx; }
	.head { display: flex; align-items: center; }
	.coach { flex: 1; color: #222; font-size: 30rpx; font-weight: 800; }
	.meta, .time { margin-top: 10rpx; color: #999; font-size: 21rpx; }
	.content { margin-top: 20rpx; color: #555; font-size: 25rpx; line-height: 42rpx; white-space: pre-wrap; }
	.reply { margin-top: 20rpx; padding: 20rpx; color: #555; background: #FFF7F1; border-radius: 12rpx; font-size: 23rpx; line-height: 38rpx; }
	.reply > text { color: #E15B00; font-weight: 700; }
	.reply-time { color: #AAA; text-align: right; font-size: 20rpx; }
	.empty { padding: 130rpx 0; color: #999; text-align: center; font-size: 25rpx; }
	.more { padding: 14rpx; color: #AAA; text-align: center; font-size: 22rpx; }
</style>
