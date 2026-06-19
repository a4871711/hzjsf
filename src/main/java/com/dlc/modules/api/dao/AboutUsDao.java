package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.AboutUs;
import com.dlc.modules.api.entity.AboutUsEntity;
import com.dlc.modules.sys.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 关于我们
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-28 19:39:01
 */
@Mapper
@Repository
public interface AboutUsDao extends BaseDao<AboutUsEntity> {
    Map<String, Object> queryListMap();

    Map<String, Object> selectByPrimaryKey(Integer type);

    Map<String,Object> queryDownloadUrl();

    Map<String,Object> queryOpenDoor();
}
