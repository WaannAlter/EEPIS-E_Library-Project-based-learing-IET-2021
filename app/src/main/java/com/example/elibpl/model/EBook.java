package com.example.elibpl.model;

public class EBook {
    private String title;
    private String author;
    private String downloadUrl;

    public EBook() {
        // Default constructor required for Firebase Firestore deserialization
    }

    public EBook(String title, String author, String downloadUrl) {
        this.title = title;
        this.author = author;
        this.downloadUrl = downloadUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }
}
