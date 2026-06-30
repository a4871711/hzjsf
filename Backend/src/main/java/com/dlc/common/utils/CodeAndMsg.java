package com.dlc.common.utils;

/**
 * @author tangxs
 * @date 2017/12/13
 */
public enum CodeAndMsg {

    OK(1,"成功"),
    ERROR_LACK_PARAM(-1,"缺少参数"),
    ERROR_USER_NOT_LOGIN(101,"用户未登录"),
    ERROR_USER_IS_LOCK(102,"用户已禁用"),
    ERROR_USER_LOGIN(-3,"用户名或密码有误，登录失败"),
    ERROR_REPEAT(-4,"帐号重复"),
    ERROR_NOT_CODE(-7,"Code is wrong,请与管理员联系"),
    ERROR_NOT_EXIS(-8,"对象不存在"),
    ERROR_PHONE_SEND_CODE_FAIL(-9,"发送验证码失败,请与管理员联系"),
    ERROR_CODE(-10,"验证码错误"),
    ERROR_NOT_ACCESS_TOKEN(-11,"AccessToken is wrong,请与管理员联系"),
    ERROR_NOT_OPENID(-12,"Openid is wrong,请与管理员联系"),
    ERROR_NOT_TICKET(-13,"ticket is wrong,请与管理员联系"),
    ERROR_IS_BLACKLIST(-14,"存在逾期未还的伞,已被加入黑名单"),
    ERROR_USER_IS_BLACKLIST(-14,"已被加入黑名单"),
    ERROR_UPDATE_FAIL(-15,"修改失败"),
    ERROR_CODE_INVALID(-16,"二维码已经失效"),
    ERROR_CODE_TYPE(-17,"二维码传入类型有误"),
    ERROR_NOT_ORDER_ID(-18,"还伞必须传入OrderId"),
    ERROR_ADD_ORDER(-19,"新增订单失败"),
    ERROR_UMB_NOT_NORM(-20,"传入的雨伞编号状态异常"),
    ERROR_ORDER_NOT_NORM(-21,"同一伞id，不能同时存在俩个未完成的订单"),
    ERROR_USER_NOT_NORM(-22,"用户权限不足"),
    ERROR_USER_MANAGER_NOT_NORM(-23,"经理绑定请先导入经理信息"),
    ERROR_ALREADY_USED(-24,"亲很抱歉，只能同时借一把伞哦，请您先归还已借的伞"),
    ERROR_PAY_TYPE_ERROR(-25,"支付类型有误"),
    ERROR_PAY_ERROR(-26,"支付失败"),
    ERROR_PAY_NOT_ORDERNO(-27,"消费须传入消费订单号"),
    ERROR_PAY_NOT_OPENID(-28,"支付参数openid不能为空"),
    ERROR_PAY_NOT_MONEY(-29,"支付参数money不能为空，且须大于0"),
    ERROR_USER_MONEY_NOT(-31,"余额不足"),
    ERROR_USER_NOT_MONEY(-30,"用户信息异常"),
    ERROR_USER_DEPOSIT_YET(-32,"用户已交押金，请勿重复操作"),
    ERROR_BANK_NO_ERROR(-33,"银行卡号有误，请填写正确的银行卡号"),
    ERROR_BANK_NAME_ERROR(-34,"开户银行名称有误，请填写与卡号对应的银行名称，如兴业银行、中国银行"),
    ERROR_USER_DEPOSIT_ERROR(-35,"用户未交押金，或押金不足"),
    ERROR_RETURN_DEPOSIT_ERROR(-36,"押金退还失败"),
    ERROR_TAPE_OUT(101,"你已在其它设备登录，请重新登录"),
    ERROR_LOGIN_OUT(101,"已绑定成功！请退出重新登录"),
    ERROR_LOGIN_OUT2(102,"已绑定成功！请退出重新登录"),
    VALIDATION_ERROR(103,"IOS授权验证失败"),
    VALIDATION_CODE_ERROR(104,"IOS授权解码失败"),
    USER_QROCDE_PASS(0,"用户二维码校验通过"),

