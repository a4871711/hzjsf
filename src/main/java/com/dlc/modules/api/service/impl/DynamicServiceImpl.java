package com.dlc.modules.api.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.Dynamic;
import com.dlc.modules.api.entity.DynamicRequest;
import com.dlc.modules.api.entity.DynamicTopicShipEntity;
import com.dlc.modules.api.service.DynamicService;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 动态
 */
@Service
@Transactional
public class DynamicServiceImpl implements DynamicService {

    private final Logger log = LoggerFactory.getLogger(DynamicServiceImpl.class);

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    private DynamicTopicShipDao dynamicTopicShipDao;

    @Autowired
    private DynamicEvaluateMapper dynamicEvaluateMapper;

    @Autowired
    private StoreGroupMapper storeGroupMapper;

    @Autowired
    private TopicDao topicDao;

    @Override
    public Map<String, Object> queryDynamicDetail(Long dynamicId, Long userId) {
        Map<String, Object> dyDetail = new HashMap<String, Object>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("userId",userId);
        queryMap.put("dynamicId",dynamicId);
        try {
            //查询动态表详情
            Map<String, Object> dyMap = dynamicMapper.queryDynamicDetail(queryMap);
            //根居动态ID查门店社群
            if(dyMap != null){
                Integer attentionStatus = (Integer) dyMap.get("attentionStatus");
                Long publishId = (Long) dyMap.get("publishId");
                if(attentionStatus == null) {
                    dyMap.put("attentionStatus", 0);
                }
                if(publishId.equals(userId)){
                    dyMap.put("attentionStatus", 2);
                }
                String img = (String) dyMap.get("publishImgUrl");
                String[] ss = new String[0];
                if (img != null && !"".equals(img)){
                    ss = img.split(",");
                }
                dyMap.put("publishImgUrl", ss);
                List<Map<String, Object>> dyTopList = dynamicTopicShipDao.queryMyTopicList(dynamicId);
                if( CollectionUtils.isEmpty(dyTopList) ){ dyTopList = new ArrayList<>(); }
                dyMap.put("topicList", dyTopList);
                //处理表情
                if(dyMap.get("content") != null){
                    dyMap.put("content",EmojiParser.parseToUnicode((String) dyMap.get("content")));
                }
                if(dyMap.get("nickname") != null){
                    dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
                }
                //点赞会员列表
                List<Map<String, Object>> dzUserList = dynamicMapper.queryDyUserlist(dynamicId);
                if( CollectionUtils.isEmpty(dzUserList) ){ dzUserList = new ArrayList<>(); }
                dyMap.put("dzUsers", dzUserList);
            }
            dyDetail.put("dynamicInfo", dyMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
         try {
            //增加阅读量+1(捕获异常不影响返回前端)
            dynamicMapper.updateReCount(dynamicId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dyDetail;
    }
    /*发布动态*/
    @Override
    public R publishDynamic(DynamicRequest dynamicRequest) {
        log.info("publishDynamic start...发布动态");
        if(dynamicRequest == null) return R.reError("没有任何发布信息");
        String content = dynamicRequest.getContent();
        String publishImg = dynamicRequest.getPublishImgUrl();
        if(StringUtils.isNotEmpty(content)){
            //处理表情
            dynamicRequest.setContent(EmojiParser.parseToHtmlHexadecimal(content));
            if(content.length() > 255){
                return R.reError("内容字数不得超过255字");
            }
        }else{
            if(StringUtils.isEmpty(publishImg)){
                return R.reError("没有任何发布");
            }
        }
        int res = 0;
        try {
            //保存发布信息
            Dynamic dynamic = new Dynamic();
            BeanUtils.copyProperties(dynamic, dynamicRequest);
            res = dynamicMapper.save(dynamic);
            Long dynamicId = dynamic.getDynamicId();
            //保存关联话题信息
            String dyshipJson = dynamicRequest.getDynamicTopic();

            if(StringUtils.isNotEmpty(dyshipJson) && dynamicId != null) {
                JSONArray jsonArray = JSONArray.parseArray(dyshipJson);
                for (Object o : jsonArray) {
                    Map<String, Object> saveMap = (Map<String, Object>)o;
                    saveMap.put("dynamicId",dynamicId);
                    saveMap.put("createdDate",new Date());
                    dynamicTopicShipDao.saveByMap(saveMap);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(res <= 0){
            return R.reError("发布失败");
        }
        return R.reOk();
    }

    @Override
    public List<Map<String, Object>> queryGroupList() {
        //查询社群列表
        return storeGroupMapper.queryStoreGroupList();
    }

    @Override
    public List<Map<String, Object>> queryTopicList() {
        //查询话题列表
        return topicDao.queryAllTopicList();
    }

    @Override
    public PageUtils queryMyDynamic(Map<String, Object> params) {
        Query query =new Query(params);
        List<Map<String,Object>> myDyList = dynamicMapper.queryMyDylist(query);
        PageUtils pageUtil = null;
        try {
            for(Map<String, Object> dyMap: myDyList){
                String img = (String) dyMap.get("publishImgUrl");
                String[] ss = new String[0];
                if (img != null && !"".equals(img)){
                    ss = img.split(",");
                }
                dyMap.put("publishImgUrl", ss);
                //处理表情
                if(dyMap.get("content") != null){
                    dyMap.put("content",EmojiParser.parseToUnicode((String) dyMap.get("content")));
                }
                if(dyMap.get("nickname") != null){
                    dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
                }
            }
            int total =  dynamicMapper.queryMyDylistCount(query);
            pageUtil = new PageUtils(myDyList,total,query.getLimit(), query.getPage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageUtil;
    }

    @Override
    public PageUtils queryDyPlList(Map<String, Object> params) {
        Query query =new Query(params);
        //根据DYNAMICID查询动态评论
        List<Map<String, Object>> dyList = dynamicEvaluateMapper.queryEvaluatList(query);
        //处理表情
        for(Map<String, Object> dyMap : dyList){
            if(dyMap.get("evContent") != null){
                dyMap.put("evContent",EmojiParser.parseToUnicode((String) dyMap.get("evContent")));
            }
            if(dyMap.get("nickname") != null){
                dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
            }

        }
        int total = dynamicEvaluateMapper.queryEvaluatListCount(query);
        PageUtils pageUtil = new PageUtils(dyList,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public int deleteDynamic(Long dynamicId, Long userId) {
        Map<String,Object> deleteMap = new HashMap<String,Object>();
        deleteMap.put("dynamicId", dynamicId);
        deleteMap.put("publishId", userId);
        //先删除动态表，再删除话题动态关系表（防止产生垃圾数据）
        int res = 0;
        try {
            res = dynamicMapper.deleteByPubIdDyId(deleteMap);
            if(res > 0){
                //删除话/动关系表
                dynamicTopicShipDao.deleteByDynamicId(dynamicId);
            }
        } catch (Exception e) {
            log.info("删除动态异常deleteDynamic（）"+e);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public List<Map<String, Object>> queryDzMembersList(Query query) {
        List<Map<String, Object>> dzMembers = dynamicMapper.queryDzMembersList(query);
        if(CollectionUtils.isNotEmpty(dzMembers)){
            for (Map<String, Object> userInfo : dzMembers){
                if(userInfo.get("nickname") != null){
                    userInfo.put("nickname",EmojiParser.parseToUnicode((String) userInfo.get("nickname")));
                }
            }
        }

        return dzMembers;
    }

    @Override
    public int queryDzMembersTotal(Query query) {
        return dynamicMapper.queryDzMembersTotal(query);
    }

}
