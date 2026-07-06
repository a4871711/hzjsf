package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.MkFlashSaleActivityDao;
import com.dlc.modules.sys.dao.PtProductDao;
import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;
import com.dlc.modules.sys.entity.PtProductEntity;
import com.dlc.modules.sys.service.SysMarketingFlashSaleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 限时秒杀活动 Service 实现。与拼团同构，差异：activity_stock 必填且>0（售罄不可买）。
 * 秒杀价 ≤ 商品 sale_price（联 pt_product 校验）；sold_count 后台只读；
 * 删除走逻辑删，进行中活动（时间窗内且 status=1）禁删。
 *
 * @author claude
 */
@Service("sysMarketingFlashSaleService")
public class SysMarketingFlashSaleServiceImpl implements SysMarketingFlashSaleService {

    @Autowired
    private MkFlashSaleActivityDao mkFlashSaleActivityDao;
    @Autowired
    private PtProductDao ptProductDao;

    @Override
    public MkFlashSaleActivityEntity queryObject(Long id) {
        return mkFlashSaleActivityDao.queryObject(id);
    }

    @Override
    public List<MkFlashSaleActivityEntity> queryList(Map<String, Object> map) {
        return mkFlashSaleActivityDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return mkFlashSaleActivityDao.queryTotal(map);
    }

    @Override
    public void save(MkFlashSaleActivityEntity entity) {
        validate(entity);
        if (entity.getStatus() == null) { entity.setStatus(1); }
        Date now = new Date();
        entity.setSoldCount(0);
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        mkFlashSaleActivityDao.save(entity);
    }

    @Override
    public void update(MkFlashSaleActivityEntity entity) {
        MkFlashSaleActivityEntity old = mkFlashSaleActivityDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("秒杀活动不存在");
        }
        validate(entity);
        entity.setUpdatedAt(new Date());
        // sold_count 后台只读：update 语句不含该列，前端传入也不会覆盖
        mkFlashSaleActivityDao.update(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        Date now = new Date();
        for (Long id : ids) {
            MkFlashSaleActivityEntity a = mkFlashSaleActivityDao.queryObject(id);
            if (a == null || Integer.valueOf(1).equals(a.getDeleted())) {
                continue;
            }
            if (Integer.valueOf(1).equals(a.getStatus()) && inTimeWindow(a.getStartTime(), a.getEndTime(), now)) {
                throw new RRException("秒杀活动[" + a.getActivityName() + "]正在进行中，不可删除，请先下架");
            }
        }
        mkFlashSaleActivityDao.deleteBatch(ids);
    }

    @Override
    public void changeStatus(Long id, Integer status, Long userId) {
        if (id == null || status == null || (status != 0 && status != 1)) {
            throw new RRException("参数不合法");
        }
        MkFlashSaleActivityEntity a = mkFlashSaleActivityDao.queryObject(id);
        if (a == null || Integer.valueOf(1).equals(a.getDeleted())) {
            throw new RRException("秒杀活动不存在");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("status", status);
        map.put("updatedBy", userId);
        mkFlashSaleActivityDao.changeStatus(map);
    }

    /* ============ 私有辅助方法 ============ */

    private void validate(MkFlashSaleActivityEntity e) {
        if (StringUtils.isBlank(e.getActivityName())) {
            throw new RRException("活动名称不能为空");
        }
        if (e.getProductId() == null) {
            throw new RRException("请选择关联商品");
        }
        PtProductEntity product = ptProductDao.queryObject(e.getProductId());
        if (product == null || Integer.valueOf(1).equals(product.getDeleted())) {
            throw new RRException("关联商品不存在");
        }
        if (!Integer.valueOf(1).equals(product.getListingStatus())) {
            throw new RRException("关联商品未上架，不能配置活动");
        }
        if (e.getFlashSalePrice() == null || e.getFlashSalePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RRException("秒杀价必须大于0");
        }
        if (product.getSalePrice() != null && e.getFlashSalePrice().compareTo(product.getSalePrice()) > 0) {
            throw new RRException("秒杀价不能高于商品售价");
        }
        // 与拼团不同：秒杀库存必填
        if (e.getActivityStock() == null || e.getActivityStock() <= 0) {
            throw new RRException("秒杀库存必填且必须大于0");
        }
        if (e.getPurchaseLimit() != null && e.getPurchaseLimit() <= 0) {
            throw new RRException("每人限购必须大于0（不限购请留空）");
        }
        checkTimeRange(e.getStartTime(), e.getEndTime());
    }

    private void checkTimeRange(String startTime, String endTime) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            throw new RRException("请填写活动开始/结束时间");
        }
        Date start = parseTime(startTime);
        Date end = parseTime(endTime);
        if (!start.before(end)) {
            throw new RRException("活动开始时间必须早于结束时间");
        }
    }

    private boolean inTimeWindow(String startTime, String endTime, Date now) {
        if (StringUtils.isBlank(startTime) || StringUtils.isBlank(endTime)) {
            return false;
        }
        Date start = parseTime(startTime);
        Date end = parseTime(endTime);
        return !now.before(start) && !now.after(end);
    }

    private Date parseTime(String time) {
        String t = time.trim();
        String pattern;
        if (t.length() == 10) {
            pattern = "yyyy-MM-dd";
        } else if (t.length() == 16) {
            pattern = "yyyy-MM-dd HH:mm";
        } else {
            pattern = "yyyy-MM-dd HH:mm:ss";
        }
        try {
            return new SimpleDateFormat(pattern).parse(t);
        } catch (ParseException ex) {
            throw new RRException("时间格式不合法：" + time);
        }
    }
}
