package com.dlc.modules.sys.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ExportExcel;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPrivateOrderService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教购买记录(pt_private_order,第15步)。路径 /sys/privateOrder。
 * 后台不手工建/删订单:无 save/update/delete,仅列表/详情/退款。
 * 门店隔离:storeIds 为空(超管)不过滤;越权详情/退款一律按 404 处理,不暴露他店单据存在性。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/privateOrder")
public class SysPrivateOrderController extends AbstractController {

    @Autowired
    private SysPrivateOrderService sysPrivateOrderService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:privateOrder:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据隔离:非超管按所属门店过滤(超管 storeIds 为空则不过滤)
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPrivateOrderService.queryList(query);
        int total = sysPrivateOrderService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 按当前筛选条件导出全部购买记录，导出范围与列表接口保持一致。
     * 这里移除分页参数后复用同一套 Mapper 筛选，避免只导出当前页或绕过门店权限。
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:privateOrder:list")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        prepareExportParams(params);

        List<Map<String, Object>> list = sysPrivateOrderService.queryList(params);
        String[] titles = {"订单编号", "会员姓名", "手机号", "购买商品", "商品类型", "服务类型", "购买门店",
                "营销活动", "支付方式", "原价", "优惠合计", "应付金额", "实付金额", "累计退款",
                "课时(剩余/总)", "到期时间", "订单状态", "下单时间", "支付时间", "结清时间", "退款时间"};
        String fileName = "私教购买记录_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        XSSFWorkbook workbook = ExportExcel.getXSSFWorkbook("购买记录", titles, buildExportValues(list));
        writeWorkbook(response, fileName, workbook);
    }

    static void prepareExportParams(Map<String, Object> params) {
        params.remove("page");
        params.remove("limit");
        params.remove("offset");
        params.remove("sidx");
        params.remove("order");
    }

    static String[][] buildExportValues(List<Map<String, Object>> list) {
        String[][] values = new String[list.size()][21];
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> row = list.get(i);
            values[i][0] = valueOf(row.get("orderNo"));
            values[i][1] = valueOf(row.get("memberName"));
            values[i][2] = valueOf(row.get("memberMobile"));
            values[i][3] = valueOf(row.get("productName"));
            values[i][4] = valueOf(row.get("productTypeName"));
            values[i][5] = serviceTypeText(row.get("serviceType"));
            values[i][6] = valueOf(row.get("storeName"));
            values[i][7] = marketingText(row.get("marketingType"));
            values[i][8] = payMethodText(row.get("payMethod"));
            values[i][9] = valueOf(row.get("originalAmount"));
            values[i][10] = valueOf(row.get("discountAmount"));
            values[i][11] = valueOf(row.get("payableAmount"));
            values[i][12] = valueOf(row.get("paidAmount"));
            values[i][13] = valueOf(row.get("refundAmount"));
            values[i][14] = lessonsText(row);
            values[i][15] = valueOf(row.get("expireAt"));
            values[i][16] = orderStatusText(row.get("orderStatus"));
            values[i][17] = valueOf(row.get("createdAt"));
            values[i][18] = valueOf(row.get("paidAt"));
            values[i][19] = valueOf(row.get("settledAt"));
            values[i][20] = valueOf(row.get("refundAt"));
        }
        return values;
    }

    private static String lessonsText(Map<String, Object> row) {
        if (row.get("benefitId") == null) {
            return "";
        }
        return valueOf(row.get("remainingLessons")) + "/" + valueOf(row.get("totalLessons"));
    }

    private static String serviceTypeText(Object value) {
        Integer code = integerValue(value);
        if (Integer.valueOf(1).equals(code)) {
            return "一对一";
        }
        if (Integer.valueOf(2).equals(code)) {
            return "一对多";
        }
        return "";
    }

    private static String marketingText(Object value) {
        Integer code = integerValue(value);
        if (Integer.valueOf(0).equals(code)) {
            return "普通";
        }
        if (Integer.valueOf(1).equals(code)) {
            return "拼团";
        }
        if (Integer.valueOf(2).equals(code)) {
            return "秒杀";
        }
        return "";
    }

    private static String payMethodText(Object value) {
        Integer code = integerValue(value);
        if (Integer.valueOf(1).equals(code)) {
            return "微信";
        }
        if (Integer.valueOf(2).equals(code)) {
            return "支付宝";
        }
        if (Integer.valueOf(3).equals(code)) {
            return "储值";
        }
        if (Integer.valueOf(4).equals(code)) {
            return "分期";
        }
        if (Integer.valueOf(9).equals(code)) {
            return "其他";
        }
        return "";
    }

    private static String orderStatusText(Object value) {
        Integer code = integerValue(value);
        if (Integer.valueOf(0).equals(code)) {
            return "待支付";
        }
        if (Integer.valueOf(1).equals(code)) {
            return "首付已付";
        }
        if (Integer.valueOf(2).equals(code)) {
            return "已结清";
        }
        if (Integer.valueOf(3).equals(code)) {
            return "已取消";
        }
        if (Integer.valueOf(4).equals(code)) {
            return "已退款";
        }
        return "";
    }

    private static Integer integerValue(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String valueOf(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:privateOrder:info")
    public R info(@PathVariable("id") Long id) {
        Map<String, Object> entity = sysPrivateOrderService.queryDetail(id,
                ShiroUtils.getUserEntity().getStoreIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404,不区分两种情况
            return R.error(404, "订单不存在");
        }
        return R.ok().put("entity", entity);
    }

    /**
     * 退款:{orderId, refundAmount, refundLessons?, remark?}。
     * refundLessons 不传=按权益剩余课时全冲,传0=只退钱不冲课时;
     * 校验/渠道分支/负向流水在 api 侧 PrivateOrderService.refund 单事务内收口。
     */
    @RequestMapping("/refund")
    @RequiresPermissions("sys:privateOrder:refund")
    public R refund(@RequestBody Map<String, Object> params) {
        Object orderIdObj = params.get("orderId");
        Object amountObj = params.get("refundAmount");
        if (orderIdObj == null || amountObj == null || amountObj.toString().trim().isEmpty()) {
            return R.error("缺少参数:orderId/refundAmount");
        }
        Long orderId = Long.valueOf(orderIdObj.toString());
        BigDecimal refundAmount = new BigDecimal(amountObj.toString());
        Integer refundLessons = params.get("refundLessons") == null
                || params.get("refundLessons").toString().trim().isEmpty()
                ? null : Integer.valueOf(params.get("refundLessons").toString());
        String remark = params.get("remark") == null ? null : params.get("remark").toString();
        // 越权校验:订单必须在管辖门店范围内,否则按不存在处理
        if (!sysPrivateOrderService.existsInScope(orderId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "订单不存在");
        }
        sysPrivateOrderService.refund(orderId, refundAmount, refundLessons, remark, getUserId());
        return R.ok();
    }

    private void writeWorkbook(HttpServletResponse response, String fileName, XSSFWorkbook workbook) {
        try {
            String encodedName = URLEncoder.encode(fileName, "UTF-8").replace("+", "%20");
            String legacyName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + legacyName
                    + "\"; filename*=UTF-8''" + encodedName);
            try (OutputStream outputStream = response.getOutputStream()) {
                workbook.write(outputStream);
                outputStream.flush();
            }
        } catch (IOException e) {
            throw new RRException("导出私教购买记录失败");
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // 响应流已完成，关闭工作簿失败不覆盖原始导出结果。
            }
        }
    }
}
