package com.example.jomarie.csquiz.model;

/**
 * Created by jomarie on 7/8/2017.
 */

public class Question {
    private int id;
    private String question;
    private String[] choices = new String[4];
    private String answer;
    private String level;
    private String course;
    private String fiblevel;
    public Question(){

    }
    public Question(int id, String question, String[] choice, String answer, String level, String course, String fiblevel){
        this.id =id;
        this.question = question;
        this.choices[0] = choice[0];
        this.choices[1] = choice[1];
        this.choices[2] = choice[2];
        this.choices[3] = choice[3];
        this.answer = answer;
        this.level = level;
        this.course = course;
        this.fiblevel = fiblevel;
    }


    public int getId() {
        return id;
    }
    public void setId(int id){ this.id = id; }
    public void setChoices(String[] choices) {
        this.choices = choices;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getChoices(int i) {
        return choices[i];
    }

    public String getLevel() {
        return level;
    }

    public String getCourse(){
        return course;
    }

    public String getFiblevel() {
        return fiblevel;
    }

    public void setCourse(String course) {this.course = course;}

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setChoices(int i,String choices) {
        this.choices[i] = choices;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setFiblevel(String fiblevel) {
        this.fiblevel = fiblevel;
    }


}
