package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.UserAddressMapper;
import com.dlc.modules.api.entity.GoodsCategory;
import com.dlc.modules.api.entity.GoodsCategoryDetail;
import com.dlc.modules.api.entity.UserAddress;
import com.dlc.modules.api.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/10 14:17
 * @description: 商品
 */
@RestController
@RequestMapping("/api/goods")//goods
public class ApiGoodsController extends BaseController{
    @Autowired
    private GoodsService goodsService;
    /**
     * 最新商品
     * */
    @RequestMapping("/latestgoodsList")
    public R latestGoods(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.error("分页信息不能为空");
        }
        Query query =new Query(params);
        List<Map<String,Object>> goodsList = goodsService.latestGoodsList(query);
        int total = goodsService.latestGoodsListCount(query);
        PageUtils pageUtil = new PageUtils(goodsList,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }


    /**
     *主分类表展示
     * */
    @RequestMapping("/goodsCateList")
    public R goodsCateList(){
        List<GoodsCategory> category  = goodsService.goodsCateList();
        return R.reOk(category);
    }


    /**
     *子分类表展示
     * */
    @RequestMapping("/goodsCateDetail")
    public R goodsCateDetail(@RequestParam Map<String, Object> params){
        if (StringUtils.isEmpty(params.get("page")) || StringUtils.isEmpty(params.get("limit"))) {
            return R.error("分页信息不能为空");
        }
        Query query =new Query(params);
        List<Map<String,Object>> list = goodsService.goodsCateDetail(query);
        int total = goodsService.goodsCateDetailCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }


    /**
     * 新增子分类
     * */
    @RequestMapping("/addGoodsCateDetail")
    public R addgoodsCateDetail(GoodsCategoryDetail goodsCategoryDetail){
        if(goodsCategoryDetail.getGoodsCategoryId()==null){
            return R.reError("父分类Id为空");
        }
        int add = goodsService.addGoodsCateDetail(goodsCategoryDetail);
        if(add<=0){
            return R.reError("新增失败");
        }
        return R.reOk("新增成功");
    }

    /**
     * 删除子分类
     * */
    @RequestMapping("/delGoodsCateDetail")
    public R delGoodsCateDetail(String ids){
        if(StringUtils.isEmpty(ids)){
            return R.reError("ids,不能为空");
        }
        int del = goodsService.delGoodsCateDetail(ids);
        if(StringUtils.isEmpty(del)){
            return R.reError("删除失败");
        }
        return R.reOk("删除成功");
    }

    /**
     * 商品详情
     * */
    @RequestMapping("/goodsDetails")
    public R goodsDetails(Long goodsId,HttpServletRequest request){
        if(StringUtils.isEmpty(goodsId)){
            return R.reError("未找到商品信息");
        }
        Map<String,Object>  map = goodsService.goodsDetails(goodsId);
        if(map==null){
            map = new HashMap<>();
            //return R.reOk(map);
            return R.reError("抱歉！商品不存在或已下架");
        }
        Long userId = getUserVo(request).getUserId();
        Map<String,Object> userAddress = this.goodsService.queryDefaultAddressByUserId(userId);
        if(userAddress!=null){
            String address = userAddress.get("province").toString()+userAddress.get("city").toString()+userAddress.get("zone").toString()+userAddress.get("addr").toString();
            userAddress.put("address",address);
            map.put("userAddress",userAddress);
            return R.reOk(map);
        }
        userAddress = new HashMap<>();
        map.put("userAddress",userAddress);
        return R.reOk(map);
    }

    /**
     *二级类表展示
     * */
    @RequestMapping("/cateDetailName")
    public R queryCateDetailName(Long goodsCategoryId){
        List<Map<String,Object>> category  = goodsService.queryCateDetailName(goodsCategoryId);
        return R.reOk(category);
    }
}
