package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.WalletDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 钱包明细表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-07-24 13:42:27
 */
@Mapper
@Repository
public interface SysWalletDetailDao extends BaseDao<WalletDetailEntity> {

    int updateUserMoney(Map<String, Object> updateMap);

    int updateCoachMoney(Map<String, Object> updateMap);
    int updateById(WalletDetailEntity walletDetailEntity);
}
