package com.example.jomarie.csquiz.model;

import android.support.annotation.NonNull;

/**
 * Created by jomarie on 7/24/2017.
 */

public class Leaderboardmodel implements Comparable{
    private int id;
    private String name;
    private String date;
    private int Score;

    public Leaderboardmodel(){
        id = 0;
        name = "";
        date = "";
        Score = 0;
    }
    public Leaderboardmodel(int id, String name, String date, int score) {
        this.id = id;
        this.name = name;
        this.date = date;
        Score = score;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getScore() {
        return Score;
    }

    public void setScore(int score) {
        Score = score;
    }


    @Override
    public int compareTo(@NonNull Object compareName) {
        int compareScore =((Leaderboardmodel)compareName).getScore();
        return compareScore- this.Score;
    }
}
