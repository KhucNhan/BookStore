package com.example.book_store.Entity;

import java.sql.Date;

public class Order {
    private int orderID;
    private int userID;
    private String date;
    private String status;

    public Order(int orderID, int userID, String date, String status) {
        this.orderID = orderID;
        this.userID = userID;
        this.date = date;
        this.status = status;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", userID=" + userID +
                ", date='" + date + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
