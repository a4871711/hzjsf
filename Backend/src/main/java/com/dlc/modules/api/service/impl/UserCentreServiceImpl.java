package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.*;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.IncomePayDetail;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.PayService;
import com.dlc.modules.api.service.UserCentreService;
import com.dlc.modules.api.service.WxPayService;
import com.dlc.modules.sys.dao.SysDeviceDao;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 個人中心
 *
 * @author dlc.dg.java
 * @email dlc.dg.java@163.com
 * @date 2018-09-10 21:04:23
 */
@Service
@Transactional
public class UserCentreServiceImpl implements UserCentreService{

    private final Logger log = LoggerFactory.getLogger(UserCentreServiceImpl.class);

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private StudentTeamclassShipMapper studentTeamclassShipMapper;

    @Autowired
    private StuPrivateclassShipMapper stuPrivateclassShipMapper;

    @Autowired
    private ArrangeClassMapper arrangeClassMapper;

    @Autowired
    private GoodsOrderMapper goodsOrderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private TeamClassOrderMapper teamClassOrderMapper;

    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;

    @Autowired
    private CardOrderMapper cardOrderMapper;

    @Autowired
    private SysDeviceDao sysDeviceDao;

    @Autowired
    private DataMapMapper dataMapMapper;

    @Autowired
    private SystemMsgDao systemMsgDao;

    @Autowired
    private SportRecordMapper sportRecordMapper;

    @Autowired
    private PayService payService;

    @Autowired
    private IncomePayDetailMapper incomePayDetailMapper;

    @Autowired
    private UserWalletMapper userWalletMapper;

    @Autowired
    private WxPayService wxPayService;

