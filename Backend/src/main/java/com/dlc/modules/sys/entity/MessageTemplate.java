package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;

public class MessageTemplate implements Serializable {
    private Long mtId;

    private Long storeId;

    private String title;

    private String message;

    private Date createdDate;

    private static final long serialVersionUID = 1L;

    public MessageTemplate(Long mtId, Long storeId, String title, String message, Date createdDate) {
        this.mtId = mtId;
        this.storeId = storeId;
        this.title = title;
        this.message = message;
        this.createdDate = createdDate;
    }

    public MessageTemplate() {
        super();
    }

    public Long getMtId() {
        return mtId;
    }

    public void setMtId(Long mtId) {
        this.mtId = mtId;
    }

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
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
        sb.append(", mtId=").append(mtId);
        sb.append(", storeId=").append(storeId);
        sb.append(", title=").append(title);
        sb.append(", message=").append(message);
        sb.append(", createdDate=").append(createdDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}