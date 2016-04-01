package com.hanvon.rc.orders;

import java.io.Serializable;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/3/29 0029.
 */
public class OrderDetail implements Serializable {
    private String OrderFailedTime;

    @Override
    public String toString() {
        return "OrderDetail{" +
                "OrderFailedTime='" + OrderFailedTime + '\'' +
                ", OrderFilesPages=" + OrderFilesPages +
                ", OrderFilesBytes=" + OrderFilesBytes +
                ", OrderMethod='" + OrderMethod + '\'' +
                ", OrderPrice=" + OrderPrice +
                ", OrderNumber='" + OrderNumber + '\'' +
                ", OrderCreateTime='" + OrderCreateTime + '\'' +
                ", OrderPayMethod=" + OrderPayMethod +
                ", OrderName='" + OrderName + '\'' +
                ", OrderPhone='" + OrderPhone + '\'' +
                ", OrderAlreadyTime='" + OrderAlreadyTime + '\'' +
                '}';
    }

    private String OrderFilesPages;
    private String OrderFilesBytes;
    private String OrderMethod;
    private String OrderPrice;
    private String OrderNumber;
    private String OrderCreateTime;
    private String OrderPayMethod;
    private String OrderName;
    private String OrderPhone;
    private String OrderAlreadyTime;

    public String getOrderTitle() {
        return OrderTitle;
    }

    public void setOrderTitle(String orderTitle) {
        OrderTitle = orderTitle;
    }

    private String OrderTitle;

    public String getOrderStatus() {
        return OrderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        OrderStatus = orderStatus;
    }

    public String getOrderFileNanme() {
        return OrderFileNanme;
    }

    public void setOrderFileNanme(String orderFileNanme) {
        OrderFileNanme = orderFileNanme;
    }

    private String OrderStatus;
    private String OrderFileNanme;


    public String getOrderFailedTime() {
        return OrderFailedTime;
    }

    public void setOrderFailedTime(String orderFailedTime) {
        OrderFailedTime = orderFailedTime;
    }

    public String getOrderFilesPages() {
        return OrderFilesPages;
    }

    public void setOrderFilesPages(String orderFilesPages) {
        OrderFilesPages = orderFilesPages;
    }

    public String getOrderFilesBytes() {
        return OrderFilesBytes;
    }

    public void setOrderFilesBytes(String orderFilesBytes) {
        OrderFilesBytes = orderFilesBytes;
    }

    public String getOrderMethod() {
        return OrderMethod;
    }

    public void setOrderMethod(String orderMethod) {
        OrderMethod = orderMethod;
    }

    public String getOrderPrice() {
        return OrderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        OrderPrice = orderPrice;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getOrderCreateTime() {
        return OrderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        OrderCreateTime = orderCreateTime;
    }

    public String getOrderPayMethod() {
        return OrderPayMethod;
    }

    public void setOrderPayMethod(String orderPayMethod) {
        OrderPayMethod = orderPayMethod;
    }

    public String getOrderName() {
        return OrderName;
    }

    public void setOrderName(String orderName) {
        OrderName = orderName;
    }

    public String getOrderPhone() {
        return OrderPhone;
    }

    public void setOrderPhone(String orderPhone) {
        OrderPhone = orderPhone;
    }

    public String getOrderAlreadyTime() {
        return OrderAlreadyTime;
    }

    public void setOrderAlreadyTime(String orderAlreadyTime) {
        OrderAlreadyTime = orderAlreadyTime;
    }


}
