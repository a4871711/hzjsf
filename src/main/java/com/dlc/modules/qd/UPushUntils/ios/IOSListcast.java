package com.dlc.modules.qd.UPushUntils.ios;

import com.dlc.modules.qd.UPushUntils.IOSNotification;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-08-13 15:40
 */
public class IOSListcast extends IOSNotification {
    public IOSListcast(String appkey,String appMasterSecret) throws Exception{
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "listcast");
    }

    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }
}
