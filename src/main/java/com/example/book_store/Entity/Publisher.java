package com.example.book_store.Entity;

public class Publisher {
    private int publisherID;
    private String namePublisher;

    public Publisher(int publisherID, String namePublisher) {
        this.publisherID = publisherID;
        this.namePublisher = namePublisher;
    }

    // Getters và setters
    public int getPublisherID() {
        return publisherID;
    }

    public void setPublisherID(int publisherID) {
        this.publisherID = publisherID;
    }

    public String getNamePublisher() {
        return namePublisher;
    }

    public void setNamePublisher(String namePublisher) {
        this.namePublisher = namePublisher;
    }

    @Override
    public String toString() {
        return "Publisher{" +
                "publisherID=" + publisherID +
                ", namePublisher='" + namePublisher + '\'' +
                '}';
    }
}

