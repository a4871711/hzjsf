package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtProductGroupBenefitEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
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

    /** 「指定团课商品」下拉项，读现有 team_class（本域不重建团课基础数据） */
    List<TeamClassEntity> queryTeamClassOptions();
}
