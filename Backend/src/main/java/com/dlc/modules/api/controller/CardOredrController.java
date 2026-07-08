package com.dlc.modules.api.controller;

import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.CouponMapper;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.FitCardMapper;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.entity.Device;
import com.dlc.modules.api.entity.FitCard;
import com.dlc.modules.api.service.CardOrderService;
import com.dlc.modules.api.service.FitCardService;
import com.dlc.modules.api.service.IncomePayDetailService;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/14/014
 */
@RestController
@RequestMapping("/api/cardOrder")//cardOrder
public class CardOredrController extends BaseController{

    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private CardOrderService cardOrderService;
    @Autowired
    private FitCardService fitCardService;
    @Autowired
    private FitCardMapper fitCardMapper;
    @Autowired
    private IncomePayDetailService incomePayDetailService;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private SysIncomePayDetailService sysIncomePayDetailService;
    
    /**
     * 列表
     * @param topicId
     * @param request
     * @return
     */
    @RequestMapping("/queryList")
    public R queryList(@RequestParam Map<String,Object> queryMap, HttpServletRequest request){
        /*getUserId(request)*/
        queryMap.put("userId", getUserId(request));
        Query query =new Query(queryMap);
        List<Map<String,Object>> queryList = cardOrderService.selectCardOrderList(query);
        int total = cardOrderService.queryCardOrderCount(query);
        PageUtils pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }


