package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductBenefitPriceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 私教商品权益价关系 Dao。 */
@Mapper
@Repository
public interface PtProductBenefitPriceDao {

    List<PtProductBenefitPriceEntity> queryByProductId(@Param("productId") Long productId);

    int save(PtProductBenefitPriceEntity entity);

    int deleteByProductId(@Param("productId") Long productId);
}
