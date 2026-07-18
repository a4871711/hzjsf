package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.ApiFlashSaleDao;
import com.dlc.modules.api.service.ApiFlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员端限时秒杀 Service 实现（多商品版）。展示口径：对外售价作划线原价、秒杀价即成交价。
 * 循环生效判活：今天在周期内 + 今天是生效日 + now 命中某个每日时段（当前时段=进行中，最近未来时段=预热）。
 *
 * @author claude
 */
@Service("apiFlashSaleService")
public class ApiFlashSaleServiceImpl implements ApiFlashSaleService {

    private static final String FMT = "yyyy-MM-dd HH:mm:ss";
    private static final String DFMT = "yyyy-MM-dd";

    @Autowired
    private ApiFlashSaleDao apiFlashSaleDao;

    @Override
    public List<Map<String, Object>> queryCurrentCards(Long storeAddrId) {
        List<Map<String, Object>> rows = apiFlashSaleDao.queryCurrentProductCards(storeAddrId);
        Date now = new Date();
        String serverTime = fmt(now);
        Map<Long, List<Map<String, Object>>> slotCache = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            int deliveryType = toInt(row.get("deliveryType"));
            List<Map<String, Object>> slots = null;
            if (deliveryType == 2) {
                Long aid = toLong(row.get("activityId"));
                slots = slotCache.get(aid);
                if (slots == null) {
                    slots = apiFlashSaleDao.queryTimeSlots(aid);
                    slotCache.put(aid, slots);
                }
            }
            Date[] win = resolveWindow(row, slots, now);
            if (win == null) {
                continue; // 循环:今天非生效日/时段已过 → 不展示
            }
            int remaining = computeRemaining(row, now);
            String status;
            if (remaining <= 0) {
                // 售罄策略：1=自动结束恢复原价(卡片消失) 2=显示「售罄」待投放结束
                if (toInt(row.get("soldOutAction")) == 1) {
                    continue;
                }
                status = "soldout";
            } else if (now.before(win[0])) {
                // 未开抢：仅开启预热倒计时且已进入预热窗(开抢前 countdownMinutes 分钟)才展示
                if (toInt(row.get("countdownEnabled")) != 1) {
                    continue;
                }
                long preheatMs = toInt(row.get("countdownMinutes")) * 60L * 1000L;
                if (preheatMs <= 0 || now.getTime() < win[0].getTime() - preheatMs) {
                    continue;
                }
                status = "preheat";
            } else {
                status = "ongoing";
            }
            int stock = toInt(row.get("activityStock"));
            int sold = stock - remaining; // 展示进度用"该窗口已售"口径(每日=当天已售,总量=累计已售)

            Map<String, Object> card = new HashMap<>();
            card.put("activityId", row.get("activityId"));
            card.put("productId", row.get("productId"));
            card.put("bizType", row.get("bizType"));
            card.put("productName", row.get("productName"));
            Object cover = row.get("activityCover");
            if (cover == null || "".equals(String.valueOf(cover).trim())) {
                cover = row.get("productCover");
            }
            card.put("coverUrl", cover);
            card.put("flashSalePrice", row.get("flashSalePrice"));
            card.put("originPrice", row.get("originPrice"));
            card.put("activityStock", stock);
            card.put("soldCount", sold);
            card.put("soldPct", stock > 0 ? Math.min(100, (int) Math.round(sold * 100.0 / stock)) : 0);
            card.put("purchaseLimit", row.get("purchaseLimit"));
            card.put("deliveryType", deliveryType);
            card.put("countdownEnabled", row.get("countdownEnabled"));
            card.put("countdownMinutes", row.get("countdownMinutes"));
            card.put("soldOutAction", row.get("soldOutAction"));
            card.put("startTime", fmt(win[0]));
            card.put("endTime", fmt(win[1]));
            card.put("serverTime", serverTime);
            card.put("status", status);
            result.add(card);
        }
        return result;
    }

    @Override
    public Map<String, Object> checkBuyable(Long activityId, Long productId) {
        Map<String, Object> ap = apiFlashSaleDao.selectActivityProduct(activityId, productId);
        if (ap == null) {
            throw new RRException("秒杀活动不存在或已结束");
        }
        int deliveryType = toInt(ap.get("deliveryType"));
        List<Map<String, Object>> slots = deliveryType == 2 ? apiFlashSaleDao.queryTimeSlots(activityId) : null;
        Date now = new Date();
        Date[] win = resolveWindow(ap, slots, now);
        if (win == null || now.before(win[0])) {
            throw new RRException("秒杀尚未开始");
        }
        if (now.after(win[1])) {
            throw new RRException("秒杀已结束");
        }
        if (computeRemaining(ap, now) <= 0) {
            throw new RRException("手慢了，秒杀已售罄");
        }
        return ap;
    }

    @Override
    public int increaseSold(Long activityId, Long productId, Date orderTime) {
        Integer mode = apiFlashSaleDao.queryStockMode(activityId);
        if (mode != null && mode == 1) {
            // 按下单当天扣当日额度:23:59 下单 00:01 回调不能扣到次日(甚至非生效日)头上
            String soldDate = new SimpleDateFormat(DFMT).format(orderTime != null ? orderTime : new Date());
            apiFlashSaleDao.ensureDailyRow(activityId, productId, soldDate);
            int n = apiFlashSaleDao.increaseDailySold(activityId, productId, soldDate);
            if (n > 0) {
                // 每日额度扣成功后同步累加 sold_count 作累计统计(无上限条件),后台"已售"才有数
                apiFlashSaleDao.increaseTotalSoldStat(activityId, productId);
            }
            return n;
        }
        return apiFlashSaleDao.increaseTotalSold(activityId, productId);
    }

    /* ================= 内部逻辑 ================= */

    /** 解析当前生效窗口 [start,end]；循环下今天非生效日/时段全过 → null */
    private Date[] resolveWindow(Map<String, Object> row, List<Map<String, Object>> slots, Date now) {
        int deliveryType = toInt(row.get("deliveryType"));
        if (deliveryType != 2) {
            Date ws = parseDT(str(row.get("startTime")));
            Date we = parseDT(str(row.get("endTime")));
            if (ws == null || we == null) {
                return null;
            }
            return new Date[]{ws, we};
        }
        // 循环：生效日校验（周一=1..周日=7）
        if (!csvContains(str(row.get("weekDays")), weekdayOf(now))) {
            return null;
        }
        if (slots == null || slots.isEmpty()) {
            return null;
        }
        String today = new SimpleDateFormat(DFMT).format(now);
        Date nextStart = null;
        Date nextEnd = null;
        for (Map<String, Object> slot : slots) {
            Date ss = parseDT(today + " " + str(slot.get("startHm")) + ":00");
            Date se = parseDT(today + " " + str(slot.get("endHm")) + ":00");
            if (ss == null || se == null) {
                continue;
            }
            if (!now.before(ss) && !now.after(se)) {
                return new Date[]{ss, se}; // 当前命中时段=进行中
            }
            if (now.before(ss) && (nextStart == null || ss.before(nextStart))) {
                nextStart = ss;
                nextEnd = se;
            }
        }
        if (nextStart != null) {
            return new Date[]{nextStart, nextEnd}; // 最近未来时段=预热
        }
        return null; // 今天时段已全部结束
    }

    /** 剩余库存：每日方式=每日投放量-当天已售；总量方式=总库存-累计已售 */
    private int computeRemaining(Map<String, Object> row, Date now) {
        int stock = toInt(row.get("activityStock"));
        int mode = toInt(row.get("stockMode"));
        int sold;
        if (mode == 1) {
            String today = new SimpleDateFormat(DFMT).format(now);
            sold = apiFlashSaleDao.queryDailySold(toLong(row.get("activityId")), toLong(row.get("productId")), today);
        } else {
            sold = toInt(row.get("soldCount"));
        }
        return stock - sold;
    }

    /** now 的星期，周一=1 .. 周日=7 */
    private int weekdayOf(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        int d = c.get(Calendar.DAY_OF_WEEK); // 周日=1..周六=7
        return d == Calendar.SUNDAY ? 7 : d - 1;
    }

    private boolean csvContains(String csv, int val) {
        if (csv == null || csv.trim().isEmpty()) {
            return false;
        }
        for (String s : csv.split(",")) {
            if (s.trim().equals(String.valueOf(val))) {
                return true;
            }
        }
        return false;
    }

    private String fmt(Date d) {
        return new SimpleDateFormat(FMT).format(d);
    }

    private Date parseDT(String t) {
        if (t == null || "null".equals(t) || t.trim().isEmpty()) {
            return null;
        }
        String s = t.trim();
        String pattern = s.length() == 16 ? "yyyy-MM-dd HH:mm" : FMT;
        try {
            return new SimpleDateFormat(pattern).parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    private String str(Object o) {
        return o == null ? null : String.valueOf(o);
    }

    private int toInt(Object v) {
        if (v == null) {
            return 0;
        }
        try {
            return Integer.parseInt(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Long toLong(Object v) {
        if (v == null) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(v).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
