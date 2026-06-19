package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.DataMapMapper;
import com.dlc.modules.api.service.DataMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/17/017
 *
 */
@Service
@Transactional
public class DataMapServiceImpl implements DataMapService {

    @Autowired
    private DataMapMapper dataMapMapper;
    @Override
    public Map<String,Object> findBraceletPrice() {
        Map<String,Object> map = dataMapMapper.findBraceletPrice();
        //BigDecimal braceletPrice = BigDecimal.valueOf((Integer) map.get("price")).divide(new BigDecimal(100));
         map.put("price",map.get("price"));
         return map;
    }
}
