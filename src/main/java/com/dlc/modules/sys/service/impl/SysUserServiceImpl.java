package com.dlc.modules.sys.service.impl;


import com.dlc.common.annotation.DataFilter;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.modules.api.dao.IncomePayDetailMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.UserTokenLoginLogsMapper;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.FaceService;
import com.dlc.modules.api.service.WxPayService;
import com.dlc.modules.qd.UPushUntils.UMengPush;
import com.dlc.modules.sys.dao.*;
import com.dlc.modules.sys.entity.*;
import com.dlc.modules.sys.service.SysCouponService;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import com.dlc.modules.sys.service.SysUserRoleService;
import com.dlc.modules.sys.service.SysUserService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * 系统用户
 * 
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年9月18日 上午9:46:09
 */
@Service("sysUserService")
public class SysUserServiceImpl implements SysUserService {
	@Autowired
	private SysUserDao sysUserDao;
	@Autowired
	private SysUserRoleService sysUserRoleService;
    @Autowired
    private FaceService faceService;
	@Autowired
	private SysDeviceDao sysDeviceDao;
    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;
    @Autowired
    private MessageTemplateMapper messageTemplateMapper;
    @Autowired
    private UserUpdateRecordMapper userUpdateRecordMapper;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private SysStoreAddressDao sysStoreAddressDao;
    @Autowired
    private SysStoreDao sysStoreDao;
    @Autowired
    private SysUserRoleDao sysUserRoleDao;
    @Autowired
    private SysCouponService couponService;
    @Autowired
    private SysIncomePayDetailService sysIncomePayDetailService;

    private static final String NEW_USER_COUPON_FAIL_REASON = "该客户非新人，发放失败";

    @Autowired
    private UserTokenLoginLogsMapper userTokenLoginLogsMapper;

