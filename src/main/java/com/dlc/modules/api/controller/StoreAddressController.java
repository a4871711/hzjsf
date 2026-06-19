package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.OpenDoorRecordMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.entity.Store;
import com.dlc.modules.api.service.StoreAddressService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/15/015
 */
@RestController
@RequestMapping("/api/storeAddress")//storeAddress
public class StoreAddressController {

    @Autowired
    private StoreAddressService storeAddressService;
    @Autowired
    private StoreAddressMapper storeAddressMapper;
    @Autowired
    private OpenDoorRecordMapper openDoorRecordMapper;

    /**
     *  @Auther:YD
     *  @parameters: 必传：userLng ,userLat，选传：province, city, zone
     *  门店列表
     */
    @RequestMapping("/list")
    public R storeList(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        List<Map<String,Object>> list = storeAddressService.storeAddressList(query);
        if(list != null && !list.isEmpty()) {
        	for(Map<String, Object> item : list) {
        		int st = openDoorRecordMapper.getStorePeopleTotal(String.valueOf(item.get("storeId")));
        		item.put("onlineUser", st);
        	}
        }
        int total = storeAddressService.queryStoreAddressTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return R.reOk().put("page",page);
    }
    
    @RequestMapping("/info")
    public R info(@RequestParam Map<String,Object> params){
        Map<String,Object> result = storeAddressMapper.findAddAndDistance(params);
        int st = openDoorRecordMapper.getStorePeopleTotal(String.valueOf(result.get("storeId")));
        result.put("onlineUser", st);
        return R.reOk().put("info",result);
    }
    
    /**
     *  @Auther:YD
     *  @parameters: 必传：userLng ,userLat，选传：province, city, zone
     *  门店数量
     */
    @RequestMapping("/count")
    public R storeCount(@RequestParam Map<String,Object> params){
        Query query = new Query(params);
        int total = storeAddressService.queryStoreAddressTotal(query);
        return R.reOk().put("total",total);
    }
    
    /**
     *  @Auther:YD
     *  @parameters:必传：userLng ,userLat，教练id
     *  私教课订单上课场所
     */
    @RequestMapping("/pcStoreAdInfo")
    public R pcStoreAdInfo(@RequestParam Map<String,Object> params){
        if (StringUtils.isBlank((String) params.get("userLng"))){
            return R.reError("精度不能为空！");
        }
        if (StringUtils.isBlank((String) params.get("userLat"))){
            return R.reError("纬度不能为空！");
        }
        if (StringUtils.isBlank((String) params.get("coachId"))){
            return R.reError("教练id不能为空！");
        }
        return R.reOk(storeAddressService.pcStoreAdInfo(params));
    }
    
    private JSONArray getAreaZone(String province, String city, List<Map<String,Object>> list) {
    	JSONArray result = new JSONArray();
        if(list != null && !list.isEmpty()) {
        	for(Map<String, Object> item: list) {       		
        		if(!String.valueOf(item.get("province")).equals(province))continue;
        		if(!String.valueOf(item.get("city")).equals(city))continue;
        		
        		String zone = String.valueOf(item.get("zone"));
        		
        		JSONObject temp = new JSONObject();
        		temp.put("title", zone);
        		temp.put("child", new JSONArray());
        		if(result.contains(temp))continue;
        		result.add(temp);
        	}
        }
        return result;
    }
    
    private JSONArray getAreaCity(String province, List<Map<String,Object>> list) {
    	JSONArray result = new JSONArray();
        if(list != null && !list.isEmpty()) {
        	for(Map<String, Object> item: list) {       		
        		if(!String.valueOf(item.get("province")).equals(province))continue;        		
        		String city = String.valueOf(item.get("city"));        		
        		JSONObject temp = new JSONObject();
        		temp.put("title", city);
        		temp.put("child", getAreaZone(province, city, list));
        		if(result.contains(temp))continue;
        		result.add(temp);
        	}
        }
        return result;
    }
    
    /**
     *  @Auther:YD
     *  门店列表
     */
    @RequestMapping("/areaList")
    public R areaList(){
        Map<String, Object> params = new HashMap<>();
        params.put("userLng", 0);
        params.put("userLat", 0);
        List<Map<String,Object>> list = storeAddressService.storeAddressList(params);
        JSONArray result = new JSONArray();
        if(list != null && !list.isEmpty()) {
        	for(Map<String, Object> item: list) {       		
        		String province = String.valueOf(item.get("province"));
        		JSONObject temp = new JSONObject();
        		temp.put("title", province);
        		temp.put("child", getAreaCity(province, list));
        		if(result.contains(temp))continue;
        		result.add(temp);
        	}
        }
        return R.reOk().put("list",result);
    }
}
