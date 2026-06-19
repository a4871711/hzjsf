package com.dlc.modules.api.dao;

import com.dlc.modules.api.entity.UserWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;


@Mapper
@Repository
public interface UserWalletMapper {
    int deleteByPrimaryKey(Long userWalletId);

    int insert(UserWallet record);

    int insertSelective(UserWallet record);

    UserWallet selectByPrimaryKey(Long userWalletId);

    int updateByPrimaryKeySelective(UserWallet record);

    int updateByPrimaryKey(UserWallet record);

    int balancePay(@Param("userId") Long userId,@Param("realPayment") Integer realPayment);

    BigDecimal queryUserMoney(Long userId);

    /**
     * queryWalletByUserId
     * @param userId
     * @return
     */
    Map<String,Object> queryWalletByUserId(Long userId);

    /**
     * 查询个人提现设置
     * @param userId
     * @return
     */
    Map<String,Object> queryWalletAccount(Long userId);

    /**
     * 提现设置
     * @param updateMap
     * @return
     */
    int updateAccountByUserId(Map<String, Object> updateMap);

    /**
     * 账户的的余额进出账
     * @param userId
     * @param money
     * @return
     */
    int updateMyMoney(@Param("userId") Long userId, @Param("money") BigDecimal money);

    /*查询当前用户余额*/
    BigDecimal queryWalletMoney(Long userId);
    //查询用户钱包信息
    UserWallet selectWalletByUserId(Long userId);

    int updateUserWallet(@Param("money") BigDecimal money, @Param("userId") Long userId);
}