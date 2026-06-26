package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.Coach;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface CoachMapper {
    int deleteByPrimaryKey(Long coachId);

    int insert(Coach record);

    int insertSelective(Coach record);

    Coach selectByPrimaryKey(Long coachId);

    int updateByPrimaryKeySelective(Coach record);

    int updateByPrimaryKey(Coach record);

    List<Map<String,Object>> queryCoachList(Map<String, Object> params);

    int queryCoachTotal(Query query);

    Map<String,Object> queryCoachInfo(Long userId);

    Integer queryCoachGradeByCoachId(Long coachId);

    List<Map<String,Object>> queryAddClassByGrade(Integer grade);

    List<Map<String,Object>> recommendCoach(Long storeId);

    List<String> queryCoachStoreIds(Long coachId);

    int updateCoachStore(Map<String, Object> map);

    Map<String,Object> queryExistShip(Map<String, Object> paramMap);

    Integer sumCoachEvleve(@Param("coachId") Long coachId);

    int queryTotal(Query query);

    Map<String,Object> queryAlipayCountInfo(Long coachId);

    int queryLowPriceFromDataMap();

    Map<String,Object> queryCoachInfoByCoachId(Long coachId);

    Map<String,Object> selectCoachByCoachId(Map<String,Object> params);

    Integer countCoachEvleve(Long coachId);

    int updateCoachInfo(@Param("userId") Long userId, @Param("headImgUrl") String headImgUrl);

    List<Long> queryExistStoreId(@Param("coachId") Long coachId, @Param("storeIds") List<Long> storeIds);

    String queryIntroduceByUId(Long coachId);
}