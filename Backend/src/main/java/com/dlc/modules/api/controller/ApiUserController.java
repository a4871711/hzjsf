package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.UserCentreService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 个人中心
 *
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-10 21:04:23
 */
@RestController
@RequestMapping("/api/user")
public class ApiUserController extends BaseController{

    @Autowired
    private UserCentreService userCentreService;

    /**
     *查询个人中心消息
     */
    @RequestMapping("/queryMyCentre")
    public R queryMyCentre(Long userId, HttpServletRequest request){
        if(null == userId){
            return R.reError("查询参数缺失");
        }
        return R.reOk(userCentreService.queryUserInfo(userId, getUserId(request)));
    }

    /**
     * 等級查詢
     * @param request
     * @return
     */
    @RequestMapping("/queryMyLevel")
    public R queryMyLevel(HttpServletRequest request){
        return R.reOk(userCentreService.queryUserLevel(getUserId(request)));
    }

    /**
     * 查詢個人系統消息
     * @param request
     * @return
     */
    @RequestMapping("/querySystemMsg")
    public R querySystemMsg(HttpServletRequest request){
        return userCentreService.querySystemMsg(getUserId(request));
    }
    /**
     * 查詢未读消息（系统）
     * @param request
     * @return
     */
    @RequestMapping("/querySysMsgUnread")
    public R querySysMsgUnread(HttpServletRequest request){
        return R.reOk(userCentreService.querySystemMsgCount(getUserId(request)));
    }

    /**
     * 查询团体课
     * @param request
     * @return
     */
    @RequestMapping("/queryMyTeamClass")
    public R queryMyTeamClass(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = userCentreService.queryTeamClass(params);
        return R.reOk(pageUtil);
    }

    /**
     * 查询私教课
     * @param request
     * @return
     */
    @RequestMapping("/queryMyPrivateClass")
    public R queryMyPrivateClass(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = userCentreService.queryMyPrivateClass(params);
        return R.reOk(pageUtil);
    }

    /**
     * 查询我的约课
     * @param request
     * @return
     */
    @RequestMapping("/queryAppointClass")
    public R queryAppointClass(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = userCentreService.queryAppointClass(params);
        return R.reOk(pageUtil);
    }

    /**
     *查询所有订单列表
     * @param request
     * @return
     */
    @RequestMapping("/queryMyOrders")
    public R queryMyOrders(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("pagesize"))){
            return R.reError("分页信息不能为空");
        }
        if(StringUtils.isEmpty(params.get("orderType"))){
            return R.reError("查询类别不明确");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = userCentreService.queryMyOrders(params);
        return R.reOk(pageUtil);
    }
    /**
     *取消订单
     * @param request
     * @return
     */
    @RequestMapping("/orderCancel")
    public R orderCancel(@RequestParam String orderNo, int orderType, HttpServletRequest request){
        if(StringUtils.isEmpty(orderNo)){
            return R.reError("订单编号不能为空");
        }
        return userCentreService.orderCancel(getUserId(request), orderNo, orderType);
    }

    /**
     * 确认订单
     * @param orderNo
     * @param request
     * @return
     */
    @RequestMapping("/orderConfirm")
    public R orderConfirm(@RequestParam String orderNo, HttpServletRequest request){
        if(StringUtils.isEmpty(orderNo)){
            return R.reError("订单编号不能为空");
        }
        return userCentreService.orderConfirm(getUserId(request), orderNo);
    }
    /**
     *删除订单（商城）物理删除
     * @param request
     * @return
     */
    @RequestMapping("/orderDel")
    public R orderDel(@RequestParam String orderNo, HttpServletRequest request){
        if(StringUtils.isEmpty(orderNo)){
            return R.reError("订单编号不能为空");
        }
        return userCentreService.orderDel(getUserId(request), orderNo);
    }

    /**
     * 查询我的订单详情
     * @param orderNo
     * @param orderType
     * @param request
     * @return
     */
    @RequestMapping("/queryMyOrderDetail")
    public R queryMyOrderDetail(@RequestParam String orderNo, int orderType, HttpServletRequest request){
        return R.reOk(userCentreService.queryMyOrderDetail(getUserId(request), orderNo, orderType));
    }

    /**
     * 查询我的健身卡
     * @param request
     * @return
     */
    @RequestMapping("/queryMyCard")
    public R queryMyCard(HttpServletRequest request){
        UserInfoVo userVo = getUserVo(request);
        Long userId = null;
        if (userVo != null) {
            userId = userVo.getUserId();
        }
        return R.reOk(userCentreService.queryMyCard(userId));
    }

    /**
     * 查询我的设备
     * @param request
     * @return
     */
    @RequestMapping("/queryMyDevice")
    public R queryMyDevice(HttpServletRequest request){
        return R.reOk(userCentreService.queryMyDevice(getUserId(request)));
    }

    /**
     * 查询兴趣标签
     * @param request
     * @return
     */
    @RequestMapping("/queryHobbies")
    public R queryHobbies(HttpServletRequest request){
        return R.reOk(userCentreService.queryHobbies());
    }
}
