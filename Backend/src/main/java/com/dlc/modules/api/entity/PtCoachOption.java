package com.dlc.modules.api.entity;

import java.io.Serializable;

/**
 * 会员端可预约教练选项（供 listByProduct 展示，需求 8.3 交集计算结果）。
 *
 * @author claude
 */
public class PtCoachOption implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String coachName;
    private String avatarUrl;
    private String coachLevel;
    private String intro;
    private Integer sortNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCoachLevel() { return coachLevel; }
    public void setCoachLevel(String coachLevel) { this.coachLevel = coachLevel; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }
}
