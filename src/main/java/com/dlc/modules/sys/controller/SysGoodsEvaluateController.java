package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysGoodsEvaluateEntity;
import com.dlc.modules.sys.service.SysGoodsEvaluateService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 商品评价表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-30 13:13:07
 */
@RestController
@RequestMapping("sys/goodsevaluate")
public class SysGoodsEvaluateController {
    @Autowired
    private SysGoodsEvaluateService sysGoodsEvaluateService;

    /**
     * 列表
     */
    @RequestMapping("/list")
  @RequiresPermissions("sys:goodsevaluate:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysGoodsEvaluateEntity> sysGoodsEvaluateList = sysGoodsEvaluateService.queryList(query);
        int total = sysGoodsEvaluateService.queryTotal(query);
        PageUtils page = new PageUtils(sysGoodsEvaluateList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{goodsEvaluatId}")
    @RequiresPermissions("sys:goodsevaluate:info")
    public R info(@PathVariable("goodsEvaluatId") Long goodsEvaluatId){
        SysGoodsEvaluateEntity goodsEvaluate = sysGoodsEvaluateService.queryObject(goodsEvaluatId);

        return R.ok().put("goodsEvaluate", goodsEvaluate);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:goodsevaluate:save")
    public R save(@RequestBody SysGoodsEvaluateEntity sysGoodsEvaluate){

        sysGoodsEvaluateService.save(sysGoodsEvaluate);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:goodsevaluate:update")
    public R update(@RequestBody SysGoodsEvaluateEntity sysGoodsEvaluate){
        ValidatorUtils.validateEntity(sysGoodsEvaluate);
        sysGoodsEvaluateService.update(sysGoodsEvaluate);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:goodsevaluate:delete")
    public R delete(@RequestBody Long[] goodsEvaluatIds){
        sysGoodsEvaluateService.deleteBatch(goodsEvaluatIds);

        return R.ok();
    }

}
