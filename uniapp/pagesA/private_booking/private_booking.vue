<template>
	<view class="booking-page">
		<view class="course-card">
			<view class="course-name">{{ productName }}</view>
			<view class="course-tip">每次预约消耗1节课，本商品每人每日最多可上 {{ dailyLessonLimit }} 节。
			</view>
		</view>

		<view class="section">
			<view class="section-title">选择上课信息</view>
			<picker :range="coaches" range-key="coachName" :value="coachIndex" @change="onCoachChange">
				<view class="picker-row"><text>教练</text><view>{{ selectedCoach ? selectedCoach.coachName : '暂无可约教练' }} ›</view></view>
			</picker>
			<picker :range="stores" range-key="storeName" :value="storeIndex"
				:disabled="loadingStores || !selectedCoach || !stores.length" @change="onStoreChange">
				<view class="picker-row"><text>上课门店</text><view>{{ storePickerText }}</view></view>
			</picker>
			<picker mode="date" :value="date" :start="today" :end="maxDate" @change="onDateChange">
				<view class="picker-row no-border"><text>上课日期</text><view>{{ date }} ›</view></view>
			</picker>
		</view>

		<view class="section">
			<view class="section-title">选择可约时段</view>
			<view class="slot-tip">时段余量由后端按教练排班和已预约人数实时计算。</view>
			<view class="slots" v-if="slots.length">
				<view class="slot" :class="{ active: slotIndex === index }" v-for="(item,index) in slots" :key="index"
					@click="slotIndex = index">
					<text>{{ item.startTime }}-{{ item.endTime }}</text>
					<text>余 {{ item.remaining }} 个名额</text>
				</view>
			</view>
			<view class="slot-empty" v-else-if="!loadingSlots">当日暂无可约时段，请更换教练、门店或日期</view>
			<view class="slot-empty" v-else>正在查询可约时段...</view>
		</view>

		<view class="submit" :class="{ disabled: submitting || !selectedSlot }" @click="confirmBook">
			{{ submitting ? '提交中...' : '确认预约' }}
		</view>
	</view>
</template>

