package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.DouyinOpenService;
import com.dlc.common.utils.DouyinPrepareResult;
import com.dlc.common.utils.DouyinVerifyResult;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.CouponMapper;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.service.CouponService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.dlc.modules.sys.dao.SysUserDao;
import com.dlc.modules.sys.entity.SysCouponEntity;
import com.dlc.modules.sys.entity.SysStoreAddressEntity;
import com.dlc.modules.sys.service.SysCouponService;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import com.dlc.modules.sys.service.SysStoreAddressService;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/12 9:19
 * 优惠卷
 */
@RestController
@RequestMapping("/api/coupon")//coupon
public class ApiCouponController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(ApiCouponController.class);
    private static final String MEITUAN_API_BASE = "http://qsg.wuminginfo.com/api/meituan/";

    @Autowired
    private CouponService couponService;
    @Autowired
    private SysCouponService sysCouponService;
    @Autowired
    private SysUserDao sysUserDao;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private DouyinOpenService douyinOpenService;
    @Autowired
    private SysStoreAddressService sysStoreAddressService;
    @Autowired
    private SysIncomePayDetailService sysIncomePayDetailService;

    private static final String NEW_USER_COUPON_FAIL_MSG = "该客户非新人，发放失败";
    private static final String NEW_USER_COUPON_DUPLICATE_MSG = "非常抱歉，此团购券仅限新人使用，不支持核销，需平台直接购买！谢谢！";

    /**
     * 优惠卷列表
     * */
    @RequestMapping("/couponList")
    public R couponList(HttpServletRequest request,Integer couponStatus
    		, @RequestParam(required=false) Long storeAddrId
    		, @RequestParam(required=false) BigDecimal paySum
    		, @RequestParam(required=false) Long fitCardId
    		, @RequestParam(required=false) String couponType){
    	UserInfoVo user = getUserVo(request);        
        List<Map<String,Object>> list = couponService.couponList(user.getUserId(),couponStatus,storeAddrId,paySum,fitCardId, parseCouponTypeList(couponType));
        return R.reOk(list);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  健身卡绑定优惠券
     */
    @RequestMapping("/bindingCoupon")
    public R bindingCoupon(@RequestParam Map<String,Object> params){
        couponService.bindingCoupon(params);
        return R.reOk();
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  优惠券详情
     */
    @RequestMapping("/coupon")
    public R coupon(Long couponId){
    	Coupon detail = couponService.selectByCouponId(couponId);
        return R.reOk(detail);
    }


    /**
     *  @Auther:YD
     *  @parameters:
     *  扣除优惠券
     */
    @RequestMapping("/deductCoupon")
    public R deductCoupon(String orderNo, Coupon coupon){
        return R.reOk(couponService.deductCoupon(orderNo,coupon));
    }
    
	
	/**
	 * 系统优惠券信息
	 */	
	@RequestMapping("/sysCoupon")
    public R sysCoupon(Long couponId){
		SysCouponEntity data = sysCouponService.selectSysCouponById(couponId);
        return R.reOk(data);
    }
	
	/**
     * app端领取/核销系统优惠券
     * storeAddrId 门店地址ID（验券时必填，用于读取美团/抖音门店配置）
     * verifyType 可选：douyin=抖音团购核销，不传或其它=美团
     */
    @RequestMapping("/sendSysCoupon")
    public R sendCoupon(@RequestParam(required=false) Long couponId, @RequestParam(required=false) String storeAddrId,
    		@RequestParam(required=false) String couponCode,
    		@RequestParam(required=false) String verifyType, HttpServletRequest request) {
        Long userId = getUserId(request);
        // 平台核销成功后若后续失败，catch 中撤销
        boolean platformVerified = false;
        boolean verifyIsDouyin = false;
        DouyinVerifyResult douyinVerifyResult = null;
        String meituanStoreIdCancel = null;
        String meituanNumCancel = null;
        Long meituanDealIdCancel = null;

        try {
	    	SysCouponEntity sysCoupon = null;
	    	int couponType = 0;
	    	Long issueGoodsId = null;
	    	//验券
	    	if(StringUtils.isNotBlank(couponCode)) {
	    		if(StringUtils.isBlank(storeAddrId)) {
	    			throw new Exception("请选择门店");
	    		}
	    		SysStoreAddressEntity storeAddress = sysStoreAddressService.queryObject(Long.valueOf(storeAddrId));
	    		if(storeAddress == null) {
	    			throw new Exception("门店不存在");
	    		}
	    		boolean isDouyin = "douyin".equalsIgnoreCase(StringUtils.trimToEmpty(verifyType));
	    		if(isDouyin) {
	    			if(StringUtils.isBlank(storeAddress.getDouyinPoiId())) {
	    				throw new Exception("该门店未配置抖音门店ID");
	    			}
	    			String poiId = storeAddress.getDouyinPoiId().trim();
	    			DouyinPrepareResult prepare = douyinOpenService.prepare(couponCode);
	    			List<SysCouponEntity> list = findSysCouponsByDouyinSku(prepare.getSkuId(), prepare.getProductId());
	    			if(list == null || list.isEmpty()) {
	    				throw new Exception("优惠券暂没上架");
	    			}
	    			sysCoupon = pickSysCouponForUser(list, userId);
	    			throwIfSysCouponNotPicked(list, userId, sysCoupon);
	    			String issueErr = checkSysCouponForIssue(sysCoupon);
	    			if(issueErr != null) {
	    				throw new Exception(issueErr);
	    			}
	    			if(StringUtils.isBlank(sysCoupon.getStoreAddrIds())) {
	    				throw new Exception("该优惠券未配置可用门店，不可核销");
	    			}
	    			if(("," + sysCoupon.getStoreAddrIds().trim() + ",").indexOf("," + storeAddrId.trim() + ",") < 0) {
	    				throw new Exception("该优惠券不可在此门店核销");
	    			}
	    			douyinVerifyResult = douyinOpenService.verify(prepare, poiId);
	    			platformVerified = true;
	    			verifyIsDouyin = true;
	    			couponType = 1;
	    			issueGoodsId = parseVerifyGoodsId(prepare.getSkuId(), prepare.getProductId());
	    		} else {
	    			if(storeAddress.getGoodsIdStoreId() == null) {
	    				throw new Exception("该门店未配置美团门店ID");
	    			}
	    			String meituanStoreId = String.valueOf(storeAddress.getGoodsIdStoreId());
		    		String data = CommonUtil.httpsRequest("http://qsg.wuminginfo.com/api/meituan/meituanPrepareFromApi?storeId=" + meituanStoreId + "&meituanNum=" + couponCode, "GET", null);
		    		if(StringUtils.isBlank(data)) {
		    			throw new Exception("参数错误");
		    		}
		    		JSONObject json = JSONObject.parseObject(data);
		    		if(json == null || json.isEmpty()) {
		    			throw new Exception("参数错误");
		    		}
		    		if(json.getInteger("code") != 200) {
		    			throw new Exception(json.getString("msg"));
		    		}
		    		json = json.getJSONObject("data");
		    		if(!json.getString("code").equals("OP_SUCCESS")) {
		    			throw new Exception(json.getString("msg"));
		    		}
		    		json = json.getJSONObject("data");
		    		SysCouponEntity search = new SysCouponEntity();
		    		search.setGoodsId(json.getLong("dealGroupId"));
		    		search.setCouponStatus(1L);
		    		List<SysCouponEntity> list = sysCouponService.selectSysCouponList(search);
		    		if(list == null || list.isEmpty()) {
		    			throw new Exception("优惠券暂没上架");
		    		}
		    		sysCoupon = pickSysCouponForUser(list, userId);
		    		throwIfSysCouponNotPicked(list, userId, sysCoupon);
		    		String issueErr = checkSysCouponForIssue(sysCoupon);
		    		if(issueErr != null) {
		    			throw new Exception(issueErr);
		    		}
		    		if(StringUtils.isBlank(sysCoupon.getStoreAddrIds())) {
		    			throw new Exception("该优惠券未配置可用门店，不可核销");
		    		}
		    		if(("," + sysCoupon.getStoreAddrIds().trim() + ",").indexOf("," + storeAddrId.trim() + ",") < 0) {
		    			throw new Exception("该优惠券不可在此门店核销");
		    		}
		    		couponType = 2;
		    		issueGoodsId = json.getLong("dealGroupId");
		    		meituanStoreIdCancel = meituanStoreId;
		    		meituanNumCancel = couponCode;
		    		meituanDealIdCancel = issueGoodsId;
		    		data = CommonUtil.httpsRequest("http://qsg.wuminginfo.com/api/meituan/meituanHexiaoFromApi?storeId=" + meituanStoreId + "&meituanNum=" + couponCode + "&count=1", "GET", null);
		    		if(StringUtils.isBlank(data)) {
		    			throw new Exception("参数错误");
		    		}
		    		json = JSONObject.parseObject(data);
		    		if(json == null || json.isEmpty()) {
		    			throw new Exception("参数错误");
		    		}
		    		if(json.getInteger("code") != 200) {
		    			throw new Exception(json.getString("msg"));
		    		}
		    		json = json.getJSONObject("data");
		    		if(!json.getString("code").equals("OP_SUCCESS")) {
		    			throw new Exception(json.getString("msg"));
		    		}
		    		platformVerified = true;
	    		}
	    	}
	    	if(sysCoupon == null) {
	    		sysCoupon = sysCouponService.selectSysCouponById(couponId);
	    	}
	        if(sysCoupon == null) {
	        	throw new Exception("参数错误");
	        }
	        if(!isSysCouponOnShelf(sysCoupon)) {
	        	throw new Exception("优惠券已下架");
	        }
	        String issueErr = checkSysCouponForIssue(sysCoupon);
	        if(issueErr != null) {
	        	throw new Exception(issueErr);
	        }
	        if(isNewUserCoupon(sysCoupon)) {
	        	if(sysIncomePayDetailService.hasValidCardPurchase(userId)) {
	        		throw new Exception(NEW_USER_COUPON_FAIL_MSG);
	        	}
	        	if(hasClaimedNewUserCoupon(userId)) {
	        		throw new Exception(NEW_USER_COUPON_DUPLICATE_MSG);
	        	}
	        }

	        Map<String, Object> updateMap = new HashMap<String, Object>();
	        Date vDate = DateUtils.addDate(new Date(), sysCoupon.getValidity().intValue());
	        updateMap.put("couponMoney", sysCoupon.getCouponPrice());
	        updateMap.put("validDay", vDate);
	        updateMap.put("limitPrice", sysCoupon.getLimitPrice());
	        updateMap.put("couponTitle", sysCoupon.getCouponTitle());
	        updateMap.put("storeAddrIds", StringUtils.isNotBlank(storeAddrId) ? storeAddrId : sysCoupon.getStoreAddrIds());
	        updateMap.put("sysCouponId", sysCoupon.getSysCouponId());
	        if(issueGoodsId != null) {
	        	updateMap.put("goodsId", issueGoodsId);
	        } else if(sysCoupon.getGoodsId() != null) {
	        	updateMap.put("goodsId", sysCoupon.getGoodsId());
	        }
	        updateMap.put("fitCardIds", sysCoupon.getFitCardIds());
	        updateMap.put("operateStoreId", 0);
	        updateMap.put("operateStore", null);
	        updateMap.put("userId", userId);
	        updateMap.put("couponType", couponType);
	        updateMap.put("couponNew", sysCoupon.getCouponNew());
	        sysUserDao.insertCouponInfo(updateMap);

	        SysCouponEntity up = new SysCouponEntity();
	        up.setSysCouponId(sysCoupon.getSysCouponId());
	        long sendCount = sysCoupon.getSendCount() == null ? 0L : sysCoupon.getSendCount().longValue();
	        up.setSendCount(sendCount + 1);
	        sysCouponService.updateSysCoupon(up);
        } catch (Exception e) {
        	if(platformVerified) {
        		if(verifyIsDouyin) {
        			if(douyinVerifyResult != null) {
        				douyinOpenService.cancelVerify(douyinVerifyResult.getCertificateId(), douyinVerifyResult.getVerifyId());
        			}
        		} else {
        			meituanReverseConsume(meituanStoreIdCancel, meituanNumCancel, meituanDealIdCancel);
        		}
        	}
        	log.warn("sendSysCoupon失败: {}", e.getMessage());
        	return R.error(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "操作失败");
        }
        return R.reOk();
    }

    /** 撤销美团验券（仅发起请求并记录日志，不校验返回结果） */
    private void meituanReverseConsume(String meituanStoreId, String meituanNum, Long dealId) {
    	if(dealId == null) {
    		log.warn("美团撤销核销跳过：dealId 为空 storeId={}, num={}", meituanStoreId, meituanNum);
    		return;
    	}
    	String url = MEITUAN_API_BASE + "meituanReverseConsumeFromApi?storeId=" + meituanStoreId
    			+ "&meituanNum=" + meituanNum + "&dealId=" + dealId;
    	try {
    		log.info("美团撤销核销请求: {}", url);
    		String data = CommonUtil.httpsRequest(url, "GET", null);
    		log.info("美团撤销核销响应: {}", data);
    	} catch (Exception e) {
    		log.warn("美团撤销核销请求异常 storeId={}, num={}", meituanStoreId, meituanNum, e);
    	}
    }

    /** 发券前校验，避免核销成功后因配置缺失 NPE */
    private String checkSysCouponForIssue(SysCouponEntity sysCoupon) {
    	if(sysCoupon.getValidity() == null) {
    		return "优惠券有效天数未配置，请联系管理员";
    	}
    	if(sysCoupon.getCouponPrice() == null) {
    		return "优惠券金额未配置，请联系管理员";
    	}
    	return null;
    }

    /** 抖音 sku_id / product_id 与 sys_coupon.douyin_sku_id 匹配，空值不参与 */
    private List<SysCouponEntity> findSysCouponsByDouyinSku(String skuId, String productId) {
    	SysCouponEntity search = new SysCouponEntity();
    	search.setCouponStatus(1L);
    	List<SysCouponEntity> all = sysCouponService.selectSysCouponList(search);
    	if(all == null || all.isEmpty()) {
    		return all;
    	}
    	List<SysCouponEntity> matched = new ArrayList<SysCouponEntity>();
    	for(SysCouponEntity item : all) {
    		if(DouyinOpenService.douyinSkuMatches(item.getDouyinSkuId(), skuId, productId)) {
    			matched.add(item);
    		}
    	}
    	return matched;
    }

    /** couponStatus：1上架 2下架；已删除记录查不到 */
    private boolean isSysCouponOnShelf(SysCouponEntity sysCoupon) {
    	return sysCoupon != null && sysCoupon.getCouponStatus() != null && sysCoupon.getCouponStatus().equals(1L);
    }

    private boolean isNewUserCoupon(SysCouponEntity sysCoupon) {
    	return sysCoupon != null && sysCoupon.getCouponNew() != null && sysCoupon.getCouponNew().equals(1L);
    }

    /** 当前账号、同手机号或同 openid 是否已领过新人券 */
    private boolean hasClaimedNewUserCoupon(Long userId) {
    	return userId != null && couponMapper.countNewUserCouponByUserId(userId) > 0;
    }

    /** couponType 多选：0平台发放 1抖音 2美团，英文逗号分隔 */
    private List<Integer> parseCouponTypeList(String couponType) {
    	if(StringUtils.isBlank(couponType)) {
    		return null;
    	}
    	List<Integer> list = new ArrayList<Integer>();
    	for(String part : couponType.split(",")) {
    		if(StringUtils.isBlank(part)) {
    			continue;
    		}
    		list.add(Integer.valueOf(part.trim()));
    	}
    	return list.isEmpty() ? null : list;
    }

    /** 验券返回的 sku_id / product_id 写入 coupon.goodsId */
    private Long parseVerifyGoodsId(String skuId, String productId) {
    	if(StringUtils.isNotBlank(skuId)) {
    		return parseLongId(skuId.trim());
    	}
    	if(StringUtils.isNotBlank(productId)) {
    		return parseLongId(productId.trim());
    	}
    	return null;
    }

    private Long parseLongId(String idStr) {
    	try {
    		return Long.valueOf(idStr);
    	} catch(NumberFormatException e) {
    		return null;
    	}
    }

    private SysCouponEntity pickSysCouponForUser(List<SysCouponEntity> list, Long userId) {
    	for(SysCouponEntity item : list) {
    		if(!isSysCouponOnShelf(item)) {
    			continue;
    		}
    		if(isNewUserCoupon(item) && !canReceiveNewUserCoupon(userId)) {
    			continue;
    		}
    		return item;
    	}
    	return null;
    }

    private void throwIfSysCouponNotPicked(List<SysCouponEntity> list, Long userId, SysCouponEntity sysCoupon) throws Exception {
    	if(sysCoupon != null) {
    		return;
    	}
    	if(hasNewUserCouponInList(list) && hasClaimedNewUserCoupon(userId)) {
    		throw new Exception(NEW_USER_COUPON_DUPLICATE_MSG);
    	}
    	throw new Exception("优惠券您已经领取过了");
    }

    private boolean hasNewUserCouponInList(List<SysCouponEntity> list) {
    	if(list == null || list.isEmpty()) {
    		return false;
    	}
    	for(SysCouponEntity item : list) {
    		if(isNewUserCoupon(item) && isSysCouponOnShelf(item)) {
    			return true;
    		}
    	}
    	return false;
    }

    private boolean canReceiveNewUserCoupon(Long userId) {
    	return userId != null
    			&& !sysIncomePayDetailService.hasValidCardPurchase(userId)
    			&& !hasClaimedNewUserCoupon(userId);
    }
}
