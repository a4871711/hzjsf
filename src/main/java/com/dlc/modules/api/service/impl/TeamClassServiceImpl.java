package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.TeamClassMapper;
import com.dlc.modules.api.entity.TeamClass;
import com.dlc.modules.api.service.TeamClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@Service
@Transactional
public class TeamClassServiceImpl implements TeamClassService{

    @Autowired
    private TeamClassMapper teamClassMapper;
    @Override
    public TeamClass selectTeamClassInfo(Long id) {
        TeamClass teamClass = teamClassMapper.selectByPrimaryKey(id);
        return teamClass;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  门店活动列表
     */
    @Override
    public List<Map<String, Object>> selectStoreTeamClassList(Map<String,Object> params) throws ParseException {
        List<Map<String,Object>> list = teamClassMapper.selectStoreTeamClassList(params);
        return list;
    }

    @Override
    public int queryTotal(Map<String,Object> params) {
        return teamClassMapper.queryTotal(params);
    }

    @Override
    public Map<String, Object> selectTeamClassInfoMap(Long id) {
        return teamClassMapper.selectByPrimaryKeyMap(id);
    }
}
