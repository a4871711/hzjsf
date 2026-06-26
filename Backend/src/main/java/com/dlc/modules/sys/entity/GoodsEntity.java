package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品表
 */
public class GoodsEntity implements Serializable {
    private Long id;
    //ID
    private Long goodsId;
    //商品类型ID
    private int categoryId;
    //商品名称
    private String name;
    //商品价格
    private BigDecimal price;
    //商品型号
    private String style;
    //商品颜色
    private String color;
    //商品大小
    private String size;
    //库存量
    private int total;
    //月销量
    private int monthlySales;
    //商品主图
    private String imgUrl;
    //商品状态0.下架1.上架，默认为1
    private Integer status;
    //运费
    private BigDecimal freight;
    //商品描述
    private String remark;
    //轮播图
    private String carouselImgUrl;
    //商品详情图
    private String remarkImgUrl;
    //删除状态
    private Integer deleteStatus;
    //创建时间
    private Date createdDate;
    //二级分类名称
    private String nextCategoryName;

    public String getNextCategoryName() {
        return nextCategoryName;
    }

    public void setNextCategoryName(String nextCategoryName) {
        this.nextCategoryName = nextCategoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMonthlySales() {
        return monthlySales;
    }

    public void setMonthlySales(int monthlySales) {
        this.monthlySales = monthlySales;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getFreight() {
        return freight;
    }

    public void setFreight(BigDecimal freight) {
        this.freight = freight;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCarouselImgUrl() {
        return carouselImgUrl;
    }

    public void setCarouselImgUrl(String carouselImgUrl) {
        this.carouselImgUrl = carouselImgUrl;
    }

    public String getRemarkImgUrl() {
        return remarkImgUrl;
    }

    public void setRemarkImgUrl(String remarkImgUrl) {
        this.remarkImgUrl = remarkImgUrl;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}
