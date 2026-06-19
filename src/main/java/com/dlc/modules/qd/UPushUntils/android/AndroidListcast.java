package com.dlc.modules.qd.UPushUntils.android;

import com.dlc.modules.qd.UPushUntils.AndroidNotification;

/**
 * 安卓列播
 * @author chenyuexin
 * @version 1.0
 * @date 2018-08-13 15:39
 */
public class AndroidListcast extends AndroidNotification {
    public AndroidListcast(String appkey,String appMasterSecret) throws Exception {
        setAppMasterSecret(appMasterSecret);
        setPredefinedKeyValue("appkey", appkey);
        this.setPredefinedKeyValue("type", "listcast");
    }

    public void setDeviceToken(String token) throws Exception {
        setPredefinedKeyValue("device_tokens", token);
    }
}
