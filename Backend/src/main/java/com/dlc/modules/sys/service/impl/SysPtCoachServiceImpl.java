package com.dlc.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.sys.dao.PtCoachDao;
import com.dlc.modules.sys.dao.PtCoachLevelDao;
import com.dlc.modules.sys.dao.PtCoachMonthlyCommissionRuleDao;
import com.dlc.modules.sys.dao.PtCoachStoreRelDao;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.entity.PtCoachLevelEntity;
import com.dlc.modules.sys.entity.PtCoachMonthlyCommissionRuleEntity;
import com.dlc.modules.sys.entity.PtCoachStoreRelEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教教练 Service 实现。事务由 sys.service.impl 切面统一管理（REQUIRED）。
 *
 * @author claude
 */
@Service("sysPtCoachService")
public class SysPtCoachServiceImpl implements SysPtCoachService {

    @Autowired
    private PtCoachDao ptCoachDao;
    @Autowired
    private PtCoachStoreRelDao ptCoachStoreRelDao;
    @Autowired
    private PtCoachLevelDao ptCoachLevelDao;
    @Autowired
    private PtCoachMonthlyCommissionRuleDao ptCoachMonthlyCommissionRuleDao;
    /** 跨模块注入 api dao(现有惯例,同 SysStoreServiceImpl 等):预约引用护栏 */
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;

    @Override
    public PtCoachEntity queryObject(Long id) {
        PtCoachEntity coach = ptCoachDao.queryObject(id);
        if (coach != null) {
            coach.setStoreIds(ptCoachStoreRelDao.queryStoreIds(id));
            coach.setStoreNames(ptCoachStoreRelDao.queryStoreNames(id));
            coach.setMonthlyCommissionRules(ptCoachMonthlyCommissionRuleDao.queryByCoachId(id));
        }
        return coach;
    }

    @Override
    public List<PtCoachEntity> queryList(Map<String, Object> map) {
        return ptCoachDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptCoachDao.queryTotal(map);
    }

    @Override
    public List<Map<String, Object>> queryMemberOptions(String keyword, String storeIds) {
        if (StringUtils.isBlank(keyword)) {
            return java.util.Collections.emptyList();
        }
        return ptCoachDao.queryMemberOptions(keyword.trim(), storeIds);
    }

    @Override
    public void bindMember(Long coachId, Long userId, Long operatorId, String storeIds) {
        if (coachId == null) {
            throw new RRException("缺少参数：coachId");
        }
        // 绑定是后台权限操作：同时校验教练和会员的门店数据范围，防止越权绑定。
        if (ptCoachDao.countCoachInScope(coachId, storeIds) == 0) {
            throw new RRException("教练不存在或无权操作");
        }
        if (userId != null) {
            if (ptCoachDao.countMemberInScope(userId, storeIds) == 0) {
                throw new RRException("会员不存在或不在当前门店范围");
            }
            if (ptCoachDao.countByUserId(userId, coachId) > 0) {
                throw new RRException("该会员账号已绑定其他教练");
            }
        }
        try {
            if (ptCoachDao.updateMemberBinding(coachId, userId, operatorId) == 0) {
                throw new RRException("教练不存在");
            }
        } catch (DuplicateKeyException e) {
            throw new RRException("该会员账号已绑定其他教练");
        }
    }

    @Override
    public void save(PtCoachEntity entity) {
        validateBase(entity);
        normalizeCertificateUrls(entity);
        if (entity.getStoreIds() == null || entity.getStoreIds().isEmpty()) {
            throw new RRException("请至少选择一个所属门店");
        }
        if (ptCoachDao.countByMobile(entity.getMobile().trim(), null) > 0) {
            throw new RRException("手机号已被占用");
        }
        entity.setCoachName(entity.getCoachName().trim());
        entity.setMobile(entity.getMobile().trim());
        if (StringUtils.isBlank(entity.getCoachLevel())) {
            PtCoachLevelEntity defaultLevel = ptCoachLevelDao.queryDefault();
            if (defaultLevel != null) {
                entity.setCoachLevel(defaultLevel.getLevelName());
            }
        }
        if (entity.getStatus() == null) { entity.setStatus(1); }
        if (entity.getCoachType() == null) { entity.setCoachType(1); }
        validateCoachType(entity.getCoachType());
        if (entity.getSortNo() == null) { entity.setSortNo(0); }
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        // 生成编号 JL+yyyyMMdd+4序，依赖 uk_pt_coach_no 兜底，撞键重试
        int retry = 0;
        while (true) {
            entity.setCoachNo(genCoachNo());
            try {
                ptCoachDao.save(entity);
                break;
            } catch (DuplicateKeyException e) {
                if (++retry >= 3) {
                    throw new RRException("教练编号生成冲突，请重试");
                }
            }
        }
        saveStoreRels(entity.getId(), entity.getStoreIds());
        saveMonthlyCommissionRules(entity.getId(), entity.getMonthlyCommissionRules());
    }

