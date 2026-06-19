package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysCouponDao;
import com.dlc.modules.sys.dao.SysStoreDao;
import com.dlc.modules.sys.entity.SysAgentEntity;
import com.dlc.modules.sys.entity.SysCouponEntity;
import com.dlc.modules.sys.entity.SysStoreEntity;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.entity.SysUserRoleEntity;
import com.dlc.modules.sys.service.SysCouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysCouponServiceImpl implements SysCouponService {

	@Autowired
    private SysCouponDao sysCouponMapper;
	@Autowired
    private SysStoreDao sysStoreDao;

    /**
     * 查询优惠券表
     * 
     * @param sysCouponId 优惠券表ID
     * @return 优惠券表
     */
    @Override
    public SysCouponEntity selectSysCouponById(Long sysCouponId)
    {
        return sysCouponMapper.selectSysCouponById(sysCouponId);
    }

    /**
     * 查询优惠券表列表
     * 
     * @param sysCoupon 优惠券表
     * @return 优惠券表
     */
    @Override
    public List<SysCouponEntity> selectSysCouponList(SysCouponEntity sysCoupon)
    {
    	List<SysCouponEntity> list = sysCouponMapper.selectSysCouponList(sysCoupon);
		if(list != null && !list.isEmpty()) {
			for(SysCouponEntity item: list) {			
				List<SysStoreEntity> storeList = sysStoreDao.queryObjectByAddr(item.getStoreAddrIds());
				item.setStoreList(storeList);
			}
		}
		return list;
    }
    
    @Override
    public int selectSysCouponCount(SysCouponEntity sysCoupon)
    {
        return sysCouponMapper.selectSysCouponCount(sysCoupon);
    }

    /**
     * 新增优惠券表
     * 
     * @param sysCoupon 优惠券表
     * @return 结果
     */
    @Override
    public int insertSysCoupon(SysCouponEntity sysCoupon)
    {
        return sysCouponMapper.insertSysCoupon(sysCoupon);
    }

    /**
     * 修改优惠券表
     * 
     * @param sysCoupon 优惠券表
     * @return 结果
     */
    @Override
    public int updateSysCoupon(SysCouponEntity sysCoupon)
    {
        return sysCouponMapper.updateSysCoupon(sysCoupon);
    }

    /**
     * 批量删除优惠券表
     * 
     * @param sysCouponIds 需要删除的优惠券表ID
     * @return 结果
     */
    @Override
    public int deleteSysCouponByIds(Long[] sysCouponIds)
    {
        return sysCouponMapper.deleteSysCouponByIds(sysCouponIds);
    }

    /**
     * 删除优惠券表信息
     * 
     * @param sysCouponId 优惠券表ID
     * @return 结果
     */
    @Override
    public int deleteSysCouponById(Long sysCouponId)
    {
        return sysCouponMapper.deleteSysCouponById(sysCouponId);
    }
}
