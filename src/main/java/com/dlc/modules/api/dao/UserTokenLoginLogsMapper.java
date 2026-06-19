package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author jiangkang
 * @Date 2022/10/9 18:42
 */

@Mapper
@Repository
public interface UserTokenLoginLogsMapper {

    int insert(Map log);

    //白名单登录记录
    HashMap getWhiteLoginLog(Map query);

    List queryRiskList(Query query);

    int queryRiskTotal(Query query);

    int checkRisk(@Param("id") int id, @Param("check") int check);

}
