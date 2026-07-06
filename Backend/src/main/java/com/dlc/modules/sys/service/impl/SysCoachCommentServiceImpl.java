package com.dlc.modules.sys.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.PtPrivateAppointmentDao;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.entity.PtPrivateAppointmentEntity;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.sys.dao.PtCoachCommentDao;
import com.dlc.modules.sys.dao.PtCoachCommentReplyDao;
import com.dlc.modules.sys.entity.PtCoachCommentEntity;
import com.dlc.modules.sys.entity.PtCoachCommentReplyEntity;
import com.dlc.modules.sys.service.SysCoachCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 教练评价后台 Service 实现（第21步·运营域·评价）。
 * 落 sys.service.impl 命中事务切面（REQUIRED）：reply（插回复+置回复状态）在一个方法内即单事务。
 * 门店隔离：本表无 store_id，全部走 Dao 内 JOIN pt_private_appointment 的 storeIds 过滤。
 * 手动补录复用 api 侧 PtPrivateAppointmentDao/UserInfoMapper 取预约状态与会员快照（跨模块注入为项目惯例）。
 *
 * @author claude
 */
@Service("sysCoachCommentService")
public class SysCoachCommentServiceImpl implements SysCoachCommentService {

    @Autowired
    private PtCoachCommentDao ptCoachCommentDao;
    @Autowired
    private PtCoachCommentReplyDao ptCoachCommentReplyDao;
    @Autowired
    private PtPrivateAppointmentDao ptPrivateAppointmentDao;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public PageUtils queryPage(Query query) {
        List<PtCoachCommentEntity> list = ptCoachCommentDao.queryList(query);
        int total = ptCoachCommentDao.queryTotal(query);
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public PtCoachCommentEntity queryDetail(Long id, String storeIds) {
        PtCoachCommentEntity comment = ptCoachCommentDao.queryDetail(id, storeIds);
        if (comment == null) {
            return null;
        }
        comment.setReplyList(ptCoachCommentReplyDao.listByCommentId(id));
        return comment;
    }

    @Override
    public boolean existsInScope(Long id, String storeIds) {
        return ptCoachCommentDao.countInScope(id, storeIds) > 0;
    }

    @Override
    public void save(PtCoachCommentEntity comment) {
        if (comment.getAppointmentId() == null) {
            throw new RRException("缺少参数：appointmentId");
        }
        Integer score = comment.getScore();
        if (score == null || score < 1 || score > 5) {
            throw new RRException("评分须为1-5星");
        }
        // 校验预约存在且已完成（appointment_status=3）
        PtPrivateAppointmentEntity apt = ptPrivateAppointmentDao.queryObject(comment.getAppointmentId());
        if (apt == null) {
            throw new RRException("预约不存在");
        }
        if (!Integer.valueOf(3).equals(apt.getAppointmentStatus())) {
            throw new RRException(CodeAndMsg.ERROR_APPOINTMENT_NOT_FINISH);
        }
        // coach_id / member_id 从预约取（后台补录不信任前端传值），会员昵称/手机快照查 user_info
        comment.setCoachId(apt.getCoachId());
        comment.setMemberId(apt.getMemberId());
        UserInfo member = userInfoMapper.selectByPrimaryKey(apt.getMemberId());
        if (member != null) {
            comment.setMemberNickname(member.getNickname());
            comment.setMemberMobile(member.getPhone());
        }
        try {
            ptCoachCommentDao.save(comment);
        } catch (DuplicateKeyException e) {
            // 撞 uk_pt_coach_comment_appointment_id：同一预约已评价
            throw new RRException(CodeAndMsg.ERROR_COMMENT_EXISTS);
        }
    }

    @Override
    public void update(PtCoachCommentEntity comment) {
        ptCoachCommentDao.update(comment);
    }

    @Override
    public void deleteBatch(Long[] ids, String storeIds) {
        ptCoachCommentDao.deleteBatch(ids, storeIds);
    }

    @Override
    public void reply(Long commentId, String replyContent, Long replyBy) {
        // 单事务：插回复行 + 置主表 reply_status=1；重复回复追加新行，状态保持 1
        PtCoachCommentReplyEntity reply = new PtCoachCommentReplyEntity();
        reply.setCommentId(commentId);
        reply.setReplyContent(replyContent);
        reply.setReplyBy(replyBy);
        ptCoachCommentReplyDao.save(reply);
        ptCoachCommentDao.updateReplyStatus(commentId);
    }

    @Override
    public void handle(Long commentId, Integer handleStatus, String handleRemark) {
        ptCoachCommentDao.updateHandle(commentId, handleStatus, handleRemark);
    }
}
