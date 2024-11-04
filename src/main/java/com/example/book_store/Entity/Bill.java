package com.example.book_store.Entity;

import java.util.Date;

public class Bill {
    private int billID;
    private String date;
    private double totalAmount;
    private int userID;
    private int orderID;

    public Bill(int billID, String date, double totalAmount, int userID,int orderID){
        this.billID = billID;
        this.date = date;
        this.totalAmount = totalAmount;
        this.userID = userID;
        this.orderID = orderID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }


}
