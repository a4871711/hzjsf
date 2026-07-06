package com.dlc.modules.sys.service;

import java.util.List;
import java.util.Map;

/**
 * 私教预约记录后台 Service(第15步):只读列表/详情/统计卡 +
 * finish/cancel/coachBook 三个动作委托 api 侧 PrivateAppointmentService(第14步三态机)。
 *
 * @author claude
 */
public interface SysPrivateAppointmentService {

    List<Map<String, Object>> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 顶部统计卡:待上课/已完成/已取消/爽约(随 list 一并返回) */
    Map<String, Object> queryStat(Map<String, Object> params);

    /** 详情;查不到或不在 storeIds 门店范围返回 null(controller 按 404 处理) */
    Map<String, Object> queryDetail(Long id, String storeIds);

    /** 门店范围内判存(finish/cancel 前越权校验) */
    boolean existsInScope(Long id, String storeIds);

    /** 教练在管辖门店范围内判存(coachBook 前越权校验) */
    boolean coachInScope(Long coachId, String storeIds);

    /** 完成核销:委托第14步 finishAppointment(条件 UPDATE 1→3 幂等 + 账本 finish) */
    void finish(Long appointmentId, Long operatorId);

    /** 后台取消(不受免费取消时间窗限制):委托第14步 cancelByAdmin(1→2 幂等 + 解冻) */
    void cancel(Long appointmentId, String cancelReason, Long operatorId);

    /**
     * 教练代约:委托第14步 coachBook(校验教练只能约自己门店会员,created_by=管理员ID)。
     * @return {appointmentId, appointmentNo}
     */
    Map<String, Object> coachBook(Long coachId, Long memberId, Long productId,
                                  String appointmentDate, String startTime, String endTime, Long operatorId);
}
