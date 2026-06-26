package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.DateUtils;
import com.dlc.modules.api.dao.BodyDimensionMapper;
import com.dlc.modules.api.dao.BodyShapeMapper;
import com.dlc.modules.api.dao.BodyStatusMapper;
import com.dlc.modules.api.dao.BodysInfoMapper;
import com.dlc.modules.api.entity.BodyDimension;
import com.dlc.modules.api.entity.BodyShape;
import com.dlc.modules.api.entity.BodyStatus;
import com.dlc.modules.api.entity.BodysInfo;
import com.dlc.modules.api.service.BodyInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-10-27 10:49
 */

@Service
@Transactional
public class BodyInfoServiceImpl implements BodyInfoService {

    @Autowired
    private BodysInfoMapper bodysInfoMapper;

    @Autowired
    private BodyDimensionMapper bodyDimensionMapper;

    @Autowired
    private BodyStatusMapper bodyStatusMapper;

    @Autowired
    private BodyShapeMapper bodyShapeMapper;

    @Override
    public int saveOrUpdateBodyInfo(JSONObject bodyInfoMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", bodyInfoMap.get("userId"));
        param.put("createTime", DateUtils.format(new Date()));
        BodysInfo bodysInfo = bodysInfoMapper.queryBodyInfoByUserId(param);
        if (bodysInfo != null) {
            //更新进入
            System.out.println("bodys_info更新，" + bodysInfo);
            bodysInfo.setCreateTime(new Date());
            bodysInfo.setBmi(bodyInfoMap.get("Bmi") + "");
            bodysInfo.setBodyFat(bodyInfoMap.get("BodyFat") + "");
            bodysInfo.setFluid(bodyInfoMap.get("Fluid") + "");
            bodysInfo.setKcal(bodyInfoMap.get("Kcal") + "");
            bodysInfo.setMuscle(bodyInfoMap.get("Muscle") + "");
            bodysInfo.setPercentBodyFat(bodyInfoMap.get("PercentBodyFat") + "");
            bodysInfo.setScanid(bodyInfoMap.get("ScanId") + "");
            bodysInfo.setWaistToHip(bodyInfoMap.get("WaistToHip") + "");
            bodysInfo.setWeight(bodyInfoMap.get("Weight") + "");
            bodysInfo.setResult(bodyInfoMap.get("Result") + "");
            System.out.println("++++++进入bodyInfo更新++++++++++");
            int res = bodysInfoMapper.updateByPrimaryKeySelective(bodysInfo);
            if (res > 0) {
                System.out.println("++++++bodyInfo更新成功++++++++++");
            }
            return res;

        }
        BodysInfo newBodyInfo = new BodysInfo();
        newBodyInfo.setUserId((long) bodyInfoMap.get("userId"));
        newBodyInfo.setVid((long) bodyInfoMap.get("vid"));
        newBodyInfo.setBmi(bodyInfoMap.get("Bmi") + "");
        newBodyInfo.setBodyFat(bodyInfoMap.get("BodyFat") + "");
        newBodyInfo.setFluid(bodyInfoMap.get("Fluid") + "");
        newBodyInfo.setKcal(bodyInfoMap.get("Kcal") + "");
        newBodyInfo.setWaistToHip(bodyInfoMap.get("WaistToHip") + "");
        newBodyInfo.setMuscle(bodyInfoMap.get("Muscle") + "");
        newBodyInfo.setCreateTime(new Date());
        newBodyInfo.setPercentBodyFat(bodyInfoMap.get("PercentBodyFat") + "");
        newBodyInfo.setScanid(bodyInfoMap.get("ScanId") + "");
        newBodyInfo.setWeight(bodyInfoMap.get("Weight") + "");
        newBodyInfo.setResult(bodyInfoMap.get("Result") + "");
        newBodyInfo.setGymEngineId((long) bodyInfoMap.getOrDefault("gymEngineId", "0"));
        System.out.println("bodys_info插入，" + newBodyInfo);
        return bodysInfoMapper.insertSelective(newBodyInfo);
    }

    @Override
    public Map<String, Object> queryBodyInfoByUserId(Long userId) {

        return bodysInfoMapper.queryBodyInfoLastRecord(userId);


    }