	@Override
	public List<String> queryAllPerms(Long userId) {
		return sysUserDao.queryAllPerms(userId);
	}

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return sysUserDao.queryAllMenuId(userId);
	}

	@Override
	public SysUserEntity queryByUserName(String username) {
		return sysUserDao.queryByUserName(username);
	}
	
	@Override
	public SysUserEntity queryObject(Long userId) {
		return sysUserDao.queryObject(userId);
	}
	/*@DataFilter(tableAlias = "u", user = false)*/
	@Override
    @DataFilter(tableAlias = "u", user = false)
	public List<SysUserEntity> queryList(Map<String, Object> map){
		List<SysUserEntity> list = sysUserDao.queryList(map);
		if(list != null && !list.isEmpty()) {
			for(SysUserEntity item: list) {
				Map<String, Object> rmap = new HashMap<>();
				rmap.put("user_id", item.getUserId());
				List<SysUserRoleEntity> roleList = sysUserRoleDao.queryList(rmap);
				item.setRoleList(roleList);
				
				List<SysStoreEntity> storeList = sysStoreDao.queryObject(item.getStoreIds());
				item.setStoreList(storeList);
			}
		}
		return list;
	}
	
	@Override
    @DataFilter(tableAlias = "u", user = false)
	public int queryTotal(Map<String, Object> map) {
		return sysUserDao.queryTotal(map);
	}

	@Override
	@Transactional
	public void save(SysUserEntity user) {
		user.setCreateTime(new Date());
		Map<String, Object> map = new HashMap<>();
		if(StringUtils.isBlank(user.getStoreIds())) {
			user.setStoreAddrIds("");
		}else {
			map.put("storeIds", user.getStoreIds());
			List<SysStoreAddressEntity> addrList = sysStoreAddressDao.queryList(map);
			if(addrList == null || addrList.isEmpty()) {
				user.setStoreAddrIds("");
			}else {
				StringBuilder sb = new StringBuilder();		        
		        for (int i = 0; i < addrList.size(); i++) {
		            sb.append(addrList.get(i).getStoreAddrId());
		            if (i < addrList.size() - 1) {
		                sb.append(",");
		            }
		        }
		        user.setStoreAddrIds(sb.toString());
			}
		}
		
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		sysUserDao.save(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional
	public void update(SysUserEntity user) {
		Map<String, Object> map = new HashMap<>();
		if(StringUtils.isBlank(user.getStoreIds())) {
			user.setStoreAddrIds("");
		}else {
			map.put("storeIds", user.getStoreIds());
			List<SysStoreAddressEntity> addrList = sysStoreAddressDao.queryList(map);
			if(addrList == null || addrList.isEmpty()) {
				user.setStoreAddrIds("");
			}else {
				StringBuilder sb = new StringBuilder();		        
		        for (int i = 0; i < addrList.size(); i++) {
		            sb.append(addrList.get(i).getStoreAddrId());
		            if (i < addrList.size() - 1) {
		                sb.append(",");
		            }
		        }
		        user.setStoreAddrIds(sb.toString());
			}
		}
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(null);
		}else{
			String salt = RandomStringUtils.randomAlphanumeric(20);
			user.setSalt(salt);
			user.setPassword(ShiroUtils.sha256(user.getPassword(), user.getSalt()));
		}
		sysUserDao.update(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional
	public void deleteBatch(Long[] userId) {
		sysUserDao.deleteBatch(userId);
	}

	@Override
	public int updatePassword(Long userId, String password, String newPassword) {
		Map<String, Object> map = new HashMap<>();
		map.put("userId", userId);
		map.put("password", password);
		map.put("newPassword", newPassword);
		return sysUserDao.updatePassword(map);
	}

	@Override
	public PageUtils queryAppList(Map<String, Object> params) {
		Query query = new Query(params);
        List<Map<String, Object>> userList = sysUserDao.queryAppList(query);
        int total = sysUserDao.queryAppTotal(query);
        PageUtils pageUtil = new PageUtils(userList, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public PageUtils queryDelList(Map<String, Object> params) {
		Query query = new Query(params);
        List<Map<String, Object>> userList = sysUserDao.queryDelList(query);
        int total = sysUserDao.queryDelTotal(query);
        PageUtils pageUtil = new PageUtils(userList, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public PageUtils queryUserCouponList(Map<String, Object> params) {
		Query query = new Query(params);

		List<Map<String, Object>> list = sysUserDao.queryUserCouponList(query);
		int total = sysUserDao.queryUserCouponTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public List<Map<String, Object>> queryUserCouponInfo(Long userId) {
		return sysUserDao.queryUserCouponInfo(userId);
	}

	@Override
	public R updateUserCoupon(Long[] userIds, Double couponMoney, Integer validDay, BigDecimal limitPrice, String couponTitle, String storeAddrIds) {
        if(userIds == null || couponMoney == null || validDay == null){
            return R.error("参数缺失");
        }
        Map<String, Object> updateMap = new HashMap<String, Object>();
        Date vDate = DateUtils.addDate(new Date(), validDay);
        updateMap.put("couponMoney", couponMoney);
        updateMap.put("validDay", vDate);
        updateMap.put("limitPrice", limitPrice);
        updateMap.put("couponTitle", couponTitle);
        updateMap.put("storeAddrIds", storeAddrIds);
        if(ShiroUtils.getUserEntity().getType().equals(1) ){
            Long storeId = ShiroUtils.getUserEntity().getStoreId();
            SysStoreEntity store = sysStoreDao.queryObject(storeId);
            updateMap.put("operateStoreId", storeId);
            updateMap.put("operateStore", store.getStoreName() );
        }else{
            updateMap.put("operateStoreId", 0);
            updateMap.put("operateStore", null);
        }
        updateMap.put("sysUserId", ShiroUtils.getUserId());
        for(Long userId : userIds){
            updateMap.put("userId", userId);
            sysUserDao.insertCouponInfo(updateMap);
        }
        try {
            //推送友盟消息
            List<String> deviceTokenList = sysUserDao.queryDeviceTokens(userIds);
            if (!CollectionUtils.isEmpty(deviceTokenList)){
                //消息模板
                String msgModel = "恭喜您获得S-live运动"+couponMoney+"元优惠券，"+
                        DateUtils.format(vDate, "yyyy-MM-dd")+" 前使用有效 ~别错过使用时间哦！";
                for(String deviceToken : deviceTokenList){

                    //走安卓推送
                    if (deviceToken.length() == 44) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.ANDROID_APPKEY, ConfigConstant.ANDROID_APP_MASTER_SECRET);
                        uMengPush.sendAndroidUnicast("我是ticker", "S-live运动-消息", msgModel, deviceToken);

                    }
                    //走IOS推送
                    if (deviceToken.length() == 64) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.IOS_APPKEY, ConfigConstant.IOS_APP_MASTER_SECRET);
                        uMengPush.sendIOSUnicast(deviceToken, msgModel);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }

        return R.ok();
	}
	
	@Override
	public R updateUserCoupon(Long[] userIds, Long sysCouponId) {
        if(userIds == null || sysCouponId == null || sysCouponId == 0){
            return R.error("参数缺失");
        }
        SysCouponEntity sysCoupon = couponService.selectSysCouponById(sysCouponId);
        if(sysCoupon == null) {
        	return R.error("参数错误");
        }
        
        Map<String, Object> updateMap = new HashMap<String, Object>();
        Date vDate = DateUtils.addDate(new Date(), sysCoupon.getValidity().intValue());
        updateMap.put("couponMoney", sysCoupon.getCouponPrice());
        updateMap.put("validDay", vDate);
        updateMap.put("limitPrice", sysCoupon.getLimitPrice());
        updateMap.put("couponTitle", sysCoupon.getCouponTitle());
        updateMap.put("storeAddrIds", sysCoupon.getStoreAddrIds());
        updateMap.put("sysCouponId", sysCouponId);
        updateMap.put("fitCardIds", sysCoupon.getFitCardIds());
        updateMap.put("couponNew", sysCoupon.getCouponNew());
        if(ShiroUtils.getUserEntity().getType().equals(1) ){
            Long storeId = ShiroUtils.getUserEntity().getStoreId();
            SysStoreEntity store = sysStoreDao.queryObject(storeId);
            updateMap.put("operateStoreId", storeId);
            updateMap.put("operateStore", store.getStoreName() );
        }else{
            updateMap.put("operateStoreId", 0);
            updateMap.put("operateStore", null);
        }
        updateMap.put("sysUserId", ShiroUtils.getUserId());

        boolean isNewUserCoupon = sysCoupon.getCouponNew() != null && sysCoupon.getCouponNew().equals(1L);
        List<Long> successUserIds = new ArrayList<Long>();
        List<Map<String, Object>> failDetails = new ArrayList<Map<String, Object>>();

        for(Long userId : userIds){
            if(isNewUserCoupon && sysIncomePayDetailService.hasValidCardPurchase(userId)){
                failDetails.add(buildCouponSendFailDetail(userId, NEW_USER_COUPON_FAIL_REASON));
                continue;
            }
            updateMap.put("userId", userId);
            sysUserDao.insertCouponInfo(updateMap);
            successUserIds.add(userId);
        }

        if(successUserIds.isEmpty()){
            return buildCouponBatchSendResult(0, failDetails);
        }

        SysCouponEntity up = new SysCouponEntity();
        up.setSysCouponId(sysCoupon.getSysCouponId());
        long sendCount = sysCoupon.getSendCount() == null ? 0L : sysCoupon.getSendCount().longValue();
        up.setSendCount(sendCount + successUserIds.size());
        couponService.updateSysCoupon(up);
        try {
            List<String> deviceTokenList = sysUserDao.queryDeviceTokens(successUserIds.toArray(new Long[0]));
            if (!CollectionUtils.isEmpty(deviceTokenList)){
                String msgModel = "恭喜您获得S-live运动"+sysCoupon.getCouponPrice()+"元优惠券，"+
                        DateUtils.format(vDate, "yyyy-MM-dd")+" 前使用有效 ~别错过使用时间哦！";
                for(String deviceToken : deviceTokenList){
                    if (deviceToken.length() == 44) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.ANDROID_APPKEY, ConfigConstant.ANDROID_APP_MASTER_SECRET);
                        uMengPush.sendAndroidUnicast("我是ticker", "S-live运动-消息", msgModel, deviceToken);
                    }
                    if (deviceToken.length() == 64) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.IOS_APPKEY, ConfigConstant.IOS_APP_MASTER_SECRET);
                        uMengPush.sendIOSUnicast(deviceToken, msgModel);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }

        return buildCouponBatchSendResult(successUserIds.size(), failDetails);
	}

    private Map<String, Object> buildCouponSendFailDetail(Long userId, String reason) {
        Map<String, Object> detail = new HashMap<String, Object>();
        detail.put("userId", userId);
        detail.put("reason", reason);
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        detail.put("phone", userInfo != null && userInfo.getPhone() != null ? userInfo.getPhone() : "");
        return detail;
    }

    private R buildCouponBatchSendResult(int successCount, List<Map<String, Object>> failDetails) {
        int failCount = failDetails == null ? 0 : failDetails.size();
        if(successCount == 0){
            return R.error(formatCouponBatchSendMsg(0, failDetails)).put("successCount", 0).put("failCount", failCount).put("failDetails", failDetails);
        }
        return R.ok(formatCouponBatchSendMsg(successCount, failDetails))
                .put("successCount", successCount)
                .put("failCount", failCount)
                .put("failDetails", failDetails);
    }

    private String formatCouponBatchSendMsg(int successCount, List<Map<String, Object>> failDetails) {
        int failCount = failDetails == null ? 0 : failDetails.size();
        if(failCount == 0){
            return "发放成功，共" + successCount + "人";
        }
        StringBuilder msg = new StringBuilder();
        msg.append("成功").append(successCount).append("个，失败").append(failCount).append("个");
        msg.append("（").append(formatCouponFailReasons(failDetails)).append("）");
        return msg.toString();
    }

    private String formatCouponFailReasons(List<Map<String, Object>> failDetails) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < failDetails.size(); i++){
            Map<String, Object> item = failDetails.get(i);
            if(i > 0){
                sb.append("；");
            }
            String phone = item.get("phone") != null ? item.get("phone").toString() : "";
            if(StringUtils.isNotBlank(phone)){
                sb.append(phone);
            }else{
                sb.append(item.get("userId"));
            }
            sb.append("：").append(item.get("reason"));
        }
        return sb.toString();
    }

	@Override
	public PageUtils queryStoreMemberList(Map<String, Object> params) {
		Query query = new Query(params);
		List<Map<String, Object>> userList = sysUserDao.queryStoreMemberList(query);
		int total = sysUserDao.queryStoreMemberTotal(query);
		PageUtils pageUtil = new PageUtils(userList, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

    @Override
    public List<SysStoreMemberExcel> queryExportStoreMemberList(Map<String, Object> params) {
        return sysUserDao.queryExportStoreMemberList(params);
    }

    @Override
    public R delAutoPayByDeviceId(Long deviceId) {
        SysDeviceEntity sysDeviceEntity = sysDeviceDao.queryObject(deviceId);
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(sysDeviceEntity.getProxyId());
        if (StringUtils.isBlank(userInfo.getContractId()) || userInfo.getWtState() != 1) {
            return R.error("解除失败，请用户核对微信签约状态");
        }
        Map<String, String> result = wxPayService.deleteContract(userInfo.getContractId());
        if (result != null) {
            if(result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
                //解除成功--更新会员自动扣费状态
                SysDeviceEntity up = new SysDeviceEntity();
                up.setDeviceId(deviceId);
                up.setAutoPay(2);
                sysDeviceDao.updateAutoPayStatus(up);
                return R.error("解除成功");

            }else {
                return R.error(result.get("err_code_des"));
            }
        }
        return R.error("解除失败");
    }

    @Override
	public boolean queryUserByPhone(String phone) {
		int res = sysUserDao.queryUserByPhone(phone);
		if(res > 0){return true;}
		return false;
	}

	@Override
	public int updateFaceStatusR(Long deviceId) {
	    Long userId = sysUserDao.getProxyIdByDeviceId(deviceId);
		//先调人脸库删除人脸根据userId
        faceService.deleteUserFace(userId);
        //删除成功更新用户人脸状态为2
        int res = sysUserDao.updateUFaceSta(userId);
        return res;
	}

	@Override
	public PageUtils queryCouponList(Map<String, Object> params) {
		Query query = new Query(params);

		List<UserCouponEntity> list = sysUserDao.queryCouponList(query);
		int total = sysUserDao.queryCouponTotal(query);
		PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public int updateMemberStatus(Long[] deviceIds, int i) {
		return sysDeviceDao.batchUpdateMemberStatus(deviceIds, i);
	}

	@Override
	public PageUtils getTranMemberList(Map<String, Object> params) {
		Query query = new Query(params);
		List<Map<String, Object>> memberList = sysUserDao.selectTranMemberList(query);
		int total = sysUserDao.selectTranMemberListTotal(query);
		PageUtils pageUtil = new PageUtils(memberList, total, query.getLimit(), query.getPage());
		return pageUtil;
	}

	@Override
	public int transferCard(Long taUserId, Long trUserId, Long storeId) {
        int res = 0;
        try {
            //查看taUserId接转人是否有卡
            int flag = sysUserDao.isAnyCardByUserId(taUserId);
            //查门店地址id
            Long storeAddressId = sysUserDao.getStoreAddressIdBySId(storeId);
            //转卡人卡信息
            SysDeviceEntity sysDeviceEntity = sysDeviceDao.queryDeviceById(trUserId, storeAddressId);
            //接转人信息
            Map<String, Object> taUserInfoMap = sysUserDao.getTaUserInfoByUid(taUserId);
            Date date = new Date();
            if(flag == 0){   //说明是无卡用户
                //查询转接人有没有在该门店购卡/转卡的记录
                SysDeviceEntity taHistoryDevice = sysDeviceDao.queryDeviceHistoryById(taUserId, storeAddressId);
                if(taHistoryDevice == null){  //说明在该门店没有购卡转卡记录
                    //接转人新增一条数据device
                    SysDeviceEntity device = new SysDeviceEntity();
                    device.setDeviceName("手环");
                    device.setStoreId(storeId);
                    device.setStoreAddressId(sysDeviceEntity.getStoreAddressId());
                    device.setProxyId(taUserId);      //转接人
                    device.setDevicePrice(sysDeviceEntity.getDevicePrice());    //原价
                    device.setInventory(1);    //数量默认1个
                    device.setStatus(0);  //待确认状态 可以领取手环，确认后需要补录手环编号
                    device.setType(sysDeviceEntity.getType()); //卡类型
                    device.setFitCardId(sysDeviceEntity.getFitCardId()); //卡ID
                    device.setValidityDate(sysDeviceEntity.getValidityDate());          //有效期
                    device.setProxyName((String) taUserInfoMap.get("nickname"));  //taUserInfoMap接转人信息
                    device.setPhone((String) taUserInfoMap.get("phone"));
                    device.setOnLineTime(date);    // 在线开始时间（转卡状态下为截止时间）
                    device.setCreateTime(date);
                    res = sysDeviceDao.insertDeviceSelective(device);
                }else{  //有转卡记录更新原来的记录
                    SysDeviceEntity taDevice = new SysDeviceEntity();
                    taDevice.setDeviceId(taHistoryDevice.getDeviceId());
                    taDevice.setStatus(0);  //待确认状态 可以领取手环，确认后需要补录手环编号
                    taDevice.setValidityDate(sysDeviceEntity.getValidityDate());   //更新有效期为转卡人有效期
                    taDevice.setOnLineTime(date);
                    taDevice.setCreateTime(date);
                    res =sysDeviceDao.updateDeviceSelective(taDevice);
                }

                if (res > 0){
                    //更新接转人信息，手环状态为待领取，人脸状态为待认证
                    sysDeviceDao.updateUserStatusByUid(taUserId);
                    //更新转卡人信息
                    SysDeviceEntity upDevice = new SysDeviceEntity();
                    upDevice.setProxyId(trUserId);
                    upDevice.setStoreAddressId(storeAddressId);
                    upDevice.setOnLineTime(date);
                    sysDeviceDao.updateDeviceStatusById(upDevice);
                    //更新转卡人用户表信息
                    sysUserDao.updateUserWristByUid(trUserId);
                }
            }else if(flag == 1){   //有卡用户  (只转天数不转类型)
                //卡有效期
                SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
                //转接人信息
                SysDeviceEntity sysDeviceEntityTa = sysDeviceDao.queryDeviceMemberById(taUserId, storeAddressId);
                //将有效期时间加在接转会员上
                String endTime = df.format(date);
                if(sysDeviceEntityTa != null){
                    //接转人过期时间
                    Date eDate = sysDeviceEntityTa.getValidityDate();
                    String cpTime = df.format(eDate);
                    Date bt = df.parse(cpTime);
                    Date et = df.parse(df.format(date));
                    if(et.before(bt)){    //当前时间小于过期时间按过期时间算， 否则按当前时间算起
                        endTime = cpTime;
                    }
                }
                //计算离过期还有多长时间,和延长时间
                long days = DateUtils.getDayDiff(df.format(date), df.format(sysDeviceEntity.getValidityDate()));
                String validityDate = df.format(df.parse(endTime).getTime() + days * 24l * 60l * 60l * 1000l);
                //更新转接人卡信息
                SysDeviceEntity upDeviceTa = new SysDeviceEntity();
                upDeviceTa.setProxyId(taUserId);
                upDeviceTa.setStoreAddressId(storeAddressId);
                upDeviceTa.setValidityDate(df.parse(validityDate));
                upDeviceTa.setOnLineTime(date);
                res = sysDeviceDao.updateDeviceValidate(upDeviceTa);
                //更新转卡人信息
                if(res > 0){
                    //更新转卡人信息
                    SysDeviceEntity upTrDevice = new SysDeviceEntity();
                    upTrDevice.setProxyId(trUserId);
                    upTrDevice.setStoreAddressId(storeAddressId);
                    upTrDevice.setOnLineTime(date);
                    sysDeviceDao.updateDeviceStatusById(upTrDevice);
                    //更新转卡人用户表信息
                    sysUserDao.updateUserWristByUid(trUserId);
                }
            }
            //插入一条交易记录  12 转卡
            if(res > 0){
                IncomePayDetail incomePayDetail = new IncomePayDetail();
                incomePayDetail.setUserId(trUserId);
                incomePayDetail.setStoreId(storeId);
                incomePayDetail.setAnotherId(taUserId);
                incomePayDetail.setOrderNo(OrderNoGenerator.getOrderIdByTime());
                incomePayDetail.setMoney(new BigDecimal("0.00"));
                //支付用途（12 转卡）,
                incomePayDetail.setPayType(12);
                incomePayDetail.setTradeDate(date);
                incomePayDetail.setTradeStatus(3);
                incomePayDetailMapper.insertSelective(incomePayDetail);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return res;
	}

    @Override
    public SysDeviceEntity queryStoreMemberById(Long userId, Long storeId) {
        //查门店地址id
        Long storeAddressId = sysUserDao.getStoreAddressIdBySId(storeId);
        return sysDeviceDao.queryDeviceById(userId, storeAddressId);
    }

    @Override
    public PageUtils getMessageTmpList(Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> msgList = messageTemplateMapper.selectMsgTempList(query);
        int total = messageTemplateMapper.selectMsgTempListTotal(query);
        PageUtils pageUtil = new PageUtils(msgList, total, query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public int saveMsgTemp(MessageTemplate messageTemplate) {
        return messageTemplateMapper.insertSelective(messageTemplate);
    }

    @Override
    public int updateMsgTemp(MessageTemplate messageTemplate) {
        return messageTemplateMapper.updateByPrimaryKeySelective(messageTemplate);
    }

    @Override
    public int deleteMsgTemp(Long mtId) {
        return messageTemplateMapper.deleteByPrimaryKey(mtId);
    }

    @Override
    public int pushMsgToUser(Long[] deviceIds, Long mtId) {
        //推送购买团体课消息(所有会员)
        try {
            //查询设备deviceToken
            List<String> dtList = sysUserDao.queryDeviceTokens(deviceIds);
            pushMessages(dtList, mtId); //推送
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int queryOtherCard(Long taUserId, Long storeId) {
        return sysDeviceDao.selectOtherCard(taUserId, storeId);
    }

    @Override
    public int pushMsgToUserByUId(Long[] userIds, Long mtId) {
        //推送购买团体课消息(所有会员)
        try {
            //查询设备deviceToken
            List<String> dtList = sysUserDao.queryDeviceTokensByUid(userIds);
            pushMessages(dtList, mtId); //推送
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int updateMemberValiditys(NewMember newMember) {
        Map<String, Object> vMap = new HashMap<>();
        vMap.put("deviceId", newMember.getDeviceId());
        vMap.put("validityDate", newMember.getNewValidityDate());
        int res = sysDeviceDao.updateMemberValidity(vMap);
        if( res > 0 ){insertUpdateRecord(newMember);}
        return res;
    }

    @Override
    public SysDeviceEntity canUpdateValidity(Long deviceId) {
        return sysDeviceDao.queryObject(deviceId);
    }

    @Override
    public int updateMemberStatusStart(Long[] deviceIds) {
        return sysDeviceDao.batchUpdateMemberStatusStart(deviceIds);
    }

    @Override
    public int updateOverMemberValiditys(NewMember newMember, String deviceNo) {
	    //先更新用户表手环编号wristId
        if(StringUtils.isNotBlank(deviceNo)){
            Map<String, Object> objectMap = new HashMap<String, Object>();
            objectMap.put("userId", newMember.getUserId());
            objectMap.put("deviceNo", deviceNo.trim());
            sysDeviceDao.updateUserInfoWrist(objectMap);
        }
        Map<String, Object> vMap = new HashMap<>();
        vMap.put("deviceId", newMember.getDeviceId());
        vMap.put("validityDate", newMember.getNewValidityDate());
        int res = sysDeviceDao.updateValidityAndSta(vMap);
        if( res > 0 ){insertUpdateRecord(newMember);}
        return res;
    }

    @Override
    public PageUtils getValidityRecordList(Map<String, Object> params) {
        Query query = new Query(params);
        List<Map<String, Object>> list = userUpdateRecordMapper.selectValidityRecordList(query);
        int total = userUpdateRecordMapper.selectValidityRecordTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return pageUtil;
    }

    /**保存更新有效期记录*/
    private void insertUpdateRecord(NewMember newMember){
        try {
            UserUpdateRecord userUpdateRecord = new UserUpdateRecord();
            userUpdateRecord.setUserId(newMember.getUserId());
            userUpdateRecord.setStoreId(newMember.getStoreId());
            userUpdateRecord.setOldValidityDate(newMember.getValidityDate());
            userUpdateRecord.setNewValidityDate(newMember.getNewValidityDate());
            userUpdateRecord.setSysUserId(ShiroUtils.getUserId());
            userUpdateRecordMapper.insertSelective(userUpdateRecord);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
    }

    /**推送实现*/
    private void pushMessages(List<String> dtList, Long mtId){

        try {
            if (!CollectionUtils.isEmpty(dtList)){
                //查询模板信息
                MessageTemplate messageTemplate = messageTemplateMapper.selectByPrimaryKey(mtId);
                //消息模板
                String msgModel = messageTemplate.getMessage();
                //标题
                String title = messageTemplate.getTitle();
                for(String deviceToken : dtList){
                    //走安卓推送
                    if (deviceToken.length() == 44) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.ANDROID_APPKEY, ConfigConstant.ANDROID_APP_MASTER_SECRET);
                        uMengPush.sendAndroidUnicast("我是ticker", title, msgModel, deviceToken);

                    }
                    //走IOS推送
                    if (deviceToken.length() == 64) {
                        UMengPush uMengPush = new UMengPush(ConfigConstant.IOS_APPKEY, ConfigConstant.IOS_APP_MASTER_SECRET);
                        uMengPush.sendIOSUnicast(deviceToken, msgModel);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int updateUserInfo(Map data) {
        return sysUserDao.updateUserInfo(data);
    }

    @Override
    public List getUserIdsByDeviceId(Long[] deviceIds) {
        return sysDeviceDao.getUserIdsByDeviceId(deviceIds);
    }

    @Override
    public int batchDeleteUsers(List userIds, long sysUserId, Date delTime) {
        return sysUserDao.batchDeleteUsers(userIds, sysUserId, delTime);
    }

    @Override
    public List queryRiskList(Query query){
        return userTokenLoginLogsMapper.queryRiskList(query);
    }

    @Override
    public int queryRiskTotal(Query query){
        return userTokenLoginLogsMapper.queryRiskTotal(query);
    }

    @Override
    public int checkRisk(int id, int check){
        return userTokenLoginLogsMapper.checkRisk(id, check);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R clearAppUserData(Long userId, String password, List<String> clearTypes) {
        if (userId == null || userId <= 0) {
            return R.error("用户参数错误");
        }
        if (StringUtils.isBlank(password)) {
            return R.error("请输入登录密码");
        }
        if (clearTypes == null || clearTypes.isEmpty()) {
            return R.error("请选择要清除的数据项");
        }
        SysUserEntity loginUser = sysUserDao.queryObject(ShiroUtils.getUserId());
        if (loginUser == null) {
            return R.error("登录状态异常");
        }
        String encrypted = ShiroUtils.sha256(password, loginUser.getSalt());
        if (!encrypted.equals(loginUser.getPassword())) {
            return R.error("登录密码错误");
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            return R.error("用户不存在");
        }
        Map<String, Integer> cleared = new LinkedHashMap<String, Integer>();
        Set<String> types = new HashSet<String>(clearTypes);
        if (types.contains("coupon")) {
            cleared.put("coupon", sysUserDao.deleteCouponByUserId(userId));
        }
        if (types.contains("cardOrder")) {
            cleared.put("cardOrder", sysUserDao.deleteCardOrderByUserId(userId));
        }
        if (types.contains("incomePayDetail")) {
            cleared.put("incomePayDetail", sysUserDao.deleteIncomePayDetailByUserId(userId));
        }
        if (types.contains("openDoor")) {
            cleared.put("openDoor", sysUserDao.deleteOpenDoorRecordByUserId(userId));
        }
        if (types.contains("storeChange")) {
            cleared.put("storeChange", sysUserDao.deleteDeviceChangeByProxyId(userId));
        }
        if (types.contains("validityChange")) {
            cleared.put("validityChange", sysUserDao.deleteUserUpdateRecordByUserId(userId));
        }
        if (types.contains("faceRecord")) {
            cleared.put("faceRecord", sysUserDao.deleteFaceIdentyRecordByUserId(userId));
        }
        if (types.contains("loginRisk")) {
            cleared.put("loginRisk", sysUserDao.deleteUserTokenLoginLogsByUserId(userId));
        }
        if (types.contains("sportRecord")) {
            cleared.put("sportRecord", sysUserDao.deleteUserSportDeviceRecordByUserId(userId));
        }
        if (types.contains("memberCard")) {
            try {
                faceService.deleteUserFace(userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cleared.put("memberCard", sysUserDao.deleteDeviceByProxyId(userId));
            sysUserDao.resetUserMemberFlags(userId);
        }
        if (types.contains("delUserRecord")) {
            cleared.put("delUserRecord", sysUserDao.deleteDelUserRecordByUserId(userId));
        }
        return R.ok("清除完成").put("cleared", cleared);
    }

}
