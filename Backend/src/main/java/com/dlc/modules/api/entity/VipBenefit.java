package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户持有的权益卡实例(对应表 vip_benefit,被转让的对象)
 * cardName 为非表字段,myBenefits 列表 join vip_benefit_card 带出卡名展示用
 */
public class VipBenefit implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 权益实例ID */
    private Long vipBenefitId;
    /** 当前持有人 */
    private Long userId;
    /** 原始购买人(留痕,永不变) */
    private Long originUserId;
    /** 来源权益卡商品 */
    private Long vipCardId;
    /** 购买订单号(末位后缀6),作回调激活的幂等键 */
    private String sourceOrderNo;
    /** 购买/归属门店 */
    private Long storeId;
    /** 购买/归属门店地址 */
    private Long storeAddrId;
    /** 购买时售价(留痕,不参与转让费用计算) */
    private BigDecimal originPrice;
    /** 生效时间(支付成功激活才赋值) */
    private Date startTime;
    /** 到期时间(支付成功激活才赋值) */
    private Date expireTime;
    /** 9待支付 0正常 1已转出失效 2已冻结 3已过期 */
    private Integer status;
    /** 已被转让次数 */
    private Integer transferCount;
    /** 是否可转 1是0否 */
    private Integer transferable;
    /** 创建时间 */
    private Date createdDate;

    /** 非表字段:来源权益卡名称(myBenefits 展示用) */
    private String cardName;

    public Long getVipBenefitId() {
        return vipBenefitId;
    }

    public void setVipBenefitId(Long vipBenefitId) {
        this.vipBenefitId = vipBenefitId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOriginUserId() {
        return originUserId;
    }

    public void setOriginUserId(Long originUserId) {
        this.originUserId = originUserId;
    }

    public Long getVipCardId() {
        return vipCardId;
    }

    public void setVipCardId(Long vipCardId) {
        this.vipCardId = vipCardId;
    }

    public String getSourceOrderNo() {
        return sourceOrderNo;
    }

    public void setSourceOrderNo(String sourceOrderNo) {
        this.sourceOrderNo = sourceOrderNo;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public BigDecimal getOriginPrice() {
        return originPrice;
    }

    public void setOriginPrice(BigDecimal originPrice) {
        this.originPrice = originPrice;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTransferCount() {
        return transferCount;
    }

    public void setTransferCount(Integer transferCount) {
        this.transferCount = transferCount;
    }

    public Integer getTransferable() {
        return transferable;
    }

    public void setTransferable(Integer transferable) {
        this.transferable = transferable;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
