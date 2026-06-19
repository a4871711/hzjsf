package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author LXK
 * @date 2018/9/11 11:15
 */
public class Advertising implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long advId;
    private Integer advType;
    private String advTitle;
    private String advMainImg;
    private String advContent;
    private String figureImg;
    private Date createdDate;

    public Long getAdvId() {
        return advId;
    }

    public void setAdvId(Long advId) {
        this.advId = advId;
    }

    public Integer getAdvType() {
        return advType;
    }

    public void setAdvType(Integer advType) {
        this.advType = advType;
    }

    public String getAdvTitle() {
        return advTitle;
    }

    public void setAdvTitle(String advTitle) {
        this.advTitle = advTitle;
    }

    public String getAdvMainImg() {
        return advMainImg;
    }

    public void setAdvMainImg(String advMainImg) {
        this.advMainImg = advMainImg;
    }

    public String getAdvContent() {
        return advContent;
    }

    public void setAdvContent(String advContent) {
        this.advContent = advContent;
    }

    public String getFigureImg() {
        return figureImg;
    }

    public void setFigureImg(String figureImg) {
        this.figureImg = figureImg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Advertising{" +
                "advId=" + advId +
                ", advType=" + advType +
                ", advTitle='" + advTitle + '\'' +
                ", advMainImg='" + advMainImg + '\'' +
                ", advContent='" + advContent + '\'' +
                ", figureImg='" + figureImg + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }


}
