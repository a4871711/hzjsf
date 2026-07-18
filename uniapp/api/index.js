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

// 首页限时秒杀:当前门店进行中/预热的秒杀商品(公开,按 storeAddrId 过滤;data 直接是数组,空数组则首页不渲染秒杀区)
export async function flashSaleCurrent(params = {}) {
  Request.isLogin = false; // 公开接口,无需登录
  const res = await Request.post('/flashSale/current', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// VIP权益卡商品列表(公开,不取身份;返回 data 为分页对象 PageUtils,卡在 data.list)
export async function getVipCardList(params = {}) {
  Request.isLogin = false; // 公开接口,无需登录
  const res = await Request.post('/vipCard/list', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// VIP权益卡商品详情(公开;currentPrice 为后端按购买人数实时算出的动态价)
export async function getVipCardDetail(params = {}) {
  Request.isLogin = false; // 公开接口,无需登录
  const res = await Request.post('/vipCard/detail', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// VIP权益卡购买下单(需登录;后端重算动态价、建待支付权益,返回 data={orderNo, paySum})
export async function buyVipCard(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipCard/buy', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 我的权益(需登录;分页,data 为 PageUtils,记录在 data.list)
export async function getMyBenefits(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipCard/myBenefits', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 转让费用试算(需登录;入参 vipBenefitId,只读不落单。data={vipBenefitId,transferCount,thisTransferNo,serviceFee})
export async function quoteVipTransfer(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/quote', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 发起转让(需登录;入参 vipBenefitId,toUserId。data 含 {transferId,status,serviceFee[,orderNo,paySum]})
export async function applyVipTransfer(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/apply', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 我的转让/受让记录(需登录;role 1我发起 2我接收 空全部,status 可选,分页 data.list)
export async function getMyTransferList(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/myList', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 受让人确认接收(需登录;入参 transferId,仅待确认(40)可操作,成功即过户生效)
export async function confirmVipTransfer(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/confirm', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 受让人拒绝接收(需登录;入参 transferId,仅待确认(40)可操作,服务费原路退转让人)
export async function rejectVipTransfer(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/reject', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 转让人撤回(需登录;入参 transferId;待审核(20)撤回退服务费,待确认(40)撤回不退)
export async function withdrawVipTransfer(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/vipTransfer/withdraw', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 停卡预检(需登录;入参 cardOrderId 可选。data 含 {freeAvailable,nextFreeDate,maxFreeDays,tiers:[{index,days,price}]},tiers 空数组=该卡未开通付费停卡)
export async function getCardPausePrecheck(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/cardPause/precheck', params);
  if (res.data.code === 1) {
    return res.data;
  }
  // 挂 code:前端要区分"-71确认非权益会员"和"网络抖动等其它失败",避免把后者也误判成非会员
  const err = new Error(res.data.msg);
  err.code = res.data.code;
  return Promise.reject(err);
}

// 申请停卡(需登录;入参 cardOrderId,pauseType 0免费/1付费,免费传 pauseDays 1~7,付费传 tierIndex。免费返回 {pauseId,needPay:false};付费返回 {pauseId,needPay:true,orderNo,paySum})
export async function applyCardPause(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/cardPause/apply', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 恢复停卡(需登录;入参 pauseId,按实际天数顺延有效期)
export async function resumeCardPause(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/cardPause/resume', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 取消停卡(需登录;入参 pauseId。未使用天数从顺延的有效期中扣回,付费停卡不退款)
export async function cancelCardPause(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/cardPause/cancel', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}

// 我的停卡记录(需登录;分页,data 为 PageUtils,记录在 data.list)
export async function getCardPauseList(params = {}) {
  Request.isLogin = true; // 需要登录
  const res = await Request.post('/cardPause/list', params);
  if (res.data.code === 1) {
    return res.data;
  }
  return Promise.reject(new Error(res.data.msg));
}
