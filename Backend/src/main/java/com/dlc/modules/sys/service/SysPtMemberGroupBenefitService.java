package com.dlc.modules.sys.service;

import com.dlc.modules.sys.entity.PtMemberGroupBenefitEntity;
import com.dlc.modules.sys.entity.PtMemberGroupBenefitFlowEntity;

import java.util.List;
import java.util.Map;

/**
 * 会员附赠团课权益 Service（本域只读；写入分散在交易域回调/团课预约/运营域任务，见需求 6.4）。
 *
 * @author claude
 */
public interface SysPtMemberGroupBenefitService {

    PtMemberGroupBenefitEntity queryObject(Long id);

    List<PtMemberGroupBenefitEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    /** 权益详情抽屉：发放/使用/回收/过期流水 */
    List<PtMemberGroupBenefitFlowEntity> queryFlow(Long benefitId);
}
