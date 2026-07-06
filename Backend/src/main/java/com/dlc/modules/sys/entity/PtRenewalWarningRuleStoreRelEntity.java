package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 续费预警规则适用门店关联表 pt_renewal_warning_rule_store_rel（第22步·运营域·续费预警）。
 * 规则 ↔ 门店多对多；编辑全删全插；无记录=全部门店。
 *
 * @author claude
 */
public class PtRenewalWarningRuleStoreRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long ruleId;
    private Long storeId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getRuleId() { return ruleId; }
    public void setRuleId(Long ruleId) { this.ruleId = ruleId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
