package com.dlc.common.utils;


/**
 * 系统参数相关Key
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-26 10:33
 */

public class ConfigConstant {
    /**
     * 云存储配置KEY
     */
    public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";

    /** 微信公众号 APPID */
    public static final String GZH_WECHAT_APPID = "wxfc03306f16362704";
    /** 微信公众号 APPSECRET */
    public static final String GZH_WECHAT_APPSECRET = "45a09c554e3c83a5108ba4455e6450bd";
    /** 助通短信短信地址（已启用，改用阿里云） */
    //public static final String ZT_PHONE_VER_URL = "http://api.zthysms.com/sendSms.do";
    public static final String ZT_PHONE_VER_URL = "http://hy.mix2.zthysms.com/sendSms.do";
    /** 助通用户名 */
    public static final String ZT_USERNAME = "xhz888hy";
    /** 助通用户密码 */
    public static final String ZT_PASSWORD = "q4iqUZ";


    /** 域名 */
    public static final String PRO_VER_URL = "http://shilijsf.shilisports.com";
   /* *//**打开婴儿车监控地址*//*
    public static final String OPEN_BABY_CAR_ULR = "http://120.77.72.190/babycar/open";
    *//**打开婴儿车音乐播放地址*//*
    public static final String OPEN_MUSIC_ULR = "http://120.77.72.190/babycar/mp3play";*/
    /**迪尔西公众号链接*/
    public static final String QRCODE_URL = "http://weixin.qq.com/r/DTkQCODEjb3yraRr92wf";
    /**S商城微信支付回调链接*/
    public static final String WX_SMALL = "http://shilijsf.shilisports.com/api/goodsOrder/wxOrderPayBck";
    /**S商城支付宝支付回调链接*/
    public static final String ZFB_SMALL = "http://shilijsf.shilisports.com/api/goodsOrder/zxbOrderPayBck";
    /**
     *  登录用户
     */
    public static final String ACCOUNT = "ACCOUNT";

    /**用户手机验证码前缀*/
    public static final String PHONE = "hzjsf_phone_";

    /**二维码生成前缀*/
    public static final String QRCODE_PRF = "hzjsf_";

    /**
     *  工作用户
     */
    public static final String ENTERPRISE = "ENTERPRISE";
    /**
     *  环信API  URL
     */
    public static final String HUANGXIN = "http://a1.easemob.com/";

    /**
     *  推荐标准  8人以上推荐
     */
    public static final int RECOMMENDEDSTANDARD = 8;

    /**
     * 未读消息
     */
    public static final int UN_READ = 0;
    /**
     * 系统消息
     */
    public static final int SYS_MESSAGE = 1;
    /**
     * 电子围栏消息
     */
    public static final Object ELECTRONIC_MESSAGE = 2;
    /**余额支付为1*/
    public static final Integer BLPAY = 1;
    /**微信支付为2*/
    public static final Integer WXPAY = 2;
    /**微信支付为自动续费13*/
    public static final Integer WXAUTOPAY = 13;
    /**支付宝支付为3*/
    public static final Integer ZFBPAY = 3;
    /**健身卡订单后缀*/
    public static final String CARD_ORDER_TYPE = "2";
    /**S商城订单后缀*/
    public static final String GOODS_ORDER_TYPE = "3";
    /**私教课订单后缀*/
    public static final String PRIVATE_CLASS_ORDER_TYPE = "4";
    /**团体课订单后缀*/
    public static final String TEAM_CLASS_ORDER_TYPE = "5";
    /**VIP权益卡购买订单后缀(income_pay_detail.payType自动=6)*/
    public static final String VIP_CARD_BUY_TYPE = "6";
    /**VIP转让费用订单后缀(income_pay_detail.payType自动=7)*/
    public static final String VIP_TRANSFER_FEE_TYPE = "7";
    /**VIP转让受让人确认时限(天):后台审核通过后受让人需在此天数内确认,超时由定时任务关单退费*/
    public static final int VIP_TRANSFER_CONFIRM_DAYS = 3;

