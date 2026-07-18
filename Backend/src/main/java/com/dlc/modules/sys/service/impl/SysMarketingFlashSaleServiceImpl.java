package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.sys.dao.MkFlashSaleActivityDao;
import com.dlc.modules.sys.entity.MkFlashSaleActivityEntity;
import com.dlc.modules.sys.entity.MkFlashSaleProductEntity;
import com.dlc.modules.sys.entity.MkFlashSaleTimeSlotEntity;
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
 * 限时秒杀活动 Service（多商品版，照《秒杀功能.dc.html》）。
 * 一活动多商品 + 单次/循环投放 + 倒计时预热 + 售罄策略。save/update 在 sys.service.impl，命中事务切面（原子）。
 *
 * @author claude
 */
@Service("sysMarketingFlashSaleService")
public class SysMarketingFlashSaleServiceImpl implements SysMarketingFlashSaleService {

    @Autowired
    private MkFlashSaleActivityDao mkFlashSaleActivityDao;

    @Override
    public MkFlashSaleActivityEntity queryObject(Long id) {
        MkFlashSaleActivityEntity a = mkFlashSaleActivityDao.queryObject(id);
        if (a != null) {
            Map<String, Object> p = new HashMap<>();
            p.put("activityId", id);
            p.put("bizType", a.getBizType() == null ? 2 : a.getBizType());
            a.setProducts(mkFlashSaleActivityDao.queryProductsByActivity(p));
            a.setTimeSlots(mkFlashSaleActivityDao.queryTimeSlotsByActivity(id));
        }
        return a;
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
        normalize(entity);
        validate(entity);
        Date now = new Date();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        mkFlashSaleActivityDao.save(entity);
        saveChildren(entity);
    }

    @Override
    public void update(MkFlashSaleActivityEntity entity) {
        MkFlashSaleActivityEntity old = mkFlashSaleActivityDao.queryObject(entity.getId());
        if (old == null || Integer.valueOf(1).equals(old.getDeleted())) {
            throw new RRException("秒杀活动不存在");
        }
        normalize(entity);
        validate(entity);
        entity.setUpdatedAt(new Date());
        // 结转已售：子表先删后建,但同商品的 sold_count 必须带过来,否则编辑一次进行中活动
        // 已售清零→总量 CAS 上限重新放开,实际可卖出配置库存的数倍
        Map<String, Object> oldQ = new HashMap<>();
        oldQ.put("activityId", entity.getId());
        oldQ.put("bizType", old.getBizType() == null ? 2 : old.getBizType());
        Map<Long, Integer> soldMap = new HashMap<>();
        for (MkFlashSaleProductEntity op : mkFlashSaleActivityDao.queryProductsByActivity(oldQ)) {
            soldMap.put(op.getProductId(), op.getSoldCount());
        }
        for (MkFlashSaleProductEntity np : entity.getProducts()) {
            Integer sold = soldMap.get(np.getProductId());
            np.setSoldCount(sold == null ? 0 : sold);
        }
        mkFlashSaleActivityDao.update(entity);
        mkFlashSaleActivityDao.deleteProductsByActivity(entity.getId());
        mkFlashSaleActivityDao.deleteTimeSlotsByActivity(entity.getId());
        saveChildren(entity);
    }