    @Override
    public int saveOrUpdateBodyDimension(JSONObject bodyDimensionMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", bodyDimensionMap.get("userId"));
        param.put("createTime", DateUtils.format(new Date()));
        BodyDimension bodyDimension = bodysInfoMapper.queryBodyDimensionByUserId(param);

        if (bodyDimension != null) {
            System.out.println("body_dimension更新，" + bodyDimension);
            //更新进入
            bodyDimension.setCreateTime(new Date());
            bodyDimension.setWaistGirth(bodyDimensionMap.get("WaistGirth") + "");
            bodyDimension.setBustGirth(bodyDimensionMap.get("BustGirth") + "");
            bodyDimension.setRightThighGirth(bodyDimensionMap.get("RightThighGirth") + "");
            bodyDimension.setRightCalfGirth(bodyDimensionMap.get("RightCalfGirth") + "");
            bodyDimension.setRightUpperArmGirth(bodyDimensionMap.get("RightUpperArmGirth") + "");
            bodyDimension.setLeftCalfGirth(bodyDimensionMap.get("LeftCalfGirth") + "");
            bodyDimension.setLeftThighGirth(bodyDimensionMap.get("LeftThighGirth") + "");
            bodyDimension.setLeftUpperArmGirth(bodyDimensionMap.get("LeftUpperArmGirth") + "");
            bodyDimension.setHipGirth(bodyDimensionMap.get("HipGirth") + "");
            bodyDimension.setHeight(bodyDimensionMap.get("Height") + "");
            bodyDimension.setScanId(bodyDimensionMap.get("ScanId") + "");
            System.out.println("++++++进入bodyInfoDimension更新++++++++++");
            System.out.println("bodyInfoDimension"+bodyDimension.toString());

            int res = bodyDimensionMapper.updateByPrimaryKeySelective(bodyDimension);
            if (res > 0) {
                System.out.println("++++++bodyDimension更新成功++++++++++");
            }
            return res;
        }


        BodyDimension newBodyDimension = new BodyDimension();
        newBodyDimension.setCreateTime(new Date());
        newBodyDimension.setScanId(bodyDimensionMap.get("ScanId") + "");
        newBodyDimension.setUserId((long) bodyDimensionMap.get("userId"));
        newBodyDimension.setVid((long) bodyDimensionMap.get("vid"));
        newBodyDimension.setWaistGirth(bodyDimensionMap.get("WaistGirth") + "");
        newBodyDimension.setBustGirth(bodyDimensionMap.get("BustGirth") + "");
        newBodyDimension.setRightThighGirth(bodyDimensionMap.get("RightThighGirth") + "");
        newBodyDimension.setRightCalfGirth(bodyDimensionMap.get("RightCalfGirth") + "");
        newBodyDimension.setRightUpperArmGirth(bodyDimensionMap.get("RightUpperArmGirth") + "");
        newBodyDimension.setLeftCalfGirth(bodyDimensionMap.get("LeftCalfGirth") + "");
        newBodyDimension.setLeftThighGirth(bodyDimensionMap.get("LeftThighGirth") + "");
        newBodyDimension.setLeftUpperArmGirth(bodyDimensionMap.get("LeftUpperArmGirth") + "");
        newBodyDimension.setHipGirth(bodyDimensionMap.get("HipGirth") + "");
        newBodyDimension.setHeight(bodyDimensionMap.get("Height") + "");
        newBodyDimension.setGymEngineId((long) bodyDimensionMap.getOrDefault("gymEngineId", "0"));
        System.out.println("body_dimension插入，" + newBodyDimension);
        return bodyDimensionMapper.insertSelective(newBodyDimension);
        }

