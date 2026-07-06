package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.StoreAddressService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.vdurmont.emoji.EmojiParser;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/14/014
 */
@Service
@Transactional
public class CardOrderServiceImpl implements CardOrderService {
    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private FitCardMapper fitCardMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private StoreAddressMapper storeAddressMapper;
    @Autowired
    private StoreAddressService storeAddressService;
    @Autowired
    private VipBenefitMapper vipBenefitMapper;

    private Logger log = LoggerFactory.getLogger(getClass());

    /** 通卡：fit_card.storeAddrIds 为空，全部门店可用 */
    private boolean isUniversalCard(FitCard fitCard) {
        return fitCard == null || StringUtils.isBlank(fitCard.getStoreAddrIds());
    }

    /**
     * 会员卡剩余整天数(加购权益卡下单时快照用):无有效会员卡/已过期 → 0;
     * 否则按日期(各截断到当天0点)算 会员卡到期日 - 今天 的正天数。
     */
    private int memberRemainDays(Long userId) {
        Device dev = deviceMapper.selectUserValidity(userId);
        if (dev == null || dev.getValidityDate() == null) {
            return 0;
        }
        long todayMs = truncateToDay(new Date());
        long expireMs = truncateToDay(dev.getValidityDate());
        long diffDays = (expireMs - todayMs) / (24L * 60 * 60 * 1000);
        return diffDays > 0 ? (int) diffDays : 0;
    }

