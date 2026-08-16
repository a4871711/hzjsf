<template>
	<view class="profile-page">
		<view class="avatar-row">
			<image class="avatar" :src="form.avatarUrl || '/static/image/my_img.png'" mode="aspectFill" />
			<button class="avatar-button" open-type="chooseAvatar" @chooseavatar="chooseAvatar">更换头像</button>
		</view>

		<view class="form-card">
			<view class="form-row readonly">
				<text class="label">教练编号</text>
				<text class="value muted">{{ form.coachNo || '-' }}</text>
			</view>
			<view class="form-row readonly">
				<text class="label">教练等级</text>
				<text class="value muted">{{ form.coachLevel || '-' }}</text>
			</view>
			<view class="form-row">
				<text class="label">姓名</text>
				<input class="input" v-model.trim="form.coachName" maxlength="30" placeholder="请输入姓名" />
			</view>
			<view class="form-row">
				<text class="label">手机号</text>
				<input class="input" v-model.trim="form.mobile" type="number" maxlength="11" placeholder="请输入手机号" />
			</view>
			<view class="form-row">
				<text class="label">性别</text>
				<picker class="picker" mode="selector" :range="genderLabels" :value="genderIndex" @change="changeGender">
					<view class="picker-value">{{ genderLabel }} <text>›</text></view>
				</picker>
			</view>
			<view class="intro-row">
				<text class="label">个人简介</text>
				<textarea class="textarea" v-model="form.intro" maxlength="300" placeholder="介绍擅长方向、训练风格等" />
				<text class="count">{{ (form.intro || '').length }}/300</text>
			</view>
	</view>

		<view class="store-tip">所属门店由后台管理员维护：{{ form.storeNames || '暂未配置' }}</view>
		<view class="save-button" :class="{ disabled: saving }" @click="save">{{ saving ? '保存中...' : '保存资料' }}</view>
	</view>
</template>

<script>
	import { API_URL } from '@/env'
	import { getPrivateCoachMine, updatePrivateCoachProfile } from '@/api/private-training.js'

	export default {
		data() {
			return {
				loading: false,
				saving: false,
				genderLabels: ['未知', '男', '女'],
				form: {
					coachNo: '', coachName: '', mobile: '', gender: 0,
					avatarUrl: '', coachLevel: '', intro: '', storeNames: ''
				}
			}
		},
		computed: {
			genderIndex() {
				const value = Number(this.form.gender || 0);
				return value >= 0 && value <= 2 ? value : 0;
			},
			genderLabel() {
				return this.genderLabels[this.genderIndex];
			}
		},
		onLoad() {
			this.load();
		},
		methods: {
			load() {
				if (this.loading) return;
				this.loading = true;
				getPrivateCoachMine().then(res => {
					this.form = Object.assign({}, this.form, (res.data && res.data.coach) || {});
				}).catch(e => this.config.Toast((e && e.message) || '资料加载失败'))
					.then(() => { this.loading = false; });
			},
			changeGender(e) {
				this.form.gender = Number(e.detail.value);
			},
			chooseAvatar(e) {
				const filePath = e.detail.avatarUrl;
				if (!filePath) return;
				uni.showLoading({ title: '上传中' });
				uni.uploadFile({
					url: API_URL + '/upload',
					filePath,
					name: 'files',
					header: { 'Accept': 'application/json, text/plain, */*' },
					formData: { token: uni.getStorageSync('token') || '' },
					success: response => {
						try {
							const body = JSON.parse(response.data || '{}');
							if (body.code === 1 && body.data && body.data.imgPath) {
								this.form.avatarUrl = body.data.imgPath;
								this.config.Toast('头像上传成功');
								return;
							}
							this.config.Toast(body.msg || '头像上传失败');
						} catch (err) {
							this.config.Toast('头像上传失败');
						}
					},
					fail: () => this.config.Toast('头像上传失败'),
					complete: () => uni.hideLoading()
				});
			},
			save() {
				if (this.saving) return;
				if (!this.form.coachName) {
					this.config.Toast('请输入姓名');
					return;
				}
				if (!/^1\d{10}$/.test(this.form.mobile || '')) {
					this.config.Toast('手机号格式不正确');
					return;
				}
				this.saving = true;
				updatePrivateCoachProfile({
					coachName: this.form.coachName,
					mobile: this.form.mobile,
					gender: this.form.gender,
					avatarUrl: this.form.avatarUrl || '',
					intro: this.form.intro || ''
				}).then(() => {
					this.config.Toast('资料保存成功');
					setTimeout(() => uni.navigateBack(), 500);
				}).catch(e => this.config.Toast((e && e.message) || '资料保存失败'))
					.then(() => { this.saving = false; });
			}
		}
	}
</script>

<style lang="scss" scoped>
	page { background: #F4F4F6; }
	.profile-page { min-height: 100vh; padding: 34rpx 28rpx 50rpx; background: #F4F4F6; box-sizing: border-box; }
	.avatar-row { display: flex; align-items: center; justify-content: space-between; padding: 28rpx 28rpx; background: #FFF; border-radius: 22rpx; }
	.avatar { width: 128rpx; height: 128rpx; border-radius: 50%; }
	.avatar-button { width: 180rpx; height: 68rpx; margin: 0; color: #FF5617; background: #FFF1E9; border: 0; border-radius: 34rpx; font-size: 24rpx; line-height: 68rpx; }
	.avatar-button::after { border: 0; }
	.form-card { margin-top: 24rpx; padding: 0 26rpx; background: #FFF; border-radius: 22rpx; }
	.form-row { display: flex; min-height: 96rpx; align-items: center; border-bottom: 1rpx solid #EFF0F2; }
	.form-row.readonly { min-height: 88rpx; }
	.label { width: 170rpx; flex: 0 0 auto; color: #4F5057; font-size: 25rpx; }
	.value, .input, .picker { min-width: 0; flex: 1; color: #222328; font-size: 25rpx; text-align: right; }
	.muted { color: #96979E; }
	.picker-value { display: flex; align-items: center; justify-content: flex-end; }
	.picker-value text { margin-left: 12rpx; color: #B0B1B7; font-size: 36rpx; }
	.intro-row { position: relative; padding: 26rpx 0 46rpx; }
	.textarea { width: 100%; height: 190rpx; margin-top: 20rpx; padding: 20rpx; color: #222328; background: #F7F7F8; border-radius: 16rpx; font-size: 24rpx; box-sizing: border-box; }
	.count { position: absolute; right: 15rpx; bottom: 16rpx; color: #B0B1B7; font-size: 19rpx; }
	.store-tip { margin: 22rpx 8rpx 0; color: #96979E; font-size: 21rpx; line-height: 34rpx; }
	.save-button { display: flex; height: 82rpx; margin-top: 34rpx; align-items: center; justify-content: center; color: #FFF; background: #FF5617; border-radius: 42rpx; font-size: 26rpx; font-weight: 900; }
	.save-button.disabled { opacity: .55; }
</style>
