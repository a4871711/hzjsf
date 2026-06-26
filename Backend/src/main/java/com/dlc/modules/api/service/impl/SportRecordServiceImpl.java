package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.modules.api.dao.PointsExchangeMapper;
import com.dlc.modules.api.dao.SportRecordMapper;
import com.dlc.modules.api.dao.UserPointsMapper;
import com.dlc.modules.api.dao.UserSportDeviceRecordMapper;
import com.dlc.modules.api.entity.PointsExchange;
import com.dlc.modules.api.entity.SportActive;
import com.dlc.modules.api.service.SportRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/17 9:09
 */
@Service
public class SportRecordServiceImpl implements SportRecordService {
    @Value("${oxyKcal}")
    public Double oxyKcal;
    @Value("${unoxyKcal}")
    public Double unoxyKcal;
    @Autowired
    private SportRecordMapper sportRecordMapper;
    @Autowired
    private UserPointsMapper userPointsMapper;
    @Autowired
    private UserSportDeviceRecordMapper userSportDeviceRecordMapper;
    @Autowired
    private PointsExchangeMapper pointsExchangeMapper;

    public Map<String, Object> querySportRecord(Long userId) {
        Map<String, Object> map = this.sportRecordMapper.querySportRecord(userId);
        return map;
    }


    public Map<String, Object> querySportListByType(Integer sportType, Long userId) {
        if (sportType == null) {
            sportType = 0;
        }
        Map<String, Object> map = this.sportRecordMapper.querySportListByType(sportType, userId);

        if (sportType == 0) {
            Map<String, Object> recordmap = null;
            String oxyKcalP = null;
            String unoxyKcalP = null;
            String teamClassKcalP = "";
            String privateClassKcalp = "";
            BigDecimal oxy = null;
            BigDecimal uno = null;
            Map<String, Object> tMap = new HashMap<>();
            //oxyKcal = (Double) map.get("oxyKcal");
            //unoxyKcal = (Double) map.get("unoxyKcal");

            //recordmap = this.sportRecordMapper.querySportRecord(userId);
            //查课程消耗能量总和的当天数据
            BigDecimal teamClassKcal = sportRecordMapper.querySportRecordTClass(userId);
            if(teamClassKcal == null){
                teamClassKcal = BigDecimal.valueOf(0.0);
            }
            BigDecimal privateClassKcal = sportRecordMapper.querySportRecordPClass(userId);
            if(privateClassKcal == null){
                privateClassKcal = BigDecimal.valueOf(0.0);
            }

            /*获取当天训练总能量*/
            //BigDecimal energy = this.userSportDeviceRecordMapper.queryEnergySumByUserId(userId);
            BigDecimal energy = this.userSportDeviceRecordMapper.queryCurrEnergySumByUserId(userId);
            if (energy == null)
                energy = BigDecimal.valueOf(0.0);
            //Double energySum = (energy+teamClassKcal+privateClassKcal);
            BigDecimal energySum = energy.add(teamClassKcal).add(privateClassKcal);
            if (energySum == null || energySum.compareTo(BigDecimal.ZERO) == 0) {
                tMap.put("energy", 0.0);
                tMap.put("oxyKcal", "0%");
                tMap.put("unoxyKcal", "0%");
                tMap.put("teamClassKcal", "0%");
                tMap.put("privateClassKcal", "0%");
                tMap.put("userId", userId);
                return tMap;
            }
            BigDecimal team = (teamClassKcal.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            //teamClassKcalP = team + "%";

            BigDecimal privateC = (privateClassKcal.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            //privateClassKcalp = privateC + "%";


            //有氧
            oxy = this.userSportDeviceRecordMapper.queryOXYEnergySumByUserId(userId);
            //无氧
            uno = this.userSportDeviceRecordMapper.queryUnoxyKcalEnergySumByUserId(userId);

            oxy = (oxy.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            //oxyKcalP = oxy + "%";
            uno = (uno.divide(energySum, 2, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(100));
            //unoxyKcalP = uno + "%";


            //精确百分比
            if(privateC.compareTo(BigDecimal.ZERO) == 1){
                privateC = new BigDecimal("100.00").subtract(oxy.add(uno).add(team));
            }else if(team.compareTo(BigDecimal.ZERO) == 1){
                team = new BigDecimal("100.00").subtract(oxy.add(uno).add(privateC));
            }else if(uno.compareTo(BigDecimal.ZERO) == 1){
                uno = new BigDecimal("100.00").subtract(oxy.add(team).add(privateC));
            }else if(oxy.compareTo(BigDecimal.ZERO) == 1){
                oxy = new BigDecimal("100.00").subtract(uno.add(team).add(privateC));
            }

            tMap.put("energy", energySum);
            tMap.put("oxyKcal", oxy + "%");
            tMap.put("unoxyKcal",  uno + "%");
            tMap.put("teamClassKcal", team + "%");
            tMap.put("privateClassKcal", privateC + "%");
            tMap.put("userId", userId);
            return tMap;

        }
        //运动类型为体测暂时不做处理（运动类型（0：训练1：户外跑 2：行走3：骑行4：体测）
        if (map == null) {
            map = new HashMap<>();
            map.put("distance", 0);
            map.put("oxyKcal", 0);
            map.put("unoxyKcal", 0);
            map.put("sportStep", 0);
            map.put("sportTime", 0);
            map.put("energy", 0);
            map.put("userId", userId);
        }
        return map;
    }


    public JSONObject sportDataCount(SportActive sportActive) {
        List<Map<String, Object>> list = null;
        JSONObject jsonObject = null;
        Map<String, Object> mapInfo = null;
        Integer cishu = 0;
        Double energy = 0.0;
        Double sportStep = 0.0;
        String sumST = null;
        Double distance = 0.0;
        Time time = null;
        Double aveSpeed = null;
        if (sportActive.getCountType() == null) {
            sportActive.setCountType(0);
        }
        Integer countType = sportActive.getCountType();
        if (countType == 0) {
            //周统计(统计本周)
            list = sportRecordMapper.sportDataCountByWeek(sportActive);
            if (list.size() != 0) {
                for (Map map : list) {
                    mapInfo = new HashMap<>();
                    Double energyTemp = 0.0;
                    Double sportStepTemp = 0.0;
                    Double distanceTemp = 0.0;

                    energyTemp = (Double) map.get("energy");//消耗能量
                    energy += energyTemp;

                    sportStepTemp = (Double) map.get("sportStep");
                    sportStep += sportStepTemp;//运动步数
                    distanceTemp = (Double) map.get("distance");
                    distance += distanceTemp;
                    BigDecimal disSum = BigDecimal.valueOf(distance);
                    //运动公里数保留2位
                    mapInfo.put("sumDistance", disSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                    // 米/秒
                    Time spTime = (Time) map.get("sportTime");
                    Double spHou = spTime.getHours() * 60.0 * 60.0;
                    Double spMin = spTime.getMinutes() * 60.0;
                    Double spSec = spTime.getSeconds() * 1.0;
                    BigDecimal spBig = new BigDecimal((distanceTemp * 1000) / (spHou + spMin + spSec));
                    Double aveSp = spBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    map.put("aveSp", aveSp);
                    mapInfo.put("cishu", map.get("cishu"));

                    map.remove("distance");
                    map.remove("cishu");
                    sumST = map.get("sumST").toString();

                    time = (Time) map.get("sumST");
                    Double hou = time.getHours() * 60.0 * 60.0;
                    Double min = time.getMinutes() * 60.0;
                    Double sec = time.getSeconds() * 1.0;

                    BigDecimal big = new BigDecimal((distance * 1000) / (hou + min + sec));
                    aveSpeed = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();


                }

                mapInfo.put("sumEnergy", energy);
                mapInfo.put("sumSportStep", sportStep);
                mapInfo.put("sumST", sumST);
                mapInfo.put("aveSpeed", aveSpeed);
            } else {
                mapInfo = new HashMap<>();
                mapInfo.put("cishu", 0);
                mapInfo.put("sumEnergy", 0);
                mapInfo.put("sumSportStep", 0);
                mapInfo.put("aveSpeed", 0.0);
                mapInfo.put("sumST", 0);
                mapInfo.put("sumDistance", 0);
            }


        } else if (countType == 1) {
            //月统计(以周为单位统计本月记录)
            list = sportRecordMapper.sportDataCountByMooth(sportActive);

            if (list.size() != 0) {
                for (Map map : list) {
                    mapInfo = new HashMap<>();
                    Double energyTemp = 0.0;
                    Double sportStepTemp = 0.0;
                    Double distanceTemp = 0.0;


                    energyTemp = (Double) map.get("energy");//消耗能量
                    energy += energyTemp;

                    sportStepTemp = (Double) map.get("sportStep");
                    sportStep += sportStepTemp;//运动步数或者速度
                    distanceTemp = (Double) map.get("distance");
                    distance += distanceTemp;
                    BigDecimal disSum = BigDecimal.valueOf(distance);
                    //运动公里数保留2位
                    mapInfo.put("sumDistance", disSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                    Time spTime = (Time) map.get("sportTime");
                    Double spHou = spTime.getHours() * 60.0 * 60.0;
                    Double spMin = spTime.getMinutes() * 60.0;
                    Double spSec = spTime.getSeconds() * 1.0;
                    BigDecimal spBig = new BigDecimal((distanceTemp * 1000) / (spHou + spMin + spSec));
                    Double aveSp = spBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    map.put("aveSp", aveSp);
                    String cs = map.get("cishu").toString();
                    cishu += Integer.valueOf(cs);

                    mapInfo.put("cishu", cishu);

                    map.remove("distance");
                    map.remove("cishu");

                    sumST = map.get("sumST").toString();


                    time = (Time) map.get("sumST");
                    Double hou = time.getHours() * 60.0 * 60.0;
                    Double min = time.getMinutes() * 60.0;
                    Double sec = time.getSeconds() * 1.0;

                    BigDecimal big = new BigDecimal((distance * 1000) / (hou + min + sec));
                    aveSpeed = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                    /*mapInfo.put("cishu",cishu);*/
                mapInfo.put("sumEnergy", energy);
                mapInfo.put("sumSportStep", sportStep);
                mapInfo.put("sumST", sumST);
                mapInfo.put("sumDistance", distance);
                mapInfo.put("aveSpeed", aveSpeed);
            } else {
                mapInfo = new HashMap<>();
                mapInfo.put("cishu", 0);
                mapInfo.put("aveSpeed", 0.0);
                mapInfo.put("sumEnergy", 0);
                mapInfo.put("sumSportStep", 0);
                mapInfo.put("sumST", 0);
                mapInfo.put("sumDistance", 0);
            }

        } else if (countType == 2) {
            //年统计(以月为单位统计每年数据)
            list = sportRecordMapper.sportDataCountByYear(sportActive);
            if (list.size() != 0) {
                for (Map map : list) {
                    mapInfo = new HashMap<>();
                    Double energyTemp = 0.0;
                    Double sportStepTemp = 0.0;
                    Double distanceTemp = 0.0;

                    energyTemp = (Double) map.get("energy");//消耗能量
                    energy += energyTemp;

                    sportStepTemp = (Double) map.get("sportStep");
                    sportStep += sportStepTemp;//运动步数或者速度
                    distanceTemp = (Double) map.get("distance");
                    distance += distanceTemp;
                    BigDecimal disSum = BigDecimal.valueOf(distance);
                    //运动公里数保留2位
                    mapInfo.put("sumDistance", disSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                    Time spTime = (Time) map.get("sportTime");
                    Double spHou = spTime.getHours() * 60.0 * 60.0;
                    Double spMin = spTime.getMinutes() * 60.0;
                    Double spSec = spTime.getSeconds() * 1.0;
                    BigDecimal spBig = new BigDecimal((distanceTemp * 1000) / (spHou + spMin + spSec));
                    Double aveSp = spBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    map.put("aveSp", aveSp);

                    String cs = map.get("cishu").toString();
                    cishu += Integer.valueOf(cs);
                    mapInfo.put("cishu", cishu);
                    map.remove("distance");
                    map.remove("cishu");

                    sumST = map.get("sumST").toString();

                    time = (Time) map.get("sumST");
                    Double hou = time.getHours() * 60.0 * 60.0;
                    Double min = time.getMinutes() * 60.0;
                    Double sec = time.getSeconds() * 1.0;

                    BigDecimal big = new BigDecimal((distance * 1000) / (hou + min + sec));
                    aveSpeed = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                mapInfo.put("sumEnergy", energy);
                mapInfo.put("sumSportStep", sportStep);
                mapInfo.put("sumST", sumST);
                mapInfo.put("sumDistance", distance);
                mapInfo.put("aveSpeed", aveSpeed);
            } else {
                mapInfo = new HashMap<>();
                mapInfo.put("aveSpeed", 0.0);
                mapInfo.put("cishu", 0);
                mapInfo.put("sumEnergy", 0);
                mapInfo.put("sumSportStep", 0);
                mapInfo.put("sumST", 0);
                mapInfo.put("sumDistance", 0);
            }

        } else if (countType == 3) {
            //总
            list = sportRecordMapper.sportDataCount(sportActive);
            if (list.size() != 0) {
                for (Map map : list) {
                    mapInfo = new HashMap<>();
                    Double energyTemp = 0.0;
                    Double sportStepTemp = 0.0;
                    Double distanceTemp = 0.0;


                    energyTemp = (Double) map.get("energy");//消耗能量
                    energy += energyTemp;

                    sportStepTemp = (Double) map.get("sportStep");
                    sportStep += sportStepTemp;//运动步数或者速度
                    distanceTemp = (Double) map.get("distance");
                    distance += distanceTemp;
                    BigDecimal disSum = BigDecimal.valueOf(distance);
                    //运动公里数保留2位
                    mapInfo.put("sumDistance", disSum.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

                    Time spTime = (Time) map.get("sumST");
                    Double spHou = spTime.getHours() * 60.0 * 60.0;
                    Double spMin = spTime.getMinutes() * 60.0;
                    Double spSec = spTime.getSeconds() * 1.0;
                    BigDecimal spBig = new BigDecimal((distanceTemp * 1000) / (spHou + spMin + spSec));
                    Double aveSp = spBig.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

                    map.put("aveSp", aveSp);

                        /*String cs  =  map.get("cishu").toString();
                        cishu+=Integer.valueOf(cs);
                        mapInfo.put("cishu",cishu);
                        map.remove("distance");
                        map.remove("cishu");*/


                    mapInfo.put("cishu", map.get("cishu"));
                    map.remove("distance");
                    map.remove("cishu");

                    sumST = map.get("sumST").toString();
                    time = (Time) map.get("sumST");
                    Double hou = time.getHours() * 60.0 * 60.0;
                    Double min = time.getMinutes() * 60.0;
                    Double sec = time.getSeconds() * 1.0;

                    BigDecimal big = new BigDecimal((distance * 1000) / (hou + min + sec));
                    aveSpeed = big.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                   /* mapInfo.put("cishu",cishu);*/
                mapInfo.put("sumEnergy", energy);
                mapInfo.put("sumSportStep", sportStep);
                mapInfo.put("sumST", sumST);
                mapInfo.put("sumDistance", distance);
                mapInfo.put("aveSpeed", aveSpeed);
            } else {
                mapInfo = new HashMap<>();
                mapInfo.put("cishu", 0);
                mapInfo.put("sumEnergy", 0);
                mapInfo.put("sumSportStep", 0);
                mapInfo.put("sumST", 0);
                mapInfo.put("sumDistance", 0);
                mapInfo.put("aveSpeed", 0.0);
            }

        }
        jsonObject = new JSONObject();
        mapInfo.put("list", list);
        jsonObject.put("mapInfo", mapInfo);
        return jsonObject;
    }


    public int saveSportData(Map<String, Object> map) {
        int res = sportRecordMapper.saveSportData(map);
        //能量兑换积分 add new function
        try {
            Long userId = (Long) map.get("userId");
            //可兑换积分
            Integer pointEx = userPointsMapper.getPointByCal(map);
            //查询当天积分获取情况和是否为卡类用户以及对应积分上限
            //Map<String, Object> infoMap = pointsExchangeMapper.querySumDayPoint(userId);
            Integer dayPoint = pointsExchangeMapper.querySumDayPoint(userId);  //当天积分兑换总和
            Integer rulePoint = pointsExchangeMapper.queryRulePoints(userId);   //规则积分上限值
            if (dayPoint == null) {
                dayPoint = 0;
            }
            if (rulePoint == null) {
                rulePoint = 0;
            }
            if (dayPoint < rulePoint) {
                //判断兑换后积分是否超出用户一天兑换上限,如果超出
                if ((dayPoint + pointEx) > rulePoint) {
                    pointEx = rulePoint - dayPoint;
                }
                if (pointEx > 0) {
                    map.put("point", pointEx);
                    int rr = userPointsMapper.updatePointByCal(map);
                    if (rr > 0) {
                        //积分兑换记录
                        PointsExchange pointsExchange = new PointsExchange();
                        pointsExchange.setUserId(userId);
                        pointsExchange.setPointsCount(pointEx);
                        pointsExchange.setDetailType((byte) 1);  //运动积分+
                        pointsExchange.setExchangeDate(new Date());
                        pointsExchangeMapper.insertSelective(pointsExchange);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //add end
        return res;
    }

    public List<Map<String, Object>> queryEnergy() {
        return sportRecordMapper.queryEnergy();
    }

    @Override
    public Long getSysFlag() {
        return userPointsMapper.getSysFlags();
    }
}
