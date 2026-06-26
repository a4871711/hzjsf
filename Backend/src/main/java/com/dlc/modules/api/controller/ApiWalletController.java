package com.dlc.modules.api.controller;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.CouponMapper;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.WalletService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 钱包
 *
 * @author lnx
 * @Date 2018-09-17
 */
@RestController
@RequestMapping("/api/wallet")
public class ApiWalletController extends BaseController {

    @Autowired
    private WalletService walletService;

    /**
     * 查询个人钱包总余额
     */
    @RequestMapping("/queryMyWallet")
    public R queryMyWallet(HttpServletRequest request) {
        /**getUserId(request)测试时先固定值*/
        return R.reOk(walletService.queryMyWallet(getUserId(request)));
    }

    /**
     * 交易"明细"查询
     *
     * @param request
     * @return
     */
    @RequestMapping("/queryMyTradeDetail")
    public R queryMyTradeDetail(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if(StringUtils.isEmpty((String) params.get("page")) || StringUtils.isEmpty((String) params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = walletService.queryMyTradeDetail(params);
        return R.reOk(pageUtil);
    }

    /**
     * 个人优惠券查询
     *
     * @param request
     * @return
     */
    @RequestMapping("/queryMyCoupon")
    public R queryMyCoupon(@RequestParam Map<String, Object> params, HttpServletRequest request) {
        if(StringUtils.isEmpty((String) params.get("page")) || StringUtils.isEmpty((String) params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = walletService.queryMyCoupon(params);
        return R.reOk(pageUtil);
    }

    /**
     * 提现设置
     *
     * @param request
     * @return
     */
    @RequestMapping("/getMoneySetting")
    public R getMoneySetting(@RequestParam Map<String, Object> updateMap, HttpServletRequest request) {
        return R.reOk(walletService.setAliAcccount(updateMap));
    }
    /**
     * 提现账号信息查询
     *
     * @param request
     * @return
     */
    @RequestMapping("/querySetting")
    public R querySetting(HttpServletRequest request) {
        return R.reOk(walletService.queryWalletSetting(getUserId(request)));
    }

}