    @Override
    public int saveOrUpdateBodyStatus(JSONObject bodyStatusMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", bodyStatusMap.get("userId"));
        param.put("createTime", DateUtils.format(new Date()));
        BodyStatus bodyStatus = bodysInfoMapper.queryBodyStatusByUserId(param);


        if (bodyStatus != null) {
            //更新进入
            System.out.println("body_status更新" + bodyStatus);
            bodyStatus.setCreateTime(new Date());
            bodyStatus.setScanId(bodyStatusMap.get("ScanId") + "");
            bodyStatus.setAcromionReference(bodyStatusMap.get("AcromionReference") + "");
            bodyStatus.setCavumConchae(bodyStatusMap.get("CavumConchae") + "");
            bodyStatus.setHipJoint(bodyStatusMap.get("HipJoint") + "");
            bodyStatus.setLeftLeg(bodyStatusMap.get("LeftLeg") + "");
            bodyStatus.setLeftShoulder(bodyStatusMap.get("LeftShoulder") + "");
            bodyStatus.setRightLeg(bodyStatusMap.get("RightLeg") + "");
            bodyStatus.setRightShoulder(bodyStatusMap.get("RightShoulder") + "");
            bodyStatus.setShoulderMidpoint(bodyStatusMap.get("ShoulderMidpoint") + "");
            bodyStatus.setResults(bodyStatusMap.get("Results")+"");
            System.out.println("++++++进入bodyInfo更新++++++++++");

            int res = bodyStatusMapper.updateByPrimaryKeySelective(bodyStatus);
            if (res > 0) {
                System.out.println("++++++bodyStatus更新成功++++++++++");
            }
            return res;
        }

        BodyStatus newBodyStatus = new BodyStatus();
        newBodyStatus.setCreateTime(new Date());
        newBodyStatus.setScanId(bodyStatusMap.get("ScanId") + "");
        newBodyStatus.setUserId((long) bodyStatusMap.get("userId"));
        newBodyStatus.setVid((long) bodyStatusMap.get("vid"));
        newBodyStatus.setResults(bodyStatusMap.get("Results")+"");
        newBodyStatus.setShoulderMidpoint(bodyStatusMap.get("ShoulderMidpoint")+"");
        newBodyStatus.setRightShoulder(bodyStatusMap.get("RightShoulder")+"");
        newBodyStatus.setLeftShoulder(bodyStatusMap.get("LeftShoulder")+"");
        newBodyStatus.setLeftLeg(bodyStatusMap.get("LeftLeg")+"");
        newBodyStatus.setRightLeg(bodyStatusMap.get("RightLeg")+"");
        newBodyStatus.setCavumConchae(bodyStatusMap.get("CavumConchae")+"");
        newBodyStatus.setAcromionReference(bodyStatusMap.get("AcromionReference")+"");
        System.out.println("body_status插入" + newBodyStatus);
        return bodyStatusMapper.insertSelective(newBodyStatus);
    }

    @Override
    public int saveOrUpdateBodyShape(JSONObject bodyShapeMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("userId", bodyShapeMap.get("userId"));
        param.put("createTime", DateUtils.format(new Date()));
        BodyShape bodyShape = bodyShapeMapper.queryBodyShapeByUserId(param);

        if (bodyShape != null) {
            //更新进入
            System.out.println("body_shape插入，" + bodyShape);
            bodyShape.setCreateTime(new Date());
            bodyShape.setScanId(bodyShapeMap.get("ScanId") + "");
            bodyShape.setResults(bodyShapeMap.get("Result") + "");
            System.out.println("++++++进入bodyInfo更新++++++++++");

            int res = bodyShapeMapper.updateByPrimaryKeySelective(bodyShape);
            if (res > 0) {
                System.out.println("++++++bodyStatus更新成功++++++++++");
            }
            return res;
        }

        BodyShape newBodyShape = new BodyShape();
        newBodyShape.setScanId(bodyShapeMap.get("ScanId") + "");
        newBodyShape.setUserId((long) bodyShapeMap.get("userId"));
        newBodyShape.setResults(bodyShapeMap.get("Result")+"");
        newBodyShape.setGymEngineId((long) bodyShapeMap.getOrDefault("gymEngineId", "0"));
        newBodyShape.setCreateTime(new Date());
        System.out.println("body_shape插入，" + newBodyShape);
        return bodyShapeMapper.insertSelective(newBodyShape);
    }

