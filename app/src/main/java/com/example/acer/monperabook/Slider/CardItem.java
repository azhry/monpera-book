package com.example.acer.monperabook.Slider;

/**
 * Created by Azhary Arliansyah on 10/02/2018.
 */

public class CardItem {

    private String code;
    private String title;
    private String content;
    private String imgUrl;
    private String like;

    public CardItem(String title, String content, String imgUrl) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
    }

    public CardItem(String code, String title, String content, String imgUrl, String like) {
        this.title = title;
        this.content = content;
        this.imgUrl = imgUrl;
        this.code = code;
        this.like = like;
    }

    public String getContent() {
        return this.content;
    }
    public String getCode() { return this.code; }
    public String getTitle() {
        return this.title;
    }
    public String getImgUrl() { return this.imgUrl; }
    public String getLike() { return this.like; }

}
