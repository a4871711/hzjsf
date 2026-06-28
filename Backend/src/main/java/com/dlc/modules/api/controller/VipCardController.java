package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.service.VipCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * VIP 权益卡商品(移动端浏览,公开接口,不取身份)
 */
@RestController
@RequestMapping("/api/vipCard")
public class VipCardController extends BaseController {
    @Autowired
    private VipCardService vipCardService;

    /**
     * 权益卡列表(小程序浏览),分页 + 后端实时算的动态价 currentPrice
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        // page/limit 规范成正整数:挡住 0/负数/非数字脏参数,
        // 否则 Query 算出负 offset → SQL "LIMIT 负数" 报错
        params.put("page", String.valueOf(toPositiveInt(params.get("page"), 1)));
        params.put("limit", String.valueOf(toPositiveInt(params.get("limit"), 10)));
        return R.reOk(vipCardService.queryVipCardList(params));
    }

    /** 把入参解析为正整数;null / 非数字 / <=0 一律取默认值 */
    private int toPositiveInt(Object val, int defaultVal) {
        if (val == null) {
            return defaultVal;
        }
        try {
            int n = Integer.parseInt(val.toString().trim());
            return n > 0 ? n : defaultVal;
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    /**
     * 权益卡详情,后端实时算动态价;不存在/已下架返回 ERROR_VIP_CARD_OFF_SHELF
     */
    @RequestMapping("/detail")
    public R detail(Long vipCardId) {
        if (vipCardId == null) {
            return R.reError("缺少参数 vipCardId");
        }
        return R.reOk(vipCardService.queryVipCardDetail(vipCardId));
    }
}
