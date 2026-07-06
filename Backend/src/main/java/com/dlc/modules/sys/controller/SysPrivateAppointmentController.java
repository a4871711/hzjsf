package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.service.SysPrivateAppointmentService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 私教预约记录(pt_private_appointment,第15步)。路径 /sys/privateAppointment。
 * finish/cancel/coachBook 三个动作直接委托 api 侧 PrivateAppointmentService(第14步三态机),
 * controller 只做参数解析与门店越权校验;cancel 走 cancelByAdmin 不受免费取消时间窗限制。
 * 门店隔离:storeIds 为空(超管)不过滤;越权详情/操作一律按 404 处理。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/privateAppointment")
public class SysPrivateAppointmentController extends AbstractController {

    @Autowired
    private SysPrivateAppointmentService sysPrivateAppointmentService;

    /** 列表 + 顶部统计卡(待上课/已完成/已取消/爽约,统计不吃 appointmentStatus 筛选) */
    @RequestMapping("/list")
    @RequiresPermissions("sys:privateAppointment:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据隔离:非超管按所属门店过滤(超管 storeIds 为空则不过滤)
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        List<Map<String, Object>> list = sysPrivateAppointmentService.queryList(query);
        int total = sysPrivateAppointmentService.queryTotal(query);
        PageUtils pageUtil = new PageUtils(list, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtil).put("stat", sysPrivateAppointmentService.queryStat(query));
    }

    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:privateAppointment:info")
    public R info(@PathVariable("id") Long id) {
        Map<String, Object> entity = sysPrivateAppointmentService.queryDetail(id,
                ShiroUtils.getUserEntity().getStoreIds());
        if (entity == null) {
            // 不存在或不在管辖门店范围:统一 404
            return R.error(404, "预约记录不存在");
        }
        return R.ok().put("entity", entity);
    }

    /** 完成核销:{appointmentId};仅已预约(1)可完成,已完成幂等返回成功不重复扣课 */
    @RequestMapping("/finish")
    @RequiresPermissions("sys:privateAppointment:finish")
    public R finish(@RequestBody Map<String, Object> params) {
        Long appointmentId = parseLong(params.get("appointmentId"));
        if (appointmentId == null) {
            return R.error("缺少参数:appointmentId");
        }
        if (!sysPrivateAppointmentService.existsInScope(appointmentId,
                ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "预约记录不存在");
        }
        sysPrivateAppointmentService.finish(appointmentId, getUserId());
        return R.ok();
    }

    /** 后台取消:{appointmentId, cancelReason?};不受免费取消时间窗限制(cancelByAdmin) */
    @RequestMapping("/cancel")
    @RequiresPermissions("sys:privateAppointment:cancel")
    public R cancel(@RequestBody Map<String, Object> params) {
        Long appointmentId = parseLong(params.get("appointmentId"));
        if (appointmentId == null) {
            return R.error("缺少参数:appointmentId");
        }
        if (!sysPrivateAppointmentService.existsInScope(appointmentId,
                ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "预约记录不存在");
        }
        String cancelReason = params.get("cancelReason") == null ? null : params.get("cancelReason").toString();
        sysPrivateAppointmentService.cancel(appointmentId, cancelReason, getUserId());
        return R.ok();
    }

    /**
     * 教练代约课:{coachId, memberId, productId, appointmentDate, startTime, endTime}。
     * 教练须在管辖门店范围内;"教练只能约自己门店会员"/权益自动匹配/容量护栏在第14步 coachBook 内收口。
     */
    @RequestMapping("/coachBook")
    @RequiresPermissions("sys:privateAppointment:coachBook")
    public R coachBook(@RequestBody Map<String, Object> params) {
        Long coachId = parseLong(params.get("coachId"));
        Long memberId = parseLong(params.get("memberId"));
        Long productId = parseLong(params.get("productId"));
        String appointmentDate = params.get("appointmentDate") == null ? null : params.get("appointmentDate").toString();
        String startTime = params.get("startTime") == null ? null : params.get("startTime").toString();
        String endTime = params.get("endTime") == null ? null : params.get("endTime").toString();
        if (coachId == null || memberId == null || productId == null
                || appointmentDate == null || startTime == null || endTime == null) {
            return R.error("缺少参数:coachId/memberId/productId/appointmentDate/startTime/endTime");
        }
        // 越权校验:教练必须在管辖门店范围内,否则按不存在处理
        if (!sysPrivateAppointmentService.coachInScope(coachId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "教练不存在");
        }
        Map<String, Object> result = sysPrivateAppointmentService.coachBook(
                coachId, memberId, productId, appointmentDate, startTime, endTime, getUserId());
        return R.ok().put("data", result);
    }

    private Long parseLong(Object val) {
        if (val == null || val.toString().trim().isEmpty()) {
            return null;
        }
        return Long.valueOf(val.toString().trim());
    }
}
