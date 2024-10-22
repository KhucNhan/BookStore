package com.example.book_store.Entity;

public class BookOrder {
    private int orderID;
    private int bookID;
    private int amount;

    public BookOrder(int orderID, int bookID, int amount) {
        this.orderID = orderID;
        this.bookID = bookID;
        this.amount = amount;
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

    @Override
    public String toString() {
        return "BooksOrders{" +
                "orderID=" + orderID +
                ", bookID=" + bookID +
                ", amount=" + amount +
                '}';
    }
}

