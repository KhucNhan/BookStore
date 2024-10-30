package com.example.book_store.Entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class CartItem {
    private int cartItemID;
    private boolean selected;
    private int cartID;
    private int bookID;
    private String image;
    private String title;
    private double price;
    private int amount;
    private double total;
    private int orderItemID;

    public CartItem(int cartItemID, boolean selected, int cartID, int bookID, String image, String title, double price, int amount, double total, int orderItemID) {
        this.cartItemID = cartItemID;
        this.cartID = cartID;
        this.bookID = bookID;
        this.image = image;
        this.title = title;
        this.price = price;
        this.amount = amount;
        this.total = total;
        this.orderItemID = orderItemID;
        this.selected = selected;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getCartID() {
        return cartID;
    }

    public void setCartID(int cartID) {
        this.cartID = cartID;
    }

    public int getbookID() {
        return bookID;
    }

    public void setbookID(int bookID) {
        this.bookID = bookID;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

}
