package com.example.book_store;

public class BookType {
    private int bookTypeID;
    private String nameBookType;

    public BookType(int bookTypeID, String nameBookType) {
        this.bookTypeID = bookTypeID;
        this.nameBookType = nameBookType;
    }

    // Getters và setters
    public int getBookTypeID() {
        return bookTypeID;
    }

    public void setBookTypeID(int bookTypeID) {
        this.bookTypeID = bookTypeID;
    }

    public String getNameBookType() {
        return nameBookType;
    }

    public void setNameBookType(String nameBookType) {
        this.nameBookType = nameBookType;
    }

    @Override
    public String toString() {
        return "BookType{" +
                "bookTypeID=" + bookTypeID +
                ", nameBookType='" + nameBookType + '\'' +
                '}';
    }
}

