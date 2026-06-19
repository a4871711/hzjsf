package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.PrivateClassOrderMapper;
import com.dlc.modules.api.service.PrivateClassOrderService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-09-29 20:51
 */
@Service
@Transactional
public class PrivateClassOrderServiceImpl implements PrivateClassOrderService {

    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;

    @Override
    public List<Map<String, Object>> queryOrderList(Query query) {
     return privateClassOrderMapper.queryOrderList(query);
    }

    @Override
    public int queryOrderListTotal(Query query) {

        return privateClassOrderMapper.queryOrderListTotal(query);
    }

    @Override
    public Map<String, Object> queryPrivateClassDetail(Long privateClassOrderId) {
        Map<String, Object> pMap = privateClassOrderMapper.queryPrivateOrderDetailByOrderId(privateClassOrderId);
        if(null != pMap){
            if(pMap.get("nickname") != null){
                pMap.put("nickname", EmojiParser.parseToUnicode((String) pMap.get("nickname")));
            }
        }
        return pMap;
    }
}
