package com.dlc.modules.sys.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.dlc.common.utils.*;
import com.dlc.modules.api.entity.GymType;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dlc.modules.sys.entity.GymEngineEntity;
import com.dlc.modules.sys.service.SysGymEngineService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * 机械表
 *
 * @author wangsheng
 * @email
 * @date 2018-09-11 09:17:04
 */
@RestController
@RequestMapping("sys/gymengine")
public class SysGymEngineController {
    @Autowired
    private SysGymEngineService gymEngineService;
    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private static final String IMG_PATH = "/statics/images";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:gymengine:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<GymEngineEntity> gymEngineList = gymEngineService.queryList(query);
        int total = gymEngineService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(gymEngineList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtils).put("gymType", gymEngineService.getGymTypeList());
    }
    /**
     * 列表
     */
    @RequestMapping("/getGymTypeList")
    @RequiresPermissions("sys:gymengine:list")
    public R getGymTypeList(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<GymType> gymEngineList = gymEngineService.queryGymTypeList(query);
        int total = gymEngineService.queryGymTypeTotal(query);
        PageUtils pageUtils = new PageUtils(gymEngineList, total, query.getLimit(), query.getPage());
        return R.ok().put("gymPage", pageUtils).put("gymTypeList", gymEngineService.getGymTypeList());
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{gymengineid}")
    @RequiresPermissions("sys:gymengine:info")
    public R info(@PathVariable("gymengineid") Long gymengineid) {
        GymEngineEntity gymEngine = gymEngineService.queryObject(gymengineid);

        return R.ok().put("gymEngine", gymEngine);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:gymengine:save")
    public R save(@RequestBody GymEngineEntity gymEngine,HttpServletRequest request) {
        gymEngine.setCreatedDate(new Date());
        gymEngineService.save(gymEngine);
        return R.ok();
    }
    /**
     * 保存type
     */
    @RequestMapping("/saveGymType")
    @RequiresPermissions("sys:gymengine:save")
    public R saveGymType(@RequestBody GymType gymType, HttpServletRequest request) {
        gymType.setCreatedDate(new Date());
        gymEngineService.saveGymType(gymType);
        return R.ok();
    }
    /**
     * 修改type
     */
    @RequestMapping("/updateGymType")
    @RequiresPermissions("sys:gymengine:update")
    public R updateGymType(@RequestBody  GymType gymType,HttpServletRequest request) {
        gymType.setCreatedDate(new Date());
        gymEngineService.updateGymType(gymType);//全部更新
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:gymengine:update")
    public R update(@RequestBody GymEngineEntity gymEngine,HttpServletRequest request) {
        //String videoImgUrl= IMG_PATH+ "/"+System.currentTimeMillis()+".jpg";
        gymEngine.setCreatedDate(new Date());
        gymEngineService.update(gymEngine);//全部更新
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:gymengine:delete")
    public R delete(@RequestBody Long[] gymengineids) {
        gymEngineService.deleteBatch(gymengineids);
        return R.ok();
    }
    /**
     * 删除
     */
    @RequestMapping("/deleteGt")
    @RequiresPermissions("sys:gymengine:delete")
    public R deleteGt(@RequestBody Long gtId) {
        gymEngineService.deleteGymType(gtId);
        return R.ok();
    }

    /**
     * 上传视频
     * @param files
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/upload")
    public R upload(MultipartFile files, HttpServletRequest request) throws IOException {

        JSONObject obj = new JSONObject();
        List<String> imgPath = new ArrayList<>();

        String filePath = request.getServletContext().getRealPath(IMG_PATH);

        if (null != files ) {

                String fileName = String.valueOf(System.currentTimeMillis());
                MultipartFile file = files;
                //保存文件
                fileName = FileUploadUtils.fileUp(file, filePath, fileName);
                if(fileName != null && fileName != ""){
                    String imagePath = request.getContextPath() + IMG_PATH+ "/" + fileName;
                    log.info(imagePath);
                    imgPath.add(imagePath);
                }


        }
        System.out.println(imgPath+"=======");
        obj.put("imgPath", imgPath);
        return R.reOk(obj);
    }
}
