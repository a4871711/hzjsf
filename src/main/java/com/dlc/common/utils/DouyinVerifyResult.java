package com.dlc.common.utils;

/**
 * 抖音验券核销结果（撤销核销需 certificate_id、verify_id）
 */
public class DouyinVerifyResult {

    private String verifyId;
    private String certificateId;

    public String getVerifyId() {
        return verifyId;
    }

    public void setVerifyId(String verifyId) {
        this.verifyId = verifyId;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }
}
