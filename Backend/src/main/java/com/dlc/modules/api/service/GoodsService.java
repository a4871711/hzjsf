package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.Goods;
import com.dlc.modules.api.entity.GoodsCategory;
import com.dlc.modules.api.entity.GoodsCategoryDetail;
import com.dlc.modules.api.entity.UserAddress;

import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/10 15:01
 */
public interface GoodsService {

    List<Map<String,Object>> latestGoodsList(Map<String, Object> query);

    int latestGoodsListCount(Query query);

    List<GoodsCategory> goodsCateList();

    List<Map<String,Object>> goodsCateDetail(Map<String, Object> query);

    int goodsCateDetailCount(Query query);

    int addGoodsCateDetail(GoodsCategoryDetail goodsCategoryDetail);

    int delGoodsCateDetail(String ids);

    Map<String,Object>   goodsDetails(Long goodsId);

    List<Map<String,Object>> queryCateDetailName(Long goodsCategoryId);

    Map<String,Object>  queryDefaultAddressByUserId(Long userId);
}
