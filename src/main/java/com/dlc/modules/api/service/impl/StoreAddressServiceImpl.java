package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.modules.api.dao.CoachMapper;
import com.dlc.modules.api.dao.CoachPlaceShipMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.dao.StoreMapper;
import com.dlc.modules.api.entity.Coach;
import com.dlc.modules.api.entity.Store;
import com.dlc.modules.api.service.StoreAddressService;
import com.dlc.modules.api.vo.UserInfoVo;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@Service
@Transactional
public class StoreAddressServiceImpl implements StoreAddressService{

    @Autowired
    private StoreAddressMapper storeAddressMapper;
    @Autowired
    private CoachMapper coachMapper;
    @Autowired
    private StoreMapper storeMapper;
    @Autowired
    private CoachPlaceShipMapper coachPlaceShipMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  门店列表
     *  距离单词：distance
     */
    @Override
    public List<Map<String,Object>> storeAddressList(Map<String, Object> params) {
        List<Map<String,Object>> list = storeAddressMapper.storeAddressList(params);
        for (Map<String,Object> map : list) {
            String imgg = null;
            if(map.get("storeImgUrl") != null){
                String[] img = map.get("storeImgUrl").toString().split(",");
                imgg = img[0];
            }
            map.put("storeImgUrls", map.get("storeImgUrl"));
            map.put("storeImgUrl",imgg);
            if (map.get("distance") == null) {
                map.put("distance", "0");
                map.put("storeAddrDetail","");

            } else {
                BigDecimal bigDecimal = BigDecimal.valueOf((Double) map.get("distance")).divide(new BigDecimal(1000));
                double distance = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("distance", distance);
                if (map.get("province") != null || map.get("city") != null || map.get("zone") != null || map.get("storeAddrDetail") != null){
                    map.put("storeAddrDetail",map.get("province")+" "+map.get("city")+" "+map.get("zone")+" "+map.get("storeAddrDetail"));
                }else {
                    map.put("storeAddrDetail","");
                }
            }
            Store store = storeMapper.selectByPrimaryKey((Long) map.get("storeId"));
        }
        return list;
    }

    @Override
    public int queryStoreAddressTotal(Map<String, Object> params) {
        return storeAddressMapper.queryStoreAddressTotal(params);
    }
    /**
     *  @Auther:YD
     *  @parameters:必传：userLng,userLat，教练id:coachId
     *  私教课订单门店地址
     */
    @Override
    public List<Map<String, Object>> pcStoreAdInfo(Map<String, Object> params) {
        //查出教练
        List<Map<String,Object>> storeNameList = coachPlaceShipMapper.selectStoreIdByCoachId(Long.valueOf((String) params.get("coachId")));
        for (Map<String,Object> map : storeNameList) {
            BigDecimal distance = BigDecimal.ZERO;
            String storeAddrDetail = " ";
            if (map != null) {
                params.put("storeId",map.get("storeId"));
                Map<String, Object> saMap = storeAddressMapper.findAddAndDistance(params);
                if (saMap != null) {
                    if (StringUtils.isNotBlank(saMap.get("distance").toString())) {
                        BigDecimal bigDecimal = BigDecimal.valueOf((Double) saMap.get("distance")).divide(new BigDecimal(1000));
                        distance = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
                    }
                    if (StringUtils.isNotBlank((String) saMap.get("storeAddrDetail"))) {
                        storeAddrDetail = saMap.get("province") + " " + saMap.get("city") + " " + saMap.get("zone") + " " + saMap.get("storeAddrDetail");
                    }
                    map.put("distance", distance);
                    map.put("storeAddrDetail", storeAddrDetail);
                }
            }
        }
        return storeNameList;
    }

	@Override
	public List<Map<String, Object>> getMyStore(UserInfoVo userVo, Map<String, Object> params){
		List<Map<String, Object>> result = new ArrayList<>();
		if(userVo != null) {
			params.put("storeAddrId", userVo.getNowStoreId());
			result = storeAddressMapper.queryMyStoreList(params);
			
			if(result == null || result.isEmpty()) {
				params.put("userId", userVo.getUserId());
				params.remove("storeId");
				result = storeAddressMapper.queryMyStoreList(params);
			}
		}
		if(result == null || result.isEmpty()) {
			result = storeAddressMapper.findStoreListByListDistance(params);
		}
		return result == null ? new ArrayList<>() : result;
	}
}
