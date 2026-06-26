package com.dlc.modules.api.service;


import com.dlc.common.utils.R;
import com.dlc.modules.api.vo.UserInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
public interface StoreAddressService {

    List<Map<String,Object>> storeAddressList(Map<String, Object> params);

    int queryStoreAddressTotal(Map<String, Object> params);

    List<Map<String,Object>> pcStoreAdInfo(Map<String, Object> params);

	List<Map<String, Object>> getMyStore(UserInfoVo userVo, Map<String, Object> params);
}
