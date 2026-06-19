package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.GymEngineEntity;
import com.dlc.modules.sys.entity.SysTopicEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 话题表
 *
 * @author daibenting
 * @email 
 * @date 2018-09-15 09:27:07
 */
public interface SysTopicService  {
    SysTopicEntity queryObject(Long topicId);


    List<SysTopicEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysTopicEntity sysTopicEntity);

    void update(SysTopicEntity sysTopicEntity);

    void deleteBatch(Long[] topicIds);
}

