package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCoachFeeRuleDao;
import com.dlc.modules.sys.dao.PtCoachMonthlyCommissionRuleDao;
import com.dlc.modules.sys.entity.PtCoachFeeRuleEntity;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.Assert.fail;

/** 普通提成规则与包月课程隔离的回归测试。 */
public class SysCoachFeeRuleServiceImplTest {

    @Test
    public void shouldRejectMonthlyProductForOrdinaryCommissionRule() throws Exception {
        SysCoachFeeRuleServiceImpl service = serviceWithMonthlyProductCount(1);
        PtCoachFeeRuleEntity entity = ordinaryRule(100L);

        try {
            service.save(entity);
            fail("包月商品不能保存到普通提成规则");
        } catch (RRException expected) {
            // 预期由后端业务校验明确拒绝。
        }
    }

    @Test
    public void shouldKeepOrdinaryProductAvailableForOrdinaryCommissionRule() throws Exception {
        SysCoachFeeRuleServiceImpl service = serviceWithMonthlyProductCount(0);

        service.save(ordinaryRule(200L));
    }

    private SysCoachFeeRuleServiceImpl serviceWithMonthlyProductCount(final int count) throws Exception {
        SysCoachFeeRuleServiceImpl service = new SysCoachFeeRuleServiceImpl();
        setField(service, "ptCoachFeeRuleDao", proxy(PtCoachFeeRuleDao.class, count));
        setField(service, "ptCoachMonthlyCommissionRuleDao", proxy(PtCoachMonthlyCommissionRuleDao.class, count));
        return service;
    }

    private PtCoachFeeRuleEntity ordinaryRule(Long productId) {
        PtCoachFeeRuleEntity entity = new PtCoachFeeRuleEntity();
        entity.setRuleName("普通规则");
        entity.setCoachId(1L);
        entity.setProductIds(Collections.singletonList(productId));
        entity.setRuleType(1);
        entity.setCommissionRate(new BigDecimal("20"));
        entity.setStatus(1);
        return entity;
    }

    @SuppressWarnings("unchecked")
    private <T> T proxy(Class<T> type, final int monthlyProductCount) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("countMonthlyProduct".equals(method.getName())) {
                    return monthlyProductCount;
                }
                if ("lockCoachForUpdate".equals(method.getName())) {
                    return 1L;
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
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type}, handler);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
