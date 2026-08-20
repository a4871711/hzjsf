package com.dlc.modules.api.service.impl;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * 私教提现金额规则测试。
 * 先通过反射锁定生产规则入口，避免测试直接复制业务实现。
 */
public class PtCoachWithdrawalAmountPolicyTest {

    @Test
    public void frozenAmountMustReduceAvailableAmount() throws Exception {
        Object actual = invoke("availableAmount",
                new BigDecimal("100.00"),
                new BigDecimal("20.00"),
                new BigDecimal("30.00"));

        assertEquals(new BigDecimal("50.00"), actual);
    }

    @Test
    public void approvedAmountCanBeLowerThanCurrentFrozenAmount() throws Exception {
        invoke("validateApprovalAmount",
                new BigDecimal("40.00"),
                new BigDecimal("60.00"),
                new BigDecimal("50.00"));
    }

    @Test
    public void approvedAmountCannotExceedAvailablePlusCurrentFrozenAmount() throws Exception {
        try {
            invoke("validateApprovalAmount",
                    new BigDecimal("40.00"),
                    new BigDecimal("60.00"),
                    new BigDecimal("101.00"));
            fail("实际结算金额超过可用余额与当前冻结金额之和时必须报错");
        } catch (InvocationTargetException expected) {
            // 业务异常由生产规则明确抛出即可。
        }
    }

    private Object invoke(String methodName, BigDecimal... args) throws Exception {
        Class<?> policy;
        try {
            policy = Class.forName("com.dlc.modules.api.service.impl.PtCoachWithdrawalAmountPolicy");
        } catch (ClassNotFoundException e) {
            fail("生产代码尚未提供私教提现金额规则");
            return null;
        }
        Method method = findMethod(policy, methodName, args.length);
        if (method == null) {
            fail("生产代码尚未提供金额规则方法: " + methodName);
            return null;
        }
        return method.invoke(null, (Object[]) args);
    }

    private Method findMethod(Class<?> type, String name, int parameterCount) {
        for (Method method : type.getDeclaredMethods()) {
            if (method.getName().equals(name) && method.getParameterTypes().length == parameterCount) {
                method.setAccessible(true);
                return method;
            }
        }
        return null;
    }
}
