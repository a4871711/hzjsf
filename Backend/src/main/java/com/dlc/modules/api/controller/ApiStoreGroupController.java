package com.dlc.modules.api.controller;

import com.alibaba.druid.util.StringUtils;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.StoreGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 门店社群
 * @author lnx
 * @Date 2018-09-19
 */
@RestController
@RequestMapping("/api/storeGroup")
public class ApiStoreGroupController extends BaseController{

    @Autowired
    private StoreGroupService storeGroupService;

    /**
     * 查同城门店社群列表   0同城 1热门 2我的
     * @param request (必填：userLng ,userLat, city   pageNum, pageSize)
     * @return
     */
    @RequestMapping("/queryGroup")
    public R querySameCityGroup(@RequestParam Map<String,Object> params, HttpServletRequest request){
        if (org.springframework.util.StringUtils.isEmpty(params.get("page")) || org.springframework.util.StringUtils.isEmpty(params.get("pagesize"))) {
            return R.error("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("userId", getUserId(request));
        int type = Integer.parseInt((String) params.get("type"));
        PageUtils pageUtil = null;
        if(type == 0){
            pageUtil = storeGroupService.querySameCityGroup(params);
        }else if(type == 1){
            pageUtil = storeGroupService.queryHotGroup(params);
        }else if(type == 2){
            pageUtil = storeGroupService.queryMyGroup(params);
        }
        return R.reOk(pageUtil);
    }

    /**
     * 查询热门社群列表
     * @param request   必须参数 pageNum, pageSize
     * @return
     */
    @RequestMapping("/queryHotGroup")
    public R queryHotGroup(@RequestParam Map<String,Object> params, HttpServletRequest request){
        return R.reOk(storeGroupService.queryHotGroup(params));
    }

    /**
     * 查询我的社群列表
     * @param request   必须参数 pageNum, pageSize
     * @return
     */
    @RequestMapping("/queryMyGroup")
    public R queryMyGroup(@RequestParam Map<String,Object> params, HttpServletRequest request){
        params.put("userId", getUserId(request));
        return R.reOk(storeGroupService.queryMyGroup(params));
    }

    /**查询社群详情*/
    @RequestMapping("/queryGroupDetail")
    public R queryGroupDetail(@RequestParam Long storeGroupId, HttpServletRequest request){
        return R.reOk(storeGroupService.queryGroupDetail(storeGroupId, getUserId(request)));
    }

    /**
     * 查询门店社群详情中的成员列表
     * @param request
     * @return
     */
    @RequestMapping("/queryGroupMemberList")
    public R queryGroupMemberList(Long storeGroupId, Integer page, Integer limit, HttpServletRequest request){
        /*getUserId(request)*/
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", getUserId(request));
        queryMap.put("storeGroupId", storeGroupId);
        queryMap.put("page", page);
        queryMap.put("limit", limit);
        Query query =new Query(queryMap);
        List<Map<String,Object>> queryList = storeGroupService.queryGroupMemberList(query);
        int total = storeGroupService.queryGroupMemberCount(query);
        PageUtils pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }
    @RequestMapping("/attendGroup")
    public R attendGroup(Integer type, Long storeGroupId, HttpServletRequest request){
        return storeGroupService.attendGroup(getUserId(request), storeGroupId, type);
    }

    /**
     * 个人中心-社群列表查询
     * @param request
     * @return
     */
    @RequestMapping("/queryGroupByUid")
    public R queryGroupByUid(@RequestParam Map<String,Object> params, HttpServletRequest request){
        if (StringUtils.isEmpty((String)params.get("page")) || StringUtils.isEmpty((String)params.get("pagesize"))) {
            return R.error("分页信息不能为空");
        }
        params.put("limit", params.get("pagesize"));
        params.put("myId", getUserId(request));
        PageUtils pageUtil = storeGroupService.queryGroupByUid(params);
        return R.reOk(pageUtil);
    }

}
