package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtProduct;

import java.util.List;
import java.util.Map;

/**
 * 私教商品会员端只读浏览 Service
 *
 * @author claude
 */
public interface PtProductApiService {

    PtProduct queryObject(Long id);

    List<PtProduct> queryList(Query query);

    int queryTotal(Query query);

    /** 查询用户端启用的商品分类选项 */
    List<Map<String, Object>> queryCategories();

    /** 查询商品可购买/可预约的门店地址列表 */
    List<Map<String, Object>> queryStores(Long productId);
}
