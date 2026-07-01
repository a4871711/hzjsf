package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductGroupBenefitRelEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 私教商品附赠团课指定商品关联 Dao
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtProductGroupBenefitRelDao extends BaseDao<PtProductGroupBenefitRelEntity> {

    int deleteByBenefitId(Long benefitId);

    List<Long> queryGroupProductIds(Long benefitId);
}
