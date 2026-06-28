import Request from '@/common/utils/request';


// 微信登录
export async function wxLogin(params = {}) {
	Request.isLogin = false; // 需要登录
	const res = await Request.post('/userInfo/proLogin', params);
	if (res.data.code === 1) {
		return res.data;
	} else {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 微信绑定手机号
export async function wxBindPhone(params = {}) {
	const res = await Request.post('/userInfo/proPhone', params);
	if (res.data.code === 1) {
		return res.data;
	} else {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}