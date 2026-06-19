package com.dlc.common.utils;

import com.dlc.common.constant.Constants;
import com.dlc.common.constant.ConstantsCommon;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

/**
 * 个联协议请求封装处理工具类
 * 
 * @author hl
 */
public class SendUtils {
    private final static Logger logger = LoggerFactory.getLogger(SendUtils.class);

    public static String send1202Data(String message, JSONArray imeis, String bodyData, String glUrl,
                                      String productId, String desKey) {
        logger.info("glUrl----->"+glUrl+"=====productId--->"+productId+"===desKey--->"+desKey);
        JSONObject headJson = new JSONObject();
        headJson.put(Constants.CODE, Constants._1202);
        //headJson.put(Constants.PRODUCT_ID, Constants.DEVICE_PRODUCT_ID);
        headJson.put(Constants.PRODUCT_ID, productId);
        headJson.put(Constants.MESSAGE, message);
        headJson.put(Constants.LENGTH, bodyData.length());

        headJson.put(Constants.IMEIS, imeis);
        logger.info("SendUtils--headJson--->"+headJson);
        logger.info("SendUtils--bodyJson--->"+bodyData);
        String resp = null;
        try {
            byte[] allData = getAllByte(headJson, bodyData, desKey);
            //resp = HttpUtils.postHttpOrHttps(Constants.GL_URL, allData, Constants.HTTP_TIMEOUT);
            resp = HttpUtils.postHttpOrHttps(glUrl, allData, Constants.HTTP_TIMEOUT);
        }
        catch (Exception e){
            // TODO 超时、IO等异常，如需重试，自行添加重试逻辑
            logger.info("发送1202协议接口报错：",e);
        }
        return resp;
    }

    public static String send1204Data(String message, JSONArray mobileIds, String bodyData, String glUrl,
                                      String productId, String desKey)
            throws UnsupportedEncodingException {
        logger.info("glUrl----->"+glUrl+"=====productId--->"+productId+"===desKey--->"+desKey);
        JSONObject headJson = new JSONObject();
        headJson.put(Constants.CODE, 1204);
        //headJson.put(Constants.PRODUCT_ID, Constants.DEVICE_PRODUCT_ID);
        headJson.put(Constants.PRODUCT_ID, productId);
        headJson.put(Constants.MESSAGE, message);
        headJson.put(Constants.LENGTH, bodyData.length());

        headJson.put(Constants.MOBILE_IDS, mobileIds);

        byte[] allData = getAllByte(headJson, bodyData, desKey);
        String resp = null;
        try {
            //resp = HttpUtils.postHttpOrHttps(Constants.GL_URL, allData, Constants.HTTP_TIMEOUT);
            resp = HttpUtils.postHttpOrHttps(glUrl, allData, Constants.HTTP_TIMEOUT);
        }
        catch (Exception e) {
            // TODO 超时、IO等异常，如需重试，自行添加重试逻辑
            e.printStackTrace();
        }
        return resp;
    }

    public static String send1205Data(String message, String imei, String glUrl, String productId) {
        logger.info("send1205Data--->glUrl----->"+glUrl+"=====productId--->"+productId);
        JSONObject headJson = new JSONObject();
        headJson.put(Constants.CODE, 1205);
        //headJson.put(Constants.PRODUCT_ID, Constants.DEVICE_PRODUCT_ID);
        headJson.put(Constants.PRODUCT_ID, productId);
        headJson.put(Constants.MESSAGE, message);
        headJson.put(Constants.LENGTH, "0");

        headJson.put(Constants.IMEI, imei);

        String resp = null;
        try {
            //resp = HttpUtils.postHttpOrHttps(Constants.GL_URL,
                    //headJson.toString().getBytes(Constants.UTF_8), Constants.HTTP_TIMEOUT);
            resp = HttpUtils.postHttpOrHttps(glUrl,
                    headJson.toString().getBytes(Constants.UTF_8), Constants.HTTP_TIMEOUT);
        }
        catch (Exception e) {
            // TODO 超时、IO等异常，如需重试，自行添加重试逻辑
            e.printStackTrace();
        }
        return resp;
    }

    public static byte[] getAllByte(JSONObject headJson, String bodyData, String desKey)
            throws UnsupportedEncodingException {
        //byte[] bodyByte = DESUtils.encrypt(bodyData.getBytes(Constants.UTF_8), Constants.DES_KEY);
        byte[] bodyByte = DESUtils.encrypt(bodyData.getBytes(Constants.UTF_8), desKey);
        int headLength = (headJson.toString() + Constants.DELIMITER).length();
        byte[] allData = new byte[bodyByte.length + headLength];
        System.arraycopy((headJson.toString() + Constants.DELIMITER).getBytes(Constants.UTF_8), 0,
                allData, 0, headLength);
        System.arraycopy(bodyByte, 0, allData, headLength, bodyByte.length);
        return allData;
    }
    
//    public static void main(String[] args) {
//        String glUrl = "";
//        String productId = "";
//        String imei = "zr5a24cae957393";
//        JSONArray imeis = new JSONArray();
//        imeis.add(imei);
//        JSONObject bodyData = JSONObject.fromObject("{'test':'test111234','adfdo':'2254545'}");
//        String strData = bodyData.toString();
//        System.out.println("strData--->"+strData.length());
//
//        String str = SendUtils.send1202Data("testmessage", imeis, strData, glUrl, productId);
//        System.out.println("str==="+str);
//
//
//    }

}
