package com.dlc.modules.sys.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.PtCoachCommentEntity;

/**
 * 教练评价后台 Service（第21步·运营域·评价）。
 * 落 sys.service.impl，命中事务切面（REQUIRED）；reply（插回复+置回复状态）为单事务。
 * 门店隔离：list/统计/详情/写操作前置越权校验均 JOIN pt_private_appointment 取 store_id。
 *
 * @author claude
 */
public interface SysCoachCommentService {

    /** 分页列表（联教练/门店名，JOIN 预约取 store_id 做门店隔离） */
    PageUtils queryPage(Query query);

    /** 详情（含回复列表 replyList）；storeIds 越权/不存在返回 null → 404 */
    PtCoachCommentEntity queryDetail(Long id, String storeIds);

    /** 门店范围内评价判存（update/handle/delete 前越权校验） */
    boolean existsInScope(Long id, String storeIds);

    /** 后台手动补录评价：校验预约已完成（3）；撞唯一键 → ERROR_COMMENT_EXISTS；member 昵称/手机快照回填 */
    void save(PtCoachCommentEntity comment);

    /** 仅改 handle_status/handle_remark/comment_content */
    void update(PtCoachCommentEntity comment);

    /** 软删 deleted=1（门店隔离） */
    void deleteBatch(Long[] ids, String storeIds);

    /** 回复：单事务插 pt_coach_comment_reply + 置主表 reply_status=1 */
    void reply(Long commentId, String replyContent, Long replyBy);

    /** 处理：标记已跟进（1）/已忽略（2）+ 写处理备注 */
    void handle(Long commentId, Integer handleStatus, String handleRemark);
}
