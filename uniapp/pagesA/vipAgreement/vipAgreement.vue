<template>
	<view class="vip-agreement">
		<view class="item" @click="config.path('/pagesA/agreement/agreement?type='+item.type)" v-for='(item,index) in protocolList' :key="index">
			{{item.text}}
		</view>
	</view>
</template>

<script>
	import { listProtocol } from '@/api/my.js'
	export default {
		data() {
			return {
				protocolList:[]
			};
		},
		onLoad() {
			this.listProtocol();
		},
		methods: {
			// 获取用户协议列表
			listProtocol() {				
				listProtocol().then((res) => {
				  this.protocolList = res.data.map((item) => {
				    let newItem = { ...item }; // 创建一个新的对象，包含原始对象的所有属性
				    switch (item.type) {
				      case 0:
				        newItem.text = '用户协议';
				        break;
				      case 1:
				        newItem.text = '隐私协议';
				        break;
				      case 2:
				        newItem.text = '矢历连续包月协议';
				        break;
				      case 3:
				        newItem.text = '矢历连续会员协议';
				        break;
				      case 4:
				        newItem.text = '矢历连续次卡会员协议';
				        break;
				      default:
				        // 如果没有匹配的case，可以在这里处理
				        break;
				    }
				    return newItem; 
				  });
				});
			},
		},
	}
</script>

<style lang="scss" scoped>
	.vip-agreement {
		padding-top: 16rpx;
		box-sizing: border-box;
		background: #F2F2F2;
		height: 100vh;
	}

	.item {
		position: relative;
		width: 90%;
		height: 92rpx;
		margin: 0 auto;
		background: #FFFFFF;
		border-radius: 15rpx;
		display: flex;
		align-items: center;
		padding: 30rpx 40rpx;
		box-sizing: border-box;
		font-weight: bold;
		font-size: 32rpx;
		color: #333333;
		margin-bottom: 20rpx;
	}

	.item:after {
		content: '';
		position: absolute;
		top: 33rpx;
		right: 33rpx;
		width: 14rpx;
		height: 14rpx;
		border-top: 1rpx solid #333;
		border-right: 2rpx solid #333;
		transform: rotate(45deg);
	}
</style>