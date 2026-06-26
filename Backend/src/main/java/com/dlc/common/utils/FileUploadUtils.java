package com.dlc.common.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyuexin
 * @version 1.0
 * @date 2018-04-28 17:06
 */
public class FileUploadUtils {
    /**
     * @param file     //文件对象
     * @param filePath //上传路径
     * @param fileName //文件名
     * @return 文件名
     */

    public static String fileUp(MultipartFile file, String filePath, String fileName) {
        String extName = ""; // 扩展名格式：
        try {
            if (file.getOriginalFilename().lastIndexOf(".") >= 0) {
                extName = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            }
            copyFile(file.getInputStream(), filePath, fileName + extName).replaceAll("-", "");
        } catch (IOException e) {
            System.out.println(e);
        }
        return fileName + extName;
    }

    /**
     * 写文件到当前目录的upload目录中
     * @param in
     * @param
     * @throws IOException
     */
    private static String copyFile(InputStream in, String dir, String realName)
            throws IOException {
        File file = new File(dir, realName);
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        }
        FileUtils.copyInputStreamToFile(in, file);
        return dir + realName;
    }



    /**
     * 文件存放目录
     */
    private static String FILEPATH;


    public static void setFilePath(String filePath){
        FILEPATH=filePath;
    }

    /**
     * @param files 文件组
     * @param request
     * @param dir 指定文件目录(不要带/) 例如:  img
     * @param type 1路径全域名(http://aaa.com/**)，2路径不带域名(/img/123456.jpg)
     * @return JSONObject()
     * @throws IOException
     */
    public static JSONObject upload(MultipartFile[] files, HttpServletRequest request,String dir,int type) throws IOException {

        JSONObject jsonObject = new JSONObject();
        List<String> imgPath = new ArrayList<>();
        /*String filePath = FILEPATH +"/"+dir;*/
        String filePath = FILEPATH;
        String imagePath = "";
        if (files != null && files.length > 0) {
            File uploadPath = new File(filePath);
            //如果文件不存在就创建
            try {
                if (!uploadPath.isDirectory()) {
                    uploadPath.mkdirs();
                }
            } catch (Exception e) {

            }
            for (int i = 0; i < files.length; i++) {
                String fileName = String.valueOf(System.currentTimeMillis());
                MultipartFile file = files[i];
                fileName = fileUp(file, filePath, fileName);
                System.out.println("=====================上传文件的地址====================="+filePath);

                if (fileName != null && fileName != "") {
                    if(type==1){
                        /*imagePath = "http://"+request.getServerName()+"/"+dir+"/"+ fileName;*/
                        String serverName=request.getServerName();
                        if ( "localhost".equals(serverName)) {
                            serverName+=":8080";
                        }
                        imagePath = "http://" + serverName + request.getContextPath() + "/statics/images" + "/" + fileName;
                        imgPath.add(imagePath);
                    }else{
                        /*imagePath="/"+dir+"/"+fileName;*/
                        imagePath = "/"+fileName;
                        imgPath.add("/statics/images" + imagePath);
                    }
                }
                System.out.println("=====================上传文件的地址=====================");
            }
        }
        jsonObject.put("path", imgPath);
        return  jsonObject;
    }
}
