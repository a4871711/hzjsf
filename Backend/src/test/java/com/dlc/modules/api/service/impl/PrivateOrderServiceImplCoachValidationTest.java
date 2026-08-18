package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** 私教订单教练可选与已选校验回归测试。 */
public class PrivateOrderServiceImplCoachValidationTest {

    @Test
    public void shouldAllowOrderWithoutCoachAndSkipCoachLookup() throws Exception {
        int[] lookupCount = new int[1];
        PrivateOrderServiceImpl service = serviceWithBookableCount(0, lookupCount);

        invokeValidation(service, 1L, 2L, null);

        assertEquals("未选择教练时不应查询教练可预约关系", 0, lookupCount[0]);
    }

    @Test
    public void shouldRejectSelectedCoachThatCannotServeProduct() throws Exception {
        PrivateOrderServiceImpl service = serviceWithBookableCount(0, new int[1]);

        try {
            invokeValidation(service, 1L, 2L, 3L);
            fail("不可服务当前商品的教练必须被拒绝");
        } catch (RRException expected) {
            // 预期由后端业务校验明确拒绝。
        }
    }

    @Test
    public void shouldAllowSelectedBookableCoach() throws Exception {
        PrivateOrderServiceImpl service = serviceWithBookableCount(1, new int[1]);

        invokeValidation(service, 1L, 2L, 3L);
    }

    private void invokeValidation(PrivateOrderServiceImpl service, Long productId, Long storeId,
                                   Long coachId) throws Exception {
        final Method method;
        try {
            method = PrivateOrderServiceImpl.class.getDeclaredMethod(
                    "validateCoachSelection", Long.class, Long.class, Long.class);
        } catch (NoSuchMethodException e) {
            fail("应提供统一的教练选择校验逻辑");
            return;
        }
        method.setAccessible(true);
        try {
            method.invoke(service, productId, storeId, coachId);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof RRException) {
                throw (RRException) e.getCause();
            }
            throw e;
        }
    }

    private PrivateOrderServiceImpl serviceWithBookableCount(final int bookableCount,
                                                              final int[] lookupCount) throws Exception {
        PrivateOrderServiceImpl service = new PrivateOrderServiceImpl();
        setField(service, "coachApiDao", coachApiDaoProxy(bookableCount, lookupCount));
        return service;
    }

    @SuppressWarnings("unchecked")
    private CoachApiDao coachApiDaoProxy(final int bookableCount, final int[] lookupCount) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("countBookableCoach".equals(method.getName())) {
                    lookupCount[0]++;
                    return bookableCount;
                }
                if (method.getReturnType() == int.class) {
                    return 0;
                }
                if (method.getReturnType() == long.class) {
                    return 0L;
                }
                if (method.getReturnType() == boolean.class) {
                    return false;
                }
                return null;
            }
        };
        return (CoachApiDao) Proxy.newProxyInstance(
                CoachApiDao.class.getClassLoader(), new Class<?>[]{CoachApiDao.class}, handler);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
