package com.dlc.modules.api.service;

import com.dlc.modules.api.vo.UserInfoVo;

import java.math.BigDecimal;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/18/018
 */
public interface IncomePayDetailService {
    int saveIncomePayDetail(String orderNo, String transaction_id, BigDecimal wallet, Integer payType);

    /**
     * VIP转让过户生效时补记对方(受让人)userId(详细技术设计 附录B.2/B.6):
     * 服务费已在 payFeeCallback 记账一次,过户不重复插入流水,仅按 orderNo 补 anotherId,天然幂等。
     */
    int updateAnotherId(String orderNo, Long anotherId);

    /**
     * VIP转让服务费退款流水(驳回/拒绝/超时/20撤回退费时写)。
     * 记账口径对齐全仓库商城退款:payType=9(退款)、money 存正数(正负号由前台 moneyType CASE 决定,
     * payType=9 记'+')、tradeType=微信、tradeStatus=3(已完成)。
     * 注:设计附录B.4 原写 payType=8,但 DB constant 表 8=积分兑换、9=退款,故改用 9(见记账口径调查)。
     * @param orderNo    原服务费订单号(= service_fee_order_no)
     * @param money      退款金额(正数,元)
     * @param userId     收退款人(转让人 fromUserId)
     * @param anotherId  对方(受让人 toUserId),留痕用
     */
    int saveTransferRefund(String orderNo, BigDecimal money, Long userId, Long anotherId);
}
