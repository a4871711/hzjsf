<template>
	<view class="schedule-page">
		<view class="schedule-head">
			<view>
				<view class="head-title">每周固定排班</view>
				<view class="head-tip">与后台教练排班实时共用</view>
			</view>
			<view class="add-button" @click="openCreate">＋ 新增</view>
		</view>

		<scroll-view class="weekday-scroll" scroll-x :show-scrollbar="false">
			<view class="weekday-row">
				<view
					class="weekday-item"
					:class="{ active: item.value === selectedWeekday }"
					v-for="item in weekdays"
					:key="item.value"
					@click="selectedWeekday = item.value"
				>
					<text class="weekday-short">{{ item.short }}</text>
					<text class="weekday-count">{{ slotsOf(item.value).length }}段</text>
				</view>
			</view>
		</scroll-view>

		<view class="schedule-content">
			<view class="content-head">
				<text class="content-title">{{ selectedWeekLabel }}</text>
				<text class="content-count">共 {{ currentSlots.length }} 个时间段</text>
			</view>

			<view class="slot-card" :class="{ disabled: Number(item.isEnabled) === 0 }" v-for="item in currentSlots" :key="item.id">
				<view class="slot-main">
					<view class="slot-time">{{ item.startTime }} - {{ item.endTime }}</view>
					<view class="slot-store">
						<u-icon name="map" color="#9A9BA4" size="22"></u-icon>
						<text>{{ item.storeName || storeName(item.storeId) }}</text>
					</view>
				</view>
				<view class="slot-side">
					<view class="status-switch" :class="{ enabled: Number(item.isEnabled) === 1 }" @click="toggleEnabled(item)">
						{{ Number(item.isEnabled) === 1 ? '已启用' : '已停用' }}
					</view>
					<view class="slot-actions">
						<text @click="openEdit(item)">编辑</text>
						<text class="delete" @click="deleteSlot(item)">删除</text>
					</view>
				</view>
			</view>

			<view class="empty" v-if="loaded && !currentSlots.length">
				<view class="empty-icon">□</view>
				<view class="empty-title">{{ selectedWeekLabel }}暂无排班</view>
				<view class="empty-tip">点击右上角“新增”设置可预约时间</view>
			</view>
		</view>

		<view class="form-mask" v-if="formVisible" @click="closeForm">
			<view class="form-panel" @click.stop>
				<view class="form-title">{{ form.id ? '编辑排班' : '新增排班' }}</view>

				<view class="form-row">
					<text class="form-label">星期</text>
					<picker class="form-picker" mode="selector" :range="weekdayLabels" :value="weekdayIndex" @change="onWeekdayChange">
						<view class="picker-value">{{ weekdayLabel(form.weekday) }} <text>›</text></view>
					</picker>
				</view>
				<view class="form-row">
					<text class="form-label">门店</text>
					<picker class="form-picker" mode="selector" :range="storeLabels" :value="storeIndex" @change="onStoreChange">
						<view class="picker-value">{{ storeName(form.storeId) }} <text>›</text></view>
					</picker>
				</view>
				<view class="form-row">
					<text class="form-label">开始时间</text>
					<picker class="form-picker" mode="time" :value="form.startTime" @change="form.startTime = $event.detail.value">
						<view class="picker-value">{{ form.startTime }} <text>›</text></view>
					</picker>
				</view>
				<view class="form-row">
					<text class="form-label">结束时间</text>
					<picker class="form-picker" mode="time" :value="form.endTime" @change="form.endTime = $event.detail.value">
						<view class="picker-value">{{ form.endTime }} <text>›</text></view>
					</picker>
				</view>

				<view class="form-actions">
					<view class="cancel-button" @click="closeForm">取消</view>
					<view class="save-button" :class="{ disabled: saving }" @click="submitForm">{{ saving ? '保存中...' : '确认保存' }}</view>
				</view>
			</view>
		</view>

		<coach-tabbar active="schedule" />
	</view>
</template>

