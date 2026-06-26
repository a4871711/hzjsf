package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.entity.SysCouponEntity;

import java.util.List;

public interface SysCouponService {
	/**
     * 查询优惠券表
     * 
     * @param sysCouponId 优惠券表ID
     * @return 优惠券表
     */
    public SysCouponEntity selectSysCouponById(Long sysCouponId);

    /**
     * 查询优惠券表列表
     * 
     * @param sysCoupon 优惠券表
     * @return 优惠券表集合
     */
    public List<SysCouponEntity> selectSysCouponList(SysCouponEntity sysCoupon);
    public int selectSysCouponCount(SysCouponEntity sysCoupon);

    /**
     * 新增优惠券表
     * 
     * @param sysCoupon 优惠券表
     * @return 结果
     */
    public int insertSysCoupon(SysCouponEntity sysCoupon);

    /**
     * 修改优惠券表
     * 
     * @param sysCoupon 优惠券表
     * @return 结果
     */
    public int updateSysCoupon(SysCouponEntity sysCoupon);

    /**
     * 批量删除优惠券表
     * 
     * @param sysCouponIds 需要删除的优惠券表ID
     * @return 结果
     */
    public int deleteSysCouponByIds(Long[] sysCouponIds);

    /**
     * 删除优惠券表信息
     * 
     * @param sysCouponId 优惠券表ID
     * @return 结果
     */
    public int deleteSysCouponById(Long sysCouponId);

}
