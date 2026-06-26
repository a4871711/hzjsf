package com.dlc.modules.api.service;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-20 14:04
 */
public interface StuPrivateclassShipService {
    List<Map<String,Object>> queryStuPrivateClassByCoachId(Long coachId);
}
