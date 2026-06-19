package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysCoachTradeDetailEntity;
import com.dlc.modules.sys.service.SysCoachTradeDetailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 教练收支明细
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-28 10:37:48
 */
@RestController
@RequestMapping("sys/coachtradedetail")
public class SysCoachTradeDetailController {
    @Autowired
    private SysCoachTradeDetailService sysCoachTradeDetailService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:coachtradedetail:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysCoachTradeDetailEntity> sysCoachTradeDetailList = sysCoachTradeDetailService.queryList(query);
        int total = sysCoachTradeDetailService.queryTotal(query);
        PageUtils page = new PageUtils(sysCoachTradeDetailList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:coachtradedetail:info")
    public R info(@PathVariable("id") Long id){
        SysCoachTradeDetailEntity coachTradeDetail = sysCoachTradeDetailService.queryObject(id);

        return R.ok().put("coachTradeDetail", coachTradeDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:coachtradedetail:save")
    public R save(@RequestBody SysCoachTradeDetailEntity sysCoachTradeDetail){
        sysCoachTradeDetail.setCreateTime(new Date());
        sysCoachTradeDetailService.save(sysCoachTradeDetail);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:coachtradedetail:update")
    public R update(@RequestBody SysCoachTradeDetailEntity sysCoachTradeDetail){
        ValidatorUtils.validateEntity(sysCoachTradeDetail);
        sysCoachTradeDetailService.update(sysCoachTradeDetail);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:coachtradedetail:delete")
    public R delete(@RequestBody Long[] ids){
        sysCoachTradeDetailService.deleteBatch(ids);

        return R.ok();
    }

}
