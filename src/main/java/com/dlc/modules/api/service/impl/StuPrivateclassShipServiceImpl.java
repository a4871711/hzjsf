package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.StuPrivateclassShipMapper;
import com.dlc.modules.api.service.StuPrivateclassShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-20 14:04
 */
@Service
@Transactional
public class StuPrivateclassShipServiceImpl implements StuPrivateclassShipService {

    @Autowired
    private StuPrivateclassShipMapper stuPrivateclassShipMapper;

    @Override
    public List<Map<String, Object>> queryStuPrivateClassByCoachId(Long coachId) {
        return stuPrivateclassShipMapper.queryStuPrivateClassByCoachId(coachId);
    }
}
