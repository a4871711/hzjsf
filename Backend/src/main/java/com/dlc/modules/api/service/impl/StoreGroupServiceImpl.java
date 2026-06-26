package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.StoreAttentionMapper;
import com.dlc.modules.api.dao.StoreGroupMapper;
import com.dlc.modules.api.entity.StoreAttention;
import com.dlc.modules.api.service.StoreGroupService;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StoreGroupServiceImpl implements StoreGroupService {
    private final Logger log = LoggerFactory.getLogger(StoreGroupServiceImpl.class);

    @Autowired
    private StoreGroupMapper storeGroupMapper;

    @Autowired
    private StoreAttentionMapper storeAttentionMapper;

    @Override
    public PageUtils querySameCityGroup(Map<String, Object> params) {
        log.info("querySameCityGroup start...");
        //查询同城
        Query query = new Query(params);
        List<Map<String,Object>> list = storeGroupMapper.querySameCityGroupList(query);
        int i = 0;
        for (Map<String,Object> map : list) {
            if (map.get("distance") == null) {
                map.put("distance", "未知");
            } else {
                BigDecimal bigDecimal = BigDecimal.valueOf((Double) map.get("distance")).divide(new BigDecimal(1000));
                double distance = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                map.put("distance", distance);
            }
            //处理前三排名
            Long menberNum = (Long) map.get("menberNum");
            String city = (String) map.get("city");
            String rank = null;
            i++;
            if(menberNum > 0 && i<= 3){
                rank = city + "人气 NO." + i;
            }
            map.put("rank", rank);
        }
        int total = storeGroupMapper.querySameCityGroupTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return page;
    }

    @Override
    public PageUtils queryHotGroup(Map<String, Object> params) {
        log.info("queryHotGroup start...");
        //查询热门社群
        Query query = new Query(params);
        List<Map<String,Object>> list = storeGroupMapper.queryHotGroup(query);
        //if(CollectionUtil.isEmpty(list)) return null;
        int total = storeGroupMapper.queryHotGroupTotal();
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return page;
    }

    @Override
    public PageUtils queryMyGroup(Map<String, Object> params) {
        //查询我的社群列表
        Query query = new Query(params);
        List<Map<String,Object>> list = storeGroupMapper.queryMyGroup(query);
        //if(CollectionUtil.isEmpty(list)) return null;
        int total = storeGroupMapper.queryMyGroupTotal(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return page;
    }

    @Override
    public Map<String, Object> queryGroupDetail(Long storeGroupId, Long userId) {
        //log.info("queryGroupDetail start...查询社群下的动态详情");
        //查询社群详情
        Map<String, Object> detailMap = new HashMap<String, Object>();
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("storeGroupId", storeGroupId);
        queryMap.put("observerId", userId);
        Map<String, Object> getDetailMap = storeGroupMapper.queryGroupDetail(queryMap);
        getDetailMap.put("dyNum", storeGroupMapper.queryDynamicListCount(queryMap));
        Integer attentionStatus = (Integer) getDetailMap.get("attentionStatus");
        if(attentionStatus == null) {
            getDetailMap.put("attentionStatus", 0);
        }
        try {
            //1.查社群
            detailMap.put("groupDetail", getDetailMap);
            //2.查社群下的会员
            List<Map<String, Object>> groupMembers = storeGroupMapper.queryGroupMembers(storeGroupId);
            if(CollectionUtils.isNotEmpty(groupMembers)){
                for(Map<String, Object> userInfoMap : groupMembers){
                    //处理表情
                    String nickname = (String) userInfoMap.get("nickname");
                    if(StringUtils.isNotBlank(nickname)){
                        userInfoMap.put("nickname", EmojiParser.parseToUnicode(nickname));
                    }
                }
            }
            detailMap.put("memberTotal", groupMembers.size());
            detailMap.put("groupMembers", groupMembers);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return detailMap;
    }

    @Override
    public List<Map<String, Object>> queryGroupMemberList(Map<String, Object> queryMap) {
        List<Map<String, Object>> groupMembers = storeAttentionMapper.queryMemberList(queryMap);
        if(CollectionUtils.isNotEmpty(groupMembers)){
            for(Map<String, Object> userInfoMap : groupMembers){
                //处理表情
                String nickname = (String) userInfoMap.get("nickname");
                if(StringUtils.isNotBlank(nickname)){
                    userInfoMap.put("nickname", EmojiParser.parseToUnicode(nickname));
                }
            }
        }
        return groupMembers;
    }

    @Override
    public int queryGroupMemberCount(Query query) {
        return storeAttentionMapper.queryMemberCount(query);
    }

    @Override
    public R attendGroup(Long userId, Long storeGroupId, Integer type) {
        try {
            StoreAttention storeAttention = new StoreAttention();
            storeAttention.setObserverId(userId);
            storeAttention.setStoreGroupId(storeGroupId);
            storeAttention.setAttentionStatus((byte) 1);
            if(type == 1){
                storeAttentionMapper.insertSelective(storeAttention);
            }else if(type == 2){
                storeAttentionMapper.deleteAttends(storeAttention);
            }
        } catch (Exception e) {
            log.info("attendGroup() 异常"+e);
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return R.reOk();
    }

    @Override
    public PageUtils queryGroupByUid(Map<String, Object> params) {
        //log.info("个人中心社群列表查询");
        Query query = new Query(params);
        List<Map<String,Object>> list = storeGroupMapper.queryGroupByUid(query);
        for(Map<String,Object> mmp : list){
            Integer attentionStatus = (Integer) mmp.get("attentionStatus");
            if(attentionStatus == null) {
                mmp.put("attentionStatus", 0);
            }
        }
        int total = storeGroupMapper.queryGroupByUidCount(query);
        PageUtils page = new PageUtils(list,total,query.getLimit(),query.getPage());
        return page;
    }
}
