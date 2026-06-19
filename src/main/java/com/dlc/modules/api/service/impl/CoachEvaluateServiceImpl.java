package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.CoachEvaluateMapper;
import com.dlc.modules.api.dao.StoreCoachEvaluateMapper;
import com.dlc.modules.api.service.CoachEvaluateService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/19/019
 */
@Service
@Transactional
public class CoachEvaluateServiceImpl implements CoachEvaluateService{

    @Autowired
    private CoachEvaluateMapper coachEvaluateMapper;
    @Autowired
    private StoreCoachEvaluateMapper storeCoachEvaluateMapper;

    @Override
    public int countCoachEva(Long coachId) {
        return coachEvaluateMapper.countCoachEva(coachId);
    }

    /**
     *  @Auther:YD
     *  @parameters:
     *  教练评价列表
     */
    @Override
    public List<Map<String, Object>> coachEvaList(Query query) {
        List<Map<String,Object>> list =  coachEvaluateMapper.coachEvaList(query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String,Object> map : list){
            String evaluatDate = sdf.format(map.get("evaluatDate"));
            map.put("evaluatDate",evaluatDate);
            //处理表情
            if(map.get("evContent") != null){
                map.put("evContent", EmojiParser.parseToUnicode((String) map.get("evContent")));
            }
        }
        return list;
    }

    @Override
    public int queryTotal(Query query) {
        return coachEvaluateMapper.queryTotal(query);
    }
    /**场馆教练列表评价*/
    @Override
    public List<Map<String, Object>> storeCoachEvaList(Query query) {
        List<Map<String,Object>> list =  storeCoachEvaluateMapper.sroreCoachEvaList(query);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (Map<String,Object> map : list){
            String evaluatDate = sdf.format(map.get("evaluatDate"));
            map.put("evaluatDate",evaluatDate);
            //处理表情
            if(map.get("evContent") != null){
                map.put("evContent", EmojiParser.parseToUnicode((String) map.get("evContent")));
            }
            //处理表情
            if(map.get("nickname") != null){
                map.put("nickname", EmojiParser.parseToUnicode((String) map.get("nickname")));
            }
        }
        return list;
    }

    @Override
    public int queryStoreCoachTotal(Query query) {
        return storeCoachEvaluateMapper.queryStoreCoachTotal(query);
    }

    @Override
    public int countStoreCoachEva(Long coachId) {
        return storeCoachEvaluateMapper.queryStoreCoachEvelTotal(coachId);
    }

}