    @Override
    public void update(PtCoachEntity entity) {
        if (entity.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtCoachEntity old = ptCoachDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("教练不存在");
        }
        if (Integer.valueOf(3).equals(old.getStatus())) {
            throw new RRException("离职教练不可编辑");
        }
        if (entity.getCoachName() != null) {
            if (StringUtils.isBlank(entity.getCoachName())) {
                throw new RRException("教练姓名不能为空");
            }
            entity.setCoachName(entity.getCoachName().trim());
        }
        // 状态机：停用/离职必须填原因（以提交值优先，否则沿用旧值）
        Integer status = entity.getStatus() != null ? entity.getStatus() : old.getStatus();
        if ((Integer.valueOf(2).equals(status) || Integer.valueOf(3).equals(status))
                && StringUtils.isBlank(entity.getDisableReason()) && StringUtils.isBlank(old.getDisableReason())) {
            throw new RRException("停用或离职时必须填写原因");
        }
        if (StringUtils.isNotBlank(entity.getMobile())) {
            entity.setMobile(entity.getMobile().trim());
            validateMobile(entity.getMobile());
            if (ptCoachDao.countByMobile(entity.getMobile(), entity.getId()) > 0) {
                throw new RRException("手机号已被占用");
            }
        }
        if (entity.getCoachType() != null) {
            validateCoachType(entity.getCoachType());
        }
        // 更新接口未携带该字段时保持原值；显式传空字符串时按清空证书处理。
        if (entity.getCertificateUrls() != null) {
            normalizeCertificateUrls(entity);
        }
        entity.setUpdatedAt(new Date());
        ptCoachDao.update(entity);
        // 门店关联全删全插
        if (entity.getStoreIds() != null) {
            if (entity.getStoreIds().isEmpty()) {
                throw new RRException("请至少选择一个所属门店");
            }
            ptCoachStoreRelDao.deleteByCoachId(entity.getId());
            saveStoreRels(entity.getId(), entity.getStoreIds());
        }
        // 旧调用方未提交新字段时保留原配置；新教练表单会明确提交 [] 表示清空。
        if (entity.getMonthlyCommissionRules() != null) {
            ptCoachMonthlyCommissionRuleDao.deleteByCoachId(entity.getId());
            saveMonthlyCommissionRules(entity.getId(), entity.getMonthlyCommissionRules());
        }
    }

    @Override
    public void deleteBatch(Long[] ids) {
        // 第14步回填：教练名下存在未来未取消预约(pt_private_appointment status=1 且未开课)不可删除；
        // 历史预约(已完成/已取消/爽约)不拦——教练为软删,记录仍可回溯。
        // 历史订单和权益保留教练/课程快照；教练软删后仍可回溯，删除只受未来预约约束。
        for (Long id : ids) {
            if (ptPrivateAppointmentDao.countFutureByCoach(id) > 0) {
                PtCoachEntity coach = ptCoachDao.queryObject(id);
                String name = coach != null ? coach.getCoachName() : String.valueOf(id);
                throw new RRException("教练[" + name + "]存在未完成的预约，不可删除");
            }
        }
        for (Long id : ids) {
            ptCoachStoreRelDao.deleteByCoachId(id);
            ptCoachMonthlyCommissionRuleDao.deleteByCoachId(id);
        }
        ptCoachDao.deleteBatch(ids);
    }

    @Override
    public List<PtPrivateAppointmentEntity> queryRecentAppointments(Long coachId) {
        // 只读抽屉,取最近50条足够回看;完整分页在第15步预约记录页
        return ptPrivateAppointmentDao.queryRecentByCoach(coachId, 50);
    }

