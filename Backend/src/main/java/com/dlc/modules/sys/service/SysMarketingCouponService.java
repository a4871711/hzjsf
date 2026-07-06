package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.MkCouponEntity;

import java.util.List;
import java.util.Map;

/**
 * 优惠券 Service（mk_coupon）。save/update 单事务写主表 + 门店/商品两张 rel（全删全插）。
 *
 * @author claude
 */
public interface SysMarketingCouponService {

    MkCouponEntity queryObject(Long id);

    List<MkCouponEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(MkCouponEntity entity);

    void update(MkCouponEntity entity);

    /** 逻辑删除；已有领取记录（mk_member_coupon）的券拒删，提示改用下架 */
    void deleteBatch(Long[] ids);

    /** 上下架切换：1上架/0下架。下架不影响已领券（仍可用到 expire_time），仅不可再领 */
    void changeStatus(Long id, Integer status, Long userId);

    /**
     * 后台手动发券：仅对上架券；逐会员写 mk_member_coupon（快照券名/类型，expire_time=now+valid_days，
     * use_status=0）；允许重复发；单条失败不影响其余。
     *
     * @return 成功发放条数
     */
    int grant(Long couponId, List<Long> memberIds);
}
