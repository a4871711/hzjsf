package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.modules.api.dao.CoachApiDao;
import com.dlc.modules.api.service.PrivateCoachCenterService;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.service.SysPtCoachService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 教练端“我的”实现。 */
@Service("privateCoachCenterService")
public class PrivateCoachCenterServiceImpl implements PrivateCoachCenterService {

    @Autowired
    private CoachApiDao coachApiDao;
    @Autowired
    private SysPtCoachService sysPtCoachService;

    @Override
    public Map<String, Object> mine(Long userId) {
        Long coachId = boundCoachId(userId);
        PtCoachEntity coach = requireCoachEntity(coachId);
        Map<String, Object> result = new HashMap<>();
        result.put("coach", profileOf(coach));
        result.put("workbenchStats", coachApiDao.queryCoachHomeStats(coachId));
        result.put("incomeSummary", coachApiDao.queryCoachIncomeSummary(coachId));
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
    public Map<String, Object> incomeList(Long userId, Integer page, Integer limit, String type) {
        Long coachId = boundCoachId(userId);
        int pageNo = positive(page, 1);
        int pageSize = Math.min(positive(limit, 15), 30);
        String incomeType = normalizeIncomeType(type);
        int offset = (pageNo - 1) * pageSize;

        List<Map<String, Object>> list = coachApiDao.queryCoachIncomeList(
                coachId, incomeType, offset, pageSize);
        int total = coachApiDao.countCoachIncome(coachId, incomeType);

        Map<String, Object> result = new HashMap<>();
        result.put("summary", coachApiDao.queryCoachIncomeSummary(coachId));
        result.put("list", list);
        result.put("total", total);
        result.put("page", pageNo);
        result.put("limit", pageSize);
        result.put("hasMore", offset + list.size() < total);
        return result;
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

    private int positive(Integer value, int defaultValue) {
        return value != null && value > 0 ? value : defaultValue;
    }
}
