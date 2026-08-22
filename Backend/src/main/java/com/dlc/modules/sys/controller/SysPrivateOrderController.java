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
 * 私教购买记录后台接口。支持列表、详情、退款、手工建单和关联数据永久删除。
 * 门店隔离统一使用 store_address.storeAddrId；越权操作不暴露其他门店单据。
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
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
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
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
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
                ShiroUtils.getUserEntity().getStoreAddrIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404,不区分两种情况
            return R.error(404, "订单不存在");
        }
        return R.ok().put("entity", entity);
    }

    /** 后台建单会员候选：必须输入姓名、手机号或ID，避免一次返回全部会员。 */
    @RequestMapping("/memberOptions")
    @RequiresPermissions("sys:privateOrder:save")
    public R memberOptions(@RequestParam(value = "keyword", required = false) String keyword) {
        String value = trim(keyword);
        if (value == null) {
            return R.ok().put("list", java.util.Collections.emptyList());
        }
        return R.ok().put("list", sysPrivateOrderService.queryMemberOptions(
                value, ShiroUtils.getUserEntity().getStoreAddrIds()));
    }

    /** 后台建单商品候选：按所选购买门店校验商品适用范围。 */
    @RequestMapping("/productOptions")
    @RequiresPermissions("sys:privateOrder:save")
    public R productOptions(@RequestParam("storeId") Long storeId,
                            @RequestParam(value = "keyword", required = false) String keyword) {
        return R.ok().put("list", sysPrivateOrderService.queryProductOptions(
                trim(keyword), storeId, ShiroUtils.getUserEntity().getStoreAddrIds()));
    }

    /** 后台建单销售教练候选：只返回商品、门店均匹配的正常教练。 */
    @RequestMapping("/coachOptions")
    @RequiresPermissions("sys:privateOrder:save")
    public R coachOptions(@RequestParam("productId") Long productId,
                          @RequestParam("storeId") Long storeId,
                          @RequestParam(value = "keyword", required = false) String keyword) {
        return R.ok().put("list", sysPrivateOrderService.queryCoachOptions(
                trim(keyword), productId, storeId, ShiroUtils.getUserEntity().getStoreAddrIds()));
    }

    /** 后台手工新增已结清购买记录，并同步创建本地权益与交易关联数据。 */
    @RequestMapping("/save")
    @RequiresPermissions("sys:privateOrder:save")
    public R save(@RequestBody Map<String, Object> params) {
        String orderNo = sysPrivateOrderService.createManual(params, getUserId(),
                ShiroUtils.getUserEntity().getStoreAddrIds());
        return R.ok().put("orderNo", orderNo);
    }

    /**
     * 永久删除订单及可定位的本地关联数据。
     * 前端必须回传人工输入的订单号，Service 在订单行锁内再次严格比对。
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:privateOrder:delete")
    public R delete(@RequestBody Map<String, Object> params) {
        Long orderId = parseLong(params.get("orderId"));
        String confirmOrderNo = params.get("confirmOrderNo") == null
                ? null : params.get("confirmOrderNo").toString();
        if (orderId == null || confirmOrderNo == null || confirmOrderNo.trim().isEmpty()) {
            return R.error("缺少参数:orderId/confirmOrderNo");
        }
        sysPrivateOrderService.deleteCascade(orderId, confirmOrderNo, getUserId(),
                ShiroUtils.getUserEntity().getStoreAddrIds());
        return R.ok();
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
        if (!sysPrivateOrderService.existsInScope(orderId, ShiroUtils.getUserEntity().getStoreAddrIds())) {
            return R.error(404, "订单不存在");
        }
        sysPrivateOrderService.refund(orderId, refundAmount, refundLessons, remark, getUserId());
        return R.ok();
    }

    private static String trim(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private static Long parseLong(Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return null;
        }
        try {
            long result = Long.parseLong(value.toString().trim());
            return result > 0 ? result : null;
        } catch (NumberFormatException e) {
            return null;
        }
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
