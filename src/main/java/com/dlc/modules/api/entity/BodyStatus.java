package com.dlc.modules.api.entity;

import java.io.Serializable;
import java.util.Date;

public class BodyStatus implements Serializable {
    private Long id;

    private String scanId;

    private Long userId;

    private Long vid;

    private String leftLeg;

    private String rightLeg;

    private String leftShoulder;

    private String rightShoulder;

    private String cavumConchae;

    private String hipJoint;

    private String shoulderMidpoint;

    private String acromionReference;

    private String results;

    private Date createTime;

    private static final long serialVersionUID = 1L;

    public BodyStatus(Long id, String scanId, Long userId, Long vid, String leftLeg, String rightLeg, String leftShoulder, String rightShoulder, String cavumConchae, String hipJoint, String shoulderMidpoint, String acromionReference, String results, Date createTime) {
        this.id = id;
        this.scanId = scanId;
        this.userId = userId;
        this.vid = vid;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.leftShoulder = leftShoulder;
        this.rightShoulder = rightShoulder;
        this.cavumConchae = cavumConchae;
        this.hipJoint = hipJoint;
        this.shoulderMidpoint = shoulderMidpoint;
        this.acromionReference = acromionReference;
        this.results = results;
        this.createTime = createTime;
    }

    public BodyStatus() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScanId() {
        return scanId;
    }

    public void setScanId(String scanId) {
        this.scanId = scanId == null ? null : scanId.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVid() {
        return vid;
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public String getLeftLeg() {
        return leftLeg;
    }

    public void setLeftLeg(String leftLeg) {
        this.leftLeg = leftLeg == null ? null : leftLeg.trim();
    }

    public String getRightLeg() {
        return rightLeg;
    }

    public void setRightLeg(String rightLeg) {
        this.rightLeg = rightLeg == null ? null : rightLeg.trim();
    }

    public String getLeftShoulder() {
        return leftShoulder;
    }

    public void setLeftShoulder(String leftShoulder) {
        this.leftShoulder = leftShoulder == null ? null : leftShoulder.trim();
    }

    public String getRightShoulder() {
        return rightShoulder;
    }

    public void setRightShoulder(String rightShoulder) {
        this.rightShoulder = rightShoulder == null ? null : rightShoulder.trim();
    }

    public String getCavumConchae() {
        return cavumConchae;
    }

    public void setCavumConchae(String cavumConchae) {
        this.cavumConchae = cavumConchae == null ? null : cavumConchae.trim();
    }

    public String getHipJoint() {
        return hipJoint;
    }

    public void setHipJoint(String hipJoint) {
        this.hipJoint = hipJoint == null ? null : hipJoint.trim();
    }

    public String getShoulderMidpoint() {
        return shoulderMidpoint;
    }

    public void setShoulderMidpoint(String shoulderMidpoint) {
        this.shoulderMidpoint = shoulderMidpoint == null ? null : shoulderMidpoint.trim();
    }

    public String getAcromionReference() {
        return acromionReference;
    }

    public void setAcromionReference(String acromionReference) {
        this.acromionReference = acromionReference == null ? null : acromionReference.trim();
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results == null ? null : results.trim();
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
        sb.append(", scanId=").append(scanId);
        sb.append(", userId=").append(userId);
        sb.append(", vid=").append(vid);
        sb.append(", leftLeg=").append(leftLeg);
        sb.append(", rightLeg=").append(rightLeg);
        sb.append(", leftShoulder=").append(leftShoulder);
        sb.append(", rightShoulder=").append(rightShoulder);
        sb.append(", cavumConchae=").append(cavumConchae);
        sb.append(", hipJoint=").append(hipJoint);
        sb.append(", shoulderMidpoint=").append(shoulderMidpoint);
        sb.append(", acromionReference=").append(acromionReference);
        sb.append(", results=").append(results);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}