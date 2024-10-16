package com.example.book_store;

public class Book {
    private int bookID;
    private String title;
    private String image;
    private String author;
    private int publishedYear;
    private int edition;
    private double price;
    private int amount;
    private int bookTypeID;
    private int publisherID;

    public Book(int bookID, String image, String title, String author, int publishedYear, int edition, double price, int amount, int bookTypeID, int publisherID) {
        this.bookID = bookID;
        this.image = image;
        this.title = title;
        this.author = author;
        this.publishedYear = publishedYear;
        this.edition = edition;
        this.price = price;
        this.amount = amount;
        this.bookTypeID = bookTypeID;
        this.publisherID = publisherID;
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

    public int getBookTypeID() {
        return bookTypeID;
    }

    public void setBookTypeID(int bookTypeID) {
        this.bookTypeID = bookTypeID;
    }

    public int getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(int publisherID) {
        this.publisherID = publisherID;
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
                ", bookTypeID=" + bookTypeID +
                ", publisherID=" + publisherID +
                '}';
    }
}

