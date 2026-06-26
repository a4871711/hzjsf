/**
 * Copyright (C), 2015-2019, XXX有限公司
 * FileName: Message
 * Author:   Administrator
 * Date:     2019/4/26 13:57
 * Description: 消息类
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.dlc.common.utils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author chenyuexin
 * @date 2019-04-26 13:57
 * @version 1.0
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -389326121047047723L;
    private Long userId;
    private BigDecimal money;
    private BigDecimal leastMoney;
    private String aliAccount;
    public Message(){}
    public Message(Long userId, BigDecimal money, BigDecimal leastMoney, String aliAccount) {
        this.userId = userId;
        this.money = money;
        this.leastMoney = leastMoney;
        this.aliAccount = aliAccount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public BigDecimal getLeastMoney() {
        return leastMoney;
    }

    public void setLeastMoney(BigDecimal leastMoney) {
        this.leastMoney = leastMoney;
    }

    public String getAliAccount() {
        return aliAccount;
    }

    public void setAliAccount(String aliAccount) {
        this.aliAccount = aliAccount;
    }
}