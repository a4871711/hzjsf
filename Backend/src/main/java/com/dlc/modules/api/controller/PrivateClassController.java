package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.PrivateClassService;
import com.dlc.modules.api.service.StudentTeamclassShipService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@RestController
@RequestMapping("/api/pClass")
public class PrivateClassController extends BaseController{

    @Autowired
    private PrivateClassService privateClassService;
    @Autowired
    private StudentTeamclassShipService studentTeamclassShipService;
    /**
     *  @Auther:YD
     *  @parameters: 教练id： coachId
     *  私教课程列表
     */
    @RequestMapping("/courseList")
    public R courseList(Long coachId){
        return R.reOk(privateClassService.courseList(coachId));
    }

    /**
     *  @Auther:YD
     *  @parameters: 教练id：coachId ，课程id：privateClassId
     *  私教课程详情
     */
    @RequestMapping("/courseInfo")
    public R courseInfo(@RequestParam Map<String,Object> params){
        Map<String,Object> map = new HashMap<>();
        if (privateClassService.courseInfo(params) != null){
            map = privateClassService.courseInfo(params);
        }
        return R.reOk(map);
    }

    /**
     *  @Auther:YD
     *  @parameters:教练id:coachId,私教课id：privateClassId,购买实时课数：buyCount，订单主图：imgUrl,总价：paySum,
     *  (选传）第一次上课时间：classTime地点:classRoom 课程单价price 课程名称className
     *  私教课程订单
     */
    @RequestMapping("/createCourseOrder")
    public R createCourseOrder(@RequestParam Map<String,Object> params, HttpServletRequest request){
        if(StringUtils.isBlank((String) params.get("paySum"))){
            return  R.error("金额不能为空！");
        }
        BigDecimal paySum = new BigDecimal((String) params.get("paySum"));
        if(paySum.compareTo(BigDecimal.ZERO) == -1){
            return  R.error("金额不能为负数！该交易存在恶意攻击风险, 已被标记");
        }
        UserInfoVo user = getUserVo(request);
        //是否是有卡会员（未过期）
        int memFlag = studentTeamclassShipService.selectIfCardUserById(user.getUserId());
        if( memFlag == 0 ){return R.reError("私教课仅限购卡会员，如您未购卡或已到期，请先购卡");}
        //判断当前会员是否已经购买过体验课，如果买过不可以下单
        int res = privateClassService.queryIsOrBuyTyk(user.getUserId(),Long.valueOf((String) params.get("privateClassId")));
        if(res > 0){
            return R.reError("您已购买过体验课, 请选购其他类型私教课程");
        }
        Map<String,Object> map = privateClassService.createCourseOrder(params,user);
        return R.reOk(map);
    }
}
