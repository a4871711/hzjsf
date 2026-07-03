package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtProduct;

import java.util.List;

/**
 * 私教商品会员端只读浏览 Service
 *
 * @author claude
 */
public interface PtProductApiService {

    PtProduct queryObject(Long id);

    List<PtProduct> queryList(Query query);

    int queryTotal(Query query);
}
