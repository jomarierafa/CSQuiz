package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
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

public class DualPlayerVer extends Activity {
    private TextView p1Question, p2Question, txtp1Score, txtp2Score;
    private TextView p1givenWord, p2givenWord, p1correctWord, p2correctWord, p1course, p2course;
    private Button p1Submit, p2Submit;
    private RadioButton p1correct, p1incorrect, p2correct, p2incorrect;
    private List<Question> mQuestionListp1, mQuestionListp2;
    private DatabaseHelper mDBHelper;
    private String p1CorrectAns, p2CorrectAns;
    private String p1choice1, p1choice2, p1choice3, p1choice4, p1tmpAnswr;
    private String p2choice1, p2choice2, p2choice3, p2choice4, p2tmpAnswr;
    private int mQuestionNumberp1 = 0, mQuestionNumberp2 = 0;
    private ProgressBar meter;

    private Timer timers, timer;
    private SoundPlayer sound;
    private int p1Score = 0, p2Score = 0, meterProgress = 50;

    private Animation givenWordAnim, correctAnsAnim, forGlitches;

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
        setContentView(R.layout.activity_dual_player_ver);

        sound = new SoundPlayer(this);
        p1Question = (TextView) findViewById(R.id.p1questionVer);
        p1Submit = (Button) findViewById(R.id.p1submitVer);
        p1correct = (RadioButton) findViewById(R.id.p1correct);
        p1incorrect = (RadioButton) findViewById(R.id.p1incorrect);
        p1givenWord = (TextView) findViewById(R.id.p1givenWord);
        p1correctWord = (TextView) findViewById(R.id.p1correctWord);
        p1course = (TextView) findViewById(R.id.player1course);
        p2course = (TextView) findViewById(R.id.player2course);

        p2Question = (TextView) findViewById(R.id.p2questionVer);
        p2Submit = (Button) findViewById(R.id.p2submitVer);
        p2correct = (RadioButton) findViewById(R.id.p2correct);
        p2incorrect = (RadioButton) findViewById(R.id.p2incorrect);
        p2givenWord = (TextView) findViewById(R.id.p2givenWord);
        p2correctWord = (TextView) findViewById(R.id.p2correctWord);

        txtp1Score = (TextView) findViewById(R.id.p1ScoreViewVer);
        txtp2Score = (TextView) findViewById(R.id.p2ScoreViewVer);

        meter = (ProgressBar) findViewById(R.id.meterVer);
        meter.setProgress(meterProgress);

        mDBHelper = new DatabaseHelper(this);
        mQuestionListp1 = mDBHelper.getListQuestion();
        mQuestionListp2 = mDBHelper.getListQuestion();
        Collections.shuffle(mQuestionListp2);
        updateQuestionP1();
        updateQuestionP2();

        forGlitches = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        forGlitches.setDuration(0);

