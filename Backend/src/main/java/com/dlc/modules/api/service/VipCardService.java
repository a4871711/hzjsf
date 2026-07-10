package com.dlc.modules.api.service;

import com.dlc.common.utils.PageUtils;
import com.dlc.modules.api.entity.VipBenefitCard;

import java.util.Map;

/**
 * VIP 权益卡商品 Service(移动端浏览)
 */
public interface VipCardService {

    /** 分页查上架权益卡列表,每条带实时动态价 currentPrice;show_buy_count=0 的卡不暴露 soldCount */
    PageUtils queryVipCardList(Map<String, Object> params);

    /** 查单条上架权益卡详情,带实时动态价;不存在/已下架抛 ERROR_VIP_CARD_OFF_SHELF */
    VipBenefitCard queryVipCardDetail(Long vipCardId);

    /**
     * 详情页展示版:在 {@link #queryVipCardDetail(Long)} 基础上额外填充
     * 绑定的可购买会员卡(bindFitCards,仅上架)与当前用户是否已持有效权益(hasBenefit)。
     * userId 为 null(未登录)时 hasBenefit=false。
     */
    VipBenefitCard queryVipCardDetail(Long vipCardId, Long userId);
}
