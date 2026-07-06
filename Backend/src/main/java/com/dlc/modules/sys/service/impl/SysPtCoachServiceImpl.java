package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.sys.dao.PtCoachDao;
import com.dlc.modules.sys.dao.PtCoachStoreRelDao;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.entity.PtCoachStoreRelEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
    /** 跨模块注入 api dao(现有惯例,同 SysStoreServiceImpl 等):预约引用护栏 */
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;

    @Override
    public PtCoachEntity queryObject(Long id) {
        PtCoachEntity coach = ptCoachDao.queryObject(id);
        if (coach != null) {
            coach.setStoreIds(ptCoachStoreRelDao.queryStoreIds(id));
            coach.setStoreNames(ptCoachStoreRelDao.queryStoreNames(id));
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
    public void save(PtCoachEntity entity) {
        validateBase(entity);
        if (entity.getStoreIds() == null || entity.getStoreIds().isEmpty()) {
            throw new RRException("请至少选择一个所属门店");
        }
        if (ptCoachDao.countByMobile(entity.getMobile().trim(), null) > 0) {
            throw new RRException("手机号已被占用");
        }
        entity.setMobile(entity.getMobile().trim());
        if (entity.getStatus() == null) { entity.setStatus(1); }
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
    }

    @Override
    public void update(PtCoachEntity entity) {
        PtCoachEntity old = ptCoachDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("教练不存在");
        }
        if (Integer.valueOf(3).equals(old.getStatus())) {
            throw new RRException("离职教练不可编辑");
        }
        // 状态机：停用/离职必须填原因（以提交值优先，否则沿用旧值）
        Integer status = entity.getStatus() != null ? entity.getStatus() : old.getStatus();
        if ((Integer.valueOf(2).equals(status) || Integer.valueOf(3).equals(status))
                && StringUtils.isBlank(entity.getDisableReason()) && StringUtils.isBlank(old.getDisableReason())) {
            throw new RRException("停用或离职时必须填写原因");
        }
        if (StringUtils.isNotBlank(entity.getMobile())) {
            entity.setMobile(entity.getMobile().trim());
            if (ptCoachDao.countByMobile(entity.getMobile(), entity.getId()) > 0) {
                throw new RRException("手机号已被占用");
            }
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
    }

    @Override
    public void deleteBatch(Long[] ids) {
        // 第14步回填：教练名下存在未来未取消预约(pt_private_appointment status=1 且未开课)不可删除；
        // 历史预约(已完成/已取消/爽约)不拦——教练为软删,记录仍可回溯。
        // (pt_private_order / pt_member_private_benefit 不含 coach_id,教练删除只受预约引用约束)
        for (Long id : ids) {
            if (ptPrivateAppointmentDao.countFutureByCoach(id) > 0) {
                PtCoachEntity coach = ptCoachDao.queryObject(id);
                String name = coach != null ? coach.getCoachName() : String.valueOf(id);
                throw new RRException("教练[" + name + "]存在未完成的预约，不可删除");
            }
        }
        for (Long id : ids) {
            ptCoachStoreRelDao.deleteByCoachId(id);
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

    private void validateBase(PtCoachEntity e) {
        if (StringUtils.isBlank(e.getCoachName())) {
            throw new RRException("教练姓名不能为空");
        }
        if (StringUtils.isBlank(e.getMobile())) {
            throw new RRException("手机号不能为空");
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

    private String genCoachNo() {
        String prefix = "JL" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        int cnt = ptCoachDao.countByNoPrefix(prefix);
        return prefix + String.format("%04d", cnt + 1);
    }
}
