package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * VIP 权益卡商品(移动端展示用,对应表 vip_benefit_card)
 * currentPrice 为非表字段,由 service 按当前 sold_count 实时算出后填入
 */
public class VipBenefitCard implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 权益卡商品ID */
    private Long vipCardId;
    /** 权益卡名称 */
    private String cardName;
    /** 权益内容描述 */
    private String benefitDesc;
    /** 基础售价(购买价) */
    private BigDecimal price;
    /** 有效天数 */
    private Integer validityDays;
    /** 适用门店ID(逗号分隔的 store_addr_id) */
    private String storeAddrIds;
    /** 可绑定的会员卡ID列表(逗号分隔,取自 fit_card.fitCardId 且须为 cardNature=1 的权益卡性质记录);为空=不限制,任何会员均可购买 */
    private String bindFitCardIds;
    /** 关联转让费用规则 */
    private Long feeRuleId;
    /** 关联停卡规则(vip_pause_rule);为空=该卡未开通付费停卡 */
    private Long pauseRuleId;
    /** 是否展示实时购买人数 1是0否 */
    private Integer showBuyCount;
    /** 起涨基数:已购<=此值不涨价 */
    private Integer baseBuyCount;
    /** 每多少人涨一档(0=不动态涨价) */
    private Integer stepNum;
    /** 每档加价 */
    private BigDecimal stepAddPrice;
    /** 封顶价(NULL=不封顶) */
    private BigDecimal priceCap;
    /** 真实累计购买人数(系统维护,show_buy_count=0 时不对外暴露) */
    private Integer soldCount;
    /** 1上架 2下架 */
    private Integer status;
    /** 创建时间 */
    private Date createdDate;

    /** 非表字段:后端按当前 sold_count 实时算出的动态购买价 */
    private BigDecimal currentPrice;

    public Long getVipCardId() {
        return vipCardId;
    }

    public void setVipCardId(Long vipCardId) {
        this.vipCardId = vipCardId;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getBenefitDesc() {
        return benefitDesc;
    }

    public void setBenefitDesc(String benefitDesc) {
        this.benefitDesc = benefitDesc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getValidityDays() {
        return validityDays;
    }

    public void setValidityDays(Integer validityDays) {
        this.validityDays = validityDays;
    }

    public String getStoreAddrIds() {
        return storeAddrIds;
    }

    public void setStoreAddrIds(String storeAddrIds) {
        this.storeAddrIds = storeAddrIds;
    }

    public String getBindFitCardIds() {
        return bindFitCardIds;
    }

    public void setBindFitCardIds(String bindFitCardIds) {
        this.bindFitCardIds = bindFitCardIds;
    }

    public Long getFeeRuleId() {
        return feeRuleId;
    }

    public void setFeeRuleId(Long feeRuleId) {
        this.feeRuleId = feeRuleId;
    }

    public Long getPauseRuleId() {
        return pauseRuleId;
    }

    public void setPauseRuleId(Long pauseRuleId) {
        this.pauseRuleId = pauseRuleId;
    }

    public Integer getShowBuyCount() {
        return showBuyCount;
    }

    public void setShowBuyCount(Integer showBuyCount) {
        this.showBuyCount = showBuyCount;
    }

    public Integer getBaseBuyCount() {
        return baseBuyCount;
    }

    public void setBaseBuyCount(Integer baseBuyCount) {
        this.baseBuyCount = baseBuyCount;
    }

    public Integer getStepNum() {
        return stepNum;
    }

    public void setStepNum(Integer stepNum) {
        this.stepNum = stepNum;
    }

    public BigDecimal getStepAddPrice() {
        return stepAddPrice;
    }

    public void setStepAddPrice(BigDecimal stepAddPrice) {
        this.stepAddPrice = stepAddPrice;
    }

    public BigDecimal getPriceCap() {
        return priceCap;
    }

    public void setPriceCap(BigDecimal priceCap) {
        this.priceCap = priceCap;
    }

    public Integer getSoldCount() {
        return soldCount;
    }

    public void setSoldCount(Integer soldCount) {
        this.soldCount = soldCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }
}