    @Override
    public void changeStatus(Long id, Integer status, String disableReason) {
        if (id == null || (status == null || (status != 1 && status != 2 && status != 3))) {
            throw new RRException("教练状态非法");
        }
        PtCoachEntity old = ptCoachDao.queryObject(id);
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("教练不存在");
        }
        if ((Integer.valueOf(2).equals(status) || Integer.valueOf(3).equals(status))
                && StringUtils.isBlank(disableReason)) {
            throw new RRException("停用或离职时必须填写原因");
        }
        PtCoachEntity u = new PtCoachEntity();
        u.setId(id);
        u.setStatus(status);
        u.setDisableReason(disableReason);
        u.setUpdatedAt(new Date());
        ptCoachDao.update(u);
    }

    @Override
    public List<PtCoachMonthlyCommissionRuleEntity> queryMonthlyProductOptions() {
        return ptCoachMonthlyCommissionRuleDao.queryMonthlyProductOptions();
    }

    private void validateBase(PtCoachEntity e) {
        if (StringUtils.isBlank(e.getCoachName())) {
            throw new RRException("教练姓名不能为空");
        }
        if (StringUtils.isBlank(e.getMobile())) {
            throw new RRException("手机号不能为空");
        }
        validateMobile(e.getMobile());
    }

    private void validateCoachType(Integer coachType) {
        if (!Integer.valueOf(1).equals(coachType) && !Integer.valueOf(2).equals(coachType)) {
            throw new RRException("教练类型非法，只允许1私教或2自由教练");
        }
    }

    private void validateMobile(String mobile) {
        if (!mobile.trim().matches("^1\\d{10}$")) {
            throw new RRException("手机号格式不正确");
        }
    }

    /**
     * certificate_urls 是 MySQL JSON 字段，空字符串会被数据库判定为非法 JSON。
     * 统一保存为 JSON 数组字符串，并在进入 Mapper 前返回可理解的业务错误。
     */
    private void normalizeCertificateUrls(PtCoachEntity entity) {
        String certificateUrls = entity.getCertificateUrls();
        if (StringUtils.isBlank(certificateUrls)) {
            entity.setCertificateUrls("[]");
            return;
        }
        try {
            JSONArray urls = JSON.parseArray(certificateUrls);
            entity.setCertificateUrls(urls == null ? "[]" : urls.toJSONString());
        } catch (Exception e) {
            throw new RRException("资格证书格式不正确");
        }
    }

    private void saveStoreRels(Long coachId, List<Long> storeIds) {
        Date now = new Date();
        for (Long storeId : storeIds) {
            if (storeId == null) { continue; }
            PtCoachStoreRelEntity rel = new PtCoachStoreRelEntity();
            rel.setCoachId(coachId);
            rel.setStoreId(storeId);
            rel.setCreatedAt(now);
            ptCoachStoreRelDao.save(rel);
        }
    }

    /**
     * 包月配置与教练资料在同一事务保存；同一课程只允许配置一次，避免核销时匹配不确定。
     */
    private void saveMonthlyCommissionRules(Long coachId,
                                            List<PtCoachMonthlyCommissionRuleEntity> rules) {
        if (rules == null || rules.isEmpty()) {
            return;
        }
        HashSet<Long> productIds = new HashSet<Long>();
        Date now = new Date();
        for (PtCoachMonthlyCommissionRuleEntity rule : rules) {
            if (rule == null || rule.getProductId() == null) {
                throw new RRException("请选择包月课程");
            }
            if (!productIds.add(rule.getProductId())) {
                throw new RRException("同一包月课程不能重复配置");
            }
            if (ptCoachMonthlyCommissionRuleDao.countMonthlyProduct(rule.getProductId()) == 0) {
                throw new RRException("所选课程不是有效的包月课程");
            }
            if (rule.getStandardLessonCount() == null || rule.getStandardLessonCount() <= 0) {
                throw new RRException("包月课程标准课节必须大于0");
            }
            BigDecimal rate = rule.getCommissionRate();
            if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0
                    || rate.compareTo(new BigDecimal("100")) > 0) {
                throw new RRException("包月课程提成比例必须大于0且不超过100");
            }
            BigDecimal belowStandardFee = rule.getBelowStandardLessonFee();
            if (belowStandardFee == null || belowStandardFee.compareTo(BigDecimal.ZERO) < 0) {
                throw new RRException("小于标准时的单节提成不能小于0");
            }
            rule.setCoachId(coachId);
            rule.setCommissionRate(rate.setScale(2, RoundingMode.HALF_UP));
            rule.setBelowStandardLessonFee(belowStandardFee.setScale(2, RoundingMode.HALF_UP));
            rule.setCreatedAt(now);
            rule.setUpdatedAt(now);
            ptCoachMonthlyCommissionRuleDao.save(rule);
        }
    }

    private String genCoachNo() {
        String prefix = "JL" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        int cnt = ptCoachDao.countByNoPrefix(prefix);
        return prefix + String.format("%04d", cnt + 1);
    }
}
