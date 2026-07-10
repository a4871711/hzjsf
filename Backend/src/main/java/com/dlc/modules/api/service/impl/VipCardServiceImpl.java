package com.dlc.modules.api.service.impl;

import com.dlc.common.exception.RRException;
import com.dlc.common.utils.CodeAndMsg;
import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.modules.api.dao.FitCardMapper;
import com.dlc.modules.api.dao.StoreAddressMapper;
import com.dlc.modules.api.dao.VipBenefitCardMapper;
import com.dlc.modules.api.entity.FitCard;
import com.dlc.modules.api.entity.VipBenefitCard;
import com.dlc.modules.api.service.VipBenefitService;
import com.dlc.modules.api.service.VipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品 Service 实现(移动端浏览)
 */
@Service("vipCardService")
public class VipCardServiceImpl implements VipCardService {
    @Autowired
    private VipBenefitCardMapper vipBenefitCardMapper;
    @Autowired
    private FitCardMapper fitCardMapper;
    @Autowired
    private VipBenefitService vipBenefitService;
    @Autowired
    private StoreAddressMapper storeAddressMapper;

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

    @Override
    public VipBenefitCard queryVipCardDetail(Long vipCardId, Long userId) {
        VipBenefitCard card = queryVipCardDetail(vipCardId);
        // 该权益卡绑定的可购买会员卡(仅上架),供详情页"可购买会员卡"区块展示
        card.setBindFitCards(loadBindFitCards(card.getBindFitCardIds()));
        // 当前用户是否已持有效权益:决定前端点击会员卡是否放行。
        // 统一走 hasValidBenefit,与购卡 -38 硬校验口径同源,避免"页面放行、下单被拒"的分叉
        card.setHasBenefit(vipBenefitService.hasValidBenefit(userId));
        // 适用门店名列表:详情页"适用哪些门店"弹窗展示
        card.setStoreNames(loadStoreNames(card.getStoreAddrIds()));
        return card;
    }

    /** 解析逗号分隔的 storeAddrIds → 查门店名列表;空/无有效 id 返回空列表 */
    private List<String> loadStoreNames(String storeAddrIds) {
        List<Long> ids = parseIds(storeAddrIds);
        if (ids.isEmpty()) {
            return new ArrayList<String>();
        }
        List<String> names = storeAddressMapper.selectStoreNamesByIds(ids);
        return names != null ? names : new ArrayList<String>();
    }

    /** 解析逗号分隔的 bindFitCardIds → 查上架会员卡详情;空/无有效 id 返回空列表 */
    private List<FitCard> loadBindFitCards(String bindFitCardIds) {
        List<Long> ids = parseIds(bindFitCardIds);
        if (ids.isEmpty()) {
            return new ArrayList<FitCard>();
        }
        List<FitCard> list = fitCardMapper.selectOnShelfByIds(ids);
        return list != null ? list : new ArrayList<FitCard>();
    }

    /** 解析逗号分隔 id 串为 Long 列表,脏数据跳过 */
    private List<Long> parseIds(String idsStr) {
        List<Long> ids = new ArrayList<Long>();
        if (idsStr != null && !idsStr.trim().isEmpty()) {
            for (String s : idsStr.split(",")) {
                String t = s.trim();
                if (!t.isEmpty()) {
                    try {
                        ids.add(Long.parseLong(t));
                    } catch (NumberFormatException ignore) {
                        // 脏数据跳过
                    }
                }
            }
        }
        return ids;
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
