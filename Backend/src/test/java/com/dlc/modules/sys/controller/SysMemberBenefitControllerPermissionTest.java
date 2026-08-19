package com.dlc.modules.sys.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertNull;

/** 批量权益操作不再要求单独的 update 权限。 */
public class SysMemberBenefitControllerPermissionTest {

    @Test
    public void batchOperationsShouldNotRequireSeparatePermission() throws Exception {
        Method adjustExpireDate = SysMemberBenefitController.class
                .getDeclaredMethod("batchAdjustExpireDate", java.util.Map.class);
        Method changeStore = SysMemberBenefitController.class
                .getDeclaredMethod("batchChangeStore", java.util.Map.class);
        Method changeCoach = SysMemberBenefitController.class
                .getDeclaredMethod("batchChangeCoach", java.util.Map.class);
        Method coachOptions = SysMemberBenefitController.class
                .getDeclaredMethod("coachOptions", String.class);

        assertNull(adjustExpireDate.getAnnotation(RequiresPermissions.class));
        assertNull(changeStore.getAnnotation(RequiresPermissions.class));
        assertNull(changeCoach.getAnnotation(RequiresPermissions.class));
        assertNull(coachOptions.getAnnotation(RequiresPermissions.class));
    }
}
