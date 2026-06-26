package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.UserAddress;

import java.util.List;
import java.util.Map;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-22 11:09
 */
public interface AddressService {
    //Remind queryObject(Long id);

    List<UserAddress> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void delete(Long id);

    void deleteBatch(Long[] ids);

    /**
     *  新增或修改收货地址
     * @param address
     * @param userId
     * @return
     */
    R saveOrUpdate(UserAddress address, Long userId);

    //假删除收货地址
    void updateStatus(Long userAddressId);

    /**
     * 设置默认
     * @return
     */
    int updateIsdefault(Long userAddressId, Long userId);

    int deleteAddr(Long userAddressId);
}
