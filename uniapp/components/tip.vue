<template>
	<view>
		<u-modal :show="show" :title="title" :confirmText="confirm" :cancelText="cancel"
			:showCancelButton="showCancelButton" confirmColor="#fe7730" @confirm="handlconfirm" @cancel="commonClick">
			<view class="slot-content">
				<rich-text :nodes="content"></rich-text>
			</view>
		</u-modal>
	</view>
</template>

<script>
	export default {
		props: {
			show: {
				type: Boolean,
				default: false
			},
			showCancelButton: {
				type: Boolean,
				default: false
			},
			title: {
				type: String,
				default: '提示'
			},
			content: {
				type: String,
				default: '测试模态框弹出'
			},
			confirm: {
				type: String,
				default: '确定'
			},
			cancel: {
				type: String,
				default: ''
			}
		},

		name: "tip",
		data() {
			return {};
		},
		methods: {
			handlconfirm() {
				console.log('确定')
				this.$emit('handlconfirm')
			},
			commonClick() {
				console.log('取消')
				this.$emit('cancellation')
			}
		}
	}
</script>

<style lang="scss" scoped>
	.u-modal__content .slot-content {
		text-align: center;
		line-height: 50rpx;
	}
</style>