package com.dlc.modules.sys.dao;


import com.dlc.modules.sys.entity.FitCardEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 健身卡(会员卡)
 *
 * @author daibenting
 * @email
 * @date 2018-09-10 10:24:28
 */
@Mapper
@Repository
public interface FitCardDao extends BaseDao<FitCardEntity> {
    Integer fitCardCount(long cardId);

    int updateOnOffCard(@Param("fitCardId") Long fitCardId,
                        @Param("status") Integer status);

    /**
     * 统计给定ID中,存在且为"权益卡性质"(cardNature=1)的 fit_card 数量。
     * 用于校验 VIP 权益卡"可绑定会员卡"配置,防止绕过前端选到普通卡性质/不存在的记录。
     */
    int countBenefitNatureByIds(List<Long> fitCardIds);
}
