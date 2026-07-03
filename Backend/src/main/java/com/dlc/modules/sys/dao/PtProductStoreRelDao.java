package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductStoreRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品适用门店关联 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductStoreRelDao extends BaseDao<PtProductStoreRelEntity> {

    int deleteByProductId(Long productId);

    List<Long> queryStoreIds(Long productId);
}
