package com.dlc.modules.sys.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysStoreEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * lingkangming
 */
@Mapper
@Repository
public interface SysStoreDao extends BaseDao<SysStoreEntity> {
	
	List<SysStoreEntity> queryObject(@Param("value") String ids);
	List<SysStoreEntity> queryObjectByAddr(@Param("value") String ids);
    /**
     * 根据门店电话判断是否已存在
     */
    List<Map<String,Object>> queryStoreByCondition(Map<String, Object> map);

    void updateStoreAddId(Long storeId);
    //根据门店id查私教课订单
    List<Map<String,Object>> queryClassList(Query query);
    //根据门店id查私教课订单总数
    int queryClassTotal(Query query);
    //根据约课id查询约课信息
    Map<String,Object> queryArrClassByAcIdStatus(Long arrangeClassId);
    //查教练列表
    List<Map<String,Object>> queryCoachList(Query query);
    //查总数
    int queryCoachTotal(Query query);

    //int updateGrade(@Param("cpsId") Long cpsId,@Param("grade") Integer grade);

    int updateGrade(Map<String, Object> params);

    Long queryStoreAddrId(Long storeId);
    //查询团体课
    List<Map<String,Object>> queryTeamClassList(Query query);

    int queryTeamClassTotal(Query query);
    //进行中 2
    int batchUpdateStatusT(List<Long> stuTeamClassId);
    //已完成 1
    int batchUpdateStatusO(Long[] stuTeamClassId);
    //查学员id和能量
    Map<String,Object> queryStuTeamClassShip(Long stuTeamClassId);

    int updateStoreAddIdByMap(Map<String, Object> upMap);
    //查询团体课学员列表
    List<Map<String,Object>> queryTeamClassStuList(Query query);

    int insertStore(Map<String,Object> map);

    BigDecimal getPercentMoney(@Param("money") BigDecimal money, @Param("grade") Integer grade);

    Double getCoachPercent(Integer grade);

    int deleteCoachPlace(Long storeId);

    int deleteCardOrder(Long storeId);

    int deleteStoreDevice(Long storeId);

    int updateUserDevice(Long storeId);

    int deleteTeamClass(Long storeId);

    int deleteTeamClassOrder(Long storeId);

    int deleteTeamClaasArr(Long storeId);

    int deleteStorePrivateArr(Long storeId);

    int deleteGroupAttion(Long storeId);

    int deleteGroupDy(Long storeId);

    int deleteDyPl(Long storeId);

    int deleteStoreGroup(Long storeId);

    int deleteStoreAddr(Long storeId);

    int deleteSysUser(Long storeId);

    int updateStoreStatus(Map<String, Object> map);

    int updateStoreAddr(Map<String, Object> map);

    int updateStoreGroup(Map<String, Object> upMap);

    int queryCoachGradeHas(Integer grade);

    int queryTeamClassStuTotal(Query query);
}
