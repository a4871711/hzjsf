import Request from '@/common/utils/request';

async function post(url, params, isLogin) {
	Request.isLogin = isLogin;
	const res = await Request.post(url, params || {});
	const body = res.data || {};
	if (body.code === 1) {
		return body;
	}
	const error = new Error(body.msg || '请求失败');
	error.code = body.code;
	return Promise.reject(error);
}

// 公开商品浏览
export function getPrivateProductList(params = {}) {
	return post('/ptProduct/list', params, false);
}

export function getPrivateProductCategories(params = {}) {
	return post('/ptProduct/categories', params, false);
}

export function getPrivateProductDetail(params = {}) {
	return post('/ptProduct/detail', params, false);
}

export function getPrivateProductStores(params = {}) {
	return post('/ptProduct/stores', params, false);
}

// 商品未指定教练时，后端会返回适用门店内所有符合预约条件的教练
export function getProductBookableCoaches(params = {}) {
	return post('/coach/listByProduct', params, false);
}

// 私教下单与订单
export function quotePrivateOrder(params = {}) {
	return post('/privateOrder/quote', params, true);
}

export function createPrivateOrder(params = {}) {
	return post('/privateOrder/create', params, true);
}

export function repayPrivateOrder(params = {}) {
	return post('/privateOrder/repay', params, true);
}

export function confirmPrivateOrderWechatPay(params = {}) {
	return post('/privateOrder/confirmWechatPay', params, true);
}

export function cancelPrivateOrder(params = {}) {
	return post('/privateOrder/cancel', params, true);
}

export function getPrivateOrders(params = {}) {
	return post('/privateOrder/myOrders', params, true);
}

export function getPrivateOrderDetail(params = {}) {
	return post('/privateOrder/detail', params, true);
}

export function getPrivateBenefits(params = {}) {
	return post('/privateOrder/myBenefits', params, true);
}

// 私教营销券（与旧 coupon 体系分开）
export function getUsablePrivateCoupons(params = {}) {
	return post('/mkCoupon/usableForOrder', params, true);
}

// 私教专用储值账户（与旧提现钱包分开）
export function getPrivateWalletAccount(params = {}) {
	return post('/ptWallet/account', params, true);
}

// 私教预约
export function getPrivateCoaches(params = {}) {
	return post('/privateAppointment/coaches', params, true);
}

export function getPrivateSlots(params = {}) {
	return post('/privateAppointment/slots', params, true);
}

export function bookPrivateAppointment(params = {}) {
	return post('/privateAppointment/book', params, true);
}

export function cancelPrivateAppointment(params = {}) {
	return post('/privateAppointment/cancel', params, true);
}

export function getPrivateAppointments(params = {}) {
	return post('/privateAppointment/myList', params, true);
}

// 教练工作台：后端根据登录 token 反查绑定教练，不接受客户端 coachId
export function getPrivateCoachWorkbench(params = {}) {
	return post('/privateAppointment/coachWorkbench', params, true);
}

// 教练固定周排班：与后台 /sys/schedule 共用 pt_coach_schedule 和业务校验
export function getPrivateCoachSchedules(params = {}) {
	return post('/privateCoachSchedule/list', params, true);
}

export function savePrivateCoachSchedule(params = {}) {
	return post('/privateCoachSchedule/save', params, true);
}

export function updatePrivateCoachSchedule(params = {}) {
	return post('/privateCoachSchedule/update', params, true);
}

export function changePrivateCoachScheduleEnabled(params = {}) {
	return post('/privateCoachSchedule/changeEnabled', params, true);
}

export function deletePrivateCoachSchedule(params = {}) {
	return post('/privateCoachSchedule/delete', params, true);
}

// 教练端“我的”：资料和收入均按登录 token 反查绑定教练
export function getPrivateCoachMine(params = {}) {
	return post('/privateCoachCenter/mine', params, true);
}

export function updatePrivateCoachProfile(params = {}) {
	return post('/privateCoachCenter/updateProfile', params, true);
}

export function getPrivateCoachIncomeList(params = {}) {
	return post('/privateCoachCenter/incomeList', params, true);
}

export function getPrivateCoachWithdrawalList(params = {}) {
	return post('/privateCoachCenter/withdrawalList', params, true);
}

export function applyPrivateCoachWithdrawal(params = {}) {
	return post('/privateCoachCenter/withdrawalApply', params, true);
}

// 完课评价
export function submitPrivateComment(params = {}) {
	return post('/coachComment/submit', params, true);
}

export function getPrivateComments(params = {}) {
	return post('/coachComment/myList', params, true);
}