    /* ============ 私教管理后台订单号末位后缀(全局仲裁,避开上面 2~7;新增任何支付链路前必回查本段) ============ */
    /**私教商品购买订单后缀(一次性/储值/分期首付主单;严禁复用"4"旧私教课,回调会错分)*/
    public static final String PT_PRIVATE_ORDER_TYPE = "b";
    /**会员储值充值订单后缀*/
    public static final String WALLET_RECHARGE_TYPE = "8";
    /**私教分期首付付款单后缀*/
    public static final String INSTALLMENT_DOWN_TYPE = "9";
    /**私教分期后续期付款单后缀*/
    public static final String INSTALLMENT_BILL_TYPE = "a";
    /**私教商品购买在income_pay_detail的支付用途(payType):后缀"b"非数字无法沿用Integer.valueOf自动映射,
     * 固定取14(constant表cId=1现占1~12,代码另占13自动续费,14未占用;展示名需在constant表配ckey=14)*/
    public static final int PT_PRIVATE_PAY_TYPE = 14;

    /**用户缓存前缀*/
    public static final String USER = "hzjsf_user_";
    /**用户缓存前缀*/
    public static final String DEVICE = "hzjsf_device_";
    public static final String DEVICE_SUB = "hzjsf_device_sub_";
    public static final String DEVICE_RAND = "hzjsf_device_rand_";
    /**商品缓存前缀*/
    public static final String GOODS = "hzjsf_goods_";
    //友盟+推送
    public static final String ANDROID_APPKEY = "5c89c0a461f56495d7000b4f";
    public static final String ANDROID_APP_MASTER_SECRET = "7jj7czljkuobzkoxcpngyyfmmlg4urfp";
    public static final String IOS_APPKEY = "5c8a09f961f56411b90003bf";
    public static final String IOS_APP_MASTER_SECRET = "yohd9yo4fxcxgxwd60iccndzknnknszd";
    public static final String IOS_USER_IDENTITY = "ios_user_id_";
    /**下面是手机短信发送配置*/
    public static final String ACTION = "send";
    public static final String ACCOUNT_NUM = "940006";
    public static final String PASSWORD = "MGwmFS";
    public static final String EXTNO = "10690458";
    public static final String RT = "json";

    //维塑-第三方接口
    public static final String VISID = "vf5bd4086794f7d";   //下个版本弃用，需根据门店获取设备相关参数
    public static final String SECRET = "54cbe0a17d501dd7bfcc0f4e112c5d96"; //下个版本弃用，需根据门店获取设备相关参数
    public static final String GET_INFO_URL = "https://api.visbodyfit.com:30000/v1/thirdsync/getinfo";  //获取配置接口
    public static String GETVIS_BODYSINFO_URL = "https://api.visbodyfit.com:30000/v1/bodys/info";   //获取体测成分数据（核心1）
    public static String GET_VISTOKEN="https://api.visbodyfit.com:30000/v1/token";  //获取接口凭证
    public static String GET_VISUSERID_URL="https://api.visbodyfit.com:30000/v1/vids";  //获取维塑用户标识id
    public static String VIS_DATABIND_URL = "https://api.visbodyfit.com:30000/v1/dataBind"; //用户信息绑定
    public static String MODELS_INFO_URL ="https://api.visbodyfit.com:30000/v1/models/info" ;   //获取体测模型文件
    public static String RANGE_INFO_URL="https://api.visbodyfit.com:30000/v1/bodys/range_info"; //获取体成分标准范围
    public static String BODY_DIMENSION_URL = "https://api.visbodyfit.com:30000/v1/datas/info"; //围度信息

    public static String GET_StATUS_VISTOKEN="https://api.visbodyfit.com:30001/v1/token";   //获取接口凭证（体态模型数据）
    public static String MODELS_BODYSTATUS_URL="https://api.visbodyfit.com:30001/v1/models/info";   //获取体态模型文件
    public static String BODY_STATUS_URL ="https://api.visbodyfit.com:30001/v1/bodyeval/datas/info"; //体态评估信息

    //维塑-api.rpro3.visbody.com相关接口
    public static final String REDIS_CMSPRO_TOKEN_KEY = "cmspro_token_";   //cmspro/3D体型追踪仪相关接口token
    public static final String REDIS_CMSPRO_STATUS_TOKEN_KEY = "cmspro_status_token_";   //cmspro/3D体型追踪仪相关接口token
    public static final String REDIS_RPRO3_TOKEN_KEY = "rpro3_token_";   //rpro3/3D智能体测精灵相关接口token
    public static final String REDIS_VISTOKEN_KEY = "toVisTokenV2";   //维塑访问我们接口时token

