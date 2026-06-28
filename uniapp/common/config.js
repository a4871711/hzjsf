export default {
	Toast: function(title) {
		uni.showToast({
			title: title,
			duration: 2000,
			icon: 'none'
		});
	},
	path: function(url, status) {
		if (status) {
			uni.reLaunch({
				url: url
			});
		} else {
			uni.navigateTo({
				url: url,
				animationType: 'pop-in',
				animationDuration: 0
			});
		}
	},
	timestampToDateTime(timestamp, formatType = 'full') {
		const date = new Date(timestamp);

		// 转换为易读的日期格式
		const year = date.getFullYear(); // 年
		const month = date.getMonth() + 1; // 月，getMonth()返回的月份是从0开始的，所以需要+1
		const day = date.getDate(); // 日
		const hour = date.getHours(); // 时
		const minute = date.getMinutes(); // 分
		const second = date.getSeconds(); // 秒

		// 将单个数字格式化为两位数（如1变为01）
		const formatNumber = (n) => {
			return n < 10 ? '0' + n : n;
		};

		// 根据不同的formatType返回不同的格式
		switch (formatType) {
			case 'date':
				// 返回格式化的日期字符串（年-月-日）
				return `${year}-${formatNumber(month)}-${formatNumber(day)}`;
			case 'time':
				// 返回格式化的时间字符串（时:分:秒）
				return `${formatNumber(hour)}:${formatNumber(minute)}:${formatNumber(second)}`;
			case 'dateTime':
					// 返回格式化的时间字符串（时:分:秒）
					return `${year}-${formatNumber(month)}-${formatNumber(day)} ${formatNumber(hour)}:${formatNumber(minute)}:${formatNumber(second)}`;
			case 'full':
			default:
				// 返回格式化的日期时间字符串（年-月-日 时:分）
				return `${year}-${formatNumber(month)}-${formatNumber(day)} ${formatNumber(hour)}:${formatNumber(minute)}`;
		}
	}

	// 示例用法：
	// console.log(timestampToDateTime(1732010596, 'date')); // 输出: 2024-05-20
	// console.log(timestampToDateTime(1732010596, 'full')); // 输出: 2024-05-20 15:27

}