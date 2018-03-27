package com.example.acer.monperabook.CustomAdapter;

import java.util.List;

/**
 * Created by Azhary Arliansyah on 27/02/2018.
 */

public class Challenge {

    private int id;
    private String question;
    private List<Answer> answers;

    public Challenge(int id, String question, List<Answer> answers) {
        this.question = question;
        this.answers = answers;
        this.id = id;
    }

    public int getId() { return this.id; }
    public String getQuestion() { return this.question; }
    public List<Answer> getAnswers() { return this.answers; }

}
