import {
	getUserInfo
} from '@/api/my.js'

export default {
	// 获取用户信息
	async getUserInfo(that) {
		await getUserInfo().then((res) => {
			that.$store.commit('userinfo', res.data)
			uni.setStorageSync('userinfo', res.data);
		});
	},
	/**
	 * 获取用户当前地址
	 */
	getMyLocation(that) {
		return new Promise((resolve, reject) => {
			uni.getLocation({
				type: 'gcj02',
				geocode: true,
				isHighAccuracy: true,
				success: resolve,	
				fail: reject
			});
			// uni.getLocation({
			// 	type: 'wgs84',
			// 	success: resolve,
			// 	fail: reject
			// });
		});
	},

	/**
	 * 再次弹窗获取位置信息
	 */
	againGetMyLocation(that) {
		uni.openSetting({
			success: (res) => {
				console.log(res);
			},
			fail: (err) => {
				console.log(err)
			}
		})
	},

}