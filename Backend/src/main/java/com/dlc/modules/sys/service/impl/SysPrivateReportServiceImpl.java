package com.dlc.modules.sys.service.impl;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.sys.dao.PtPrivateReportDao;
import com.dlc.modules.sys.service.SysPrivateReportService;
import com.dlc.modules.sys.vo.PrivateReportDetailVo;
import com.dlc.modules.sys.vo.PrivateReportRowVo;
import com.dlc.modules.sys.vo.PrivateReportSummaryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 私教收入报表 Service 实现（第23步 §19）。纯只读聚合查询，无写。
 * 分页在此层拼 offset/limit（默认 page=1、limit=10），口径全部落在 SQL（见 PtPrivateReportDao.xml）。
 *
 * @author claude
 */
@Service("sysPrivateReportService")
public class SysPrivateReportServiceImpl implements SysPrivateReportService {

    @Autowired
    private PtPrivateReportDao ptPrivateReportDao;

    @Override
    public PrivateReportSummaryVo summary(Map<String, Object> params) {
        PrivateReportSummaryVo vo = ptPrivateReportDao.summary(params);
        if (vo == null) {
            // 区间内无数据：返回全 0，避免前端拿到 null
            vo = new PrivateReportSummaryVo();
            vo.setTotalLessons(0L);
            vo.setIncome(BigDecimal.ZERO);
            vo.setCost(BigDecimal.ZERO);
            vo.setGrossProfit(BigDecimal.ZERO);
            vo.setGrossRate(BigDecimal.ZERO);
        }
        return vo;
    }

    @Override
    public PageUtils storeReport(Map<String, Object> params) {
        applyPaging(params);
        List<PrivateReportRowVo> list = ptPrivateReportDao.storeReport(params);
        int total = ptPrivateReportDao.storeReportTotal(params);
        return new PageUtils(list, total, intVal(params.get("limit")), intVal(params.get("page")));
    }

    @Override
    public PageUtils coachReport(Map<String, Object> params) {
        applyPaging(params);
        List<PrivateReportRowVo> list = ptPrivateReportDao.coachReport(params);
        int total = ptPrivateReportDao.coachReportTotal(params);
        return new PageUtils(list, total, intVal(params.get("limit")), intVal(params.get("page")));
    }

    @Override
    public PageUtils courseReport(Map<String, Object> params) {
        applyPaging(params);
        List<PrivateReportRowVo> list = ptPrivateReportDao.courseReport(params);
        int total = ptPrivateReportDao.courseReportTotal(params);
        return new PageUtils(list, total, intVal(params.get("limit")), intVal(params.get("page")));
    }

    @Override
    public PageUtils detailReport(Map<String, Object> params) {
        applyPaging(params);
        List<PrivateReportDetailVo> list = ptPrivateReportDao.detailReport(params);
        int total = ptPrivateReportDao.detailReportTotal(params);
        return new PageUtils(list, total, intVal(params.get("limit")), intVal(params.get("page")));
    }

    @Override
    public PageUtils abnormalList(Map<String, Object> params) {
        applyPaging(params);
        List<Map<String, Object>> list = ptPrivateReportDao.abnormalList(params);
        int total = ptPrivateReportDao.abnormalListTotal(params);
        return new PageUtils(list, total, intVal(params.get("limit")), intVal(params.get("page")));
    }

    /** 解析 page/limit（默认 page=1、limit=10）并回填 offset/limit/page 到 params，供 XML 分页。 */
    private void applyPaging(Map<String, Object> params) {
        int page = intVal(params.get("page"));
        int limit = intVal(params.get("limit"));
        if (page < 1) { page = 1; }
        if (limit < 1) { limit = 10; }
        params.put("page", page);
        params.put("limit", limit);
        params.put("offset", (page - 1) * limit);
    }

    private int intVal(Object o) {
        if (o == null) { return 0; }
        String s = o.toString().trim();
        if (s.isEmpty()) { return 0; }
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
