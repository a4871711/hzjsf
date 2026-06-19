package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.CoachWallet;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;

@Repository
public interface CoachWalletMapper {
    int deleteByPrimaryKey(Long coachWalletId);

    int insert(CoachWallet record);

    int insertSelective(CoachWallet record);

    CoachWallet selectByPrimaryKey(Long coachWalletId);

    int updateByPrimaryKeySelective(CoachWallet record);

    int updateByPrimaryKey(CoachWallet record);

    Double queryCoachAccountBalance(Long coachId);

    int updateAlipayCount(Map<String, Object> params);

    //更新余额
    int updateCoachMoney(@Param("coachId") Long coachId, @Param("money") BigDecimal money);

    CoachWallet selectByCoachId(Long coachId);
}