<script>
	import {
		getPrivateCoaches,
		getPrivateAppointmentStores,
		getPrivateSlots,
		bookPrivateAppointment
	} from '@/api/private-training.js'

	export default {
		data() {
			return {
				benefitId: '',
				productId: '',
				preferredStoreId: '',
				productName: '私教课程',
				dailyLessonLimit: 1,
				coaches: [],
				coachIndex: 0,
				stores: [],
				storeIndex: 0,
				date: '',
				today: '',
				maxDate: '',
				slots: [],
				slotIndex: -1,
				loadingStores: false,
				loadingSlots: false,
				storeRequestSeq: 0,
				slotRequestSeq: 0,
				submitting: false
			}
		},
		computed: {
			selectedCoach() {
				return this.coaches[this.coachIndex] || null;
			},
			selectedStore() {
				return this.stores[this.storeIndex] || null;
			},
			selectedSlot() {
				return this.slots[this.slotIndex] || null;
			},
			storePickerText() {
				if (this.loadingStores) return '正在加载门店...';
				return this.selectedStore ? this.selectedStore.storeName + ' ›' : '暂无可用门店';
			}
		},
		onLoad(options) {
			this.benefitId = options.benefitId || '';
			this.productId = options.productId || '';
			this.preferredStoreId = options.storeId || '';
			this.productName = decodeURIComponent(options.productName || '私教课程');
			this.dailyLessonLimit = Number(options.dailyLessonLimit || 1);
			this.today = this.formatDate(new Date());
			this.date = this.today;
			const max = new Date();
			max.setDate(max.getDate() + 90);
			this.maxDate = this.formatDate(max);
			this.loadOptions();
		},
		methods: {
			loadOptions() {
				if (!this.benefitId || !this.productId) {
					this.config.Toast('权益信息不完整');
					return;
				}
				uni.showLoading({ title: '加载中' });
				getPrivateCoaches({ benefitId: this.benefitId }).then((res) => {
					this.coaches = res.data || [];
					this.coachIndex = 0;
					uni.hideLoading();
					if (!this.selectedCoach) {
						this.clearStoresAndSlots();
						return;
					}
					this.loadStores(this.preferredStoreId);
				}).catch((e) => {
					uni.hideLoading();
					this.coaches = [];
					this.clearStoresAndSlots();
					this.config.Toast((e && e.message) || '可约信息加载失败');
				});
			},
			onCoachChange(e) {
				const previousStoreId = this.selectedStore ? this.selectedStore.storeId : '';
				this.coachIndex = Number(e.detail.value || 0);
				this.loadStores(previousStoreId);
			},
			onStoreChange(e) {
				this.storeIndex = Number(e.detail.value || 0);
				this.loadSlots();
			},
			onDateChange(e) {
				this.date = e.detail.value;
				this.loadSlots();
			},
			clearStoresAndSlots() {
				this.storeRequestSeq += 1;
				this.slotRequestSeq += 1;
				this.stores = [];
				this.storeIndex = 0;
				this.slots = [];
				this.slotIndex = -1;
				this.loadingStores = false;
				this.loadingSlots = false;
			},
			loadStores(preferredStoreId) {
				const coach = this.selectedCoach;
				this.slotRequestSeq += 1;
				this.slots = [];
				this.slotIndex = -1;
				this.loadingSlots = false;
				this.stores = [];
				this.storeIndex = 0;
				if (!coach) {
					this.loadingStores = false;
					return;
				}
				const requestSeq = ++this.storeRequestSeq;
				this.loadingStores = true;
				getPrivateAppointmentStores({
					benefitId: this.benefitId,
					coachId: coach.id
				}).then((res) => {
					if (requestSeq !== this.storeRequestSeq) return;
					this.stores = res.data || [];
					const preferred = String(preferredStoreId || '');
					const index = this.stores.findIndex((item) => String(item.storeId) === preferred);
					this.storeIndex = index >= 0 ? index : 0;
					this.loadingStores = false;
					this.loadSlots();
				}).catch((e) => {
					if (requestSeq !== this.storeRequestSeq) return;
					this.loadingStores = false;
					this.stores = [];
					this.storeIndex = 0;
					this.config.Toast((e && e.message) || '门店加载失败');
				});
			},
			loadSlots() {
				this.slots = [];
				this.slotIndex = -1;
				if (!this.selectedCoach || !this.selectedStore || !this.date) {
					this.slotRequestSeq += 1;
					this.loadingSlots = false;
					return;
				}
				const requestSeq = ++this.slotRequestSeq;
				this.loadingSlots = true;
				getPrivateSlots({
					benefitId: this.benefitId,
					coachId: this.selectedCoach.id,
					storeId: this.selectedStore.storeId,
					date: this.date
				}).then((res) => {
					if (requestSeq !== this.slotRequestSeq) return;
					this.slots = res.data || [];
					this.loadingSlots = false;
				}).catch((e) => {
					if (requestSeq !== this.slotRequestSeq) return;
					this.loadingSlots = false;
					this.config.Toast((e && e.message) || '时段加载失败');
				});
			},
			confirmBook() {
				if (this.submitting || !this.selectedSlot || !this.selectedCoach || !this.selectedStore) return;
				const slot = this.selectedSlot;
				uni.showModal({
					title: '确认预约',
					content: this.date + ' ' + slot.startTime + '-' + slot.endTime + '\n' + this.selectedCoach.coachName + ' · ' + this.selectedStore.storeName,
					confirmText: '确认预约',
					success: (res) => {
						if (res.confirm) this.submitBook();
					}
				});
			},
			submitBook() {
				const slot = this.selectedSlot;
				this.submitting = true;
				bookPrivateAppointment({
					benefitId: this.benefitId,
					coachId: this.selectedCoach.id,
					storeId: slot.storeId,
					date: slot.date || this.date,
					startTime: slot.startTime,
					endTime: slot.endTime
				}).then(() => {
					this.submitting = false;
					this.config.Toast('预约成功');
					setTimeout(() => uni.redirectTo({ url: '/pagesA/private_appointment/private_appointment' }), 800);
				}).catch((e) => {
					this.submitting = false;
					this.config.Toast((e && e.message) || '预约失败');
					this.loadSlots();
				});
			},
			formatDate(date) {
				const p = (n) => n < 10 ? '0' + n : '' + n;
				return date.getFullYear() + '-' + p(date.getMonth() + 1) + '-' + p(date.getDate());
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F4; }
	.booking-page { min-height: 100vh; padding: 24rpx 24rpx 50rpx; background: #F4F4F4; box-sizing: border-box; }
	.course-card { padding: 32rpx; color: #FFF; background: linear-gradient(135deg, #FF8A3D, #E15B00); border-radius: 20rpx; }
	.course-name { font-size: 34rpx; font-weight: 900; }
	.course-tip { margin-top: 12rpx; opacity: .92; font-size: 23rpx; line-height: 38rpx; }
	.section { margin-top: 20rpx; padding: 28rpx; background: #FFF; border-radius: 20rpx; }
	.section-title { padding-left: 14rpx; border-left: 6rpx solid #E15B00; color: #222; font-size: 30rpx; font-weight: 800; }
	.picker-row { display: flex; min-height: 90rpx; align-items: center; border-bottom: 1rpx solid #EEE; color: #666; font-size: 26rpx; }
	.picker-row.no-border { border-bottom: none; }
	.picker-row view { flex: 1; margin-left: 30rpx; color: #222; text-align: right; }
	.slot-tip { margin-top: 18rpx; color: #999; font-size: 22rpx; line-height: 34rpx; }
	.slots { display: flex; flex-wrap: wrap; margin-top: 20rpx; }
	.slot { width: calc(50% - 10rpx); min-height: 100rpx; margin: 0 20rpx 20rpx 0; display: flex; flex-direction: column; align-items: center; justify-content: center; color: #555; background: #F7F7F7; border: 2rpx solid transparent; border-radius: 14rpx; font-size: 25rpx; box-sizing: border-box; }
	.slot:nth-child(2n) { margin-right: 0; }
	.slot text:last-child { margin-top: 8rpx; color: #999; font-size: 20rpx; }
	.slot.active { color: #E15B00; background: #FFF3EB; border-color: #E15B00; }
	.slot.active text:last-child { color: #E15B00; }
	.slot-empty { padding: 70rpx 20rpx; color: #999; text-align: center; font-size: 24rpx; line-height: 40rpx; }
	.submit { height: 88rpx; margin-top: 28rpx; display: flex; align-items: center; justify-content: center; color: #FFF; background: linear-gradient(90deg, #FF8A3D, #E15B00); border-radius: 44rpx; font-size: 29rpx; font-weight: 800; }
	.submit.disabled { opacity: .5; }
</style>
