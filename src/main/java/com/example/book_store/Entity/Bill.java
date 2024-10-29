package com.example.book_store.Entity;

import java.util.Date;

public class Bill {
    private int billID;
    private Date date;
    private double totalAmount;
    private int userID;
    private String status;

    public Bill(int billID, Date date, double totalAmount, int userID, String status) {
        this.billID = billID;
        this.date = date;
        this.totalAmount = totalAmount;
        this.userID = userID;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getBillID() {
        return billID;
    }

    public void setBillID(int billID) {
        this.billID = billID;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
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

    @Override
    public String toString() {
        return "Bill{" +
                "billID=" + billID +
                ", date=" + date +
                ", totalAmount=" + totalAmount +
                ", userID=" + userID +
                ", status=" + status +
                '}';
    }
}
