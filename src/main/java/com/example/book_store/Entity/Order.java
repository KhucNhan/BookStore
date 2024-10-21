package com.example.book_store.Entity;

public class Order {
    private int orderID;
    private int billID;
    private int bookID;
    private int amount;
    private double price;
    private double totalAmount;

    public Order(int orderID, int billID, int bookID, int amount, double price) {
        this.orderID = orderID;
        this.billID = billID;
        this.bookID = bookID;
        this.amount = amount;
        this.price = price;
        this.totalAmount = amount * price;
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

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderID=" + orderID +
                ", billID=" + billID +
                ", bookID=" + bookID +
                ", amount=" + amount +
                ", price=" + price +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
