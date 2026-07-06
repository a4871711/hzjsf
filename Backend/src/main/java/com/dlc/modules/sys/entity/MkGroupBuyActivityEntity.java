package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 拼团活动主表 mk_group_buy_activity。状态：1上架/0下架。
 * sold_count 后台只读（购买回调 CAS +1，update 语句不写该列）。
 * start_time/end_time 用 String 承接 SQL DATE_FORMAT 结果（与 PtCoachScheduleEntity 同一约定）。
 *
 * @author claude
 */
public class MkGroupBuyActivityEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String activityName;
    /** 关联私教商品ID（→ pt_product.id） */
    private Long productId;
    /** 拼团价（≤ 商品 sale_price） */
    private BigDecimal groupBuyPrice;
    /** 成团人数（>1） */
    private Integer groupMemberCount;
    /** 活动库存，NULL=不限量 */
    private Integer activityStock;
    /** 已售数量（后台只读） */
    private Integer soldCount;
    /** 每人限购，NULL=不限购 */
    private Integer purchaseLimit;
    private String startTime;
    private String endTime;
    private String mtGroupBuyId;
    private String dyGroupBuyId;
    /** 状态：1上架 0下架 */
    private Integer status;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    /** 是否删除：0否 1是 */
    private Integer deleted;

    /* ===== 非持久字段 ===== */
    /** 商品名（列表联 pt_product） */
    private String productName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public BigDecimal getGroupBuyPrice() { return groupBuyPrice; }
    public void setGroupBuyPrice(BigDecimal groupBuyPrice) { this.groupBuyPrice = groupBuyPrice; }

    public Integer getGroupMemberCount() { return groupMemberCount; }
    public void setGroupMemberCount(Integer groupMemberCount) { this.groupMemberCount = groupMemberCount; }

    public Integer getActivityStock() { return activityStock; }
    public void setActivityStock(Integer activityStock) { this.activityStock = activityStock; }

    public Integer getSoldCount() { return soldCount; }
    public void setSoldCount(Integer soldCount) { this.soldCount = soldCount; }

    public Integer getPurchaseLimit() { return purchaseLimit; }
    public void setPurchaseLimit(Integer purchaseLimit) { this.purchaseLimit = purchaseLimit; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getMtGroupBuyId() { return mtGroupBuyId; }
    public void setMtGroupBuyId(String mtGroupBuyId) { this.mtGroupBuyId = mtGroupBuyId; }

    public String getDyGroupBuyId() { return dyGroupBuyId; }
    public void setDyGroupBuyId(String dyGroupBuyId) { this.dyGroupBuyId = dyGroupBuyId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public Long getCreatedBy() { return createdBy; }
    public void setCreatedBy(Long createdBy) { this.createdBy = createdBy; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Long getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Long updatedBy) { this.updatedBy = updatedBy; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
