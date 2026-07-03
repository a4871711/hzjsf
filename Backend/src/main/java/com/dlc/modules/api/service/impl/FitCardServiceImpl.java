package com.dlc.modules.api.service.impl;

import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.R;
import com.dlc.modules.api.dao.CouponMapper;
import com.dlc.modules.api.dao.DataMapMapper;
import com.dlc.modules.api.dao.FitCardMapper;
import com.dlc.modules.api.dao.VipBenefitMapper;
import com.dlc.modules.api.entity.Coupon;
import com.dlc.modules.api.entity.DataMap;
import com.dlc.modules.api.entity.FitCard;
import com.dlc.modules.api.service.FitCardService;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * @Auther:YD
 * @Date: Creat in  2018/9/11/011
 */
@Service
@Transactional
public class FitCardServiceImpl implements FitCardService {

    @Autowired
    private FitCardMapper fitCardMapper;
    @Autowired
    private DataMapMapper dataMapMapper;
    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private SysIncomePayDetailService sysIncomePayDetailService;
    @Autowired
    private VipBenefitMapper vipBenefitMapper;

    /**
     *  @Auther:YD
     *  @parameters:
     *  查询健身卡列表
     */
    @Override
    public List<Map<String, Object>> queryFitCardList(FitCard fitCard, Long userId) {
        List<Map<String,Object>> list = fitCardMapper.queryFitCardList(fitCard);
        int isNewUser = 1;
        if(userId != null && sysIncomePayDetailService.hasValidCardPurchase(userId)) {
            isNewUser = 0;
        }
        //权益会员:名下有「正常且未过期」VIP权益卡;配置了权益卡价格(benefitPrice)的卡对其显示/按权益价售卖
        int isBenefitMember = (userId != null && vipBenefitMapper.countValidByUser(userId) > 0) ? 1 : 0;
        if(list != null) {
            for(Map<String, Object> item : list) {
                item.put("isNewUser", isNewUser);
                item.put("isBenefitMember", isBenefitMember);
                item.put("salePrice", resolveListSalePrice(item, isNewUser == 1, isBenefitMember == 1));
            }
        }
        return list;
    }

    /**
     *  @Auther:YD
     *  @parameters: id： 健身卡id
     *  查询健身卡详情
     */
    @Override
    public Map<String, Object> queryFitCardInfo(Long id) {
        Map<String,Object> map = fitCardMapper.queryFitCardInfo(id);
        // 查询手环价格
        Map<String,Object> dataMap = dataMapMapper.findBraceletPrice();
        //BigDecimal braceletPrice = BigDecimal.valueOf((Integer) dataMap.get("price"));
        map.put("price",dataMap.get("price"));
        map.put("braceletName",dataMap.get("dataName"));
        return map;
    }

    /** 列表展示用应付价：权益会员且卡配了权益卡价 → benefitPrice；新人 newUserPrice→cardPrice→costPrice；老用户 cardPrice→costPrice */
    private Object resolveListSalePrice(Map<String, Object> item, boolean isNewUser, boolean isBenefitMember) {
        if(isBenefitMember) {
            Object benefitPrice = firstPositiveFromMap(item, "benefitPrice");
            if(benefitPrice != null) {
                return benefitPrice;
            }
        }
        if(isNewUser) {
            Object price = firstPositiveFromMap(item, "newUserPrice", "cardPrice", "costPrice");
            if(price != null) {
                return price;
            }
        }
        Object price = firstPositiveFromMap(item, "cardPrice", "costPrice");
        return price != null ? price : item.get("cardPrice");
    }

    private Object firstPositiveFromMap(Map<String, Object> item, String... keys) {
        for(String key : keys) {
            Object val = item.get(key);
            if(val != null && StringUtils.isNotBlank(String.valueOf(val))
                    && new BigDecimal(String.valueOf(val)).compareTo(BigDecimal.ZERO) > 0) {
                return val;
            }
        }
        return null;
    }

    @Override
    public Coupon queryByCouponId(Long couponId) {
        Coupon coupon = couponMapper.selectByCouponId(couponId);
        return coupon;
    }
}