    @Override
    public List<Map<String, Object>> queryBodyDimensionInfo(Long userId) {
        //查出最近两次的围度信息

        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> list = bodyDimensionMapper.queryBodyDimensionList(userId);
        if (list.isEmpty()) {
            return null;

        } else if (list.size() == 1) {
            //只有一条记录的情况
            Map<String, Object> dimensionMap = list.get(0);
            for (Map.Entry<String, Object> entry : dimensionMap.entrySet()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", entry.getKey());
                itemMap.put("value", entry.getValue());
                itemMap.put("difference", 0.0);
                resultList.add(itemMap);
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
            return resultList;
        } else if (list.size() == 2) {
            Map<String, Object> oneMap = list.get(0);
            Map<String, Object> twoMap = list.get(1);
            for (Map.Entry<String, Object> entry : oneMap.entrySet()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", entry.getKey());
                itemMap.put("value", entry.getValue());
                BigDecimal dec1 = new BigDecimal(entry.getValue() + "");
                BigDecimal dec2 = new BigDecimal(twoMap.get(entry.getKey() + "") + "");
                itemMap.put("difference", dec1.subtract(dec2));
                resultList.add(itemMap);
            }
            return resultList;
        }
        return null;
    }

    @Override
    public Map<String, Object> queryBodyDimensionScanId(Long userId) {
        return bodyDimensionMapper.queryBodyDimensionScanId(userId);
    }

    @Override
    public List<Map<String, Object>> queryBodyInfoBetweenTime(Map<String, Object> paramMap) {
       return  bodyStatusMapper.queryBodyInfoBetweenTime(paramMap);
    }

    @Override
    public List<Map<String, Object>> queryBodyBodyStatusByUserId(Long userId) {
        List<Map<String,Object>> list = bodyStatusMapper.queryBodyBodyStatusByUserId(userId);
        return list;
    }

    @Override
    public List<Map<String, Object>> queryBodyShapeLastRecord(Long userId) {
        List<Map<String,Object>> list = bodyShapeMapper.queryBodyShapeLastRecord(userId);
        return list;
    }

    @Override
    public JSONObject bodyinfoFormatData(JSONObject bodyInfoMap){
        JSONObject data = new JSONObject();
        data.put("Weight", bodyInfoMap.getJSONObject("WT").getString("v") );
        data.put("BodyFat", bodyInfoMap.getJSONObject("BFM").getString("v") );
        data.put("PercentBodyFat", bodyInfoMap.getJSONObject("PBF").getString("v") );
        data.put("Bmi", bodyInfoMap.getJSONObject("BMI").getString("v") );
        data.put("Kcal", bodyInfoMap.getJSONObject("BMR").getString("v") );
        data.put("WaistToHip", bodyInfoMap.getJSONObject("WHR").getString("v") );
        data.put("Fluid", bodyInfoMap.getJSONObject("TBW").getString("v") );
        data.put("Muscle", bodyInfoMap.getJSONObject("LM").getString("v") );
        data.put("Result", bodyInfoMap.toJSONString() );
        return data;
    }

    @Override
    public JSONObject bodyDimensFormatData(JSONObject bodyDimensionMap){
        JSONObject data = new JSONObject();
        data.put("Height", bodyDimensionMap.getString("height"));
        data.put("LeftUpperArmGirth", bodyDimensionMap.getString("left_upper_arm_girth"));
        data.put("RightUpperArmGirth", bodyDimensionMap.getString("right_upper_arm_girth"));
        data.put("BustGirth", bodyDimensionMap.getString("bust_girth"));
        data.put("WaistGirth", bodyDimensionMap.getString("waist_girth"));
        data.put("HipGirth", bodyDimensionMap.getString("hip_girth"));
        data.put("RightThighGirth", bodyDimensionMap.getString("right_thigh_girth"));
        data.put("LeftThighGirth", bodyDimensionMap.getString("left_thigh_girth"));
        data.put("RightCalfGirth", bodyDimensionMap.getString("right_calf_girth"));
        data.put("LeftCalfGirth", bodyDimensionMap.getString("left_calf_girth"));
        return data;
    }

    @Override
    public JSONObject bodyStatusFormatData(JSONObject bodyStatusMap){
        JSONObject data = new JSONObject();
        data.put("LeftLeg", bodyStatusMap.getString("height"));
        data.put("RightLeg", bodyStatusMap.getString("left_upper_arm_girth"));
        data.put("LeftShoulder", bodyStatusMap.getString("right_upper_arm_girth"));
        data.put("RightShoulder", bodyStatusMap.getString("bust_girth"));
        data.put("CavumConchae", bodyStatusMap.getString("waist_girth"));
        data.put("HipJoint", bodyStatusMap.getString("hip_girth"));
        data.put("ShoulderMidpoint", bodyStatusMap.getString("right_thigh_girth"));
        data.put("AcromionReference", bodyStatusMap.getString("left_thigh_girth"));
        data.put("results", bodyStatusMap.getString("right_calf_girth"));
        return data;
    }

    @Override
    public JSONObject bodyShapeFormatData(JSONObject bodyShapeMap){
        JSONObject data = new JSONObject();
        data.put("Result", bodyShapeMap.toJSONString());
        return data;
    }

}
