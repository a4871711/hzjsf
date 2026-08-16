package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductGroupBenefitEntity;
import com.dlc.modules.sys.entity.PtProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品附赠团课权益规则 Dao（1:1 挂商品）
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductGroupBenefitDao extends BaseDao<PtProductGroupBenefitEntity> {

    PtProductGroupBenefitEntity queryByProductId(Long productId);

    int deleteByProductId(Long productId);

    /** 「指定团课商品」下拉项，读取未删除的一对多私教商品。 */
    List<PtProductEntity> queryGroupProductOptions();
}
