package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysIncomePayDetailEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 收支明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:19
 */
@Mapper
@Repository
public interface SysIncomePayDetailDao extends BaseDao<SysIncomePayDetailEntity> {
    String countMoney();
    String storedCountMoney(Map<String, Object> params);

    //提现退款
    int insertBack(SysIncomePayDetailEntity record);

    int queryStoredTotal(Query query);
    int queryAllStoredTotal(Query query);

    List<SysIncomePayDetailEntity> queryStoredList(Map<String, Object> query);
    List<SysIncomePayDetailEntity> queryAllStoredList(Map<String, Object> query);

    List<Map<String,Object>> queryOrderInfo(Long id);

    List<Map<String,Object>> selectTransList(Query query);

    int selectTransListTotal(Query query);

    int updateTradeStatus(@Param("incomePayDetailId") Long incomePayDetailId, @Param("tradeStatus") int tradeStatus);
    
    Map<String, Object> selectStoreStats(Map<String, Object> query);
    
    List<Map<String,Object>> selectStoreStatsByCard(Map<String, Object> query);
    List<Map<String,Object>> selectStoreStatsCountByCard(Map<String, Object> query);

    /** 是否存在有效购卡记录（收支明细口径，含同手机号/openid） */
    int countValidCardPurchaseByUserId(@Param("userId") Long userId);

}