    @Override
    public Map<String, Object> queryUserInfo(Long userId, Long myId) {
        //log.info("ApiUserCentreController.queryUserInfo start...");
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", userId);
        queryMap.put("myId",myId);
        Map<String, Object> userInfoMap = null;
        try {
            userInfoMap = userInfoMapper.queryUserInfoById(queryMap);
            if(null == userInfoMap){
                return new HashMap<>();
            }
            Integer attentionStatus = (Integer)userInfoMap.get("attentionStatus");
            if(attentionStatus == null) {
                userInfoMap.put("attentionStatus", 0);
            }
            Integer userEnergy = sportRecordMapper.queryUserTotalEnergy(userId);
            Integer level = userInfoMapper.queryUserLevel(userEnergy);
            userInfoMap.put("level", level);
            //处理表情
            String nickname = (String) userInfoMap.get("nickname");
            if(StringUtils.isNotBlank(nickname)){
                userInfoMap.put("nickname", EmojiParser.parseToUnicode(nickname));
            }
            //更新签约状态/处理用户在微信端取消了自动续费
//            if(userInfoMap.get("wtState").toString().equalsIgnoreCase("1")){
//                String contractId = userInfoMap.get("contractId").toString();
//                Map<String, String> result = wxPayService.queryContract(contractId);
//                if(result != null && result.get("return_code").equals("SUCCESS") && result.get("result_code").equals("SUCCESS")){
//                    UserInfo updateData = new UserInfo();
//                    updateData.setUserId( Long.valueOf(userInfoMap.get("userId").toString()) );
//                    updateData.setWtState(1);
//                    updateData.setWtOpenId(result.get("openid"));
//                    updateData.setContractId(result.get("contract_id"));
//                    userInfoMapper.updateContract(updateData);
//                }else {
//                    userInfoMapper.cancelContractV2( Long.valueOf(userInfoMap.get("userId").toString()) );
//                }
//            }
        } catch (Exception e) {
            log.info("queryUserInfo异常{}"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return userInfoMap;
    }

    @Override
    public Map<String, Object> queryUserLevel(Long userId) {
        //log.info("UserCentreController.queryMyLevel start..."+userId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {

            //查询用户消耗总能量(私教课总能量+其他运动总能量)
            Integer userEnergy = sportRecordMapper.queryUserTotalEnergy(userId);
            Map<String, Object> levMap = userInfoMapper.queryLevel(userEnergy);
            resultMap.put("userId", userId);
            resultMap.put("level", levMap.get("price"));
            resultMap.put("levelName", levMap.get("dataName"));
            resultMap.put("proEnergy", levMap.get("proEnergy"));
            resultMap.put("percent", levMap.get("percent"));
            resultMap.put("rule", levMap.get("rule"));
        } catch (Exception e) {
            log.info("queryUserLevel异常{}"+e);
            e.printStackTrace();
        }
        return resultMap;
    }

    @Override
    public R querySystemMsg(Long userId) {
        //log.info("UserCentreController.querySystemMsg start..."+userId);
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("msgList", systemMsgDao.querySysMsgByUserId(userId));
        try {
            systemMsgDao.updateReadFlag(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.reOk(resMap);
    }

    @Override
    public PageUtils queryTeamClass(Map<String, Object> parms) {
        //log.info("UserCentreController.queryMyTeamClass start...");
        Query query =new Query(parms);
        List<Map<String, Object>> list = studentTeamclassShipMapper.queryTeamClassByUserId(query);
        int total = studentTeamclassShipMapper.queryTeamClassCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());

        return pageUtil;
    }

    @Override
    public PageUtils queryMyPrivateClass(Map<String, Object> parms) {
        //log.info("UserCentreController.queryMyPrivateClass start...");
        Query query =new Query(parms);
        List<Map<String, Object>> pcList = stuPrivateclassShipMapper.queryPrivateClassByUserId(query);
        for(Map<String, Object> pl : pcList){
            int classStatus = (int) pl.get("classStatus");      // 1：已完成 2：进行中 3：已过期0:待上课
            if(classStatus == 1 || classStatus == 3){
                pl.put("appointStatus", 1);
            }else{
                pl.put("appointStatus", 0);
            }

        }
        int total = stuPrivateclassShipMapper.queryPrivateClassCount(query);
        PageUtils pageUtil = new PageUtils(pcList,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public PageUtils queryAppointClass(Map<String, Object> params) {
        //log.info("UserCentreController.queryAppointClass start...");
        Query query =new Query(params);
        List<Map<String, Object>> appList = arrangeClassMapper.queryAppointClassByUserId(query);
        int total = arrangeClassMapper.queryAppointClassCount(query);
        PageUtils pageUtil = new PageUtils(appList,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public PageUtils queryMyOrders(Map<String, Object> params) {
        //log.info("UserCentreController.queryMyOrders start...");
        //0：商城订单 1：团教课订单 2：私教课订单 3.卡订单 默认查0商城订单
        Query query =new Query(params);
        List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
        //订单类型（ 0：商城订单 1：团教课订单 2：私教课订单)
        int orderType = Integer.parseInt((String) params.get("orderType"));
        //订单状态（0：待付款 1：待发货 2：待收货）
        int orderStatus = 3;         //初始化3：查全部
        if(StringUtils.isNotEmpty((String) params.get("orderStatus"))){
            orderStatus = Integer.parseInt((String) params.get("orderStatus"));
        }
        PageUtils pageUtil = null;
        int total = 0;
        try {
            //商城订单
            if(orderType == 0){
                //查全部
                if(orderStatus == 3){
                    queryList = goodsOrderMapper.queryGoodsOrderByUserId(query);
                    for(Map<String, Object> goodsMap : queryList){
                        Long userId = (Long) goodsMap.get("userId");
                        String orderNo = (String) goodsMap.get("orderNo");
                        goodsMap.put("goodsInfo", orderDetailMapper.queryDetailByOrderNo(userId, orderNo));
                    }
                    total = goodsOrderMapper.queryGoodsOrderCount(query);
                    pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
                }else{
                    //0：待付款 1：待发货 2：待收货
                    queryList = goodsOrderMapper.queryGoodsOrderByStatus(query);
                    for(Map<String, Object> goodsMap : queryList){
                        Long userId = (Long) goodsMap.get("userId");
                        String orderNo = (String) goodsMap.get("orderNo");
                        goodsMap.put("goodsInfo", orderDetailMapper.queryDetailByOrderNo(userId, orderNo));
                    }
                    total = goodsOrderMapper.queryGoodsOrderByStatusNum(query);
                    pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
                }

            }else if(orderType == 1){  //团课订单
                queryList = teamClassOrderMapper.queryTeamClassOrder(query);
                total = teamClassOrderMapper.queryTeamClassOrderNum(query);
                pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());

            }else if(orderType == 2){  //私教课订单
                queryList = privateClassOrderMapper.queryPrivateClassOrder(query);
                total = privateClassOrderMapper.queryPrivateClassOrderNum(query);
                pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());

            }

        } catch (Exception e) {
            log.info("queryMyOrders 异常："+e);
            e.printStackTrace();
        }

        return pageUtil;

    }

    @Override
    public Map<String, Object> queryMyOrderDetail(Long userId, String orderNo, int orderType) {
        //log.info("UserCentreController.queryMyOrderDetail start..."+userId);
        //0：商城订单 1：团教课订单 2：私教课订单 3.卡订单 默认查0商城订单
        Map<String, Object> resposeMap = new HashMap<String, Object>();

        try {
            switch (orderType){
                case 0:   //商城订单
                    resposeMap = goodsOrderMapper.queryOrderDetail(userId, orderNo);
                    putDetailInfo(resposeMap);         //处理订单状态
                    resposeMap.put("goodsInfo", orderDetailMapper.queryDetailByOrderNo(userId, orderNo));
                    break;
                case 1:   //团课订单
                    resposeMap = teamClassOrderMapper.queryTeamOrderDetail(userId, orderNo);
                    putDetailInfo(resposeMap);
                    break;
                case 2:    //私教课订单
                    resposeMap = privateClassOrderMapper.queryPrivateOrderDetail(userId, orderNo);
                    putDetailInfo(resposeMap);
                    break;
                case 3:     //卡订单
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.info("queryMyOrderDetail 异常："+e);
            e.printStackTrace();
        }
        if(resposeMap == null){resposeMap = new HashMap<String, Object>();}
        return resposeMap;
    }

    @Override
    public Map<String, Object> queryMyCard(Long userId) {
        //查询我的健身卡
        //根据wristId来判断用户是否有卡
//        Map<String, Object> meMap = userInfoMapper.getCardStatus(userId);
//        Map<String, Object> result = null;
//        if(meMap.get("wristId") != null && !StringUtils.isBlank((String) meMap.get("wristId"))){   //有卡才去查询
//            result = cardOrderMapper.queryCardDetailByUserId(userId);
//        }
        Map<String, Object> result = cardOrderMapper.queryCardDetailByUserId(userId);
        if(null == result){result = new HashMap<String, Object>();}
        return result;
    }

    @Override
    public List<Map<String, Object>> queryMyDevice(Long userId) {
        //log.info("UserCentreController.queryMyDevice start..."+userId);
        List<Map<String, Object>> result = sysDeviceDao.queryDeviceByUserId(userId);
        if(CollectionUtils.isEmpty(result)){result = new ArrayList<Map<String, Object>>();}
        return result;
    }

    @Override
    public List<String> queryHobbies() {
        List<String> result = dataMapMapper.queryHobbies();
        if(CollectionUtils.isEmpty(result)){result = new ArrayList<String>();}
        return result;
    }

    @Override
    public Map<String, Object> querySystemMsgCount(Long userId) {
        Map<String, Object> resMap = new HashMap<String, Object>();
        resMap.put("unRead", systemMsgDao.queryUnReadNum(userId));
        return resMap;
    }
    //取消订单
    @Override
    public R orderCancel(Long userId, String orderNo, int orderType) {
        int res = 0;
        Map<String, Object> updateMap = new HashMap<String, Object>();
        updateMap.put("userId", userId);
        updateMap.put("orderNo", orderNo);
        try {
            switch (orderType){
                case 0:   //取消商城订单
                    //已付款的订单点击“取消” 走退款接口（金额原路返回）待付款的只更新状态
                    Map<String,Object> params = payService.queryOrderInfoOrderNo(orderNo);
                    if(null == params){
                        return R.reError("订单信息不存在");
                    }
                    //待付款
                    String transactionNo = (String) params.get("transactionNo");
                    int status = (Integer)params.get("status");
                    int payType = (Integer)params.get("payType");
                    Long myId = (Long) params.get("userId");
                    BigDecimal money = (BigDecimal) params.get("realPayment");
                    //待发货或者待收货要进行退款处理
                    if(status == 1 || status == 2){
                        if(payType == 1){
                            //余额支付退款+
                           int flags = userWalletMapper.updateMyMoney(myId, money);
                           if(flags <= 0){
                               return R.reError("发起余额退款失败,请联系商家");
                           }
                        }else if(payType == 2){
                            //微信支付退款
                          String resWx = payService.wxRefund(params);
                          if(resWx == null){
                              return R.reError("发起微信退款失败,请联系商家");
                          }
                        }else if(payType == 3){
                            //支付宝支付退款
                          String resZfb = payService.zfbRefund(params);
                          if(resZfb == null){
                             return R.reError("发起支付宝退款失败,请联系商家");
                          }
                        }
                        //收支明细表(退款时插入数据)其他情况只做状态更新
                        incomeDeal(myId, orderNo, money, payType, transactionNo);
                    }

                    //更新订单状态
                    updateMap.put("status", (byte)4);
                    res = goodsOrderMapper.updateOrderStatus(updateMap);
                    break;
                case 1:   //取消团课订单
                    updateMap.put("status", (byte)2);
                    res = teamClassOrderMapper.updateTeamOrderStatus(updateMap);
                    break;
                case 2:    //取消私教课订单
                    updateMap.put("status", (byte)2);
                    res = privateClassOrderMapper.updatePrivateOrderStatus(updateMap);
                    break;
                case 3:     //卡订单
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        if(res <= 0){
            R.reError("取消失败");
        }
        return R.reOk();
    }

    private void incomeDeal(Long myId, String orderNo, BigDecimal money, int payType, String transactionNo) {
        try {
            Date date = new Date();
            //收支明细表
            IncomePayDetail inCome = new IncomePayDetail();
            //用户id
            inCome.setUserId(myId);
            //订单号
            inCome.setOrderNo(orderNo);
            //流动号
            inCome.setTransactionNumber(transactionNo);
            //支付用途 (商城退款)
            inCome.setPayType(9);
            //金额
            inCome.setMoney(money);
            //收支时间
            inCome.setTradeDate(date);
            //收支方式(余额退款)
            inCome.setTradeType(payType);
            //状态(已完成)
            inCome.setTradeStatus(3);
            //审核通过时间
            //交易完成时间
            inCome.setTransactionTime(date);
            //创建时间
            inCome.setCreatedDate(date);
            //查询当前用户所属门店id
            Long storeId = userInfoMapper.queryStoreIdByUserId(myId);
            inCome.setStoreId(storeId);
            incomePayDetailMapper.insertSelective(inCome);
            //系统消息
            SystemMsgEntity systemMsgEntity = new SystemMsgEntity();
            systemMsgEntity.setUserId(myId);  //userId
            String record = Constant.PayType.getDesc(payType)+"退款已受理，原订单编号："+orderNo+"金额："+money;
            systemMsgEntity.setRecord(record);
            systemMsgEntity.setMsgType(2);   //退款消息
            systemMsgEntity.setSendTime(new Date());
            systemMsgDao.save(systemMsgEntity);
        } catch (Exception e) {
            log.info("incomeDeal异常信息{}"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
    }

    @Override
    public R orderDel(Long userId, String orderNo) {
        int res = 0;
        try {
            //物理删除商城订单根据orderNo
            res = goodsOrderMapper.deleteByOrderNo(orderNo);
            if(res > 0){
                //删除订单详情表
                orderDetailMapper.deleteDetailByOrderNo(orderNo);
                //注：收支明细不能删除（income）
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        if(res <= 0){
            R.reError("删除失败，找不到该订单号");
        }
        return R.reOk();
    }

    @Override
    public R orderConfirm(Long userId, String orderNo) {
        //确认订单
        int res = 0;
        try {
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("userId", userId);
            updateMap.put("orderNo", orderNo);
            updateMap.put("status", (byte)3);    //已完成
            res = goodsOrderMapper.updateOrderStatus(updateMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(res <= 0){
            return R.reError("操作失败");
        }
        return R.reOk();
    }

    /**
     * 处理订单状态
     * @param resposeMap
     */
    private void putDetailInfo(Map<String, Object> resposeMap) {
        if(null != resposeMap){
            int status = (int) resposeMap.get("status");
            long canceTime = 0;
            SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd HH:mm");
            //待付款
            if (status == Constant.OrderStatus.WAIT_PAY.getValue()){
                try {
                    canceTime = format.parse(resposeMap.get("createdDate").toString()).getTime()+Constant.LIMIT_TIME_PAY;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int totalMin = DateUtils.getMin(new Date().getTime(), canceTime);
                int hour = (totalMin%(24*60))/60;
                int minute = (totalMin%(24*60))%60;
                resposeMap.put("waitCanceHours",hour);
                resposeMap.put("waitCanceMin",minute);
            }

            //待确认收货
            if (status == Constant.OrderStatus.WAIT_RECIVE.getValue()){
                try {
                    //15天后
                    canceTime = format.parse(resposeMap.get("createdDate").toString()).getTime()+Constant.LIMIT_TIME_RECEIVE;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int totalMin = DateUtils.getMin(new Date().getTime(), canceTime);
                int hour = (totalMin%(24*60))/60;
                int minute = (totalMin%(24*60))%60;
                int day = totalMin/(24*60);
                resposeMap.put("waitConfirmHours",hour);
                resposeMap.put("waitConfirmMin",minute);
                resposeMap.put("waitConfirmDay",day);
            }
        }

    }

}
