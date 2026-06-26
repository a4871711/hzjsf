package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.SysStoreGoodsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 门店商品表
 * 
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-01-14 09:04:26
 */
@Mapper
@Repository
public interface SysStoreGoodsDao extends BaseDao<SysStoreGoodsEntity> {

    Integer queryCountByBarCode(Map<String,Object> params);

    SysStoreGoodsEntity queryGoodsByBarCode(String barCode);

    int batchUpdateStoreGoodsNum(List<Map<String, Object>> goodsOrderList);
}
