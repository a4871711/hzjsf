package com.dlc.modules.api.controller;

import com.dlc.common.utils.R;
import com.dlc.modules.api.entity.PtCoachOption;
import com.dlc.modules.api.service.CoachApiService;
import com.dlc.modules.api.vo.PtAvailableSlotVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 私教教练会员端只读浏览（可匿名访问，无需登录）。
 * 业务校验失败由 service 层抛 RRException，交由全局 RRExceptionHandler 统一转换为 R 返回。
 *
 * @author claude
 */
@RestController
@RequestMapping("/api/coach")
public class CoachApiController extends BaseController {

    @Autowired
    private CoachApiService coachApiService;

    /** 需求 8.3：某私教商品当前可预约的教练列表 */
    @RequestMapping("/listByProduct")
    public R listByProduct(Long productId) {
        if (productId == null) {
            return R.reError("商品ID不能为空");
        }
        List<PtCoachOption> list = coachApiService.listByProduct(productId);
        return R.reOk(list);
    }

    /** 需求 5.4：某商品+教练+门店+日期 的可约时段切片 */
    @RequestMapping("/availableSlots")
    public R availableSlots(Long productId, Long coachId, Long storeId, String date) {
        if (productId == null || coachId == null || storeId == null || StringUtils.isBlank(date)) {
            return R.reError("参数不完整");
        }
        List<PtAvailableSlotVo> slots = coachApiService.availableSlots(productId, coachId, storeId, date);
        return R.reOk(slots);
    }
}