    /**
     *  @Auther:YD
     *  @parameters:健身卡id：cardId,paySum总价，wrist手环类型，wristPrice手环价格，storeAddressId门店id，userAddressId收货地址id，sendTyp配送方式
     *  卡订单
     */
    @RequestMapping("/createOrder")
    public R createFitCardOrder(@RequestParam Map<String,Object> params, HttpServletRequest request){
        log.info("创建订单："+ params +"========================");
        BigDecimal paySum = new BigDecimal(String.valueOf(params.get("paySum")));   //实际付款金额
        if(paySum.compareTo(BigDecimal.ZERO) == -1){
            //return  R.reError("金额不能为负数！该交易存在恶意攻击风险, 已被标记");
        }
        UserInfoVo user = getUserVo(request);
        //根据卡id查询出健身卡详情
        //Map<String,Object> fitCard = fitCardService.queryFitCardInfo(Long.valueOf(String.valueOf(params.get("fitCardId"))));
        //查询用户历史健身卡订单  (20190311 改查设备信息表)
        //Map<String,Object> cardOrderMap = cardOrderService.queryCardInfoByUserId(user.getUserId());
        Device device = deviceMapper.selectUserValidity(user.getUserId());
        boolean isValidity = true;
        if(device == null) {
    		isValidity = false;
    		device = deviceMapper.selectByUser(user.getUserId());
    	}
        log.info("创建订单device："+ device +"========================");
        //卡实际金额是否和传来的金额一致
        //Map<String,Object> fitCard = fitCardService.queryFitCardInfo(Long.valueOf(String.valueOf(params.get("fitCardId"))));
        FitCard fitCard = fitCardMapper.getFitCardInfo(Long.valueOf(String.valueOf(params.get("fitCardId"))));
        if(fitCard == null) {
        	return R.error("卡套餐不存在");
        }
        boolean isNewUser = !sysIncomePayDetailService.hasValidCardPurchase(user.getUserId());
        BigDecimal cardPrice = fitCard.resolveSalePrice(isNewUser);
        if(cardPrice.compareTo(paySum) == -1 || paySum.compareTo(new BigDecimal(-1)) == 0){ //实际卡价格小于传进来的价格
            params.put("paySum", cardPrice);
            paySum = cardPrice;
        }
        /*if(device != null) {
        	params.put("autoPay", device.getAutoPay()); //是否自动续费
	        params.put("nextPrice", device.getNextPrice());
	        params.put("nextPrice2", device.getNextPrice2());
	        params.put("nextPrice3", device.getNextPrice3());
	        params.put("nextPriceTitle", device.getNextPriceTitle());
	        params.put("nextPriceTitle2", device.getNextPriceTitle2());
	        params.put("nextPriceTitle3", device.getNextPriceTitle3());
	        params.put("useCount", fitCard.get("useCount")); //次卡
	        params.put("type", device.getType()); //次卡
        }else if(fitCard != null) {*/
	        params.put("autoPay", fitCard.getAutoPay()); //是否自动续费
	        params.put("nextPrice", fitCard.getNextPrice());
	        params.put("nextPrice2", fitCard.getNextPrice2());
	        params.put("nextPrice3", fitCard.getNextPrice3());
	        params.put("nextPriceTitle", fitCard.getNextPriceTitle());
	        params.put("nextPriceTitle2", fitCard.getNextPriceTitle2());
	        params.put("nextPriceTitle3", fitCard.getNextPriceTitle3());
	        params.put("useCount", fitCard.getUseCount()); //次卡
	        params.put("type", fitCard.getCardType()); //次卡
	        params.put("storeAddrIds", StringUtils.isBlank(fitCard.getStoreAddrIds()) ? "" : fitCard.getStoreAddrIds());
        //}
        
        Map<String,Object> map = new HashMap<>();
        if (device != null){
        	if(device.getType() == 10 && fitCard.getCardType() != 10) {
        		//return R.error("本次续费只能是次卡");
        		isValidity = false;
        	}
        	if(device.getType() != 10 && fitCard.getCardType() == 10) {
        		return R.error("非次卡不可续费次卡");
        	}
        	params.put("deviceId", device.getDeviceId());
        	if(isValidity) {
        		params.put("oldValidityDate", device.getValidityDate());
        		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd 08:00:00");
        		//续费:在原有效期基础上顺延validity天,原有效期已含"当天"语义,这里不能再减1
        		String validityDate = df.format(device.getValidityDate().getTime() + fitCard.getValidity() * 24l * 60l * 60l * 1000l);
        		params.put("validityDate", validityDate);
        	}else{
        		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd 08:00:00");
        		//新周期从"今天"起算,今天算第1天,故减1天,避免比配置天数多算1天(过期判定按日期、含当天有效)
            	String validityDate = df.format((new Date()).getTime() + Math.max(0, fitCard.getValidity() - 1) * 24l * 60l * 60l * 1000l);
            	params.put("validityDate", validityDate); 
        	}

            if(isValidity && fitCard.getFitCardId().equals(device.getFitCardId())) {
            	params.put("buyCount", device.getBuyCount() + 1);
                
                //续费
            	String[] s1 = CommonUtil.tryStrings(fitCard.getNextPriceTitle());
            	if (s1.length > 0 && device.getBuyCount() >= 1 && fitCard.getNextPrice() != null && fitCard.getNextPrice().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
            		cardPrice = fitCard.getNextPrice();
                }
            	String[] s2 = CommonUtil.tryStrings(fitCard.getNextPriceTitle2());
                if (device.getBuyCount() > s1.length && fitCard.getNextPrice2() != null && fitCard.getNextPrice2().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
                	cardPrice = fitCard.getNextPrice2();
                }
                String[] s3 = CommonUtil.tryStrings(fitCard.getNextPriceTitle3());
                if (device.getBuyCount() > s1.length + s2.length && fitCard.getNextPrice3() != null && fitCard.getNextPrice3().compareTo(BigDecimal.ZERO) > 0) {//次月扣款金额
                	cardPrice = device.getNextPrice3();
                }
                params.put("paySum", cardPrice); 
            }else{
            	params.put("buyCount", 1);
            }           

            if ( params.get("couponId") != null && StringUtils.isNotBlank(params.get("couponId").toString()) ){
                Coupon coupon = couponMapper.selectByCouponId(Long.valueOf(String.valueOf(params.get("couponId"))));
                if (coupon != null) {
                    if(coupon.getLimitPrice() != null && coupon.getLimitPrice().compareTo(paySum) > 0) {
                    	return R.error("优惠券门槛未达到");
                    }
                }else {
                	return R.error("优惠券不存在");
                }
            }
            
            map = cardOrderService.createFitCardOrder(user,params,1);
        }else {
        	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd 08:00:00");
        	//新周期从"今天"起算,今天算第1天,故减1天,避免比配置天数多算1天(过期判定按日期、含当天有效)
        	String validityDate = df.format((new Date()).getTime() + Math.max(0, fitCard.getValidity() - 1) * 24l * 60l * 60l * 1000l);
        	params.put("validityDate", validityDate);     
            params.put("buyCount", 1);   	
            
            if ( params.get("couponId") != null && StringUtils.isNotBlank(params.get("couponId").toString()) ){
                Coupon coupon = couponMapper.selectByCouponId(Long.valueOf(String.valueOf(params.get("couponId"))));
                if (coupon != null) {
                    if(coupon.getLimitPrice() != null && coupon.getLimitPrice().compareTo(cardPrice) > 0) {
                    	return R.error("优惠券门槛未达到");
                    }
                }else {
                	return R.error("优惠券不存在");
                }
            }
            
            //第一次购卡
            map = cardOrderService.createFitCardOrder(user,params,0);
        }
        /*if(map != null && CommonUtil.tryToBigDecimal(String.valueOf(map.get("paySum"))).compareTo(BigDecimal.ZERO) <= 0){
	        //更新健身卡订单
        	try {
		        log.info("-------不用付款更新健身卡订单=========" );
		        String orderNo = String.valueOf(map.get("orderNo"));
		        int res = cardOrderService.updateCardOrder(orderNo, BigDecimal.ZERO, "", ConfigConstant.WXPAY, 0);
		        if (res > 0) {
		            log.info("=========添加自动续费收支明细==========");
		            incomePayDetailService.saveIncomePayDetail(orderNo,"",BigDecimal.ZERO,ConfigConstant.WXPAY);
		        }
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }*/
        return R.reOk(map);
    }
}
