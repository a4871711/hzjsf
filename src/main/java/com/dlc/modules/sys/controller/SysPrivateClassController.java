package com.dlc.modules.sys.controller;

import com.dlc.common.utils.PageUtils;
import com.dlc.common.utils.Query;
import com.dlc.common.utils.R;
import com.dlc.common.validator.ValidatorUtils;
import com.dlc.modules.sys.entity.SysGradePriceShipEntity;
import com.dlc.modules.sys.entity.SysPrivateClassEntity;
import com.dlc.modules.sys.service.SysGradePriceShipService;
import com.dlc.modules.sys.service.SysPrivateClassService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/sys/private")
public class SysPrivateClassController {
    @Autowired
    private SysPrivateClassService sysPrivateClassService;
    @Autowired
    private SysGradePriceShipService sysGradePriceShipService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:private:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysPrivateClassEntity> sysPrivateClassEntities = sysPrivateClassService.queryList(query);
        int total = sysPrivateClassService.queryTotal(query);
        PageUtils pageUtils=new PageUtils(sysPrivateClassEntities,total,query.getLimit(),query.getPage());
        return R.ok().put("pages", pageUtils).put("classType",sysPrivateClassService.queryClassType());
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{activityTrainId}")
    @RequiresPermissions("sys:private:info")
    public R info(@PathVariable("activityTrainId") Long privateClassId) {
        SysPrivateClassEntity sysPrivateClassEntity = sysPrivateClassService.queryObject(privateClassId);
        return R.ok().put("sysPrivateClassEntity", sysPrivateClassEntity);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:private:save")
    public R save(@RequestBody SysPrivateClassEntity sysPrivateClassEntity,Long dengji,BigDecimal price) {
        sysPrivateClassEntity.setCreatedDate(new Date());
        //非空验证
        ValidatorUtils.validateEntity(sysPrivateClassEntity);
        sysPrivateClassService.save(sysPrivateClassEntity);
        SysPrivateClassEntity entity= sysPrivateClassService.queryLast(sysPrivateClassEntity);
        sysGradePriceShipService.savap(entity.getPrivateClassId(),dengji,price);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:private:update")
    public R update(@RequestBody SysPrivateClassEntity sysPrivateClassEntity,Long dengji,BigDecimal price) {
        ValidatorUtils.validateEntity(sysPrivateClassEntity);
        SysGradePriceShipEntity shipEntity=new SysGradePriceShipEntity();
        shipEntity.setPrivateClassId(sysPrivateClassEntity.getPrivateClassId());
        shipEntity.setGrade(dengji);
        shipEntity.setPrice(price);
        sysGradePriceShipService.update(shipEntity);//全部更新
        sysPrivateClassService.update(sysPrivateClassEntity);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:private:delete")
    public R delete(@RequestBody Long[] privateClassIds) {
        int res = sysPrivateClassService.deleteBatch(privateClassIds);
        if(res > 0){
            return R.ok();
        }
        return R.error("失败");
    }
    /**
     * 上传图片
     *
     * @param file
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload")
    public R upload(@RequestParam("file")MultipartFile file , HttpServletRequest request) throws IOException {
        String fileName = file.getOriginalFilename();
        String path=request.getSession().getServletContext().getRealPath("images/");
        File tempFile = new File(path, new Date().getTime() + String.valueOf(fileName));
        if (!tempFile.getParentFile().exists()) {
            tempFile.getParentFile().mkdir();
        }
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        file.transferTo(tempFile);
//        //绝对路径
//        return R.ok("http://"+request.getServerName()+":"+request.getServerPort()+"/images/" + tempFile.getName());
        //相对路径
        return R.ok("/images/" + tempFile.getName());
    }
    /**
     * 查询规则等级
     */
    @RequestMapping("/gradeRule")
    public R gradeRule(@RequestParam("money") Double money) {
        Integer grade = sysGradePriceShipService.getGradeRule(money);
        return R.ok().put("gradeRu", grade);
    }
}
