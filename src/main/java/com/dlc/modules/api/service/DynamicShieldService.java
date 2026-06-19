package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.DynamicShield;

public interface DynamicShieldService {
    /**
     * 屏蔽-动态
     * @param dynamicShield
     * @return
     */
    int saveShieldDynamic(DynamicShield dynamicShield);
}
