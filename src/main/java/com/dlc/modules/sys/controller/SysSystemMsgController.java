package com.dlc.modules.sys.controller;

import java.util.List;
import java.util.Map;
import java.util.Date;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysSystemMsgEntity;
import com.dlc.modules.sys.service.SysSystemMsgService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.R;



/**
 * 系统消息记录表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2018-12-10 17:46:22
 */
@RestController
@RequestMapping("sys/systemmsg")
public class SysSystemMsgController {
    @Autowired
    private SysSystemMsgService sysSystemMsgService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:systemmsg:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query = new Query(params);
        List<SysSystemMsgEntity> sysSystemMsgList = sysSystemMsgService.queryList(query);
        int total = sysSystemMsgService.queryTotal(query);
        PageUtils page = new PageUtils(sysSystemMsgList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{sysMsgId}")
    @RequiresPermissions("sys:systemmsg:info")
    public R info(@PathVariable("sysMsgId") Long sysMsgId){
        SysSystemMsgEntity systemMsg = sysSystemMsgService.queryObject(sysMsgId);

        return R.ok().put("systemMsg", systemMsg);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:systemmsg:save")
    public R save(@RequestBody SysSystemMsgEntity sysSystemMsg){
        sysSystemMsg.setSendTime(new Date());
        sysSystemMsgService.save(sysSystemMsg);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:systemmsg:update")
    public R update(@RequestBody SysSystemMsgEntity sysSystemMsg){
        ValidatorUtils.validateEntity(sysSystemMsg);
        sysSystemMsgService.update(sysSystemMsg);//全部更新
        
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:systemmsg:delete")
    public R delete(@RequestBody Long[] sysMsgIds){
        sysSystemMsgService.deleteBatch(sysMsgIds);

        return R.ok();
    }

}
