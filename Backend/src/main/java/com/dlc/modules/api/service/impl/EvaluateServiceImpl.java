package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.CoachEvaluate;
import com.dlc.modules.api.entity.DynamicEvaluate;
import com.dlc.modules.api.entity.DzRecordEntity;
import com.dlc.modules.api.entity.StoreCoachEvaluate;
import com.dlc.modules.api.service.EvaluateService;
import com.dlc.modules.api.vo.UserInfoVo;
import com.vdurmont.emoji.EmojiParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class EvaluateServiceImpl implements EvaluateService {
    private final Logger log = LoggerFactory.getLogger(EvaluateServiceImpl.class);
    @Autowired
    private CoachEvaluateMapper coachEvaluateMapper;

    @Autowired
    private DynamicEvaluateMapper dynamicEvaluateMapper;

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    private DzRecordDao dzRecordDao;

    @Autowired
    private PrivateClassOrderMapper privateClassOrderMapper;

    @Autowired
    private StoreCoachEvaluateMapper storeCoachEvaluateMapper;
    @Autowired
    private DeviceMapper deviceMapper;

    @Override
    public int saveEvaluatePc(UserInfoVo user, Map<String, Object> params) {
        int res = 0;
        try {
            String orderNo = (String) params.get("orderNo");
            Long userId = user.getUserId();
            CoachEvaluate coachEvaluate = new CoachEvaluate();
            coachEvaluate.setCoachId(Long.parseLong((String) params.get("coachId")));
            coachEvaluate.setOrderNo(orderNo);
            coachEvaluate.setPrivateClassId(Long.parseLong((String) params.get("privateClassId")));
            //处理表情
            coachEvaluate.setEvContent(EmojiParser.parseToHtmlHexadecimal((String) params.get("evContent")));
            coachEvaluate.setEvLevel(Integer.parseInt((String) params.get("evLevel")));
            coachEvaluate.setClassName((String) params.get("className"));
            coachEvaluate.setEvaluatImgUrl((String) params.get("evaluatImgUrl"));
            //教练私教课评论
            coachEvaluate.setUserId(userId);
            if(user.getNickname() != null){
                coachEvaluate.setNickname(EmojiParser.parseToHtmlHexadecimal(user.getNickname()));
            }

            coachEvaluate.setHeadImgUrl(user.getHeadImgUrl());
            coachEvaluate.setEvaluatDate(new Date());
            res = coachEvaluateMapper.insertSelective(coachEvaluate);
            //更新订单为已评价
            Map<String, Object> updateMap = new HashMap<String, Object>();
            updateMap.put("userId", userId);
            updateMap.put("orderNo", orderNo);
            updateMap.put("status", (byte)3);  //3 已评价
            privateClassOrderMapper.updatePrivateOrderStatus(updateMap);
        } catch (NumberFormatException e) {
            log.info("saveEvaluatePc 评论异常{}"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return res;
    }

    @Override
    public int saveAnswerDynamic(UserInfoVo user, Long dynamicId, String evContent) {
        int res = 0;
        try {
            //会员动态评论
            DynamicEvaluate dynamicEvaluate = new DynamicEvaluate();
            dynamicEvaluate.setReviewerId(user.getUserId());
            if(user.getNickname() != null){
                dynamicEvaluate.setNickname(EmojiParser.parseToHtmlHexadecimal(user.getNickname()));
            }
            dynamicEvaluate.setHeadImgUrl(user.getHeadImgUrl());
            dynamicEvaluate.setDynamicId(dynamicId);
            dynamicEvaluate.setEvContent(evContent);
            dynamicEvaluate.setEvaluatDate(new Date());
            res = dynamicEvaluateMapper.insertSelective(dynamicEvaluate);
            //根据动态id将该动态的评论数+1
            if(res > 0){
                dynamicMapper.updatePlCount(dynamicEvaluate.getDynamicId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }

        return res;
    }

    @Override
    public int saveStoreCoachEvaluate(UserInfoVo user, Map<String, Object> params) {
        int res = 0;
        try {
            Long userId = user.getUserId();
            StoreCoachEvaluate coachEvaluate = new StoreCoachEvaluate();
            coachEvaluate.setScId(Long.parseLong((String) params.get("coachId")));
            //处理表情
            coachEvaluate.setEvContent(EmojiParser.parseToHtmlHexadecimal((String) params.get("evContent")));
            coachEvaluate.setEvLevel(Integer.parseInt((String) params.get("evLevel")));
            coachEvaluate.setEvaluatImgUrl((String) params.get("evaluatImgUrl"));
            //教练私教课评论
            coachEvaluate.setUserId(userId);
            if(user.getNickname() != null){
                coachEvaluate.setNickname(EmojiParser.parseToHtmlHexadecimal(user.getNickname()));
            }
            coachEvaluate.setHeadImgUrl(user.getHeadImgUrl());
            coachEvaluate.setEvaluatDate(new Date());
            res = storeCoachEvaluateMapper.insertSelective(coachEvaluate);
        } catch (Exception e) {
            log.info("saveStoreCoachEvaluate 评论异常{}"+e);
            throw new RRException("请求失败",e);
        }
        return res;
    }

    @Override
    public int ifHaveCard(Long userId) {
        return deviceMapper.queryIsCardUserById(userId);
    }

    @Override
    public R saveDz(Long userId, Long dynamicId) {
        Map<String, Object> dzMap = new HashMap<String, Object>();
        dzMap.put("dynamicId", dynamicId);
        //dzMap.put("dzUserId", userId);
        //int flagNum = dzRecordDao.queryDzByUIdAndDyId(queryMap);
        int flag = 0;
        try {
            DzRecordEntity dzRecordEntity = new DzRecordEntity();
            dzRecordEntity.setDynamicId(dynamicId);
            dzRecordEntity.setDzUserId(userId);
            //保存点赞记录关系表
            flag = dzRecordDao.save(dzRecordEntity);
            //动态中点赞数+1
            if(flag > 0){
              dzMap.put("dzCount", 1);   //点赞数+1
              dynamicMapper.updateDzCount(dzMap);
            }
        } catch (Exception e) {
            log.info("点赞saveDz（）异常"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return R.reOk();
    }

    @Override
    public R cancelDz(Long userId, Long dynamicId) {
        //取消点赞
        Map<String, Object> dzMap = new HashMap<String, Object>();
        dzMap.put("dynamicId", dynamicId);
        dzMap.put("dzUserId", userId);
        int flag = 0;
        try {
            //保存点赞记录关系表
            flag = dzRecordDao.deleteDz(dzMap);
            //动态中点赞数-1
            if(flag > 0){
              dzMap.put("dzCount", -1);   //点赞数-1
              dynamicMapper.updateDzCount(dzMap);
            }
        } catch (Exception e) {
            log.info("取消点赞cancelDz（）异常"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return R.reOk();
    }
}
