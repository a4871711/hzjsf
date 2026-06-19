package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.modules.sys.entity.CardType;
import com.dlc.modules.sys.entity.FitCardEntity;
import com.dlc.modules.sys.service.SysFitCardService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * 健身卡(会员卡)
 *
 * @author wangsheng
 * @email
 * @date 2018-09-10 10:24:28
 */
@RestController
@RequestMapping("sys/fitcard")
public class SysFitCardController {
    @Autowired
    private SysFitCardService fitCardService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:fitcard:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<FitCardEntity> fitCardList = fitCardService.queryList(query);
        int total = fitCardService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(fitCardList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils).put("cardType", fitCardService.selectCardTypeList());
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{fitcardid}")
    @RequiresPermissions("sys:fitcard:info")
    public R info(@PathVariable("fitcardid") Long fitCardId) {
        FitCardEntity fitCard = fitCardService.queryObject(fitCardId);
        return R.ok().put("fitCard", fitCard);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:fitcard:save")
    public R save(@RequestBody FitCardEntity fitCard) {
        fitCard.setCreatedDate(new Date());
        fitCardService.save(fitCard);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:fitcard:update")
    public R update(@RequestBody FitCardEntity fitCard) {
        fitCardService.update(fitCard);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:fitcard:delete")
    public R delete(@RequestBody Long[] fitCardIds) {
        if (fitCardIds != null) {
            for (Long fitCardId :
                    fitCardIds) {
                int result = fitCardService.fitCardCount(fitCardId);
                if (result > 0) {
                    return R.error("该会员卡已被购买，不能删除");
                }
            }
        }
        fitCardService.deleteBatch(fitCardIds);
        return R.ok();
    }
    /**
     * 上架
     */
    @RequestMapping("/onCard")
    @RequiresPermissions("sys:fitcard:update")
    public R onCard(@RequestBody Long[] fitCardIds) {
        if (fitCardIds != null) {
            for (Long fitCardId :
                    fitCardIds) {
                fitCardService.updateOnOffCard(fitCardId, 1);
            }
        }
        return R.ok();
    }
    /**
     * 下架
     */
    @RequestMapping("/offCard")
    @RequiresPermissions("sys:fitcard:update")
    public R offCard(@RequestBody Long[] fitCardIds) {
        if (fitCardIds != null) {
            for (Long fitCardId :
                    fitCardIds) {
                fitCardService.updateOnOffCard(fitCardId, 2);
            }
        }
        return R.ok();
    }
    /**
     * 删除卡类型
     */
    @RequestMapping("/deleteCt")
    public R deleteCt(@RequestBody Long ctId) {
        if( ctId== null ){ return R.error("操作有误"); }
        //查询该类型的卡是否已有人买过
        int res = fitCardService.queryIfExistCardTypeByCtId(ctId);
        if( res > 0 ){ return R.error("此类型卡已有会员购买记录，请勿删除"); }
        fitCardService.deleteCardType(ctId);
        return R.ok();
    }
    /**
     * 卡类型
     */
    @RequestMapping("/getCardTypeList")
    public R getModelList() {
        return R.ok().put("cardType", fitCardService.selectCardTypeList());
    }

    /**
     * 保存卡类型
     */
    @RequestMapping("/saveCardType")
    public R saveCardType(@RequestBody CardType cardType) {
        //查询卡类型是否已存在
        int cflag = fitCardService.queryIfExistCardType(cardType.getCtName());
        if(cflag > 0){
            return R.error("卡类型:"+cardType.getCtName()+" 已存在");
        }
        //获取类型最大值并+1
        Integer lastCardType = fitCardService.getLastCardType();
        if(lastCardType == null){
            lastCardType = 100;     //防止卡类型数据异常此处类型定义100开始
        }
        cardType.setCardType(lastCardType);
        //保存卡类型信息
        fitCardService.saveCardType(cardType);
        return R.ok();
    }
    /**
     * 保存卡类型
     */
    @RequestMapping("/updateCardType")
    public R updateCardType(@RequestBody CardType cardType) {
        //查询卡类型是否已存在
        int cflag = fitCardService.queryIfExistCardType(cardType.getCtName());
        if(cflag > 0){
            return R.error("卡类型:"+cardType.getCtName()+" 已存在");
        }

        //更新卡类型信息
        fitCardService.updateCardType(cardType);
        return R.ok();
    }
}
