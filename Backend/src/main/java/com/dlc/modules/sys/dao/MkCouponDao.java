package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.MkCouponEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 优惠券主表 Dao（mk_coupon）
 *
 * @author claude
 */
@Mapper
@Repository
public interface MkCouponDao extends BaseDao<MkCouponEntity> {

    /** 上下架切换（独立语句，避免走全量 update 误清空可空列）；map: id/status/updatedBy */
    int changeStatus(Map<String, Object> map);
}
