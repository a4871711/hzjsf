package com.dlc.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.modules.sys.dao.VipPauseRuleDao;
import com.dlc.modules.sys.entity.VipPauseRuleEntity;
import com.dlc.modules.sys.service.SysVipPauseRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * VIP 停卡规则 ServiceImpl
 * 事务由 spring-jdbc.xml 切面统一织入 sys.service.impl 包(默认 REQUIRED),无需 @Transactional
 */
@Service("sysVipPauseRuleService")
public class SysVipPauseRuleServiceImpl implements SysVipPauseRuleService {
    @Autowired
    private VipPauseRuleDao vipPauseRuleDao;

    @Override
    public VipPauseRuleEntity queryObject(Long pauseRuleId) {
        return vipPauseRuleDao.queryObject(pauseRuleId);
    }

    @Override
    public List<VipPauseRuleEntity> queryList(Map<String, Object> map) {
        return vipPauseRuleDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return vipPauseRuleDao.queryTotal(map);
    }

    @Override
    public void save(VipPauseRuleEntity vipPauseRule) {
        validateTiersJson(vipPauseRule.getTiersJson());
        if (vipPauseRule.getStatus() == null) {
            vipPauseRule.setStatus(1);
        }
        vipPauseRuleDao.save(vipPauseRule);
    }

    @Override
    public void update(VipPauseRuleEntity vipPauseRule) {
        validateTiersJson(vipPauseRule.getTiersJson());
        if (vipPauseRule.getStatus() == null) {
            vipPauseRule.setStatus(1);
        }
        vipPauseRuleDao.update(vipPauseRule);
    }

    @Override
    public void deleteBatch(Long[] pauseRuleIds) {
        vipPauseRuleDao.deleteBatch(pauseRuleIds);
    }

    @Override
    public int countCardRefByRuleId(Long pauseRuleId) {
        return vipPauseRuleDao.countCardRefByRuleId(pauseRuleId);
    }

    /**
     * 校验 tiers_json:必须是合法 JSON 数组且 1~10 档;每档 days 为 1~365 的正整数且各档 days 不重复、price>0。
     * 任一不满足抛 ERROR_VIP_FEE_RULE_FORMAT,由 RRExceptionHandler 兜底转成 R(code=-70)。
     * 金额一律后端校验,不信前端;运行期按 days 匹配档位直接解析,故此处必须挡住非法配置。
     */
    private void validateTiersJson(String tiersJson) {
        if (tiersJson == null || tiersJson.trim().isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
        }
        List<JSONObject> tiers;
        try {
            tiers = JSON.parseArray(tiersJson, JSONObject.class);
        } catch (Exception e) {
            throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
        }
        if (tiers == null || tiers.isEmpty() || tiers.size() > 10) {
            throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
        }
        Set<Integer> daysSet = new HashSet<>();
        for (JSONObject tier : tiers) {
            BigDecimal daysDec;
            BigDecimal price;
            try {
                daysDec = tier.getBigDecimal("days");
                price = tier.getBigDecimal("price");
            } catch (Exception e) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            // days 必须为 1~365 的正整数:用 BigDecimal 判，拒绝小数(避免 fastjson getInteger 把 1.9 静默截断成 1)
            if (daysDec == null || daysDec.signum() <= 0
                    || daysDec.stripTrailingZeros().scale() > 0
                    || price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            // days 上限 365:直接用 BigDecimal 比较,避免 intValue() 对 ≥2^31 的值回绕截断绕过上限
            if (daysDec.compareTo(BigDecimal.valueOf(365)) > 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            // 此处 days 必为 1~365(上面已校验正整数且不超上限),intValue 安全,用于各档去重
            if (!daysSet.add(daysDec.intValue())) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
        }
    }
}
