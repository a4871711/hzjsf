package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.OrderTaskService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.rmi.runtime.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OrderTaskServiceImpl implements OrderTaskService{

    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private CardOrderMapper cardOrderMapper;
    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;
    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;
    @Autowired
    private GoodsOrderMapper goodsOrderMapper;
    @Autowired
    private CoachAppointmentMapper coachAppointmentMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Override
    public void selectAllCardOrderByStatus() {
        Long updateTime = ((new Date().getTime()) - 30l * 60l * 1000l);
        log.info("updateTime:"+updateTime);
        //健身卡订单 '订单状态：订单状态：0 待付款，1 待发货，2 待收货，3待评价，4已完成 5已取消',
        List<CardOrder> cardOrderList = cardOrderMapper.selectAllCardOrderByStatus();
        for (CardOrder co : cardOrderList){
            if (co.getCreatedDate().getTime() < updateTime){
                co.setStatus(5);
                cardOrderMapper.updateByPrimaryKeySelective(co);
            }
        }

        //私教课 '订单状态：0 待付款，1,已付款，2.已取消 3.已评价',
        List<PrivateClassOrder> pcoList = privateClassOrderMapper.selectPcoListByStatus();
        for (PrivateClassOrder pco : pcoList) {
            if (pco.getCreatedDate().getTime() < updateTime) {
                pco.setStatus(2);
                privateClassOrderMapper.updateByPrimaryKeySelective(pco);
                log.info("====取消订单的订单号为：" + pco.getOrderNo()+"=========");
                //还原教练预约时间
                if (StringUtils.isNotBlank(pco.getClassTime())) {
                    String appTime = pco.getClassTime().split(" ")[0];
                    String timeName = pco.getClassTime().split(" ")[1];
                    Long coachId = pco.getCoachId();
                    Map<String, Object> map = new HashMap<>();
                    map.put("appTime", appTime);
                    map.put("coachId", coachId);
                    CoachAppointment coachAppointment = coachAppointmentMapper.selectByAppTimeAndCoachId(map);
                    String dataJsonArray = coachAppointment.getAppDate();
                    JSONArray jsonArray = JSONArray.parseArray(dataJsonArray);

                    for (Object object : jsonArray) {
                        Map<String, Object> objectMap = (Map<String, Object>) object;

                        List<Map<String, Object>> lm = (List<Map<String, Object>>) objectMap.get("timeData");
                        for (Map<String, Object> mm : lm) {
                            if (timeName.equals(mm.get("timeName"))) {
                                log.info("====被修改的时间点对应的map====:" + mm);
                                mm.put("bookType", 0);
                                log.info("====修改后的时间点对应的map====:" + mm);
                            }
                        }
                        coachAppointment.setAppDate(jsonArray.toJSONString());
                        coachAppointmentMapper.updateByPrimaryKeySelective(coachAppointment);
                    }
                }
            }
        }
        //团体课订单 '订单状态：0 待付款，1,已付款，2.已取消',
        List<TeamClassOrder> tcList = teamClassOrderMapper.selectAllTcListByStatus();
        for (TeamClassOrder tco : tcList){
            if (tco.getCreatedDate().getTime() < updateTime){
                tco.setStatus((byte) 2);
                teamClassOrderMapper.updateByPrimaryKeySelective(tco);
            }
        }

        //商品订单 '订单状态：0 待付款，1 待发货，2 待收货，3已完成，4已取消 5已评价 6已退款',
        List<GoodsOrder> goList = goodsOrderMapper.selectAllOrderByStatus();
        for (GoodsOrder god : goList){
            if (god.getCreatedDate().getTime() < updateTime){
                god.setStatus((byte) 4);
                goodsOrderMapper.updateByPrimaryKeySelective(god);
            }
        }
        //优惠券查询并更改是否过期
        log.info("=============开始查询优惠券日期是否过期=========");
        List<Coupon> couponList = couponMapper.selectAllCoupons();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowTime = new Date();
        for (Coupon coupon : couponList){
            Date endTime = coupon.getValidityTime();
            if (endTime.before(nowTime)){
                coupon.setCouponStatus(2);
                couponMapper.updateByPrimaryKeySelective(coupon);
                log.info("============已设置成过期=========");
            }
        }
    }
}
