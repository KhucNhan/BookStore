package com.example.book_store.Entity;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Book {
    private int bookID;
    private String title;
    private String image;
    private String author;
    private int publishedYear;
    private int edition;
    private double price;
    private int amount;
    private String bookType;
    private String publisher;
    private boolean status;

    public Book(int bookID, String image, String title, String author, int publishedYear, int edition, double price, int amount, String bookType, String publisher, boolean status) {
        this.bookID = bookID;
        this.image = image;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.edition = edition;
        this.price = price;
        this.amount = amount;
        this.bookType = bookType;
        this.publisher = publisher;
        this.status = status;
    }

    public Book(int id, String image, String title, String author, double price, int amount, int publishedYear) {
        this.bookID = id;
        this.image = image;
        this.title = title;
        this.author = author;
        this.price = price;
        this.amount = amount;
        this.publishedYear = publishedYear;
    }

    public static Book fromResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("BookID");
        String image = rs.getString("image");
        String title = rs.getString("title");
        String author = rs.getString("author");
        double price = rs.getDouble("price");
        int amount = rs.getInt("amount");
        int publishedYear =rs.getInt("publishedYear");
        return new Book(id, image, title, author, price,amount,publishedYear);
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
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

    public String getBookType() {
        return bookType;
    }

    public void setBookType(String bookType) {
        this.bookType = bookType;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookID=" + bookID +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", author='" + author + '\'' +
                ", publishedYear=" + publishedYear +
                ", edition=" + edition +
                ", price=" + price +
                ", amount=" + amount +
                ", bookType=" + bookType +
                ", publisher=" + publisher +
                ", status=" + status +
                '}';
    }

}