<script>
	import CoachTabbar from '@/components/coach-tabbar/coach-tabbar.vue'
	import {
		getPrivateCoachSchedules,
		savePrivateCoachSchedule,
		updatePrivateCoachSchedule,
		changePrivateCoachScheduleEnabled,
		deletePrivateCoachSchedule
	} from '@/api/private-training.js'

	export default {
		components: { CoachTabbar },
		data() {
			const day = new Date().getDay();
			return {
				loaded: false,
				loading: false,
				saving: false,
				formVisible: false,
				selectedWeekday: day === 0 ? 7 : day,
				weekdays: [
					{ value: 1, short: '一', label: '周一' },
					{ value: 2, short: '二', label: '周二' },
					{ value: 3, short: '三', label: '周三' },
					{ value: 4, short: '四', label: '周四' },
					{ value: 5, short: '五', label: '周五' },
					{ value: 6, short: '六', label: '周六' },
					{ value: 7, short: '日', label: '周日' }
				],
				stores: [],
				schedules: [],
				form: this.blankForm(day === 0 ? 7 : day)
			}
		},
		computed: {
			selectedWeekLabel() {
				return this.weekdayLabel(this.selectedWeekday);
			},
			currentSlots() {
				return this.slotsOf(this.selectedWeekday);
			},
			weekdayLabels() {
				return this.weekdays.map(item => item.label);
			},
			weekdayIndex() {
				return Math.max(0, this.weekdays.findIndex(item => item.value === Number(this.form.weekday)));
			},
			storeLabels() {
				return this.stores.map(item => item.storeName || `门店#${item.storeId}`);
			},
			storeIndex() {
				return Math.max(0, this.stores.findIndex(item => Number(item.storeId) === Number(this.form.storeId)));
			}
		},
		onShow() {
			this.loadSchedules();
		},
		onPullDownRefresh() {
			this.loadSchedules(() => uni.stopPullDownRefresh());
		},
		methods: {
			blankForm(weekday) {
				return { id: null, storeId: null, weekday, startTime: '09:00', endTime: '10:00' };
			},
			loadSchedules(done) {
				if (this.loading) {
					done && done();
					return;
				}
				this.loading = true;
				getPrivateCoachSchedules().then(res => {
					const data = res.data || {};
					this.stores = data.stores || [];
					this.schedules = data.schedules || [];
				}).catch(e => {
					this.config.Toast((e && e.message) || '排班加载失败');
				}).then(() => {
					this.loaded = true;
					this.loading = false;
					done && done();
				});
			},
			slotsOf(weekday) {
				return this.schedules.filter(item => Number(item.weekday) === Number(weekday));
			},
			weekdayLabel(weekday) {
				const hit = this.weekdays.find(item => item.value === Number(weekday));
				return hit ? hit.label : '请选择';
			},
			storeName(storeId) {
				const hit = this.stores.find(item => Number(item.storeId) === Number(storeId));
				return hit ? (hit.storeName || `门店#${storeId}`) : '请选择门店';
			},
			openCreate() {
				if (!this.stores.length) {
					this.config.Toast('该教练暂未配置所属门店');
					return;
				}
				this.form = this.blankForm(this.selectedWeekday);
				this.form.storeId = this.stores[0].storeId;
				this.formVisible = true;
			},
			openEdit(item) {
				this.form = {
					id: item.id,
					storeId: item.storeId,
					weekday: Number(item.weekday),
					startTime: item.startTime,
					endTime: item.endTime
				};
				this.formVisible = true;
			},
			closeForm() {
				if (!this.saving) this.formVisible = false;
			},
			onWeekdayChange(e) {
				const item = this.weekdays[Number(e.detail.value)];
				if (item) this.form.weekday = item.value;
			},
			onStoreChange(e) {
				const item = this.stores[Number(e.detail.value)];
				if (item) this.form.storeId = item.storeId;
			},
			submitForm() {
				if (this.saving) return;
				if (!this.form.storeId) {
					this.config.Toast('请选择门店');
					return;
				}
				if (!this.form.startTime || !this.form.endTime || this.form.endTime <= this.form.startTime) {
					this.config.Toast('结束时间必须晚于开始时间');
					return;
				}
				this.saving = true;
				const request = this.form.id ? updatePrivateCoachSchedule : savePrivateCoachSchedule;
				request(Object.assign({}, this.form, { isEnabled: 1 })).then(() => {
					this.formVisible = false;
					this.selectedWeekday = Number(this.form.weekday);
					this.config.Toast('排班保存成功');
					this.loadSchedules();
				}).catch(e => {
					this.config.Toast((e && e.message) || '排班保存失败');
				}).then(() => {
					this.saving = false;
				});
			},
			toggleEnabled(item) {
				const next = Number(item.isEnabled) === 1 ? 0 : 1;
				changePrivateCoachScheduleEnabled({ id: item.id, isEnabled: next }).then(() => {
					item.isEnabled = next;
					this.config.Toast(next === 1 ? '已启用' : '已停用');
				}).catch(e => this.config.Toast((e && e.message) || '状态修改失败'));
			},
			deleteSlot(item) {
				uni.showModal({
					title: '删除排班',
					content: `确定删除 ${item.startTime}-${item.endTime} 吗？已被未来预约占用的排班不能删除。`,
					success: res => {
						if (!res.confirm) return;
						deletePrivateCoachSchedule({ id: item.id }).then(() => {
							this.config.Toast('删除成功');
							this.loadSchedules();
						}).catch(e => this.config.Toast((e && e.message) || '删除失败'));
					}
				});
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.schedule-page { min-height: 100vh; padding-bottom: calc(120rpx + constant(safe-area-inset-bottom)); padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); background: #F4F4F6; box-sizing: border-box; }
	.schedule-head { display: flex; align-items: center; justify-content: space-between; padding: 30rpx 30rpx 26rpx; color: #FFF; background: linear-gradient(120deg, #FF5617, #FF803D); }
	.head-title { font-size: 34rpx; font-weight: 900; }
	.head-tip { margin-top: 8rpx; color: rgba(255, 255, 255, .82); font-size: 21rpx; }
	.add-button { padding: 14rpx 22rpx; background: rgba(255, 255, 255, .20); border: 1rpx solid rgba(255, 255, 255, .36); border-radius: 30rpx; font-size: 23rpx; font-weight: 800; }
	.weekday-scroll { width: 100%; background: #FFF; white-space: nowrap; }
	.weekday-row { display: flex; min-width: 750rpx; padding: 18rpx 16rpx; box-sizing: border-box; }
	.weekday-item { display: inline-flex; width: 96rpx; height: 92rpx; margin: 0 4rpx; flex: 0 0 auto; flex-direction: column; align-items: center; justify-content: center; color: #7C7D85; background: #F7F7F8; border-radius: 18rpx; }
	.weekday-item.active { color: #FFF; background: #FF5617; box-shadow: 0 8rpx 18rpx rgba(255, 86, 23, .20); }
	.weekday-short { font-size: 27rpx; font-weight: 900; }
	.weekday-count { margin-top: 5rpx; font-size: 18rpx; }
	.schedule-content { padding: 28rpx; }
	.content-head { display: flex; align-items: center; justify-content: space-between; padding: 0 2rpx 20rpx; }
	.content-title { color: #1E1F24; font-size: 31rpx; font-weight: 900; }
	.content-count { color: #9A9BA4; font-size: 21rpx; }
	.slot-card { display: flex; align-items: center; justify-content: space-between; min-height: 126rpx; margin-bottom: 18rpx; padding: 23rpx 24rpx; background: #FFF; border-radius: 20rpx; box-sizing: border-box; }
	.slot-card.disabled { opacity: .62; }
	.slot-main { min-width: 0; flex: 1; }
	.slot-time { color: #1E1F24; font-size: 31rpx; font-weight: 900; }
	.slot-store { display: flex; align-items: center; margin-top: 12rpx; color: #85868E; font-size: 22rpx; }
	.slot-store text { overflow: hidden; margin-left: 7rpx; text-overflow: ellipsis; white-space: nowrap; }
	.slot-side { display: flex; margin-left: 20rpx; flex: 0 0 auto; flex-direction: column; align-items: flex-end; }
	.status-switch { padding: 8rpx 14rpx; color: #96979E; background: #F0F1F3; border-radius: 18rpx; font-size: 19rpx; }
	.status-switch.enabled { color: #168F56; background: #E8F7EF; }
	.slot-actions { margin-top: 17rpx; color: #72737A; font-size: 21rpx; }
	.slot-actions text { margin-left: 20rpx; }
	.slot-actions .delete { color: #E35D55; }
	.empty { padding: 100rpx 20rpx; color: #A2A3AA; text-align: center; }
	.empty-icon { font-size: 55rpx; }
	.empty-title { margin-top: 18rpx; color: #7D7E85; font-size: 27rpx; font-weight: 800; }
	.empty-tip { margin-top: 11rpx; font-size: 21rpx; }
	.form-mask { position: fixed; z-index: 120; top: 0; right: 0; bottom: 0; left: 0; display: flex; align-items: flex-end; background: rgba(18, 20, 25, .45); }
	.form-panel { width: 100%; padding: 32rpx 30rpx calc(34rpx + constant(safe-area-inset-bottom)); padding: 32rpx 30rpx calc(34rpx + env(safe-area-inset-bottom)); background: #FFF; border-radius: 28rpx 28rpx 0 0; box-sizing: border-box; }
	.form-title { padding-bottom: 24rpx; color: #1D1E23; font-size: 32rpx; font-weight: 900; }
	.form-row { display: flex; align-items: center; min-height: 86rpx; border-top: 1rpx solid #F0F1F3; }
	.form-label { width: 155rpx; color: #65666D; font-size: 24rpx; }
	.form-picker { min-width: 0; flex: 1; }
	.picker-value { display: flex; align-items: center; justify-content: space-between; color: #202126; font-size: 25rpx; }
	.picker-value text { color: #AFB0B7; font-size: 34rpx; }
	.form-actions { display: flex; margin-top: 30rpx; }
	.cancel-button, .save-button { display: flex; height: 78rpx; align-items: center; justify-content: center; border-radius: 39rpx; font-size: 25rpx; font-weight: 800; }
	.cancel-button { width: 210rpx; color: #6F7078; background: #F1F2F4; }
	.save-button { margin-left: 20rpx; flex: 1; color: #FFF; background: #FF5617; }
	.save-button.disabled { opacity: .55; }
</style>
