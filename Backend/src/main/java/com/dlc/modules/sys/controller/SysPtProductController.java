package com.dlc.modules.sys.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ExportExcel;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtProductEntity;
import com.dlc.modules.sys.entity.TeamClassEntity;
import com.dlc.modules.sys.service.SysPtProductService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教商品管理。路径 /sys/ptProduct，perms 全小写 sys:ptproduct:*（对齐总则 §0.5）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptProduct")
public class SysPtProductController extends AbstractController {

    @Autowired
    private SysPtProductService sysPtProductService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptproduct:list")
    public R list(@RequestParam Map<String, Object> params) {
        // pt_product_store_rel.store_id 保存门店地址ID，不是 store.storeId。
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        Query query = new Query(params);
        List<PtProductEntity> list = sysPtProductService.queryList(query);
        int total = sysPtProductService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 按当前筛选条件导出全部私教商品。导出范围与列表保持一致，并复用列表门店数据权限。
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:ptproduct:list")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        params.remove("page");
        params.remove("limit");
        params.remove("offset");
        params.remove("sidx");
        params.remove("order");

        List<PtProductEntity> list = sysPtProductService.queryList(params);
        String[] titles = {"编号", "商品名称", "商品类型", "商品分类", "服务类型", "适用门店", "指定教练",
                "售价", "课时", "单次时长(分钟)", "有效期", "上架状态", "排序", "创建时间", "更新时间"};
        String fileName = "私教商品列表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        XSSFWorkbook workbook = ExportExcel.getXSSFWorkbook("私教商品列表", titles, buildExportValues(list));
        writeWorkbook(response, fileName, workbook);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptproduct:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("product", sysPtProductService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptproduct:save")
    public R save(@RequestBody PtProductEntity product) {
        product.setCreatedBy(getUserId());
        product.setUpdatedBy(getUserId());
        sysPtProductService.save(product);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptproduct:update")
    public R update(@RequestBody PtProductEntity product) {
        product.setUpdatedBy(getUserId());
        sysPtProductService.update(product);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptproduct:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtProductService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/onCard")
    @RequiresPermissions("sys:ptproduct:update")
    public R onCard(@RequestBody Long[] ids) {
        sysPtProductService.onCard(ids);
        return R.ok();
    }

    @RequestMapping("/offCard")
    @RequiresPermissions("sys:ptproduct:update")
    public R offCard(@RequestBody Long[] ids) {
        sysPtProductService.offCard(ids);
        return R.ok();
    }

    @RequestMapping("/copy/{id}")
    @RequiresPermissions("sys:ptproduct:save")
    public R copy(@PathVariable("id") Long id) {
        sysPtProductService.copy(id);
        return R.ok();
    }

    /** 指定团课商品下拉项（供附赠团课权益配置使用，无需单独按钮权限） */
    @RequestMapping("/groupClassOptions")
    public R groupClassOptions() {
        List<TeamClassEntity> list = sysPtProductService.groupClassOptions();
        return R.ok().put("list", list);
    }

    private String[][] buildExportValues(List<PtProductEntity> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[][] values = new String[list.size()][15];
        for (int i = 0; i < list.size(); i++) {
            PtProductEntity product = list.get(i);
            values[i][0] = valueOf(product.getProductNo());
            values[i][1] = valueOf(product.getProductName());
            values[i][2] = valueOf(product.getTypeName());
            values[i][3] = valueOf(product.getCategoryName());
            values[i][4] = serviceTypeText(product.getServiceType());
            values[i][5] = valueOf(product.getStoreNames());
            values[i][6] = isBlank(product.getCoachNames()) ? "不限教练" : product.getCoachNames();
            values[i][7] = valueOf(product.getSalePrice());
            values[i][8] = valueOf(product.getLessonCount());
            values[i][9] = valueOf(product.getDurationMinutes());
            values[i][10] = validityText(product.getValidityDays());
            values[i][11] = Integer.valueOf(1).equals(product.getListingStatus()) ? "已上架" : "未上架";
            values[i][12] = valueOf(product.getSortNo());
            values[i][13] = product.getCreatedAt() == null ? "" : dateFormat.format(product.getCreatedAt());
            values[i][14] = product.getUpdatedAt() == null ? "" : dateFormat.format(product.getUpdatedAt());
        }
        return values;
    }

    private String serviceTypeText(Integer serviceType) {
        if (Integer.valueOf(1).equals(serviceType)) {
            return "一对一";
        }
        if (Integer.valueOf(2).equals(serviceType)) {
            return "一对多";
        }
        return "";
    }

    private String validityText(Integer validityDays) {
        if (validityDays == null) {
            return "";
        }
        return Integer.valueOf(-1).equals(validityDays) ? "长期有效" : validityDays + "天";
    }

    private String valueOf(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
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
            throw new RRException("导出私教商品失败");
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // 响应流已完成，关闭工作簿失败不覆盖原始导出结果。
            }
        }
    }
}
