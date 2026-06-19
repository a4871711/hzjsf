package com.dlc.modules.qd.utils;

import com.dlc.common.utils.ConfigConstant;
import com.github.wxpay.sdk.WXPayConfig;
import org.apache.poi.hssf.record.pivottable.StreamIDRecord;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class MyConfig implements WXPayConfig {

    private byte[] certData;

    public MyConfig() throws Exception {
        URL resource = MyConfig.class.getClassLoader().getResource("test/apiclient_cert.p12");
//        String certPath = "/javadata/DGJAVA/tomcat/cert/gxzjj/test/apiclient_cert.p12";
        String path = resource.getPath();
        File file = new File(path);
        InputStream certStream = new FileInputStream(file);
        this.certData = new byte[(int) file.length()];
        certStream.read(this.certData);
        certStream.close();
    }

    /**
     * 支付宝支付相关信息
     */
    public static final String ZFB_NOTIFYURL = "http://shilijsf.shilisports.com/api/pay/zfbNotify";
    public static final String ZFB_INMONEY_NOTIFYURL = "http://shilijsf.shilisports.com/api/walletPay/zfbNotify";
    public static final String ZFB_APPID = "2018082061034850";
    public static final String ZFB_PRIVATE_KEY = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCNCPflalx0MJgB28L+h1WEwk4fHsow9MrCZmcidYJ8dK1ZnLGRKTEvp8Kt5fuQHRThYvJmyIW68CE0bHoj8ElWMmaigzwQcDSsVqzAyPVfaXu/wj3tPnq4HhmiuyanC4V9VlAKMctufzbBjqM8/knX9EREbj3JvSCxWYWIzWgH9zk8KZVg7keKRW+w2VVgRD4EDRgljjw3TuCHwpR4T6i2pc27Tq7Pd76MkinFnFsjYjgel8MCf/9wRSeQIg6njmc4xHpzX403p0irJfYOSOd3pp9oNTgMB7CS3vd3TU8gS89MSbwR2IpWWkeFltY0O3CYbuI08+ekGJn4QB4D/Ds3AgMBAAECggEAKZdue8J9F2rJOSsU+zjv+GHQ5g6PdBaMwP2wBpJ+87L4+EdkejmansIrgFslb9+Tq9AhSjxya6mrMSmYb4bKKdhdOinXicV9UyGQep4I9qVF8EwZNSDK0gdWag4lHPgh2s+cdRUwOBreDADDlRYssrn/c+rK3/adcI/E+I4S0t4drArkTgd0aXUZAuu68+SB95nKW09UwVMcY7de5iOBx8nwRKS/ozBSYGAgUln9cu0634APMy2Eek4tRV7ULwT2Z39AQLlQi9fQeQ97YXrn0aO2V3ScB/IcLOVyf2f5BzYokkXqlzzkTTI6krxIzbeFbrKh2E7498ZYLoZMEwoa0QKBgQDWJiwfysD95Sc2SoFnj9MqiGs9jPHwL8Lw4M1V1fkbo5NZEo7Rgl5fdUH6N/INsYAu0sY0ZqYZhFrVSz8NxqIeKoRi9DG+v4s4VfS7NDAfW3nkCqaUl0CKfFVMilYjF8uQMwJrzimz5XIe8VKfsPC/4KV9tsrMCQ7I1hsmM+teHwKBgQComOqh7zD6NipI7GuIx+fF2S09OPDbVudLr4tdDYRefEKTnrWAqVRfpsH4ceKFHiJwjNRXtyQeUvwdsIiKhFH6+rRJWqBg3zxrPWPtvGIDZ2dD1XioS/dLnPJzB9kcmXkOku8csl78Pj+LJ36XyhX4agHJHw8JXp7YVz16WEhP6QKBgQDPGCc/r4Inhr3/ANpX3nzl+vtKkM0/69Bnxd6NDdpT4kKStyGqdprxUaI2angIygkePvMypbonnxmAf75AgkExTwir8V9SCkaGeomrqHKJQJuF7s7dj8/4nPiYhod6dH6Fuu6ydrNZp6BPUC0JBcIsrQEdq/I+ktvbWUL0FO2fHQKBgDkvbN4yOEWWG6+SaUMCsy2bZUnPx3pONrOoPjf4vr4hnZ9ehKe4R4MOF9rCHhgSyZGtYtTBrMGQWi1j1FxPV1Mut/ntgFp7PY4OS5e2fvQynYcXoA5NffqqqF21+sFFmRcFZY8xPSCHqJv1cG3hwKP6qNJj84C1FGHC7XpS+WwJAoGBAKTFgwC3NmSAF0UdX6BGoPyBUUm8z+CAf1CHDpXL5mpd4y7FJy5ZOY7gu4PhjjiKY6NQT/M9SxAZIEl6gurjvdehRLIK7+RTLEY4Jq87lKlRkBiHo+LxP8TZqWPbo1iP6hX1vIqzve5Mto4MDVjIvI2S2HM74aPw0cOn1weVEGQP";
    public static final String ZFB_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAjcCx8gRbqgj+2IuemvLlZ2pPkauf1t8XsP3rs1uO9eIaTGHebTyNMlCao85b2AVgNJI5dl06hUUQ9Gv8C4Ea7Cg55kTHnf4it+e3yObjyOOuzxsuZK84epqKKT/qrdWwR4gysrIsxTsLgq6TGx5fLju6rM84lxd3X+OF0gJlsOZ9CxYdvF42F3N7Gf+pTMJb9pXFq1xlOrB9OM4md9hq8PZB8R8Udu4AOaLAqMupRbY+ytlDk0ThfQ1DD2PEKuTK4SxSDocXjv1sxIsd8vAakgiW927IoMp0BVU2MnGsd0AVoNLamkG/xCdjtUe2esozsYBwTLsJGtpIo11Y4GCykQIDAQAB";
    public static final String ZFB_SIGN_TYPE = "RSA2";
    public static final String ZFB_GATEWAY = "https://openapi.alipay.com/gateway.do";
    public static final String ZFB_CHARSET = "utf-8";
    public static final String ZFB_OBJECT = "json";
    public static final String ZFB_PID = "2088231210563821";

    //微信开放平台
    public String getAppID() {
        return "wxb6dd809831452549";
    }
    //公众号平台
    public String getGzhAppID() {
        return "wxfc03306f16362704";
    }
    
  //微信小程序
    public String getProAppID() {
        return ConfigConstant.PRO_WECHAT_APPID;
    }
    
  //微信小程序
    public String getProAppSecret() {
        return ConfigConstant.PRO_WECHAT_APPSECRET;
    }

    //商户号
    public String getMchID() {
        return "1646899496";
    }

    //充值reappid
    public String getReAppId() {
        return "";
    }
    //充值reMchId
    public String getReMchId() {
        return "";
    }
    //微信充值商户号
    public String getAppSecret() {
        return "ff4ad662075e3c36eec6ce90fb131a01";
    }
    //公众号平台
    public String getGzhAppSecret() {
        return "45a09c554e3c83a5108ba4455e6450bd";
    }
    public String getKey() {
        return "13467468162164598496132134676945";
    }

        public InputStream getCertStream() {
        ByteArrayInputStream certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }

    public int getHttpConnectTimeoutMs() {
        return 8000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    /**
     * 微信支付接口地址
     */
    //微信支付统一接口(POST)
    public final static String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    //微信退款接口(POST)
    public final static String REFUND_URL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    //订单查询接口(POST)
    public final static String CHECK_ORDER_URL = "https://api.mch.weixin.qq.com/pay/orderquery";
    //关闭订单接口(POST)
    public final static String CLOSE_ORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";
    //退款查询接口(POST)
    public final static String CHECK_REFUND_URL = "https://api.mch.weixin.qq.com/pay/refundquery";
    //对账单接口(POST)
    public final static String DOWNLOAD_BILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";
    //短链接转换接口(POST)
    public final static String SHORT_URL = "https://api.mch.weixin.qq.com/tools/shorturl";
    //接口调用上报接口(POST)
    public final static String REPORT_URL = "https://api.mch.weixin.qq.com/payitil/report";
    //微信公众号支付获取openId
    public final static String oauth_url = "https://api.weixin.qq.com/sns/oauth/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";

}