    @Override
    public void deleteBatch(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        Date now = new Date();
        for (Long id : ids) {
            // 只需活动级字段判进行中,走 DAO 免查商品/时段子表
            MkFlashSaleActivityEntity a = mkFlashSaleActivityDao.queryObject(id);
            if (a == null || Integer.valueOf(1).equals(a.getDeleted())) {
                continue;
            }
            if (Integer.valueOf(1).equals(a.getStatus()) && isRunningNow(a, now)) {
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

    /* ============ 私有 ============ */

    /** 子表写入（save/update 共用）：给每个商品/时段补 activityId 后批量插 */
    private void saveChildren(MkFlashSaleActivityEntity entity) {
        List<MkFlashSaleProductEntity> products = entity.getProducts();
        for (MkFlashSaleProductEntity p : products) {
            p.setActivityId(entity.getId());
        }
        mkFlashSaleActivityDao.insertProducts(products);
        // 仅循环生效存每日时段
        if (Integer.valueOf(2).equals(entity.getDeliveryType()) && entity.getTimeSlots() != null && !entity.getTimeSlots().isEmpty()) {
            for (MkFlashSaleTimeSlotEntity t : entity.getTimeSlots()) {
                t.setActivityId(entity.getId());
            }
            mkFlashSaleActivityDao.insertTimeSlots(entity.getTimeSlots());
        }
    }

    /** 补默认值 */
    private void normalize(MkFlashSaleActivityEntity e) {
        if (e.getBizType() == null) { e.setBizType(2); }
        if (e.getDeliveryType() == null) { e.setDeliveryType(1); }
        if (e.getStockMode() == null) { e.setStockMode(2); }
        if (e.getCountdownEnabled() == null) { e.setCountdownEnabled(0); }
        if (e.getSoldOutAction() == null) { e.setSoldOutAction(1); }
        if (e.getStatus() == null) { e.setStatus(1); }
        // 单次生效清空循环字段，循环生效清空单次字段，避免脏数据
        if (e.getDeliveryType() == 1) {
            e.setActivityStartDate(null);
            e.setActivityEndDate(null);
            e.setWeekDays(null);
            e.setStockMode(2);
            e.setTimeSlots(null);
        } else {
            e.setStartTime(null);
            e.setEndTime(null);
        }
    }

    private void validate(MkFlashSaleActivityEntity e) {
        if (StringUtils.isBlank(e.getActivityName())) {
            throw new RRException("活动名称不能为空");
        }
        int bizType = e.getBizType();
        // 本期只支持会员卡/权益卡：私教下单链路(PtPrivateOrderDao.selectFlashSaleActivity)仍读活动表已弃用的
        // 单商品列,尚未迁移到 mk_flash_sale_product 子表,放开 bizType=1 会导致私教秒杀下单 NPE
        if (bizType != 2 && bizType != 3) {
            throw new RRException("商品类型不合法（当前支持会员卡/权益卡）");
        }
        // 商品
        List<MkFlashSaleProductEntity> products = e.getProducts();
        if (products == null || products.isEmpty()) {
            throw new RRException("请至少选择一个秒杀商品");
        }
        if (products.size() > 100) {
            throw new RRException("单个秒杀活动最多 100 个商品");
        }
        for (MkFlashSaleProductEntity p : products) {
            if (p.getProductId() == null) {
                throw new RRException("存在未选择的商品");
            }
            Map<String, Object> bp = new HashMap<>();
            bp.put("bizType", bizType);
            bp.put("productId", p.getProductId());
            Map<String, Object> brief = mkFlashSaleActivityDao.queryProductBrief(bp);
            if (brief == null) {
                throw new RRException("关联商品不存在(ID " + p.getProductId() + ")");
            }
            String name = String.valueOf(brief.get("name"));
            if (!"1".equals(String.valueOf(brief.get("listing")))) {
                throw new RRException("商品[" + name + "]未上架，不能配置秒杀");
            }
            BigDecimal salePrice = brief.get("salePrice") == null ? null : new BigDecimal(String.valueOf(brief.get("salePrice")));
            if (p.getFlashSalePrice() == null || p.getFlashSalePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new RRException("商品[" + name + "]的秒杀价必须大于 0");
            }
            if (salePrice != null && p.getFlashSalePrice().compareTo(salePrice) > 0) {
                throw new RRException("商品[" + name + "]秒杀价不能高于对外售价（¥" + trim(salePrice) + "）");
            }
            if (p.getActivityStock() == null || p.getActivityStock() <= 0) {
                throw new RRException("商品[" + name + "]的秒杀库存必填且需大于 0");
            }
        }
        if (e.getPurchaseLimit() != null && e.getPurchaseLimit() <= 0) {
            throw new RRException("每人限购必须大于 0（不限购请留空）");
        }
        if (Integer.valueOf(1).equals(e.getCountdownEnabled())) {
            Integer m = e.getCountdownMinutes();
            if (m == null || m <= 0) {
                throw new RRException("开启倒计时需填写预热分钟数");
            }
        }
        // 投放方式
        if (e.getDeliveryType() == 1) {
            validateOnce(e);
        } else if (e.getDeliveryType() == 2) {
            validateLoop(e);
        } else {
            throw new RRException("投放方式不合法");
        }
    }

    /** 单次生效：连续时段，start<end，时长≤23h */
    private void validateOnce(MkFlashSaleActivityEntity e) {
        if (StringUtils.isBlank(e.getStartTime()) || StringUtils.isBlank(e.getEndTime())) {
            throw new RRException("请填写生效时间");
        }
        Date s = parseDateTime(e.getStartTime());
        Date en = parseDateTime(e.getEndTime());
        if (!s.before(en)) {
            throw new RRException("开始时间必须早于结束时间");
        }
        long hours = (en.getTime() - s.getTime()) / (60L * 60L * 1000L);
        if (hours > 23) {
            throw new RRException("单次生效时长不能超过 23 小时");
        }
    }

    /** 循环生效：周期≤90天、至少1生效日、至少1时段、单日累计≤18h */
    private void validateLoop(MkFlashSaleActivityEntity e) {
        if (StringUtils.isBlank(e.getActivityStartDate()) || StringUtils.isBlank(e.getActivityEndDate())) {
            throw new RRException("请填写活动周期");
        }
        Date s = parseDate(e.getActivityStartDate());
        Date en = parseDate(e.getActivityEndDate());
        if (s.after(en)) {
            throw new RRException("活动周期开始日不能晚于结束日");
        }
        long days = (en.getTime() - s.getTime()) / (24L * 60L * 60L * 1000L) + 1;
        if (days > 90) {
            throw new RRException("活动周期不能超过 90 天");
        }
        if (StringUtils.isBlank(e.getWeekDays())) {
            throw new RRException("请至少选择一个生效日");
        }
        List<MkFlashSaleTimeSlotEntity> slots = e.getTimeSlots();
        if (slots == null || slots.isEmpty()) {
            throw new RRException("请至少添加一个每日投放时段");
        }
        int totalMin = 0;
        for (MkFlashSaleTimeSlotEntity t : slots) {
            int start = hmToMin(t.getStartHm());
            int end = hmToMin(t.getEndHm());
            if (start < 0 || end < 0 || end <= start) {
                throw new RRException("每日投放时段不合法（结束需晚于开始）");
            }
            totalMin += (end - start);
        }
        if (totalMin > 18 * 60) {
            throw new RRException("每日投放时段单日累计不能超过 18 小时");
        }
        if (e.getStockMode() == null || (e.getStockMode() != 1 && e.getStockMode() != 2)) {
            throw new RRException("请选择库存设置方式");
        }
    }

    private boolean isRunningNow(MkFlashSaleActivityEntity a, Date now) {
        if (a.getDeliveryType() != null && a.getDeliveryType() == 2) {
            // 循环：粗判在周期内即视为进行中（禁删口径从严）
            if (StringUtils.isBlank(a.getActivityStartDate()) || StringUtils.isBlank(a.getActivityEndDate())) {
                return false;
            }
            Date s = parseDate(a.getActivityStartDate());
            Date en = parseDate(a.getActivityEndDate());
            return !now.before(s) && !now.after(addDays(en, 1));
        }
        if (StringUtils.isBlank(a.getStartTime()) || StringUtils.isBlank(a.getEndTime())) {
            return false;
        }
        Date s = parseDateTime(a.getStartTime());
        Date en = parseDateTime(a.getEndTime());
        return !now.before(s) && !now.after(en);
    }

    /* ---- 工具 ---- */
    private String trim(BigDecimal b) { return b.stripTrailingZeros().toPlainString(); }

    private int hmToMin(String hm) {
        if (StringUtils.isBlank(hm) || hm.indexOf(':') < 0) { return -1; }
        try {
            String[] a = hm.trim().split(":");
            int h = Integer.parseInt(a[0]);
            int m = Integer.parseInt(a[1]);
            if (h < 0 || h > 23 || m < 0 || m > 59) { return -1; }
            return h * 60 + m;
        } catch (Exception ex) {
            return -1;
        }
    }

    private Date addDays(Date d, int days) {
        return new Date(d.getTime() + days * 24L * 60L * 60L * 1000L);
    }

    private Date parseDateTime(String t) {
        String s = t.trim();
        String pattern = s.length() == 16 ? "yyyy-MM-dd HH:mm" : "yyyy-MM-dd HH:mm:ss";
        try {
            return new SimpleDateFormat(pattern).parse(s);
        } catch (ParseException ex) {
            throw new RRException("时间格式不合法：" + t);
        }
    }

    private Date parseDate(String t) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(t.trim());
        } catch (ParseException ex) {
            throw new RRException("日期格式不合法：" + t);
        }
    }
}
