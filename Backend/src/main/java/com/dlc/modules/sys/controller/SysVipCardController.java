package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.VipBenefitCardEntity;
import com.dlc.modules.sys.service.SysVipCardService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * VIP 权益卡商品(vip_benefit_card)后台管理
 * 仿 SysFitCardController。sold_count 系统维护、后台只读,save/update 一律忽略前端传入值。
 *
 * @date 2026-06-28
 */
@RestController
@RequestMapping("sys/vipCard")
public class SysVipCardController {

    @Autowired
    private SysVipCardService sysVipCardService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:vipcard:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<VipBenefitCardEntity> list = sysVipCardService.queryList(query);
        int total = sysVipCardService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(list, total, query.getLimit(), query.getPage());
        // 与 VIP 模块 sibling(SysVipFeeRuleController)及前端 r-table 约定一致,key 用 "pages"
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{vipCardId}")
    @RequiresPermissions("sys:vipcard:info")
    public R info(@PathVariable("vipCardId") Long vipCardId) {
        VipBenefitCardEntity vipCard = sysVipCardService.queryObject(vipCardId);
        return R.ok().put("vipCard", vipCard);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:vipcard:save")
    public R save(@RequestBody VipBenefitCardEntity vipCard) {
        R check = validate(vipCard);
        if (check != null) {
            return check;
        }
        sysVipCardService.save(vipCard);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:vipcard:update")
    public R update(@RequestBody VipBenefitCardEntity vipCard) {
        if (vipCard.getVipCardId() == null) {
            return R.error("缺少权益卡ID");
        }
        R check = validate(vipCard);
        if (check != null) {
            return check;
        }
        sysVipCardService.update(vipCard);
        return R.ok();
    }

    /**
     * 删除:已售出(存在关联 vip_benefit 记录)的权益卡不可删
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:vipcard:delete")
    public R delete(@RequestBody Long[] vipCardIds) {
        if (vipCardIds != null) {
            for (Long vipCardId : vipCardIds) {
                int result = sysVipCardService.benefitCount(vipCardId);
                if (result > 0) {
                    return R.error("该权益卡已售出,不能删除");
                }
            }
        }
        sysVipCardService.deleteBatch(vipCardIds);
        return R.ok();
    }

    /**
     * 上架(批量,status=1)
     */
    @RequestMapping("/onCard")
    @RequiresPermissions("sys:vipcard:update")
    public R onCard(@RequestBody Long[] vipCardIds) {
        if (vipCardIds != null) {
            for (Long vipCardId : vipCardIds) {
                sysVipCardService.updateOnOffCard(vipCardId, 1);
            }
        }
        return R.ok();
    }

    /**
     * 下架(批量,status=2)。仅停售,不影响已售出实例与转让流程
     */
    @RequestMapping("/offCard")
    @RequiresPermissions("sys:vipcard:update")
    public R offCard(@RequestBody Long[] vipCardIds) {
        if (vipCardIds != null) {
            for (Long vipCardId : vipCardIds) {
                sysVipCardService.updateOnOffCard(vipCardId, 2);
            }
        }
        return R.ok();
    }

    /**
     * 公共校验:名称非空、售价>=0、涨价步进人数>=0
     */
    private R validate(VipBenefitCardEntity vipCard) {
        if (vipCard.getCardName() == null || vipCard.getCardName().trim().isEmpty()) {
            return R.error("权益卡名称不能为空");
        }
        if (vipCard.getPrice() == null || vipCard.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            return R.error("售价不能小于0");
        }
        if (vipCard.getStepNum() != null && vipCard.getStepNum() < 0) {
            return R.error("涨价步进人数不能小于0");
        }
        if (vipCard.getStepAddPrice() != null && vipCard.getStepAddPrice().compareTo(BigDecimal.ZERO) < 0) {
            return R.error("每档加价不能小于0");
        }
        if (vipCard.getPriceCap() != null && vipCard.getPriceCap().compareTo(BigDecimal.ZERO) < 0) {
            return R.error("封顶价不能小于0");
        }
        return null;
    }
}
