package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.CommonUtil;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.common.utils.RedisUtils;
import com.dlc.modules.api.dao.DeviceMapper;
import com.dlc.modules.api.dao.OpenDoorRecordMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.entity.OpenDoorRecord;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.service.AboutUsService;
import com.dlc.modules.api.service.StoreDeviceV2Service;
import com.dlc.modules.api.vo.RidoVo;
import com.dlc.modules.qd.utils.MD5Util;
import com.dlc.modules.qd.utils.SendPushPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wefit")
public class WefitController extends BaseController{
    final Logger logger = LoggerFactory.getLogger(WefitController.class);

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private StoreAddressMapper storeAddressMapper;

    @Autowired
    private AboutUsService aboutUsService;

    @Autowired
    private OpenDoorRecordMapper openDoorRecordMapper;

    @Autowired
    private StoreDeviceV2Service storeDeviceV2Service;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 开启跑步机设备
     * @param id
     * @param user_id
     * @param request
     * @return
     */
    @RequestMapping("/device/open")
    public R openDevice(String id, String user_id, HttpServletRequest request){
        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(user_id)){
            return R.reError("查询参数缺失");
        }

//        user_id = getUserId(request);
        //验证用户会员卡
        Long proxyId = deviceMapper.checkUserValidity(Long.parseLong(user_id));
        if(proxyId == null){
            return R.reError("非会员卡用户，请购卡后使用");
        }
        RidoVo deviceV2 = storeDeviceV2Service.queryRidoDeviceByDeviceUserId(user_id, "301");
        System.out.println("用户开启设备检查" + deviceV2);
        if(deviceV2 != null){
            return R.reError("您有跑步机未关闭，请关闭后再开启新设备");
        }
        Date now = new Date();
        String strTime = String.valueOf(now.getTime());
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appkey", ConfigConstant.WEFIT_APPKEY);
        paramMap.put("timestamp", now.getTime() + "");
        paramMap.put("id", id);
        paramMap.put("user_id", user_id);
        paramMap.put("order_number", OrderNoGenerator.getOrderIdByTime());
        StringBuilder sb = new StringBuilder();
        String append = sb.append(ConfigConstant.WEFIT_APPKEY).append(ConfigConstant.WEFIT_PRIVATE_APPKEY).append(strTime).toString();
        paramMap.put("sign", MD5Util.MD5Encode(append, "utf-8"));
        String result = SendPushPost.sendPost(ConfigConstant.WEFIT_OPEN_DEVICE_URL, paramMap);
        JSONObject jsonObject = JSONObject.parseObject(result);
        logger.info("开启跑步机，用户提交数据" + paramMap);
        logger.info("开启跑步机设备结果：" + jsonObject);
        if(jsonObject.getInteger("status").equals(0)){
            try{
                String deviceId = storeDeviceV2Service.getDeviceIdFromDecode(id);
                storeDeviceV2Service.setRidoDeviceState(deviceId, 1, Long.parseLong(user_id));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return R.reOk(jsonObject);
    }

    /**
     * 微光互联二维码扫描器扫码回调接口/扫码验证用户是否具有开门权限
     * @return
     */
    @RequestMapping(value = "/checkUserQrcode", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String checkQrcode(HttpServletRequest request, @RequestBody String post){
        logger.info("扫码开门接口提交参数：" + post);
        String code = "code=1000";
        try{
            String str = post;
            //参数转化
            if(str.indexOf("vgdecoderesult=") == -1 || str.indexOf("devicenumber=") == -1){
                return code;
            }
            String[] paramsData = str.split("&&");
            String[] paramA = paramsData[0].split("=");
            String[] paramB = paramsData[1].split("=");
            Long scanStoreId = Long.parseLong(paramB[1]);

            Map configDoorMap = aboutUsService.queryOpenDoor();
            int distanceDoor = Integer.valueOf( configDoorMap.get("open_door").toString() );
            int validDoor = Integer.valueOf( configDoorMap.get("qrcode_valid").toString() );

            //参数的效验
//            System.out.println("扫码开门参数效验A，vgdecoderesult=" + paramA[1]);
//            System.out.println("扫码开门参数效验B，devicenumber=" + paramB[1]);
            String[] qrcodeData = paramA[1].split("-");
            String uid = qrcodeData[0].replace("hzjsf_", "");
            //条件一：二维码有效期
            Long validity = Long.parseLong(qrcodeData[1]) + validDoor * 1000; //二维码附带时间戳，有效时间五分钟
            //条件二：限制扫码用户与门店的距离不超过后台配置距离
            Double longitude = Double.parseDouble(qrcodeData[3]);
            Double latitude = Double.parseDouble(qrcodeData[2]);
            int distance = storeAddressMapper.getDistanceByUser(scanStoreId, longitude, latitude);
            logger.info("用户与门店的距离" + longitude +"/"+ latitude +"/"+ distance);            

            //用户扫码记录：store_id 记录扫码门店(devicenumber)，不用会员卡 storeId(通卡可能为空)
            OpenDoorRecord openDoorRecord = new OpenDoorRecord();
            openDoorRecord.setUserId(Long.parseLong(uid));
            openDoorRecord.setStoreId(scanStoreId);
            openDoorRecord.setLongitude(longitude.toString());
            openDoorRecord.setLatitude(latitude.toString());
            openDoorRecord.setDistance(distance);
            openDoorRecord.setResult(0);
            openDoorRecord.setCreateTime(new Date());
            
            if(distance > 50) {
            	//openDoorRecord.setRemark("会员定位距离过远");
            	//openDoorRecordMapper.insert(openDoorRecord);
            	//return code;
            }
            //条件三：检查会员卡有效期
            Long proxyId = deviceMapper.checkUserValidity(Long.parseLong(uid));
            Map deviceMap = deviceMapper.selectByProxyId(Long.parseLong(uid));
            if(deviceMap == null || deviceMap.isEmpty()) {
            	openDoorRecord.setRemark("会员卡已过期");
            	openDoorRecordMapper.insert(openDoorRecord);
            	return code;
            }
            // device_store_id 记录用户办卡门店；通卡无专属门店时用扫码门店
            Long deviceStoreId = parseLongOrZero(deviceMap.get("storeId"));
            String[] cardStoreAddrIds = CommonUtil.tryStrings(deviceMap.get("storeAddrIds") == null ? "" : String.valueOf(deviceMap.get("storeAddrIds")));
            if (cardStoreAddrIds.length == 0) {
                openDoorRecord.setDeviceStoreId(scanStoreId);
            } else {
                openDoorRecord.setDeviceStoreId(deviceStoreId);
            }
            
            Map storeAddress = storeAddressMapper.queryStoreAddressByStoreId(scanStoreId);
            if(storeAddress == null || storeAddress.isEmpty()) {
            	openDoorRecord.setRemark("门店数据异常");
            	openDoorRecordMapper.insert(openDoorRecord);
            	return code;
            }
            String[] storeAddrIds = cardStoreAddrIds;
            if(storeAddrIds.length > 0 && !CommonUtil.contains(storeAddrIds, String.valueOf(storeAddress.get("storeAddrId")))) {
            	openDoorRecord.setRemark("会员卡不适用该门店");
            	openDoorRecordMapper.insert(openDoorRecord);
            	return code;
            }

            Long now = new Date().getTime();
            if(validity < now){
                logger.info("扫码开门参数效验，已失效。validity=" + validity +"/"+ now );
                openDoorRecord.setRemark("二维码已过有效期");
                System.out.println("插入参数" + openDoorRecord );
                openDoorRecordMapper.insert(openDoorRecord);
                return code;
            }else if(distance > distanceDoor){
                openDoorRecord.setRemark("距离过远");
                openDoorRecordMapper.insert(openDoorRecord);
                return code;
            }else if(proxyId == null){
                openDoorRecord.setRemark("会员卡已过期");
                openDoorRecordMapper.insert(openDoorRecord);
                return code;
            }
            String deviceId = mapStr(deviceMap, "deviceId", "");
            String rand = redisUtils.get(ConfigConstant.DEVICE_RAND + deviceId);
            if(StringUtils.isEmpty(rand) || qrcodeData.length < 5 || !rand.equals(qrcodeData[4])) {
            	openDoorRecord.setRemark("二维码已失效");
                openDoorRecordMapper.insert(openDoorRecord);
                return code;
            }
            //判断次卡
            if("10".equals(mapStr(deviceMap, "type", "0"))) {
            	int useCount = parseIntOrZero(deviceMap.get("useCount"));
            	int usedCount = parseIntOrZero(deviceMap.get("usedCount"));
            	String lasttime = redisUtils.get(ConfigConstant.DEVICE + deviceId);
            	String lastsub = redisUtils.get(ConfigConstant.DEVICE_SUB + deviceId);
            	if(StringUtils.isEmpty(lasttime)) {
            		openDoorRecord.setRemark("二维码已失效");
                    openDoorRecordMapper.insert(openDoorRecord);
                    return code;
            	}
            	if(!lasttime.equals(lastsub)) {
	            	if(useCount <= usedCount) {
	            		openDoorRecord.setRemark("会员卡次数已用完");
	                    openDoorRecordMapper.insert(openDoorRecord);
	                    return code;
	            	}
	            	//扣次数
	            	redisUtils.set(ConfigConstant.DEVICE_SUB + deviceId, lasttime, 120 * 60);
	            	//保存次数到数据库
	            	deviceMapper.updateCountByPrimaryKey(parseLongOrZero(deviceId));
            	}
            }
            
            logger.info("开门成功，参数："+ post);

            openDoorRecord.setResult(1);
            openDoorRecordMapper.insert(openDoorRecord);
            return "code=0000";
        } catch (Exception e){
        	logger.error("扫码开门失败，参数：" + post, e);
            return code;
        }
    }

    private static Long parseLongOrZero(Object value) {
        if (value == null || "".equals(String.valueOf(value).trim())) {
            return 0L;
        }
        return Long.parseLong(String.valueOf(value));
    }

    private static int parseIntOrZero(Object value) {
        if (value == null || "".equals(String.valueOf(value).trim())) {
            return 0;
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private static String mapStr(Map<?, ?> map, String key, String defaultVal) {
        if (map == null) {
            return defaultVal;
        }
        Object value = map.get(key);
        return value == null ? defaultVal : String.valueOf(value);
    }




}
