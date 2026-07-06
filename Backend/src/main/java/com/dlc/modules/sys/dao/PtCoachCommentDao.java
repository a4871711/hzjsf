package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 教练评价后台 Dao（pt_coach_comment，第21步）。
 * 本表无 store_id：列表/统计/详情/判存一律 JOIN pt_private_appointment 取 store_id 做门店隔离，
 * storeIds 为逗号分隔串，空=超管不过滤。对应 mapper/sys/PtCoachCommentDao.xml（sys 目录热刷新免重启）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachCommentDao {

    /** 分页列表：联教练名/门店名（JOIN 预约取 store_id）；门店隔离 storeIds 空=超管不过滤 */
    List<PtCoachCommentEntity> queryList(Map<String, Object> params);

    int queryTotal(Map<String, Object> params);

    /** 详情（含 store_id）；storeIds 过滤在 SQL 内收口，越权/不存在返回 null → 404 */
    PtCoachCommentEntity queryDetail(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 门店范围内评价判存（update/handle/delete 前越权校验）：0=不存在或不在管辖门店 */
    int countInScope(@Param("id") Long id, @Param("storeIds") String storeIds);

    /** 后台手动补录：插入撞 uk_pt_coach_comment_appointment_id 由 service 捕获 DuplicateKeyException 转 ERROR_COMMENT_EXISTS */
    int save(PtCoachCommentEntity entity);

    /** 仅允许改 handle_status/handle_remark/comment_content（差评处理），不可改 appointment_id */
    int update(PtCoachCommentEntity entity);

    /** 软删 deleted=1（门店隔离 storeIds 空=超管不过滤） */
    int deleteBatch(@Param("ids") Long[] ids, @Param("storeIds") String storeIds);

    /** 回复后置 reply_status=1（配合 reply 单事务） */
    int updateReplyStatus(@Param("id") Long id);

    /** handle：更新处理状态 + 处理备注 */
    int updateHandle(@Param("id") Long id, @Param("handleStatus") Integer handleStatus,
                     @Param("handleRemark") String handleRemark);
}
