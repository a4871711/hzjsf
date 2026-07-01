package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtMemberGroupBenefitEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 会员附赠团课权益 Dao（本域只读；写入分散在交易域/团课预约/运营域任务）
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtMemberGroupBenefitDao extends BaseDao<PtMemberGroupBenefitEntity> {
}
