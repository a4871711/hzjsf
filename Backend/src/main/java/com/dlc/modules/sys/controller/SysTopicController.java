package com.dlc.modules.sys.controller;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dlc.common.utils.Query;
import com.dlc.modules.sys.entity.SysTopicEntity;
import com.dlc.modules.sys.service.SysTopicService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;



/**
 * 话题表
 *
 * @author daibenting
 * @email 
 * @date 2018-09-15 09:27:07
 */
@RestController
@RequestMapping("sys/topic")
public class SysTopicController {
    @Autowired
    private SysTopicService topicService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:topic:list")
    public R list(@RequestParam Map<String, Object> params){
        Query query=new Query(params);
        List<SysTopicEntity> topicList=topicService.queryList(query);
        int total=topicService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(topicList,total,query.getLimit(),query.getPage());
        return R.ok().put("page",pageUtils);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{topicId}")
    @RequiresPermissions("sys:topic:info")
    public R info(@PathVariable("topicId") Long topicId){
        SysTopicEntity topic = topicService.queryObject(topicId);
        return R.ok().put("topic", topic);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:topic:save")
    public R save(@RequestBody SysTopicEntity topic){
        topic.setCreatedDate(new Date());
        topicService.save(topic);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:topic:update")
    public R update(@RequestBody SysTopicEntity topic){
        topicService.update(topic);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:topic:delete")
    public R delete(@RequestBody Long[] topicIds){
        topicService.deleteBatch(topicIds);
        return R.ok();
    }
}
