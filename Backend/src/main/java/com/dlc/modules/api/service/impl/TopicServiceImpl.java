package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.DynamicTopicShipDao;
import com.dlc.modules.api.dao.TopicAttentionMapper;
import com.dlc.modules.api.dao.TopicDao;
import com.dlc.modules.api.entity.TopicAttention;
import com.dlc.modules.api.service.TopicService;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {
    private final Logger log = LoggerFactory.getLogger(TopicServiceImpl.class);

    @Autowired
    private TopicDao topicDao;

    @Autowired
    private TopicAttentionMapper topicAttentionMapper;

    @Autowired
    private DynamicTopicShipDao dynamicTopicShipDao;

    @Override
    public PageUtils queryTopicList(Map<String, Object> map) {
        Query query =new Query(map);
        List<Map<String,Object>> list = topicDao.queryTopicList(query);
        int total = topicDao.queryTopicListCount(query);
        PageUtils pageUtil = new PageUtils(list,total,query.getLimit(), query.getPage());
        return pageUtil;
    }

    @Override
    public Map<String, Object> queryTopicDetail(Long userId, Long topicId) {
        Map<String, Object> detailMap = new HashMap<String, Object>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("topicId", topicId);
        queryMap.put("userId", userId);

        try {
            Map<String, Object> tdMap = topicDao.queryTopicById(queryMap);
            if(null != tdMap){
               int dyNum = topicDao.queryTopicDyCount(topicId);
               tdMap.put("dyNum", dyNum);
            }
            //查询话题主信息
            detailMap.put("topicDetail", topicDao.queryTopicById(queryMap));
            //查询该话题下的关注会员
            List<Map<String, Object>> topicMenbers = topicAttentionMapper.queryTopicMenbers(topicId);
            if(CollectionUtils.isNotEmpty(topicMenbers)){
                for(Map<String, Object> userInfoMap : topicMenbers){
                    //处理表情
                    String nickname = (String) userInfoMap.get("nickname");
                    if(StringUtils.isNotBlank(nickname)){
                        userInfoMap.put("nickname", EmojiParser.parseToUnicode(nickname));
                    }
                }
            }
            detailMap.put("memberTotal", topicMenbers.size()); //会员数
            detailMap.put("topicMenbers", topicMenbers);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailMap;
    }

    @Override
    public List<Map<String, Object>> queryTopicMemberList(Map<String, Object> queryMap) {
        List<Map<String, Object>> topicMembers = topicAttentionMapper.queryMemberList(queryMap);
        if(CollectionUtils.isNotEmpty(topicMembers)){
            for(Map<String, Object> userInfoMap : topicMembers){
                //处理表情
                String nickname = (String) userInfoMap.get("nickname");
                if(StringUtils.isNotBlank(nickname)){
                    userInfoMap.put("nickname", EmojiParser.parseToUnicode(nickname));
                }
            }
        }
        return topicMembers;
    }

    @Override
    public int queryTopicMemberCount(Query queryMap) {
        return topicAttentionMapper.queryMemberCount(queryMap);
    }

    @Override
    public R attendTopic(Long userId, Long topicId, Integer type) {
        try {
            TopicAttention topicAttention = new TopicAttention();
            topicAttention.setObserverId(userId);
            topicAttention.setTopicId(topicId);
            topicAttention.setAttentionStatus((byte) 1);
            if(type == 1){
                topicAttentionMapper.insertSelective(topicAttention);
            }else if(type == 2){
                topicAttentionMapper.deleteAttend(topicAttention);
            }

        } catch (Exception e) {
            log.info("attendTopic() 话题关注异常"+e);
            e.printStackTrace();
        }
        return R.reOk();
    }
}
