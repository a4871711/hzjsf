package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.OrderNoGenerator;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.*;
import com.dlc.modules.api.service.CoachService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.*;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@Service
@Transactional
public class CoachServiceImpl implements CoachService{

    @Autowired
    private CoachMapper coachMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private StoreCoachMapper storeCoachMapper;
    @Autowired
    private StoreCoachEvaluateMapper storeCoachEvaluateMapper;

    @Autowired
    private CoachWalletMapper coachWalletMapper;

    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;

    @Autowired
    private WalletDetailDao walletDetailDao;

    @Autowired
    private CoachPlaceShipMapper coachPlaceShipMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练列表
     */
    @Override
    public List<Map<String,Object>> queryCoachList(Map<String, Object> params) {
        List<Map<String,Object>> list = coachMapper.queryCoachList(params);
        for (Map<String,Object> map : list) {
            //教练评分总和
            Integer evLevelSum;
            if (coachMapper.sumCoachEvleve((Long) map.get("coachId")) == null){
                evLevelSum = 0;
            }else {
                evLevelSum = coachMapper.sumCoachEvleve((Long) map.get("coachId"));
            }
            //累计评价条数
            Integer classCount = coachMapper.countCoachEvleve((Long) map.get("coachId"));
            if (classCount == 0){
                map.put("score",0);
                map.put("level",0);
            }else {
                BigDecimal score = BigDecimal.valueOf(evLevelSum).divide(BigDecimal.valueOf(classCount),1,BigDecimal.ROUND_HALF_DOWN);
                map.put("score",score);
                map.put("level",score);
            }
            //教练课程最低价格

        }
        try {
            //排序
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double score1 = Double.valueOf(o1.get("score").toString()) ;//score1是从你list里面拿出来的一个
                    Double score2 = Double.valueOf(o2.get("score").toString()) ; //score2是从你list里面拿出来的第二个
                    return score2.compareTo(score1);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int queryCoachTotal(Query query) {
        return coachMapper.queryCoachTotal(query);
    }
    
    /**
     *  @Auther:YD
     *  @parameters:
     *  教练列表
     */
    @Override
    public List<Map<String,Object>> queryStoreCoachList(Map<String, Object> params) {
        List<Map<String,Object>> list = storeCoachMapper.queryStoreCoachList(params);
        return list;
    }

    @Override
    public int queryStoreCoachTotal(Query query) {
        return storeCoachMapper.queryStoreCoachTotal(query);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练详情
     */
    @Override
    public Map<String,Object> selectCoachInfo(Map<String,Object> params) {

        Map<String,Object> map = coachMapper.selectCoachByCoachId(params);
        Map<String,Object> storeMap = storeMapper.queryStoreInfo(Long.valueOf((String) params.get("storeId")));
        String backGroundImg = ((String) storeMap.get("storeImgUrl")).split(",")[0];
        map.put("backGroundImg",backGroundImg);
        //教练评分总和
        Integer evLevelSum;
        if (coachMapper.sumCoachEvleve((Long) map.get("coachId")) == null){
            evLevelSum = 0;
        }else {
            evLevelSum = coachMapper.sumCoachEvleve((Long) map.get("coachId"));
        }
        //累计评价条数
        Integer classCount = coachMapper.countCoachEvleve((Long) map.get("coachId"));
        if (classCount == 0){
            map.put("score",0);
            map.put("level",0);
        }else {
            BigDecimal score = BigDecimal.valueOf(evLevelSum).divide(BigDecimal.valueOf(classCount),1,BigDecimal.ROUND_HALF_DOWN);
            map.put("score",score);
            map.put("level",score);
        }
        return map;
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  推荐教练
     */
    @Override
    public List<Map<String, Object>> recommendCoach(Long storeId) {
        List<Map<String,Object>> list = coachMapper.recommendCoach(storeId);
        for (Map<String,Object> map : list) {
            //教练评分总和
            Integer evLevelSum;
            if (coachMapper.sumCoachEvleve((Long) map.get("coachId")) == null){
                evLevelSum = 0;
            }else {
                evLevelSum = coachMapper.sumCoachEvleve((Long) map.get("coachId"));
            }
            //累计评价条数
            Integer classCount = coachMapper.countCoachEvleve((Long) map.get("coachId"));
            if (classCount == 0){
                map.put("score",0);
                map.put("level",0);
            }else {
                BigDecimal score = BigDecimal.valueOf(evLevelSum).divide(BigDecimal.valueOf(classCount),1,BigDecimal.ROUND_HALF_DOWN);
                map.put("score",score);
                map.put("level",score);
            }
        }
        try {
            //排序
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double score1 = Double.valueOf(o1.get("score").toString()) ;//score1是从你list里面拿出来的一个
                    Double score2 = Double.valueOf(o2.get("score").toString()) ; //score2是从你list里面拿出来的第二个
                    return score2.compareTo(score1);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public int queryTotal(Query query) {
        return coachMapper.queryTotal(query);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  服务场地 要修改
     */
    @Override
    public List<Map<String,Object>> servePlaceList(Long coachId) {
        List<Map<String,Object>> storeNameList = coachPlaceShipMapper.selectStoreIdByCoachId(coachId);
        List<Map<String,Object>> servePlaceList = new ArrayList<>();
        for (Map<String,Object> storeMap : storeNameList) {
            Map<String,Object> map = storeMapper.queryStoreInfo(Long.valueOf(storeMap.get("storeId").toString()));
            if(null != map){
//                String storeImgUrl = map.get("storeImgUrl").toString();
//                map.put("storeImgUrl",storeImgUrl);
//                /*StringBuilder sb = new StringBuilder();
//                String alladdressDetail =  sb.append(map.get("province").toString()).append(map.get("city").toString()).append(map.get("zone").toString()).append(map.get("storeAddrDetail").toString()).toString();*/
//               // map.put("addressDetail",alladdressDetail);
                String storeImgUrl = map.get("storeImgUrl").toString();
                if (storeImgUrl.contains(",")){
                    String[] split = storeImgUrl.split(",");
                    String newImg = split[0];
                    map.put("storeImgUrl",newImg);
                }
                servePlaceList.add(map);
            }
        }
        return servePlaceList;
    }

    @Override
    public int saveCoachInfo(Coach coach) {
        return coachMapper.insertSelective(coach);
    }

    @Override
    public Map<String, Object> queryCoachStatus(Long userId) {
        return coachMapper.queryCoachInfo(userId);
    }

    @Override
    public Integer queryCoachGradeByCoachId(Long coachId) {
        return coachMapper.queryCoachGradeByCoachId(coachId);
    }

    @Override
    public List<Map<String, Object>> queryAddClassByGrade(Integer grade) {
        return coachMapper.queryAddClassByGrade(grade);

    }

    @Override
    public Double queryCoachAccountBalance(Long coachId) {
        Double accountBalance = coachWalletMapper.queryCoachAccountBalance(coachId);
        if (accountBalance==null){
            return 0.00;
        }
        return accountBalance;
    }

    @Override
    public Double queryCoachTotalIncome(Long coachId) {
        Double totalIncome = privateClassOrderMapper.queryCoachTotalIncome(coachId);
        if (totalIncome==null){
            return 0.00;
        }
        return totalIncome;
    }

    @Override
    public Double queryWithdrawCash(Long userId) {
        Double withdrawCash = walletDetailDao.queryWithdrawCash(userId);
        if (withdrawCash==null){
            return 0.00;
        }
        return withdrawCash;
    }

    @Override
    public List<Map<String, Object>> queryWithdrawCashList(Query query) {
        return walletDetailDao.queryWithdrawCashList(query);

    }

    @Override
    public int queryWithdrawCashListTotal(Query query) {
        return walletDetailDao.queryWithdrawCashListTotal(query);
    }

    @Override
    public  List<String> queryCoachStoreId(Long coachId) {
        return coachMapper.queryCoachStoreIds(coachId);
    }

    @Override
    public int updateCoachStore(Map<String, Object> map) {
        return coachMapper.updateCoachStore(map);
    }

    @Override
    public Map<String, Object> queryExistShip(Map<String, Object> paramMap) {
        return coachMapper.queryExistShip(paramMap);
    }

    @Override
    public int addCoachPlaceShip(Map<String, Object> paramMap) {
        CoachPlaceShip coachPlaceShip = new CoachPlaceShip();
        coachPlaceShip.setCoachId(Long.parseLong(paramMap.get("coachId").toString()));
        coachPlaceShip.setStoreId(Long.parseLong(paramMap.get("storeId").toString()));
        coachPlaceShip.setStoreName((String) paramMap.get("storeName"));    //新增字段
        return coachPlaceShipMapper.insertSelective(coachPlaceShip);
    }

    @Override
    public Map<String, Object> queryAlipayCountInfo(Long coachId) {
        return coachMapper.queryAlipayCountInfo(coachId);
    }

    @Override
    public int queryLowPriceFromDataMap() {
        return coachMapper.queryLowPriceFromDataMap();
    }

    @Override
    public int updateCoachChWallet(Map<String, Object> params) {
        return coachWalletMapper.updateAlipayCount(params);
    }
    @Override
    public Map<String, Object> test() {
        Map<String,Object> map = new HashMap<>();
        map.put("token","5c75351f68ed7d0a4f103dc777dbf93b");
        return map;
    }



    @Override
    public int addWithdrawCashRecord(Long coachId, BigDecimal money, String alipay, String realName) {
        WalletDetailEntity walletDetailEntity = new WalletDetailEntity();
        walletDetailEntity.setAliAccount(alipay);
        walletDetailEntity.setCreateTime(new Date());
        walletDetailEntity.setOrderNo(OrderNoGenerator.getOrderIdByTime());
        walletDetailEntity.setMoney(money);
        //状态（1为审核中 2审核失败 3已完成  4提现中 5提现失败,6 充值失败）
        walletDetailEntity.setStatus(1);
        walletDetailEntity.setUserId(coachId);
        //明细类型（1.(用户) 提现 2.（教练）提现）',
        walletDetailEntity.setType(2);
        walletDetailEntity.setRealName(realName);
        return walletDetailDao.save(walletDetailEntity);
    }

    @Override
    public BigDecimal queryCoachWallet(Long coachId) {
        return walletDetailDao.queryCoachWalletBalance(coachId);

    }

    @Override
    public int updateCoachWalletMoney(BigDecimal newBalance,Long coachId) {
        Map<String,Object> param= new HashMap<>();
        param.put("newBalance",newBalance);
        param.put("coachId",coachId);
        return walletDetailDao.updateCoachWalletMoney(param);
    }

    @Override
    public Map<String, Object> queryCoachInfo(Long coachId) {
        Map<String, Object> map = coachMapper.queryCoachInfoByCoachId(coachId);
        return map;
    }

    //教练审核用
    @Override
    public int updateCoachInfo(Coach coach) {
        if (coach.getMinClassMoney()==null){
            //此处为空 则只是审核，不为空用作修改教练最低课程价格用
            //教练审核状态重新置为待审核
            coach.setApproveStatus(0);
        }
        return coachMapper.updateByPrimaryKeySelective(coach);
    }


    //更新课程用
    @Override
    public void updateCoachClassInfo(Coach coach) {
        coachMapper.updateByPrimaryKeySelective(coach);
    }

    @Override
    public List<Long> queryExistStoreId(Long coachId, List<Long> sts) {
        return coachMapper.queryExistStoreId(coachId, sts);
    }
    /**推荐场馆教练*/
    @Override
    public List<Map<String, Object>> recommendStoreCoach(Long storeId) {
        List<Map<String,Object>> list = storeCoachMapper.recommendStoreCoach(storeId);
        for (Map<String,Object> map : list) {
            //教练评分总和
            Integer evLevelSum = storeCoachEvaluateMapper.sumStoreCoachEvleve((Long) map.get("coachId"));
            //累计评价条数
            Integer classCount = storeCoachEvaluateMapper.countStoreCoachEvleve((Long) map.get("coachId"));
            if (classCount == 0){
                map.put("score",0);
                map.put("level",0);
            }else {
                BigDecimal score = BigDecimal.valueOf(evLevelSum).divide(BigDecimal.valueOf(classCount),1,BigDecimal.ROUND_HALF_DOWN);
                map.put("score",score);
                map.put("level",score);
            }
        }
        try {
            //排序
            Collections.sort(list, new Comparator<Map<String, Object>>() {
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Double score1 = Double.valueOf(o1.get("score").toString()) ;//score1是从你list里面拿出来的一个
                    Double score2 = Double.valueOf(o2.get("score").toString()) ; //score2是从你list里面拿出来的第二个
                    return score2.compareTo(score1);
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Map<String, Object> selectStoreCoachInfo(Map<String, Object> params) {
        Map<String,Object> map = storeCoachMapper.selectStoreCoachByCoachId(params);
        if(map == null){ return new HashMap<>();}
        Map<String,Object> storeMap = storeMapper.queryStoreInfo(Long.valueOf((String) params.get("storeId")));
        String backGroundImg = ((String) storeMap.get("storeImgUrl")).split(",")[0];
        map.put("backGroundImg",backGroundImg);
        //教练评分总和
        int evLevelSum = storeCoachEvaluateMapper.sumStoreCoachEvleve((Long) map.get("coachId"));
        //累计评价条数
        int classCount = storeCoachEvaluateMapper.countStoreCoachEvleve((Long) map.get("coachId"));
        if (classCount == 0){
            map.put("score",0);
            map.put("level",0);
        }else {
            BigDecimal score = BigDecimal.valueOf(evLevelSum).divide(BigDecimal.valueOf(classCount),1,BigDecimal.ROUND_HALF_DOWN);
            map.put("score",score);
            map.put("level",score);
        }
        return map;
    }

    @Override
    public CoachWallet queryCoachWalletEntity(Long coachId) {
        return coachWalletMapper.selectByCoachId(coachId);
    }


    @Override
    public int addCoachWallet(CoachWallet coachWallet) {
      return coachWalletMapper.insertSelective(coachWallet);
    }

}
