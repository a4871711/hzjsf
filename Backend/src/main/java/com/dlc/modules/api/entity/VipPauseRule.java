package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 停卡规则(按天数分档定价,移动端只读)
 * 对应表 vip_pause_rule;tiers_json 存分档数组,被 vip_benefit_card.pause_rule_id 引用。
 * api 侧仅在停卡预检/付费停卡申请时读 tiers_json 算档位。
 */
public class VipPauseRule implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long pauseRuleId;
    /** 规则名称 */
    private String ruleName;
    /** 分档JSON,形如 [{"days":3,"price":100},{"days":10,"price":200}] */
    private String tiersJson;
    /** 1启用 0停用 */
    private Integer status;
    /** 创建时间 */
    private Date createdDate;

    public Long getPauseRuleId() {
        return pauseRuleId;
    }

    public void setPauseRuleId(Long pauseRuleId) {
        this.pauseRuleId = pauseRuleId;
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
