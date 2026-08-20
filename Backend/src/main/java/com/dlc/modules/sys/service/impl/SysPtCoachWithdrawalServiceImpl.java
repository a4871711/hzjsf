package com.dlc.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachWithdrawalDao;
import com.dlc.modules.api.service.impl.PtCoachWithdrawalAmountPolicy;
import com.dlc.modules.sys.entity.PtCoachWithdrawalReviewForm;
import com.dlc.modules.sys.service.SysPtCoachWithdrawalService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/** 提现审核实现。事务由 sys.service.impl 事务切面统一管理。 */
@Service("sysPtCoachWithdrawalService")
public class SysPtCoachWithdrawalServiceImpl implements SysPtCoachWithdrawalService {

    @Autowired
    private CoachWithdrawalDao coachWithdrawalDao;

    @Override
    public List<Map<String, Object>> queryList(Map<String, Object> params) {
        return coachWithdrawalDao.queryAdminList(params);
    }

    @Override
    public int queryTotal(Map<String, Object> params) {
        return coachWithdrawalDao.countAdminList(params);
    }

    @Override
    public Map<String, Object> queryObject(Long id, String storeIds) {
        if (id == null) {
            throw new RRException("缺少参数：id");
        }
        Map<String, Object> item = coachWithdrawalDao.queryAdminObject(id, storeIds);
        if (item == null) {
            throw new RRException("提现记录不存在或无权操作");
        }
        return item;
    }

    @Override
    public void review(PtCoachWithdrawalReviewForm form, Long operatorId, String storeIds) {
        if (form == null || form.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        if (!Integer.valueOf(1).equals(form.getStatus()) && !Integer.valueOf(2).equals(form.getStatus())) {
            throw new RRException("审核状态非法");
        }
        // 先做门店范围校验，再进入锁定和状态流转，避免通过记录ID越权审核。
        Map<String, Object> scoped = queryObject(form.getId(), storeIds);
        Long coachId = asLong(scoped.get("coachId"), "教练ID");
        if (coachWithdrawalDao.lockCoach(coachId) == null) {
            throw new RRException("教练不存在");
        }
        // 与教练端申请保持相同的锁顺序：先锁教练，再锁提现行，降低并发审核/申请死锁风险。
        Map<String, Object> pending = coachWithdrawalDao.queryForUpdate(form.getId());
        if (pending == null) {
            throw new RRException("提现记录不是待审核状态，不能重复审核");
        }
        String attachmentUrls = normalizeAttachmentUrls(form.getAttachmentUrls());
        String remark = trimRemark(form.getReviewRemark());

        if (Integer.valueOf(1).equals(form.getStatus())) {
            PtCoachWithdrawalReviewForm reject = form;
            reject.setAttachmentUrls(attachmentUrls);
            reject.setReviewRemark(remark);
            com.dlc.modules.api.entity.PtCoachWithdrawalEntity entity = reviewEntity(reject, operatorId);
            if (coachWithdrawalDao.reject(entity) != 1) {
                throw new RRException("提现驳回失败，请刷新后重试");
            }
            return;
        }

        PtCoachWithdrawalAmountPolicy.requirePositive(form.getSettlementAmount(), "结算金额");
        BigDecimal settlementAmount = money(form.getSettlementAmount(), "结算金额");
        BigDecimal actualSettlementAmount = money(form.getActualSettlementAmount(), "实际结算金额");
        Map<String, Object> balance = coachWithdrawalDao.queryBalance(coachId);
        BigDecimal availableBefore = money(balance.get("incomeTotal"), "累计收入")
                .subtract(money(balance.get("settledAmount"), "已结算金额"))
                .subtract(money(balance.get("frozenAmount"), "冻结金额"));
        BigDecimal currentFrozen = money(pending.get("frozenAmount"), "冻结金额");
        PtCoachWithdrawalAmountPolicy.validateApprovalAmount(
                availableBefore, currentFrozen, actualSettlementAmount);
        BigDecimal maxSettlement = availableBefore.add(currentFrozen);
        if (settlementAmount.compareTo(maxSettlement) > 0) {
            throw new RRException("结算金额超过可用余额");
        }

        PtCoachWithdrawalReviewForm approve = form;
        approve.setSettlementAmount(settlementAmount);
        approve.setActualSettlementAmount(actualSettlementAmount);
        approve.setAttachmentUrls(attachmentUrls);
        approve.setReviewRemark(remark);
        com.dlc.modules.api.entity.PtCoachWithdrawalEntity entity = reviewEntity(approve, operatorId);
        if (coachWithdrawalDao.approve(entity) != 1) {
            throw new RRException("提现审核通过失败，请刷新后重试");
        }
    }

    private com.dlc.modules.api.entity.PtCoachWithdrawalEntity reviewEntity(
            PtCoachWithdrawalReviewForm form, Long operatorId) {
        com.dlc.modules.api.entity.PtCoachWithdrawalEntity entity =
                new com.dlc.modules.api.entity.PtCoachWithdrawalEntity();
        entity.setId(form.getId());
        entity.setStatus(form.getStatus());
        entity.setSettlementAmount(form.getSettlementAmount());
        entity.setActualSettlementAmount(form.getActualSettlementAmount());
        entity.setAttachmentUrls(form.getAttachmentUrls());
        entity.setReviewRemark(form.getReviewRemark());
        entity.setReviewedBy(operatorId);
        return entity;
    }

    private String normalizeAttachmentUrls(String value) {
        if (StringUtils.isBlank(value)) {
            return "[]";
        }
        try {
            JSONArray array = JSON.parseArray(value);
            return array == null ? "[]" : array.toJSONString();
        } catch (Exception e) {
            throw new RRException("审核附件格式不正确");
        }
    }

    private String trimRemark(String value) {
        if (value == null) {
            return null;
        }
        String result = value.trim();
        if (result.length() > 500) {
            throw new RRException("审核备注不能超过500个字");
        }
        return result;
    }

    private BigDecimal money(Object value, String fieldName) {
        if (!(value instanceof Number)) {
            throw new RRException(fieldName + "数据异常");
        }
        return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
    }

    private Long asLong(Object value, String fieldName) {
        if (!(value instanceof Number)) {
            throw new RRException(fieldName + "数据异常");
        }
        return ((Number) value).longValue();
    }
}
