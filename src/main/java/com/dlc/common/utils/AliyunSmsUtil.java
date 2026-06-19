package com.dlc.common.utils;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.dlc.modules.api.dao.MsgLogMapper;
import com.dlc.modules.api.entity.MsgLog;
import com.dlc.modules.qd.utils.PhoneCodeVer;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 阿里云短信
 * @Author jiangkang
 * @Date 2022/9/1 17:17
 */
@Component
public class AliyunSmsUtil {
	private Logger log =  LoggerFactory.getLogger(getClass());
//    public static final String accessKeyId = "YOUR_ACCESS_KEY_ID";
//    public static final String accessKeySecret = "YOUR_ACCESS_KEY_SECRET";
//    public static final String templateCode = "SMS_248086017";
    public static final String signName = "Slive运动";

    @Value("${aliyun.accessKeyId}")
    public String accessKeyId;

    @Value("${aliyun.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.templateCode}")
    private String templateCode;    //用户注册短信验证码

//    @Value("${aliyun.signName}")
//    private String signName;


    private static Logger logger = LoggerFactory.getLogger(PhoneCodeVer.class);

    private static RedisUtils redisUtils;

    @Autowired
    private MsgLogMapper msgLogMapper;

    @Autowired
    public void setRedisUtils(RedisUtils redisUtils) {
        this.redisUtils = redisUtils;
    }

    public static String getCode(String phone){
        String code = (int)((Math.random()*9+1)*1000) + "";
        redisUtils.set(ConfigConstant.PHONE + phone, code,600);
        return code;
    }

    public MsgLog sendSms(String phone) throws Exception{
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        com.aliyun.dysmsapi20170525.Client client = new com.aliyun.dysmsapi20170525.Client(config);
        String code = getCode(phone);
        com.aliyun.dysmsapi20170525.models.SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setPhoneNumbers(phone)
                .setTemplateParam("{\"code\":\""+ code +"\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();

        MsgLog msgLog = new MsgLog();
        msgLog.setMobile(phone);
        msgLog.setCode(code);

        try {
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
            log.info("短信发送记录：phone=" + phone + "/" + new Gson().toJson(response.getBody()) );
            if(!response.getBody().getCode().equalsIgnoreCase("OK")){
                msgLog.setStatus(0);
                msgLog.setMsg(response.getBody().getMessage());
            }else{
                msgLog.setStatus(1);
            }
            msgLog.setCreateTime(new Date());
            msgLogMapper.insert(msgLog);
            return msgLog;
        } catch (Exception _error){
            TeaException error = new TeaException(_error.getMessage(), _error);
            com.aliyun.teautil.Common.assertAsString(error.message);
            log.info("短信Exception ", error);
            
            msgLog.setStatus(0);
            msgLog.setMsg(_error.getMessage());
        }
        return msgLog;
    }

}
