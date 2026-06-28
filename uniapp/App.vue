<script>
	export default {
		globalData: {
			system: '',
			capsule: ''
		},
		onLaunch: function() {
			let token = uni.getStorageSync('token');
			if (token) {
				this.$store.commit('token', token)
				this.common.getUserInfo(this);
			}

			// 获取系统信息
			// uni.getSystemInfo({
			// 	success: res => {
			// 		// console.log('res==', res)
			// 		this.globalData.system = res
			// 	}
			// })
			// 获取胶囊信息
			// this.globalData.capsule = wx.getMenuButtonBoundingClientRect()
			// console.log('获取胶囊信息==', this.globalData.capsule)
			
		},
		onShow: function() {

		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	@import "uview-ui/index.scss";
	/*每个页面公共css */
	@import url("static/css.css");

	.uni-tabbar-border {
		// background-color: #fff !important;
		box-shadow: 0rpx 0rpx 9rpx 0rpx rgba(26, 26, 26, 0.1) !important;
	}
</style>