package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.SysMemberBenefitDao;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/** 会员私教权益后台批量调整的业务校验测试。 */
public class SysMemberBenefitServiceImplTest {

    @Test
    public void shouldConvertIncreaseAndDecreaseToSignedOffset() throws Exception {
        Method method = findMethod("toSignedOffset", String.class, Integer.class);
        SysMemberBenefitServiceImpl service = serviceWithDao(0, 0, 0);

        assertEquals(Integer.valueOf(30), method.invoke(service, "increase", 30));
        assertEquals(Integer.valueOf(-7), method.invoke(service, "decrease", 7));

        try {
            method.invoke(service, "increase", 0);
            fail("增加 0 天必须拒绝");
        } catch (InvocationTargetException expected) {
            assertTrue(expected.getCause() instanceof RRException);
        }
    }

    @Test
    public void shouldRejectExpireBatchWhenSomeSelectedBenefitsAreOutOfScope() throws Exception {
        SysMemberBenefitServiceImpl service = serviceWithDao(1, 2, 1);

        try {
            invoke(service, "batchAdjustExpireDate",
                    Arrays.asList(10L, 11L), "increase", Integer.valueOf(30), "100,101");
            fail("选中权益不完整时必须整批拒绝");
        } catch (InvocationTargetException expected) {
            assertTrue(expected.getCause() instanceof RRException);
            assertTrue(expected.getCause().getMessage().contains("不存在、已退款或不在门店权限范围"));
        }
    }

    @Test
    public void shouldRejectStoreBatchWhenTargetStoreIsOutOfScope() throws Exception {
        SysMemberBenefitServiceImpl service = serviceWithDao(2, 2, 0);

        try {
            invoke(service, "batchChangeStore",
                    Arrays.asList(10L, 11L), 202L, "100,101");
            fail("目标门店不在权限范围内时必须拒绝");
        } catch (InvocationTargetException expected) {
            assertTrue(expected.getCause() instanceof RRException);
            assertTrue(expected.getCause().getMessage().contains("目标门店不存在或不在门店权限范围"));
        }
    }

    private SysMemberBenefitServiceImpl serviceWithDao(final int expireScopeCount,
                                                        final int storeScopeCount,
                                                        final int targetStoreScopeCount) throws Exception {
        SysMemberBenefitServiceImpl service = new SysMemberBenefitServiceImpl();
        setField(service, "sysMemberBenefitDao", proxy(SysMemberBenefitDao.class,
                expireScopeCount, storeScopeCount, targetStoreScopeCount));
        return service;
    }

    private Method findMethod(String name, Class<?>... parameterTypes) throws Exception {
        try {
            Method method = SysMemberBenefitServiceImpl.class.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            fail("生产代码尚未提供方法: " + name);
            return null;
        }
    }

    private Object invoke(Object target, String name, Object... args) throws Exception {
        Method method;
        if ("batchAdjustExpireDate".equals(name)) {
            method = findMethod(name, List.class, String.class, Integer.class, String.class);
        } else {
            method = findMethod(name, List.class, Long.class, String.class);
        }
        return method.invoke(target, args);
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, final int expireScopeCount,
                        final int storeScopeCount, final int targetStoreScopeCount) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("countAdjustableBenefits".equals(method.getName())) {
                    return expireScopeCount;
                }
                if ("countChangeableBenefits".equals(method.getName())) {
                    return storeScopeCount;
                }
                if ("countStoreAddressInScope".equals(method.getName())) {
                    return targetStoreScopeCount;
                }
                if (method.getReturnType() == int.class) {
                    return 0;
                }
                return null;
            }
        };
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
