package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * lingkangming
 * 门店表
 */
public class SysStoreEntity implements Serializable {
    //门店ID
    private Long storeId;
    //门店名称
    private String storeName;
    //门店位置ID
    private Long storeAddrId;
    //门店地址
    private String storeAddr;
    //门店详情图
    private String storeImgUrl;
    //门店电话
    private String storePhone;
    //场内实时人数
    private int currentNum;
    //门店地址
    private String storeArea;
    //营业状态1：正常
    private Integer status;
    //创建时间
    private Date createdDate;
    //营业时间
    private String hours;
    
    private String agent5Ids;
    private String agent6Ids;
    
    private List<SysAgentEntity> agent5;
    private List<SysAgentEntity> agent6;
    
    private List<SysUserEntity> sysUser5;
    private List<SysUserEntity> sysUser6;
    
    private Map<String, Object> stats;
    
    //省
    private String province;
    //市
    private String city;
    //区
    private String zone;
    //门店详细地址
    private String storeAddrDetail;
    //经度
    private String longitude;
    //纬度
    private String latitude;
    
    private Integer goodsIdStoreId;

    /** 抖音核销门店 poi_id */
    private String douyinPoiId;

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

    public Long getStoreAddrId() {
        return storeAddrId;
    }

    public void setStoreAddrId(Long storeAddrId) {
        this.storeAddrId = storeAddrId;
    }

    public String getStoreImgUrl() {
        return storeImgUrl;
    }

    public void setStoreImgUrl(String storeImgUrl) {
        this.storeImgUrl = storeImgUrl;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    public String getStoreArea() {
        return storeArea;
    }

    public void setStoreArea(String storeArea) {
        this.storeArea = storeArea;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getStoreAddr() {
        return storeAddr;
    }

    public void setStoreAddr(String storeAddr) {
        this.storeAddr = storeAddr;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

	public String getHours() {
		return hours;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}

	public String getAgent5Ids() {
		return agent5Ids;
	}

	public void setAgent5Ids(String agent5Ids) {
		this.agent5Ids = agent5Ids;
	}

	public String getAgent6Ids() {
		return agent6Ids;
	}

	public void setAgent6Ids(String agent6Ids) {
		this.agent6Ids = agent6Ids;
	}

	public List<SysAgentEntity> getAgent5() {
		return agent5;
	}

	public void setAgent5(List<SysAgentEntity> agent5) {
		this.agent5 = agent5;
	}

	public List<SysAgentEntity> getAgent6() {
		return agent6;
	}

	public void setAgent6(List<SysAgentEntity> agent6) {
		this.agent6 = agent6;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getStoreAddrDetail() {
		return storeAddrDetail;
	}

	public void setStoreAddrDetail(String storeAddrDetail) {
		this.storeAddrDetail = storeAddrDetail;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public Map<String, Object> getStats() {
		return stats;
	}

	public void setStats(Map<String, Object> stats) {
		this.stats = stats;
	}

	public List<SysUserEntity> getSysUser5() {
		return sysUser5;
	}

	public void setSysUser5(List<SysUserEntity> sysUser5) {
		this.sysUser5 = sysUser5;
	}

	public List<SysUserEntity> getSysUser6() {
		return sysUser6;
	}

	public void setSysUser6(List<SysUserEntity> sysUser6) {
		this.sysUser6 = sysUser6;
	}

	public Integer getGoodsIdStoreId() {
		return goodsIdStoreId;
	}

	public void setGoodsIdStoreId(Integer goodsIdStoreId) {
		this.goodsIdStoreId = goodsIdStoreId;
	}

	public String getDouyinPoiId() {
		return douyinPoiId;
	}

	public void setDouyinPoiId(String douyinPoiId) {
		this.douyinPoiId = douyinPoiId;
	}
}
