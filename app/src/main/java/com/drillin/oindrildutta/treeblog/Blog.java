package com.drillin.oindrildutta.treeblog;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;

import java.io.Serializable;
import java.util.Objects;

public class Blog implements Comparable, Serializable {
    private final int num;
    private final String url;
    private final String title;
    private final String date;
    private final String author;
    private int sortMode;

    public Blog(int num, String url, String title, String date, String author) {
        this.num = num;
        this.url = url;
        this.title = title;
        this.date = date;
        this.author = author;
        sortMode = 0;
    }

    public String getUrl() {
        return url;
    }

    public int getNum() {
        return num;
    }

    public String getTitle() {
        return Jsoup.parse(title).text();
    }

    public String getDate() {
        return date.substring(0, 10);
    }

    public String getAuthor() {
        return author;
    }

//    public String getImgUrl() {
//        if(imgUrl == null)
//            return "";
//        return imgUrl;
//    }
//
//    public Bitmap getImg(String url) {
//        if(url == null)
//            return null;
//        OkHttpHandler handler = new OkHttpHandler();
//        byte[] image;
//        try {
//            image = handler.execute(url).get();
//            if (image != null && image.length > 0)
//                return BitmapFactory.decodeByteArray(image, 0, image.length);
//        } catch (Exception ignored) {}
//        return null;
//    }
//
//    public int getScore() {
//        return score;
//    }
//
//    public Blog setScore(int score) {
//        this.score = score;
//        return this;
//    }

    @Override
    public String toString() {
        return title + author;
    }

    public void setSortMode(int sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    public boolean equals(Object o) {
        Blog other = (Blog)o;
        return Objects.equals(title, other.getTitle()) && Objects.equals(url, other.getUrl()) && Objects.equals(author, other.getAuthor());
    }

    @Override
    public int compareTo(@NonNull Object another) {
        Blog other = (Blog)another;
        if(sortMode == 0)
            return num - other.getNum();
        else if(sortMode == 1)
            return other.getTitle().compareTo(title);
        else return sortMode == 2 ? other.getAuthor().compareTo(author) : 0;
    }
}