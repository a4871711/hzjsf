package com.dlc.modules.sys.service;

import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.sys.entity.PtCoachEntity;
import com.dlc.modules.sys.entity.PtCoachMonthlyCommissionRuleEntity;

import java.util.List;
import java.util.Map;

/**
 * 私教教练 Service（pt_coach，与旧 SysCoachService 并存不复用）
 *
 * @author claude
 */
public interface SysPtCoachService {

    PtCoachEntity queryObject(Long id);

    List<PtCoachEntity> queryList(Map<String, Object> map);

    int queryTotal(Map<String, Object> map);

    void save(PtCoachEntity entity);

    void update(PtCoachEntity entity);

    void deleteBatch(Long[] ids);

    void changeStatus(Long id, Integer status, String disableReason);

    /** 教练绑定会员账号的远程搜索候选 */
    List<Map<String, Object>> queryMemberOptions(String keyword, String storeIds);

    /** 绑定/解绑手机端会员账号 */
    void bindMember(Long coachId, Long userId, Long operatorId, String storeIds);

    /** 教练详情只读抽屉:该教练最近预约(第14步接入 pt_private_appointment 回填) */
    List<PtPrivateAppointmentEntity> queryRecentAppointments(Long coachId);

    /** 包月课程提成配置使用的商品候选。 */
    List<PtCoachMonthlyCommissionRuleEntity> queryMonthlyProductOptions();
}
