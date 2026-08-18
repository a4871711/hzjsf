package com.dlc.modules.api.controller;

import com.dlc.modules.api.service.PrivateOrderService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/** 私教订单教练可选回归测试。 */
public class PrivateOrderControllerTest {

    @Test
    public void shouldCreateOrderWithoutCoach() throws Exception {
        final boolean[] called = new boolean[1];
        final Long[] selectedCoachId = new Long[1];

        PrivateOrderController controller = new PrivateOrderController() {
            @Override
            public UserInfoVo getUserVo(HttpServletRequest request) {
                return new UserInfoVo();
            }
        };
        setField(controller, "privateOrderService", serviceProxy(called, selectedCoachId));

        controller.create(1L, 2L, null, 1, 3, null, 0, null, null);

        assertTrue("未选择教练时仍应调用下单服务", called[0]);
        assertNull("未选择教练时应将 coachId 保持为 null", selectedCoachId[0]);
    }

    private PrivateOrderService serviceProxy(final boolean[] called, final Long[] selectedCoachId) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                if ("create".equals(method.getName())) {
                    called[0] = true;
                    selectedCoachId[0] = (Long) args[3];
                    return new HashMap<String, Object>();
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
        return (PrivateOrderService) Proxy.newProxyInstance(
                PrivateOrderService.class.getClassLoader(),
                new Class<?>[]{PrivateOrderService.class}, handler);
    }

    private void setField(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getSuperclass().getDeclaredField(name);
        field.setAccessible(true);
        field.set(target, value);
    }
}