    /** 截断到当天 0 点的毫秒值(忽略时分秒) */
    private long truncateToDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTimeInMillis();
    }

    private String resolveStoreAddrIds(FitCard fitCard) {
        return isUniversalCard(fitCard) ? "" : fitCard.getStoreAddrIds();
    }

    /** 按卡套餐设置 device 门店限制：通卡清空 storeId / storeAddrIds */
    private void applyCardStoreScope(Device device, FitCard fitCard, CardOrder cardOrder) {
        device.setStoreAddrIds(resolveStoreAddrIds(fitCard));
        if (isUniversalCard(fitCard)) {
            device.setStoreId(null);
            return;
        }
        if (cardOrder.getStoreAddressId() != null && cardOrder.getStoreAddressId() > 0) {
            device.setStoreId(cardOrderMapper.queryStoreIdByAddress(cardOrder.getStoreAddressId()));
        }
    }

    private void syncDeviceStoreScope(Long deviceId, Device device) {
        deviceMapper.updateStoreScope(deviceId, device.getStoreId(), device.getStoreAddrIds());
    }

    /**
     *  @Auther:YD
     *  @parameters: 健身卡id：cardId,paySum总价，wrist手环类型，wristPrice手环价格，storeAddressId门店id，userAddressId收货地址id，sendTyp 配送方式
     *  卡订单
     */
    @Override
    public Map<String,Object> createFitCardOrder(UserInfoVo user, Map<String, Object> params, int flag) {
        log.info("创建订单params："+ params +"========================");
        CardOrder cardOrder = new CardOrder();
        //用户id
        cardOrder.setUserId(user.getUserId());
        //健身卡id
        cardOrder.setCardId(Long.valueOf((String) params.get("fitCardId")));
        cardOrder.setType(Integer.valueOf(String.valueOf(params.get("type"))));
        //1:自动续费2：非自动续费
        cardOrder.setAutoPay(Integer.valueOf(String.valueOf(params.get("autoPay"))));
        if (params.get("nextPrice")!=null) {
            cardOrder.setNextPrice(new BigDecimal(String.valueOf(params.get("nextPrice"))));
            cardOrder.setNextPriceTitle(String.valueOf(params.get("nextPriceTitle")));
        }
        if (params.get("nextPrice2")!=null) {
            cardOrder.setNextPrice2(new BigDecimal(String.valueOf(params.get("nextPrice2"))));
            cardOrder.setNextPriceTitle2(String.valueOf(params.get("nextPriceTitle2")));
        }
        if (params.get("nextPrice3")!=null) {
            cardOrder.setNextPrice3(new BigDecimal(String.valueOf(params.get("nextPrice3"))));
            cardOrder.setNextPriceTitle3(String.valueOf(params.get("nextPriceTitle3")));
        }
        //0: 未勾选免费服务  1：勾选了免费服务
        if (params.get("selecteFree")!=null) {
            cardOrder.setSelecteFree(Integer.valueOf(String.valueOf(params.get("selecteFree"))));
        }
        FitCard orderFitCard = fitCardMapper.selectByPrimaryKey(cardOrder.getCardId());
        if (orderFitCard != null) {
            cardOrder.setStoreAddrIds(StringUtils.isBlank(orderFitCard.getStoreAddrIds()) ? "" : orderFitCard.getStoreAddrIds());
        } else if (params.get("storeAddrIds") != null) {
            cardOrder.setStoreAddrIds(String.valueOf(params.get("storeAddrIds")));
        }
        if (params.get("oldValidityDate")!=null) {
        	cardOrder.setOldValidityDate(DateUtils.toDateCST(String.valueOf(params.get("oldValidityDate"))));
        }
        if (params.get("deviceId")!=null) {
        	cardOrder.setDeviceId(Long.valueOf(params.get("deviceId").toString()));
        }
        cardOrder.setValidityDate(DateUtils.toDate(String.valueOf(params.get("validityDate"))));
        cardOrder.setUseCount(Integer.parseInt(String.valueOf(params.get("useCount"))));
        //订单编号 订单号后面加 1 便于微信回调时调用回调方法 2：购买健身卡订单 4：购买私教课订单 5：购买团体课订单
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.CARD_ORDER_TYPE;
        cardOrder.setOrderNo(orderNo);
        //总价
        BigDecimal paySum = new BigDecimal(String.valueOf(params.get("paySum")));
        cardOrder.setPaySum(paySum);
        cardOrder.setBuyCount(Integer.parseInt(String.valueOf(params.get("buyCount"))));
        //优惠券id
        if ( params.get("couponId") != null && StringUtils.isNotBlank(params.get("couponId").toString()) ){
            cardOrder.setCouponId(Long.valueOf(params.get("couponId").toString()));
            Coupon coupon = couponMapper.selectByCouponId(Long.valueOf(String.valueOf(params.get("couponId"))));
            if (coupon != null) {
                if (paySum.compareTo(coupon.getCouponPrice()) == -1) {
                    //cardOrder.setPaySum(BigDecimal.ZERO);
                    cardOrder.setRealPayment(BigDecimal.ZERO);
                } else {
                    cardOrder.setRealPayment(paySum.subtract(coupon.getCouponPrice()));
                }
            }else {
                cardOrder.setRealPayment(paySum);
            }
        }else {
            cardOrder.setRealPayment(paySum);
        }
        //0第一次购卡，1：续费
        if(flag == 0){
            //手环类型
            cardOrder.setWrist(params.get("wrist")==null?null:Integer.parseInt((String) params.get("wrist")));
            //手环价格
            //cardOrder.setWristPrice(params.get("wristPrice")==null?null:Integer.parseInt(params.get("wristPrice").toString()));
            cardOrder.setWristPrice(params.get("wristPrice")==null?null: (new BigDecimal((String) params.get("wristPrice"))));
        }
            //门店地址id
            cardOrder.setStoreAddressId(params.get("storeAddressId")==null?null:Long.valueOf((String) params.get("storeAddressId")));
            //收货地址id
            cardOrder.setUserAddressId(params.get("userAddressId")==null?null:Long.valueOf((String) params.get("userAddressId")));
            //配送方式
            cardOrder.setSendType(params.get("sendType")==null?null:Integer.parseInt((String) params.get("sendType")));            
        
        if((cardOrder.getStoreAddressId() == null || cardOrder.getStoreAddressId() == 0) && 
        		user.getNowStoreId() != 0) {
        	//Map<String, Object> storeAddress = storeAddressMapper.queryStoreAddressByStoreId((long) user.getNowStoreId());
        	//cardOrder.setStoreAddressId(storeAddress.get("storeAddressId")==null?null:Long.valueOf((String) storeAddress.get("storeAddressId")));
        	cardOrder.setStoreAddressId(Long.valueOf(user.getNowStoreId()));
        }
        
        if(cardOrder.getStoreAddressId() == null || cardOrder.getStoreAddressId() == 0) {
	        //续费的时候需要绑定门店id
	        Long storeAddressId = userInfoMapper.queryStoreAddrIdByUserId(user.getUserId());
	        cardOrder.setStoreAddressId(storeAddressId);
        }
        if(cardOrder.getStoreAddressId() != null && cardOrder.getStoreAddressId() >= 0) {
        	StoreAddress storeAddress = storeAddressMapper.findStoreAddressByStoreAddressId(cardOrder.getStoreAddressId());
        	if(storeAddress != null) {
        		cardOrder.setAgent5Ids(cardOrder.getAgent5Ids());
        		cardOrder.setAgent6Ids(cardOrder.getAgent6Ids());
        	}
        }
        //订单状态
        cardOrder.setStatus(0);
        //创建时间
        cardOrder.setCreatedDate(new Date());
        log.info("插入card_order订单信息:" + cardOrder);
        int result = cardOrderMapper.insertSelective(cardOrder);
        log.info("插入card_order订单信息是否成功:" + result);
        if(result > 0){
            //随单开通权益会员:同事务建待支付权益占位(status=9),支付成功回调开卡后一并激活
            if (params.get("buyVipCardId") != null) {
                VipBenefit vb = new VipBenefit();
                vb.setUserId(user.getUserId());
                vb.setOriginUserId(user.getUserId());
                vb.setVipCardId(Long.valueOf(String.valueOf(params.get("buyVipCardId"))));
                vb.setSourceOrderNo(orderNo);
                vb.setStoreId(userInfoMapper.queryStoreIdByUserId(user.getUserId()));
                int nowStoreId = user.getNowStoreId();
                vb.setStoreAddrId(nowStoreId > 0 ? (long) nowStoreId : null);
                vb.setOriginPrice(new BigDecimal(String.valueOf(params.get("vipCardPrice"))));
                // 下单即快照会员卡剩余整天数(此刻本单会员卡尚未续期,故为「本单会员卡之前」的剩余天数);
                // 激活时据此顺延生效/到期,无有效期为0=加购立即生效
                vb.setDeferDays(memberRemainDays(user.getUserId()));
                vb.setStatus(9);
                vb.setTransferCount(0);
                vb.setTransferable(1);
                vipBenefitMapper.insertSelective(vb);
            }
            Map<String,Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("paySum", cardOrder.getRealPayment()+"");
            return map;
        }
        return null;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后回调更新订单
     */
    @Override
    public int updateCardOrder(String orderNo, BigDecimal wallet, String transaction_id, Integer payType, Integer autoPay) throws ParseException {

        //根据订单号查询订单
        CardOrder cardOrder = cardOrderMapper.selectByOrderNo(orderNo);
        log.info("待更新会员卡：" + cardOrder);
        if (cardOrder == null || cardOrder.getStatus() == 4) { //不存在或已处理的不再处理
            return 0;
        }
        //根据健身卡id查询出健身卡
        FitCard fitCard = fitCardMapper.selectByPrimaryKey(cardOrder.getCardId());
        //支付方式
        cardOrder.setPayType(payType);
        //流水号
        cardOrder.setTransactionNo(transaction_id);
        //订单状态
        cardOrder.setStatus(4);
        //完成时间
        Date nowTime = new Date();
        cardOrder.setFinishTime(nowTime);
        //卡有效期
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        //查出健身卡有效期
        /*Map<String,Object> cardOrderMap = cardOrderMapper.queryCardDetailByUserId(cardOrder.getUserId());
        log.info("购卡前查询用户会员卡信息:" + cardOrderMap);
        //Boolean flag = false;
        String endTime = df.format(nowTime);
        if (cardOrderMap != null){
            //判断当前时间和有效期时间大小 按时间大的更新
            String cpTime = (String) cardOrderMap.get("validityDate");
            Date bt = df.parse(cpTime);
            Date et = df.parse(df.format(nowTime));
            if(et.before(bt)){    //当前时间小于过期时间按过期时间算， 否则按当前时间算起
                endTime = cpTime;
            }
            log.info("设备validityDate问题排查:" + cpTime + "/" + bt + "/" + et + "/" + et.before(bt) + "/" + endTime);
        }
        Long days = Long.valueOf(fitCard.getValidity());
        String validityDate = df.format(df.parse(endTime).getTime() + days * 24l * 60l * 60l * 1000l);
        cardOrder.setValidityDate(df.parse(validityDate));
        if(autoPay != null)cardOrder.setAutoPay(autoPay);*/        
        //扣除优惠券
        if (cardOrder.getCouponId() != null){
            Coupon coupon = couponMapper.selectByCouponId(cardOrder.getCouponId());
            coupon.setCouponStatus(1);
            couponMapper.updateByPrimaryKeySelective(coupon);
        }
        //更新用户手环id 为 0  方便前端判断下次续费直接跳转历史会员卡的详情
        //先查询是否有够卡记录
        Long userId = cardOrder.getUserId();

        //try {
            Map<String, Object> queryMap = userInfoMapper.getCardStatus(userId);
            log.info("待更新user:" + queryMap);
            if(null != queryMap){
                UserInfo userInfo = new UserInfo();
                userInfo.setUserId(userId);
                
                boolean isValidity = true;
                Device myDevice = null;
                if(cardOrder.getDeviceId() != null && cardOrder.getDeviceId() > 0) {
                	myDevice = deviceMapper.selectByPrimaryKey(cardOrder.getDeviceId());
                }else {
                	myDevice = deviceMapper.selectUserValidity(userId);
                	if(myDevice == null) {
                		isValidity = false;
                		myDevice = deviceMapper.selectByUser(userId);
                	}
                }
                log.info("是否第一次买卡:" +  StringUtils.isBlank((String) queryMap.get("wristId")) );
                //if(StringUtils.isBlank((String) queryMap.get("wristId"))){//说明第一次买卡
                    userInfo.setWristId("0");
                //    Device dv = deviceMapper.queryHaveDeviceById(userId, cardOrder.getStoreAddressId());
                //    if(dv == null){  //说明以前在此门店从未买过卡
                     if(myDevice == null) {
                        log.info("从未买过卡:" +  myDevice );
                        //新增设备信息
                        Device device = new Device();
                        device.setDeviceName(fitCard.getCardName());  //购卡是写死后台门店可更新
                        device.setStoreAddressId(cardOrder.getStoreAddressId());
                        applyCardStoreScope(device, fitCard, cardOrder);
                        device.setProxyId(userId);
                        //device.setDevicePrice(cardOrder.getWristPrice());
                        device.setInventory(1);    //数量默认1个
                        device.setStatus((byte)1);  //标识待确认领取/寄送，确认后需要补录手环编号
                        device.setType(fitCard.getCardType()); //卡类型
                        device.setFitCardId(cardOrder.getCardId()); //卡id
                        device.setDevicePrice(cardOrder.getRealPayment());  //实际付款
                        device.setValidityDate(cardOrder.getValidityDate());          //有效期
                        device.setCreateTime(nowTime);
                        device.setAddressId(cardOrder.getUserAddressId());
                        device.setValidity(fitCard.getValidity());
                        if(queryMap.get("nickname") != null){
                            device.setProxyName(EmojiParser.parseToHtmlHexadecimal((String) queryMap.get("nickname")));
                        }
                        device.setPhone((String) queryMap.get("phone"));
                        device.setAutoPay(cardOrder.getAutoPay()); //是否自动续费类型
                        device.setNextPrice(cardOrder.getNextPrice()); //次月价格（自动续费需要）
                        device.setNextPrice2(cardOrder.getNextPrice2());
                        device.setNextPrice3(cardOrder.getNextPrice3()); //次月价格（自动续费需要）
                        device.setNextPriceTitle(cardOrder.getNextPriceTitle());
                        device.setNextPriceTitle2(cardOrder.getNextPriceTitle2()); //次月价格（自动续费需要）
                        device.setNextPriceTitle3(cardOrder.getNextPriceTitle3());
                        device.setSelecteFree(cardOrder.getSelecteFree()); //0: 未勾选免费服务  1：勾选了免费服务
                        device.setUseCount(cardOrder.getUseCount());
                        device.setBuyCount(cardOrder.getBuyCount());
                        log.info("第一次买卡，更新用户信息:" +  device );
                        log.info("第一次买卡，更新用户信息:" +  cardOrder );
                        deviceMapper.insertSelective(device);
                        syncDeviceStoreScope(device.getDeviceId(), device);

                        cardOrder.setDeviceId(device.getDeviceId());

                    }else{  //说明以前买此门店的卡，只需要更新原信息就可
                        log.info("前买此门店的卡，更新myDevice:" +  myDevice );
                        Device oldDevice = new Device();
                        oldDevice.setDeviceId(myDevice.getDeviceId());
                        if(fitCard != null && !myDevice.getFitCardId().equals(fitCard.getFitCardId())) {
                        	oldDevice.setDeviceName(fitCard.getCardName());
                        	oldDevice.setValidity(fitCard.getValidity());
                            oldDevice.setType(fitCard.getCardType()); //卡类型
                        }
                        oldDevice.setStoreAddressId(cardOrder.getStoreAddressId());
                        applyCardStoreScope(oldDevice, fitCard, cardOrder);
                        oldDevice.setStatus((byte)1);  //待确认状态 可以领取手环，确认后需要补录手环编号
                        oldDevice.setFitCardId(cardOrder.getCardId()); //卡id
                        oldDevice.setDevicePrice(cardOrder.getRealPayment());  //实际付款
                        oldDevice.setAddressId(cardOrder.getUserAddressId());
                        if(queryMap.get("nickname") != null){
                            oldDevice.setProxyName(EmojiParser.parseToHtmlHexadecimal((String) queryMap.get("nickname")));
                        }
                        oldDevice.setPhone((String) queryMap.get("phone"));
                        oldDevice.setValidityDate(cardOrder.getValidityDate());   //更新有效期
                        //oldDevice.setOnLineTime(nowTime);
                        //oldDevice.setCreateTime(nowTime);
                        oldDevice.setAutoPay(cardOrder.getAutoPay()); //是否自动续费类型
                        oldDevice.setNextPrice(cardOrder.getNextPrice()); //次月价格（自动续费需要）
                        oldDevice.setNextPrice2(cardOrder.getNextPrice2());
                        oldDevice.setNextPrice3(cardOrder.getNextPrice3()); //次月价格（自动续费需要）
                        oldDevice.setNextPriceTitle(cardOrder.getNextPriceTitle());
                        oldDevice.setNextPriceTitle2(cardOrder.getNextPriceTitle2()); //次月价格（自动续费需要）
                        oldDevice.setNextPriceTitle3(cardOrder.getNextPriceTitle3());
                        oldDevice.setSelecteFree(cardOrder.getSelecteFree()); //0: 未勾选免费服务  1：勾选了免费服务
                        oldDevice.setUseCount((isValidity ? myDevice.getUseCount() : 0) + cardOrder.getUseCount());
                        oldDevice.setUsedCount(isValidity ? myDevice.getUsedCount() : 0);
                        oldDevice.setBuyCount(cardOrder.getBuyCount());
                        deviceMapper.updateByPrimaryKeySelective(oldDevice);
                        syncDeviceStoreScope(myDevice.getDeviceId(), oldDevice);
                        log.info("前买此门店的卡，更新oldDevice:" + isValidity + " => " +  oldDevice );
                    }

                /*}else{ //续费  (更新有效期)
                    Device deviceUpdate = new Device();
                    deviceUpdate.setProxyId(userId);
                    Long storeId = cardOrderMapper.queryStoreIdByAddress(cardOrder.getStoreAddressId());
                    deviceUpdate.setStoreId(storeId);
                    deviceUpdate.setStoreAddressId(cardOrder.getStoreAddressId());
                    deviceUpdate.setStoreAddrIds(cardOrder.getStoreAddrIds());
                    deviceUpdate.setType(fitCard.getCardType());   //卡类型
                    deviceUpdate.setFitCardId(cardOrder.getCardId());   //卡id
                    deviceUpdate.setDevicePrice(cardOrder.getRealPayment());  //实际付款
                    deviceUpdate.setValidityDate(cardOrder.getValidityDate());
                    deviceUpdate.setAutoPay(cardOrder.getAutoPay()); //是否自动续费类型
                    deviceUpdate.setNextPrice(cardOrder.getNextPrice()); //次月价格（自动续费需要）
                    deviceUpdate.setNextPrice2(cardOrder.getNextPrice2());
                    deviceUpdate.setNextPrice3(cardOrder.getNextPrice3()); //次月价格（自动续费需要）
                    deviceUpdate.setNextPriceTitle(cardOrder.getNextPriceTitle());
                    deviceUpdate.setNextPriceTitle2(cardOrder.getNextPriceTitle2()); //次月价格（自动续费需要）
                    deviceUpdate.setNextPriceTitle3(cardOrder.getNextPriceTitle3());
                    deviceUpdate.setSelecteFree(cardOrder.getSelecteFree()); //0: 未勾选免费服务  1：勾选了免费服务
                    deviceUpdate.setUseCount(deviceUpdate.getUseCount() + cardOrder.getUseCount());
                    deviceUpdate.setBuyCount(cardOrder.getBuyCount());
                    log.info("续费:" +  deviceUpdate );
                    deviceMapper.updateValiditByUid(deviceUpdate);
                }*/
                //add new 更新人脸识别状态为2 已购卡未认证 1:已认证
                if((int)queryMap.get("faceStatus") != 1){
                    userInfo.setFaceStatus(2);
                }
                if(StringUtils.isNotBlank(userInfo.getWristId()) || userInfo.getFaceStatus() != null){
                    userInfoMapper.updateByPrimaryKeySelective(userInfo);
                }

            }
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        return cardOrderMapper.updateByPrimaryKey(cardOrder);
    }

    @Override
    public Map<String,Object> selectCardOrderByUSerId(Long userId) {
        return cardOrderMapper.selectCardOrderByUSerId(userId);
    }

    @Override
    public Map<String, Object> queryCardInfoByUserId(Long userId) {
        return deviceMapper.selectByProxyId(userId);
    }

    @Override
    public int updateOrderStatus(String orderNo, int status){
        return cardOrderMapper.updateOrderStatus(orderNo, status);
    }
	@Override
	public List<Map<String, Object>> selectCardOrderList(Query query) {
		List<Map<String, Object>> list = cardOrderMapper.queryList(query);
        if(CollectionUtils.isNotEmpty(list)){
            for(Map<String, Object> item : list){
                if(item.get("storeAddressId") != null && item.get("storeAddressId") != "") {
                	Long storeAddressId = Long.valueOf(String.valueOf(item.get("storeAddressId")));
                	StoreAddress storeAddr = storeAddressMapper.selectByPrimaryKey(storeAddressId);
                	if(storeAddr != null) {
                		item.put("storeName", storeAddr.getStoreName());
                	}
                }
            }
        }
        return list;
	}
	@Override
	public int queryCardOrderCount(Query query) {
		return cardOrderMapper.queryTotal(query);
	}

}
