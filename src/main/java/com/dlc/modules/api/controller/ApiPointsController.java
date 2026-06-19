package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.PointsExchange;
import com.dlc.modules.api.service.PointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author lnx
 * @Date 2018-09-15
 */
@RestController
@RequestMapping("/api/points")
public class ApiPointsController extends BaseController{

    @Autowired
    private PointsService pointsService;
    /**
     * 1.查询积分和可兑换余额
     */
    @RequestMapping("/queryMyPoints")
    public R queryMyPoints(HttpServletRequest request){
        return R.reOk(pointsService.queryMyPoints(getUserId(request)));
    }

    /**
     * 积分明细查询
     * @param request
     * @return
     */
    @RequestMapping("/queryPointsExchange")
    public R queryPointsExchange(HttpServletRequest request){
        return R.reOk(pointsService.queryPointsExchange(getUserId(request)));
    }

    /**
     * 积分兑换
     * @return
     */
    @RequestMapping("/exchangePoints")
    public R exchangePoints(PointsExchange pointsExchange, HttpServletRequest request){
        /**getUserId(request)测试时先给固定值*/
        return pointsService.pointsExchange(pointsExchange, getUserId(request));
    }
}
