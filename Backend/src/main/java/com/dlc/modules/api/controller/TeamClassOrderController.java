package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.StudentTeamclassShip;
import com.dlc.modules.api.entity.TeamClass;
import com.dlc.modules.api.service.StudentTeamclassShipService;
import com.dlc.modules.api.service.TeamClassOrderService;
import com.dlc.modules.api.service.TeamClassService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/20/020
 */
@RestController
@RequestMapping("/api/teamClassOrder")
public class TeamClassOrderController extends BaseController{

    @Autowired
    private TeamClassOrderService teamClassOrderService;
    @Autowired
    private TeamClassService teamClassService;
    @Autowired
    private StudentTeamclassShipService studentTeamclassShipService;

    /**
     *  @Auther:YD
     *  @parameters:
     *  团体课订单付费
     */
    @RequestMapping("/createOrder")
    public Map<String,Object> createTeamClassOrder(@RequestParam Map<String,Object> params, HttpServletRequest request){
        Long userId = getUserId(request);
        //判断用户有没有报名该活动
        //根据课程id 查找出所有学员
        //是否是有卡会员（未过期）
        int memFlag = studentTeamclassShipService.selectIfCardUserById(userId);
        if( memFlag == 0 ){return R.reError("团体课预约仅限购卡会员，如您未购卡或已到期，请先购卡");}
        //根据teamclassId和userId查询
        int count = studentTeamclassShipService.selectCountById(Long.valueOf((String) params.get("teamClassId")), userId);
        if( count > 0 ){return R.reError("您已经报名了该课程！");}
        TeamClass teamClass = teamClassService.selectTeamClassInfo(Long.valueOf((String) params.get("teamClassId")));
        params.put("paySum", teamClass.getClassPrice());    //这里用数据库实际金额
        params.put("className",teamClass.getTeamClassName());
        params.put("price",teamClass.getClassPrice());
        params.put("imgUrl",teamClass.getFirstImgUrl());
        params.put("classLabel",teamClass.getClassLabel());
        params.put("storeId",teamClass.getStoreId());
        params.put("energy",teamClass.getEnergy());
        if (teamClass.getActulNum() == teamClass.getTotalNum()){
            return R.reError("该课程报名已满员！");
        }else {
            Map<String,Object> map = teamClassOrderService.createTeamClassOrder(params,userId);
            return R.reOk(map);
        }
    }

    /**
     *  @Auther:YD
     *  @parameters: teamClassId 团体课id，classTime 上课时间  classRoom 上课门店名  classAddress 上课地址
     *  团体课免费
     */
    @RequestMapping("/freeTeamClassOrder")
    public R freeTeamClassOrder(@RequestParam Map<String,Object> params,HttpServletRequest request){

        Long userId = getUserId(request);
        /*List<StudentTeamclassShip> list = studentTeamclassShipService.selectByStoreId(Long.valueOf((String) params.get("teamClassId")));
        for (StudentTeamclassShip sts : list){
            if (sts.getStudentId() == userId){
                return R.reError("您已经报名了该课程！");
            }
        }*/
        //是否是有卡会员（未过期）
        int memFlag = studentTeamclassShipService.selectIfCardUserById(userId);
        if( memFlag == 0 ){return R.reError("团体课预约仅限购卡会员，如您未购卡或已到期，请先购卡");}
        //根据teamclassId和userId查询
        int count = studentTeamclassShipService.selectCountById(Long.valueOf((String) params.get("teamClassId")), userId);
        if( count > 0 ){return R.reError("您已经报名了该课程！");}
        TeamClass teamClass = teamClassService.selectTeamClassInfo(Long.valueOf((String) params.get("teamClassId")));
        if (teamClass.getActulNum() == teamClass.getTotalNum()){
            return R.reError("该课程报名已满员！");
        }else {
            return R.reOk(teamClassOrderService.freeTeamClassOrder(params,userId));
        }
    }
}
