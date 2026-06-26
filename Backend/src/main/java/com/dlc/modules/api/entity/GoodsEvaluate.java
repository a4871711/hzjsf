package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class GoodsEvaluate implements Serializable {
    private Long goodsEvaluatId;

    private Long goodsId;

    private String orderNo;

    private Long userId;

    private String headImgUrl;

    private String nickname;

    private Integer evLevel;

    private String evContent;

    private String evaluatImgUrl;

    private Date evaluatDate;

    private static final long serialVersionUID = 1L;

    public Long getGoodsEvaluatId() {
        return goodsEvaluatId;
    }

    public void setGoodsEvaluatId(Long goodsEvaluatId) {
        this.goodsEvaluatId = goodsEvaluatId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getEvLevel() {
        return evLevel;
    }

    public void setEvLevel(Integer evLevel) {
        this.evLevel = evLevel;
    }

    public String getEvContent() {
        return evContent;
    }

    public void setEvContent(String evContent) {
        this.evContent = evContent;
    }

    public String getEvaluatImgUrl() {
        return evaluatImgUrl;
    }

    public void setEvaluatImgUrl(String evaluatImgUrl) {
        this.evaluatImgUrl = evaluatImgUrl;
    }

    public Date getEvaluatDate() {
        return evaluatDate;
    }

    public void setEvaluatDate(Date evaluatDate) {
        this.evaluatDate = evaluatDate;
    }

    @Override
    public String toString() {
        return "GoodsEvaluate{" +
                "goodsEvaluatId=" + goodsEvaluatId +
                ", goodsId=" + goodsId +
                ", orderNo='" + orderNo + '\'' +
                ", userId=" + userId +
                ", headImgUrl='" + headImgUrl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", evLevel=" + evLevel +
                ", evContent='" + evContent + '\'' +
                ", evaluatImgUrl='" + evaluatImgUrl + '\'' +
                ", evaluatDate=" + evaluatDate +
                '}';
    }
}