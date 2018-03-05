package com.example.acer.monperabook.CustomAdapter;

import java.util.List;

/**
 * Created by Azhary Arliansyah on 27/02/2018.
 */

public class Challenge {

    private String question;
    private List<String> answers;

    public Challenge(String question, List<String> answers) {
        this.question = question;
        this.answers = answers;
    }

    public String getQuestion() { return this.question; }
    public List<String> getAnswers() { return this.answers; }

}
