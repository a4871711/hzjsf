package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 14:53
 * @description 颜色表
 */
public class ColorEntity implements Serializable{
    //颜色id
    private Long colorId;
    //颜色
    private String color;

    public Long getColorId() {
        return colorId;
    }

    public void setColorId(Long colorId) {
        this.colorId = colorId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
