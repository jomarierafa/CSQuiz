package com.example.jomarie.csquiz.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jomarie.csquiz.LoadingScreen;
import com.example.jomarie.csquiz.MainActivity;
import com.example.jomarie.csquiz.model.Question;
import com.example.jomarie.csquiz.normalgame;
import com.example.jomarie.csquiz.quickThree;
import com.example.jomarie.csquiz.quickgame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by jomarie on 7/8/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static String dbName = "courses.sqlite";
    public static final int DB_VERSION = 1;
    public static final String LOCATION = "/data/data/com.example.jomarie.csquiz/databases/";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context){
        super(context, dbName, null, DB_VERSION);
        this.mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(newVersion > oldVersion){

        }
    }

    public void openDatabase(){
        String dbPath = mContext.getDatabasePath(dbName).getPath();
        if(mDatabase !=  null && mDatabase.isOpen()){

            return;
        }
        mDatabase = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
    public void closeDatabase(){
        if(mDatabase!=null){
            mDatabase.close();
        }
    }
    // adding all the question from the database to the arraylist in normal game mode
    public List<Question> getListQuestion(){
        List<Question> questionList = new ArrayList<>();
        openDatabase();
        String sub = "", column = "";
        Cursor cursor = null;
        if(MainActivity.mode.equals("normalgame")) {
            sub = quickgame.subject;
            int levels = 0;
            /* this is for future use
            if (quickgame.subjectRow == 0) {
                levels = quickgame.sub0Level;
            } else if (quickgame.subjectRow == 1) {
                levels = quickgame.sub1Level;
            } else if (quickgame.subjectRow == 2) {
                levels = quickgame.sub2Level;
            } else if (quickgame.subjectRow == 3) {
                levels = quickgame.sub3Level;
            } else if (quickgame.subjectRow == 4) {
                levels = quickgame.sub4Level;
            }*/
            if(normalgame.gameType.equals("rumble")){column = "fiblevel";}else{column="levels";}
            levels = quickgame.sub0Level;
            cursor = mDatabase.rawQuery("SELECT '" + sub + "' as table_name, * FROM " + sub + " WHERE " + column + " = '" + levels + "'", null);
        }
        else if(MainActivity.mode.equals("quickgame")){
            sub = quickThree.subject;
            int firstLevel = quickThree.firstLevel;
            int secondLevel = quickThree.secondLevel;
            if(normalgame.gameType.equals("rumble")){column = "fiblevel";}else{column="levels";}
            cursor = mDatabase.rawQuery("SELECT '" + sub + "' as table_name, * FROM " + sub + " WHERE "+ column +" = '" + firstLevel + "' OR " +column + " = '" + secondLevel + "'" , null);
        }else if(MainActivity.mode.equals("timelimit")){
            String sub1, sub2, sub3, sub4, sub5;
            sub1 = LoadingScreen.sub1;
            sub2 = LoadingScreen.sub2;
            sub3 = LoadingScreen.sub3;
            sub4 = LoadingScreen.sub4;
            sub5 = LoadingScreen.sub5;
            cursor = mDatabase.rawQuery("SELECT '" + sub1 + "' as table_name, * FROM "+ sub1  +
                    " UNION SELECT '" + sub2 + "' as table_name, * FROM " + sub2 +
                    " UNION SELECT '" + sub3 + "' as table_name, * FROM " + sub3 +
                    " UNION SELECT '" + sub4 + "' as table_name, * FROM " + sub4 +
                    " UNION SELECT '" + sub5 + "' as table_name, * FROM " + sub5, null);
        }else if(MainActivity.mode.equals("dual")){
            String sub1="Cisco", sub2="Automata", sub3="OOP", sub4="IntroductionToCS", sub5="AIntelligence", sub6="DBMS", sub7="Algo", sub8="COA", sub9="OST", sub10="LogicCircuit", sub11="WebProg", sub12="ProgrammingLan";
            cursor = mDatabase.rawQuery("SELECT '" + sub1 + "' as table_name, * FROM "+ sub1  +
                    " UNION SELECT '" + sub2 + "' as table_name, * FROM " + sub2 +
                    " UNION SELECT '" + sub3 + "' as table_name, * FROM " + sub3 +
                    " UNION SELECT '" + sub4 + "' as table_name, * FROM " + sub4 +
                    " UNION SELECT '" + sub5 + "' as table_name, * FROM " + sub5 +
                    " UNION SELECT '" + sub6 + "' as table_name, * FROM " + sub6 +
                    " UNION SELECT '" + sub7 + "' as table_name, * FROM " + sub7 +
                    " UNION SELECT '" + sub8 + "' as table_name, * FROM " + sub8 +
                    " UNION SELECT '" + sub9 + "' as table_name, * FROM " + sub9 +
                    " UNION SELECT '" + sub10 + "' as table_name, * FROM " + sub10 +
                    " UNION SELECT '" + sub11 + "' as table_name, * FROM " + sub11 +
                    " UNION SELECT '" + sub12 + "' as table_name, * FROM " + sub12, null);
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Question question = new Question();
            String questText = cursor.getString(cursor.getColumnIndex("question"));
            question.setQuestion(questText);
            String ch1Text = cursor.getString(cursor.getColumnIndex("choice1"));
            question.setChoices(0,ch1Text);
            String ch2Text = cursor.getString(cursor.getColumnIndex("choice2"));
            question.setChoices(1,ch2Text);
            String ch3Text = cursor.getString(cursor.getColumnIndex("choice3"));
            question.setChoices(2,ch3Text);
            String ch4Text = cursor.getString(cursor.getColumnIndex("choice4"));
            question.setChoices(3,ch4Text);

            String answerText = cursor.getString(cursor.getColumnIndex("answer"));
            question.setAnswer(answerText);

            String level = cursor.getString(cursor.getColumnIndex("levels"));
            question.setLevel(level);

            String course = cursor.getString(cursor.getColumnIndex("table_name"));
            question.setCourse(course);

            String fiblevel = cursor.getString(cursor.getColumnIndex("fiblevel"));
            question.setFiblevel(fiblevel);

            questionList.add(question);
            cursor.moveToNext();

        }

        Collections.shuffle(questionList);
        cursor.close();
        closeDatabase();
        return questionList;
    }



}
