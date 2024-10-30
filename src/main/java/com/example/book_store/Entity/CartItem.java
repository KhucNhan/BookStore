package com.example.book_store.Entity;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class CartItem {
    private int idOrder;
    private int idBook;
    private String title;
    private String image;
    private double price;
    private int amount;
    private double total;
    private BooleanProperty selected = new SimpleBooleanProperty(false);

    public CartItem(int idOrder, int idBook, String title, String image, double price, int amount, double total) {
        this.idOrder = idOrder;
        this.idBook = idBook;
        this.title = title;
        this.image = image;
        this.price = price;
        this.amount = amount;
        this.total = total;
        this.selected.set(false);
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
        this.total = this.price * this.amount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

}
