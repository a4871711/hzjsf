package com.dlc.modules.sys.service;

import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.WalletDetailEntity;

import java.util.List;
import java.util.Map;

/**
 * 钱包明细表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-07-24 13:42:27
 */
public interface SysWalletDetailService {
	
	WalletDetailEntity queryObject(Long id);
	
	List<WalletDetailEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(WalletDetailEntity walletDetail);
	
	void update(WalletDetailEntity walletDetail);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
	int updateById(WalletDetailEntity walletDetailEntity);

//    R payToUser(String openId, Integer money, String orderNo, Long id, Long userId) throws Exception;

    //int updateWalletDetailStatus(Integer status, Long id, String orderNo, Long userId, Integer type);

    R updateWalletDetailStatus(WalletDetailEntity walletDetail, int flag);

    int saveWalletDetail(Map<String, Object> params);
}
