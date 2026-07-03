package com.dlc.modules.api.controller;

import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.VipTransferService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * VIP 权益转让(移动端,需登录)。
 * 第9步:试算 quote。第10步:发起 apply、我的记录 myList(到"待审核"为止)。
 * 第11步:受让人确认 confirm(过户单事务生效)。撤回/拒绝/后台审核在后续步骤补。
 */
@RestController
@RequestMapping("/api/vipTransfer")
public class VipTransferController extends BaseController {

    @Autowired
    private VipTransferService vipTransferService;

    /**
     * 试算转让费用(只读,不落单不收费):按本次转让次数命中分档,返回应缴服务费。
     * 入参 vipBenefitId;校验权益属当前用户。
     */
    @RequestMapping("/quote")
    public R quote(Long vipBenefitId, HttpServletRequest request) {
        if (vipBenefitId == null) {
            return R.reError(CodeAndMsg.ERROR_LACK_PARAM);   // 对齐 §5.5 可能返回码:缺参=-1
        }
        Long userId = getUserId(request);
        return R.reOk(vipTransferService.quote(userId, vipBenefitId));
    }

    /**
     * 发起转让(需登录,校验封禁):前置校验 + 在途占用 + 建单。
     * 服务费>0 → 返回 {transferId,status:10,serviceFee,orderNo,paySum},前端调 /wx/proPay 付服务费;
     * 服务费=0 → 返回 {transferId,status:20,serviceFee:0} 直接待审核。
     */
    @RequestMapping("/apply")
    public R apply(Long vipBenefitId, Long toUserId, HttpServletRequest request) {
        if (vipBenefitId == null || toUserId == null) {
            return R.reError(CodeAndMsg.ERROR_LACK_PARAM);
        }
        // 登录 + 封禁校验(未登录/被禁由 getUserVo 抛;其余业务码由 service 抛附录A码)
        UserInfoVo fromUser = getUserVo(request);
        return R.reOk(vipTransferService.apply(fromUser, vipBenefitId, toUserId));
    }

    /**
     * 我的转让/受让记录:role(1我发起 2我接收,不传=全部)、status(可选)、分页。
     */
    @RequestMapping("/myList")
    public R myList(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        Long userId = getUserId(request);
        params.put("userId", userId);
        // role/status 解析为 Integer(供 mapper OGNL 数值比较);无效/缺省则移除,不参与过滤
        putIntOrRemove(params, "role");
        putIntOrRemove(params, "status");
        // Query 要求 page/limit 必须存在,规范成正整数挡脏参
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(vipTransferService.myList(params));
    }

    /**
     * 受让人确认接收(过户入口,不付费):仅受让人本人、仅 status=40 且未超确认截止时间可确认。
     * 命中后单事务完成过户(§7.4):原持有人视角权益转出(user_id 已变更)、受让人获得权益且到期日不变(继承)。
     * 返回 {transferId,status:70,effectTime}。
     */
    @RequestMapping("/confirm")
    public R confirm(Long transferId, HttpServletRequest request) {
        if (transferId == null) {
            return R.reError(CodeAndMsg.ERROR_LACK_PARAM);
        }
        UserInfoVo toUser = getUserVo(request);
        return R.reOk(vipTransferService.confirm(toUser, transferId));
    }

    /**
     * 转让人撤回(§5.8):20待审核→60且退服务费;40待受让人确认→60但不退费。仅转让人本人。
     * 返回 {transferId,status:60}。
     */
    @RequestMapping("/withdraw")
    public R withdraw(Long transferId, HttpServletRequest request) {
        if (transferId == null) {
            return R.reError(CodeAndMsg.ERROR_LACK_PARAM);
        }
        Long fromUserId = getUserId(request);
        return R.reOk(vipTransferService.withdraw(fromUserId, transferId));
    }

    /**
     * 受让人拒绝(§5.10):仅受让人本人、仅 status=40 可拒绝,40→51 并全额退服务费。
     * 返回 {transferId,status:51}。
     */
    @RequestMapping("/reject")
    public R reject(Long transferId, HttpServletRequest request) {
        if (transferId == null) {
            return R.reError(CodeAndMsg.ERROR_LACK_PARAM);
        }
        Long toUserId = getUserId(request);
        return R.reOk(vipTransferService.reject(toUserId, transferId));
    }

    /** 把 key 解析为 Integer 覆盖回 params;null/非数字则移除该键(不参与过滤) */
    private void putIntOrRemove(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null || val.toString().trim().isEmpty()) {
            params.remove(key);
            return;
        }
        try {
            params.put(key, Integer.parseInt(val.toString().trim()));
        } catch (NumberFormatException e) {
            params.remove(key);
        }
    }

    /** 入参解析为正整数;null / 非数字 / <=0 取默认值 */
    private int toPositiveInt(Object val, int defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        try {
            int n = Integer.parseInt(val.toString().trim());
            return n > 0 ? n : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
