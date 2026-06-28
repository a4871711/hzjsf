<template>
	<view class="agreement font_size_30 clr_h">
		<rich-text :nodes="content"></rich-text>
	</view>
</template>

<script>
	import {
		queryProtocol
	} from '@/api/my.js'
	export default {
		data() {
			return {
				content: '',
			}
		},
		onLoad(type) {
			this.setNavTitle(type);
			this.queryProtocol(type);
		},
		methods: {
			// 动态设置标题
			setNavTitle(params) {
				let newTitle = '';
				switch (params.type) {
					case '0':
						newTitle = '用户协议';
						break;
					case '1':
						newTitle = '隐私协议';
						break;
					case'2':
						newTitle = '矢历连续包月协议';
						break;
					case '3':
						newTitle = '矢历连续会员协议';
						break;
					case '4':
						newTitle = '矢历连续次卡会员协议';
						break;
					default:
						newTitle = '没获取到'
						// 如果没有匹配的case，可以在这里处理
						break;
				}
				uni.setNavigationBarTitle({
					title: newTitle
				});
			},
			// 协议详情
			queryProtocol(type) {
				queryProtocol(type).then((res) => {
					this.content = res.data.protocols
				});
			},
		},
	}
</script>

<style scoped>
	.agreement {
		padding: 40rpx;
		box-sizing: border-box;
		word-wrap: break-word;
		/* 旧版浏览器支持 */
		overflow-wrap: break-word;
		/* 标准属性 */
	}
</style>