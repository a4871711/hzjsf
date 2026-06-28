package com.dlc.modules.sys.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.modules.sys.dao.VipFeeRuleDao;
import com.dlc.modules.sys.entity.VipFeeRuleEntity;
import com.dlc.modules.sys.service.SysVipFeeRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * VIP 转让费用规则 ServiceImpl
 * 事务由 spring-jdbc.xml 切面统一织入 sys.service.impl 包(默认 REQUIRED),无需 @Transactional
 */
@Service("sysVipFeeRuleService")
public class SysVipFeeRuleServiceImpl implements SysVipFeeRuleService {
    @Autowired
    private VipFeeRuleDao vipFeeRuleDao;

    @Override
    public VipFeeRuleEntity queryObject(Long feeRuleId) {
        return vipFeeRuleDao.queryObject(feeRuleId);
    }

    @Override
    public List<VipFeeRuleEntity> queryList(Map<String, Object> map) {
        return vipFeeRuleDao.queryList(map);
    }

    @Override
    public int queryTotal(Map<String, Object> map) {
        return vipFeeRuleDao.queryTotal(map);
    }

    @Override
    public void save(VipFeeRuleEntity vipFeeRule) {
        validateTiersJson(vipFeeRule.getTiersJson());
        if (vipFeeRule.getStatus() == null) {
            vipFeeRule.setStatus(1);
        }
        vipFeeRuleDao.save(vipFeeRule);
    }

    @Override
    public void update(VipFeeRuleEntity vipFeeRule) {
        validateTiersJson(vipFeeRule.getTiersJson());
        if (vipFeeRule.getStatus() == null) {
            vipFeeRule.setStatus(1);
        }
        vipFeeRuleDao.update(vipFeeRule);
    }

    @Override
    public void deleteBatch(Long[] feeRuleIds) {
        vipFeeRuleDao.deleteBatch(feeRuleIds);
    }

    @Override
    public int countCardRefByRuleId(Long feeRuleId) {
        return vipFeeRuleDao.countCardRefByRuleId(feeRuleId);
    }

    /**
     * 校验 tiers_json:必须是合法 JSON 数组且至少一档;每档 fromCount 为正整数且严格升序(升序+去重)、fee>=0。
     * 任一不满足抛 ERROR_VIP_FEE_RULE_FORMAT,由 RRExceptionHandler 兜底转成 R(code=-70)。
     * 金额一律后端校验,不信前端;运行期 calcTransferFee 直接解析,故此处必须挡住非法配置。
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
        if (tiers == null || tiers.isEmpty()) {
            throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
        }
        int prevFromCount = 0;
        for (JSONObject tier : tiers) {
            BigDecimal fromCountDec;
            BigDecimal fee;
            try {
                fromCountDec = tier.getBigDecimal("fromCount");
                fee = tier.getBigDecimal("fee");
            } catch (Exception e) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            // fromCount 必须为正整数:用 BigDecimal 判，拒绝小数(避免 fastjson getInteger 把 1.9 静默截断成 1)
            if (fromCountDec == null || fromCountDec.signum() <= 0
                    || fromCountDec.stripTrailingZeros().scale() > 0
                    || fee == null || fee.compareTo(BigDecimal.ZERO) < 0) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            int fromCount = fromCountDec.intValue();
            if (fromCount <= prevFromCount) {
                throw new RRException(CodeAndMsg.ERROR_VIP_FEE_RULE_FORMAT);
            }
            prevFromCount = fromCount;
        }
    }
}
