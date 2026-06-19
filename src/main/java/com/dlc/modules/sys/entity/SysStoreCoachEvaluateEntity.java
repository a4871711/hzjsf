package com.dlc.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 门店教练评价表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-04-02 09:09:30
 */
public class SysStoreCoachEvaluateEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 教练评价表
     */
    private Long evaluateId;
    /**
     * 教练id
     */
    private Long scId;
    private String coachName;
    /**
     * 评论用户ID
     */
    private Long userId;

    private String  userName;

    public String getCoachName() {
        return coachName;
    }

    public void setCoachName(String coachName) {
        this.coachName = coachName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 头像
     */
    private String headImgUrl;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 评价等级(*级)
     */
    private Integer evLevel;
    /**
     * 评价内容
     */
    private String evContent;
    /**
     * 评论图
     */
    private String evaluatImgUrl;
    /**
     * 评论时间(yyyy-MM-dd)
     */
    private Date evaluatDate;

    /**
     * 设置：教练评价表
     */
    public void setEvaluateId(Long evaluateId) {
        this.evaluateId = evaluateId;
    }

    /**
     * 获取：教练评价表
     */
    public Long getEvaluateId() {
        return evaluateId;
    }

    /**
     * 设置：教练id
     */
    public void setScId(Long scId) {
        this.scId = scId;
    }

    /**
     * 获取：教练id
     */
    public Long getScId() {
        return scId;
    }

    /**
     * 设置：评论用户ID
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取：评论用户ID
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置：头像
     */
    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    /**
     * 获取：头像
     */
    public String getHeadImgUrl() {
        return headImgUrl;
    }

    /**
     * 设置：昵称
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 获取：昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 设置：评价等级(*级)
     */
    public void setEvLevel(Integer evLevel) {
        this.evLevel = evLevel;
    }

    /**
     * 获取：评价等级(*级)
     */
    public Integer getEvLevel() {
        return evLevel;
    }

    /**
     * 设置：评价内容
     */
    public void setEvContent(String evContent) {
        this.evContent = evContent;
    }

    /**
     * 获取：评价内容
     */
    public String getEvContent() {
        return evContent;
    }

    /**
     * 设置：评论图
     */
    public void setEvaluatImgUrl(String evaluatImgUrl) {
        this.evaluatImgUrl = evaluatImgUrl;
    }

    /**
     * 获取：评论图
     */
    public String getEvaluatImgUrl() {
        return evaluatImgUrl;
    }

    /**
     * 设置：评论时间(yyyy-MM-dd)
     */
    public void setEvaluatDate(Date evaluatDate) {
        this.evaluatDate = evaluatDate;
    }

    /**
     * 获取：评论时间(yyyy-MM-dd)
     */
    public Date getEvaluatDate() {
        return evaluatDate;
    }
}
