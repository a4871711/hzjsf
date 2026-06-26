package com.dlc.modules.api.service;


import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.Coach;
import com.dlc.modules.api.entity.CoachWallet;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map; /**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
public interface CoachService {
    List<Map<String,Object>> queryCoachList(Map<String, Object> params);

    int queryCoachTotal(Query query);

    Map<String,Object> selectCoachInfo(Map<String,Object> params);

    int saveCoachInfo(Coach coach);

    List<Map<String,Object>> recommendCoach(Long storeId);

    List<Map<String,Object>> servePlaceList(Long coachId);

    Map<String, Object> queryCoachStatus(Long userId);

    Integer queryCoachGradeByCoachId(Long coachId);

    List<Map<String,Object>> queryAddClassByGrade(Integer grade);

    Double queryCoachAccountBalance(Long coachId);

    Double queryCoachTotalIncome(Long coachId);

    Double queryWithdrawCash(Long userId);

    List<Map<String, Object>> queryWithdrawCashList(Query query);

    int queryWithdrawCashListTotal(Query query);

    List<String> queryCoachStoreId(Long coachId);

    int updateCoachStore(Map<String, Object> map);

    Map<String,Object> queryExistShip(Map<String, Object> paramMap);

    int addCoachPlaceShip(Map<String, Object> paramMap);

    int queryTotal(Query query);

    Map<String,Object> queryAlipayCountInfo(Long coachId);

    int queryLowPriceFromDataMap();

    int updateCoachChWallet(Map<String, Object> params);

    int addWithdrawCashRecord(Long coachId, BigDecimal money, String alipay, String realName);

    BigDecimal queryCoachWallet(Long coachId);

    int updateCoachWalletMoney(BigDecimal newBalance,Long coachId);

    Map<String,Object> queryCoachInfo(Long coachId);

    int updateCoachInfo(Coach coach);

    int addCoachWallet(CoachWallet coachWallet);

    Map<String,Object> test();

    void updateCoachClassInfo(Coach coach);

    List<Long> queryExistStoreId(Long coachId, List<Long> sts);

    List<Map<String,Object>> recommendStoreCoach(Long storeId);

    Map<String,Object> selectStoreCoachInfo(Map<String, Object> params);

    CoachWallet queryCoachWalletEntity(Long coachId);

	List<Map<String, Object>> queryStoreCoachList(Map<String, Object> params);

	int queryStoreCoachTotal(Query query);
}
