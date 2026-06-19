package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.DynamicTopicShipEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 动态话题关系表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-19 15:06:17
 */
@Mapper
@Repository
public interface DynamicTopicShipDao extends BaseDao<DynamicTopicShipEntity> {
	List<Map<String, Object>> queryMyTopicList(@Param("dynamicId") Long dynamicId);
    /**查询详情话题下的动态*/
    List<Map<String, Object>> queryTopicDynamic(Map<String,Object> queryMap);

    int queryTopicDynamicCount(Map<String,Object> queryMap);

    int saveByMap(Map<String, Object> saveMap);

    /**
     *删除动态话题关系
     */
    int deleteByDynamicId(Long dynamicId);
}
