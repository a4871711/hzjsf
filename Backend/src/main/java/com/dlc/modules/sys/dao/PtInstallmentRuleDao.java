package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtInstallmentRuleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 私教商品分期规则 Dao（1:1 挂商品，商品域建表与维护，资金域只读引用）
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtInstallmentRuleDao extends BaseDao<PtInstallmentRuleEntity> {

    PtInstallmentRuleEntity queryByProductId(Long productId);

    int deleteByProductId(Long productId);
}
