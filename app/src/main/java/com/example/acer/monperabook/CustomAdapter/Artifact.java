package com.example.acer.monperabook.CustomAdapter;

/**
 * Created by Azhary Arliansyah on 29/10/2017.
 */

public class Artifact {

    private String code;
    private String title;
    private String description;

    public Artifact(String code, String title, String description) {
        this.code = code;
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCode() { return this.code; }
}
