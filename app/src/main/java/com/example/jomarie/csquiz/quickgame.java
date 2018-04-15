package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jomarie.csquiz.model.Leaderboardmodel;
import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import es.dmoral.toasty.Toasty;


public class quickgame extends Activity{
    private SoundPlayer sound;
    public  static int score, correctCount, column, questionAnswer;
    public static  String subject;
    public static int subjectRow;
    public static int sub0Level = 1, sub1Level = 1, sub2Level = 1, sub3Level = 1, sub4Level = 1;
    public static boolean firstLLStatus, secondLLStatus, thirdLLStatus, fourthLLStatus;
    ArrayList<String> list;

    public static Button qbtn1_1 , qbtn1_2, qbtn1_3, qbtn1_4, qbtn1_5;
    public static Button qbtn2_1 , qbtn2_2, qbtn2_3, qbtn2_4, qbtn2_5;
    public static Button qbtn3_1 , qbtn3_2, qbtn3_3, qbtn3_4, qbtn3_5;
    public static Button qbtn4_1 , qbtn4_2, qbtn4_3, qbtn4_4, qbtn4_5;
    public static Button qbtn5_1 , qbtn5_2, qbtn5_3, qbtn5_4, qbtn5_5;
    public static TextView yourScore, lvl1, lvl2, lvl3, lvl4, lvl5;
    public static String txtDirection;
    public static String lvlTexts;

    public static TextView sub1, sub2, sub3, sub4, sub5;

    public static Activity quickG;

    public static Countdown timer;
    public static CountdownDirection timerDirection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sound = new SoundPlayer(this);
        setContentView(R.layout.activity_quickgame);
        AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if(!manager.isMusicActive()) {
            startService(new Intent(this, thinking.class));
        }
        questionAnswer = 0;
        column = 1;
        correctCount = 0;
        quickG = this;
        qbtn1_1 = (Button) findViewById(R.id.button10);
        qbtn1_2 = (Button) findViewById(R.id.button27);
        qbtn1_3 = (Button) findViewById(R.id.button28);
        qbtn1_4 = (Button) findViewById(R.id.button13);
        qbtn1_5 = (Button) findViewById(R.id.button20);
        qbtn2_1 = (Button) findViewById(R.id.button9);
        qbtn2_2 = (Button) findViewById(R.id.button26);
        qbtn2_3 = (Button) findViewById(R.id.button30);
        qbtn2_4 = (Button) findViewById(R.id.button14);
        qbtn2_5 = (Button) findViewById(R.id.button21);
        qbtn3_1 = (Button) findViewById(R.id.button11);
        qbtn3_2 = (Button) findViewById(R.id.button25);
        qbtn3_3 = (Button) findViewById(R.id.button31);
        qbtn3_4 = (Button) findViewById(R.id.button17);
        qbtn3_5 = (Button) findViewById(R.id.button22);
        qbtn4_1 = (Button) findViewById(R.id.button12);
        qbtn4_2 = (Button) findViewById(R.id.button24);
        qbtn4_3 = (Button) findViewById(R.id.button15);
        qbtn4_4 = (Button) findViewById(R.id.button18);
        qbtn4_5 = (Button) findViewById(R.id.button29);
        qbtn5_1 = (Button) findViewById(R.id.button8);
        qbtn5_2 = (Button) findViewById(R.id.button23);
        qbtn5_3 = (Button) findViewById(R.id.button16);
        qbtn5_4 = (Button) findViewById(R.id.button19);
        qbtn5_5 = (Button) findViewById(R.id.button32);
        lvl1 = (TextView) findViewById(R.id.lvl1);
        lvl2 = (TextView) findViewById(R.id.lvl2);
        lvl3 = (TextView) findViewById(R.id.lvl3);
        lvl4 = (TextView) findViewById(R.id.lvl4);
        lvl5 = (TextView) findViewById(R.id.lvl5);

        firstLLStatus = true;
        secondLLStatus = true;
        thirdLLStatus = true;
        fourthLLStatus = true;

        list = getIntent().getStringArrayListExtra("listOfsubjects");
        sub1 = (TextView) findViewById(R.id.textView);
        sub2 = (TextView) findViewById(R.id.textView2);
        sub3 = (TextView) findViewById(R.id.textView3);
        sub4 = (TextView) findViewById(R.id.textView4);
        sub5 = (TextView) findViewById(R.id.textView5);
        yourScore = (TextView) findViewById(R.id.textView6);
        sub1.setText(list.get(0));
        sub2.setText(list.get(1));
        sub3.setText(list.get(2));
        sub4.setText(list.get(3));
        sub5.setText(list.get(4));

        yourScore.setText(String.valueOf(score));

        timer = new Countdown(5000, 3000);
        timerDirection = new CountdownDirection(4000, 3000);
        //show direction
        txtDirection = "Answer 2 or more Question Correctly to go on level 2";
        lvlTexts = "Level 1";
        showDirection();

