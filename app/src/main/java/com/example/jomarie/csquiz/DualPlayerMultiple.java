package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jomarie.csquiz.database.DatabaseHelper;
import com.example.jomarie.csquiz.model.Question;
import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


import es.dmoral.toasty.Toasty;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

public class DualPlayerMultiple extends Activity {
    private TextView p1Question, p2Question, txtp1Score, txtp2Score, p1course, p2course;
    private Button p1Choice1, p1Choice2, p1Choice3, p1Choice4;
    private Button p2Choice1, p2Choice2, p2Choice3, p2Choice4;
    private List<Question> mQuestionListp1, mQuestionListp2;
    private DatabaseHelper mDBHelper;
    private String p1CorrectAns, p2CorrectAns;
    private int mQuestionNumberp1 = 0, mQuestionNumberp2 = 0;
    private ProgressBar meter;
    private Timer timers, timer;
    private SoundPlayer sound;
    private int p1Score = 0, p2Score = 0, meterProgress = 50;

    RingProgressBar ringProgressBar;
    int progress = 0;
    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                if(progress < 1000){
                    progress++;
                    ringProgressBar.setProgress(progress);
                }
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dual_players);

        sound = new SoundPlayer(this);
        p1Question = (TextView) findViewById(R.id.p1question);
        p1Choice1 = (Button) findViewById(R.id.p1chhoice1);
        p1Choice2 = (Button) findViewById(R.id.p1choice2);
        p1Choice3 = (Button) findViewById(R.id.p1choice3);
        p1Choice4 = (Button) findViewById(R.id.p1choice4);
        p2Question = (TextView) findViewById(R.id.p2question);
        p2Choice1 = (Button) findViewById(R.id.p2choice1);
        p2Choice2 = (Button) findViewById(R.id.p2choice2);
        p2Choice3 = (Button) findViewById(R.id.p2choice3);
        p2Choice4 = (Button) findViewById(R.id.p2choice4);

        txtp1Score = (TextView) findViewById(R.id.p1ScoreView);
        txtp2Score = (TextView) findViewById(R.id.p2ScoreView);

        p1course = (TextView) findViewById(R.id.mplayer1course);
        p2course = (TextView) findViewById(R.id.mplayer2course);

        meter = (ProgressBar) findViewById(R.id.meter);
        meter.setProgress(meterProgress);

        mDBHelper = new DatabaseHelper(this);
        mQuestionListp1 = mDBHelper.getListQuestion();
        mQuestionListp2 = mDBHelper.getListQuestion();
        Collections.shuffle(mQuestionListp2);
        updateQuestionP1();
        updateQuestionP2();

        ringProgressBar = (RingProgressBar) findViewById(R.id.progressbar);
        ringProgressBar.setOnProgressListener(new RingProgressBar.OnProgressListener(){
           @Override
            public void progressToComplete(){
               Intent intent = new Intent(DualPlayerMultiple.this, DualplayerWinner.class);
               intent.putExtra("p1score", String.valueOf(p1Score));
               intent.putExtra("p2score", String.valueOf(p2Score));
               startActivity(intent);
               finish();
               stopService(new Intent(DualPlayerMultiple.this, thinkingmusic.class));
               startService(new Intent(DualPlayerMultiple.this, backgroundmusic.class));
           }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 100; i++) {
                    try{
                        Thread.sleep(2000);
                        myHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        startService(new Intent(this, thinkingmusic.class));
        p1Question.getBackground().setAlpha(40);
        p2Question.getBackground().setAlpha(40);
        setFont();
    }

    private void setFont(){
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        p1Question.setTypeface(textFont);
        p2Question.setTypeface(textFont);
        p1course.setTypeface(textFont);
        p2course.setTypeface(textFont);
        p1Choice1.setTypeface(textFont);
        p1Choice2.setTypeface(textFont);
        p1Choice3.setTypeface(textFont);
        p1Choice4.setTypeface(textFont);
        p2Choice1.setTypeface(textFont);
        p2Choice2.setTypeface(textFont);
        p2Choice3.setTypeface(textFont);
        p2Choice4.setTypeface(textFont);
    }

    private void updateQuestionP1(){
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if(mQuestionNumberp1 < mQuestionListp1.size()) {
            Question s = mQuestionListp1.get(mQuestionNumberp1);
            p1Question.setText(s.getQuestion());
            p1course.setText(s.getCourse());
            //for randoming the question
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            p1Choice1.setText(s.getChoices((Integer) arr[0]));
            p1Choice2.setText(s.getChoices((Integer) arr[1]));
            p1Choice3.setText(s.getChoices((Integer) arr[2]));
            p1Choice4.setText(s.getChoices((Integer) arr[3]));
            p1CorrectAns = s.getAnswer();
            mQuestionNumberp1++;
        }
        else{
            Toast.makeText(this, "You Answer All the Question", Toast.LENGTH_SHORT).show();
        }
    }
    private void updateQuestionP2(){
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if(mQuestionNumberp2 < mQuestionListp2.size()) {
            Question s2 = mQuestionListp2.get(mQuestionNumberp2);
            p2Question.setText(s2.getQuestion());
            p2course.setText(s2.getCourse());
            //for randoming the question
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            p2Choice1.setText(s2.getChoices((Integer) arr[0]));
            p2Choice2.setText(s2.getChoices((Integer) arr[1]));
            p2Choice3.setText(s2.getChoices((Integer) arr[2]));
            p2Choice4.setText(s2.getChoices((Integer) arr[3]));
            p2CorrectAns = s2.getAnswer();
            mQuestionNumberp2++;
        }
        else{
            Toasty.info(this, "You Answer All The Question!", Toast.LENGTH_SHORT, true).show();
        }
    }

    private void updateScoreP1(int point){
        p1Score = Integer.parseInt(String.valueOf(txtp1Score.getText()));
        p1Score += point;
        txtp1Score.setText(String.valueOf(p1Score));
    }
    private void updateScoreP2(int point){
        p2Score = Integer.parseInt(String.valueOf(txtp2Score.getText()));
        p2Score += point;
        txtp2Score.setText(String.valueOf(p2Score));
    }


    public void chooseP1(View view) {
        Button answer = (Button) view;
        disabledButtonChoiceP1();
        if (answer.getText().equals(p1CorrectAns)) {
            increment();
            sound.playCorrectSound();
            answer.setBackgroundResource(R.drawable.correctbutton);
            updateScoreP1(1);
        } else {
            sound.playWrongSound();
            answer.setBackgroundResource(R.drawable.wrongbutton);
            viewCorrectAnsP1();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DualPlayerMultiple.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestionP1();
                        resetChoiceP1();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    public void chooseP2(View view) {
        Button answer = (Button) view;
        disabledButtonChoiceP2();

        if (answer.getText().equals(p2CorrectAns)) {
            decrement();
            sound.playCorrectSound();
            answer.setBackgroundResource(R.drawable.correctbutton);
            updateScoreP2(1);
        } else {
            sound.playWrongSound();
            answer.setBackgroundResource(R.drawable.wrongbutton);
            viewCorrectAnsP2();
        }
        timers = new Timer();
        timers.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DualPlayerMultiple.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestionP2();
                        resetChoiceP2();
                        timers.cancel();
                        timers.purge();
                    }
                });
            }
        }, 3000, 3000);
    }
    public void disabledButtonChoiceP1(){
        p1Choice1.setClickable(false);
        p1Choice2.setClickable(false);
        p1Choice3.setClickable(false);
        p1Choice4.setClickable(false);
    }
    public void disabledButtonChoiceP2(){
        p2Choice1.setClickable(false);
        p2Choice2.setClickable(false);
        p2Choice3.setClickable(false);
        p2Choice4.setClickable(false);
    }
    public void viewCorrectAnsP1(){
        if(p1CorrectAns.equals(p1Choice1.getText())){p1Choice1.setBackgroundResource(R.drawable.correctbutton);}
        else if(p1CorrectAns.equals(p1Choice2.getText())){p1Choice2.setBackgroundResource(R.drawable.correctbutton);}
        else if(p1CorrectAns.equals(p1Choice3.getText())){p1Choice3.setBackgroundResource(R.drawable.correctbutton);}
        else {p1Choice4.setBackgroundResource(R.drawable.correctbutton);}
    }
    public void viewCorrectAnsP2(){
        if(p2CorrectAns.equals(p2Choice1.getText())){p2Choice1.setBackgroundResource(R.drawable.correctbutton);}
        else if(p2CorrectAns.equals(p2Choice2.getText())){p2Choice2.setBackgroundResource(R.drawable.correctbutton);}
        else if(p2CorrectAns.equals(p2Choice3.getText())){p2Choice3.setBackgroundResource(R.drawable.correctbutton);}
        else {p2Choice4.setBackgroundResource(R.drawable.correctbutton);}
    }
    public void resetChoiceP1(){
        p1Choice1.setClickable(true);
        p1Choice2.setClickable(true);
        p1Choice3.setClickable(true);
        p1Choice4.setClickable(true);
        p1Choice1.setBackgroundResource(R.drawable.menubutton);
        p1Choice2.setBackgroundResource(R.drawable.menubutton);
        p1Choice3.setBackgroundResource(R.drawable.menubutton);
        p1Choice4.setBackgroundResource(R.drawable.menubutton);
    }
    public void resetChoiceP2(){
        p2Choice1.setClickable(true);
        p2Choice2.setClickable(true);
        p2Choice3.setClickable(true);
        p2Choice4.setClickable(true);
        p2Choice1.setBackgroundResource(R.drawable.menubutton);
        p2Choice2.setBackgroundResource(R.drawable.menubutton);
        p2Choice3.setBackgroundResource(R.drawable.menubutton);
        p2Choice4.setBackgroundResource(R.drawable.menubutton);
    }

    //meter of two player
    private void increment(){
        if(meterProgress < 100){
            meterProgress += 5;
            meter.setProgress(meterProgress);
        }
        if(meterProgress == 80){
            progress = 1000;
            ringProgressBar.setProgress(progress);
        }
    }
    private void decrement(){
        if(meterProgress > 0){
            meterProgress -= 5;
            meter.setProgress(meterProgress);
        }
        if(meterProgress == 20){
            progress = 1000;
            ringProgressBar.setProgress(progress);
        }
    }


    //back button of android
    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(DualPlayerMultiple.this, thinking.class));
            stopService(new Intent(DualPlayerMultiple.this, thinkingmusic.class));
            stopService(new Intent(DualPlayerMultiple.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to back on the Main Menu?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        myHandler.removeCallbacksAndMessages(null);
                        stopService(new Intent (DualPlayerMultiple.this, thinkingmusic.class));
                        Intent intent = new Intent(DualPlayerMultiple.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }

}
