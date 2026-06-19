package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.GoodsCategoryMapper;
import com.dlc.modules.api.dao.GoodsMapper;
import com.dlc.modules.api.dao.UserAddressMapper;
import com.dlc.modules.api.entity.Goods;
import com.dlc.modules.api.entity.GoodsCategory;
import com.dlc.modules.api.entity.GoodsCategoryDetail;
import com.dlc.modules.api.entity.UserAddress;
import com.dlc.modules.api.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/10 15:02
 */
@Service("GoodsListService")
public class GoodsServiceImpl implements GoodsService{
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsCategoryMapper goodsCategoryMapper;
    @Autowired
    private UserAddressMapper userAddressMapper;

    public List<Map<String,Object>> latestGoodsList(Map<String, Object> query) {
        List<Map<String,Object>> goodsList = goodsMapper.latestGoodsList(query);
        /*for(Map<String,Object> goods:goodsList){
          BigDecimal fTemp = new BigDecimal(goods.get("freight").toString());
          BigDecimal pTemp =  (BigDecimal) goods.get("price");
          BigDecimal price = pTemp.divide(new BigDecimal(100));
          Double freight = (fTemp.doubleValue())/100;
          goods.put("price",price);
          goods.put("freight",freight);
        }*/
        return goodsList;
    }


    public int latestGoodsListCount(Query query) {
        return goodsMapper.latestGoodsListCount(query);
    }


    public List<GoodsCategory> goodsCateList() {
        return goodsCategoryMapper.goodsCateList();
    }


    public List<Map<String, Object>> goodsCateDetail(Map<String, Object> query) {
        List<Map<String, Object>> list =  goodsCategoryMapper.goodsCateDetail(query);
        for(Map map:list){
            Long goodsId = (Long) map.get("goodsId");
            List<Map<String, Object>> colorList = goodsMapper.queryGoodsColor(goodsId);//根据商品Id查询出商品颜色
            String colors ="";
            String color = "";
            for (Map cMap :colorList){
                String colorTemp = cMap.get("color").toString();
                colors+=(colorTemp+"/");
                color =colors.substring(0,colors.length()-1);
            }
        map.put("color",color);
        }

        return list;
    }


    public int goodsCateDetailCount(Query query) {
        return goodsCategoryMapper.goodsCateDetailCount(query);
    }


    public int addGoodsCateDetail(GoodsCategoryDetail goodsCategoryDetail) {
        return goodsCategoryMapper.insertSelective(goodsCategoryDetail);
    }


    public int delGoodsCateDetail(String ids) {
        String [] idTemp = ids.split(",");
        int temp = 0;
        for (int i = 0 ;i<idTemp.length;i++){
         Long id = Long.valueOf(idTemp[i]);
         temp = goodsCategoryMapper.deleteByPrimaryKey(id);
        }
        return temp;
    }


    public Map<String,Object>  goodsDetails(Long goodsId) {
        List<Map<String,Object>>  list = goodsMapper.goodsDetails(goodsId);
        Map<String,Object> detail = null;
        List<Map<String,Object> > specification = null;
        Map<String,Object> dataInfo = new HashMap<>();
        String size = "";
        String color = "";
        String colors = "";
        if(list!=null){
            for(Map map :list){
                List<Map<String, Object>> colorList = goodsMapper.queryGoodsColor(goodsId);//根据商品Id查询出商品颜色
                specification = new ArrayList<>();
                for(Map cMap :colorList){
                    color  =  cMap.get("color").toString();
                    detail = new HashMap<>();
                    String sizeResult = "";
                    String sizeTemp = "";
                    List<Map<String,Object>> sizeList= goodsMapper.queryGoodsSize(goodsId,color);// 查询size
                    detail.put("color",color);
                    colors+=(color+"/");
                    color =colors.substring(0,colors.length()-1);
                    for(Map obj :sizeList){
                        size  = obj.get("size").toString();
                        sizeResult+=(size+",");
                        sizeTemp = sizeResult.substring(0,sizeResult.length()-1);
                        detail.put("size",sizeTemp);
                    }
                    specification.add(detail);
                }
                map.put("specification",specification);
                map.put("color",color);
                return map;

            }
        }
        return  null;
    }

    @Override
    public List<Map<String, Object>> queryCateDetailName(Long goodsCategoryId) {
        return goodsCategoryMapper.queryCateDetailName(goodsCategoryId);
    }

    @Override
    public Map<String,Object>  queryDefaultAddressByUserId(Long userId) {
        return this.userAddressMapper.queryDefaultAddressByUserId(userId);
    }
}
