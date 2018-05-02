package com.example.android.news;

/**
 * Created by prajakkhruasuwan on 11/19/17.
 */

public class NYTNews {

    private String title = "";
    private String description = "";
    private String imageUrl;
    private String linkUrl;
    private String source;
    private String publishDate;

    public NYTNews(String title,
                   String desc,
                   String imageUrl,
                   String linkUrl,
                   String source,
                   String publishDate) {
        this.title = title;
        this.description = desc;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.source = source;
        this.publishDate = publishDate;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getSource() {
        return source;
    }

    public String getPublishDate() {
        return publishDate;
    }
}
