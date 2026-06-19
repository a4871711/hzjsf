package com.dlc.modules.api.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.dlc.common.exception.RRException;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.PointsExchangeMapper;
import com.dlc.modules.api.dao.UserInfoMapper;
import com.dlc.modules.api.dao.UserPointsMapper;
import com.dlc.modules.api.dao.UserWalletMapper;
import com.dlc.modules.api.entity.PointsExchange;
import com.dlc.modules.api.service.PointsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class PointsServiceImpl implements PointsService {

    private final Logger log = LoggerFactory.getLogger(PointsServiceImpl.class);

    @Autowired
    private UserPointsMapper userPointsMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private PointsExchangeMapper pointsExchangeMapper;
    @Autowired
    private UserWalletMapper userWalletMapper;
    @Override
    public Map<String, Object> queryMyPoints(Long userId) {
        log.info("PointsService.queryMyPoints start..." + userId);
        Map<String, Object> result = userPointsMapper.getPointsByUserId(userId);
        if(null == result){result = new HashMap<String, Object>();}
        return result;
    }

    @Override
    public List<Map<String, Object>> queryPointsExchange(Long userId) {
        log.info("PointsService.PointsExchangeMapper start..." + userId);
        List<Map<String, Object>> result = pointsExchangeMapper.getPointDetailByUserId(userId);
        if(CollectionUtils.isEmpty(result)){result = new ArrayList<Map<String, Object>>();}
        return result;
    }

    @Override
    public R pointsExchange(PointsExchange pointsExchange, Long userId) {
        log.info("PointsService.pointsExchange start..." + userId);
        if (null == pointsExchange.getDetailType() || null == pointsExchange.getExchangeMoney() ||
                null == pointsExchange.getPointsCount()) {
            return R.reError("兑换信息缺失"); //兑换信息缺失
        }
        if (pointsExchange.getUserId() == null) {
            pointsExchange.setUserId(userId);
        }
        Map<String, Object> updateMap = new HashMap<String, Object>();
        int res = 0;
        int r = 0;
        int s = 0;
        try {
            updateMap.put("pointsCount", pointsExchange.getPointsCount());
            updateMap.put("userId", pointsExchange.getUserId());
            //查询用户所属门店
            Long storeId = userInfoMapper.queryStoreIdByUserId(userId);
            pointsExchange.setStoreId(storeId);
            res = pointsExchangeMapper.insertSelective(pointsExchange);
            if(res <= 0){
                return R.reError("兑换失败");
            }
            BigDecimal money = new BigDecimal(pointsExchange.getExchangeMoney());
            s = userWalletMapper.updateMyMoney(pointsExchange.getUserId(), money);
            if(s <= 0){
                return R.reError("兑换失败");
            }
            r = userPointsMapper.updatePointCount(updateMap);
            if(r <= 0){
                return R.reError("兑换失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RRException("请求失败",e);
        }
        return R.reOk();
    }

    @Override
    public int updatePointCountTask(Map<String, Object> updateMap) {
        return userPointsMapper.updatePointCountTask(updateMap);
    }


}
