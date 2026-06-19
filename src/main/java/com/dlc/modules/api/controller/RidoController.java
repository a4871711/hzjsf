package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.DateUtils;
import com.dlc.common.utils.MathUtil;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.StoreDeviceV2;
import com.dlc.modules.api.entity.UserSportDeviceRecord;
import com.dlc.modules.api.service.StoreDeviceV2Service;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.api.vo.RidoVo;
import com.dlc.modules.qd.utils.QrcodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 力动设备相关
 * @Author jiangkang
 * @Date 2022/9/9 17:04
 */
@RestController
@RequestMapping("/api/device/rido")
public class RidoController{

    final Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private StoreDeviceV2Service storeDeviceV2Service;

    @Value("${rido_qrcode_path}")
    private String ridoQrcodePath;


    /**
     * 上传运动数据
     * @return
     */
    @RequestMapping(value = "/uploadSportData", method = RequestMethod.POST)
    @ResponseBody
    public R uploadSportData(@RequestBody JSONObject jsonObject){
        logger.info("上传数据{}", jsonObject);
        Long userId = jsonObject.getLong("userId");
        Date startTime = new Date(jsonObject.getLong("startTime"));

//        String phone = jsonObject.getString("phone");
//        Map userMap = userInfoService.getUserInfoByPhone(phone);
//        Long userId = Long.valueOf(userMap.getOrDefault("userId", "0").toString());

//        BigDecimal dbDistance = MathUtil.fromIntToBigDecimal( jsonObject.getInteger("distance") );
//        BigDecimal dbCalorie = MathUtil.fromIntToBigDecimal( jsonObject.getInteger("calorie") );

        UserSportDeviceRecord userSportDeviceRecord = new UserSportDeviceRecord();
        userSportDeviceRecord.setUserId( userId );
        userSportDeviceRecord.setDeviceId( jsonObject.getString("deviceMac") );
        userSportDeviceRecord.setDeviceName( jsonObject.getString("deviceName") );
        userSportDeviceRecord.setCalorie( jsonObject.getBigDecimal("calorie") );
        userSportDeviceRecord.setDistance( jsonObject.getBigDecimal("distance") );
        userSportDeviceRecord.setAvgSpeed( jsonObject.getBigDecimal("avgSpeed") );
        userSportDeviceRecord.setAvgGradient( Integer.parseInt(jsonObject.getString("avgGradient")) );
        userSportDeviceRecord.setBpm( jsonObject.getInteger("bpm") );
//        userSportDeviceRecord.setStepSize( jsonObject.getBigDecimal("stepSize") );
        userSportDeviceRecord.setStepCount( jsonObject.getInteger("stepCount") );

        userSportDeviceRecord.setStartTime(startTime);
        userSportDeviceRecord.setCreateTime(new Date());
        userSportDeviceRecord.setSportTime( jsonObject.getInteger("duration") );

        int record = userInfoService.saveUserSportRecord(userSportDeviceRecord);
        if (record > 0) {
            logger.info(userId + "运动信息保存成功");
        }

        R r = new R();
        r.put("code", 200);
        r.put("msg", "成功");
        return r;
    }

    /**
     * 获取设备信息
     * @param device_mac
     * @return
     */
    @RequestMapping(value = "/heartbeat", method = RequestMethod.GET)
    public R heartbeat(String device_mac){
        R r = new R();
        RidoVo device = storeDeviceV2Service.queryRidoDeviceByDeviceId(device_mac);
        if(device == null){
            r.put("code", 10001);
            r.put("msg", "设备不存在");
            return r;
        }
        r.put("code", 200);
        r.put("msg", "成功");
        r.put("data", device);
        return r;
    }

    /**
     * 获取设备二维码
     * @param device_mac
     * @return
     */
    @RequestMapping(value = "/qrcode", method = RequestMethod.GET)
    public R qrcode(HttpServletRequest request, String device_mac){
        R r = new R();
        RidoVo device = storeDeviceV2Service.queryRidoDeviceByDeviceId(device_mac);
        if(device == null){
            r.put("code", 10001);
            r.put("msg", "设备不存在");
        }else{
            String filename = "";
            try {
                String qrContent = "deviceId=" + device_mac;
                filename = QrcodeUtil.generateQRCodeImage(qrContent, 350, 350, ridoQrcodePath);
            }catch (Exception e){
                e.printStackTrace();
            }
            String qrcodeUrl = "http://" + request.getServerName() + request.getContextPath() + "/statics/images/rido/qrcode/" + filename;
            System.out.println("二维码路径：" + qrcodeUrl);
            HashMap data = new HashMap();
            data.put("qrcode", qrcodeUrl);

            r.put("code", 200);
            r.put("msg", "成功");
            r.put("data", data);
        }

        return r;
    }

    /**
     * 开启跑步机
     * @param device_mac
     * @param user_id
     * @return
     */
    @RequestMapping(value = "/setState", method = RequestMethod.POST)
    public R open(String device_mac, int state, Long user_id){
        R r = new R();
        RidoVo device = storeDeviceV2Service.queryRidoDeviceByDeviceId(device_mac);
        if(device == null){
            r.put("code", 10001);
            r.put("msg", "设备不存在");
            return r;
        }
        if(state == 0){
            user_id = 0L;
        }
//        Long proxyId = userInfoService.getDeviceProxyId(user_id);
//        if(proxyId == null){
//            r.put("code", 40001);
//            r.put("msg", "非会员卡用户，请购卡后使用");
//            return r;
//        }

        int result = storeDeviceV2Service.setRidoDeviceState(device_mac, state, user_id);
        if(result > 0){
            r.put("code", 200);
            r.put("msg", "设备状态更新成功");
        }else{
            r.put("code", 10100);
            r.put("msg", "设备开启失败");
        }

        return r;
    }

}
