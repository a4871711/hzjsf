package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.dao.SysProtocolMapper;
import com.dlc.modules.sys.entity.Protocol;
import com.dlc.modules.sys.service.SysProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sysProtocolService")
public class SysProtocolServiceImpl implements SysProtocolService {
    @Autowired
    private SysProtocolMapper protocolMapper;
    @Override
    public List<Protocol> queryList(Query query) {
        return protocolMapper.queryList(query);
    }

    @Override
    public int queryTotal(Query query) {
        return protocolMapper.queryTotal(query);
    }

    @Override
    public Protocol queryObject(Long pId) {
        return protocolMapper.selectByPrimaryKey(pId);
    }

    @Override
    public int queryIfExist(Integer type) {
        return protocolMapper.selectIfExist(type);
    }

    @Override
    public int save(Protocol protocol) {
        return protocolMapper.insertSelective(protocol);
    }

    @Override
    public int update(Protocol protocol) {
        return protocolMapper.updateByPrimaryKeySelective(protocol);
    }

    @Override
    public int deleteBatch(Long[] pIds) {
        return protocolMapper.deleteBatch(pIds);
    }
}
