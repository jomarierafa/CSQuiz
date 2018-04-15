package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;


import java.util.Collections;
import java.util.List;

public class quickThree extends Activity{
    List<String> x;
    private SoundPlayer sound;
    public static int subjectRow;
    public static String subject;
    public static int firstLevel;
    public static int secondLevel;
    public static Button click;

    public static Button btn1_1 , btn1_2, btn1_3;
    public static Button btn2_1 , btn2_2, btn2_3;
    public static Button btn3_1 , btn3_2, btn3_3;
    public static Activity quick3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_three);
        sound = new SoundPlayer(this);
        quick3 = this;
        gameMode stop = new gameMode();
        stop.stopTimer();

        AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if(!manager.isMusicActive()) {
            startService(new Intent(this, thinking.class));
        }

        btn1_1 = (Button) findViewById(R.id.btn1_1);
        btn1_2 = (Button) findViewById(R.id.btn1_2);
        btn1_3 = (Button) findViewById(R.id.btn1_3);
        btn2_1 = (Button) findViewById(R.id.btn2_1);
        btn2_2 = (Button) findViewById(R.id.btn2_2);
        btn2_3 = (Button) findViewById(R.id.btn2_3);
        btn3_1 = (Button) findViewById(R.id.btn3_1);
        btn3_2 = (Button) findViewById(R.id.btn3_2);
        btn3_3 = (Button) findViewById(R.id.btn3_3);

        Typeface font = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        btn1_1.setTypeface(font);
        btn1_2.setTypeface(font);
        btn1_3.setTypeface(font);
        btn2_1.setTypeface(font);
        btn2_2.setTypeface(font);
        btn2_3.setTypeface(font);
        btn3_1.setTypeface(font);
        btn3_2.setTypeface(font);
        btn3_3.setTypeface(font);


        x = getIntent().getStringArrayListExtra("listOfsubjects");
        Collections.shuffle(x);

        TextView sub1 = (TextView) findViewById(R.id.textView7);
        TextView sub2 = (TextView) findViewById(R.id.textView9);
        TextView sub3 = (TextView) findViewById(R.id.textView8);

        sub1.setText(x.get(0));sub1.setTypeface(font);
        sub2.setText(x.get(1));sub2.setTypeface(font);
        sub3.setText(x.get(2));sub3.setTypeface(font);

        showDirection();
    }


    public void sendInten() {
        stopService(new Intent(quickThree.this, thinking.class));
        if(normalgame.gameType == "multiple") {
            Intent intent = new Intent(this, gameMode.class);
            startActivity(intent);
        } else if(normalgame.gameType == "rumble"){
            Intent intent = new Intent(this, FillInTheBlank.class);
            startActivity(intent);
        } else if(normalgame.gameType.equalsIgnoreCase("verifying")){
            Intent intent = new Intent(this, VerifyingMode.class);
            startActivity(intent);
        }
    }


    public void sub1(View view){
        sound.playButtonSound();
        click = (Button) view;
        level();
        subjectRow = 1;
        subject = x.get(0);
        sendInten();
    }
    public void sub2(View view){
        sound.playButtonSound();
        click = (Button) view;
        level();
        subjectRow = 2;
        subject = x.get(1);
        sendInten();
    }
    public void sub3(View view){
        sound.playButtonSound();
        click = (Button) view;
        level();
        subjectRow = 3;
        subject = x.get(2);
        sendInten();
    }

    public void level(){
        if(click.getText().equals("easy")){
            firstLevel = 1;
            secondLevel = 2;
        }
        else if(click.getText().equals("medium")){
            firstLevel = 3;
            secondLevel = 4;
        }
        else if(click.getText().equals("hard")){
            firstLevel = 5;
            secondLevel = 5;
        }
    }

    public void showDirection(){
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(quickThree.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);
        TextView txtRequirement = (TextView) sViewDirection.findViewById(R.id.txtDirection);
        TextView lvlText = (TextView) sViewDirection.findViewById(R.id.txtRequirements);
        lvlText.setText("QuickGame mode");
        txtRequirement.setText("Answer 10 questions on easy, medium and hard level of each categories.");

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

    public void quit(View view){
        sound.playButtonSound();
        exitByBackKey();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(quickThree.this, thinking.class));
            stopService(new Intent(quickThree.this, thinkingmusic.class));
            stopService(new Intent(quickThree.this, backgroundmusic.class));
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
                .setMessage("Quit Quick Game?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        gameMode stop = new gameMode();
                        stop.stopTimer();
                        stopService(new Intent (quickThree.this, thinking.class));
                        startService(new Intent(quickThree.this, backgroundmusic.class));
                        Intent intent = new Intent(quickThree.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }
}
