package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class FillInTheBlank extends Activity {
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private TextView questionView, shuffleWord, questionLeft;
    private EditText answer;
    private Button btnSubmit;
    private ImageButton firstLetter, timerOff ,correct,doubleTry;
    private ImageView timerIcon;
    private LinearLayout lifelines, userAccess;
    private TextView clock;
    private String correctAns;
    private int score = 0;
    private int  mQuestionNumber = 0;
    public static CountDownTimer countdown;

    private SoundPlayer sound;
    private Timer timer;
    private Timer timers;
    private boolean doubleTipStatus = false, shuffleStatus = true;
    private Animation blink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_in_the_blank);
        sound = new SoundPlayer(this);
        lifelines = (LinearLayout) findViewById(R.id.fillLifelines);
        userAccess = (LinearLayout) findViewById(R.id.userAccess);
        btnSubmit = (Button) findViewById(R.id.submitFill);
        questionView = (TextView) findViewById(R.id.fillQuestion);
        answer = (EditText) findViewById(R.id.fillAnswer);
        clock = (TextView) findViewById(R.id.fillTimer);
        questionLeft = (TextView) findViewById(R.id.fquestionLeft);
        shuffleWord = (TextView) findViewById(R.id.shuffleWord);
        firstLetter = (ImageButton) findViewById(R.id.firstLetter);
        timerOff = (ImageButton) findViewById(R.id.timerOff);
        correct = (ImageButton) findViewById(R.id.correctAns);
        doubleTry = (ImageButton) findViewById(R.id.doubleTry);
        timerIcon = (ImageView) findViewById(R.id.ftimerIcon);

        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        questionView.setTypeface(textFont);
        shuffleWord.setTypeface(textFont);
        questionLeft.setTypeface(textFont);
        btnSubmit.setTypeface(textFont);

        startAnimation();
        questionView.getBackground().setAlpha(40);

        startService(new Intent(this, thinkingmusic.class));

        if(MainActivity.mode.equals("normalgame")){
            correct.setVisibility(View.VISIBLE);
            timerOff.setVisibility(View.VISIBLE);
            doubleTry.setVisibility(View.VISIBLE);
            firstLetter.setVisibility(View.VISIBLE);
            updateQuestionNormal();
        }
        else if(MainActivity.mode.equals("quickgame")){
            updateQuestionQuick();
        }

        //countdown
        if(MainActivity.mode.equals("normalgame")) {
            countdown = new CountDownTimer(46 * 1000, 1000) {
                @Override
                public void onTick(long time) {
                    clock.setText("" + time / 1000);
                }

                @Override
                public void onFinish() {
                    timerIcon.clearAnimation();
                    answer.setBackgroundResource(R.drawable.correctbutton);
                    Toasty.info(FillInTheBlank.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    manageButtonsF();
                    quickgame.questionAnswer += 1;
                    if(quickgame.questionAnswer == 5) {
                        Toast.makeText(FillInTheBlank.this, "yay", Toast.LENGTH_SHORT).show();
                        checkCorrectCount();
                    }
                    stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
                    sound.playWrongSound();
                    disabledUserAccess();
                    answer.setText(correctAns);
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            startService(new Intent (FillInTheBlank.this, thinking.class));
                            timers.cancel();
                            timers.purge();
                        }
                    }, 3000, 3000);
                }
            }.start();

        }else if(MainActivity.mode.equals("quickgame")){
            countdown = new CountDownTimer(46 * 1000, 1000) {
                @Override
                public void onTick(long time) {
                    clock.setText("" + time / 1000);
                }

                @Override
                public void onFinish() {
                    timerIcon.clearAnimation();
                    Toasty.info(FillInTheBlank.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    sound.playWrongSound();
                    disabledUserAccess();
                    answer.setText(correctAns);
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            FillInTheBlank.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdown.start();
                                    updateQuestionQuick();
                                    resetUserAccess();
                                    timers.cancel();
                                    timers.purge();
                                }
                            });
                        }
                    }, 3000, 3000);
                }
            }.start();
        }

        lifelineStatus();
        //para d agad lumabas ang keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void startAnimation(){
        Animation moveIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation moveIn2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        timerIcon.startAnimation(blink);

        moveIn.setStartOffset(500);
        moveIn2nd.setStartOffset(700);
        userAccess.startAnimation(moveIn);
        lifelines.startAnimation(moveIn2nd);
    }

    private void updateQuestionNormal(){
        Question s = mQuestionList.get(0);
        questionView.setText(s.getQuestion());
        correctAns = s.getAnswer();

        String tmpAnswr = correctAns;
        shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
        if(Integer.parseInt(s.getFiblevel()) == 4 && normalgame.gameType.equalsIgnoreCase("rumble")){shuffleWord.setVisibility(View.INVISIBLE);}
        if(Integer.parseInt(s.getFiblevel()) == 5){shuffleWord.setVisibility(View.INVISIBLE);}
    }
    //for quickgame or practiceMode
    private void updateQuestionQuick(){
        timerIcon.startAnimation(blink);
        if(mQuestionNumber < 10) {
            Question s = mQuestionList.get(mQuestionNumber);
            questionView.setText(s.getQuestion());
            correctAns = s.getAnswer();
            String tmpAnswr = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
            mQuestionNumber++;
            questionLeft.setText(mQuestionNumber + "/10");
            if(Integer.parseInt(s.getFiblevel()) > 3){shuffleWord.setVisibility(View.INVISIBLE);}else{shuffleWord.setVisibility(View.VISIBLE);}
        }
        else{
            finish();
            managePracticeButton();
            countdown.cancel();
            countdown = null;
            stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
            startService(new Intent (FillInTheBlank.this, thinking.class));
        }
    }

    public void submit(View view){
        timerIcon.clearAnimation();
        if(answer.getText().toString().isEmpty()){
            Toasty.info(this, "Provide an Answer!", Toast.LENGTH_SHORT, true).show();
            return;
        }

        if(answer.getText().toString().equalsIgnoreCase(correctAns)){
            sound.playCorrectSound();
            if (MainActivity.mode.equals("normalgame")) {
                manageButtonsT();
                stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
            }else if(MainActivity.mode.equals("quickgame")){
                correctCount();
            }
            disabledUserAccess();
            answer.setTextColor(Color.GREEN);
            Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
        }else{
            sound.playWrongSound();
            Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
            if(doubleTipStatus){
                timerIcon.startAnimation(blink);
                doubleTipStatus = false;
                countdown.cancel();
                answer.setText("");
                countdown.start();
                return;
            }

            if (MainActivity.mode.equals("normalgame")) {
                manageButtonsF();
                stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
            }
            disabledUserAccess();
            answer.setTextColor(Color.RED);
            shuffleWord.setText(correctAns);
        }

        countdown.cancel();



        timer = new Timer();
        if (MainActivity.mode.equals("normalgame")) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    finish();
                    startService(new Intent (FillInTheBlank.this, thinking.class));
                    timer.cancel();
                    timer.purge();
                }
            }, 3000, 3000);
        }else if (MainActivity.mode.equals("quickgame")){
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    FillInTheBlank.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            countdown.start();
                            updateQuestionQuick();
                            resetUserAccess();
                            timer.cancel();
                            timer.purge();
                        }
                    });
                }
            }, 3000, 3000);
        }
        if(MainActivity.mode.equals("normalgame")) {
            quickgame.questionAnswer += 1;
            if(quickgame.questionAnswer == 5) {
                checkCorrectCount();
            }
        }
    }

    private void updateScore(int point){
        score = Integer.parseInt(String.valueOf(quickgame.yourScore.getText()));
        score += point;
        quickgame.yourScore.setText(String.valueOf(score));
    }

    private void correctCount(){
        //this is for practice mode
        score += 1;
    }
    public void manageButtonsT(){
        if(quickgame.subjectRow == 0){
            if(quickgame.sub0Level == 1){
                updateScore(50);
                quickgame.qbtn1_1.setClickable(false);
                quickgame.qbtn1_1.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub0Level == 2){
                updateScore(100);
                quickgame.qbtn1_2.setClickable(false);
                quickgame.qbtn1_2.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub0Level == 3){
                updateScore(250);
                quickgame.qbtn1_3.setClickable(false);
                quickgame.qbtn1_3.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub0Level == 4){
                updateScore(500);
                quickgame.qbtn1_4.setClickable(false);
                quickgame.qbtn1_4.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub0Level == 5){
                updateScore(1000);
                quickgame.qbtn1_5.setClickable(false);
                quickgame.qbtn1_5.setBackgroundResource(R.drawable.correctbutton);
            }
        }
        else if (quickgame.subjectRow == 1){
            if(quickgame.sub1Level == 1){
                updateScore(50);
                quickgame.qbtn2_1.setClickable(false);
                quickgame.qbtn2_1.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub1Level == 2){
                updateScore(100);
                quickgame.qbtn2_2.setClickable(false);
                quickgame.qbtn2_2.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub1Level == 3){
                updateScore(250);
                quickgame.qbtn2_3.setClickable(false);
                quickgame.qbtn2_3.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub1Level == 4){
                updateScore(500);
                quickgame.qbtn2_4.setClickable(false);
                quickgame.qbtn2_4.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub1Level == 5){
                updateScore(1000);
                quickgame.qbtn2_5.setClickable(false);
                quickgame.qbtn2_5.setBackgroundResource(R.drawable.correctbutton);
            }
        }
        else if (quickgame.subjectRow == 2){
            if(quickgame.sub2Level == 1){
                updateScore(50);
                quickgame.qbtn3_1.setClickable(false);
                quickgame.qbtn3_1.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub2Level == 2){
                updateScore(100);
                quickgame.qbtn3_2.setClickable(false);
                quickgame.qbtn3_2.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub2Level == 3){
                updateScore(250);
                quickgame.qbtn3_3.setClickable(false);
                quickgame.qbtn3_3.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub2Level == 4){
                updateScore(500);
                quickgame.qbtn3_4.setClickable(false);
                quickgame.qbtn3_4.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub2Level == 5){
                updateScore(1000);
                quickgame.qbtn3_5.setClickable(false);
                quickgame.qbtn3_5.setBackgroundResource(R.drawable.correctbutton);
            }
        }
        else if (quickgame.subjectRow == 3){
            if(quickgame.sub3Level == 1){
                updateScore(50);
                quickgame.qbtn4_1.setClickable(false);
                quickgame.qbtn4_1.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub3Level == 2){
                updateScore(100);
                quickgame.qbtn4_2.setClickable(false);
                quickgame.qbtn4_2.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub3Level == 3){
                updateScore(250);
                quickgame.qbtn4_3.setClickable(false);
                quickgame.qbtn4_3.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub3Level == 4){
                updateScore(500);
                quickgame.qbtn4_4.setClickable(false);
                quickgame.qbtn4_4.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub3Level == 5){
                updateScore(1000);
                quickgame.qbtn4_5.setClickable(false);
                quickgame.qbtn4_5.setBackgroundResource(R.drawable.correctbutton);
            }
        }
        else if (quickgame.subjectRow == 4){
            if(quickgame.sub4Level == 1){
                updateScore(50);
                quickgame.qbtn5_1.setClickable(false);
                quickgame.qbtn5_1.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub4Level == 2){
                updateScore(100);
                quickgame.qbtn5_2.setClickable(false);
                quickgame.qbtn5_2.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub4Level == 3){
                updateScore(250);
                quickgame.qbtn5_3.setClickable(false);
                quickgame.qbtn5_3.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub4Level == 4){
                updateScore(500);
                quickgame.qbtn5_4.setClickable(false);
                quickgame.qbtn5_4.setBackgroundResource(R.drawable.correctbutton);
            }
            else if(quickgame.sub4Level == 5){
                updateScore(1000);
                quickgame.qbtn5_5.setClickable(false);
                quickgame.qbtn5_5.setBackgroundResource(R.drawable.correctbutton);
            }
        }

        quickgame.correctCount += 1;
    }
    public void addSublevel(){
        quickgame.sub0Level += 1;
        quickgame.sub1Level += 1;
        quickgame.sub2Level += 1;
        quickgame.sub3Level += 1;
        quickgame.sub4Level += 1;
    }
    public void checkCorrectCount() {
        switch (quickgame.column) {
            case 1:
                if (quickgame.correctCount == 5) {
                    updateScore(100);
                    quickgame.lvl1.setText("+100");
                }
                if (quickgame.correctCount >= 2) {
                    Toasty.info(this, "Level 2!", Toast.LENGTH_SHORT, true).show();
                    quickgame.qbtn1_2.setEnabled(true);
                    quickgame.qbtn2_2.setEnabled(true);
                    quickgame.qbtn3_2.setEnabled(true);
                    quickgame.qbtn4_2.setEnabled(true);
                    quickgame.qbtn5_2.setEnabled(true);
                    quickgame.qbtn1_2.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn2_2.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn3_2.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn4_2.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn5_2.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn1_2.setText("100");
                    quickgame.qbtn2_2.setText("100");
                    quickgame.qbtn3_2.setText("100");
                    quickgame.qbtn4_2.setText("100");
                    quickgame.qbtn5_2.setText("100");
                    quickgame.column += 1;
                    quickgame.correctCount = 0;
                    quickgame.questionAnswer = 0;
                    addSublevel();
                    quickgame.lvlTexts = "Level 2";
                    quickgame.txtDirection = "Answer 3 or more Available Questions Correctly to go on Level 3";
                    quickgame.timerDirection.start();
                } else {
                    quickgame.timer.start();
                }
                break;

            case 2:
                if (quickgame.correctCount == 5) {
                    updateScore(200);
                    quickgame.lvl2.setText("+200");
                }
                if (quickgame.correctCount >= 3) {
                    Toasty.info(this, "Level 3!", Toast.LENGTH_SHORT, true).show();
                    quickgame.qbtn1_3.setEnabled(true);
                    quickgame.qbtn2_3.setEnabled(true);
                    quickgame.qbtn3_3.setEnabled(true);
                    quickgame.qbtn4_3.setEnabled(true);
                    quickgame.qbtn5_3.setEnabled(true);
                    quickgame.qbtn1_3.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn2_3.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn3_3.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn4_3.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn5_3.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn1_3.setText("250");
                    quickgame.qbtn2_3.setText("250");
                    quickgame.qbtn3_3.setText("250");
                    quickgame.qbtn4_3.setText("250");
                    quickgame.qbtn5_3.setText("250");
                    quickgame.column += 1;
                    quickgame.correctCount = 0;
                    quickgame.questionAnswer = 0;
                    addSublevel();
                    quickgame.lvlTexts = "Level 3";
                    quickgame.txtDirection = "Answer 4 or more Available Questions Correctly to go on Level 4";
                    quickgame.timerDirection.start();
                } else {
                    quickgame.timer.start();
                }
                break;

            case 3:
                if (quickgame.correctCount == 5) {
                    updateScore(500);
                    quickgame.lvl3.setText("+500");
                }
                if (quickgame.correctCount >= 4) {
                    Toasty.info(this, "Level 4", Toast.LENGTH_SHORT, true).show();
                    quickgame.qbtn1_4.setEnabled(true);
                    quickgame.qbtn2_4.setEnabled(true);
                    quickgame.qbtn3_4.setEnabled(true);
                    quickgame.qbtn4_4.setEnabled(true);
                    quickgame.qbtn5_4.setEnabled(true);
                    quickgame.qbtn1_4.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn2_4.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn3_4.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn4_4.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn5_4.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn1_4.setText("500");
                    quickgame.qbtn2_4.setText("500");
                    quickgame.qbtn3_4.setText("500");
                    quickgame.qbtn4_4.setText("500");
                    quickgame.qbtn5_4.setText("500");
                    quickgame.column += 1;
                    quickgame.correctCount = 0;
                    quickgame.questionAnswer = 0;
                    addSublevel();
                    quickgame.lvlTexts = "Level 4";
                    quickgame.txtDirection = "Answer all Available Questions Correctly to go on Level 5";
                    quickgame.timerDirection.start();
                } else {
                    quickgame.timer.start();
                }
                break;

            case 4:
                if (quickgame.correctCount == 5) {
                    updateScore(1000);
                    quickgame.lvl4.setText("+1000");
                }
                if (quickgame.correctCount >= 5) {
                    Toasty.info(this, "Level 5!", Toast.LENGTH_SHORT, true).show();
                    quickgame.qbtn1_5.setEnabled(true);
                    quickgame.qbtn2_5.setEnabled(true);
                    quickgame.qbtn3_5.setEnabled(true);
                    quickgame.qbtn4_5.setEnabled(true);
                    quickgame.qbtn5_5.setEnabled(true);
                    quickgame.qbtn1_5.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn2_5.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn3_5.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn4_5.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn5_5.setBackgroundResource(R.drawable.menubutton);
                    quickgame.qbtn1_5.setText("1000");
                    quickgame.qbtn2_5.setText("1000");
                    quickgame.qbtn3_5.setText("1000");
                    quickgame.qbtn4_5.setText("1000");
                    quickgame.qbtn5_5.setText("1000");
                    quickgame.column += 1;
                    quickgame.correctCount = 0;
                    quickgame.questionAnswer = 0;
                    addSublevel();
                    quickgame.lvlTexts = "Level 5";
                } else {
                    quickgame.timer.start();
                }
                break;
            default:
                if (quickgame.correctCount == 5) {
                    updateScore(200);
                    quickgame.lvl5.setText("+2000");
                }
                Toasty.info(this, "You answered all level!", Toast.LENGTH_SHORT, true).show();
                quickgame.timer.start();
        }

    }
    public  void manageButtonsF(){
        if(quickgame.subjectRow == 0){
            if(quickgame.sub0Level == 1) {
                quickgame.qbtn1_1.setClickable(false);
                quickgame.qbtn1_1.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn1_1.setText("0");
            }
            else if(quickgame.sub0Level == 2){
                quickgame.qbtn1_2.setClickable(false);
                quickgame.qbtn1_2.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn1_2.setText("0");
            }
            else if(quickgame.sub0Level == 3){
                quickgame.qbtn1_3.setClickable(false);
                quickgame.qbtn1_3.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn1_3.setText("0");
            }
            else if(quickgame.sub0Level == 4){
                quickgame.qbtn1_4.setClickable(false);
                quickgame.qbtn1_4.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn1_4.setText("0");
            }
            else if(quickgame.sub0Level == 5){
                quickgame.qbtn1_5.setClickable(false);
                quickgame.qbtn1_5.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn1_5.setText("0");
            }
        }
        else if (quickgame.subjectRow == 1){
            if(quickgame.sub1Level == 1){
                quickgame.qbtn2_1.setClickable(false);
                quickgame.qbtn2_1.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn2_1.setText("0");
            }
            else if(quickgame.sub1Level == 2){
                quickgame.qbtn2_2.setClickable(false);
                quickgame.qbtn2_2.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn2_2.setText("0");
            }
            else if(quickgame.sub1Level == 3){
                quickgame.qbtn2_3.setClickable(false);
                quickgame.qbtn2_3.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn2_3.setText("0");
            }
            else if(quickgame.sub1Level == 4){
                quickgame.qbtn2_4.setClickable(false);
                quickgame.qbtn2_4.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn2_4.setText("0");
            }
            else if(quickgame.sub1Level == 5){
                quickgame.qbtn2_5.setClickable(false);
                quickgame.qbtn2_5.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn2_5.setText("0");
            }
        }
        else if (quickgame.subjectRow == 2){
            if(quickgame.sub2Level == 1){
                quickgame.qbtn3_1.setClickable(false);
                quickgame.qbtn3_1.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn3_1.setText("0");
            }
            else if(quickgame.sub2Level == 2){
                quickgame.qbtn3_2.setClickable(false);
                quickgame.qbtn3_2.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn3_2.setText("0");
            }
            else if(quickgame.sub2Level == 3){
                quickgame.qbtn3_3.setClickable(false);
                quickgame.qbtn3_3.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn3_3.setText("0");
            }
            else if(quickgame.sub2Level == 4){
                quickgame.qbtn3_4.setClickable(false);
                quickgame.qbtn3_4.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn3_4.setText("0");
            }
            else if(quickgame.sub2Level == 5){
                quickgame.qbtn3_5.setClickable(false);
                quickgame.qbtn3_5.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn3_5.setText("0");
            }
        }
        else if (quickgame.subjectRow == 3){
            if(quickgame.sub3Level == 1){
                quickgame.qbtn4_1.setClickable(false);
                quickgame.qbtn4_1.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn4_1.setText("0");
            }
            else if(quickgame.sub3Level == 2){
                quickgame.qbtn4_2.setClickable(false);
                quickgame.qbtn4_2.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn4_2.setText("0");
            }
            else if(quickgame.sub3Level == 3){
                quickgame.qbtn4_3.setClickable(false);
                quickgame.qbtn4_3.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn4_3.setText("0");
            }
            else if(quickgame.sub3Level == 4){
                quickgame.qbtn4_4.setClickable(false);
                quickgame.qbtn4_4.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn4_4.setText("0");
            }
            else if(quickgame.sub3Level == 5){
                quickgame.qbtn4_5.setClickable(false);
                quickgame.qbtn4_5.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn4_5.setText("0");
            }
        }
        else if (quickgame.subjectRow == 4){
            if(quickgame.sub4Level == 1){
                quickgame.qbtn5_1.setClickable(false);
                quickgame.qbtn5_1.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn5_1.setText("0");
            }
            else if(quickgame.sub4Level == 2){
                quickgame.qbtn5_2.setClickable(false);
                quickgame.qbtn5_2.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn5_2.setText("0");
            }
            else if(quickgame.sub4Level == 3){
                quickgame.qbtn5_3.setClickable(false);
                quickgame.qbtn5_3.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn5_3.setText("0");
            }
            else if(quickgame.sub4Level == 4){
                quickgame.qbtn5_4.setClickable(false);
                quickgame.qbtn5_4.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn5_4.setText("0");
            }
            else if(quickgame.sub4Level == 5){
                quickgame.qbtn5_5.setClickable(false);
                quickgame.qbtn5_5.setBackgroundResource(R.drawable.wrongbutton);
                quickgame.qbtn5_5.setText("0");
            }
        }
    }
    public void disabledUserAccess(){
        btnSubmit.setClickable(false);
        answer.setEnabled(false);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(false);
        }
    }
    public void resetUserAccess(){
        btnSubmit.setClickable(true);
        answer.setEnabled(true);
        answer.setText("");
        answer.setTextColor(Color.WHITE);
    }

    //mange all button in quickgame/practiceMode
    public void managePracticeButton(){
        if(quickThree.subjectRow == 1) {
            if(quickThree.click.getText().equals("easy")){
                quickThree.btn1_1.setClickable(false);
                if(score >= 5){
                    quickThree.btn1_1.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn1_1.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn1_1.setText(String.valueOf(score) + " /10");
            }else if(quickThree.click.getText().equals("medium")){
                quickThree.btn1_2.setClickable(false);
                if(score >= 5){
                    quickThree.btn1_2.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn1_2.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn1_2.setText(String.valueOf(score) + " /10");
            }
            else{
                quickThree.btn1_3.setClickable(false);
                if(score >= 5){
                    quickThree.btn1_3.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn1_3.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn1_3.setText(String.valueOf(score) + " /10");
            }
        } else if(quickThree.subjectRow == 2) {
            if(quickThree.click.getText().equals("easy")){
                quickThree.btn2_1.setClickable(false);
                if(score >= 5){
                    quickThree.btn2_1.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn2_1.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn2_1.setText(String.valueOf(score) + " /10");
            }else if(quickThree.click.getText().equals("medium")){
                quickThree.btn2_2.setClickable(false);
                if(score >= 5){
                    quickThree.btn2_2.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn2_2.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn2_2.setText(String.valueOf(score) + " /10");
            }
            else{
                quickThree.btn2_3.setClickable(false);
                if(score >= 5){
                    quickThree.btn2_3.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn2_3.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn2_3.setText(String.valueOf(score) + " /10");
            }
        } else {
            if(quickThree.click.getText().equals("easy")){
                quickThree.btn3_1.setClickable(false);
                if(score >= 5){
                    quickThree.btn3_1.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn3_1.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn3_1.setText(String.valueOf(score) + " /10");
            }else if(quickThree.click.getText().equals("medium")){
                quickThree.btn3_2.setClickable(false);
                if(score >= 5){
                    quickThree.btn3_2.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn3_2.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn3_2.setText(String.valueOf(score) + " /10");
            }
            else{
                quickThree.btn3_3.setClickable(false);
                if(score >= 5){
                    quickThree.btn3_3.setBackgroundResource(R.drawable.correctbutton);
                } else{
                    quickThree.btn3_3.setBackgroundResource(R.drawable.wrongbutton);
                }
                quickThree.btn3_3.setText(String.valueOf(score) + " /10");
            }
        }
    }



    public static String shuffleString(String string) {
        String[] words = string.split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            List<Character> letters = new ArrayList<Character>();
            for (char letter : word.toCharArray()) {
                letters.add(letter);
            }
            if (letters.size() > 2) {
                Collections.shuffle(letters);
            }
            for (char letter : letters) {
                builder.append(letter);
            }
            builder.append(" ");
        }
        return builder.toString();
    }

    //lifeline
    public void timerStop(View view){
        timerIcon.clearAnimation();
        countdown.cancel();
        timerOff.setEnabled(false);
        timerOff.setBackgroundResource(R.drawable.lifelineused);
        quickgame.fourthLLStatus = false;
    }
    public void correctAnswer(View view){
        timerIcon.clearAnimation();
        quickgame.thirdLLStatus = false;
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);
        stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
        answer.setBackgroundResource(R.drawable.correctbutton);
        answer.setText(correctAns);
        disabledUserAccess();
        manageButtonsT();
        quickgame.questionAnswer += 1;
        if(quickgame.questionAnswer == 5) {
            checkCorrectCount();
        }
        countdown.cancel();

        timer = new Timer();
        if (MainActivity.mode.equals("normalgame")) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    finish();
                    startService(new Intent(FillInTheBlank.this, thinking.class));
                    timer.cancel();
                    timer.purge();
                }
            }, 3000, 3000);
        }
    }

    public void firstLetter(View view){
        char firstChar = correctAns.charAt(0);
        answer.setText(String.valueOf(firstChar));
        quickgame.firstLLStatus = false;
        firstLetter.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        firstLetter.setEnabled(false);
        firstLetter.setBackgroundResource(R.drawable.lifelineused);
    }
    public void shuffle(View view){
        if(shuffleStatus) {
            String tmpAnswr = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
            shuffleStatus = false;
        }else{
            Toasty.info(this, "Already Shuffled!", Toast.LENGTH_SHORT, true).show();
        }
    }
    public void doubleTry(View view){
        doubleTry.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTry.setBackgroundResource(R.drawable.lifelineused);
        doubleTipStatus = true;
        quickgame.secondLLStatus = false;
        doubleTry.setEnabled(false);
    }

    public void lifelineStatus(){
        if(!quickgame.firstLLStatus){
            firstLetter.setEnabled(false);
            firstLetter.setBackgroundResource(R.drawable.lifelineused);
            firstLetter.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.thirdLLStatus){
            correct.setEnabled(false);
            correct.setBackgroundResource(R.drawable.lifelineused);
            correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.secondLLStatus){
            doubleTry.setEnabled(false);
            doubleTry.setBackgroundResource(R.drawable.lifelineused);
            doubleTry.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.fourthLLStatus){
            timerOff.setEnabled(false);
            timerOff.setBackgroundResource(R.drawable.lifelineused);
            timerOff.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }

    }

    //back button of android
    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(FillInTheBlank.this, thinking.class));
            stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
            stopService(new Intent(FillInTheBlank.this, backgroundmusic.class));
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
                        countdown.cancel();
                        if(MainActivity.mode.equals("normalgame")) {
                            quickgame.quickG.finish();
                        }else if(MainActivity.mode.equals("quickgame")){
                            quickThree.quick3.finish();
                        }
                        finish();
                        stopService(new Intent(FillInTheBlank.this, thinkingmusic.class));
                        startService(new Intent(FillInTheBlank.this, backgroundmusic.class));
                        Intent intent = new Intent(FillInTheBlank.this, MainActivity.class);
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
