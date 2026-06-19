package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class StoreCoachShip implements Serializable {
    private Long storeCoachShipId;

    private Long storeId;

    private Long coachId;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public StoreCoachShip(Long storeCoachShipId, Long storeId, Long coachId, Date createdDate) {
        this.storeCoachShipId = storeCoachShipId;
        this.storeId = storeId;
        this.coachId = coachId;
        this.createdDate = createdDate;
    }

    public StoreCoachShip() {
        super();
    }

    public Long getStoreCoachShipId() {
        return storeCoachShipId;
    }

    public void setStoreCoachShipId(Long storeCoachShipId) {
        this.storeCoachShipId = storeCoachShipId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public Long getCoachId() {
        return coachId;
    }

    public void setCoachId(Long coachId) {
        this.coachId = coachId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", storeCoachShipId=").append(storeCoachShipId);
        sb.append(", storeId=").append(storeId);
        sb.append(", coachId=").append(coachId);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}