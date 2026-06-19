package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author jiangkang
 * @Date 2022/9/6 14:57
 */
public class MsgLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String mobile;
    private String code;
    private String msg;
    private int status;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
