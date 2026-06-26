package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.PrivateClassService;
import com.dlc.modules.api.vo.UserInfoVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@Service
@Transactional
public class PrivateClassServiceImpl implements PrivateClassService{

    private Logger log =  LoggerFactory.getLogger(getClass());

    @Autowired
    private PrivateClassMapper privateClassMapper;
    @Autowired
    private CoachMapper coachMapper;
    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;
    @Autowired
    private StuPrivateclassShipMapper stuPrivateclassShipMapper;
    @Autowired
    private ArrangeClassMapper arrangeClassMapper;
    @Autowired
    private StoreAddressMapper storeAddressMapper;
    @Autowired
    private CoachAppointmentMapper coachAppointmentMapper;
    /**
     *  @Auther:YD
     *  @parameters:
     *  课程
     */
    @Override
    public List<Map<String, Object>> courseList(Long coachId) {
        return privateClassMapper.courseList(coachId);
    }
    /**
     *  @Auther:YD
     *  @parameters:
     *  课程详情
     */
    @Override
    public Map<String, Object> courseInfo(Map<String, Object> params) {
        Map<String,Object> map = privateClassMapper.courseInfo(params);


        return map;
    }
    /**
     *  @Auther:YD
     *  @parameters: 教练id:coachId,私教课id：privateClassId,购买实时课数：buyCount，订单主图：imgUrl,总价：paySum,
     *  （选传）第一次上课时间：classTime 地点classRoom
     *  课程订单
     */
    @Override
    public Map<String,Object> createCourseOrder(Map<String, Object> params,UserInfoVo user) {
        //根据教练id查出教练
        Coach coach = coachMapper.selectByPrimaryKey(Long.valueOf((String) params.get("coachId")));
        PrivateClassOrder pco = new PrivateClassOrder();
        //订单编号 订单号后面加 2 便于微信回调时调用回调方法 2：购买健身卡订单 4：购买私教课订单 5：购买团体课订单
        String orderNo = OrderNoGenerator.getOrderIdByTime() + ConfigConstant.PRIVATE_CLASS_ORDER_TYPE;
        //openId
        pco.setOpenId(user.getOpenId());
        //用户id
        pco.setUserId(user.getUserId());
        //私教课程id
        pco.setPrivateClassId(Long.valueOf((String) params.get("privateClassId")));
        //私教课名称
        pco.setClassName((String) params.get("className"));
        //私教课程单价
        pco.setPrice(new BigDecimal((String) params.get("price")));
        //教练id
        pco.setCoachId(coach.getCoachId());
        //教练名称
        pco.setCoachName(coach.getCoachName());
        //购买实时课数
        pco.setBuyCount(Integer.parseInt((String) params.get("buyCount")));
        //图片
        pco.setImgUrl((String) params.get("imgUrl"));
        //订单编号
        pco.setOrderNo(orderNo);
        //总价
        pco.setPaySum(new BigDecimal((String) params.get("paySum")));
        //实付金额
        pco.setRealPayment(new BigDecimal((String) params.get("paySum")));
        //订单状态
        pco.setStatus(0);
        //日期（格式 : 2018-09-18 10:30）约课日期+预约时间点
        pco.setClassTime(params.get("dataTime") == null?null:params.get("dataTime").toString());
        log.info("约课日期+预约时间点:========="+params.get("dataTime")+"=======================");
        //预约数据json字符串
        pco.setClassDate(params.get("dataJsonArray") == null?null:params.get("dataJsonArray").toString());
        log.info("预约数据json字符串:=========="+params.get("dataJsonArray")+"=======================");
        //上课门店id
        pco.setStoreAddrId(params.get("storeAddrId") == null?null:Long.valueOf(params.get("storeAddrId").toString()));
        //门店名称
        if (StringUtils.isNotBlank((String) params.get("storeAddrId"))) {
            StoreAddress storeAddress = storeAddressMapper.findStoreAddressByStoreAddressId(Long.valueOf((String) params.get("storeAddrId")));
            pco.setStoreName(storeAddress.getStoreName());
        }
        pco.setCreatedDate(new Date());
        privateClassOrderMapper.insertSelective(pco);
        log.info("私教课订单详情：========================"+pco.toString()+"====================================");
        // 编辑预约预约可时间
        if (StringUtils.isNotBlank((String) params.get("dataTime"))){
            CoachAppointment coachAppointment = new CoachAppointment();
            coachAppointment.setCoachId(coach.getCoachId());
            String dataTime = ((String) params.get("dataTime")).split(" ")[0];
            Map<String,Object> map = new HashMap<>();
            map.put("coachId",Long.valueOf((String) params.get("coachId")));
            map.put("appTime",dataTime);
            map.put("appDate",params.get("dataJsonArray"));
            coachAppointmentMapper.updateCoachAppointment(map);
        }

        Map<String,Object> map = new HashMap<>();
        map.put("orderNo",orderNo);
        map.put("paySum",pco.getRealPayment()+"");
        return map;
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  支付后更新订单
     */
    @Override
    public int updatePrivateClassOrder(String orderNo, BigDecimal wallet, String transaction_id, int payType) throws ParseException {
        //根据订单号查出订单
        PrivateClassOrder pco = privateClassOrderMapper.selectPrivateClassOrderByOrder(orderNo);
        //根据私教课id查出私教课
        PrivateClass pc = privateClassMapper.selectByPrimaryKey(pco.getPrivateClassId());
        //支付方式
        pco.setPayType(payType);
        //实付金额
        pco.setRealPayment(wallet);
        //流水号
        pco.setTransactionNo(transaction_id);
        //订单状态 已付款
        pco.setStatus(1);
        //支付时间
        pco.setPayTime(new Date());
        //完成时间
        pco.setFinish(new Date());
        //有效期
        if(pc != null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Long days = pc.getValidityDay().longValue();
            String validityDate = df.format(new Date((new Date()).getTime() + days * 24l * 60l * 60l * 1000l));
            pco.setValidityDate(df.parse(validityDate));
            pco.setDuration(pc.getClassTime());    //新加课程时长/分钟
            pco.setEnergy(pc.getEnergy());
            pco.setOrderType(pc.getClassType());
        }
        int result = privateClassOrderMapper.updateByPrimaryKeySelective(pco);
        //add by 20181012 to:支付成功后生成学生上课信息
        try {
            if(null != pco){
                //生成学生课程关系信息
                StuPrivateclassShip stuPcs = new StuPrivateclassShip();
                stuPcs.setClassId(pco.getPrivateClassId());
                stuPcs.setCoachId(pco.getCoachId());
                stuPcs.setStudentId(pco.getUserId());
                stuPcs.setClassName(pco.getClassName());   //新加课程名称
                stuPcs.setOrderNo(orderNo);
                stuPcs.setBuyNumber(pco.getBuyCount());
                Integer askNumber = pco.getBuyCount();
                if(StringUtils.isNotEmpty(pco.getClassTime()) && null != pco.getStoreAddrId()){ //表示选了首次上课时间和地点
                    askNumber = askNumber - 1;
                }
                stuPcs.setAskNumber(askNumber);  //可约节数（如果购买的时候已经约了一节可约节数-1）
                stuPcs.setClassStatus(0);
                stuPcs.setPrice(pco.getPrice());
                stuPcs.setValidityDate(pco.getValidityDate());
                stuPcs.setDuration(pco.getDuration());
                stuPcs.setEnergy(pco.getEnergy());
                stuPcs.setOrderType(pco.getOrderType());
                stuPrivateclassShipMapper.insertSelective(stuPcs);
                //如果选择了首次上课就生成约课信息
                Long spsId = stuPcs.getSpsId();
                if(StringUtils.isNotEmpty(pco.getClassTime()) && null != pco.getStoreAddrId()){ //表示选了首次上课时间和地点
                    appointClass(pco, spsId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //add end
        return result;
    }

    @Override
    public int queryIsOrBuyTyk(Long userId, Long privateClassId) {
        //先查询该课程是不是体验课
        int r = privateClassMapper.queryIsOrTyclass(privateClassId);
        int count = 0;
        if(r > 0){
            count = privateClassOrderMapper.queryIsOrBuyTyclass(userId);
        }
        return count;
    }

    private void appointClass(PrivateClassOrder pco, Long spsId) {
        if(pco == null || spsId == null){
            return;
        }
        try {
            Long coachId = pco.getCoachId();
            String dataJsonArray = pco.getClassDate();
            //获取十位的日期2018-10-11
            String dataTime = pco.getClassTime().substring(0, 10);
            ArrangeClass arrangeClass = new ArrangeClass();
            arrangeClass.setSpsId(spsId);
            arrangeClass.setCoachClassId(pco.getPrivateClassId());
            arrangeClass.setCoachId(coachId);
            arrangeClass.setStudentId(pco.getUserId());            //学员id拿当前用户id
            arrangeClass.setClassPlaceId(pco.getStoreAddrId());
            arrangeClass.setClassPlace(pco.getStoreName());                 //上课门店名称
            arrangeClass.setClassTime(pco.getClassTime());
            arrangeClass.setClassDate(dataJsonArray);
            arrangeClass.setClassName(pco.getClassName());          //新加字段课程名称
            arrangeClass.setDuration(pco.getDuration());
            arrangeClass.setPrice(pco.getPrice());
            arrangeClass.setValidityDate(pco.getValidityDate());
            arrangeClassMapper.insertSelective(arrangeClass);
//            //将教练约课时间替换掉（更新）
//            Map<String,Object> map = new HashMap<>();
//            map.put("coachId",coachId);
//            map.put("appTime",dataTime);
//            map.put("appDate",dataJsonArray);
//            coachAppointmentMapper.updateCoachAppointment(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
