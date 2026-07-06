package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.PtMemberWalletDao;
import com.dlc.modules.api.dao.PtMemberWalletFlowDao;
import com.dlc.modules.api.entity.PtMemberWalletEntity;
import com.dlc.modules.api.entity.PtMemberWalletFlowEntity;
import com.dlc.modules.api.service.PtMemberWalletService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * 会员端储值账户 API(第19步资金域)。路径 /api/ptWallet(区别于旧提现钱包 {@link ApiWalletController} 的 /api/wallet)。
 * <p>身份用 BaseController.getUserId/getUserVo;返回 R.reOk(data)/R.reError。</p>
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/ptWallet")
public class ApiPtWalletController extends BaseController {

    @Autowired
    private PtMemberWalletService ptMemberWalletService;
    @Autowired
    private PtMemberWalletDao ptMemberWalletDao;
    @Autowired
    private PtMemberWalletFlowDao ptMemberWalletFlowDao;

    /**
     * 我的储值账户:余额/累计充值/累计消费/状态。无账户返回零值占位(不报错)。
     */
    @RequestMapping("/account")
    public R account(HttpServletRequest request) {
        Long userId = getUserId(request);
        PtMemberWalletEntity wallet = ptMemberWalletDao.selectByMemberId(userId);
        Map<String, Object> data = new HashMap<String, Object>();
        if (wallet == null) {
            data.put("balanceAmount", BigDecimal.ZERO.setScale(2));
            data.put("totalRechargeAmount", BigDecimal.ZERO.setScale(2));
            data.put("totalConsumeAmount", BigDecimal.ZERO.setScale(2));
            data.put("status", 1);
        } else {
            data.put("balanceAmount", wallet.getBalanceAmount());
            data.put("totalRechargeAmount", wallet.getTotalRechargeAmount());
            data.put("totalConsumeAmount", wallet.getTotalConsumeAmount());
            data.put("status", wallet.getStatus());
        }
        return R.reOk(data);
    }

    /**
     * 发起储值充值(走微信,复用支付通道)。入参 amount(>0)。返回微信 App 支付调起参数。
     * 需登录且校验封禁(充值属资金动作)。
     */
    @RequestMapping("/recharge")
    public R recharge(BigDecimal amount, HttpServletRequest request) {
        UserInfoVo user = getUserVo(request);
        SortedMap<String, String> payParams = ptMemberWalletService.recharge(user.getUserId(), amount, request);
        return R.reOk().put("params", payParams);
    }

    /**
     * 我的储值流水(分页,倒序)。入参 page/limit,可选 flowType。
     */
    @RequestMapping("/flowList")
    public R flowList(Integer page, Integer limit, Integer flowType, HttpServletRequest request) {
        Long userId = getUserId(request);
        int p = page == null || page < 1 ? 1 : page;
        int l = limit == null || limit < 1 ? 10 : limit;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("memberId", userId);
        params.put("flowType", flowType);
        params.put("offset", (p - 1) * l);
        params.put("limit", l);

        List<PtMemberWalletFlowEntity> list = ptMemberWalletFlowDao.queryMyFlows(params);
        int total = ptMemberWalletFlowDao.countMyFlows(params);
        if (list == null) {
            list = new ArrayList<PtMemberWalletFlowEntity>();
        }
        PageUtils pageUtil = new PageUtils(list, total, l, p);
        return R.reOk(pageUtil);
    }
}
