package com.dlc.modules.api.service.impl;

import com.dlc.modules.api.dao.ShoppingCarMapper;
import com.dlc.modules.api.entity.Goods;
import com.dlc.modules.api.entity.ShoppingCar;
import com.dlc.modules.api.service.ShoppingCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/14 20:55
 */
@Service
public class ShoppingCarServiceImpl implements ShoppingCarService {
    @Autowired
    private ShoppingCarMapper shoppingCarMapper;

    public void saveShoppingCar(ShoppingCar shoppingCar) {

        //判断是否已经存在
        String color = shoppingCar.getColor();
        String size = shoppingCar.getSize();
        Long goodsId = shoppingCar.getGoodsId();
        Long uId = shoppingCar.getUserId();
        List<Map<String,Object>> list = this.shoppingCarMapper.queryShCarByGoodsId(goodsId,uId);
        if(list.size()!=0){
            for(Map map :list){
                String colorM = map.get("color").toString();
                String sizeM = map.get("size").toString();
                Long userId = (Long) map.get("userId");
                if(colorM.equals(color) && size.equals(sizeM) && userId==userId){
                    //同一件商品(颜色尺寸都相同)增加数据
                    Integer goodsNum = shoppingCar.getGoodsNum();
                    Long pkId = (Long) map.get("pkId");
                    this.shoppingCarMapper.addGoodsNum(pkId,goodsNum);
                    return;
                }
            }
        }
        //查询商品信息
        Map<String, Object> gm = shoppingCarMapper.selectGoodsInfo(goodsId);
        shoppingCar.setCategoryId((Integer) gm.get("categoryId"));
        shoppingCar.setFreight((BigDecimal) gm.get("freight"));
        this.shoppingCarMapper.insertSelective(shoppingCar);

    }


    public List<Map<String,Object>> queryList(Long userId) {
        List<Map<String,Object>> list = shoppingCarMapper.selectByUserId(userId);
        return list;
    }


    public int updateShoppingCar(Map<String,Object> map) {
        int result = shoppingCarMapper.updateShoppingCar(map);
        return result;
    }


    public int deleteShoppingCar(Long userId, String pkIds) {
        String[] arr = pkIds.split(",");
        int reult = 0;
        for(String temp:arr){
            reult = this.shoppingCarMapper.deleteByPrimaryKey(Long.valueOf(temp));
        }
        return reult;
    }


}
