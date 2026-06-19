package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.AboutUsDao;
import com.dlc.modules.api.dao.ProtocolMapper;
import com.dlc.modules.api.entity.AboutUs;
import com.dlc.modules.api.entity.AboutUsEntity;
import com.dlc.modules.api.service.AboutUsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-24 11:15
 */
@Service
public class AboutUsServiceImpl implements AboutUsService {
    private static final int HashMap = 0;
	@Autowired
    private AboutUsDao aboutUsDao;
    @Autowired
    private ProtocolMapper protocolMapper;
    @Override
    public AboutUs queryObject(Integer id) {
        return null;
    }

    @Override
    public Map<String, Object> queryList() {
        Map<String, Object> resMap = aboutUsDao.queryListMap();
        if(null == resMap){
            //为了不返回空对象
            resMap = new HashMap<String, Object>();
        }
        return resMap;
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return 0;
    }

    @Override
    public void save(AboutUs aboutUs) {

    }

    @Override
    public void update(AboutUs aboutUs) {

    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void deleteBatch(Integer[] ids) {

    }

    @Override
    public Map<String, Object> downVersion(Integer type) {
        //1.IOS，2.Android
        Map<String, Object> downMap = aboutUsDao.selectByPrimaryKey(type);
        //为了不返回“”
        if(downMap == null) return new HashMap<String, Object>();
        return downMap;
    }

    @Override
    public String queryBrandLogo() {
        String brandLogo = aboutUsDao.queryListMap().get("brand_logo").toString();
        return brandLogo;
    }

    @Override
    public Map<String, Object> selectDownloadUrl() {
        return aboutUsDao.queryDownloadUrl();
    }

    @Override
    public Map<String, Object> queryProtocolByType(int type) {
        Map<String, Object> resMap = protocolMapper.queryProtocolByType(type);
        if(null == resMap){
            resMap = new HashMap<>();
        }
        return resMap;
    }

    @Override
    public List<Map<String, Object>> listProtocol() {
        List<Map<String, Object>> resMap = protocolMapper.queryProtocol();
        if(null == resMap){
            resMap = new ArrayList<>();
        }
        return resMap;
    }

    @Override
    public Map<String, Object> queryOpenDoor(){
        return aboutUsDao.queryOpenDoor();
    }
}
