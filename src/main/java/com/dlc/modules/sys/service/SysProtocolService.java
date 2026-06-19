package com.dlc.modules.sys.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.Protocol;

import java.util.List;

public interface SysProtocolService {
    List<Protocol> queryList(Query query);

    int queryTotal(Query query);

    Protocol queryObject(Long pId);

    int queryIfExist(Integer type);

    int save(Protocol protocol);

    int update(Protocol protocol);

    int deleteBatch(Long[] pIds);
}
