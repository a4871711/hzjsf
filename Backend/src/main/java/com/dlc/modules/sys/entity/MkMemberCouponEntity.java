package com.dlc.modules.sys.entity;

import java.io.Serializable;

/**
 * 会员优惠券领取记录表 mk_member_coupon（快照 coupon_name/coupon_type，券模板改名不回溯）。
 * use_status：0未使用 1已使用 2已过期 3使用中（下单占用态，见交易域 CAS 口径）。
 * 时间字段用 String 承接 SQL DATE_FORMAT 结果（与 PtCoachScheduleEntity 同一约定）。
 *
 * @author claude
 */
public class MkMemberCouponEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long memberId;
    private Long couponId;
    /** 券名称快照 */
    private String couponName;
    /** 券类型快照：1满减券 2折扣券 */
    private Integer couponType;
    private String receiveTime;
    /** 到期时间 = receive_time + valid_days（落库即算） */
    private String expireTime;
    /** 使用状态：0未使用 1已使用 2已过期 3使用中 */
    private Integer useStatus;
    /** 使用的私教订单ID（→ pt_private_order） */
    private Long usedOrderId;
    private String usedTime;

    /* ===== 非持久字段 ===== */
    /** 发券时的有效天数（insert 用 DATE_ADD(NOW(), INTERVAL validDays DAY) 计算 expire_time） */
    private Integer validDays;
    /** 会员昵称（列表联 user_info） */
    private String memberName;
    /** 会员手机号（列表联 user_info） */
    private String memberMobile;
    /** 使用订单号（列表联 pt_private_order） */
    private String usedOrderNo;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public Long getCouponId() { return couponId; }
    public void setCouponId(Long couponId) { this.couponId = couponId; }

    public String getCouponName() { return couponName; }
    public void setCouponName(String couponName) { this.couponName = couponName; }

    public Integer getCouponType() { return couponType; }
    public void setCouponType(Integer couponType) { this.couponType = couponType; }

    public String getReceiveTime() { return receiveTime; }
    public void setReceiveTime(String receiveTime) { this.receiveTime = receiveTime; }

    public String getExpireTime() { return expireTime; }
    public void setExpireTime(String expireTime) { this.expireTime = expireTime; }

    public Integer getUseStatus() { return useStatus; }
    public void setUseStatus(Integer useStatus) { this.useStatus = useStatus; }

    public Long getUsedOrderId() { return usedOrderId; }
    public void setUsedOrderId(Long usedOrderId) { this.usedOrderId = usedOrderId; }

    public String getUsedTime() { return usedTime; }
    public void setUsedTime(String usedTime) { this.usedTime = usedTime; }

    public Integer getValidDays() { return validDays; }
    public void setValidDays(Integer validDays) { this.validDays = validDays; }

    public String getMemberName() { return memberName; }
    public void setMemberName(String memberName) { this.memberName = memberName; }

    public String getMemberMobile() { return memberMobile; }
    public void setMemberMobile(String memberMobile) { this.memberMobile = memberMobile; }

    public String getUsedOrderNo() { return usedOrderNo; }
    public void setUsedOrderNo(String usedOrderNo) { this.usedOrderNo = usedOrderNo; }
}
