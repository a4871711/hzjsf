package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysWalletDetailEntity;
import com.dlc.modules.sys.service.SysWalletDetailCountService;
import com.dlc.modules.sys.service.SysWalletDetailService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 钱包明细表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-11 15:33:55
 */
@RestController
@RequestMapping("sys/walletdetailcount")
public class SysWalletDetailCountController {
    @Autowired
    private SysWalletDetailCountService sysWalletDetailCountService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:walletdetail:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysWalletDetailEntity> sysWalletDetailList = sysWalletDetailCountService.queryList(query);
        int total = sysWalletDetailCountService.queryTotal(query);
        PageUtils page = new PageUtils(sysWalletDetailList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    @RequiresPermissions("sys:walletdetail:info")
    public R info(@PathVariable("id") Long id){
        SysWalletDetailEntity walletDetail = sysWalletDetailCountService.queryObject(id);

        return R.ok().put("walletDetail", walletDetail);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:walletdetail:save")
    public R save(@RequestBody SysWalletDetailEntity sysWalletDetail){
        sysWalletDetail.setCreateTime(new Date());
        sysWalletDetailCountService.save(sysWalletDetail);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:walletdetail:update")
    public R update(@RequestBody SysWalletDetailEntity sysWalletDetail){
        ValidatorUtils.validateEntity(sysWalletDetail);
        sysWalletDetailCountService.update(sysWalletDetail);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:walletdetail:delete")
    public R delete(@RequestBody Long[] ids){
        sysWalletDetailCountService.deleteBatch(ids);

        return R.ok();
    }

}
