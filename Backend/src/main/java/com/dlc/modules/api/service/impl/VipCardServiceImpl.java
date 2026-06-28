package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.VipBenefitCardMapper;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.service.VipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品 Service 实现(移动端浏览)
 */
@Service("vipCardService")
public class VipCardServiceImpl implements VipCardService {
    @Autowired
    private VipBenefitCardMapper vipBenefitCardMapper;

    @Override
    public PageUtils queryVipCardList(Map<String, Object> params) {
        Query query = new Query(params);
        List<VipBenefitCard> list = vipBenefitCardMapper.queryList(query);
        int total = vipBenefitCardMapper.queryTotal(query);
        if (list != null) {
            for (VipBenefitCard card : list) {
                fillDisplayFields(card);
            }
        }
        return new PageUtils(list, total, query.getLimit(), query.getPage());
    }

    @Override
    public VipBenefitCard queryVipCardDetail(Long vipCardId) {
        VipBenefitCard card = vipBenefitCardMapper.queryObject(vipCardId);
        if (card == null) {
            // 查不到上架卡(不存在或已下架)
            throw new RRException(CodeAndMsg.ERROR_VIP_CARD_OFF_SHELF);
        }
        fillDisplayFields(card);
        return card;
    }

    /** 填充展示字段:实时动态价 + 按 show_buy_count 控制是否暴露购买人数 */
    private void fillDisplayFields(VipBenefitCard card) {
        card.setCurrentPrice(calcCurrentPrice(card));
        // show_buy_count != 1 时不对外暴露真实购买人数,置 0
        if (card.getShowBuyCount() == null || card.getShowBuyCount() != 1) {
            card.setSoldCount(0);
        }
    }

    /**
     * 动态购买价(详细技术设计 §8.3):按权益卡当前真实累计购买人数 sold_count 实时计算。
     * 关闭涨价(step_num<=0 或 step_add_price<=0)→ 返回基础价 price;
     * sold_count<=base_buy_count → 返回基础价;
     * 否则 tier=floor((n-base)/step_num)(整数除法即向下取整),p=price+tier*step_add_price,
     * price_cap 非空时 p=min(p, price_cap)。
     */
    private BigDecimal calcCurrentPrice(VipBenefitCard card) {
        BigDecimal price = card.getPrice() == null ? BigDecimal.ZERO : card.getPrice();
        Integer stepNum = card.getStepNum();
        BigDecimal stepAddPrice = card.getStepAddPrice();
        // 关闭动态涨价
        if (stepNum == null || stepNum <= 0
                || stepAddPrice == null || stepAddPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return price;
        }
        int n = card.getSoldCount() == null ? 0 : card.getSoldCount();
        int base = card.getBaseBuyCount() == null ? 0 : card.getBaseBuyCount();
        // 尚未越过起涨基数,不涨价
        if (n <= base) {
            return price;
        }
        int tier = (n - base) / stepNum;   // 整数除法 = 向下取整
        BigDecimal p = price.add(stepAddPrice.multiply(new BigDecimal(tier)));
        BigDecimal cap = card.getPriceCap();
        if (cap != null && p.compareTo(cap) > 0) {
            p = cap;
        }
        return p;
    }
}
