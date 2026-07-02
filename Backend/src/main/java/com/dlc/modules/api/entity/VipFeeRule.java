package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * VIP 转让费用规则(按转让次数分档定额,移动端只读)
 * 对应表 vip_fee_rule;tiers_json 存分档数组,被 vip_benefit_card.fee_rule_id 引用。
 * 与 sys 侧 VipFeeRuleEntity 同表,api 侧仅在试算/发起时读 tiers_json 算费用。
 */
public class VipFeeRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long feeRuleId;
    /** 规则名称 */
    private String ruleName;
    /** 分档JSON,形如 [{"fromCount":1,"fee":0},{"fromCount":2,"fee":50},{"fromCount":3,"fee":100}] */
    private String tiersJson;
    /** 1启用 0停用 */
    private Integer status;
    /** 创建时间 */
    private Date createdDate;

    public Long getFeeRuleId() {
        return feeRuleId;
    }

    public void setFeeRuleId(Long feeRuleId) {
        this.feeRuleId = feeRuleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getTiersJson() {
        return tiersJson;
    }

    public void setTiersJson(String tiersJson) {
        this.tiersJson = tiersJson;
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
}
