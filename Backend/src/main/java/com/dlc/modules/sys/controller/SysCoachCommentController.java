package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.PtCoachCommentEntity;
import com.dlc.modules.sys.service.SysCoachCommentService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 教练评价管理（pt_coach_comment，第21步·运营域）。路径 /sys/coachComment。
 * 会员评价的产生在移动端 api（CoachCommentController#submit），后台只做查询/回复/处理/删除/手动补录。
 * ⚠️ 门店隔离：pt_coach_comment 无 store_id，list/统计/详情/写操作前置越权校验一律 JOIN pt_private_appointment
 *    取 store_id 做 storeIds IN 过滤（超管 storeIds 空=看全部）；越权详情/操作一律按 404 处理。
 *
 * perms 与 sql/sys_menu_ops.sql 逐字对齐——菜单仅平铺 list/info/reply/handle/delete 五个按钮权限：
 *   - list/info/delete/reply/handle 各用同名 perm；
 *   - save（手动补录）、update（编辑处理，仅改 handle_status/handle_remark/comment_content）无独立菜单按钮，
 *     语义上属「运营处理」写操作，复用 sys:coachComment:handle 串（后续如需拆细粒度权限，另加菜单按钮即可）。
 *
 * @author claude
 */
@RestController
@RequestMapping("/sys/coachComment")
public class SysCoachCommentController extends AbstractController {

    @Autowired
    private SysCoachCommentService sysCoachCommentService;

    /** 列表：联教练名/门店名（JOIN 预约取 store_id 门店隔离）；筛选教练名/会员昵称手机/评分/回复状态/处理状态/时间区间 */
    @RequestMapping("/list")
    @RequiresPermissions("sys:coachComment:list")
    public R list(@RequestParam Map<String, Object> params) {
        // 门店数据隔离：非超管按所属门店过滤（超管 storeIds 为空则不过滤）
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        Query query = new Query(params);
        PageUtils page = sysCoachCommentService.queryPage(query);
        return R.ok().put("page", page);
    }

    /** 详情（含回复列表 replyList）；越权/不存在按 404 */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:coachComment:info")
    public R info(@PathVariable("id") Long id) {
        PtCoachCommentEntity comment = sysCoachCommentService.queryDetail(id,
                ShiroUtils.getUserEntity().getStoreIds());
        if (comment == null) {
            return R.error(404, "评价记录不存在");
        }
        return R.ok().put("comment", comment);
    }

    /** 手动补录评价：校验预约已完成（3）+ 唯一键拦重复；coach_id/会员快照后端从预约与 user_info 取 */
    @RequestMapping("/save")
    @RequiresPermissions("sys:coachComment:handle")
    public R save(@RequestBody PtCoachCommentEntity comment) {
        sysCoachCommentService.save(comment);
        return R.ok();
    }

    /** 编辑：仅改 handle_status/handle_remark/comment_content（不可改 appointment_id）；先越权判存 */
    @RequestMapping("/update")
    @RequiresPermissions("sys:coachComment:handle")
    public R update(@RequestBody PtCoachCommentEntity comment) {
        if (comment.getId() == null) {
            return R.error("缺少参数：id");
        }
        if (!sysCoachCommentService.existsInScope(comment.getId(),
                ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "评价记录不存在");
        }
        sysCoachCommentService.update(comment);
        return R.ok();
    }

    /** 软删 deleted=1（门店隔离在 SQL 内收口，越权 id 不会被删） */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:coachComment:delete")
    public R delete(@RequestBody Long[] ids) {
        if (ids == null || ids.length == 0) {
            return R.error("缺少参数：ids");
        }
        sysCoachCommentService.deleteBatch(ids, ShiroUtils.getUserEntity().getStoreIds());
        return R.ok();
    }

    /** 回复：{commentId, replyContent}；单事务插回复 + 置主表 reply_status=1。先越权判存 */
    @RequestMapping("/reply")
    @RequiresPermissions("sys:coachComment:reply")
    public R reply(@RequestBody Map<String, Object> params) {
        Long commentId = parseLong(params.get("commentId"));
        String replyContent = params.get("replyContent") == null ? null : params.get("replyContent").toString().trim();
        if (commentId == null) {
            return R.error("缺少参数：commentId");
        }
        if (replyContent == null || replyContent.isEmpty()) {
            return R.error("回复内容不能为空");
        }
        if (!sysCoachCommentService.existsInScope(commentId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "评价记录不存在");
        }
        sysCoachCommentService.reply(commentId, replyContent, getUserId());
        return R.ok();
    }

    /** 处理：{commentId, handleStatus(1已跟进/2已忽略), handleRemark}；先越权判存 */
    @RequestMapping("/handle")
    @RequiresPermissions("sys:coachComment:handle")
    public R handle(@RequestBody Map<String, Object> params) {
        Long commentId = parseLong(params.get("commentId"));
        Integer handleStatus = parseInt(params.get("handleStatus"));
        String handleRemark = params.get("handleRemark") == null ? null : params.get("handleRemark").toString();
        if (commentId == null) {
            return R.error("缺少参数：commentId");
        }
        if (handleStatus == null || (handleStatus != 1 && handleStatus != 2)) {
            return R.error("处理状态须为 1已跟进 或 2已忽略");
        }
        if (!sysCoachCommentService.existsInScope(commentId, ShiroUtils.getUserEntity().getStoreIds())) {
            return R.error(404, "评价记录不存在");
        }
        sysCoachCommentService.handle(commentId, handleStatus, handleRemark);
        return R.ok();
    }

    private Long parseLong(Object val) {
        if (val == null || val.toString().trim().isEmpty()) {
            return null;
        }
        return Long.valueOf(val.toString().trim());
    }

    private Integer parseInt(Object val) {
        if (val == null || val.toString().trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(val.toString().trim());
    }
}
