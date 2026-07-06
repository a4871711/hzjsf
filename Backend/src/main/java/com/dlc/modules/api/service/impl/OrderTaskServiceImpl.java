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
    @Autowired
    private PtPrivateOrderDao ptPrivateOrderDao;
    @Autowired
    private PtPrivateOrderCouponRelDao ptPrivateOrderCouponRelDao;
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

    /**
     * 第16步私教单分支(与既有分支分开成方法,由 UpdateOrderStatusTask 单独 try/catch 调用,
     * 走本 impl 的事务切面,失败不影响既有卡/商城/私教课/团课分支)。
     * 幂等:cancelTimeoutOrder 卡 WHERE order_status=0,与支付回调 settleOrder 互斥,
     * 命中才释放券(releaseMemberCoupon 卡 use_status=3 AND used_order_id=本单,双CAS不误放)。
     */
    @Override
    public void cancelTimeoutPrivateOrders() {
        // 私教购买单 '订单状态:0待支付 1首付已付 2已结清 3已取消 4已退款',超30分钟未付取消
        List<PtPrivateOrderEntity> list = ptPrivateOrderDao.queryTimeoutUnpaid(30);
        for (PtPrivateOrderEntity order : list) {
            if (ptPrivateOrderDao.cancelTimeoutOrder(order.getId()) == 0) {
                // 0行=扫描后已被支付/取消,券占用归属已变,跳过
                continue;
            }
            log.info("====私教订单超时取消,订单号:" + order.getOrderNo() + "=========");
            if (order.getMemberCouponId() != null) {
                int released = ptPrivateOrderCouponRelDao.releaseMemberCoupon(
                        order.getMemberCouponId(), order.getId());
                if (released == 0) {
                    // 券已被核销/释放(状态漂移),记日志人工核对,不阻断其余订单
                    log.warn("私教订单超时取消但优惠券释放0行,orderNo=" + order.getOrderNo()
                            + ",memberCouponId=" + order.getMemberCouponId());
                }
            }
        }
    }
}
