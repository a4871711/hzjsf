package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.SystemMsgEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 系统消息记录表
 * 
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-11 16:01:16
 */
@Mapper
@Repository
public interface SystemMsgDao {

    int queryTotal(Long userId);

    int save(SystemMsgEntity systemMsgEntity);

    int update(SystemMsgEntity systemMsgEntity);

    int delete(Long sysMsgId);

    int deleteBatch(List<Long> sysMsgId);

    /**
     * 查詢個人系統消息
     * @param userId
     * @return
     */
    List<Map<String, Object>> querySysMsgByUserId(Long userId);

    int updateReadFlag(Long userId);
    //查询未读数
    int queryUnReadNum(Long userId);
}
