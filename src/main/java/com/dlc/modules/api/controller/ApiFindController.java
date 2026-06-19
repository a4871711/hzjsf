package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.DynamicShield;
import com.dlc.modules.api.service.DynamicShieldService;
import com.dlc.modules.api.service.FindService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 发现
 * @author lnx
 * @Date 2018-09-18
 */
@RestController
@RequestMapping("/api/find")
public class ApiFindController extends BaseController {

    @Autowired
    private FindService findService;
    @Autowired
    private DynamicShieldService dynamicShieldService;
    /**
     * 查询“精选”热门
     * @param request
     * @return
     */
    @RequestMapping("/queryFind")
    public R queryFind(Integer page, Integer limit, HttpServletRequest request){
        /*getUserId(request)*/
        return findService.queryFind(getUserId(request), page, limit);
    }
    /**
     * 查询“精选”会员动态
     * @param request
     * @return
     */
    @RequestMapping("/queryFindDynamic")
    public R queryFindDynamic(Integer page, Integer limit, HttpServletRequest request){
        if(page == null || limit == null){
            return R.reError("分页参数为空");
        }
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", getUserId(request));
        queryMap.put("page", page);
        queryMap.put("limit", limit);
        PageUtils pageUtil = findService.queryFindDynamic(queryMap);
        return R.reOk(pageUtil);
    }

    /**
     * 动态屏蔽
     * @param request
     * @return
     */
    @RequestMapping("/shieldDynamic")
    public R shieldDynamic(Long dynamicId, HttpServletRequest request){
        if(dynamicId == null){
            return R.reError("app参数为空");
        }
        DynamicShield dynamicShield = new DynamicShield();
        dynamicShield.setDynamicId(dynamicId);
        dynamicShield.setUserId(getUserId(request));
        dynamicShieldService.saveShieldDynamic(dynamicShield);
        return R.reOk();
    }

    /**
     * 查询话题详情或社群详情下方的动态列表
     * @param request
     * @return
     */
    @RequestMapping("/queryDyList")
    public R queryDyList(Long storeGroupId, Long topicId, Integer page, Integer pagesize, HttpServletRequest request){
        if(page == null || pagesize == null){
            return R.reError("分页参数为空");
        }
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", getUserId(request));
        queryMap.put("page", page);
        queryMap.put("limit", pagesize);
        queryMap.put("storeGroupId", storeGroupId);
        queryMap.put("topicId", topicId);
        PageUtils pageUtil = findService.queryDyList(queryMap);
        return R.reOk(pageUtil);
    }
    /**
     * 查询我的关注
     * @param request
     * @return
     */
    @RequestMapping("/queryMyAttention")
    public R queryMyAttention(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (org.springframework.util.StringUtils.isEmpty(params.get("page")) || org.springframework.util.StringUtils.isEmpty(params.get("limit"))) {
            return R.error("分页信息不能为空");
        }
        params.put("userId", getUserId(request));
        PageUtils pageUtil = findService.queryMyAttention(params);
        return R.reOk(pageUtil);
    }

    /**
     * 添加关注
     */
    @RequestMapping("/addAttention")
    public R addAttention(Long attentedId, Integer type, HttpServletRequest request){
        /*attentedId, getUserId(request)*/
        return R.reOk().put("status", findService.addAttention(getUserId(request), attentedId, type));
    }

    /**
     * 取消关注
     */
    @RequestMapping("/cancelAttention")
    public R cancelAttention(Long attentedId, HttpServletRequest request){
        /*attentedId, getUserId(request)*/
        return R.reOk().put("status", findService.cancelAttention(getUserId(request), attentedId));
    }

    /**
     * 举报
     * @param attentedId
     * @param request
     * @return
     */
    @RequestMapping("/report")
    public R report(Long attentedId, String reportType, HttpServletRequest request){
        /*attentedId, getUserId(request)*/
        return R.reOk().put("status", findService.report(getUserId(request), attentedId, reportType));
    }
    /**
     * 举报类型查询
     * @param request
     * @return
     */
    @RequestMapping("/queryReportType")
    public R queryReportType(HttpServletRequest request){
        /*attentedId, getUserId(request)*/
        return R.reOk(findService.queryReportType());
    }

    /**
     * 粉丝/关注成员查询 1粉丝 2关注成员
     * @param request
     * @return
     */
    @RequestMapping("/queryFansAttend")
    public R queryFansAttend(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (StringUtils.isEmpty((String) params.get("page")) || StringUtils.isEmpty((String) params.get("pagesize"))) {
            return R.error("分页信息不能为空");
        }
        if(StringUtils.isEmpty((String) params.get("type"))){
            return R.error("查询类型不能为空");
        }
        Integer type = Integer.parseInt((String) params.get("type"));
        params.put("limit", params.get("pagesize"));
        params.put("myId", getUserId(request));
        PageUtils pageUtil = null;
        //1查粉丝列表
        if(type == 1){
            pageUtil = findService.queryFans(params);
        }else if(type == 2){
            //2查关注成员列表
            pageUtil = findService.queryAttend(params);
        }else{
            return R.error("查询类型不存在");
        }

        return R.reOk(pageUtil);
    }

}
