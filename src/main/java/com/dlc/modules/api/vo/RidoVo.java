package com.dlc.modules.api.vo;

import java.io.Serializable;

/**
 * @Author jiangkang
 * @Date 2022/9/14 19:49
 */
public class RidoVo implements Serializable {

    private String deviceId;
    private String deviceName;
    private int state;
    private Long userId;
    private Integer autoClose;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(Integer autoClose) {
        this.autoClose = autoClose;
    }

}
