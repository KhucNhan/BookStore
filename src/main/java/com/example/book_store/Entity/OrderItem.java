package com.example.book_store.Entity;

public class OrderItem {
    private int orderItemID;
    private int orderID;
    private int bookID;
    private int amount;
    private double price;
    private double totalPrice;

    public OrderItem(int orderItemID, int orderID, int bookID, int amount, double price, double totalPrice) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.bookID = bookID;
        this.amount = amount;
        this.price = price;
        this.totalPrice = totalPrice;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
