package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.StoreAddressService;
import com.dlc.modules.api.service.VipBenefitService;
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
    private VipBenefitService vipBenefitService;
    @Autowired
    private com.dlc.modules.api.service.ApiFlashSaleService apiFlashSaleService;

    private Logger log = LoggerFactory.getLogger(getClass());

    /** 通卡：fit_card.storeAddrIds 为空，全部门店可用 */
    private boolean isUniversalCard(FitCard fitCard) {
        return fitCard == null || StringUtils.isBlank(fitCard.getStoreAddrIds());
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
        Long fitCardId = Long.valueOf(String.valueOf(params.get("fitCardId")));
        // 权益类型会员卡(cardNature=1)须持有明确绑定当前会员卡的有效权益才能购买/续费。校验放在本方法(手动下单与
        // 自动代扣 papAutoPay 的共用入口)而非 Controller,两条路径统一拦截;自动代扣被拦时
        // 异常由 papAutoPay 循环 catch,仅跳过该用户当日续费,不影响其他用户
        // fitCardId 来自下单参数；重新查询 fit_card，不能信任客户端自行声明 cardNature/cardType。
        FitCard natureCard = fitCardMapper.getFitCardInfo(fitCardId);
        VipBenefit purchaseBenefit = null;
        if (natureCard != null && Integer.valueOf(1).equals(natureCard.getCardNature())) {
            if (!vipBenefitService.hasValidBenefitForFitCard(user.getUserId(), fitCardId)) {
                throw new RRException(CodeAndMsg.ERROR_FIT_CARD_NEED_BENEFIT);
            }
            // cardType=0 表示月卡。系统只允许持有一张有效权益，因此直接取该会员当前有效权益的到期日做购买边界判断。
            if (Integer.valueOf(0).equals(natureCard.getCardType())) {
                purchaseBenefit = vipBenefitService.latestValidBenefit(user.getUserId());
            }
        }
        CardOrder cardOrder = new CardOrder();
        //用户id
        cardOrder.setUserId(user.getUserId());
        //健身卡id
        cardOrder.setCardId(fitCardId);
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
        if (purchaseBenefit != null) {
            // validityDate 是本次下单完成后的会员卡到期日；validity 是单张月卡天数；buyCount 是本次购买张数。
            // 三个值都在 Service 内统一校验，使手动购买与自动续费复用同一套限制。
            validateBenefitMonthlyCardLimit(purchaseBenefit, cardOrder.getValidityDate(),
                    natureCard.getValidity(), cardOrder.getBuyCount());
        }
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
        //限时秒杀来源(Controller 已校验活动有效并把秒杀价写进 paySum)
        if (params.get("flashSaleActivityId") != null
                && StringUtils.isNotBlank(String.valueOf(params.get("flashSaleActivityId")))) {
            cardOrder.setFlashSaleActivityId(Long.valueOf(String.valueOf(params.get("flashSaleActivityId"))));
        }
        log.info("插入card_order订单信息:" + cardOrder);
        int result = cardOrderMapper.insertSelective(cardOrder);
        log.info("插入card_order订单信息是否成功:" + result);
        if(result > 0){
            Map<String,Object> map = new HashMap<>();
            map.put("orderNo", orderNo);
            map.put("paySum", cardOrder.getRealPayment()+"");
            return map;
        }
        return null;
    }

    /**
     * 校验权益性质月卡的购买数量和购买周期是否仍落在当前权益有效期内。
     *
     * <p>本项目的 {@code nextValidityDate} 表示本次下单后的会员卡到期日，且到期日当天仍可使用，
     * 因此本次购买周期的起始日为：{@code nextValidityDate - (validityDays - 1)}。
     * 起始日晚于权益到期日，说明本次购买已经跨出权益范围；例如权益 8 月 19 日到期，
     * 月卡每张 30 天，本次到期日为 9 月 18 日时，周期起始日仍是 8 月 20 日，必须拒绝。</p>
     *
     * @param benefit 当前会员唯一一张有效权益，使用其 expireTime 判断购买边界
     * @param nextValidityDate 本次下单完成后的会员卡到期日，来源于下单参数 validityDate
     * @param validityDays 单张月卡的有效天数，来源于 fit_card.validity
     * @param buyCount 本次购买张数，来源于下单参数 buyCount，最多为 12
     */
    static void validateBenefitMonthlyCardLimit(VipBenefit benefit, Date nextValidityDate,
                                                Integer validityDays, Integer buyCount) {
        // 任一边界数据缺失或为非法值时明确拒绝，避免绕过权益期限校验继续创建订单。
        if (benefit == null || benefit.getExpireTime() == null || nextValidityDate == null
                || validityDays == null || validityDays <= 0 || buyCount == null || buyCount <= 0) {
            throw new RRException(CodeAndMsg.ERROR_FIT_CARD_MONTH_LIMIT);
        }
        // buyCount 是本次下单张数，12 张上限在创建订单前拦截。
        if (buyCount > 12) {
            throw new RRException(CodeAndMsg.ERROR_FIT_CARD_MONTH_LIMIT);
        }

        // 日期统一归零后只比较自然日，不让订单或权益记录中的时分秒改变边界结果。
        Calendar nextPeriodStart = Calendar.getInstance();
        nextPeriodStart.setTime(startOfDay(nextValidityDate));
        nextPeriodStart.add(Calendar.DAY_OF_MONTH, -(validityDays - 1));
        if (nextPeriodStart.getTime().after(startOfDay(benefit.getExpireTime()))) {
            throw new RRException(CodeAndMsg.ERROR_FIT_CARD_MONTH_LIMIT);
        }
    }

    /** 购买限制按自然日比较，忽略订单和权益记录中的时分秒。 */
    private static Date startOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
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
        //限时秒杀会员卡：支付成功后 CAS 扣减秒杀库存(幂等由上面 status==4 早返回保证,只会执行一次)。
        //会员卡无商品硬库存,秒杀库存仅活动表承载;高并发下极端超卖仅记警告不阻断已付订单(P0 取舍,见《秒杀功能.md》Q1b)。
        if (cardOrder.getFlashSaleActivityId() != null) {
            int dec = apiFlashSaleService.increaseSold(cardOrder.getFlashSaleActivityId(), cardOrder.getCardId(), cardOrder.getCreatedDate());
            if (dec <= 0) {
                log.warn("会员卡秒杀库存扣减失败(已售罄/活动失效) orderNo={}, activityId={}", orderNo, cardOrder.getFlashSaleActivityId());
            }
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
