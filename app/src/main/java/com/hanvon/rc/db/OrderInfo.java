package com.hanvon.rc.db;

import java.io.Serializable;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/5/10 0010.
 */
public class OrderInfo implements Serializable {
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPayMode() {
        return payMode;
    }

    public void setPayMode(String payMode) {
        this.payMode = payMode;
    }

    private String orderNumber;
    private String payMode; // 0 alipay  1 wxpay
}
