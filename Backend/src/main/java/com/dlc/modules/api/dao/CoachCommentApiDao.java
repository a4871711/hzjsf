package com.dlc.modules.api.dao;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.PtCoachCommentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 教练评价（移动端）Dao（pt_coach_comment，第21步·运营域）。
 * 会员端提交评价 + 我的评价列表。对应 mapper/api/CoachCommentApiDao.xml。
 * ⚠️ api 目录 XML 改动必须重启 Tomcat（MybatisMapperRefresh 只热刷 mapper/sys/*.xml）。
 *
 * @author claude
 */
@Mapper
@Repository
public interface CoachCommentApiDao {

    /** 友好预检：该预约是否已评价（>0 已评价）；DB uk_pt_coach_comment_appointment_id 兜底 */
    int countByAppointment(@Param("appointmentId") Long appointmentId);

    /** 会员提交评价：撞 uk_pt_coach_comment_appointment_id 由 service 捕获 DuplicateKeyException 转 ERROR_COMMENT_EXISTS */
    int save(PtCoachCommentEntity entity);

    /** 我的评价分页（联教练/门店名 + 后台最新回复），member_id 过滤 */
    List<PtCoachCommentEntity> queryMyList(Query query);

    int countMyList(Query query);
}
