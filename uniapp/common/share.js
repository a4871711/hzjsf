export default {
	data() {
		return {
			share: {
				title: '矢历健身',
				path: '/pages/index/index',
				imageUrl: '/static/login_logo.png',
			}
		}
	},
	// 分享给朋友
	onShareAppMessage(res) {
		return {
			title: this.share.title,
			path: this.share.path,
			imageUrl: this.share.imageUrl,
		}
	},
	// 分享到朋友圈
	onShareTimeline(res) {
		return {
			title: this.share.title,
			path: this.share.path,
			imageUrl: this.share.imageUrl,
		}
	},
}