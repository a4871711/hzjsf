package com.dlc.modules.api.entity;

import java.io.Serializable;

public class Protocol implements Serializable {
    private Long pId;

    private Integer type;

    private Integer selected;

    private String protocols;

    private static final long serialVersionUID = 1L;

    public Protocol(Long pId, Integer type, Integer selected, String protocols) {
        this.pId = pId;
        this.type = type;
        this.selected = selected;
        this.protocols = protocols;
    }

    public Protocol() {
        super();
    }

    public Long getpId() {
        return pId;
    }

    public void setpId(Long pId) {
        this.pId = pId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols == null ? null : protocols.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", pId=").append(pId);
        sb.append(", type=").append(type);
        sb.append(", selected=").append(selected);
        sb.append(", protocols=").append(protocols);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}