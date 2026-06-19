/**
 * Copyright (C), 2015-2018, XXX有限公司
 * FileName: HttpClientUtils
 * Author:   Administrator
 * Date:     2018/5/28 9:21
 * Description: httpClient请求工具类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.modules.qd.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author chenyuexin
 * @date 2018-05-28 09:21
 * @version 1.0
 */
public class HttpClientUtils {
    private final static Logger log = LoggerFactory.getLogger(HttpClientUtils.class);
    /**
     * 发送HttpPost请求
     *
     * @param strURL
     *            服务地址
     * @param params
     *
     * @return 成功:返回json字符串<br/>
     */
    public static String jsonPost(String strURL, Map<String, String> params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "application/json"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
//            out.append(params);
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }

        } catch (IOException e) {
            log.error("Exception occur when send http post request!", e);
        }
        return "error"; // 自定义错误信息
    }


    public static String post(String strURL, Map<String, String> params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "*/*"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); // 设置发送数据的格式
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8"); // utf-8编码
//            out.append(params);

            String param = "";
            if (params != null && params.size() > 0) {
                Iterator<String> ite = params.keySet().iterator();
                while (ite.hasNext()) {
                    String key = ite.next();// key
                    String value = (String) params.get(key);
                    param += key + "=" + value + "&";
                }
                param = param.substring(0, param.length() - 1);
            }
            out.append(param);

            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            if (length != -1) {
                byte[] data = new byte[length];
                byte[] temp = new byte[512];
                int readLen = 0;
                int destPos = 0;
                while ((readLen = is.read(temp)) > 0) {
                    System.arraycopy(temp, 0, data, destPos, readLen);
                    destPos += readLen;
                }
                String result = new String(data, "UTF-8"); // utf-8编码
                return result;
            }

        } catch (IOException e) {
            log.error("Exception occur when send http post request!", e);
        }
        return "error"; // 自定义错误信息
    }

    public static String postJson(String strURL, String params) {
        try {
            URL url = new URL(strURL);// 创建连接
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("POST"); // 设置请求方式
            connection.setRequestProperty("Accept", "*/*"); // 设置接收数据的格式
            connection.setRequestProperty("Content-Type", "application/json"); // 设置发送数据的格式
            connection.connect();
            OutputStream out = connection.getOutputStream(); // utf-8编码
            out.write(params.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();

            int code = connection.getResponseCode();
            InputStream is = null;
            if (code == 200 || code == 201) {
                is = connection.getInputStream();
            } else {
                is = connection.getErrorStream();
            }

            // 读取响应
            int length = (int) connection.getContentLength();// 获取长度
            byte[] buffer = new byte[length];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (int len = 0; (len = is.read(buffer)) > 0; ) {
                baos.write(buffer, 0, len);
            }
            String returnValue = new String(baos.toByteArray(), "utf-8");
            System.out.println("result=" + returnValue);
            return returnValue;
        } catch (IOException e) {
            System.out.println("Exception occur when send http post request!" + e);
        }
//        return "error"; // 自定义错误信息
        Map returnMap = new HashMap<>();
        returnMap.put("code", "0");
        returnMap.put("error_msg", "Success");
        return JSONObject.toJSONString(returnMap);
    }



    public static void main(String[] args) {
        String url = "http://api.rpro3.visbody.com/v1/dataBind";
        Map<String, String> map = new HashMap<>();

        map.put("scan_id", "1");
        map.put("device_id", "2");
        map.put("third_uid", "3616");
        map.put("mobile", "13412767153" );
        map.put("sex", "1" );
        map.put("height", "160" );

        map.put("birthday", "1986-04-01" );
        map.put("token", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2ZklkIjoidmZ5MUVrRVBzdXAwNWVMQiIsInZmU2VjcmV0IjoiQVBjUHlGZFRSbVk5aEx1VDdKZkdaWFJPaEVudEZRUEwiLCJpYXQiOjE2NTIzMjI5MjgsImV4cCI6MTY1MjMyNjUyOH0.p0egnbSwtphHxiYb7xAX92XHGx9Rvh8cvovicd5ONqU" );


        String result = post(url, map);
        System.out.println("result =="+result);
    }

}
