package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.InformationMapper;
import com.dlc.modules.api.dao.SportRecordMapper;
import com.dlc.modules.api.service.InformationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/10/010
 */
@Service
@Transactional
public class InformationServiceImpl implements InformationService {
    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private InformationMapper informationMapper;

    @Override
    public List<Map<String, Object>> queryInformationList(Map<String, Object> params) {
        List<Map<String,Object>> list = informationMapper.queryInformationList(params);
        return list;
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return informationMapper.queryTotal(params);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  资讯详情
     */
    @Override
    public Map<String, Object> queryInformationInfo(Long id) {
        Map<String,Object> map = informationMapper.queryInformationInfo(id);
        return map;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  轮播图
     */
    @Override
    public Map<String, Object> selectSowingMap(Integer infType) {
        Map<String,Object> map = new HashMap<>();
        List<Map<String,Object>> list = informationMapper.selectSowingMap(infType);
        String[] imgs = new String[list.size()];
        for (int i = 0;i<list.size();i++) {
            imgs[i]=(String)list.get(i).get("infImgUrl");
        }
        map.put("infImgUrl",imgs);
        return map;
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  S-live 咨询说
     */
    @Override
    public List<Map<String, Object>> querySliveList() {
        return informationMapper.querySliveList();
    }
}