    // ===== VIP 权益转让 统一码表（详细技术设计 附录A，-50 ~ -70） =====
    ERROR_VIP_BENEFIT_NOT_EXIST(-50,"权益不存在"),
    ERROR_VIP_BENEFIT_NOT_OWNER(-51,"该权益不属于你"),
    ERROR_VIP_BENEFIT_STATUS_ABNORMAL(-52,"权益状态异常"),
    ERROR_VIP_BENEFIT_EXPIRED(-53,"权益已过期"),
    ERROR_VIP_BENEFIT_NOT_TRANSFERABLE(-54,"该权益当前不可转让"),
    ERROR_VIP_CARD_OFF_SHELF(-55,"权益卡已下架"),
    ERROR_VIP_TO_USER_NOT_EXIST(-56,"受让人不存在"),
    ERROR_VIP_TO_USER_SELF(-57,"不能转让给自己"),
    ERROR_VIP_USER_BLACKLIST(-58,"账号被封禁或在黑名单"),
    ERROR_VIP_STORE_NOT_APPLICABLE(-59,"受让人门店不在该权益适用的门店范围内"),
    ERROR_VIP_TRANSFER_NOT_EXIST(-60,"转让单不存在"),
    ERROR_VIP_TRANSFER_STATUS(-61,"当前转让单状态不允许该操作"),
    ERROR_VIP_TRANSFER_IN_PROGRESS(-62,"该权益已有进行中的转让"),
    ERROR_VIP_TRANSFER_ARREARS(-63,"存在欠费，暂不可转让"),
    ERROR_VIP_TRANSFER_REFUNDED_CARD(-64,"已办理退卡，不可转让"),
    ERROR_VIP_TRANSFER_VIOLATION(-65,"存在重大违规记录"),
    ERROR_PAUSE_LIMIT_MONTH(-66,"本月已停卡一次"),
    ERROR_PAUSE_LIMIT_YEAR(-67,"全年停卡次数已用完，共12次"),
    ERROR_PAUSE_STATE(-68,"停卡状态异常"),
    ERROR_VIP_FEE_RULE_NOT_EXIST(-69,"转让费用规则不存在或未配置"),
    ERROR_VIP_FEE_RULE_FORMAT(-70,"分档配置格式有误"),

    /* ===== 私教管理后台业务码(全局收口,唯一来源;避开 -1~-36 / 101~104 / VIP -50~-70) ===== */
    /* —— 交易域 -80 ~ -99 —— */
    ERROR_PRODUCT_OFF(-80,"商品已下架"),
    ERROR_PRODUCT_NOT_EXIST(-81,"私教商品不存在"),
    ERROR_STOCK_NOT_ENOUGH(-82,"库存不足"),
    ERROR_PURCHASE_LIMIT(-83,"已达每人限购数量"),
    ERROR_COUPON_INVALID(-84,"优惠券不可用"),
    ERROR_BENEFIT_NOT_EXIST(-85,"会员私教权益不存在"),
    ERROR_BENEFIT_EXPIRED(-86,"私教权益已过期"),
    ERROR_BENEFIT_STATUS_ABNORMAL(-87,"私教权益状态异常"),
    ERROR_LESSON_NOT_ENOUGH(-88,"剩余课时不足"),
    ERROR_SLOT_TAKEN(-89,"该时段已被约满"),
    ERROR_SLOT_ILLEGAL(-90,"预约时段不合法"),
    ERROR_CANCEL_WINDOW(-91,"已超过可无责取消时间"),
    ERROR_PT_ORDER_NOT_EXIST(-92,"私教订单不存在"),
    ERROR_PT_ORDER_STATUS(-93,"订单状态不允许该操作"),
    ERROR_REFUND_AMOUNT_ILLEGAL(-94,"退款金额不合法"),
    ERROR_COACH_UNAVAILABLE(-95,"该教练当前不可预约"),
    ERROR_APPOINTMENT_NOT_EXIST(-96,"预约记录不存在"),
    /* —— 资金域 -100 ~ -119 —— */
    ERROR_WALLET_NOT_EXIST(-100,"储值账户不存在"),
    ERROR_WALLET_FROZEN(-101,"储值账户已冻结"),
    ERROR_WALLET_BALANCE_NOT_ENOUGH(-102,"储值余额不足"),
    ERROR_INSTALLMENT_PLAN_NOT_EXIST(-103,"分期计划不存在"),
    ERROR_INSTALLMENT_BILL_NOT_EXIST(-104,"分期账单不存在"),
    ERROR_INSTALLMENT_BILL_PAID(-105,"该期账单已支付"),
    ERROR_INSTALLMENT_NOT_CURRENT_PERIOD(-106,"请按顺序支付当前期账单"),
    /* —— 运营域 -120 ~ -129 —— */
    ERROR_APPOINTMENT_NOT_FINISH(-120,"仅已完成预约可评价"),
    ERROR_COMMENT_EXISTS(-121,"该预约已评价"),
    ERROR_APPOINTMENT_NOT_OWNER(-122,"该预约不属于你"),
    ERROR_SETTLE_RATIO(-123,"门店分成比例之和必须为100%"),
    ;

    CodeAndMsg(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private Integer code;
    private String msg;
    /**
     * @Package com.dlc.common.utils
     * @Description: CodeAndMsg
     * @Copyright: Copyright (c) 2017
     * Author tangxs
     * @date 2017/12/13 17:12
     * version V1.0.0
     */
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
