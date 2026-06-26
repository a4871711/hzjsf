package com.dlc.modules.api.vo;

import java.io.Serializable;

public class PapPayApplyVo implements Serializable {
    /**
     * 门店id
     */
    private Long storeId;
    /**
     * 门店名称
     */
    private String storeName;
    /**
     * 门店地址
     */
    private String storeAddress;
    /**
     * 商户系统内部的订单号
     */
    private String outTradeNo;
    /**
     * 微信返回的委托代扣协议id
     */
    private String contractId;
    /**
     * 订单总金额，单位为分
     */
    private Integer totalFee;

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
        this.storeId = storeId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getOutTradeNo() {
        return outTradeNo;
    }

    public void setOutTradeNo(String outTradeNo) {
        this.outTradeNo = outTradeNo;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Integer getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Integer totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    public String toString() {
        return "PapPayApplyVo{" +
                "storeId=" + storeId +
                ", storeName='" + storeName + '\'' +
                ", storeAddress='" + storeAddress + '\'' +
                ", outTradeNo='" + outTradeNo + '\'' +
                ", contractId='" + contractId + '\'' +
                ", totalFee=" + totalFee +
                '}';
    }
}