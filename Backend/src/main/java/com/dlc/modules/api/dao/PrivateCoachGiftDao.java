package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/** 自由教练赠课查询 Dao；课时写入仍由订单与权益 Dao 完成。 */
@Mapper
@Repository
public interface PrivateCoachGiftDao {

    List<Map<String, Object>> queryGiftableBenefits(@Param("userId") Long userId);

    Map<String, Object> queryMemberExact(@Param("keyword") String keyword);

    List<Map<String, Object>> queryGiftHistory(Query query);

    int countGiftHistory(Query query);
}
