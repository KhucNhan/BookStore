package com.example.book_store.Entity;

public class CartItem {
    private String title;
    private String image;
    private double price;
    private int amount;
    private double total;

    public CartItem(String title, String image, double price, int amount, double total) {
        this.title = title;
        this.image = image;
        this.price = price;
        this.amount = amount;
        this.total = total;
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
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
