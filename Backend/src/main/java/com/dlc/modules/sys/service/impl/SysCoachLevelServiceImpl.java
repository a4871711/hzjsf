package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtCoachDao;
import com.dlc.modules.sys.dao.PtCoachLevelDao;
import com.dlc.modules.sys.entity.PtCoachLevelEntity;
import com.dlc.modules.sys.service.SysCoachLevelService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教教练等级 Service 实现。
 * 事务由 spring-jdbc.xml 的 txPointcut 统一覆盖 sys.service.impl（REQUIRED）。
 *
 * @author claude
 */
@Service("sysCoachLevelService")
public class SysCoachLevelServiceImpl implements SysCoachLevelService {

    @Autowired
    private PtCoachLevelDao ptCoachLevelDao;
    @Autowired
    private PtCoachDao ptCoachDao;

    @Override
    public PtCoachLevelEntity queryObject(Long id) {
        return ptCoachLevelDao.queryObject(id);
    }

    @Override
    public List<PtCoachLevelEntity> queryList(Map<String, Object> map) {
        return ptCoachLevelDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptCoachLevelDao.queryTotal(map);
    }

    @Override
    public void save(PtCoachLevelEntity entity) {
        if (StringUtils.isBlank(entity.getLevelName())) {
            throw new RRException("等级名称不能为空");
        }
        if (ptCoachLevelDao.countByName(entity.getLevelName().trim(), null) > 0) {
            throw new RRException("等级名称已存在");
        }
        entity.setLevelName(entity.getLevelName().trim());
        if (entity.getSortNo() == null) { entity.setSortNo(0); }
        if (entity.getStatus() == null) { entity.setStatus(1); }
        boolean wantDefault = Integer.valueOf(1).equals(entity.getIsDefault());
        entity.setIsDefault(0);
        Date now = new Date();
        if (entity.getCreatedAt() == null) { entity.setCreatedAt(now); }
        entity.setUpdatedAt(now);
        ptCoachLevelDao.save(entity);
        if (wantDefault) {
            ptCoachLevelDao.clearAllDefault();
            PtCoachLevelEntity u = new PtCoachLevelEntity();
            u.setId(entity.getId());
            u.setIsDefault(1);
            u.setUpdatedAt(now);
            ptCoachLevelDao.update(u);
        }
    }

    @Override
    public void update(PtCoachLevelEntity entity) {
        PtCoachLevelEntity old = ptCoachLevelDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("等级不存在");
        }
        if (StringUtils.isNotBlank(entity.getLevelName())) {
            entity.setLevelName(entity.getLevelName().trim());
            if (ptCoachLevelDao.countByName(entity.getLevelName(), entity.getId()) > 0) {
                throw new RRException("等级名称已存在");
            }
            // 改名同步教练冗余等级名（同事务）
            if (!entity.getLevelName().equals(old.getLevelName())) {
                ptCoachDao.updateLevelNameRef(old.getLevelName(), entity.getLevelName());
            }
        }
        boolean wantDefault = Integer.valueOf(1).equals(entity.getIsDefault());
        if (wantDefault) {
            ptCoachLevelDao.clearAllDefault();
        }
        entity.setUpdatedAt(new Date());
        ptCoachLevelDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            PtCoachLevelEntity lvl = ptCoachLevelDao.queryObject(id);
            if (lvl == null) { continue; }
            if (Integer.valueOf(1).equals(lvl.getIsDefault())) {
                throw new RRException("默认等级不可删除：" + lvl.getLevelName());
            }
            if (ptCoachDao.countByLevelName(lvl.getLevelName()) > 0) {
                throw new RRException("该等级下存在教练，不可删除：" + lvl.getLevelName());
            }
        }
        ptCoachLevelDao.deleteBatch(ids);
    }

    @Override
    public void changeStatus(Long id, Integer status) {
        PtCoachLevelEntity u = new PtCoachLevelEntity();
        u.setId(id);
        u.setStatus(status);
        u.setUpdatedAt(new Date());
        ptCoachLevelDao.update(u);
    }

    @Override
    public List<PtCoachLevelEntity> queryOptions() {
        return ptCoachLevelDao.queryOptions();
    }
}
