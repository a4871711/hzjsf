package com.dlc.modules.sys.service.impl;

import com.dlc.modules.sys.dao.SysTopicDao;
import com.dlc.modules.sys.entity.SysTopicEntity;
import com.dlc.modules.sys.service.SysTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("topicService")
public class SysTopicServiceImpl implements SysTopicService {

    @Autowired
    private SysTopicDao sysTopicDao;


    @Override
    public SysTopicEntity queryObject(Long topicId) {
        return sysTopicDao.queryObject(topicId);
    }

    @Override
    public List<SysTopicEntity> queryList(Map<String, Object> map) {
        return sysTopicDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return sysTopicDao.queryTotal(map);
    }

    @Override
    public void save(SysTopicEntity sysTopicEntity) {
        sysTopicDao.save(sysTopicEntity);
    }

    @Override
    public void update(SysTopicEntity sysTopicEntity) {
        sysTopicDao.update(sysTopicEntity);
    }

    @Override
    public void deleteBatch(Long[] topicIds) {
        sysTopicDao.deleteBatch(topicIds);
    }
}
