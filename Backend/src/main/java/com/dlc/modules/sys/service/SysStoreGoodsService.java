package com.dlc.modules.sys.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.sys.entity.SysStoreGoodsEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 门店商品表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-01-14 09:04:26
 */
public interface SysStoreGoodsService {

    SysStoreGoodsEntity queryObject(Long id);

    List<SysStoreGoodsEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(SysStoreGoodsEntity sysStoreGoodsEntity);

    void update(SysStoreGoodsEntity sysStoreGoodsEntity);

    void deleteBatch(Long[] ids);

    Integer queryCountByBarCode(Map<String,Object> params);

    SysStoreGoodsEntity queryGoodsByBarCode(String barCode);

    R goodsAccounts(Map<String, Object> params, UserInfo userInfo, BigDecimal userWallet);

    int queryIsForbiddenStatus(Long userId, String wristId);
}

