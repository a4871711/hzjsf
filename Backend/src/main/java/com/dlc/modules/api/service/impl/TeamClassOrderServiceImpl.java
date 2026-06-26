package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.dao.StudentTeamclassShipMapper;
import com.dlc.modules.api.entity.StudentTeamclassShip;
import com.dlc.modules.api.entity.TeamClass;
import com.dlc.modules.api.entity.TeamClassOrder;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.TeamClassOrderService;
import com.dlc.modules.qd.UPushUntils.UMengPush;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/20/020
 */
@Service
@Transactional
public class TeamClassOrderServiceImpl implements TeamClassOrderService{
    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;
    @Autowired
    private StudentTeamclassShipMapper studentTeamclassShipMapper;
    @Autowired
    private TeamClassMapper teamClassMapper;

    /**
     *  @Auther:YD
     *  @parameters:团体课id:teamClassId,总价：paySum，教练姓名：coachName，时间：classTime,场所：classRoom地址：classAddress，
     *  创建团体课订单
     */
    @Override
    public Map<String,Object> createTeamClassOrder(Map<String, Object> params,Long userId) {
        TeamClassOrder tco = new TeamClassOrder();
        tco.setUserId(userId);
        //团体课id
        tco.setTeamClassId(Long.valueOf((String) params.get("teamClassId")));
        //门店id
        tco.setStoreId((Long)params.get("storeId"));
        //订单号
        String orderNo = OrderNoGenerator.getOrderIdByTime()+ ConfigConstant.TEAM_CLASS_ORDER_TYPE;
        tco.setOrderNo(orderNo);
        //总价
        tco.setPaySum((BigDecimal)params.get("paySum"));
        //实付金额
        tco.setRealPayment((BigDecimal)params.get("paySum"));
        //订单状态
        tco.setStatus((byte) 0);
        //教练名
        tco.setCoachName((String) params.get("coachName"));
        //上课时间
        tco.setClassTime((String) params.get("classTime"));
        //上课场所
        tco.setClassRoom((String) params.get("classRoom"));
        //上课地址
        tco.setClassAddress((String) params.get("classAddress"));
        //课程名字
        tco.setClassName((String) params.get("className"));
        //课程单价
        tco.setPrice((BigDecimal) params.get("price"));
        //团体课首图
        tco.setImgUrl((String) params.get("imgUrl"));
        //课程标签
        tco.setClassLabel((String) params.get("classLabel"));
        //创建时间
        tco.setCreatedDate(new Date());
        //消耗kcal
        tco.setEnergy((Double) params.get("energy"));
        teamClassOrderMapper.insertSelective(tco);
        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        map.put("paySum",tco.getRealPayment()+"");
        return map;
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后回调更新团体订单
     */
    @Override
    public int updateTeamClassOrder(String orderNo, BigDecimal wallet, String transaction_id, int payType) {
        //根据订单号查出订单
        TeamClassOrder tco = teamClassOrderMapper.selectTeamClassOrderByOrderNo(orderNo);
        //流水号
        tco.setTransactionNo(transaction_id);
        //支付方式
        tco.setPayType((byte) payType);
        //订单状态
        tco.setStatus((byte) 1);
        //支付时间和完成时间
        tco.setPayTime(new Date());
        tco.setFinish(new Date());
        int result = teamClassOrderMapper.updateByPrimaryKey(tco);
        //add by 20181012 to:支付成功后生成学生上课信息
        try {
            if(null != tco){
                StudentTeamclassShip record = new StudentTeamclassShip();
                record.setClassId(tco.getTeamClassId());
                record.setStudentId(tco.getUserId());
                record.setClassType(1);     //精品课程
                record.setClassStatus(0);    //待上课
                record.setOrderNo(orderNo);
                record.setClassTime(tco.getClassTime());
                record.setClassroom(tco.getClassRoom());
                record.setClassAddress(tco.getClassAddress());
                //新加
                record.setClassName(tco.getClassName());
                record.setClassLabel(tco.getClassLabel());
                record.setClassImgUrl(tco.getImgUrl());
                record.setClassPrice(tco.getPrice());
                record.setStoreId(tco.getStoreId());
                record.setCoachName(tco.getCoachName());
                record.setEnergy(tco.getEnergy());
                studentTeamclassShipMapper.insertSelective(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        //添加团体课当前报名人数
        TeamClass teamClass = teamClassMapper.selectByPrimaryKey(tco.getTeamClassId());
        teamClass.setActulNum(teamClass.getActulNum() + 1);
        teamClassMapper.updateByPrimaryKeySelective(teamClass);
        return result;
    }

    @Override
    public int freeTeamClassOrder(Map<String, Object> params,Long userId) {
        StudentTeamclassShip stcs = new StudentTeamclassShip();
        Long classId = Long.valueOf((String) params.get("teamClassId"));
        TeamClass teamClass = teamClassMapper.selectByPrimaryKey(classId);
        stcs.setStudentId(userId);
        stcs.setClassId(classId);
        stcs.setClassType(0);
        stcs.setClassStatus(0);
        stcs.setClassTime((String) params.get("classTime"));
        stcs.setClassroom((String) params.get("classRoom"));
        stcs.setClassAddress((String) params.get("classAddress"));
        if(null != teamClass){
            stcs.setClassName(teamClass.getTeamClassName());
            stcs.setClassLabel(teamClass.getClassLabel());
            stcs.setClassImgUrl(teamClass.getFirstImgUrl());
            stcs.setClassPrice(teamClass.getClassPrice());
            stcs.setStoreId(teamClass.getStoreId());
            stcs.setCoachName(teamClass.getCoachName());
        }
        int result = studentTeamclassShipMapper.insertSelective(stcs);
        //添加团体课当前报名人数
        teamClass.setActulNum(teamClass.getActulNum() + 1);
        teamClassMapper.updateByPrimaryKeySelective(teamClass);
        return result;
    }
}

