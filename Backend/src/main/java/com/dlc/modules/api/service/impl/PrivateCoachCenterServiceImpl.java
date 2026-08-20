package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.dao.CoachWithdrawalDao;
import com.dlc.modules.api.entity.PtCoachWithdrawalEntity;
import com.dlc.modules.api.service.PrivateCoachCenterService;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;
import java.util.List;
import java.util.Map;

/** 教练端“我的”实现。 */
@Service("privateCoachCenterService")
public class PrivateCoachCenterServiceImpl implements PrivateCoachCenterService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter
            .ofPattern("uuuu-MM")
            .withResolverStyle(ResolverStyle.STRICT);

    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private CoachWithdrawalDao coachWithdrawalDao;
    @Autowired
    private SysPtCoachService sysPtCoachService;

    @Override
    public Map<String, Object> mine(Long userId) {
        Long coachId = boundCoachId(userId);
        PtCoachEntity coach = requireCoachEntity(coachId);
        Map<String, Object> result = new HashMap<>();
        result.put("coach", profileOf(coach));
        result.put("workbenchStats", coachApiDao.queryCoachHomeStats(coachId));
        YearMonth currentMonth = YearMonth.now();
        result.put("incomeSummary", coachApiDao.queryCoachIncomeSummary(
                coachId, monthStart(currentMonth), nextMonthStart(currentMonth)));
        return result;
    }

    @Override
    public void updateProfile(Long userId, String coachName, String mobile, Integer gender,
                              String avatarUrl, String intro) {
        Long coachId = boundCoachId(userId);
        if (StringUtils.isBlank(coachName)) {
            throw new RRException("教练姓名不能为空");
        }
        if (StringUtils.isBlank(mobile)) {
            throw new RRException("手机号不能为空");
        }
        if (gender != null && gender != 0 && gender != 1 && gender != 2) {
            throw new RRException("性别参数非法");
        }
        PtCoachEntity update = new PtCoachEntity();
        update.setId(coachId);
        update.setCoachName(coachName);
        update.setMobile(mobile);
        update.setGender(gender == null ? 0 : gender);
        update.setAvatarUrl(avatarUrl == null ? "" : avatarUrl.trim());
        update.setIntro(intro == null ? "" : intro.trim());
        update.setUpdatedBy(userId);
        // 复用后台 Service 的手机号格式、唯一性和教练状态校验。
        sysPtCoachService.update(update);
    }

    @Override
    public Map<String, Object> incomeList(Long userId, Integer page, Integer limit,
                                          String type, String month) {
        Long coachId = boundCoachId(userId);
        int pageNo = positive(page, 1);
        int pageSize = Math.min(positive(limit, 15), 30);
        String incomeType = normalizeIncomeType(type);
        YearMonth selectedMonth = normalizeIncomeMonth(month);
        String monthStart = monthStart(selectedMonth);
        String nextMonthStart = nextMonthStart(selectedMonth);
        int offset = (pageNo - 1) * pageSize;

        List<Map<String, Object>> list = coachApiDao.queryCoachIncomeList(
                coachId, incomeType, monthStart, nextMonthStart, offset, pageSize);
        int total = coachApiDao.countCoachIncome(
                coachId, incomeType, monthStart, nextMonthStart);

        Map<String, Object> result = new HashMap<>();
        result.put("summary", incomeSummary(coachId, selectedMonth));
        result.put("list", list);
        result.put("total", total);
        result.put("page", pageNo);
        result.put("limit", pageSize);
        result.put("hasMore", offset + list.size() < total);
        return result;
    }

    @Override
    public Map<String, Object> withdrawalList(Long userId, Integer page, Integer limit, String month) {
        Long coachId = boundCoachId(userId);
        YearMonth selectedMonth = normalizeIncomeMonth(month);
        int pageNo = positive(page, 1);
        int pageSize = Math.min(positive(limit, 10), 30);
        int offset = (pageNo - 1) * pageSize;
        List<Map<String, Object>> list = coachWithdrawalDao.queryCoachList(coachId, offset, pageSize);
        for (Map<String, Object> item : list) {
            item.put("bankCardNo", maskBankCard(item.get("bankCardNo")));
        }
        int total = coachWithdrawalDao.countCoachList(coachId);
        Map<String, Object> result = new HashMap<>();
        result.put("summary", incomeSummary(coachId, selectedMonth));
        result.put("list", list);
        result.put("total", total);
        result.put("page", pageNo);
        result.put("limit", pageSize);
        result.put("hasMore", offset + list.size() < total);
        return result;
    }

    @Override
    public void applyWithdrawal(Long userId, BigDecimal amount, String accountName,
                                String bankName, String bankCardNo) {
        Long coachId = boundCoachId(userId);
        if (StringUtils.isBlank(accountName)) {
            throw new RRException("收款人姓名不能为空");
        }
        if (StringUtils.isBlank(bankName)) {
            throw new RRException("开户行名称不能为空");
        }
        String normalizedCardNo = normalizeBankCardNo(bankCardNo);
        PtCoachWithdrawalAmountPolicy.requirePositive(amount, "提现金额");

        // 锁教练主表而不是只锁余额查询结果，保证两个并发提现不会同时消费同一可用余额。
        if (coachWithdrawalDao.lockCoach(coachId) == null) {
            throw new RRException("教练不存在");
        }
        Map<String, Object> balance = coachWithdrawalDao.queryBalance(coachId);
        BigDecimal available = availableAmount(balance);
        PtCoachWithdrawalAmountPolicy.validateRequestedAmount(amount, available);

        PtCoachWithdrawalEntity entity = new PtCoachWithdrawalEntity();
        entity.setCoachId(coachId);
        entity.setRequestedAmount(money(amount));
        entity.setSettlementAmount(money(amount));
        entity.setFrozenAmount(money(amount));
        entity.setAccountName(accountName.trim());
        entity.setBankName(bankName.trim());
        entity.setBankCardNo(normalizedCardNo);
        entity.setStatus(0);
        entity.setCreatedAt(new Date());
        coachWithdrawalDao.save(entity);
    }

    private Map<String, Object> incomeSummary(Long coachId, YearMonth selectedMonth) {
        Map<String, Object> summary = coachApiDao.queryCoachIncomeSummary(
                coachId, monthStart(selectedMonth), nextMonthStart(selectedMonth));
        Map<String, Object> balance = coachWithdrawalDao.queryBalance(coachId);
        BigDecimal totalIncome = requiredAmount(summary.get("totalIncome"), "累计收入");
        BigDecimal settledAmount = requiredAmount(balance.get("settledAmount"), "已结算金额");
        BigDecimal frozenAmount = requiredAmount(balance.get("frozenAmount"), "冻结金额");
        summary.put("incomeTotal", totalIncome);
        summary.put("settledAmount", settledAmount);
        summary.put("frozenAmount", frozenAmount);
        summary.put("selectedMonth", selectedMonth.format(MONTH_FORMATTER));
        summary.put("availableAmount", PtCoachWithdrawalAmountPolicy.availableAmount(
                totalIncome, settledAmount, frozenAmount));
        return summary;
    }

    private BigDecimal availableAmount(Map<String, Object> balance) {
        if (balance == null) {
            throw new RRException("提现余额数据异常");
        }
        return PtCoachWithdrawalAmountPolicy.availableAmount(
                requiredAmount(balance.get("incomeTotal"), "累计收入"),
                requiredAmount(balance.get("settledAmount"), "已结算金额"),
                requiredAmount(balance.get("frozenAmount"), "冻结金额"));
    }

    private BigDecimal requiredAmount(Object value, String fieldName) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).setScale(2, RoundingMode.HALF_UP);
        }
        if (value instanceof Number) {
            return new BigDecimal(value.toString()).setScale(2, RoundingMode.HALF_UP);
        }
        throw new RRException(fieldName + "数据异常");
    }

    private BigDecimal money(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String normalizeBankCardNo(String bankCardNo) {
        if (StringUtils.isBlank(bankCardNo)) {
            throw new RRException("银行卡号不能为空");
        }
        String normalized = bankCardNo.replace(" ", "").replace("-", "");
        if (!normalized.matches("\\d{12,19}")) {
            throw new RRException("银行卡号格式不正确");
        }
        return normalized;
    }

    private String maskBankCard(Object value) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "";
        }
        String card = value.toString();
        return card.length() <= 4 ? card : "****" + card.substring(card.length() - 4);
    }

    /** 手机端教练身份只认 pt_coach.user_id 绑定，不允许客户端传 coachId。 */
    private Long boundCoachId(Long userId) {
        Map<String, Object> coach = coachApiDao.queryBoundCoachByUserId(userId);
        if (coach == null) {
            throw new RRException("当前账号未绑定正常状态的私教");
        }
        Object id = coach.get("id");
        if (!(id instanceof Number)) {
            throw new RRException("教练身份数据异常");
        }
        return ((Number) id).longValue();
    }

    private PtCoachEntity requireCoachEntity(Long coachId) {
        PtCoachEntity coach = sysPtCoachService.queryObject(coachId);
        if (coach == null || Integer.valueOf(1).equals(coach.getDeleted())) {
            throw new RRException("教练不存在");
        }
        return coach;
    }

    /** 只返回教练本人需要的字段，避免把后台审计字段和绑定信息暴露给页面。 */
    private Map<String, Object> profileOf(PtCoachEntity coach) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", coach.getId());
        profile.put("coachNo", coach.getCoachNo());
        profile.put("coachName", coach.getCoachName());
        profile.put("mobile", coach.getMobile());
        profile.put("gender", coach.getGender());
        profile.put("avatarUrl", coach.getAvatarUrl());
        profile.put("coachLevel", coach.getCoachLevel());
        profile.put("intro", coach.getIntro());
        profile.put("storeNames", coach.getStoreNames());
        return profile;
    }

    private String normalizeIncomeType(String type) {
        if (StringUtils.isBlank(type)) {
            return null;
        }
        String value = type.trim().toLowerCase();
        if (!"lesson".equals(value) && !"sale".equals(value)) {
            throw new RRException("收入类型参数非法");
        }
        return value;
    }

    private YearMonth normalizeIncomeMonth(String month) {
        if (StringUtils.isBlank(month)) {
            return YearMonth.now();
        }
        try {
            return YearMonth.parse(month.trim(), MONTH_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RRException("收入月份参数非法，正确格式为yyyy-MM");
        }
    }

    private String monthStart(YearMonth month) {
        return month.atDay(1).toString();
    }

    private String nextMonthStart(YearMonth month) {
        return month.plusMonths(1).atDay(1).toString();
    }

    private int positive(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }
}