        ringProgressBar = (RingProgressBar) findViewById(R.id.progressbarVer);
        ringProgressBar.setOnProgressListener(new RingProgressBar.OnProgressListener(){
            @Override
            public void progressToComplete(){
                Intent intent = new Intent(DualPlayerVer.this, DualplayerWinner.class);
                intent.putExtra("p1score", String.valueOf(p1Score));
                intent.putExtra("p2score", String.valueOf(p2Score));
                startActivity(intent);
                finish();
                stopService(new Intent(DualPlayerVer.this, thinkingmusic.class));
                startService(new Intent(DualPlayerVer.this, backgroundmusic.class));
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
        p1givenWord.setTypeface(textFont);
        p2givenWord.setTypeface(textFont);
        p1correctWord.setTypeface(textFont);
        p2correctWord.setTypeface(textFont);
        p1course.setTypeface(textFont);
        p2course.setTypeface(textFont);
        p1correct.setTypeface(textFont);
        p2correct.setTypeface(textFont);
        p1incorrect.setTypeface(textFont);
        p2incorrect.setTypeface(textFont);
        p1Submit.setTypeface(textFont);
        p2Submit.setTypeface(textFont);
    }
    private void correctAnswerAnimationP1(){
        givenWordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        correctAnsAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        givenWordAnim.setStartOffset(600);
        correctAnsAnim.setStartOffset(620);
        p1givenWord.startAnimation(givenWordAnim);
        p1correctWord.startAnimation(correctAnsAnim);
    }
    private void correctAnswerAnimationP2(){
        givenWordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        correctAnsAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        givenWordAnim.setStartOffset(600);
        correctAnsAnim.setStartOffset(620);
        p2givenWord.startAnimation(givenWordAnim);
        p2correctWord.startAnimation(correctAnsAnim);
    }

    private void updateQuestionP1(){
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if(mQuestionNumberp1 < mQuestionListp1.size()) {
            Question s = mQuestionListp1.get(mQuestionNumberp1);
            p1Question.setText(s.getQuestion());
            p1CorrectAns = s.getAnswer();
            p1course.setText(s.getCourse());
            //to make a random choices
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            p1choice1 = s.getChoices((Integer) arr[0]);
            p1choice2 = s.getChoices((Integer) arr[1]);
            p1choice3 = s.getChoices((Integer) arr[2]);
            p1choice4 = s.getChoices((Integer) arr[3]);
            p1correctWord.setText(p1CorrectAns);

            List<String> randomChoices = new ArrayList<>(Arrays.asList(p1choice1, p1CorrectAns, p1choice2, p1choice3, p1CorrectAns, p1choice4));
            Collections.shuffle(randomChoices);
            p1tmpAnswr = randomChoices.get(0);
            p1givenWord.setText(p1tmpAnswr.toUpperCase());
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
            p2CorrectAns = s2.getAnswer();
            p2course.setText(s2.getCourse());
            //to make a random choices
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            p2choice1 = s2.getChoices((Integer) arr[0]);
            p2choice2 = s2.getChoices((Integer) arr[1]);
            p2choice3 = s2.getChoices((Integer) arr[2]);
            p2choice4 = s2.getChoices((Integer) arr[3]);
            p2correctWord.setText(p2CorrectAns);

            List<String> randomChoices = new ArrayList<>(Arrays.asList(p2choice1, p2CorrectAns, p2choice2, p2choice3, p2CorrectAns, p2choice4));
            Collections.shuffle(randomChoices);
            p2tmpAnswr = randomChoices.get(0);
            p2givenWord.setText(p2tmpAnswr.toUpperCase());
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

    public void submitP1(View view) {
        if(p1correct.isChecked()) {
            if (p1tmpAnswr.equalsIgnoreCase(p1CorrectAns)) {
                increment();
                sound.playCorrectSound();
                updateScoreP1(1);
                disabledUserAccessP1();
                p1givenWord.setTextColor(Color.GREEN);
                p1Submit.setBackgroundResource(R.drawable.correctbutton);
            } else {
                correctAnswerAnimationP1();
                sound.playWrongSound();
                disabledUserAccessP1();
                p1givenWord.setTextColor(Color.RED);
                p1Submit.setBackgroundResource(R.drawable.wrongbutton);
            }

        }else{
            if (!p1tmpAnswr.equalsIgnoreCase(p1CorrectAns)) {
                increment();
                correctAnswerAnimationP1();
                sound.playCorrectSound();
                updateScoreP1(1);
                disabledUserAccessP1();
                p1givenWord.setTextColor(Color.RED);
                p1Submit.setBackgroundResource(R.drawable.correctbutton);
            } else {
                sound.playWrongSound();
                disabledUserAccessP1();
                p1givenWord.setTextColor(Color.GREEN);
                p1Submit.setBackgroundResource(R.drawable.wrongbutton);
            }
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DualPlayerVer.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestionP1();
                        resetUserAccessP1();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    public void submitP2(View view) {
        if(p2correct.isChecked()) {
            if (p2tmpAnswr.equalsIgnoreCase(p2CorrectAns)) {
                decrement();
                sound.playCorrectSound();
                updateScoreP2(1);
                disabledUserAccessP2();
                p2givenWord.setTextColor(Color.GREEN);
                p2Submit.setBackgroundResource(R.drawable.correctbutton);
            } else {
                correctAnswerAnimationP2();
                sound.playWrongSound();
                disabledUserAccessP2();
                p2givenWord.setTextColor(Color.RED);
                p2Submit.setBackgroundResource(R.drawable.wrongbutton);
            }

        }else{
            if (!p2tmpAnswr.equalsIgnoreCase(p2CorrectAns)) {
                decrement();
                correctAnswerAnimationP2();
                sound.playCorrectSound();
                updateScoreP2(1);
                disabledUserAccessP2();
                p2givenWord.setTextColor(Color.RED);
                p2Submit.setBackgroundResource(R.drawable.correctbutton);
            } else {
                sound.playWrongSound();
                disabledUserAccessP2();
                p2givenWord.setTextColor(Color.GREEN);
                p2Submit.setBackgroundResource(R.drawable.wrongbutton);
            }
        }

        timers = new Timer();
        timers.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                DualPlayerVer.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestionP2();
                        resetUserAccessP2();
                        timers.cancel();
                        timers.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    public void disabledUserAccessP1(){
        p1Submit.setClickable(false);
        p1correct.setClickable(false);
        p1incorrect.setClickable(false);
    }

    public void disabledUserAccessP2(){
        p2Submit.setClickable(false);
        p2correct.setClickable(false);
        p2incorrect.setClickable(false);
    }

    public void resetUserAccessP1() {
        p1Submit.setBackgroundResource(R.drawable.menubutton);
        p1correct.setChecked(true);
        p1Submit.setClickable(true);
        p1correct.setClickable(true);
        p1incorrect.setClickable(true);
        p1givenWord.setTextColor(Color.WHITE);
        p1correctWord.startAnimation(forGlitches);
        p1givenWord.clearAnimation();

    }
    public void resetUserAccessP2() {
        p2Submit.setBackgroundResource(R.drawable.menubutton);
        p2correct.setChecked(true);
        p2Submit.setClickable(true);
        p2correct.setClickable(true);
        p2incorrect.setClickable(true);
        p2givenWord.setTextColor(Color.WHITE);
        p2correctWord.startAnimation(forGlitches);
        p2givenWord.clearAnimation();

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
            stopService(new Intent(DualPlayerVer.this, thinking.class));
            stopService(new Intent(DualPlayerVer.this, thinkingmusic.class));
            stopService(new Intent(DualPlayerVer.this, backgroundmusic.class));
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
                        stopService(new Intent (DualPlayerVer.this, thinkingmusic.class));
                        Intent intent = new Intent(DualPlayerVer.this, MainActivity.class);
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