    public static final String REDIS_TOKEN_CHECK_KEY = "toRproToken";   //下个版本弃用，需根据门店获取设备相关参数
    public static final String REDIS_KEY_RPRO = "rpro3Token";   //下个版本弃用，需根据门店获取设备相关参数
    public static final String RPRO_KEY = "vfy1EkEPsup05eLB";   //下个版本弃用，需根据门店获取设备相关参数
    public static final String RPRO_SECRET = "APcPyFdTRmY9hLuT7JfGZXROhEntFQPL";    //下个版本弃用，需根据门店获取设备相关参数

    public static String GET_RPRO_TOKEN = "http://api.rpro3.visbody.com/v1/token";   //获取接口凭证
    public static String RPRO_DATABIND_URL = "http://api.rpro3.visbody.com/v1/dataBind"; //用户信息绑定
    public static String RPRO_MEASURE_MASS = "http://api.rpro3.visbody.com/v1/measure/mass";   //获取体测成分数据
    public static String RPRO_MEASURE_GIRTH = "http://api.rpro3.visbody.com/v1/measure/girth";   //获取围度数据
    public static String RPRO_SHAPE_POINTS = "http://api.rpro3.visbody.com/v1/shape/points";   //获取体态评估身体数据

    //wefit/微光互联/开启跑步机
    public static final String WEFIT_APPKEY ="dgxiaoheizijianshengfang";
    public static final String WEFIT_PRIVATE_APPKEY ="asdfghjklzswedx";
    public static String WEFIT_OPEN_DEVICE_URL = "http://wefit.wefit20.com/Api/Public/open";


    public static String ticket = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
    //购买健身卡微信支付回调
    public static final String NOTIFY_WX_URL = "http://shilijsf.shilisports.com/api/pay/wxNotify";

    //充值微信回调
    public static final String NOTIFY_WX_INMONEY_URL = "http://shilijsf.shilisports.com/api/walletPay/wxNotify";
    //公众号微信支付回调
    public static String NOTIFY_URL = "http://shilijsf.shilisports.com/api/pay/wxH5Notify";

    //购买商城商品微信退款回调
    public static String REFOUND_WXNOTIFY_URL = "http://shilijsf.shilisports.com/api/walletPay/wxRefoundNotify";
    //购买商城商品支付宝退款回调
    public static String REFOUND_ZFBNOTIFY_URL = "http://shilijsf.shilisports.com/api/walletPay/zfbRefoundNotify";

    /**供货端支付回调链接*/
    public static final String FORGOODS_CALLBACK_URL = "http://gxzjj.j.xiaozhuschool.com/api/myWallet/goodsCallBk";

    /**企业向个人付款到微信零钱的接口地址*/
    public static final String PAY_TO_USER = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
    /**免费领取纸巾url*/
    public static final String FREE_MAPPER = "http://gxzjj.j.xiaozhuschool.com/h5/gxzjj/other/html/user_success.html?";
    public static final String COACHAPPOINTMENT ="[{\"dayName\":\"\",\"timeData\":[{\"timeName\":\"00:00\",\"bookType\":1},{\"timeName\":\"01:00\",\"bookType\":1},{\"timeName\":\"02:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"03:00\",\"bookType\":1},{\"timeName\":\"04:00\",\"bookType\":1},{\"timeName\":\"05:00\",\"bookType\":1},{\"timeName\":\"06:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"07:00\",\"bookType\":1},{\"timeName\":\"08:00\",\"bookType\":1},{\"timeName\":\"09:00\",\"bookType\":1},{\"timeName\":\"10:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"11:00\",\"bookType\":1},{\"timeName\":\"12:00\",\"bookType\":1},{\"timeName\":\"13:00\",\"bookType\":1},{\"timeName\":\"14:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"15:00\",\"bookType\":1},{\"timeName\":\"16:00\",\"bookType\":1},{\"timeName\":\"17:00\",\"bookType\":1},{\"timeName\":\"18:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"19:00\",\"bookType\":1},{\"timeName\":\"20:00\",\"bookType\":1},{\"timeName\":\"21:00\",\"bookType\":1},{\"timeName\":\"22:00\",\"bookType\":1},\n" +
            "{\"timeName\":\"23:00\",\"bookType\":1}]}]\n" +
            "\n";



    /**生产环境URL*/
    
    
    /**小程序**/
    /** 小程序 APPID */
    public static final String PRO_WECHAT_APPID = "wxdbb61533c8e04999";
    /** 小程序 APPSECRET */
    public static final String PRO_WECHAT_APPSECRET = "7267e0c558878740bc85b66cd6092df5";
}
