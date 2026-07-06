package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.vo.PrivateReportDetailVo;
import com.dlc.modules.sys.vo.PrivateReportRowVo;
import com.dlc.modules.sys.vo.PrivateReportSummaryVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 私教收入报表 Dao（第23步 §19，纯只读聚合查询，无写）。
 * 口径统一（见 mapper/sys/PtPrivateReportDao.xml）：
 * - 按已完成预约 finish_at 落统计日；beginDate/endDate 必填，门店隔离 storeIds。
 * - 单次收入 = 净实收(paid-refund) ÷ 订单总课时 × 本次完成课时（按 appointment 行先算再 SUM）。
 * - 成本命中 pt_coach_fee_rule(rule_type=1，先(coach,product)后(coach,0))，缺配=0 且进异常列表。
 * - lesson_count>0 防除零；毛利率 income=0 显示 0。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtPrivateReportDao {

    /** 顶部 4 卡片汇总。 */
    PrivateReportSummaryVo summary(Map<String, Object> params);

    /** 门店分组报表。 */
    List<PrivateReportRowVo> storeReport(Map<String, Object> params);

    int storeReportTotal(Map<String, Object> params);

    /** 教练分组报表。 */
    List<PrivateReportRowVo> coachReport(Map<String, Object> params);

    int coachReportTotal(Map<String, Object> params);

    /** 课程分组报表。 */
    List<PrivateReportRowVo> courseReport(Map<String, Object> params);

    int courseReportTotal(Map<String, Object> params);

    /** 明细报表（一行=一次已完成核销）。 */
    List<PrivateReportDetailVo> detailReport(Map<String, Object> params);

    int detailReportTotal(Map<String, Object> params);

    /** 异常数据列表（缺门店/缺教练/未配课时费/课时非法）。 */
    List<Map<String, Object>> abnormalList(Map<String, Object> params);

    int abnormalListTotal(Map<String, Object> params);
}
