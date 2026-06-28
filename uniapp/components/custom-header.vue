<template>
	<view class="custom-header">
		<!-- <text class="title">{{ title }}</text> -->
		<view class="navbar" :style="{'background':background, 'height':navbarHeight + 'px'}">
			<view :style="{'height':capsuleHeight + 'px', 'top': capsuleTop + 'px' }" class="arrow-content">
				<view class="arrow" v-if="isShowArrow" @click="handleGoToBack"></view>
			</view>
			<view class="text" :style="{ 'height': capsuleHeight + 'px', 'top': capsuleTop + 'px' }">{{title}}</view>
		</view>

	</view>
</template>

<script>
	export default {
		props: {
			title: {
				type: String,
				default: '首页'
			},
			background: {
				type: String,
				default: 'linear-gradient(to right,#FF3413,#FF924D)'
			},
			isShowArrow: {
				type: Boolean,
				default: true
			}
		},
		data: {
			capsuleTop: '', //胶囊距离屏幕顶部的距离
			capsuleHeight: '', //胶囊高度
			navbarHeight: '' //导航栏高度
		},
		onLoad() {
			// 获取应用实例
			const App = getApp();
			console.log('App==',App);return;
			this.capsuleTop = App.globalData.capsule.top;
			this.capsuleHeight = App.globalData.capsule.height;
			this.navbarHeight = (App.globalData.capsule.top - App.globalData.system.statusBarHeight) * 2 + App.globalData
				.capsule.height + App.globalData.system.statusBarHeight;
		},
		methods: {
			handleGoToBack() {
				uni.navigateBack({
					delta: 1
				})
			}
		}
	}
</script>

<style scoped>
	.navbar {
		width: 100%;
		//设置导航栏固定在顶部
		position: fixed;
		top: 0;
		z-index: 99;
	}

	.arrow-content {
		position: absolute;
		left: 40rpx;
		z-index: 999;
		display: flex;
		align-items: center;
	}

	.arrow {
		width: 20rpx;
		height: 20rpx;
		border: 5rpx solid #FFFFFF;
		border-right-color: transparent;
		border-bottom-color: transparent;
		transform: rotate(-45deg);
	}

	.text {
		position: absolute;
		left: 0;
		right: 0;
		font-size: 28rpx;
		color: #FFFFFF;
		display: flex;
		align-items: center;
		justify-content: center;
	}
</style>