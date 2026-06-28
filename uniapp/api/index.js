import Request from '@/common/utils/request';


// 轮播图
export async function getSwiper(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/advertising/advertisingList', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 首页门店
export async function getMyStore(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/userInfo/getMyStore', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 教练列表
export async function getCoachList(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/coach/storeCoachList', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 会员卡列表
export async function getfitCardList(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/fitCard/list', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 门店数量
export async function getStoreCount(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/storeAddress/count', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 门店详情
export async function getStoreInfo(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/storeAddress/info', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// VIP套餐详情
export async function getFitCardInfo(params = {}) {
  const res = await Request.post('/fitCard/info', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}
