import Request from '@/common/utils/request';


// 优惠券
export async function getCouponList(params = {}) {
	const res = await Request.post('/coupon/couponList', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 美团团购
export async function getSendSysCoupon(params = {}) {
	const res = await Request.post('/coupon/sendSysCoupon', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}