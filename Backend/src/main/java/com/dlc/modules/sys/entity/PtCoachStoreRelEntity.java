package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 私教教练-门店关联表 pt_coach_store_rel
 *
 * @author claude
 */
public class PtCoachStoreRelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long coachId;
    private Long storeId;
    private Date createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCoachId() { return coachId; }
    public void setCoachId(Long coachId) { this.coachId = coachId; }

    public Long getStoreId() { return storeId; }
    public void setStoreId(Long storeId) { this.storeId = storeId; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
