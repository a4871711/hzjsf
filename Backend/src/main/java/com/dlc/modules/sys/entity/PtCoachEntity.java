package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 私教教练主表 pt_coach
 * 注意：与旧 SysCoachEntity(coach 表) 并存不复用，私教域专用。
 *
 * @author claude
 */
public class PtCoachEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    /** 教练编号 JL+yyyyMMdd+4序 */
    private String coachNo;
    private String coachName;
    private String mobile;
    /** 性别：1男 2女 0未知 */
    private Integer gender;
    private String avatarUrl;
    /** 等级名称（冗余存名称，与 pt_coach_level 同步） */
    private String coachLevel;
    private String intro;
    /** 资格证书图片/附件地址 JSON 字符串 */
    private String certificateUrls;
    /** 教练状态：1正常 2停用 3离职 */
    private Integer status;
    private String disableReason;
    private Integer sortNo;
    private Long createdBy;
    private Date createdAt;
    private Long updatedBy;
    private Date updatedAt;
    private Integer deleted;

    /** 非持久字段：所属门店ID集合，用于新增/编辑表单与回填 */
    private List<Long> storeIds;
    /** 非持久字段：列表展示用门店名称（逗号拼接） */
    private String storeNames;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCoachNo() { return coachNo; }
    public void setCoachNo(String coachNo) { this.coachNo = coachNo; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getCoachLevel() { return coachLevel; }
    public void setCoachLevel(String coachLevel) { this.coachLevel = coachLevel; }

    public String getIntro() { return intro; }
    public void setIntro(String intro) { this.intro = intro; }

    public String getCertificateUrls() { return certificateUrls; }
    public void setCertificateUrls(String certificateUrls) { this.certificateUrls = certificateUrls; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getDisableReason() { return disableReason; }
    public void setDisableReason(String disableReason) { this.disableReason = disableReason; }

    public Integer getSortNo() { return sortNo; }
    public void setSortNo(Integer sortNo) { this.sortNo = sortNo; }

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

    public List<Long> getStoreIds() { return storeIds; }
    public void setStoreIds(List<Long> storeIds) { this.storeIds = storeIds; }

    public String getStoreNames() { return storeNames; }
    public void setStoreNames(String storeNames) { this.storeNames = storeNames; }
}
