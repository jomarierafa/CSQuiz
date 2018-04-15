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
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.jomarie.csquiz.bluetooth.BluetoothActivity;
import com.example.jomarie.csquiz.database.DatabaseHelper;
import com.example.jomarie.csquiz.leaderboard.Leaderboard;
import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class MainActivity extends Activity {
    private DatabaseHelper mDBHelper;
    private SoundPlayer sound;
    private Button start, leaderBoard, settings, help;
    private Button quick, survival, timeLimit, dualPlayer;
    private Button survivalLB, timeLimitLB;
    public static String leaderboardType;
    private static Animation moveout, moveout2nd, moveout3rd, moveout4th, moveout5th, movein, movein2nd, movein3rd, movein4th, forGlitches;
    public static String mode, sub;
    private Integer phase = 1;
    private ImageView backButton;
    List<String> x;
    gameMode stop = new gameMode();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sound = new SoundPlayer(this);
        start = (Button) findViewById(R.id.button);
        leaderBoard = (Button)findViewById(R.id.button2);
        settings = (Button) findViewById(R.id.button3);
        help = (Button) findViewById(R.id.help);
        quick = (Button) findViewById(R.id.button4);
        survival = (Button) findViewById(R.id.button5);
        timeLimit = (Button) findViewById(R.id.button7);
        dualPlayer = (Button) findViewById(R.id.button6);
        survivalLB = (Button) findViewById(R.id.survival);
        timeLimitLB = (Button) findViewById(R.id.TimeLimit);
        backButton = (ImageView) findViewById(R.id.backButton);
        Typeface font = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        start.setTypeface(font);
        leaderBoard.setTypeface(font);
        settings.setTypeface(font);
        help.setTypeface(font);
        quick.setTypeface(font);
        survival.setTypeface(font);
        timeLimit.setTypeface(font);
        dualPlayer.setTypeface(font);
        survivalLB.setTypeface(font);
        timeLimitLB.setTypeface(font);

        // check if databse exist
        mDBHelper = new DatabaseHelper(this);
        File database = getApplicationContext().getDatabasePath(DatabaseHelper.dbName);
        if(!database.exists()){
            mDBHelper.getReadableDatabase();
            //copy database
            if(copyDatabase(this)){
                Toasty.info(this, "Welcome to CSQUIZ", Toast.LENGTH_SHORT).show();
            } else{
                Toast.makeText(this, "Copy data error", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        AudioManager manager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        if (!manager.isMusicActive()) {
            startService(new Intent(this, backgroundmusic.class));
        }


        moveout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        moveout2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        moveout3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        moveout4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        moveout5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        movein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        moveout2nd.setStartOffset(200);
        moveout3rd.setStartOffset(400);
        moveout4th.setStartOffset(600);

        startAnimation();

        forGlitches = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        forGlitches.setDuration(0);

        new Eula(this).show();
    }

    public void startAnimation(){
        movein.setStartOffset(800);
        movein2nd.setStartOffset(1000);
        movein3rd.setStartOffset(1200);
        movein4th.setStartOffset(1400);
        start.startAnimation(movein);
        leaderBoard.startAnimation(movein2nd);
        settings.startAnimation(movein3rd);
        help.startAnimation(movein4th);
    }


    public void play(View view){
        Animation movein5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        movein.setStartOffset(800);
        movein2nd.setStartOffset(1000);
        movein3rd.setStartOffset(1200);
        movein4th.setStartOffset(1400);
        movein5th.setStartOffset(1800);
        phase = 2;
        sound.playButtonSound();

        Toasty.Config.getInstance()
                .setErrorColor(ContextCompat.getColor(getApplicationContext(), R.color.color1))
                .setInfoColor(ContextCompat.getColor(getApplicationContext(), R.color.color2))
                .setSuccessColor(ContextCompat.getColor(getApplicationContext(), R.color.color3))
                .setWarningColor(ContextCompat.getColor(getApplicationContext(), R.color.color4))
                .setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.color_text))
                .tintIcon(true)//(apply textColor also to the icon)
                .setTextSize(30)
                .apply();

        start.startAnimation(moveout);
        leaderBoard.startAnimation(moveout2nd);
        settings.startAnimation(moveout3rd);
        help.startAnimation(moveout4th);
        quick.setVisibility(View.VISIBLE);
        survival.setVisibility(View.VISIBLE);
        timeLimit.setVisibility(View.VISIBLE);
        dualPlayer.setVisibility(View.VISIBLE);
        quick.startAnimation(movein);
        survival.startAnimation(movein2nd);
        timeLimit.startAnimation(movein3rd);
        dualPlayer.startAnimation(movein4th);
        backButton.startAnimation(movein5th);

        start.setClickable(false);
        leaderBoard.setClickable(false);
        settings.setClickable(false);
        help.setClickable(false);
        quick.setClickable(true);
        survival.setClickable(true);
        timeLimit.setClickable(true);
        dualPlayer.setClickable(true);
        backButton.setClickable(true);
    }

    public void leaderboards(View view){
        sound.playButtonSound();
        Animation backBtn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        movein.setStartOffset(800);
        movein2nd.setStartOffset(1000);
        backBtn.setStartOffset(1200);
        phase = 3;
        quick.startAnimation(forGlitches);
        start.startAnimation(moveout);
        leaderBoard.startAnimation(moveout2nd);
        settings.startAnimation(moveout3rd);
        help.startAnimation(moveout4th);
        survivalLB.setVisibility(View.VISIBLE);
        timeLimitLB.setVisibility(View.VISIBLE);
        quick.setVisibility(View.INVISIBLE);
        survivalLB.startAnimation(movein);
        timeLimitLB.startAnimation(movein2nd);
        backButton.startAnimation(backBtn);

        start.setClickable(false);
        leaderBoard.setClickable(false);
        settings.setClickable(false);
        help.setClickable(false);
        timeLimitLB.setClickable(true);
        survivalLB.setClickable(true);
        backButton.setClickable(true);
    }

    public void back(View view){
        backButton.setClickable(false);
        sound.playButtonSound();

        if(phase == 2){
            quick.startAnimation(moveout);
            survival.startAnimation(moveout2nd);
            timeLimit.startAnimation(moveout3rd);
            dualPlayer.startAnimation(moveout4th);
            backButton.startAnimation(moveout5th);
            quick.setVisibility(View.INVISIBLE);
            survival.setVisibility(View.INVISIBLE);
            quick.setClickable(false);
            survival.setClickable(false);
            timeLimit.setClickable(false);
            dualPlayer.setClickable(false);
            start.setClickable(true);
            leaderBoard.setClickable(true);
            settings.setClickable(true);
            help.setClickable(true);
            start.startAnimation(movein);
            leaderBoard.startAnimation(movein2nd);
            settings.startAnimation(movein3rd);
            help.startAnimation(movein4th);
        }else if(phase == 3){
            survival.startAnimation(forGlitches);
            quick.startAnimation(forGlitches);

            start.setClickable(true);
            leaderBoard.setClickable(true);
            settings.setClickable(true);
            help.setClickable(true);
            survivalLB.setClickable(false);
            timeLimitLB.setClickable(false);
            survivalLB.startAnimation(moveout);
            timeLimitLB.startAnimation(moveout2nd);
            backButton.startAnimation(moveout5th);
            movein.setStartOffset(400);
            movein2nd.setStartOffset(600);
            movein3rd.setStartOffset(800);
            movein4th.setStartOffset(1000);
            start.startAnimation(movein);
            leaderBoard.startAnimation(movein2nd);
            settings.startAnimation(movein3rd);
            help.startAnimation(movein4th);

        }
    }

    public void leaderboard(View view){
        sound.playButtonSound();
        Button btnLB = (Button) view;
        if(btnLB.getText().equals("Survival")){
            leaderboardType = "survival";
        }else{
            leaderboardType = "timeLimit";
        }
        Intent intent = new Intent(this, Leaderboard.class);
        startActivity(intent);
        finish();
    }


    public void settings(View view){
        sound.playButtonSound();
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
        finish();
    }
    public void helpdesk(View view){
        sound.playButtonSound();
        Intent intent = new Intent(this, HelpDesk.class);
        startActivity(intent);
        finish();
    }

    //category
    public void quick(View view){
        sound.playButtonSound();
        mode = "quickgame";
        Intent intent = new Intent(this, normalgame.class);
        startActivity(intent);
        finish();
    }
    public void normal(View view){
        sound.playButtonSound();
        quickgame.sub0Level = 1;
        quickgame.sub1Level = 1;
        quickgame.sub2Level = 1;
        quickgame.sub3Level = 1;
        quickgame.sub4Level = 1;
        mode = "normalgame";
        stop.stopTimer();
        Intent intent = new Intent(this, normalgame.class);
        startActivity(intent);
        finish();
    }
    public void timeLimit(View view){
        sound.playButtonSound();
        Intent intent = new Intent(this, normalgame.class);
        mode="timelimit";
        startActivity(intent);
        finish();
    }


    public void dualPlayer(View view){
        sound.playButtonSound();
        android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
        View sView = getLayoutInflater().inflate(R.layout.dialog_twoplayermode, null);


        ImageButton bluetooth = (ImageButton) sView.findViewById(R.id.btnBluetooth);
        ImageButton phone = (ImageButton) sView.findViewById(R.id.btnPhone);

        bluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                mode = "dual";
                Intent intent = new Intent(MainActivity.this, BluetoothActivity.class);
                startActivity(intent);
                finish();
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                mode = "dual";
                Intent intent = new Intent(MainActivity.this, normalgame.class);
                startActivity(intent);
                finish();
            }
        });
        sBuilder.setView(sView);
        android.support.v7.app.AlertDialog dialog = sBuilder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


    private boolean copyDatabase(Context context) {
        try {
            InputStream inputStream = context.getAssets().open(DatabaseHelper.dbName);
            String outFileName = DatabaseHelper.LOCATION + DatabaseHelper.dbName;
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buff = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buff)) > 0) {
                outputStream.write(buff, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(MainActivity.this, thinking.class));
            stopService(new Intent(MainActivity.this, thinkingmusic.class));
            stopService(new Intent(MainActivity.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
                exitByBackKey();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    protected void exitByBackKey() {

        AlertDialog alertbox = new AlertDialog.Builder(this)
                .setMessage("You want to exit CSQuiz?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        stopService(new Intent(MainActivity.this, thinking.class));
                        stopService(new Intent(MainActivity.this, thinkingmusic.class));
                        stopService(new Intent(MainActivity.this, backgroundmusic.class));
                        finish();
                        System.exit(0);

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }


}
