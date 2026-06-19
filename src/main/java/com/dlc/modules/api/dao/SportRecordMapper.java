package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.SportActive;
import com.dlc.modules.api.entity.SportRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SportRecordMapper {
    int deleteByPrimaryKey(Long srId);

    int insert(SportRecord record);

    int insertSelective(SportRecord record);

    SportRecord selectByPrimaryKey(Long srId);

    int updateByPrimaryKeySelective(SportRecord record);

    int updateByPrimaryKey(SportRecord record);

    Map<String,Object> querySportRecord(Long userId);

    Map<String,Object> querySportListByType(@Param("sportType") Integer sportType,@Param("userId") Long userId);

    List<Map<String,Object>> sportDataCount(SportActive sportActive);

    List<Map<String,Object>>  sportDataCountByWeek(SportActive sportActive);

    List<Map<String,Object>> sportDataCountByMooth(SportActive sportActive);

    List<Map<String,Object>> sportDataCountByYear(SportActive sportActive);

    int saveSportData(Map<String, Object> map);

    List<SportActive> sportDataCountALL(SportActive sportActive);

    List<SportActive> sportDataCountByWeekALL(SportActive sportActive);

    List<SportActive> sportDataCountByMoothALL(SportActive sportActive);

    List<SportActive> sportDataCountByYearALL(SportActive sportActive);

    //查询sport_active表个人总能量
    Integer queryUserTotalEnergy(Long userId);
    //登录活跃天数更新
    int updateActivity(Long userId);
    //更新个人私教课数据
    int updatePrivateClassInfo(Map<String, Object> arrMap);
    //更新个人团课数据
    int updateTeamClassInfo(Map<String, Object> updateMap);
    //获取每个用户的sum(私教课和团体课)的能量
    List<Map<String,Object>> queryEnergy();
    //获取团课当天能量总和
    BigDecimal querySportRecordTClass(Long userId);
    ////获取私教课当天能量总和
    BigDecimal querySportRecordPClass(Long userId);
}