package com.dlc.modules.api.controller;

import com.dlc.common.utils.*;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-18 16:05
 */

@RestController
@RequestMapping("/api")
public class FileUploadController {
    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private static final String IMG_PATH = "/statics/images";

    /**
     * @param files
     * @param request
     * @return
     * @throws IOException
     * @api {POST} /fileUpload/upload  上传图片接口
     * @apiGroup common
     * @apiPermission Chenyuexin
     * @apiParam {File[]} files 图片数组,注意input文件框的name属性必须为 files,否则上传失败
     * @apiSuccessExample {json} 成功的响应
     * HTTP/1.1 200 OK
     * {
     * "code":"1",
     * "msg":"成功",
     * "data":{
     * "imgPath": [
     * "/statics/images/xxxxxxx.png"
     * ]
     * }
     * }
     */
    @RequestMapping(value = "/upload")
    public R upload(MultipartFile[] files, HttpServletRequest request) throws IOException {

        JSONObject obj = new JSONObject();
        List<String> imgPath = new ArrayList<>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String timePath = sdf.format(date);

        String filePath = request.getServletContext().getRealPath(IMG_PATH) + "/" + timePath;
        log.info("filePath----->" + filePath);
        String imagePath = "";
        if (files != null && files.length > 0) {

            //循环获取file数组中得文件
            for (int i = 0; i < files.length; i++) {
                String fileName = String.valueOf(System.currentTimeMillis()) + i;
                MultipartFile file = files[i];
                log.info("file.size==" + file.getSize());
                if (file.getSize() > Constant.FILE_SIZE) {
                    return R.reError("请上传10Mb内的图片");
                }
                //保存文件
                fileName = FileUploadUtils.fileUp(file, filePath, fileName);

                if (fileName != null && fileName != "") {
                    imagePath = imagePath + ConfigConstant.PRO_VER_URL + request.getContextPath() + IMG_PATH + "/" + timePath + "/" + fileName;
                    if (i == files.length - 1) {
                        imagePath = imagePath + "";
                    } else {
                        imagePath = imagePath + ",";
                    }
                  /*  try {
                        Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(filePath + "/" + fileName);//压缩
                    } catch (IOException e) {
                        return R.error("不支持此文件上传");
                    }*/
                    log.info(imagePath);
                    imgPath.add(imagePath);
                }
            }
        }
        // obj.put("imgPath", ConfigConstant.PRO_VER_URL + imgPath.get(0).toString());
        log.info("imagePath:*******" + imagePath);
        obj.put("imgPath", imagePath);
        return R.reOk(obj);
    }

}
