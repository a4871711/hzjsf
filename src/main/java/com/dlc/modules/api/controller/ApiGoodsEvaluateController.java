package com.dlc.modules.api.controller;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.GoodsEvaluate;
import com.dlc.modules.api.service.GoodsEvaluateService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author LXK
 * @date 2018/9/11 19:02
 * 商品评价
 */
@RestController
@RequestMapping("/api/goodsEvaluate")//goodsEvaluate
public class ApiGoodsEvaluateController extends BaseController{
    @Autowired
    private GoodsEvaluateService goodsEvaluateService;

    /**
     * 商品评价列表
     * */
    @RequestMapping("/evaluateList")
    public R goodsEvaluateList(@RequestParam Map<String, Object> params){
        if (org.springframework.util.StringUtils.isEmpty(params.get("page")) || org.springframework.util.StringUtils.isEmpty(params.get("limit"))) {
            return R.error("分页信息不能为空");
        }
        Query query =new Query(params);
        List<Map<String,Object>> list = goodsEvaluateService.goodsEvaluateList(query);
        //处理表情
        for(Map<String, Object> dyMap : list){
            if(dyMap.get("evContent") != null){
                dyMap.put("evContent",EmojiParser.parseToUnicode((String) dyMap.get("evContent")));
            }
            if(dyMap.get("nickname") != null){
                dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
            }
        }
        int total = goodsEvaluateService.goodsEvaluateListCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return R.reOk(pageUtil);
    }

    /**
     * 商品评价
     */
    @RequestMapping("/evaluateGoods")
    public R evaluateGoods(@RequestParam Map<String, Object> params, HttpServletRequest request){
        if (params == null ) {
            return R.reError("参数不能为空");
        }
        if(StringUtils.isEmpty((String) params.get("orderNo"))){
            return R.reError("订单编号不能为空");
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

        UserInfoVo user =  getUserVo(request);
        int i = goodsEvaluateService.save(user, params);
        if (i <= 0) {
            return R.reError("评论失败");
        }
        return R.reOk();
    }
}
