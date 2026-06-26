package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:09
 * @description 尺寸表
 */
public class SizeEntity implements Serializable {
    //尺寸id
    private Long sizeId;
    //尺寸大小
    private String size;

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        this.sizeId = sizeId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
