package com.dlc.modules.api.service;

import com.dlc.modules.api.entity.AboutUs;
import com.dlc.modules.api.entity.AboutUsEntity;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-24 11:14
 */
public interface AboutUsService {
    AboutUs queryObject(Integer id);

    Map<String, Object> queryList();

    int queryTotal(Map<String, Object> map);

    void save(AboutUs aboutUs);

    void update(AboutUs aboutUs);

    void delete(Integer id);

    void deleteBatch(Integer[] ids);

    Map<String, Object> downVersion(Integer type);

    String queryBrandLogo();

    Map<String, Object> selectDownloadUrl();

    Map<String, Object> queryProtocolByType(int type);

    Map<String, Object> queryOpenDoor();

	List<Map<String, Object>> listProtocol();
}
