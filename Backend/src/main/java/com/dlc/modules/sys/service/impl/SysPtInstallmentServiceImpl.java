package com.dlc.modules.sys.service.impl;

import com.dlc.modules.api.dao.SystemMsgDao;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.sys.dao.SysInstallmentDao;
import com.dlc.modules.sys.service.SysPtInstallmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台 · 私教分期 Service 实现(第20步资金域)。落在 sys.service.impl,命中事务切面(REQUIRED)。
 * <p>查询走 SysInstallmentDao;催缴走站内信(system_msg,复用 api 侧 SystemMsgDao),try/catch 不阻塞。</p>
 *
 * @author claude
 */
@Service("sysPtInstallmentService")
public class SysPtInstallmentServiceImpl implements SysPtInstallmentService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private SysInstallmentDao sysInstallmentDao;
    @Autowired
    private SystemMsgDao systemMsgDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return sysInstallmentDao.queryList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return sysInstallmentDao.queryTotal(params);
    }

    @Override
    public Map<String, Object> queryDetail(Long id) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("plan", sysInstallmentDao.queryObject(id));
        result.put("bills", sysInstallmentDao.queryBills(id));
        return result;
    }

    @Override
    public boolean remind(Long id) {
        if (id == null) {
            return false;
        }
        Map<String, Object> plan = sysInstallmentDao.queryObject(id);
        if (plan == null) {
            return false;
        }
        // 催缴前置:必须存在逾期账单(status=2),否则视为"无逾期账单"
        Map<String, Object> summary = sysInstallmentDao.queryOverdueSummary(id);
        int overdueCount = summary == null || summary.get("overdueCount") == null
                ? 0 : ((Number) summary.get("overdueCount")).intValue();
        if (overdueCount <= 0) {
            return false;
        }
        BigDecimal overdueAmount = summary.get("overdueAmount") == null
                ? BigDecimal.ZERO : new BigDecimal(summary.get("overdueAmount").toString());
        Object memberIdObj = plan.get("memberId");
        if (memberIdObj == null) {
            return false;
        }
        Long memberId = ((Number) memberIdObj).longValue();
        // 触达:站内信(不改账单状态,仅通知);try/catch 不阻塞主流程
        try {
            SystemMsgEntity msg = new SystemMsgEntity();
            msg.setUserId(memberId);
            msg.setRecord("您有 " + overdueCount + " 期私教分期已逾期 ¥" + overdueAmount.toPlainString()
                    + ",请尽快支付,否则将暂停约课");
            msg.setMsgType(0);
            msg.setSendTime(new Date());
            systemMsgDao.save(msg);
        } catch (Exception e) {
            // 触达失败不阻塞:记日志,催缴按已尝试处理(前端提示已发送)
            log.error("分期催缴站内信发送失败 planId={},memberId={}", id, memberId, e);
        }
        return true;
    }
}
