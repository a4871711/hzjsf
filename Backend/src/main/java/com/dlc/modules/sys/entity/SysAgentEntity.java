package com.dlc.modules.sys.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:09
 * @description 尺寸表
 */
public class SysAgentEntity implements Serializable {
	/** ID */
    private Long id;

    /** 父ID */
    private Long parentId;

    /** 用户类型：0 广告主，1 供货主，2 代理商（管理员），3 运维人员，4  财务人员 5合伙人(2024) 6管理员(2024) */
    private Long type;

    /** 昵称 */
    private String name;

    /** 用户头像 */
    private String headImgUrl;

    /** 手机号 */
    private String phone;

    /** 密码 */
    private String password;

    /** 钱包余额 */
    private Long wallet;

    /** 用户状态(1 启用 2 禁用) */
    private Integer status;

    /** 用户角色：0 普通用户，1省级，2 市级，3 区级，4个人（这个字段备用） */
    private Long role;

    /** 删除状态 0 可用，1 删除 */
    private Long deleteStatus;

    /** 代理城市 */
    private String city;

    /** 微信openId */
    private String openId;

    /** 佣金类型 1 按比例，2 按金额 */
    private Long commissionType;

    /** 分佣值 */
    private Long commissionValue;

    private Date createTime;
    
    private Integer limit;
    private Integer offset;
    private Integer page;
    private String ids;
    private Date startTime;
    private Date endTime;
    
    private int storeNum;
    private BigDecimal storeMonthMoney;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getType() {
		return type;
	}

	public void setType(Long type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getWallet() {
		return wallet;
	}

	public void setWallet(Long wallet) {
		this.wallet = wallet;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getRole() {
		return role;
	}

	public void setRole(Long role) {
		this.role = role;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
	
	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public Long getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(Long deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	public Long getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(Long commissionType) {
		this.commissionType = commissionType;
	}

	public Long getCommissionValue() {
		return commissionValue;
	}

	public void setCommissionValue(Long commissionValue) {
		this.commissionValue = commissionValue;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public BigDecimal getStoreMonthMoney() {
		return storeMonthMoney;
	}

	public void setStoreMonthMoney(BigDecimal storeMonthMoney) {
		this.storeMonthMoney = storeMonthMoney;
	}

	public int getStoreNum() {
		return storeNum;
	}

	public void setStoreNum(int storeNum) {
		this.storeNum = storeNum;
	}
}
