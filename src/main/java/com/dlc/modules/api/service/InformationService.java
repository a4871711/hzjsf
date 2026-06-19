package com.dlc.modules.api.service;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/10/10/010
 */
public interface InformationService {

    List<Map<String,Object>> queryInformationList(Map<String, Object> params);

    int queryTotal(Map<String,Object> params);

    Map<String,Object> queryInformationInfo(Long id);

    Map<String,Object> selectSowingMap(Integer infType);

    List<Map<String,Object>> querySliveList();
}
