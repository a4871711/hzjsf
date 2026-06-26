package com.dlc.common.constant;

/**
 * 静态变量及配置信息
 * 
 * @author hl
 */
public interface Constants {

    String DELIMITER = "\0";
    String PRODUCT_ID = "product_id";
    String IMEI = "imei";
    String CODE = "code";
    String MESSAGE = "message";
    String RESULT = "result";
    String LENGTH = "length";
    String IMEIS = "imeis";
    String MOBILE_IDS = "mobile_ids";

    String UTF_8 = "UTF-8";
    String POST = "POST";
    String APPLICATION_JSON = "application/json";
    int HTTP_TIMEOUT = 5000;
    /**
     * 以下信息需服务端提供并修改
     */
    //String IEMI_VALUE = "ic5b47124a95eb9";
    //String DES_KEY = "dierxigx";// des加解密密码
    //String DEVICE_PRODUCT_ID = "40001040010016";// 产品id
    //String GL_URL = "http://test.igelian.com:9111/mpfesHttp/s/d";// 个联服务接口地址
    String WHITELIST = "*";// 个联服务器ip白名单


    String CBUY = "cbuy";
    String LOCK = "lock";
    String ACT = "act";
    String DEV_STA = "devsta";
    String SERIAL = "serial";
    String IID = "iid";
    String NUM = "num";
    String TSP = "tsp";
    String VALUE = "value";

    int _1202 = 1202;
    int _1204 = 1204;
    int _1205 = 1205;

    int OPEN_LOCK = 1;
    int CLOSE_LOCK = 0;

    //发布订阅频道  提现频道
    String CHANNEL = "withdraw";

}
