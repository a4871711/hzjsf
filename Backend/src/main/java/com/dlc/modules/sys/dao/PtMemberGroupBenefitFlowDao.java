package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtMemberGroupBenefitFlowEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 会员附赠团课权益流水 Dao（本域只读，供权益详情抽屉展示，不作为独立分页页面）
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtMemberGroupBenefitFlowDao {

    List<PtMemberGroupBenefitFlowEntity> queryByBenefitId(Long benefitId);
}
