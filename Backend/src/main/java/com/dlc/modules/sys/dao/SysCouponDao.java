package com.dlc.modules.sys.dao;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.entity.SysCouponEntity;

/**
 * @author LINGKANGMING
 * @createTime 2018 - 09 - 20 15:02
 * @description 尺寸dao
 */
@Mapper
@Repository
public interface SysCouponDao extends BaseDao<SysCouponEntity>{

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
     * 删除优惠券表
     * 
     * @param sysCouponId 优惠券表ID
     * @return 结果
     */
    public int deleteSysCouponById(Long sysCouponId);

    /**
     * 批量删除优惠券表
     * 
     * @param sysCouponIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteSysCouponByIds(Long[] sysCouponIds);
    
    public Map<String, Object> selectCouponStats(Map<String, Object> params);
}
