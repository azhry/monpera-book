package com.example.acer.monperabook.CustomAdapter;

/**
 * Created by Azhary Arliansyah on 21/03/2018.
 */

public class Answer {

    private int status;
    private int id;
    private String answer;

    public Answer(int id, String answer, int status) {
        this.id = id;
        this.answer = answer;
        this.status = status;
    }

    public int getId() { return this.id; }
    public String getAnswer() { return this.answer; }
    public int getStatus() { return this.status; }

}
