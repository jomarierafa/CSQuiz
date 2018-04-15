package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class VerifyingMode extends Activity {
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private TextView questionView, givenWord, correctWord, questionLeft;
    private RadioButton radIncorrect, radCorrect;
    private Button btnSubmit;
    private ImageButton seeChoices, timerOff ,correct, doubleTry;
    private ImageView timerIcon;
    private LinearLayout lifelines;
    private TextView clock;
    private String tmpAnswr, correctAns , choice1, choice2, choice3, choice4;
    private int score = 0;
    private int  mQuestionNumber = 0;
    public static CountDownTimer countdown;
    private Animation blink, givenWordAnim, correctAnsAnim, forGlitches;

    private SoundPlayer sound;
    private Timer timer;
    private Timer timers;
    private boolean doubleTipStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifying_mode);
        sound = new SoundPlayer(this);
        btnSubmit = (Button) findViewById(R.id.submitVer);
        questionView = (TextView) findViewById(R.id.verQuestion);
        radCorrect = (RadioButton) findViewById(R.id.correct);
        radIncorrect = (RadioButton) findViewById(R.id.incorrect);
        clock = (TextView) findViewById(R.id.verTimer);
        givenWord = (TextView) findViewById(R.id.givenWord);
        correctWord = (TextView) findViewById(R.id.correctWord);
        questionLeft = (TextView) findViewById(R.id.vquestionLeft);
        seeChoices = (ImageButton) findViewById(R.id.seeChoices);
        timerOff = (ImageButton) findViewById(R.id.verTimerOff);
        correct = (ImageButton) findViewById(R.id.verCorrectAns);
        doubleTry = (ImageButton) findViewById(R.id.verDoubleTry);
        timerIcon = (ImageView) findViewById(R.id.vtimerIcon);
        lifelines = (LinearLayout) findViewById(R.id.verLifelines);

        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        forGlitches = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        forGlitches.setDuration(0);


        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        questionView.setTypeface(textFont);
        radCorrect.setTypeface(textFont);
        radIncorrect.setTypeface(textFont);
        givenWord.setTypeface(textFont);
        correctWord.setTypeface(textFont);
        questionLeft.setTypeface(textFont);
        btnSubmit.setTypeface(textFont);

        startService(new Intent(this, thinkingmusic.class));

        startAnimation();
        questionView.getBackground().setAlpha(40);
        if(MainActivity.mode.equals("normalgame")){
            correct.setVisibility(View.VISIBLE);
            timerOff.setVisibility(View.VISIBLE);
            doubleTry.setVisibility(View.VISIBLE);
            seeChoices.setVisibility(View.VISIBLE);
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
                    clock.setText("" + time / 1000);
                }

                @Override
                public void onFinish() {
                    timerIcon.clearAnimation();
                    Toasty.info(VerifyingMode.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    manageButtonsF();
                    quickgame.questionAnswer += 1;
                    if(quickgame.questionAnswer == 5) {
                        Toast.makeText(VerifyingMode.this, "yay", Toast.LENGTH_SHORT).show();
                        checkCorrectCount();
                    }
                    stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                    sound.playWrongSound();
                    disabledUserAccess();
                    if(tmpAnswr.equalsIgnoreCase(correctAns)){
                        givenWord.setTextColor(Color.GREEN);
                    }else{
                        correctAnswerAnimation();
                        givenWord.setTextColor(Color.RED);
                    }
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            startService(new Intent (VerifyingMode.this, thinking.class));
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
                    Toasty.info(VerifyingMode.this, "Times UP!", Toast.LENGTH_SHORT, true).show();
                    clock.setText("");
                    sound.playWrongSound();
                    disabledUserAccess();
                    if(tmpAnswr.equalsIgnoreCase(correctAns)){
                        givenWord.setTextColor(Color.GREEN);
                    }else{
                        correctAnswerAnimation();
                        givenWord.setTextColor(Color.RED);
                    }
                    timers = new Timer();
                    timers.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            VerifyingMode.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    countdown.start();
                                    givenWord.setTextColor(Color.WHITE);
                                    correctWord.startAnimation(forGlitches);
                                    givenWord.clearAnimation();
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

    }
    //animations
    private void startAnimation(){
        Animation fadeFirst = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeSecond = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeThird = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fade4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation moveIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        blink = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        fadeFirst.setDuration(500);
        fadeSecond.setDuration(500);
        fadeThird.setDuration(500);
        fade4th.setDuration(500);
        fadeFirst.setStartOffset(500);
        fadeSecond.setStartOffset(700);
        fadeThird.setStartOffset(900);
        fade4th.setStartOffset(1100);
        moveIn.setStartOffset(1500);
        timerIcon.startAnimation(blink);
        givenWord.startAnimation(fadeFirst);
        radCorrect.startAnimation(fadeSecond);
        radIncorrect.startAnimation(fadeThird);
        btnSubmit.startAnimation(fade4th);
        lifelines.startAnimation(moveIn);
    }
    private void correctAnswerAnimation(){
        givenWordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        correctAnsAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        givenWordAnim.setStartOffset(600);
        correctAnsAnim.setStartOffset(620);
        givenWord.startAnimation(givenWordAnim);
        correctWord.startAnimation(correctAnsAnim);
    }

    private void updateQuestionNormal(){
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        Question s = mQuestionList.get(0);
        questionView.setText(s.getQuestion());
        //to make the choices random
        Collections.shuffle(x);
        Object[] arr = x.toArray();
        choice1 = s.getChoices((Integer) arr[0]);
        choice2 = s.getChoices((Integer) arr[1]);
        choice3 = s.getChoices((Integer) arr[2]);
        choice4 = s.getChoices((Integer) arr[3]);
        correctAns = s.getAnswer();
        correctWord.setText(correctAns);

        List<String> randomChoices = new ArrayList<>(Arrays.asList(choice1, choice2, choice3, choice4, correctAns, correctAns));
        Collections.shuffle(randomChoices);
        tmpAnswr = randomChoices.get(0);
        givenWord.setText(tmpAnswr.toUpperCase());
        mQuestionNumber++;
    }
    //for quickgame or practiceMode
    private void updateQuestionQuick(){
        timerIcon.startAnimation(blink);
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if(mQuestionNumber < 10) {
            Question s = mQuestionList.get(mQuestionNumber);
            questionView.setText(s.getQuestion());
            //to make a random choices
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            choice1 = s.getChoices((Integer) arr[0]);
            choice2 = s.getChoices((Integer) arr[1]);
            choice3 = s.getChoices((Integer) arr[2]);
            choice4 = s.getChoices((Integer) arr[3]);
            correctAns = s.getAnswer();
            correctWord.setText(correctAns);

            List<String> randomChoices = new ArrayList<>(Arrays.asList(choice1, correctAns, choice2, choice3, correctAns, choice4));
            Collections.shuffle(randomChoices);
            tmpAnswr = randomChoices.get(0);
            givenWord.setText(tmpAnswr.toUpperCase());
            mQuestionNumber++;
            questionLeft.setText(mQuestionNumber + "/10");
        }
        else{
            finish();
            managePracticeButton();
            Toast.makeText(this, "That was the last one", Toast.LENGTH_SHORT).show();
            countdown.cancel();
            countdown = null;
            stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
            startService(new Intent (VerifyingMode.this, thinking.class));
        }
    }

    public void submit(View view){
        timerIcon.clearAnimation();
        if(radCorrect.isChecked()){
            if(tmpAnswr.equalsIgnoreCase(correctAns)){
                sound.playCorrectSound();
                if (MainActivity.mode.equals("normalgame")) {
                    manageButtonsT();
                    stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                }else if(MainActivity.mode.equals("quickgame")){
                    correctCount();
                }
                disabledUserAccess();
                givenWord.setTextColor(Color.GREEN);
                Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
            }else{
                correctAnswerAnimation();
                sound.playWrongSound();
                Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
                if(doubleTipStatus){
                    doubleTipStatus = false;
                    countdown.cancel();
                    timerIcon.startAnimation(blink);
                    countdown.start();
                    return;
                }

                if (MainActivity.mode.equals("normalgame")) {
                    manageButtonsF();
                    stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                }
                disabledUserAccess();
                givenWord.setTextColor(Color.RED);
            }
        }else{
            if(!tmpAnswr.equalsIgnoreCase(correctAns)){
                correctAnswerAnimation();
                sound.playCorrectSound();
                if (MainActivity.mode.equals("normalgame")) {
                    manageButtonsT();
                    stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                }else if(MainActivity.mode.equals("quickgame")){
                    correctCount();
                }
                disabledUserAccess();
                givenWord.setTextColor(Color.RED);
                Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
            }else {
                sound.playWrongSound();
                Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
                if (doubleTipStatus) {
                    doubleTipStatus = false;
                    countdown.cancel();
                    timerIcon.startAnimation(blink);
                    countdown.start();
                    return;
                }

                if (MainActivity.mode.equals("normalgame")) {
                    manageButtonsF();
                    stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                }
                disabledUserAccess();
                givenWord.setTextColor(Color.GREEN);
            }
        }

        countdown.cancel();



        timer = new Timer();
        if (MainActivity.mode.equals("normalgame")) {
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    finish();
                    startService(new Intent (VerifyingMode.this, thinking.class));
                    timer.cancel();
                    timer.purge();
                }
            }, 3000, 3000);
        }else if (MainActivity.mode.equals("quickgame")){
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    VerifyingMode.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            givenWord.setTextColor(Color.WHITE);
                            countdown.start();
                            correctWord.startAnimation(forGlitches);
                            givenWord.clearAnimation();
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
        radCorrect.setClickable(false);
        radIncorrect.setClickable(false);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(false);
        }
    }
    public void resetUserAccess(){
        btnSubmit.setClickable(true);
        radCorrect.setClickable(true);
        radIncorrect.setClickable(true);
        seeChoices.setEnabled(true);
        doubleTry.setEnabled(true);
        correct.setEnabled(true);
        timerOff.setEnabled(true);
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

    //lifeline
    public void seeChoices(View view){
        seeChoices.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        seeChoices.setEnabled(false);
        seeChoices.setBackgroundResource(R.drawable.lifelineused);
        quickgame.firstLLStatus = false;
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(VerifyingMode.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);
        TextView possibleAnswer = (TextView) sViewDirection.findViewById(R.id.txtDirection);
        TextView label = (TextView) sViewDirection.findViewById(R.id.txtRequirements);
        label.setText("All Possible Answer");
        possibleAnswer.setText("* " + choice1 + "\n* " + choice2 + "\n* " + choice3 + "\n* " + choice4);
        final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
        dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDirection.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                dialogDirection.cancel();
            }
        });
    }
    public void timerStop(View view){
        timerIcon.clearAnimation();
        countdown.cancel();
        timerOff.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        timerOff.setEnabled(false);
        timerOff.setBackgroundResource(R.drawable.lifelineused);
        quickgame.fourthLLStatus = false;
    }
    public void correctAnswer(View view){
        timerIcon.clearAnimation();
        quickgame.thirdLLStatus = false;
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);
        correct.setEnabled(false);
        stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
        givenWord.setTextColor(Color.GREEN);
        givenWord.setText(correctAns);
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
                    startService(new Intent(VerifyingMode.this, thinking.class));
                    timer.cancel();
                    timer.purge();
                }
            }, 3000, 3000);
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
            seeChoices.setEnabled(false);
            seeChoices.setBackgroundResource(R.drawable.lifelineused);
            seeChoices.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
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
            stopService(new Intent(VerifyingMode.this, thinking.class));
            stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
            stopService(new Intent(VerifyingMode.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }
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
                        stopService(new Intent(VerifyingMode.this, thinkingmusic.class));
                        startService(new Intent(VerifyingMode.this, backgroundmusic.class));
                        Intent intent = new Intent(VerifyingMode.this, MainActivity.class);
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
