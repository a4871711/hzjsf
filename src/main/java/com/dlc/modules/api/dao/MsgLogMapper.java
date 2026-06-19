package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.MsgLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Author jiangkang
 * @Date 2022/9/6 15:11
 */
@Mapper
@Repository
public interface MsgLogMapper {

    int insert(MsgLog log);

}
