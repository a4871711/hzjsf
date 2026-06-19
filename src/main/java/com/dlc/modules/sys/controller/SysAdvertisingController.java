package com.dlc.modules.sys.controller;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.dlc.common.utils.*;
import com.dlc.modules.sys.entity.GoodsEntity;
import com.dlc.modules.sys.entity.SysAdvertisingEntity;
import com.dlc.modules.sys.service.GoodsService;
import com.dlc.modules.sys.service.SysAdvertisingService;
import net.sf.json.JSONObject;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;


/**
 * 广告表
 *
 * @author wangsheng
 * @email
 * @date 2018-09-12 16:11:35
 */
@RestController
@RequestMapping("sys/advertising")
public class SysAdvertisingController {
    @Autowired
    private SysAdvertisingService sysAdvertisingService;
    @Autowired
    private GoodsService goodsService;

    @Value("${project_url}")
    public String PROJECT_URL;
    private static final String IMG_PATH = "/statics/images";

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("sys:advertising:list")
    public R list(@RequestParam Map<String, Object> params) {
        Query query = new Query(params);
        List<SysAdvertisingEntity> advertisingList = sysAdvertisingService.queryList(query);
        int total = sysAdvertisingService.queryTotal(query);
        PageUtils pageUtils = new PageUtils(advertisingList, total, query.getLimit(), query.getPage());
        return R.ok().put("page", pageUtils).put("goodsList", sysAdvertisingService.queryGoodsList());
    }

    /**
     * 选择商品列表
     */
    @RequestMapping("/goodsSelectList")
    public R goodsList(@RequestParam Map<String, Object> params){
        //查询列表数据
        Query query = new Query(params);
        List<Map<String, Object>> goodsList = goodsService.queryGoodsSelectList(query);
        int total = goodsService.queryGoodsSelectTotal(query);
        PageUtils pageUtil = new PageUtils(goodsList, total, query.getLimit(), query.getPage());
        return R.ok().put("goodsPage", pageUtil);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{advid}")
    @RequiresPermissions("sys:advertising:info")
    public R info(@PathVariable("advid") Long advid) {
        SysAdvertisingEntity sysAdvertising = sysAdvertisingService.queryObject(advid);
        return R.ok().put("advertising", sysAdvertising);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    @RequiresPermissions("sys:advertising:save")
    public R save(@RequestBody SysAdvertisingEntity sysAdvertisingEntity) {
        sysAdvertisingEntity.setCreatedDate(new Date());
        sysAdvertisingService.save(sysAdvertisingEntity);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    @RequiresPermissions("sys:advertising:update")
    public R update(@RequestBody SysAdvertisingEntity sysAdvertisingEntity) {
        sysAdvertisingService.update(sysAdvertisingEntity);//全部更新

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    @RequiresPermissions("sys:advertising:delete")
    public R delete(@RequestBody Long[] advids) {
        sysAdvertisingService.deleteBatch(advids);
        return R.ok();
    }

    @RequestMapping(value = "/upload")
    public R upload(MultipartFile[] files, HttpServletRequest request) throws IOException {

        JSONObject obj = new JSONObject();
        List<String> imgPath = new ArrayList<>();

        String filePath = request.getServletContext().getRealPath(IMG_PATH);

        if (null != files && files.length > 0) {
            //循环获取file数组中得文件
            for (int i = 0; i < files.length; i++) {
                String fileName = String.valueOf(System.currentTimeMillis());
                MultipartFile file = files[i];
                if (file.getSize() > Constant.FILE_SIZE) {
                    return R.error("请上传10Mb内的图片");
                }
                //保存文件
                fileName = FileUploadUtils.fileUp(file, filePath, fileName);
                if (fileName != null && fileName != "") {


                    //sky暂时屏蔽图片裁剪功能，打包时老是报错
//                    ImageCompressUtil.zipImageFile(filePath + "/" + fileName, 690, 198, 1f, "x2");
                    File oldFile = new File(filePath + "/" + fileName);
                    if (null != oldFile && true == oldFile.isFile()) {
                        oldFile.delete();
                    }
                    fileName= fileName.replace(".","x2.");
                    String imagePath = PROJECT_URL + request.getContextPath() + IMG_PATH + "/" + fileName;

                    imgPath.add(imagePath);
                }
            }

        }
        obj.put("imgPath", imgPath);
        return R.reOk(obj);
    }
}
