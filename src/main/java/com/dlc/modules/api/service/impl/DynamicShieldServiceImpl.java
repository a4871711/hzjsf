package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.DynamicShieldMapper;
import com.dlc.modules.api.entity.DynamicShield;
import com.dlc.modules.api.service.DynamicShieldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DynamicShieldServiceImpl implements DynamicShieldService {

    @Autowired
    private DynamicShieldMapper dynamicShieldMapper;

    @Override
    public int saveShieldDynamic(DynamicShield dynamicShield) {
        return dynamicShieldMapper.insertSelective(dynamicShield);
    }
}
