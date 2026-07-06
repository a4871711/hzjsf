package com.dlc.modules.sys.dao;

import com.dlc.modules.sys.entity.PtCoachCommentReplyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教练评价回复后台 Dao（pt_coach_comment_reply，第21步）。
 * 对应 mapper/sys/PtCoachCommentReplyDao.xml（sys 目录热刷新免重启）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface PtCoachCommentReplyDao {

    /** 插回复行（回复人 reply_by=当前 sys 用户；reply_time 由 DDL 默认值维护） */
    int save(PtCoachCommentReplyEntity entity);

    /** 某评价的全部回复（详情用），按回复时间升序 */
    List<PtCoachCommentReplyEntity> listByCommentId(@Param("commentId") Long commentId);
}
