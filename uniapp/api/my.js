import Request from '@/common/utils/request';


// 会员协议列表
export async function listProtocol(params = {}) {
	Request.isLogin = false; // 需要登录
	const res = await Request.post('/share/listProtocol', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 协议详情
export async function queryProtocol(params = {}) {
	Request.isLogin = false; // 需要登录
	const res = await Request.post('/share/queryProtocol', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 获取用户信息
export async function getUserInfo(params = {}) {
	const res = await Request.post('/userInfo/findUserInfo', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 购买下单
export async function createOrder(params = {}) {
	// Request 是单例,isLogin 会残留上一次调用的值(如首页公开接口 flashSaleCurrent 置 false),须显式声明需登录
	Request.isLogin = true;
	const res = await Request.post('/cardOrder/createOrder', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 获取调起签约接口支付信息
export async function getWxpayData(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/wx/proPreenTrustWeb', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 获取调起微信接口支付信息
export async function Wxpay(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/wx/proPay', params);
	if (res.data.code === 1) {
		return res.data;
	} else {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 切换门店保存
export async function setNowStore(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/userInfo/setNowStore', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 修改用户信息
export async function updateUserInfo(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/userInfo/updateUserInfo', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 开门二维码/当前有效会员卡
export async function getOpenDoorQR(params = {}) {
	Request.isLogin = true; // 需要登录
	Request.isError = false;
	const res = await Request.post('/userInfo/getOpenDoorQR', params);
	return res.data;
}


// 购卡记录
export async function cardRecord(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/cardOrder/queryList', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}

// 解约
export async function deleteContractTest(params = {}) {
	Request.isLogin = true; // 需要登录
	const res = await Request.post('/wx/deleteContractTest', params);
	if (res.data.code === 1) {
		return res.data;
	}
	return Promise.reject(new Error(res.data.msg));
}