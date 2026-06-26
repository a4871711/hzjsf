package com.dlc.common.utils;

/**
 * 常量
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月15日 下午1:23:52
 */
public class Constant {
    /**
     * 超级管理员ID
     */
    public static final int SUPER_ADMIN = 1;

    /**
     * 菜单类型
     *
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年11月15日 下午1:24:29
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        private MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 定时任务状态
     *
     * @author chenshun
     * @email sunlightcs@gmail.com
     * @date 2016年12月3日 上午12:07:22
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
        NORMAL(0),
        /**
         * 暂停
         */
        PAUSE(1);

        private int value;

        private ScheduleStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3);

        private int value;

        private CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public enum OrderStatus {
        /**
         * 待付款0
         */
        WAIT_PAY(0),
        /**
         * 待发货1
         */
        WAIT_SEED(1),
        /**
         * 待收货2
         */
        WAIT_RECIVE(2),
        /**
         * 待评价3
         */
        WAIT_COMMENT(3),
        /**
         * 已完成4
         */
        ORDER_OVER(4),
        /**
         * 已取消5
         */
        ORDER_CANCEL(5);

        private int value;

        OrderStatus(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    /**上传图片限制大小*/
    public static final Long FILE_SIZE = 10485760l;//10M

    /** 待支付状态订单 自动取消时间24小时*/
    public static final long LIMIT_TIME_PAY = 24*60*60*1000;

    /** 待确认收货订单 自动取消时间15天*/
    public static final long LIMIT_TIME_RECEIVE = 15*24*60*60*1000;

    /**APP版本更新你路径*/
    public static final String APP_FILEPATH = "/data/dlc/app/";

    /**APP文件名*/
    public static final String APP_FILENAME = "hzjsf.apk";
    /*举报内容模板*/
    public static final String REPORT_BADSALES = "垃圾营销：您好！您已被举报！为了保证您当前账号正常使用，请勿发布或参与垃圾营销内容。";

    public static final String REPORT_ILLEGALINFO = "非法信息：您好！您已被举报！为了保证您当前账号正常使用，请勿发布或参与非法信息内容。";

    public enum AddressStatus {
        /**
         * 地址不可用
         */
        ADDRESS_UNUSABLE(0),

        /**
         * 地址可用
         */
        ADDRESS_USABLE(1);

        private int value;

        AddressStatus(int value) {
            this.value =value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum WalletDetailStatus{
        /**
         *审核中
         */
        CHECKING(1),

        /**
         * 审核失败
         */
        CHECK_FAILED(2),

        /**
         * 已完成
         */
        CHECK_PAY_SUCCESS(3),

        /**
         * 提现中
         */
        CHECK_PAYING(4),

        /**
         * 提现失败
         */
        CHECK_PAY_FAILED(5);


        private int value;

        WalletDetailStatus(int value){
            this.value =value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum PayType{
        BAL(1, "余额"), WX(2, "微信"), ZFB(3, "支付宝");

        private final Integer status;

        private final String desc;

        private PayType(final Integer status,final String desc){
            this.status = status;
            this.desc = desc;
        }

        public static Integer getValue(Integer value) {
            PayType[] payTypes = values();
            for (PayType pt : payTypes) {
                if (pt.status().equals(value)) {
                    return pt.status();
                }
            }
            return null;
        }

        public static String getDesc(Integer value) {
            PayType[] payTypes = values();
            for (PayType pt : payTypes) {
                if (pt.status().equals(value)) {
                    return pt.desc();
                }
            }
            return null;
        }

        public Integer status(){
            return this.status;
        }

        public String desc(){
            return this.desc;
        }

    }

}
