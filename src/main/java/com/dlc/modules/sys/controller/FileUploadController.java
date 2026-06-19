package com.dlc.modules.sys.controller;

import com.dlc.common.constant.ConstantProperty;
import com.dlc.common.utils.ConfigConstant;
import com.dlc.common.utils.Constant;
import com.dlc.common.utils.FileUploadUtils;
import com.dlc.common.utils.R;
import net.coobird.thumbnailator.Thumbnails;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-05-18 16:05
 */

@RestController("sysUploadController")
@RequestMapping("/sys")
public class FileUploadController {
    private final Logger log = LoggerFactory.getLogger(FileUploadController.class);

    private static final String IMG_PATH = "/statics/images";

    /**
     * 项目域名
     */

    @Value("${project_url}")
    public String PROJECT_URL;

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

        String widthStr = request.getParameter("width");
        String heightStr = request.getParameter("height");
        if (widthStr != null) {
            Integer width=Integer.valueOf(widthStr);
            Integer height=Integer.valueOf(heightStr);
            BufferedImage image= ImageIO.read(files[0].getInputStream());
            if (image.getWidth()>width||image.getHeight()>height){
                return R.error("请上传" + widthStr + "像素(宽) X " + heightStr + "像素（高）范围内的图片");
            }
        }
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
//                     String imagePath = request.getContextPath() + IMG_PATH+ "/" + fileName;
                    String imagePath = PROJECT_URL + request.getContextPath() + IMG_PATH + "/" + fileName;
                    /*Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(filePath+"/"+fileName);//压缩*/
               /*             String video = fileName.substring(fileName.length()-3,fileName.length());
                    System.out.println(video);
                    if(!video.equals("mp4")){
                        try {
                            Thumbnails.of(file.getInputStream()).scale(1f).outputQuality(0.25f).toFile(filePath+"/"+fileName);//压缩
                        } catch (IOException e) {
                            return R.error("不支持此文件上传");
                        }
                    }*/

                    log.info(imagePath);
                    imgPath.add(imagePath);
                }
            }

        }
        obj.put("imgPath", imgPath);
        return R.reOk(obj);
    }

    @RequestMapping(value = "/image/del")
    public R deleteImg(HttpServletRequest request) {
        //删除原来的图片
        String oldImgUrl = request.getParameter("oldImgUrl");
        String filePath = request.getServletContext().getRealPath("");
        if (StringUtils.isNotBlank(oldImgUrl)) {
            File oldFile = new File(filePath, oldImgUrl);
            if (null != oldFile && true == oldFile.isFile()) {
                oldFile.delete();
            }
        }
        return R.reOk();
    }

//    private String filePath = "E:\\project\\zljd\\target\\zljd\\statics\\images";

    @RequestMapping("/uploads")
    public R uploads(@RequestParam("file") MultipartFile[] files, HttpServletRequest request) throws IOException {

        String widthStr = request.getParameter("width");
        String heightStr = request.getParameter("height");
        if (widthStr != null) {
            ;
            Integer width=Integer.valueOf(widthStr);
            Integer height=Integer.valueOf(heightStr);
            BufferedImage image= ImageIO.read(files[0].getInputStream());
            if (image.getWidth()>width||image.getHeight()>height){
                return R.error("请上传" + widthStr + "像素(宽) X " + heightStr + "像素（高）范围内的图片");
            }
        }
        String dirPath = "logo";
//        log.info("上传路径为:" + filePath + dirPath);
        String filePath = request.getServletContext().getRealPath(IMG_PATH);
        FileUploadUtils.setFilePath(filePath);
        com.alibaba.fastjson.JSONObject jsonObject = null;
        try {
            jsonObject = FileUploadUtils.upload(files, request, dirPath, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.ok(jsonObject);
    }
}
