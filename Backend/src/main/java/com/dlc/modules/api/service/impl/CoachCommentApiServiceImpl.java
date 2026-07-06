package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachCommentApiDao;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.PtCoachCommentEntity;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.CoachCommentApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 教练评价（移动端）Service 实现（第21步·运营域·评价）。
 * 落 api.service.impl 命中事务切面（REQUIRED）。
 * ⚠️ 关联的 mapper/api/CoachCommentApiDao.xml 改动须重启 Tomcat。
 *
 * @author claude
 */
@Service("coachCommentApiService")
public class CoachCommentApiServiceImpl implements CoachCommentApiService {

    @Autowired
    private CoachCommentApiDao coachCommentApiDao;
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void submit(Long userId, Long appointmentId, Integer score, String commentContent) {
        if (userId == null || appointmentId == null) {
            throw new RRException(CodeAndMsg.ERROR_LACK_PARAM);
        }
        if (score == null || score < 1 || score > 5) {
            throw new RRException("评分须为1-5星");
        }
        // 预约存在校验
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.queryObject(appointmentId);
        if (apt == null) {
            throw new RRException("预约不存在");
        }
        // 归属校验：非本人 → ERROR_APPOINTMENT_NOT_OWNER
        if (!userId.equals(apt.getMemberId())) {
            throw new RRException(CodeAndMsg.ERROR_APPOINTMENT_NOT_OWNER);
        }
        // 已完成校验：仅 appointment_status=3 可评价
        if (!Integer.valueOf(3).equals(apt.getAppointmentStatus())) {
            throw new RRException(CodeAndMsg.ERROR_APPOINTMENT_NOT_FINISH);
        }
        // 一预约一评价：先查友好提示（DB 唯一键兜底见下）
        if (coachCommentApiDao.countByAppointment(appointmentId) > 0) {
            throw new RRException(CodeAndMsg.ERROR_COMMENT_EXISTS);
        }
        PtCoachCommentEntity comment = new PtCoachCommentEntity();
        comment.setAppointmentId(appointmentId);
        comment.setCoachId(apt.getCoachId());   // coach_id 从预约取，不信任前端
        comment.setMemberId(userId);
        comment.setScore(score);
        comment.setCommentContent(commentContent);
        // 会员昵称/手机快照（避免会员改名后历史失真）
        UserInfo member = userInfoMapper.selectByPrimaryKey(userId);
        if (member != null) {
            comment.setMemberNickname(member.getNickname());
            comment.setMemberMobile(member.getPhone());
        }
        try {
            coachCommentApiDao.save(comment);
        } catch (DuplicateKeyException e) {
            // 并发下撞 uk_pt_coach_comment_appointment_id：DB 兜底
            throw new RRException(CodeAndMsg.ERROR_COMMENT_EXISTS);
        }
    }

    @Override
    public PageUtils myList(Map<String, Object> params) {
        Query query = new Query(params);
        List<PtCoachCommentEntity> list = coachCommentApiDao.queryMyList(query);
        int total = coachCommentApiDao.countMyList(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }
}
