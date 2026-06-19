package com.dlc.modules.api.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.CoachEvaluate;
import com.dlc.modules.api.entity.DynamicEvaluate;
import com.dlc.modules.api.entity.GoodsEvaluate;
import com.dlc.modules.api.service.EvaluateService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 评价
 * @author lnx
 * @Date 2018-09-21
 */
@RestController
@RequestMapping("/api/evaluate")
public class ApiEvaluateController extends BaseController{

    @Autowired
    private EvaluateService evaluateService;
    /**
     * 私教课评价（教练）
     */
    @RequestMapping("/evaluatePriClass")
    public R evaluatePc(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (params == null) {
            return R.reError("请求参数不能为空");
        }
        if (StringUtils.isEmpty((String) params.get("coachId")) || StringUtils.isEmpty((String) params.get("orderNo"))
                || StringUtils.isEmpty((String) params.get("privateClassId"))) {
            return R.reError("评论参数不能为空");
        }
        if (StringUtils.isEmpty((String) params.get("evContent")) && StringUtils.isEmpty((String) params.get("evaluatImgUrl"))
                && StringUtils.isEmpty((String) params.get("evLevel"))){
                return R.reError("请完善评论信息");
        }
        if (StringUtils.isNotEmpty((String) params.get("evContent"))){
            if(params.get("evContent").toString().length() > 255){
                return R.reError("评论字数不超过255");
            }
        }

        //UserInfoVo user = (UserInfoVo) request.getSession().getAttribute(ConfigConstant.ACCOUNT);
        UserInfoVo user = getUserVo(request);
        int i = evaluateService.saveEvaluatePc(user, params);
        if (i <= 0) {
            return R.reError("发表失败,请联系管理员");
        }
        return R.reOk();
    }

    /**
     * 场馆教练评价
     */
    @RequestMapping("/evaluateStoreCoach")
    public R evaluateStoreCoach(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (params == null) {
            return R.reError("请求参数不能为空");
        }
        if (StringUtils.isEmpty((String) params.get("coachId"))) {
            return R.reError("评论参数不能为空");
        }
        if (StringUtils.isEmpty((String) params.get("evContent")) && StringUtils.isEmpty((String) params.get("evaluatImgUrl"))
                && StringUtils.isEmpty((String) params.get("evLevel"))){
            return R.reError("请完善评论信息");
        }
        if (StringUtils.isNotEmpty((String) params.get("evContent"))){
            if(params.get("evContent").toString().length() > 255){
                return R.reError("评论字数不超过255");
            }
        }

        UserInfoVo user = getUserVo(request);
        int memFlag = evaluateService.ifHaveCard(user.getUserId());
        if(memFlag == 0){
            return R.reError("仅限已购卡会员参与评论");
        }
        int i = evaluateService.saveStoreCoachEvaluate(user, params);
        if (i <= 0) {
            return R.reError("发表失败,请联系管理员");
        }
        return R.reOk();
    }
    /**
     * 回帖（评论会员动态）
     */
    @RequestMapping("/evaluateDynamic")
    public R evaluateDynamic(Long dynamicId, String evContent, HttpServletRequest request){

        if (dynamicId == null || StringUtils.isEmpty(evContent)) {
            return R.reError("评论参数不能为空");
        }
        if (evContent.length()>255){
            return R.reError("评论字数不超过255");
        }
        //处理内容里面的表情
        evContent = EmojiParser.parseToHtmlHexadecimal(evContent);
        UserInfoVo user = getUserVo(request);
        int i = evaluateService.saveAnswerDynamic(user, dynamicId, evContent);
        if (i <= 0) {
            return R.reError("发表失败,请联系管理员");
        }
        return R.reOk();
    }
    /**
     * 点赞
     */
    @RequestMapping("/dz")
    public R dz(Long dynamicId, Integer type, HttpServletRequest request){
        if (dynamicId == null) {
            return R.reError("请求参数不能为空");
        }
        UserInfoVo user = getUserVo(request);
        if (user == null) {
            return R.reError("用户未登录");
        }
        if(type == 1){
            evaluateService.saveDz(user.getUserId(), dynamicId);
        }else if(type == 2){
            evaluateService.cancelDz(user.getUserId(), dynamicId);
        }
        return R.reOk();
    }
    /**
     * 取消点赞
     */
    @RequestMapping("/cancelDz")
    public R cancelDz(Long dynamicId, HttpServletRequest request){
        if (dynamicId == null) {
            return R.reError("请求参数不能为空");
        }
        //UserInfoVo user = (UserInfoVo) request.getSession().getAttribute(ConfigConstant.ACCOUNT);
        UserInfoVo user = getUserVo(request);
        if (user == null) {
            return R.reError("用户未登录");
        }
        return evaluateService.cancelDz(user.getUserId(), dynamicId);
    }
}
