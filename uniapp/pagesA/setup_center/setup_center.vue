<template>
	<view class="center">
		<view class="hdimg">
			<image :src="avatarimg ? avatarimg:'/static/image/my_img.png'" alt="" class="img" />
			<button type="avatar-wrapper" open-type="chooseAvatar" @chooseavatar="onChooseavatar" class="ghbtn">
				更换
			</button>
			
		</view>
		<view class="item">
			<view class="item-left">用户昵称</view>
			<view class="item-right">
				 <input v-model="nickname"  @blur="saveNickname" />
			</view>
		</view>
		
		
		<view class="item">
			<view class="item-left">微信</view>
			<view class="jcbtn flex_col_center" @click="openUp">解除绑定</view>
		</view>
		<view class="item" @click="config.path('/pagesA/vipAgreement/vipAgreement')">
			<view class="item-left">会员协议</view>
			<view class="icon-arrow"></view>
		</view>
		<view class="login-out flex_col_center" @click="loginOut()">退出登录</view>

		<u-popup :show="show" @close="close" mode="center" :round="10" style="padding: 0;margin: 0;">
			<view class="up-pop text_center">
			<view class="up-title">解除绑定</view>
			<view class="up-cont">请联系客服进行解绑</view>
			<view class="up-btn flex_col_center" @click="close">知道了</view>
			</view>		
		</u-popup>
	</view>

</template>

<script>
	import {updateUserInfo} from '@/api/my.js'
	import {
	  API_URL
	} from '@/env'
	export default {
		data() {
			return {
				show: false,
				avatarimg: '',	
				nickname:''
			};
		},
		onLoad() {	
			this.nickname = this.$store.state.userinfo.nickname || "SL" + this.$store.state.userinfo.phone.toString().slice(-4)
			this.avatarimg = this.$store.state.userinfo.headImgUrl;
		},
		methods: {	
			// 修改昵称
			async saveNickname(){
				await updateUserInfo({nickname: this.nickname}).then(() => {
					this.config.Toast('修改成功');
				});
				// 获取用户信息
				await this.common.getUserInfo(this);
			},
			// 手动获取头像
			async onChooseavatar(e) {	
				this.avatarimg = e.detail.avatarUrl;
				await uni.uploadFile({
				  url: API_URL + '/upload', // 你的上传API地址
				  filePath: e.detail.avatarUrl,
				  name: 'files', // 这里根据API的要求进行修改
				  header: {
					'Content-Type': 'multipart/form-data',
					'Accept': 'application/json, text/plain, */*',
				  },
				  formData: {
					  //token: uni.getStorageSync('token')
				  },
				  success: async uploadFileRes => {
					let res = JSON.parse(uploadFileRes.data)
					if(res.code == 1){
						let imgPath = res.data.imgPath
						await updateUserInfo({headImgUrl: imgPath}).then(() => {
							this.config.Toast('修改成功');
						});
						// 获取用户信息
						await this.common.getUserInfo(this);
					}else{
						this.config.Toast('上传失败')
					}
					
				  },
				  fail: uploadFileError => {
				    // 处理上传失败的错误
				  }
				});
			
			},
			openUp() {
				this.show = true
			  },
			close() {
				this.show = false				
			},
			// 退出登录
			loginOut(){
				uni.setStorageSync('token', null);
				uni.setStorageSync('userinfo', null);
				this.$store.commit('userinfo', null)
				this.$store.commit('token', null)				
				this.config.Toast('退出成功')				
				setTimeout(() => {
					uni.switchTab({
						url: '/pages/my/my'
					})
				}, 1000)
				
			},			
		}
	}
</script>

<style lang="scss">
	page{
		background: #fff;
	}
	.u-safe-bottom{
		display: none;
	}
	.center {
		padding: 50rpx;
		box-sizing: border-box;
		.up-pop{
			width: 580rpx;
			.up-title{
				font-size: 32rpx;
				color: #999999;
				margin: 48rpx 0 36rpx 0;
			}
			.up-cont{
				font-weight: 500;
				font-size: 32rpx;
				color: #333333;
				margin-bottom: 63rpx;
			}
			.up-btn{
				height: 76rpx;
				background: #212222;
				font-size: 32rpx;
				color: #FFFFFF;
				border-radius:0 0 20rpx 20rpx;
			}
		}
		.login-out {
			font-weight: bold;
			font-size: 30rpx;
			color: #FFFFFF;
			width: 636rpx;
			height: 90rpx;
			background: #373838;
			border-radius: 45rpx;
			position: fixed;
			bottom: 60rpx;
		}

		.hdimg {
			display: flex;
			justify-content: space-between;
			align-items: center;
			margin-bottom: 67rpx;

			.img {
				width: 141rpx;
				height: 141rpx;
				border-radius: 50%;
			}

			.ghbtn {
				width: 170rpx;
				height: 63rpx;
				border-radius: 5rpx;
				font-weight: 500;
				font-size: 32rpx;
				color: #333333;
				display: flex;
				align-items: center;
				justify-content: center;
				margin: 0;
			}
		}

		.item {
			display: flex;
			justify-content: space-between;
			margin-bottom: 56rpx;

			.item-left {
				font-weight: bold;
				font-size: 28rpx;
				color: #333333;
			}
			.item-right{
				text-align: right;
			}
			.jcbtn {
				width: 144rpx;
				height: 51rpx;
				border-radius: 26rpx;
				border: 1px solid #ccc;
				font-size: 24rpx;
			}

			.icon-arrow {
				position: relative;
				padding-right: 25rpx;
			}

			.icon-arrow:after {
				content: '';
				position: absolute;
				top: 15rpx;
				right: -1px;
				width: 14rpx;
				height: 14rpx;
				border-top: 1rpx solid #ccc;
				border-right: 2rpx solid #ccc;
				transform: rotate(45deg);
			}
		}
	}
</style>