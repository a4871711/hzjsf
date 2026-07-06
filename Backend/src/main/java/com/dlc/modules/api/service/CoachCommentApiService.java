package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;

import java.util.Map;

/**
 * 教练评价（移动端）Service（第21步·运营域·评价）。
 * 落 api.service.impl，命中事务切面（REQUIRED）。会员在私教课完成后写评价。
 *
 * @author claude
 */
public interface CoachCommentApiService {

    /**
     * 会员提交评价。校验链：预约存在 → 属本人（member_id=userId，否则 ERROR_APPOINTMENT_NOT_OWNER）
     * → appointment_status=3 已完成（否则 ERROR_APPOINTMENT_NOT_FINISH）→ 一预约一评价
     * （先查友好提示 + 撞 uk_pt_coach_comment_appointment_id DB 兜底，均转 ERROR_COMMENT_EXISTS）。
     * 写入落 coach_id（从预约取）、member_nickname/member_mobile 快照（查 user_info）、score(1-5)/comment_content。
     */
    void submit(Long userId, Long appointmentId, Integer score, String commentContent);

    /** 我的评价分页（含后台回复），member_id 过滤 */
    PageUtils myList(Map<String, Object> params);
}