        setFont();
    }

    public  void setFont(){
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        lvl1.setTypeface(textFont);
        lvl2.setTypeface(textFont);
        lvl3.setTypeface(textFont);
        lvl4.setTypeface(textFont);
        lvl5.setTypeface(textFont);
        sub1.setTypeface(textFont);
        sub2.setTypeface(textFont);
        sub3.setTypeface(textFont);
        sub4.setTypeface(textFont);
        sub5.setTypeface(textFont);
    }

    public class Countdown extends CountDownTimer {

        public Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millis) {

        }

        @Override
        public void onFinish() {
            Toasty.Config.getInstance().setInfoColor(Color.RED).apply();
            Toasty.info(quickgame.this, "Game Over!", Toast.LENGTH_SHORT, true).show();
            android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(quickgame.this);
            View sView = getLayoutInflater().inflate(R.layout.dialog_score, null);
            final EditText player = (EditText) sView.findViewById(R.id.PlayerName);
            Button submit = (Button) sView.findViewById(R.id.btn_Submit);
            Button cancel = (Button) sView.findViewById(R.id.btn_SubmitNot);
            TextView scoreView = (TextView) sView.findViewById(R.id.scoreMessage);
            scoreView.setText("You Got " + yourScore.getText() + " Points");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    sound.playButtonSound();
                    if (!player.getText().toString().isEmpty()){
                        ArrayList<Leaderboardmodel> mLeaderboardlist = new ArrayList<>();
                        SharedPreferences appSharedPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        Gson gson = new Gson();
                        String tmp = "";
                        if(normalgame.gameType.equalsIgnoreCase("multiple")){
                            tmp = "MyObject";
                        }else if(normalgame.gameType.equalsIgnoreCase("rumble")){
                            tmp = "fib";
                        }else if(normalgame.gameType.equalsIgnoreCase("verifying")) {
                            tmp = "verifying";
                        } else{
                            tmp = "allTypes";
                        }
                        String json = appSharedPrefs.getString(tmp, "");
                        if(json.isEmpty()){
                            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
                        }else {
                            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
                            }.getType();
                            mLeaderboardlist = gson.fromJson(json, type);
                        }
                        String date = DateFormat.getDateTimeInstance().format(new Date());
                        Leaderboardmodel s = new Leaderboardmodel();
                        s.setDate(date);
                        s.setName(player.getText().toString());
                        s.setScore(Integer.parseInt(String.valueOf(yourScore.getText())));
                        mLeaderboardlist.add(s);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson newgson = new Gson();
                        String newjson = newgson.toJson(mLeaderboardlist);
                        prefsEditor.putString(tmp, newjson);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        finish();
                        stopService(new Intent (quickgame.this, thinking.class));
                        Intent intent = new Intent(quickgame.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toasty.info(quickgame.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            sBuilder.setView(sView);
            android.support.v7.app.AlertDialog dialog = sBuilder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sound.playButtonSound();
                    stopService(new Intent (quickgame.this, thinking.class));
                    finish();
                    Intent intent = new Intent(quickgame.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
    public class CountdownDirection extends CountDownTimer {

        public CountdownDirection(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millis) {}
        @Override
        public void onFinish() {
           showDirection();
        }
    }

    public void showDirection(){
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(quickgame.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);
        TextView txtRequirement = (TextView) sViewDirection.findViewById(R.id.txtDirection);
        TextView lvlText = (TextView) sViewDirection.findViewById(R.id.txtRequirements);
        lvlText.setText(lvlTexts);
        txtRequirement.setText(txtDirection);

        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        txtRequirement.setTypeface(textFont);
        lvlText.setTypeface(textFont);

        final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
        dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDirection.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                dialogDirection.cancel();
            }
        });
    }

    public void sendIntent(){
        sound.playButtonSound();
        if (normalgame.gameType.equalsIgnoreCase("multiple")) {
            Intent intent = new Intent(this, gameMode.class);
            startActivity(intent);
        }else if(normalgame.gameType.equalsIgnoreCase("rumble")){
            Intent intent = new Intent(this, FillInTheBlank.class);
            startActivity(intent);
        }else if(normalgame.gameType.equalsIgnoreCase("verifying")){
            Intent intent = new Intent(this, VerifyingMode.class);
            startActivity(intent);
        } else if(normalgame.gameType.equalsIgnoreCase("random")){
            if(sub0Level == 1){
                Intent intent = new Intent(this, VerifyingMode.class);
                startActivity(intent);
            }else if(sub0Level == 2 || sub0Level == 3){
                Intent intent = new Intent(this, gameMode.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, FillInTheBlank.class);
                startActivity(intent);
            }
        }

    }

    //click listener of every button in row by row
    public void subject1(View view){
        stopService(new Intent (quickgame.this, thinking.class));
        subjectRow = 0;
        subject = list.get(0);
        sendIntent();
    }
    public void subject2(View view){
        stopService(new Intent (quickgame.this, thinking.class));
        subjectRow = 1;
        subject = list.get(1);
        sendIntent();
    }
    public  void subject3(View view){
        stopService(new Intent (quickgame.this, thinking.class));
        subjectRow = 2;
        subject = list.get(2);
        sendIntent();
    }
    public void subject4(View view){
        stopService(new Intent (quickgame.this, thinking.class));
        subjectRow = 3;
        subject = list.get(3);
        sendIntent();
    }
    public  void subject5(View view){
        stopService(new Intent (quickgame.this, thinking.class));
        subjectRow = 4;
        subject = list.get(4);
        sendIntent();
    }




    //back button of android

    public void quitSurivial(View view){
        sound.playButtonSound();
        exitByBackKey();
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
                        gameMode stop = new gameMode();
                        stop.stopTimer();
                        finish();
                        stopService(new Intent (quickgame.this, thinking.class));
                        startService(new Intent(quickgame.this, backgroundmusic.class));
                        Intent intent = new Intent(quickgame.this, MainActivity.class);
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
