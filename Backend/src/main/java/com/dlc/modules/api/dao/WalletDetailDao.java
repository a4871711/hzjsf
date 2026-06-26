package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.WalletDetailEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 钱包明细表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-21 15:54:31
 */
@Mapper
@Repository
public interface WalletDetailDao extends BaseDao<WalletDetailEntity> {

    Double queryWithdrawCash(Long userId);

    List<Map<String,Object>> queryWithdrawCashList(Query query);

    int queryWithdrawCashListTotal(Query query);

    BigDecimal queryCoachWalletBalance(Long coachId);

    int updateCoachWalletMoney(Map<String, Object> param);

    int updateDetailByOrderNo(Map<String, Object> updateMap);
    //根据订单编号查询用户id
    Long queryUidByOrderNo(String orderNo);
}
