package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtProductTypeDao;
import com.dlc.modules.sys.entity.PtProductTypeEntity;
import com.dlc.modules.sys.service.SysPtProductTypeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教商品类型 Service 实现。事务由 sys.service.impl 切面统一管理。
 *
 * @author claude
 */
@Service("sysPtProductTypeService")
public class SysPtProductTypeServiceImpl implements SysPtProductTypeService {

    @Autowired
    private PtProductTypeDao ptProductTypeDao;

    @Override
    public PtProductTypeEntity queryObject(Long id) {
        return ptProductTypeDao.queryObject(id);
    }

    @Override
    public List<PtProductTypeEntity> queryList(Map<String, Object> map) {
        return ptProductTypeDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptProductTypeDao.queryTotal(map);
    }

    @Override
    public void save(PtProductTypeEntity entity) {
        if (StringUtils.isBlank(entity.getTypeName())) {
            throw new RRException("类型名称不能为空");
        }
        String name = entity.getTypeName().trim();
        if (name.length() < 2 || name.length() > 20) {
            throw new RRException("类型名称长度需为 2-20 字");
        }
        if (ptProductTypeDao.countByName(name, null) > 0) {
            throw new RRException("类型名称已存在");
        }
        entity.setTypeName(name);
        if (entity.getSortNo() == null) { entity.setSortNo(0); }
        if (entity.getStatus() == null) { entity.setStatus(1); }
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        ptProductTypeDao.save(entity);
    }

    @Override
    public void update(PtProductTypeEntity entity) {
        PtProductTypeEntity old = ptProductTypeDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("类型不存在");
        }
        if (StringUtils.isNotBlank(entity.getTypeName())) {
            String name = entity.getTypeName().trim();
            if (name.length() < 2 || name.length() > 20) {
                throw new RRException("类型名称长度需为 2-20 字");
            }
            if (ptProductTypeDao.countByName(name, entity.getId()) > 0) {
                throw new RRException("类型名称已存在");
            }
            entity.setTypeName(name);
        }
        entity.setUpdatedAt(new Date());
        ptProductTypeDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            PtProductTypeEntity t = ptProductTypeDao.queryObject(id);
            if (t == null) { continue; }
            if (ptProductTypeDao.countByTypeIdInProduct(id) > 0) {
                throw new RRException("该类型下存在商品，不可删除：" + t.getTypeName());
            }
        }
        ptProductTypeDao.deleteBatch(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        PtProductTypeEntity u = new PtProductTypeEntity();
        u.setId(id);
        u.setStatus(status);
        u.setUpdatedAt(new Date());
        ptProductTypeDao.update(u);
    }

    @Override
    public List<PtProductTypeEntity> queryOptions() {
        return ptProductTypeDao.queryOptions();
    }
}
