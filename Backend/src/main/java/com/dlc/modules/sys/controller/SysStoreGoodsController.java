package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.api.entity.UserInfo;
import com.dlc.modules.api.service.UserInfoService;
import com.dlc.modules.sys.entity.SysStoreGoodsEntity;
import com.dlc.modules.sys.entity.SysUserEntity;
import com.dlc.modules.sys.service.SysIncomePayDetailService;
import com.dlc.modules.sys.service.SysStoreGoodsService;
import com.dlc.modules.sys.service.SysWalletDetailService;
import com.dlc.modules.sys.shiro.ShiroUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 门店商品表
 *
 * @author wangsheng
 * @email 1524400275@qq.com
 * @date 2019-01-14 09:04:26
 */
@RestController
@RequestMapping("sys/storegoods")
public class SysStoreGoodsController {
    @Autowired
    private SysStoreGoodsService sysStoreGoodsService;

    @Autowired
    private UserInfoService userInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:storegoods:list")
    public R list(@RequestParam Map<String, Object> params) {
        SysUserEntity user = ShiroUtils.getUserEntity();
        if (user.getStoreId() != null && !"".equals(user.getStoreId()))
            params.put("storeId", user.getStoreId());
        Query query = new Query(params);
        List<SysStoreGoodsEntity> sysStoreGoodsList = sysStoreGoodsService.queryList(query);
        int total = sysStoreGoodsService.queryTotal(query);
        PageUtils page = new PageUtils(sysStoreGoodsList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{goodsId}")
    @RequiresPermissions("sys:storegoods:info")
    public R info(@PathVariable("goodsId") Long goodsId) {
        SysStoreGoodsEntity storeGoods = sysStoreGoodsService.queryObject(goodsId);

        return R.ok().put("storeGoods", storeGoods);
    }

    /**
     * 信息
     */
    @RequestMapping("/infoGoods/{barCode}")
    @RequiresPermissions("sys:storegoods:infoGoods")
    public R infoGoods(@PathVariable("barCode") String barCode) {
        SysStoreGoodsEntity storeGoods = sysStoreGoodsService.queryGoodsByBarCode(barCode);
        if (storeGoods == null) {
            return R.error("此商品不存在");
        }
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("checked", false);
        resMap.put("name", storeGoods.getName());
        resMap.put("price", storeGoods.getPrice());
        resMap.put("style", storeGoods.getStyle());
        resMap.put("barCode", storeGoods.getBarCode());
        resMap.put("num", 1);
        return R.ok().put("storeGoods", resMap);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:storegoods:save")
    public R save(@RequestBody SysStoreGoodsEntity sysStoreGoods) {
        SysStoreGoodsEntity storeGoods = sysStoreGoodsService.queryGoodsByBarCode(sysStoreGoods.getBarCode());
        if (storeGoods != null) {
            return R.error("此商品已存在");
        }
        SysUserEntity user = ShiroUtils.getUserEntity();
        if (user.getStoreId() != null && !"".equals(user.getStoreId()))
            sysStoreGoods.setStoreId(user.getStoreId());
        sysStoreGoods.setCreatedDate(new Date());
        sysStoreGoodsService.save(sysStoreGoods);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:storegoods:update")
    public R update(@RequestBody SysStoreGoodsEntity sysStoreGoods) {
        SysStoreGoodsEntity temp = sysStoreGoodsService.queryObject(sysStoreGoods.getGoodsId());

        SysStoreGoodsEntity storeGoods = sysStoreGoodsService.queryGoodsByBarCode(sysStoreGoods.getBarCode());
        if (storeGoods != null && !temp.getBarCode().equals(  storeGoods.getBarCode())) {
            return R.error("此商品已存在");
        }
        ValidatorUtils.validateEntity(sysStoreGoods);
        sysStoreGoodsService.update(sysStoreGoods);//全部更新

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:storegoods:delete")
    public R delete(@RequestBody Long[] goodsIds) {
        sysStoreGoodsService.deleteBatch(goodsIds);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/queryCountByBarCode")
    @RequiresPermissions("sys:storegoods:queryCountByBarCode")
    public R queryCountByBarCode(@RequestParam Map<String, Object> params) {
        return sysStoreGoodsService.queryCountByBarCode(params) > 0 ? R.error("已存在该条形码，请重新输入") : R.ok();
    }


    /**
     * 门店商品结算
     *
     * @return
     */
    @RequestMapping("/goodsAccounts")
    @RequiresPermissions("sys:storegoods:goodsAccounts")
    public R goodsAccounts(@RequestBody Map<String, Object> params) {
        params.put("storeIds", ShiroUtils.getUserEntity().getStoreIds());
        String wristId = params.get("wristId").toString();
        //通过手环id查询用户余额

        UserInfo userInfo = userInfoService.queryUserIdByCardid(wristId);
        if (userInfo == null) {
            return R.error("此手环（或卡）尚未绑定用户");
        }
        //查看该用户会员手环是否已经禁用，如果已禁用返回失败
        int forbidden = sysStoreGoodsService.queryIsForbiddenStatus(userInfo.getUserId(), wristId);
        if (forbidden > 0) {
            return R.error("无效手环（或已禁用）");
        }
        BigDecimal userWallet = userInfoService.queryUserWalletByUserId(userInfo.getUserId());
        if (userWallet.compareTo(new BigDecimal(params.get("totalMoney").toString())) == -1) {
            return R.error("用户余额不足");
        }
        params.put("userId", userInfo.getUserId());
        return sysStoreGoodsService.goodsAccounts(params, userInfo, userWallet);
    }


}
