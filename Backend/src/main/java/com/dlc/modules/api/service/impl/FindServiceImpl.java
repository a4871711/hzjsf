package com.dlc.modules.api.service.impl;

import com.baomidou.mybatisplus.toolkit.CollectionUtil;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.*;
import com.dlc.modules.api.entity.SystemMsgEntity;
import com.dlc.modules.api.service.FindService;
import com.google.common.base.Joiner;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class FindServiceImpl implements FindService {
    private static Logger log = LoggerFactory.getLogger(FindServiceImpl.class);
    @Autowired
    private TopicDao topicDao;

    @Autowired
    private DynamicMapper dynamicMapper;

    @Autowired
    private UserAttentionMapper userAttentionMapper;

    @Autowired
    private SystemMsgDao systemMsgDao;

    @Autowired
    private DataMapMapper dataMapMapper;

    @Autowired
    private DynamicTopicShipDao dynamicTopicShipDao;

    @Autowired
    private StoreGroupMapper storeGroupMapper;

    @Override
    public R queryFind(Long userId, Integer page, Integer limit) {
        Map<String, Object> findMap = new HashMap<String, Object>();
        findMap.put("hotTopic",topicDao.queryHotTopic());
        return R.reOk(findMap);
    }

    @Override
    public PageUtils queryMyAttention(Map<String, Object> params) {
        //查询我的关注，根据时间顺序排
        Query query =new Query(params);
        List<Map<String,Object>> myDyList = userAttentionMapper.queryMyAttention(query);//dynamicMapper.queryMyDylist(query);
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
            int total =  userAttentionMapper.queryMyAttentionCount(query);
            pageUtil = new PageUtils(myDyList,total,query.getLimit(), query.getPage());
        } catch (Exception e) {
            log.info("queryMyAttention 异常"+e);
            e.printStackTrace();
        }

        return pageUtil;
    }

    @Override
    public int addAttention(Long userId, Long attentedId, Integer type) {
        //添加关注
        if (attentedId == null) {
            return 0;
        }
        int res = 0;
        if(type == 1){   //添加关注
            try {
                res = userAttentionMapper.insertAttention(userId, attentedId);
            } catch (Exception e) {
                log.info("addAttention()关注失败,userId+attentedId:"+userId+"+"+attentedId);
                e.printStackTrace();
            }
        }else if(type == 2){
            res = userAttentionMapper.deleteAttention(userId, attentedId);
        }
        return res;
    }

    @Override
    public int cancelAttention(Long userId, Long attentedId) {
        //取消关注
        if (attentedId == null) {
            return 0;
        }
        return userAttentionMapper.updateAttentionStatus(userId, attentedId);
    }

    @Override
    public int report(Long userId, Long attentedId, String reportType) {
        //log.info("report 举报用户"+userId+"reportType:"+reportType);
        //举报
        if (userId == null || attentedId == null || reportType == null) {
            return 0;
        }
        int res = 0;
        try {
            //res = userAttentionMapper.updateReportType(userId, attentedId, reportType);
                String record = "";
                if(StringUtils.isNotBlank(reportType)){
                    String[] ss = reportType.split(",");
                    List<Long> li = new ArrayList<>();
                    for (String sl:ss){
                        li.add(Long.valueOf(sl.trim()));
                    }
                    List<String> labelList = dataMapMapper.queryReportNameList(li);
                    if(CollectionUtil.isNotEmpty(labelList)){
                        record = Joiner.on(",").join(labelList);
                    }

                }
                //此时保存系统消息表
                SystemMsgEntity systemMsgEntity = new SystemMsgEntity();
                systemMsgEntity.setUserId(attentedId);  //此处举报的是被关注者
                String records = "举报警示：当前账号发布的动态涉及 ("+record+")请规范个人行为！";
                systemMsgEntity.setRecord(records);
                systemMsgEntity.setMsgType(1);   //举报信息
                systemMsgEntity.setSendTime(new Date());
                res = systemMsgDao.save(systemMsgEntity);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return res;
    }

    @Override
    public PageUtils queryFindDynamic(Map<String, Object> queryMap) {
        Query query =new Query(queryMap);
        List<Map<String, Object>> dyList = dynamicMapper.queryDylist(query);
        PageUtils pageUtil = null;
        try {
            for(Map<String, Object> dyMap: dyList){
                Integer attentionStatus = (Integer) dyMap.get("attentionStatus");
                if(attentionStatus == null) {
                    dyMap.put("attentionStatus", 0);
                }
                String img = (String) dyMap.get("publishImgUrl");
                String[] ss = new String[0];       //为了不给前对返回空"",在此处声明
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
            int total = dynamicMapper.queryDylistCount(query);
            pageUtil = new PageUtils(dyList,total,query.getLimit(), query.getPage());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return pageUtil;
    }

    @Override
    public List<Map<String, Object>> queryReportType() {
        return dataMapMapper.queryReportList();
    }

    @Override
    public PageUtils queryDyList(Map<String, Object> queryMap) {

        Query query =new Query(queryMap);
        //List<Long> dynamicIdList = new ArrayList<Long>();
        PageUtils pageUtil = null;
        try {
            if(queryMap.get("storeGroupId") != null){  //查社群下方的列表
                //3.查询社群下的动态列表
                List<Map<String, Object>> dyList = storeGroupMapper.queryDynamicList(query);
                //动态下关联话题查询
                for (Map<String, Object> dyMap : dyList) {
                    Integer attentionStatus = (Integer) dyMap.get("attentionStatus");
                    if(attentionStatus == null) {
                        dyMap.put("attentionStatus", 0);
                    }
                    String img = (String) dyMap.get("publishImgUrl");
                    String[] ss = new String[0];
                    if (img != null && !"".equals(img)){
                        ss = img.split(",");
                    }
                    dyMap.put("publishImgUrl", ss);
                    dyMap.put("topicList", dynamicTopicShipDao.queryMyTopicList((Long) dyMap.get("dynamicId")));
                    //处理表情
                    if(dyMap.get("content") != null){
                        dyMap.put("content",EmojiParser.parseToUnicode((String) dyMap.get("content")));
                    }
                    if(dyMap.get("nickname") != null){
                        dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
                    }
                }
                int total = storeGroupMapper.queryDynamicListCount(query);
                pageUtil = new PageUtils(dyList,total,query.getLimit(), query.getPage());

            }else if(queryMap.get("topicId") != null){   //查话题下方动态
                //查询该话题下的动态
                List<Map<String, Object>> dyList = dynamicTopicShipDao.queryTopicDynamic(query);

                for (Map<String, Object> dyMap : dyList) {
                    Integer attentionStatus = (Integer) dyMap.get("attentionStatus");
                    if(attentionStatus == null) {
                        dyMap.put("attentionStatus", 0);
                    }
                    String img = (String) dyMap.get("publishImgUrl");
                    String[] ss = new String[0];
                    if (img != null && !"".equals(img)){
                        ss = img.split(",");

                    }
                    dyMap.put("publishImgUrl", ss);
                    dyMap.put("topicList", dynamicTopicShipDao.queryMyTopicList((Long) dyMap.get("dynamicId")));
                    //处理表情
                    if(dyMap.get("content") != null){
                        dyMap.put("content",EmojiParser.parseToUnicode((String) dyMap.get("content")));
                    }
                    if(dyMap.get("nickname") != null){
                        dyMap.put("nickname",EmojiParser.parseToUnicode((String) dyMap.get("nickname")));
                    }
                }
                int total = dynamicTopicShipDao.queryTopicDynamicCount(query);
                pageUtil = new PageUtils(dyList,total,query.getLimit(), query.getPage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pageUtil;
    }

    @Override
    public PageUtils queryFans(Map<String, Object> params) {
        Query query =new Query(params);
        List<Map<String, Object>> list = userAttentionMapper.queryFansByUId(query);
        for(Map<String, Object> maps : list){
            Integer attentionStatus = (Integer) maps.get("attentionStatus");
            if(attentionStatus == null) {
                maps.put("attentionStatus", 0);
            }
            //表情处理
            if(maps.get("nickname") != null){
                maps.put("nickname",EmojiParser.parseToUnicode((String) maps.get("nickname")));
            }
        }
        int total = userAttentionMapper.queryFansCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public PageUtils queryAttend(Map<String, Object> params) {
        Query query =new Query(params);
        List<Map<String, Object>> list = userAttentionMapper.queryAttendByUId(query);
        for(Map<String, Object> maps : list){
            Integer attentionStatus = (Integer) maps.get("attentionStatus");
            if(attentionStatus == null) {
                maps.put("attentionStatus", 0);
            }
            //表情处理
            if(maps.get("nickname") != null){
                maps.put("nickname",EmojiParser.parseToUnicode((String) maps.get("nickname")));
            }
        }
        int total = userAttentionMapper.queryAttendByCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

}
