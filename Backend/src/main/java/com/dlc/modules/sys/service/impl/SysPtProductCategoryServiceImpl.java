package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.PtProductCategoryDao;
import com.dlc.modules.sys.dao.PtProductDao;
import com.dlc.modules.sys.entity.PtProductCategoryEntity;
import com.dlc.modules.sys.service.SysPtProductCategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 私教商品分类 Service 实现。事务由 sys.service.impl 切面统一管理。
 */
@Service("sysPtProductCategoryService")
public class SysPtProductCategoryServiceImpl implements SysPtProductCategoryService {

    @Autowired
    private PtProductCategoryDao ptProductCategoryDao;
    @Autowired
    private PtProductDao ptProductDao;

    @Override
    public PtProductCategoryEntity queryObject(Long id) {
        return ptProductCategoryDao.queryObject(id);
    }

    @Override
    public List<PtProductCategoryEntity> queryList(Map<String, Object> map) {
        return ptProductCategoryDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return ptProductCategoryDao.queryTotal(map);
    }

    @Override
    public void save(PtProductCategoryEntity entity) {
        String name = validateName(entity.getCategoryName());
        if (ptProductCategoryDao.countByName(name, null) > 0) {
            throw new RRException("分类名称已存在");
        }
        entity.setCategoryName(name);
        if (entity.getSortNo() == null) { entity.setSortNo(0); }
        if (entity.getStatus() == null) { entity.setStatus(1); }
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        ptProductCategoryDao.save(entity);
    }

    @Override
    public void update(PtProductCategoryEntity entity) {
        if (entity.getId() == null) {
            throw new RRException("缺少参数：id");
        }
        PtProductCategoryEntity old = ptProductCategoryDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("商品分类不存在");
        }
        if (entity.getCategoryName() != null) {
            String name = validateName(entity.getCategoryName());
            if (ptProductCategoryDao.countByName(name, entity.getId()) > 0) {
                throw new RRException("分类名称已存在");
            }
            entity.setCategoryName(name);
        }
        entity.setUpdatedAt(new Date());
        ptProductCategoryDao.update(entity);
        // 商品仍保留 category_name 快照，分类改名时同步，兼容移动端和旧接口。
        if (entity.getCategoryName() != null && !entity.getCategoryName().equals(old.getCategoryName())) {
            ptProductDao.updateCategoryNameByCategoryId(entity.getId(), entity.getCategoryName());
        }
    }

    @Override
    public void deleteBatch(Long[] ids) {
        for (Long id : ids) {
            PtProductCategoryEntity category = ptProductCategoryDao.queryObject(id);
            if (category == null) { continue; }
            if (ptProductCategoryDao.countByCategoryIdInProduct(id) > 0) {
                throw new RRException("该分类下存在商品，不可删除：" + category.getCategoryName());
            }
        }
        ptProductCategoryDao.deleteBatch(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (id == null || (status == null || (status != 0 && status != 1))) {
            throw new RRException("分类状态不合法");
        }
        PtProductCategoryEntity old = ptProductCategoryDao.queryObject(id);
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("商品分类不存在");
        }
        PtProductCategoryEntity update = new PtProductCategoryEntity();
        update.setId(id);
        update.setStatus(status);
        update.setUpdatedAt(new Date());
        ptProductCategoryDao.update(update);
    }

    @Override
    public List<PtProductCategoryEntity> queryOptions() {
        return ptProductCategoryDao.queryOptions();
    }

    private String validateName(String categoryName) {
        if (StringUtils.isBlank(categoryName)) {
            throw new RRException("分类名称不能为空");
        }
        String name = categoryName.trim();
        if (name.length() < 2 || name.length() > 20) {
            throw new RRException("分类名称长度需为 2-20 字");
        }
        return name;
    }
}
