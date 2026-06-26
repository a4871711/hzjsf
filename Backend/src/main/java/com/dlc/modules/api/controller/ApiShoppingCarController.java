package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.ShoppingCar;
import com.dlc.modules.api.service.ShoppingCarService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/14 20:52
 * 购物车
 */
@RestController
@RequestMapping("/api/shoppingCar")//shoppingCar
public class ApiShoppingCarController extends BaseController{
    @Autowired
    private ShoppingCarService shoppingCarService;


    /**
     * 新增购物车
     * */
    @RequestMapping("/addShoppingCar")
    public R addShoppingCar(ShoppingCar shoppingCar, HttpServletRequest request){
        Long userId = getUserVo(request).getUserId();
        shoppingCar.setUserId(userId);
        if(StringUtils.isEmpty(shoppingCar.getColor()) && StringUtils.isEmpty( shoppingCar.getSize())){
            return R.reError("商品尺寸和商品颜色不能为空");
        }

        this.shoppingCarService.saveShoppingCar(shoppingCar);
        return R.reOk();
    }

    /**
     * 查看购物车
     * */
    @RequestMapping("/shoppingCarList")
    public R shoppingCarList(HttpServletRequest request){
        Long userId = getUserVo(request).getUserId();
        List<Map<String,Object>> shoppingCarList = this.shoppingCarService.queryList(userId);
        return R.reOk(shoppingCarList);
    }

    /**
     * 编辑购物车
     * */
    @RequestMapping("/updateShoppingCar")
    public R updateShoppingCar(HttpServletRequest request,@RequestParam Map<String,Object> map){
        Long userId = getUserVo(request).getUserId();
        map.put("userId",userId);
        int result = this.shoppingCarService.updateShoppingCar(map);
        if(result<=0){
            return R.reError("修改失败");
        }
        return R.reOk();
    }

    /**
     * 删除购物车
     * */
    @RequestMapping("/delShoppingCar")
    public R delShoppingCar(HttpServletRequest request,String pkIds){
        Long userId = getUserVo(request).getUserId();
        int result = this.shoppingCarService.deleteShoppingCar(userId,pkIds);
        if(result<=0){
            R.reError("删除失败");
        }
        return R.reOk();
    }
}
