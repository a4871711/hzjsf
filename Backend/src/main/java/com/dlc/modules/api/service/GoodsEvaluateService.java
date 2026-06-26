package com.dlc.modules.api.service;

import com.dlc.common.utils.Query;
import com.dlc.modules.api.entity.GoodsEvaluate;
import com.dlc.modules.api.vo.UserInfoVo;

import java.util.List;
import java.util.Map;

public interface GoodsEvaluateService {
    List<Map<String,Object>> goodsEvaluateList(Map<String, Object> query);

    int goodsEvaluateListCount(Query query);

    int save(UserInfoVo user, Map<String, Object> params);
}
