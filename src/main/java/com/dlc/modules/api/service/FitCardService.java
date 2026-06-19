package com.dlc.modules.api.service;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.entity.FitCard;
import com.dlc.modules.api.vo.UserInfoVo;

import java.util.List;
import java.util.Map;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/11/011
 */
public interface FitCardService {
    /**
     *  @Auther:YD
     *  查询健身卡列表
     */
    List<Map<String,Object>> queryFitCardList(FitCard fitCard, Long userId);
    /**
     *  @Auther:YD
     *  查询健身卡详情
     */
    Map<String,Object> queryFitCardInfo(Long id);

    Coupon queryByCouponId(Long couponId);
}
