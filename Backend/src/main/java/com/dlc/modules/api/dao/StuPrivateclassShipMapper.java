package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.StuPrivateclassShip;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface StuPrivateclassShipMapper {
    int deleteByPrimaryKey(Long spsId);

    int insert(StuPrivateclassShip record);

    int insertSelective(StuPrivateclassShip record);

    StuPrivateclassShip selectByPrimaryKey(Long spsId);

    StuPrivateclassShip selectByKey(Long spsId);

    int updateByPrimaryKeySelective(StuPrivateclassShip record);

    int updateByPrimaryKey(StuPrivateclassShip record);

    /**
     * 查询我的私教课
     * @param parms
     * @return
     */
    List<Map<String, Object>> queryPrivateClassByUserId(Map<String,Object> parms);

    List<Map<String,Object>> queryStuPrivateClassByCoachId(Long coachId);

    int queryPrivateClassCount(Query query);
    //可约节数-1
    int updateAskNumber(Map<String, Object> params);
    //退课+1
    int updateAskNumberByAcId(Long arrangeClassId);
    //查询已过期且未上完课的课程
    List<Map<String,Object>> queryClassByValidate();
    //更新状态int
    int updatSpsStatus(@Param("spsId") Long spsId, @Param("classStatus") Integer classStatus);
}