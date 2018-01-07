package com.example.acer.monperabook.CustomAdapter;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class Artifact {

    private String code;
    private String title;
    private String description;
    private String like;
    private String images;

    public Artifact(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.like = "0";
        this.images = "";
    }

    public Artifact(String code, String title, String description, String like) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.like = like;
        this.images = "";
    }

    public Artifact(String code, String title, String description, String like, String images) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.like = like;
        this.images = images;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCode() { return this.code; }

    public String getLike() { return this.like; }

    public String getImages() { return this.images; }
}
