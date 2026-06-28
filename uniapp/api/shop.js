import Request from '@/common/utils/request';


// 门店列表
export async function getStoreList(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/storeAddress/list', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 省市区
export async function getAreaList(params = {}) {
  Request.isLogin = false; // 需要登录
  const res = await Request.post('/storeAddress/areaList', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

