package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.sys.vo.PrivateReportSummaryVo;

import java.util.Map;

/**
 * 私教收入报表 Service（第23步 §19，纯只读聚合查询）。
 * 口径见 mapper/sys/PtPrivateReportDao.xml：先按 appointment 行算单次收入再 SUM；成本命中课时费规则缺配=0 进异常分流。
 * beginDate/endDate 必填，门店隔离由 controller 传入 storeIds。
 *
 * @author claude
 */
public interface SysPrivateReportService {

    /** 顶部 4 卡片汇总。 */
    PrivateReportSummaryVo summary(Map<String, Object> params);

    /** 门店分组报表（分页）。 */
    PageUtils storeReport(Map<String, Object> params);

    /** 教练分组报表（分页）。 */
    PageUtils coachReport(Map<String, Object> params);

    /** 课程分组报表（分页）。 */
    PageUtils courseReport(Map<String, Object> params);

    /** 明细报表（分页）。 */
    PageUtils detailReport(Map<String, Object> params);

    /** 异常数据列表（分页）。 */
    PageUtils abnormalList(Map<String, Object> params);
}
