package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.ApiFlashSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员端限时秒杀(移动端)。首页秒杀卡片公开可看(不强制登录)。
 * 抢购下单复用各域现有下单接口(会员卡 /api/cardOrder/createOrder、权益卡 /api/vipCard/buy)，仅多传 flashSaleActivityId。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/flashSale")
public class ApiFlashSaleController extends BaseController {

    @Autowired
    private ApiFlashSaleService apiFlashSaleService;

    /**
     * 当前秒杀卡片列表：进行中 + 预热中的上架活动。
     * storeAddrId=当前门店(可空)：传则按商品适用门店过滤(会员卡 storeAddrIds/showStoreAddrIds、权益卡 store_addr_ids,空=通店)。
     * 每项：activityId/bizType(2会员卡3权益卡)/productId/productName/coverUrl/flashSalePrice/originPrice
     *      /activityStock/soldCount/soldPct/purchaseLimit/startTime/endTime/serverTime/status(preheat|ongoing|soldout)。
     * 空数组表示当前无秒杀，前端不渲染该区块。
     */
    @RequestMapping("/current")
    public R current(Long storeAddrId) {
        return R.reOk(apiFlashSaleService.queryCurrentCards(storeAddrId));
    }
}
