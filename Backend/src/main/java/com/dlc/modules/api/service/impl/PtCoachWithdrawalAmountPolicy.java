package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 私教提现金额规则。
 * 金额计算单独抽出，保证申请冻结和审核扣减使用同一套边界判断。
 */
public final class PtCoachWithdrawalAmountPolicy {
    private static final BigDecimal ZERO = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

    private PtCoachWithdrawalAmountPolicy() {
    }

    public static BigDecimal availableAmount(BigDecimal incomeTotal,
                                             BigDecimal settledAmount,
                                             BigDecimal frozenAmount) {
        requireNonNegative(incomeTotal, "累计收入");
        requireNonNegative(settledAmount, "已结算金额");
        requireNonNegative(frozenAmount, "冻结金额");
        BigDecimal available = money(incomeTotal)
                .subtract(money(settledAmount))
                .subtract(money(frozenAmount));
        if (available.compareTo(ZERO) < 0) {
            throw new RRException("可提现金额计算异常");
        }
        return available.setScale(2, RoundingMode.HALF_UP);
    }

    public static void validateRequestedAmount(BigDecimal requestedAmount, BigDecimal availableAmount) {
        requirePositive(requestedAmount, "提现金额");
        requireNonNegative(availableAmount, "可提现金额");
        if (money(requestedAmount).compareTo(money(availableAmount)) > 0) {
            throw new RRException("提现金额不能超过可提现金额");
        }
    }

    public static void validateApprovalAmount(BigDecimal availableBefore,
                                              BigDecimal currentFrozen,
                                              BigDecimal actualSettlementAmount) {
        requireNonNegative(availableBefore, "审核前可提现金额");
        requireNonNegative(currentFrozen, "当前冻结金额");
        requirePositive(actualSettlementAmount, "实际结算金额");
        BigDecimal max = money(availableBefore).add(money(currentFrozen));
        if (money(actualSettlementAmount).compareTo(max) > 0) {
            throw new RRException("实际结算金额超过可用余额");
        }
    }

    public static void requirePositive(BigDecimal value, String fieldName) {
        if (value == null || money(value).compareTo(ZERO) <= 0) {
            throw new RRException(fieldName + "必须大于0");
        }
        if (value.stripTrailingZeros().scale() > 2) {
            throw new RRException(fieldName + "最多保留两位小数");
        }
    }

    public static void requireNonNegative(BigDecimal value, String fieldName) {
        if (value == null || money(value).compareTo(ZERO) < 0) {
            throw new RRException(fieldName + "不能为负数或空值");
        }
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
