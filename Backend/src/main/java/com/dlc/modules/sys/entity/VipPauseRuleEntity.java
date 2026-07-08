package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * VIP 停卡规则(按停卡天数分档定价)
 * 对应表 vip_pause_rule;tiers_json 存分档数组,被 vip_benefit_card.pause_rule_id 引用
 */
public class VipPauseRuleEntity implements Serializable {
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
