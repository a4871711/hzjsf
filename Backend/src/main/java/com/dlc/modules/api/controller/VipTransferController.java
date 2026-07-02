package com.dlc.modules.api.controller;

import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.VipTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * VIP 权益转让(移动端,需登录)。
 * 第9步先开放试算 quote;发起/撤回/确认/拒绝等状态机接口在第10步补。
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
}
