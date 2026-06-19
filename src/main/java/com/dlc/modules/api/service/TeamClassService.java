package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.TeamClass;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
public interface TeamClassService {
    TeamClass selectTeamClassInfo(Long id);

    List<Map<String,Object>> selectStoreTeamClassList(Map<String,Object> params) throws ParseException;

    int queryTotal(Map<String,Object> params);

    Map<String,Object> selectTeamClassInfoMap(Long id);
}
