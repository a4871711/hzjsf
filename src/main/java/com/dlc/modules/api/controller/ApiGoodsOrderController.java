package com.dlc.modules.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.GoodsOrder;
import com.dlc.modules.api.entity.OrderDetail;
import com.dlc.modules.api.service.GoodsOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/14 8:57
 * 订单
 */
@RestController
@RequestMapping("/api/goodsOrder")//goodsOrder
public class ApiGoodsOrderController extends BaseController{
    @Autowired
    private GoodsOrderService goodsOrderService;

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 商城订单
     * */
    @RequestMapping("/addGoodsOrder")
    public R addGoodsOrder(String jsonArray,String pkIds, HttpServletRequest request){
        JSONObject obj = com.alibaba.fastjson.JSONObject.parseObject(jsonArray);//alibaba

        GoodsOrder goodsOrder = JSONObject.toJavaObject(obj,GoodsOrder.class);
        if(goodsOrder.getRealPayment().compareTo(BigDecimal.ZERO) == -1){
            return  R.error("金额不能为负数！该交易存在恶意攻击风险, 已被标记");
        }
        Long userId = getUserVo(request).getUserId();
        goodsOrder.setUserId(userId);
        //查询商品是否已下架,或已删除如果是不能下单
        List<OrderDetail> orderDetailL = goodsOrder.getOrderDetailList();
        for(OrderDetail gl :orderDetailL){
            int flag = goodsOrderService.ifExistGoods(gl.getGoodsId());
            if(flag == 0){ //商品不存在或已下架
                return R.reError(gl.getGoodsName()+"商品已下架");
            }
        }

        Map<String,Object> order = goodsOrderService.addGoodsOrder(goodsOrder);

        log.info("addGoodsOrder******************************"+jsonArray+"++++"+pkIds+"order=="+order);

        if(!order.equals("") && pkIds!=null &&  !pkIds.equals(0)){
            goodsOrderService.delShoppingCar(pkIds);
        }

        if(order==null){
            return R.reError("添加订单失败");
        }

        return R.reOk(order);
    }

}
