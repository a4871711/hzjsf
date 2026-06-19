package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.StringUtil;
import com.dlc.modules.api.dao.FaceIdentyRecordMapper;
import com.dlc.modules.api.dao.OpenDoorRecordMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.dao.StoreMapper;
import com.dlc.modules.api.entity.Store;
import com.dlc.modules.api.entity.StoreAddress;
import com.dlc.modules.api.service.StoreService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@Service
@Transactional
public class StoreServiceImpl implements StoreService{

    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private StoreAddressMapper storeAddressMapper;
    @Autowired
    private FaceIdentyRecordMapper faceIdentyRecordMapper;

    @Autowired
    private OpenDoorRecordMapper openDoorRecordMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  门店详情
     *  findAddAndDistance : 查询门店的地址和距离
     */
    @Override
    public Map<String,Object> queryStoreInfo(Map<String,Object> params) {
        Map<String,Object> map = storeMapper.queryStoreInfo(Long.valueOf((String) params.get("storeId")));
        if (map != null) {
            Map<String, Object> saMap = storeAddressMapper.findAddAndDistance(params);
            if (saMap == null) {
                saMap.put("distance", "未知");
                map.put("storeAddrDetail","未知");

            } else {
                BigDecimal distance = BigDecimal.ZERO;
                if (saMap.get("distance") != null && StringUtils.isNotBlank(saMap.get("distance").toString())) {
                    BigDecimal bigDecimal = BigDecimal.valueOf((Double) saMap.get("distance")).divide(new BigDecimal(1000));
                    distance = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                map.put("distance", distance);
                if(StringUtils.isBlank((String) params.get("userLat")) || StringUtils.isBlank((String) params.get("userLng"))){
                    map.put("distance", "未知");
                }
                if (saMap.get("province") != null || saMap.get("city") != null || saMap.get("zone") != null || saMap.get("storeAddrDetail") != null){
                    map.put("storeAddrDetail",saMap.get("province")+" "+saMap.get("city")+" "+saMap.get("zone")+" "+saMap.get("storeAddrDetail"));
                }else {
                    map.put("storeAddrDetail","未知");
                }
            }
            if("0".equalsIgnoreCase( map.get("currentNum").toString() )){
                String storeId = String.valueOf( params.get("storeId").toString() );
                int peopleNum = openDoorRecordMapper.getStorePeopleTotal(storeId);
                map.put("currentNum", peopleNum);
            }
        }else {
            map = new HashMap<>();
        }
        return map;
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  推荐门店按距离
     */
    @Override
    public Map<String, Object> recommendedStores(Map<String, Object> params) {


        //根据门店列表查出距离最近的门店
        Map<String, Object> map = storeAddressMapper.findStoreByListDistance(params);
        if (map == null) {
            Map<String, Object> newMap = new HashMap<>();
            return newMap;
        } else {
            //根据门店id
            Map<String, Object> storeMap = storeMapper.queryStoreInfo((Long) map.get("storeId"));
            if (storeMap == null) {
                storeMap = new HashMap<>();
                //storeMap.put("distance", "0");
                //map.put("storeAddrDetail", "");
            } else {
                BigDecimal distance = BigDecimal.ZERO;
                if (map.get("distance")!=null) {
                    BigDecimal bigDecimal = BigDecimal.valueOf((Double) map.get("distance")).divide(new BigDecimal(1000));
                    distance = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                }
                storeMap.put("distance", distance);
                if (map.get("province") != null || map.get("city") != null || map.get("zone") != null || map.get("storeAddrDetail") != null){
                    map.put("storeAddrDetail",map.get("province")+" "+map.get("city")+" "+map.get("zone")+" "+map.get("storeAddrDetail"));
                }else {
                    map.put("storeAddrDetail","");
                }
            }
            return storeMap;
        }
    }

    @Override
    public String queryStoreName(Long id) {
        //根据门店id查门店名称
        String storeName = null;
        try {
            storeName = storeMapper.queryStoreName(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storeName;
    }

    @Override
    public int deleteFaceRecord() {
        return faceIdentyRecordMapper.deleteRecodeHistory();
    }
}
