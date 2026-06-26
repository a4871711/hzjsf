package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class BodyDimension implements Serializable {
    private Long id;

    private Long vid;

    private Long userId;

    private String scanId;

    private String height;

    private String leftUpperArmGirth;

    private String rightUpperArmGirth;

    private String bustGirth;

    private String waistGirth;

    private String hipGirth;

    private String rightThighGirth;

    private String leftThighGirth;

    private String rightCalfGirth;

    private String leftCalfGirth;

    private Long gymEngineId;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public BodyDimension(Long id, Long vid, Long userId, String scanId, String height, String leftUpperArmGirth, String rightUpperArmGirth, String bustGirth, String waistGirth, String hipGirth, String rightThighGirth, String leftThighGirth, String rightCalfGirth, String leftCalfGirth, Long gymEngineId, Date createTime) {
        this.id = id;
        this.vid = vid;
        this.userId = userId;
        this.scanId = scanId;
        this.height = height;
        this.leftUpperArmGirth = leftUpperArmGirth;
        this.rightUpperArmGirth = rightUpperArmGirth;
        this.bustGirth = bustGirth;
        this.waistGirth = waistGirth;
        this.hipGirth = hipGirth;
        this.rightThighGirth = rightThighGirth;
        this.leftThighGirth = leftThighGirth;
        this.rightCalfGirth = rightCalfGirth;
        this.leftCalfGirth = leftCalfGirth;
        this.gymEngineId = gymEngineId;
        this.createTime = createTime;
    }

    public BodyDimension() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId == null ? null : scanId.trim();
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height == null ? null : height.trim();
    }

    public String getLeftUpperArmGirth() {
        return leftUpperArmGirth;
    }

    public void setLeftUpperArmGirth(String leftUpperArmGirth) {
        this.leftUpperArmGirth = leftUpperArmGirth == null ? null : leftUpperArmGirth.trim();
    }

    public String getRightUpperArmGirth() {
        return rightUpperArmGirth;
    }

    public void setRightUpperArmGirth(String rightUpperArmGirth) {
        this.rightUpperArmGirth = rightUpperArmGirth == null ? null : rightUpperArmGirth.trim();
    }

    public String getBustGirth() {
        return bustGirth;
    }

    public void setBustGirth(String bustGirth) {
        this.bustGirth = bustGirth == null ? null : bustGirth.trim();
    }

    public String getWaistGirth() {
        return waistGirth;
    }

    public void setWaistGirth(String waistGirth) {
        this.waistGirth = waistGirth == null ? null : waistGirth.trim();
    }

    public String getHipGirth() {
        return hipGirth;
    }

    public void setHipGirth(String hipGirth) {
        this.hipGirth = hipGirth == null ? null : hipGirth.trim();
    }

    public String getRightThighGirth() {
        return rightThighGirth;
    }

    public void setRightThighGirth(String rightThighGirth) {
        this.rightThighGirth = rightThighGirth == null ? null : rightThighGirth.trim();
    }

    public String getLeftThighGirth() {
        return leftThighGirth;
    }

    public void setLeftThighGirth(String leftThighGirth) {
        this.leftThighGirth = leftThighGirth == null ? null : leftThighGirth.trim();
    }

    public String getRightCalfGirth() {
        return rightCalfGirth;
    }

    public void setRightCalfGirth(String rightCalfGirth) {
        this.rightCalfGirth = rightCalfGirth == null ? null : rightCalfGirth.trim();
    }

    public String getLeftCalfGirth() {
        return leftCalfGirth;
    }

    public void setLeftCalfGirth(String leftCalfGirth) {
        this.leftCalfGirth = leftCalfGirth == null ? null : leftCalfGirth.trim();
    }

    public Long getGymEngineId() {
        return gymEngineId;
    }

    public void setGymEngineId(Long gymEngineId) {
        this.gymEngineId = gymEngineId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", vid=").append(vid);
        sb.append(", userId=").append(userId);
        sb.append(", scanId=").append(scanId);
        sb.append(", height=").append(height);
        sb.append(", leftUpperArmGirth=").append(leftUpperArmGirth);
        sb.append(", rightUpperArmGirth=").append(rightUpperArmGirth);
        sb.append(", bustGirth=").append(bustGirth);
        sb.append(", waistGirth=").append(waistGirth);
        sb.append(", hipGirth=").append(hipGirth);
        sb.append(", rightThighGirth=").append(rightThighGirth);
        sb.append(", leftThighGirth=").append(leftThighGirth);
        sb.append(", rightCalfGirth=").append(rightCalfGirth);
        sb.append(", leftCalfGirth=").append(leftCalfGirth);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}