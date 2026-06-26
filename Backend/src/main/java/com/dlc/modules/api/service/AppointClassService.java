package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.AboutUs;

import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-24 11:14
 */
public interface AppointClassService {

    R appointClass(Map<String, Object> params, Long userId);

    R appointDel(Long spsId);

    R appointCancel(Long arrangeClassId);
}
