package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtProduct;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教商品会员端只读浏览 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductApiDao {

    PtProduct queryObject(Long id);

    List<PtProduct> queryList(Query query);

    int queryTotal(Query query);

    List<Map<String, Object>> queryCategories();

    List<Map<String, Object>> queryStores(Long productId);
}
