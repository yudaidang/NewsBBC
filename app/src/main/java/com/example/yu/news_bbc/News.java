package com.example.yu.news_bbc;

/**
 * Created by cpu11268-local on 05/02/2018.
 */

public class News {
    private String author;
    private String title;
    private String url;
    private String urlImage;
    private String date;

    public News(String author, String title, String url, String urlImage, String date) {
        this.author = author;
        this.title = title;
        this.url = url;
        this.urlImage = urlImage;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
