package com.dlc.modules.api.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.service.TopicService;
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
 * 话题
 * @author lnx
 * @Date 2018-09-19
 */
@RestController
@RequestMapping("/api/topic")
public class ApiTopicController extends BaseController{

    @Autowired
    private TopicService topicService;




    /**
     * 话题详情查询
     * @param topicId
     * @param request
     * @return
     */
    @RequestMapping("/queryTopicDetail")
    public R queryTopicDetail(Long topicId, HttpServletRequest request){
        return R.reOk(topicService.queryTopicDetail(getUserId(request), topicId));
    }

    /**
     * 查询话题详情中的成员列表
     * @param topicId
     * @param request
     * @return
     */
    @RequestMapping("/queryTopicMemberList")
    public R queryTopicMemberList(Long topicId, Integer page, Integer limit, HttpServletRequest request){
        /*getUserId(request)*/
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId", getUserId(request));
        queryMap.put("topicId", topicId);
        queryMap.put("page", page);
        queryMap.put("limit", limit);
        Query query =new Query(queryMap);
        List<Map<String,Object>> queryList = topicService.queryTopicMemberList(query);
        int total = topicService.queryTopicMemberCount(query);
        PageUtils pageUtil = new PageUtils(queryList,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }

    /**
     * 话题关注
     * @param topicId
     * @param request
     * @return
     */
    @RequestMapping("/attendTopic")
    public R attendTopic(Integer type, Long topicId, HttpServletRequest request){
        return topicService.attendTopic(getUserId(request), topicId, type);
    }

    /**
     * 查询话题列表
     * @return
     */
    @RequestMapping("/queryTopicList")
    public R queryTopicList(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if(StringUtils.isEmpty(params.get("pageNum")) || StringUtils.isEmpty(params.get("pageSize"))){
            return R.reError("分页信息不能为空");
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("page", params.get("pageNum"));
        map.put("limit", params.get("pageSize"));
        map.put("userId", getUserId(request));
        PageUtils pageUtil = topicService.queryTopicList(map);
        return R.reOk(pageUtil);
    }

}
