package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.DynamicRequest;
import com.dlc.modules.api.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 动态
 * @author lnx
 * @Date 2018-09-19
 */
@RestController
@RequestMapping("/api/dynamic")
public class ApiDynamicController extends BaseController {

    @Autowired
    private DynamicService dynamicService;

    /**
     * 查询动态详情
     * @param dynamicId
     * @param request
     * @return
     */
    @RequestMapping("/queryDyDetail")
    public R queryDyDetail(Long dynamicId, HttpServletRequest request){

        return R.reOk(dynamicService.queryDynamicDetail(dynamicId, getUserId(request)));
    }

    @RequestMapping("/deleteDynamic")
    public R deleteDynamic(Long dynamicId, HttpServletRequest request){
        if(null == dynamicId){
            R.reError("操作参数缺失");
        }
        return R.reOk().put("status", dynamicService.deleteDynamic(dynamicId, getUserId(request)));
    }

    /**
     * 分页查询评论列表
     * @param request
     * @return
     */
    @RequestMapping("/queryDyPlList")
    public R queryDyPlList(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (org.springframework.util.StringUtils.isEmpty(params.get("page")) || org.springframework.util.StringUtils.isEmpty(params.get("pagesize"))) {
            return R.error("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        PageUtils pageUtil = dynamicService.queryDyPlList(params);
        return R.reOk(pageUtil);
    }

    /**
     * 发布动态
     * @param DynamicRequest
     * @param request
     * @return
     */
    @RequestMapping("/publishDynamic")
    public R publishDynamic(DynamicRequest DynamicRequest, HttpServletRequest request){
        return dynamicService.publishDynamic(DynamicRequest);
    }

    /**
     * 查询社群列表
     * @param request
     * @return
     */
    @RequestMapping("/getGroupList")
    public R getGroupList(HttpServletRequest request){
        return R.reOk(dynamicService.queryGroupList());
    }

    /**
     * 查询话题列表
     * @param request
     * @return
     */
    @RequestMapping("/getTopicList")
    public R getTopicList(HttpServletRequest request){
        return R.reOk(dynamicService.queryTopicList());
    }

    /**
     * 查询我的动态列表
     * @return
     */
    @RequestMapping("/queryMyDynamic")
    public R queryMyDynamic(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (org.springframework.util.StringUtils.isEmpty(params.get("page")) || org.springframework.util.StringUtils.isEmpty(params.get("pagesize"))) {
            return R.error("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("myId", getUserId(request));
        PageUtils pageUtil = dynamicService.queryMyDynamic(params);
        return R.reOk(pageUtil);
    }

    /**
     * 查询动态点赞成员列表
     * @param dynamicId
     * @param request
     * @return
     */
    @RequestMapping("/queryDzMemberList")
    public R queryDzMemberList(Long dynamicId, Integer page, Integer limit, HttpServletRequest request){
        /*getUserId(request)*/
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", getUserId(request));
        queryMap.put("dynamicId", dynamicId);
        queryMap.put("page", page);
        queryMap.put("limit", limit);
        Query query =new Query(queryMap);
        List<Map<String,Object>> queryList = dynamicService.queryDzMembersList(query);
        int total = dynamicService.queryDzMembersTotal(query);
        PageUtils pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }
}
