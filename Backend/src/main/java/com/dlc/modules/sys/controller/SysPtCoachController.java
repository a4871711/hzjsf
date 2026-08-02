package com.dlc.modules.sys.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ExportExcel;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
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
 * 私教教练管理（pt_coach）。
 * 注意：路径 /sys/ptCoach，避免与旧教练模块 SysCoachController(/sys/coach) 冲突。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/ptCoach")
public class SysPtCoachController extends AbstractController {

    @Autowired
    private SysPtCoachService sysPtCoachService;

    @RequestMapping("/list")
    @RequiresPermissions("sys:ptCoach:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 私教域 store_id 保存的是 store_address.storeAddrId，权限也必须使用门店地址ID。
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        Query query = new Query(params);
        List<PtCoachEntity> list = sysPtCoachService.queryList(query);
        int total = sysPtCoachService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil);
    }

    /**
     * 按当前筛选条件导出全部教练。导出范围与列表接口保持一致，避免绕过门店数据权限。
     */
    @RequestMapping("/export")
    @RequiresPermissions("sys:ptCoach:list")
    public void export(@RequestParam Map<String, Object> params, HttpServletResponse response) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreAddrIds());
        params.remove("page");
        params.remove("limit");
        params.remove("offset");
        params.remove("sidx");
        params.remove("order");

        List<PtCoachEntity> list = sysPtCoachService.queryList(params);
        String[] titles = {"编号", "姓名", "手机", "所属门店", "等级", "状态", "排序", "创建时间"};
        String[][] values = buildExportValues(list);
        String fileName = "教练列表_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xlsx";
        XSSFWorkbook workbook = ExportExcel.getXSSFWorkbook("教练列表", titles, values);
        writeWorkbook(response, fileName, workbook);
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:ptCoach:info")
    public R info(@PathVariable("id") Long id) {
        return R.ok().put("coach", sysPtCoachService.queryObject(id));
    }

    @RequestMapping("/save")
    @RequiresPermissions("sys:ptCoach:save")
    public R save(@RequestBody PtCoachEntity coach) {
        coach.setCreatedBy(getUserId());
        coach.setUpdatedBy(getUserId());
        sysPtCoachService.save(coach);
        return R.ok();
    }

    @RequestMapping("/update")
    @RequiresPermissions("sys:ptCoach:update")
    public R update(@RequestBody PtCoachEntity coach) {
        coach.setUpdatedBy(getUserId());
        sysPtCoachService.update(coach);
        return R.ok();
    }

    @RequestMapping("/delete")
    @RequiresPermissions("sys:ptCoach:delete")
    public R delete(@RequestBody Long[] ids) {
        sysPtCoachService.deleteBatch(ids);
        return R.ok();
    }

    @RequestMapping("/changeStatus")
    @RequiresPermissions("sys:ptCoach:changeStatus")
    public R changeStatus(@RequestBody PtCoachEntity coach) {
        sysPtCoachService.changeStatus(coach.getId(), coach.getStatus(), coach.getDisableReason());
        return R.ok();
    }

    /**
     * 教练预约只读抽屉(第14步回填):该教练最近预约,含会员/商品/门店名。
     */
    @RequestMapping("/appointments/{id}")
    @RequiresPermissions("sys:ptCoach:appointments")
    public R appointments(@PathVariable("id") Long id) {
        return R.ok().put("list", sysPtCoachService.queryRecentAppointments(id));
    }

    private String[][] buildExportValues(List<PtCoachEntity> list) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[][] values = new String[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            PtCoachEntity coach = list.get(i);
            values[i][0] = valueOf(coach.getCoachNo());
            values[i][1] = valueOf(coach.getCoachName());
            values[i][2] = valueOf(coach.getMobile());
            values[i][3] = valueOf(coach.getStoreNames());
            values[i][4] = valueOf(coach.getCoachLevel());
            values[i][5] = statusText(coach.getStatus());
            values[i][6] = coach.getSortNo() == null ? "" : String.valueOf(coach.getSortNo());
            values[i][7] = coach.getCreatedAt() == null ? "" : dateFormat.format(coach.getCreatedAt());
        }
        return values;
    }

    private String valueOf(String value) {
        return value == null ? "" : value;
    }

    private String statusText(Integer status) {
        if (Integer.valueOf(1).equals(status)) {
            return "正常";
        }
        if (Integer.valueOf(2).equals(status)) {
            return "停用";
        }
        if (Integer.valueOf(3).equals(status)) {
            return "离职";
        }
        return "";
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
            throw new RRException("导出教练数据失败");
        } finally {
            try {
                workbook.close();
            } catch (IOException ignored) {
                // 响应流已完成，关闭工作簿失败不覆盖原始导出结果。
            }
        }
    }
}
