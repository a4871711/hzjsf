package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.GoodsEvaluateMapper;
import com.dlc.modules.api.dao.GoodsOrderMapper;
import com.dlc.modules.api.entity.GoodsEvaluate;
import com.dlc.modules.api.service.GoodsEvaluateService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author LXK
 * @date 2018/9/11 19:09
 */
@Service
public class GoodsEvaluateServiceImpl implements GoodsEvaluateService {
    @Autowired
    private GoodsEvaluateMapper goodsEvaluateMapper;

    @Autowired
    private GoodsOrderMapper goodsOrderMapper;

    public List<Map<String, Object>> goodsEvaluateList(Map<String, Object> query) {
        return goodsEvaluateMapper.goodsEvaluateList(query);
    }


    public int goodsEvaluateListCount(Query query) {
        return goodsEvaluateMapper.goodsEvaluateListCount(query);
    }

    /**
     * 商品评论,支持单个商品评论和订单下多个商品评论
     *
     * @param user
     * @return
     */
    @Override
    public int save(UserInfoVo user, Map<String, Object> params) {
        int res = 0;
        try {
            String orderNo = (String) params.get("orderNo");
            Long userId = user.getUserId();
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("userId", userId);
            updateMap.put("orderNo", orderNo);
            updateMap.put("status", (byte)5);    //已评价
            //单个商品评论的情况
            if(StringUtils.isNotEmpty((String) params.get("goodsId"))){
                GoodsEvaluate goodsEvaluate = new GoodsEvaluate();
                goodsEvaluate.setOrderNo(orderNo);
                //处理表情
                goodsEvaluate.setEvContent(EmojiParser.parseToHtmlHexadecimal((String) params.get("evContent")));
                goodsEvaluate.setEvLevel(Integer.parseInt((String) params.get("evLevel")));
                goodsEvaluate.setEvaluatImgUrl((String) params.get("evaluatImgUrl"));
                goodsEvaluate.setUserId(userId);
                goodsEvaluate.setEvaluatDate(new Date());
                if(user.getNickname() != null){
                    goodsEvaluate.setNickname(EmojiParser.parseToHtmlHexadecimal(user.getNickname()));
                }
                goodsEvaluate.setHeadImgUrl(user.getHeadImgUrl());
                goodsEvaluate.setGoodsId(Long.parseLong((String) params.get("goodsId")));
                goodsEvaluateMapper.insertSelective(goodsEvaluate);
                //更新订单状态已评价
                goodsOrderMapper.updateOrderStatus(updateMap);
                return 1;
            }
            List<GoodsEvaluate> gdeList = new ArrayList<GoodsEvaluate>();
            //订单下多个商品评论
            List<Long> gdList = goodsOrderMapper.queryGidByOrderNo(orderNo);
            for(Long gId : gdList){
                GoodsEvaluate gds = new GoodsEvaluate();
                gds.setOrderNo(orderNo);
                gds.setEvContent(EmojiParser.parseToHtmlHexadecimal((String) params.get("evContent")));
                gds.setEvLevel(Integer.parseInt((String) params.get("evLevel")));
                gds.setEvaluatImgUrl((String) params.get("evaluatImgUrl"));
                gds.setUserId(userId);
                gds.setEvaluatDate(new Date());
                if(user.getNickname() != null){
                    gds.setNickname(EmojiParser.parseToHtmlHexadecimal(user.getNickname()));
                }
                gds.setHeadImgUrl(user.getHeadImgUrl());
                gds.setGoodsId(gId);
                gdeList.add(gds);
            }
            if(!CollectionUtils.isEmpty(gdeList)){
                res = goodsEvaluateMapper.insertEvelutBatch(gdeList);
                //更新订单状态已评价
                goodsOrderMapper.updateOrderStatus(updateMap);
            }

        }catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }

        return res;
    }
}
