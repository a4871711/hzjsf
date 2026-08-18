package com.dlc.modules.sys.controller;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/** 私教购买记录导出字段和全量导出参数回归测试。 */
public class SysPrivateOrderControllerTest {

    @Test
    public void shouldRemovePaginationParamsBeforeExport() {
        Map<String, Object> params = new HashMap<>();
        params.put("page", 3);
        params.put("limit", 10);
        params.put("offset", 20);
        params.put("sidx", "created_at");
        params.put("order", "desc");
        params.put("orderNo", "PT202608180001");

        SysPrivateOrderController.prepareExportParams(params);

        assertFalse(params.containsKey("page"));
        assertFalse(params.containsKey("limit"));
        assertFalse(params.containsKey("offset"));
        assertFalse(params.containsKey("sidx"));
        assertFalse(params.containsKey("order"));
        assertTrue(params.containsKey("orderNo"));
    }

    @Test
    public void shouldBuildReadableExportRow() {
        Map<String, Object> row = new HashMap<>();
        row.put("orderNo", "PT202608180001");
        row.put("memberName", "张三");
        row.put("memberMobile", "13800000000");
        row.put("productName", "私教体验课");
        row.put("productTypeName", "私教商品");
        row.put("serviceType", 1);
        row.put("storeName", "城市风景店");
        row.put("marketingType", 0);
        row.put("payMethod", 1);
        row.put("originalAmount", "3000.00");
        row.put("discountAmount", "100.00");
        row.put("payableAmount", "2900.00");
        row.put("paidAmount", "2900.00");
        row.put("refundAmount", "0.00");
        row.put("benefitId", 1L);
        row.put("remainingLessons", 9);
        row.put("totalLessons", 10);
        row.put("expireAt", "2027-08-18 23:59:59");
        row.put("orderStatus", 2);
        row.put("createdAt", "2026-08-18 10:00:00");
        row.put("paidAt", "2026-08-18 10:01:00");
        row.put("settledAt", "2026-08-18 10:02:00");
        row.put("refundAt", null);

        String[][] values = SysPrivateOrderController.buildExportValues(java.util.Collections.singletonList(row));

        assertArrayEquals(new String[] {
                "PT202608180001", "张三", "13800000000", "私教体验课", "私教商品", "一对一",
                "城市风景店", "普通", "微信", "3000.00", "100.00", "2900.00", "2900.00", "0.00",
                "9/10", "2027-08-18 23:59:59", "已结清", "2026-08-18 10:00:00",
                "2026-08-18 10:01:00", "2026-08-18 10:02:00", ""
        }, values[0]);
    }
}
