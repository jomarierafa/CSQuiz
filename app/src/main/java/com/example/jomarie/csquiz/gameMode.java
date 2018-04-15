package com.example.jomarie.csquiz;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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


public class gameMode extends Activity {
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private TextView questionView, questionLeft;
    private Button btnChoice1, btnChoice2, btnChoice3, btnChoice4;
    private ImageButton half,correct,doubleTip,timerOff;
    private ImageView timerIcon;
    private LinearLayout vwLifeline;
    private TextView clock;
    private String correctAns;
    private int score = 0;
    private int  mQuestionNumber = 0;
    public static CountDownTimer countdown;
    private Animation blink;

    private Timer timer;
    private Timer timers;

    private SoundPlayer sound;
    private boolean doubleTipStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_mode);
        sound = new SoundPlayer(this);
        questionView = (TextView) findViewById(R.id.question);
        btnChoice1 = (Button) findViewById(R.id.choice1);
        btnChoice2 = (Button) findViewById(R.id.choice2);
        btnChoice3 = (Button) findViewById(R.id.choice3);
        btnChoice4 = (Button) findViewById(R.id.choice4);
        half = (ImageButton) findViewById(R.id.half);
        doubleTip = (ImageButton) findViewById(R.id.doubleTip);
        timerOff = (ImageButton) findViewById(R.id.mTimerOff);
        correct = (ImageButton) findViewById(R.id.correctAns);
        timerIcon = (ImageView) findViewById(R.id.timerIcon);
        clock = (TextView) findViewById(R.id.timer);
        questionLeft = (TextView) findViewById(R.id.questionLeft);
        vwLifeline = (LinearLayout) findViewById(R.id.viewLifeline);
        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        questionView.setTypeface(textFont);
        btnChoice1.setTypeface(textFont);
        btnChoice2.setTypeface(textFont);
        btnChoice3.setTypeface(textFont);
        btnChoice4.setTypeface(textFont);
        questionLeft.setTypeface(textFont);

        Animation();
        questionView.getBackground().setAlpha(40);

        startService(new Intent(this, thinkingmusic.class));

        if(MainActivity.mode.equals("normalgame")){
            correct.setVisibility(View.VISIBLE);
            timerOff.setVisibility(View.VISIBLE);
            doubleTip.setVisibility(View.VISIBLE);
            half.setVisibility(View.VISIBLE);
            updateQuestionNormal();
        }
        else if(MainActivity.mode.equals("quickgame")){
            updateQuestionQuick();
        }

        //countdown
        if(MainActivity.mode.equals("normalgame")) {
            countdown = new CountDownTimer(31 * 1000, 1000) {
                @Override
                public void onTick(long time) {
                    clock.setText(" " + time / 1000);
                }

                @Override
                public void onFinish() {
                    timerIcon.clearAnimation();
                    Toasty.info(gameMode.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    manageButtonsF();
                    quickgame.questionAnswer += 1;
                    if(quickgame.questionAnswer == 5) {
                        Toast.makeText(gameMode.this, "yay", Toast.LENGTH_SHORT).show();
                        checkCorrectCount();
                    }
                    stopService(new Intent(gameMode.this, thinkingmusic.class));
                    sound.playWrongSound();
                    disabledButtonChoice();
                    viewCorrectAns();
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            startService(new Intent (gameMode.this, thinking.class));
                            timers.cancel();
                            timers.purge();
                        }
                    }, 3000, 3000);
                }
            }.start();
        }else if(MainActivity.mode.equals("quickgame")){
            countdown = new CountDownTimer(31 * 1000, 1000) {
                @Override
                public void onTick(long time) {
                    clock.setText("" + time / 1000);
                }

                @Override
                public void onFinish() {
                    timerIcon.clearAnimation();
                    Toasty.info(gameMode.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    sound.playWrongSound();
                    disabledButtonChoice();
                    viewCorrectAns();
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            gameMode.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdown.start();
                                    updateQuestionQuick();
                                    resetChoice();
                                    timers.cancel();
                                    timers.purge();
                                    }
                                });
                        }
                    }, 3000, 3000);
                }
            }.start();
        }

        //if the lifeline is use
        lifelineStatus();


    }

    public void Animation(){
        Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        move.setStartOffset(500);
        move2nd.setStartOffset(700);
        move3rd.setStartOffset(900);
        move4th.setStartOffset(1100);
        move5th.setStartOffset(1300);
        btnChoice1.startAnimation(move);
        btnChoice2.startAnimation(move2nd);
        btnChoice3.startAnimation(move3rd);
        btnChoice4.startAnimation(move4th);
        vwLifeline.startAnimation(move5th);
        timerIcon.startAnimation(blink);
    }

    //for normal game
    private void updateQuestionNormal(){
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        Question s = mQuestionList.get(0);
        questionView.setText(s.getQuestion());
        //for shuffle the choices
        Collections.shuffle(x);
        Object[] arr = x.toArray();
        btnChoice1.setText(s.getChoices((Integer) arr[0]));
        btnChoice2.setText(s.getChoices((Integer) arr[1]));
        btnChoice3.setText(s.getChoices((Integer) arr[2]));
        btnChoice4.setText(s.getChoices((Integer) arr[3]));

        correctAns = s.getAnswer();
    }
    //for quickgame or practiceMode
    private void updateQuestionQuick(){
        timerIcon.startAnimation(blink);
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if(mQuestionNumber < 10) {
            Question s = mQuestionList.get(mQuestionNumber);
            questionView.setText(s.getQuestion());
            //for shuffle the choices
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            btnChoice1.setText(s.getChoices((Integer) arr[0]));
            btnChoice2.setText(s.getChoices((Integer) arr[1]));
            btnChoice3.setText(s.getChoices((Integer) arr[2]));
            btnChoice4.setText(s.getChoices((Integer) arr[3]));
            correctAns = s.getAnswer();
            mQuestionNumber++;
            questionLeft.setText(mQuestionNumber + "/10");
        }
        else{
            finish();
            managePracticeButton();
            countdown.cancel();
            countdown = null;
            stopService(new Intent(gameMode.this, thinkingmusic.class));
            startService(new Intent (gameMode.this, thinking.class));
        }
    }
    //choosing answer
    public void choose(View view) {
        timerIcon.clearAnimation();
        Button answer = (Button) view;
        if (answer.getText().equals(correctAns)) {
            sound.playCorrectSound();
            if (MainActivity.mode.equals("normalgame")) {
                manageButtonsT();
                stopService(new Intent(gameMode.this, thinkingmusic.class));
            }else if(MainActivity.mode.equals("quickgame")){
                correctCount();
            }

            disabledButtonChoice();
            answer.setBackgroundResource(R.drawable.correctbutton);
            Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
        } else {
            sound.playWrongSound();
            Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
            if(doubleTipStatus){
                timerIcon.startAnimation(blink);
                doubleTipStatus = false;
                countdown.cancel();
                answer.setEnabled(false);
                countdown.start();
                return;
            }
            if (MainActivity.mode.equals("normalgame")) {
                manageButtonsF();
                stopService(new Intent(gameMode.this, thinkingmusic.class));
            }
            disabledButtonChoice();
            answer.setBackgroundResource(R.drawable.wrongbutton);
            viewCorrectAns();
        }
        countdown.cancel();




        timer = new Timer();
        if (MainActivity.mode.equals("normalgame")) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        finish();
                        startService(new Intent (gameMode.this, thinking.class));
                        timer.cancel();
                        timer.purge();
                }
            }, 3000, 3000);
        }else if (MainActivity.mode.equals("quickgame")){
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                        gameMode.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                countdown.start();;
                                updateQuestionQuick();
                                resetChoice();
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

    // manage all buttons if the answer is Correct and Incorrect in activity quickgame.java
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
    //manage all buttons in gameMode.java
    public void disabledButtonChoice(){
        btnChoice1.setClickable(false);
        btnChoice2.setClickable(false);
        btnChoice3.setClickable(false);
        btnChoice4.setClickable(false);
        for ( int i = 0; i < vwLifeline.getChildCount();  i++ ){
            View view = vwLifeline.getChildAt(i);
            view.setEnabled(false);
        }
    }
    public void viewCorrectAns(){
        if(correctAns.equals(btnChoice1.getText())){btnChoice1.setBackgroundResource(R.drawable.correctbutton);}
        else if(correctAns.equals(btnChoice2.getText())){btnChoice2.setBackgroundResource(R.drawable.correctbutton);}
        else if(correctAns.equals(btnChoice3.getText())){btnChoice3.setBackgroundResource(R.drawable.correctbutton);}
        else {btnChoice4.setBackgroundResource(R.drawable.correctbutton);}
    }
    public void resetChoice(){
        btnChoice1.setClickable(true);
        btnChoice2.setClickable(true);
        btnChoice3.setClickable(true);
        btnChoice4.setClickable(true);
        btnChoice1.setBackgroundResource(R.drawable.menubutton);
        btnChoice2.setBackgroundResource(R.drawable.menubutton);
        btnChoice3.setBackgroundResource(R.drawable.menubutton);
        btnChoice4.setBackgroundResource(R.drawable.menubutton);
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


    public void stopTimer() {
        if(countdown != null) {
            countdown.cancel();
        }
    }

    //Lifeline
    public void twoChoices(View view){
        half.setBackgroundResource(R.drawable.lifelineused);
        half.setEnabled(false);
        half.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        quickgame.secondLLStatus = false;
        btnChoice1.setEnabled(false);
        btnChoice2.setEnabled(false);
        btnChoice3.setEnabled(false);
        btnChoice4.setEnabled(false);
        List<Integer> a = new ArrayList<>(Arrays.asList(0,1,2));
        Collections.shuffle(a);
        Object[] arr = a.toArray();
        if(correctAns.equals(btnChoice1.getText())){
            btnChoice1.setEnabled(true);
            if(arr[0].equals(0)){
                btnChoice2.setEnabled(true);
            }else if(arr[1].equals(1)){
                btnChoice3.setEnabled(true);
            }else{
                btnChoice4.setEnabled(true);
            }
        }
        else if(correctAns.equals(btnChoice2.getText())){
            btnChoice2.setEnabled(true);
            if(arr[0].equals(0)){
                btnChoice1.setEnabled(true);
            }else if(arr[1].equals(1)){
                btnChoice3.setEnabled(true);
            }else{
                btnChoice4.setEnabled(true);
            }
        }
        else if(correctAns.equals(btnChoice3.getText())){
            btnChoice3.setEnabled(true);
            if(arr[0].equals(0)){
                btnChoice2.setEnabled(true);
            }else if(arr[1].equals(1)){
                btnChoice1.setEnabled(true);
            }else{
                btnChoice4.setEnabled(true);
            }
        }
        else {
            btnChoice4.setEnabled(true);
            if(arr[0].equals(0)){
                btnChoice2.setEnabled(true);
            }else if(arr[1].equals(1)){
                btnChoice3.setEnabled(true);
            }else{
                btnChoice1.setEnabled(true);
            }
        }
    }
    public void corectAns(View view){
        timerIcon.clearAnimation();
        correct.setBackgroundResource(R.drawable.lifelineused);
        stopService(new Intent(gameMode.this, thinkingmusic.class));
        disabledButtonChoice();
        half.setEnabled(false);
        quickgame.thirdLLStatus = false;
        correct.setEnabled(false);
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTip.setEnabled(false);
        manageButtonsT();
        quickgame.questionAnswer += 1;
        if(quickgame.questionAnswer == 5) {
            checkCorrectCount();
        }
        if(correctAns.equals(btnChoice1.getText())){btnChoice1.setBackgroundResource(R.drawable.correctbutton);}
        else if(correctAns.equals(btnChoice2.getText())){btnChoice2.setBackgroundResource(R.drawable.correctbutton);}
        else if(correctAns.equals(btnChoice3.getText())){btnChoice3.setBackgroundResource(R.drawable.correctbutton);}
        else {btnChoice4.setBackgroundResource(R.drawable.correctbutton);}
        countdown.cancel();

        timer = new Timer();
        if (MainActivity.mode.equals("normalgame")) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    finish();
                    startService(new Intent(gameMode.this, thinking.class));
                    timer.cancel();
                    timer.purge();
                }
            }, 3000, 3000);
        }

    }
    public void doubleTip(View view){
        doubleTip.setBackgroundResource(R.drawable.lifelineused);
        doubleTipStatus = true;
        quickgame.firstLLStatus = false;
        doubleTip.setEnabled(false);
        doubleTip.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
    }
    public void lifelineStatus(){
        if(!quickgame.firstLLStatus){
            doubleTip.setEnabled(false);
            doubleTip.setBackgroundResource(R.drawable.lifelineused);
            doubleTip.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.thirdLLStatus){
            correct.setEnabled(false);
            correct.setBackgroundResource(R.drawable.lifelineused);
            correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.secondLLStatus){
            half.setEnabled(false);
            half.setBackgroundResource(R.drawable.lifelineused);
            half.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
        if(!quickgame.fourthLLStatus){
            timerOff.setEnabled(false);
            timerOff.setBackgroundResource(R.drawable.lifelineused);
            timerOff.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        }
    }

    public void mTimerOff(View view){
        timerIcon.clearAnimation();
        countdown.cancel();
        timerOff.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        timerOff.setEnabled(false);
        timerOff.setBackgroundResource(R.drawable.lifelineused);
        quickgame.fourthLLStatus = false;
    }

    //back button of android
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
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
                        stopService(new Intent(gameMode.this, thinkingmusic.class));
                        startService(new Intent(gameMode.this, backgroundmusic.class));
                        Intent intent = new Intent(gameMode.this, MainActivity.class);
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
