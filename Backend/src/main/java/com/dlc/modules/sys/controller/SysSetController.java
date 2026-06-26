package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysDataMapEntity;
import com.dlc.modules.sys.service.SysSetService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys")
public class SysSetController {
    @Autowired
    private SysSetService sysSetService;

    /**
     * 积分集合
     *
     * @return
     */
    @RequestMapping("/creditset/list")
    @RequiresPermissions("sys:datamap:list")
    public R creditsetlist(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryCreditsetList(query);
        int total = sysSetService.queryCreditsetTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 查询积分
     */
    @RequestMapping("/info/{dataMapId}")
    @RequiresPermissions("sys:datamap:info")
    public R creditsetinfo(@PathVariable("dataMapId") Long dataMapId) {
        SysDataMapEntity dataMapEntity = sysSetService.queryCreditsetObject(dataMapId);
        return R.ok().put("dataMapEntity", dataMapEntity);
    }

    /**
     * 保存积分
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:datamap:save")
    public R creditsetSave(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.saveCreditset(dataMapEntity);
        return R.ok();
    }

    /**
     * 修改积分
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:datamap:update")
    public R creditsetUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.updateCreditset(dataMapEntity);//全部更新
        return R.ok();
    }
    @RequestMapping("/creditset/delete")
    @RequiresPermissions("sys:datamap:delete")
    public R creditsetDelete(@RequestBody Long[] ids) {
        int result = sysSetService.deleteBatch(ids);
        if (result > 0) {
            return R.ok();
        } else {
            return R.error("未知意外，稍后重试");
        }
    }

    /**
     * 手环集合
     *
     * @return braceletset
     */
    @RequestMapping("/braceletset/list")
    @RequiresPermissions("sys:datamap:list")
    public R braceletsetlist(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryBraceletsetList(query);
        int total = sysSetService.queryBraceletsetTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 查询手环
     */
    @RequestMapping("/braceletset/info/{dataMapId}")
    @RequiresPermissions("sys:datamap:info")
    public R braceletsetinfo(@PathVariable("dataMapId") Long dataMapId) {
        SysDataMapEntity dataMapEntity = sysSetService.queryBraceletsetObject(dataMapId);
        return R.ok().put("dataMapEntity", dataMapEntity);
    }

    /**
     * 修改手环设置
     */
    @RequestMapping("/braceletset/update")
    @RequiresPermissions("sys:datamap:update")
    public R braceletsetUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.updateBraceletset(dataMapEntity);//全部更新
        return R.ok();
    }

    /**
     * 会员集合
     *
     * @return
     */
    @RequestMapping("/member/list")
    @RequiresPermissions("sys:datamap:list")
    public R memberlist(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryMembersetList(query);
        int total = sysSetService.queryMembersetTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 查询会员
     */
    @RequestMapping("/member/info/{dataMapId}")
    @RequiresPermissions("sys:datamap:info")
    public R membersetinfo(@PathVariable("dataMapId") Long dataMapId) {
        SysDataMapEntity dataMapEntity = sysSetService.queryMembersetObject(dataMapId);
        return R.ok().put("dataMapEntity", dataMapEntity);
    }
    /**
     * 查询会员
     */
    @RequestMapping("/memberLevel/list")
    @RequiresPermissions("sys:memberLevel:list")
    public R memberLevelList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryMembersetRuleList(query);
        int total = sysSetService.queryMembersetRuleTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }

    /**
     * 查询会员
     */
    @RequestMapping("/memberLevel/update")
    @RequiresPermissions("sys:memberLevel:update")
    public R memberLevelUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.updateMemberRule(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 查询课程分类
     */
    @RequestMapping("/classClassify/list")
    @RequiresPermissions("sys:classClassify:list")
    public R classClassifyList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryClassClassifyList(query);
        int total = sysSetService.queryClassClassifyListTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }
    /**
     * 保存教练等级和课程最高价格关系
     */
    @RequestMapping("/classClassify/save")
    @RequiresPermissions("sys:classClassify:save")
    public R classClassifySave(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.saveClassClassify(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 保存教练等级和课程最高价格关系
     */
    @RequestMapping("/classClassify/update")
    @RequiresPermissions("sys:classClassify:update")
    public R classClassifyUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.updateClassClassify(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 查询教练等级和课程最高价格关系
     */
    @RequestMapping("/classGrade/list")
    @RequiresPermissions("sys:classGrade:list")
    public R classGradeList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryClassGradeList(query);
        int total = sysSetService.queryClassGradeListTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }
    /**
     * 修改教练等级和课程最高价格关系
     */
    @RequestMapping("/classGrade/update")
    @RequiresPermissions("sys:classGrade:update")
    public R classGradeUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        //查询当前等级是否已存在
        sysSetService.updateClassGradeUpdate(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 保存教练等级和课程最高价格关系
     */
    @RequestMapping("/classGrade/save")
    @RequiresPermissions("sys:classGrade:save")
    public R classGradeSave(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        //查询当前等级是否已存在
        Integer re = sysSetService.querySameGrade(dataMapEntity.getPrice());
        if(re > 0){return R.error("该等级已存在");}
        sysSetService.saveClassGrade(dataMapEntity);//全部更新
        return R.ok();
    }
    /**
     * 修改教练等级和课程最高价格关系
     */
    @RequestMapping("/classGrade/delete")
    @RequiresPermissions("sys:classGrade:delete")
    public R classGradeDelete(@RequestBody Long ids) {
        //非空验证
        sysSetService.classGradeDelete(ids);//全部更新
        return R.ok();
    }
    /**
     * 修改会员设置
     */
    @RequestMapping("/member/update")
    @RequiresPermissions("sys:datamap:update")
    public R memberUpdate(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        sysSetService.updateMemberset(dataMapEntity);//全部更新
        return R.ok();
    }

    /***
     * 分类
     * @param params
     * @return
     */
    @RequestMapping("/fenlei/list")
    @RequiresPermissions("sys:datamap:list")
    public R fenleilist(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryFeileiList(query);
        int total = sysSetService.queryBraceletsetTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }
    /***
     * 分类新增
     * @return
     */
    @RequestMapping("fenlei/save")
    @RequiresPermissions("sys:datamap:list")
    public R fenleiSave(@RequestBody SysDataMapEntity dataMapEntity) {
        //非空验证
        ValidatorUtils.validateEntity(dataMapEntity);
        //先查询最大nextLable,新增的时候在他的基础上+1
        Long nextLable = sysSetService.queryMaxLableNext();
        dataMapEntity.setIdxLabel(6l);      //6:表示举报类型
        dataMapEntity.setNextLabel(nextLable);       //举报类型编号
        sysSetService.saveCreditset(dataMapEntity);
        return R.ok();
    }
    /**
     * 设置最低提款
     * @param params
     * @return
     */
    @RequestMapping("/moeny/list")
    @RequiresPermissions("sys:datamap:list")
    public R moeny(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysDataMapEntity> dataMapEntityList = sysSetService.queryMoney(query);
        int total = sysSetService.queryBraceletsetTotal(query);
        PageUtils pageUtils = new PageUtils(dataMapEntityList, total, query.getLimit(), query.getPage());
        return R.ok().put("pages", pageUtils);
    }
}
