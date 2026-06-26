package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.UserSportDeviceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface UserSportDeviceRecordMapper {
    int deleteByPrimaryKey(Long id);

    int insert(UserSportDeviceRecord record);

    int insertSelective(UserSportDeviceRecord record);

    UserSportDeviceRecord selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UserSportDeviceRecord record);

    int updateByPrimaryKey(UserSportDeviceRecord record);

    int saveUserSportRecord(UserSportDeviceRecord userSportDeviceRecord);

    Map<String, Object> queryUserSportTotal(Query query);

    int queryTotalSportRecord(Query query);

    List<Map<String, Object>> queryUserRecordByUserId(Query query);

    BigDecimal queryEnergySumByUserId(Long userId);

    BigDecimal queryUnoxyKcalEnergySumByUserId(Long userId);

    BigDecimal queryOXYEnergySumByUserId(Long userId);
    //查询当天能量总和
    BigDecimal queryCurrEnergySumByUserId(Long userId);
    //查课程消耗能量
    Map<String, Object> queryClassTotal(Query query);

    int queryClassCount(Query query);
}