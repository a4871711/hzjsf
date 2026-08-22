<template>
	<view class="open-code">
		<view class="state-card" v-if="loaded && !granted">
			<view class="state-title">{{ deniedTitle }}</view>
			<view class="state-message">{{ message }}</view>
			<view class="appointment-info" v-if="appointment">
				<view>{{ appointment.productName || '私教课程' }}</view>
				<view>{{ appointment.date }} {{ appointment.startTime }}-{{ appointment.endTime }}</view>
				<view>{{ appointment.storeName || '--' }} · {{ appointment.coachName || '--' }}</view>
			</view>
			<view class="next-time" v-if="nextAccessTime">开门码开放时间：{{ nextAccessTime }}</view>
			<view class="state-btn" v-if="reason === 'APPOINTMENT_REQUIRED'" @click="goBenefits">请先预约课程</view>
			<view class="state-btn" v-else-if="reason === 'APPOINTMENT_TOO_EARLY' || reason === 'APPOINTMENT_WINDOW_EXPIRED'" @click="goAppointments">查看我的预约</view>
			<view class="state-btn" v-else-if="reason === 'MEMBERSHIP_REQUIRED'" @click="goBuyCard">立即购卡</view>
		</view>

		<view class="appointment-mode" v-if="granted && accessMode === 'APPOINTMENT'">
			<view class="mode-title">预约开门码</view>
			<view class="appointment-card">
				<view class="course-name">{{ appointment.productName || '私教课程' }}</view>
				<view>{{ appointment.date }} {{ appointment.startTime }}-{{ appointment.endTime }}</view>
				<view>{{ appointment.storeName || '--' }} · {{ appointment.coachName || '--' }}</view>
				<view class="access-window">开门有效期：{{ appointment.accessStart }} 至 {{ appointment.accessEnd }}</view>
			</view>
			<view class="code-img">
				<ikun-qrcode v-if="qrcodeUrl" width="400" height="400" unit="rpx" color="#000000" :data="qrcodeUrl"></ikun-qrcode>
			</view>
			<view class="tips">
				<view>二维码4秒刷新一次</view>
				<view>仅限本次预约门店使用，请勿分享</view>
			</view>
		</view>

		<view class="membership-mode" v-if="granted && accessMode === 'MEMBERSHIP'">
			<view class="monthly vip-box" v-if="Number(codeInfo.data.type) !== 10">
				<view class="code-img">
					<ikun-qrcode v-if="qrcodeUrl" width="400" height="400" unit="rpx" color="#000000" :data="qrcodeUrl"></ikun-qrcode>
				</view>
				<view class="tips">
					<view>二维码4秒刷新一次</view>
					<view>请打开页面后迅速放置在开门器前</view>
					<view>二维码不可分享</view>
				</view>
			</view>

			<view class="count-card vip-box" v-else>
				<view class="tips top-tips">
					<view>二维码4秒刷新一次</view>
					<view>请打开页面后迅速放置在开门器前</view>
					<view>二维码不可分享</view>
				</view>
				<view class="masked-code" v-if="isEffective">
					<image src="/static/code1.png" class="placeholder"></image>
					<view class="show-qrcode" @click="handleShowQrcode">展示二维码</view>
				</view>
				<view class="code-img" v-else>
					<ikun-qrcode v-if="qrcodeUrl" width="400" height="400" unit="rpx" color="#000000" :data="qrcodeUrl"></ikun-qrcode>
				</view>
				<view class="time-box">
					<view class="tips" v-if="isEffective">
						<view>展示二维码后，次卡剩余次数减1</view>
						<view>展示二维码后120分钟内可重复进出</view>
					</view>
					<template v-else>
						<view>亮码时间：{{ codeInfo.createtime }}</view>
						<view>当前二维码有效期至：{{ codeInfo.lasttime }}</view>
					</template>
				</view>
				<view class="card-summary">
					<view>次卡剩余次数：{{ Number(codeInfo.data.useCount || 0) - Number(codeInfo.data.usedCount || 0) }}次</view>
					<view>有效期至：{{ codeInfo.data.validityDate || '--' }}</view>
				</view>
			</view>
		</view>

		<view class="loading" v-if="!loaded">开门码加载中...</view>
	</view>
</template>

