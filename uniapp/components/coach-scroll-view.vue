<template>
	<scroll-view class="scroll-view" scroll-x="true">
		<view class="scroll-view-item" v-for="(item, index) in coachList" :key="index">
			<image :src="item.headImgUrl" class="image" mode="aspectFill" @click="previewImage(item.headImgUrl)">
			</image>
			<view class="name flex_1">{{item.coachName}}</view>
		</view>
	</scroll-view>
</template>

<script>
	export default {
		name: "coach-scroll-view",
		props: {
			coachList: {
				type: Array,
				default: []
			}
		},
		data() {
			return {

			};
		},
		methods: {
			// 调用uni-app的previewImage方法来预览图片
			previewImage(imageUrl) {
				uni.previewImage({
					current: imageUrl, // 当前显示图片的链接
					urls: [imageUrl] // 需要预览的图片链接列表
				});
			},
		}
	}
</script>

<style lang="scss" scoped>
	.scroll-view {
		white-space: nowrap;
		width: 100%;
		padding-bottom: 30px;

		.scroll-view-item {
			display: inline-block;
			width: 236rpx;
			height: 424rpx;
			margin-right: 10px;
			padding-bottom: 10rpx;

			.image {
				width: 100%;
				height: 339rpx;
				border-radius: 20rpx 20rpx 0rpx 0rpx;
				vertical-align: middle;
			}

			.name {
				height: 84.91rpx;
				font-size: 26rpx;
				font-weight: bold;
				text-align: center;
				background: #FFFFFF;
				border-radius: 0rpx 0rpx 20rpx 20rpx;
				box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1);
			}
		}
	}
</style>