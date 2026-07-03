package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductCoachRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品指定教练关联 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductCoachRelDao extends BaseDao<PtProductCoachRelEntity> {

    int deleteByProductId(Long productId);

    List<Long> queryCoachIds(Long productId);
}