<script>
	import { getDoorAccessQR } from '@/api/my.js'

	export default {
		data() {
			return {
				loaded: false,
				granted: false,
				accessMode: '',
				reason: '',
				message: '',
				nextAccessTime: '',
				appointment: null,
				qrcodeUrl: '',
				codeInfo: { data: {} },
				isEffective: true,
				intervalId: null,
				requesting: false
			}
		},
		computed: {
			deniedTitle() {
				return ({
					CARD_PAUSED: '会员卡暂停中',
					MEMBERSHIP_REQUIRED: '需要会籍卡',
					APPOINTMENT_REQUIRED: '暂无可用预约',
					APPOINTMENT_TOO_EARLY: '尚未到开放时间',
					APPOINTMENT_WINDOW_EXPIRED: '预约开门已失效'
				})[this.reason] || '暂不可开门';
			}
		},
		onLoad() {
			this.loadDoorAccess();
		},
		onUnload() {
			this.clearInterval();
		},
		methods: {
			handleShowQrcode() {
				if (Number(this.codeInfo.data.useCount || 0) <= Number(this.codeInfo.data.usedCount || 0)) {
					this.config.Toast('没有可用次数');
					return;
				}
				this.isEffective = false;
				this.ensureInterval();
			},
			ensureInterval() {
				if (this.intervalId) return;
				this.intervalId = setInterval(() => this.loadDoorAccess(), 4000);
			},
			clearInterval() {
				if (!this.intervalId) return;
				clearInterval(this.intervalId);
				this.intervalId = null;
			},
			loadDoorAccess() {
				if (this.requesting) return;
				this.requesting = true;
				getDoorAccessQR({
					userLat: this.$store.state.latilongi.latitude,
					userLng: this.$store.state.latilongi.longitude
				}).then((res) => {
					this.requesting = false;
					this.loaded = true;
					if (Number(res.code) !== 1) {
						this.applyDenied(res);
						return;
					}
					const previousMode = this.accessMode;
					this.granted = true;
					this.reason = '';
					this.message = '';
					this.nextAccessTime = '';
					this.accessMode = res.accessMode || 'MEMBERSHIP';
					this.qrcodeUrl = res.qrCode || '';
					this.codeInfo = Object.assign({ data: {} }, res);
					this.appointment = res.appointment || (this.accessMode === 'APPOINTMENT' ? res.data : null);
					if (this.accessMode === 'APPOINTMENT') {
						this.isEffective = false;
						this.ensureInterval();
						return;
					}
					if (this.codeInfo.data.validityDate) {
						this.codeInfo.data.validityDate = this.config.timestampToDateTime(this.codeInfo.data.validityDate, 'date');
					}
					if (Number(this.codeInfo.data.type) === 10) {
						if (previousMode !== 'MEMBERSHIP') this.isEffective = true;
						if (!this.isEffective) this.ensureInterval();
					} else {
						this.isEffective = false;
						this.ensureInterval();
					}
				}).catch((e) => {
					this.requesting = false;
					this.loaded = true;
					this.applyDenied({ message: (e && e.message) || '开门码加载失败' });
				});
			},
			applyDenied(res) {
				this.granted = false;
				this.accessMode = '';
				this.qrcodeUrl = '';
				this.reason = res.reason || '';
				this.message = res.msg || res.message || '暂不可使用开门码';
				this.nextAccessTime = res.nextAccessTime || '';
				this.appointment = res.appointment || null;
				if (this.reason === 'APPOINTMENT_TOO_EARLY') this.ensureInterval();
				else this.clearInterval();
			},
			goBenefits() {
				uni.navigateTo({ url: '/pagesA/private_benefit/private_benefit' });
			},
			goAppointments() {
				uni.navigateTo({ url: '/pagesA/private_appointment/private_appointment' });
			},
			goBuyCard() {
				uni.navigateTo({ url: '/pagesA/card_renewal/card_renewal?openVip=1' });
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.open-code { min-height: 100vh; padding: 32rpx 28rpx 60rpx; box-sizing: border-box; }
	.loading { padding-top: 45vh; color: #999; text-align: center; font-size: 26rpx; }
	.state-card { margin-top: 24vh; padding: 48rpx 36rpx; background: #FFF; border-radius: 24rpx; text-align: center; }
	.state-title { color: #222; font-size: 36rpx; font-weight: 900; }
	.state-message { margin-top: 18rpx; color: #777; font-size: 26rpx; line-height: 42rpx; }
	.appointment-info { margin-top: 30rpx; padding: 24rpx; color: #555; background: #FFF7F1; border-radius: 16rpx; font-size: 24rpx; line-height: 42rpx; }
	.next-time { margin-top: 24rpx; color: #E15B00; font-size: 25rpx; font-weight: 700; }
	.state-btn { width: 300rpx; height: 76rpx; margin: 34rpx auto 0; display: flex; align-items: center; justify-content: center; color: #FFF; background: #373838; border-radius: 40rpx; font-size: 27rpx; }
	.mode-title { margin: 24rpx 0; color: #222; text-align: center; font-size: 34rpx; font-weight: 900; }
	.appointment-card { padding: 26rpx 28rpx; color: #666; background: #FFF; border-radius: 20rpx; font-size: 24rpx; line-height: 42rpx; }
	.course-name { color: #222; font-size: 31rpx; font-weight: 800; }
	.access-window { margin-top: 12rpx; color: #E15B00; font-weight: 700; }
	.code-img { min-height: 470rpx; display: flex; align-items: center; justify-content: center; }
	.appointment-mode .code-img { margin-top: 30rpx; background: #FFF; border-radius: 20rpx; }
	.tips { color: #999; text-align: center; font-size: 25rpx; line-height: 40rpx; }
	.monthly .code-img { margin-top: 180rpx; }
	.vip-box { overflow: hidden; background: #FFF; border-radius: 20rpx; }
	.count-card { margin-top: 70rpx; }
	.top-tips { padding: 26rpx; background: #F4F4F4; }
	.masked-code { position: relative; height: 500rpx; display: flex; align-items: center; justify-content: center; }
	.placeholder { width: 400rpx; height: 400rpx; filter: grayscale(100%); }
	.show-qrcode { position: absolute; left: 50%; top: 50%; width: 270rpx; height: 90rpx; display: flex; align-items: center; justify-content: center; transform: translate(-50%, -50%); color: #FFF; background: #373838; border-radius: 46rpx; font-size: 29rpx; }
	.time-box { padding: 0 30rpx 30rpx; color: #555; text-align: center; font-size: 24rpx; line-height: 42rpx; }
	.card-summary { padding: 24rpx 42rpx; color: #FFF; background: #000; font-size: 24rpx; line-height: 38rpx; }
</style>
