package com.example.jomarie.csquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import java.util.ArrayList;

public class LoadingScreen extends Activity {

    private static int spalshInterval = 4000;
    private ArrayList subjects;
    private ImageView loadingImage;
    public static String sub1, sub2, sub3, sub4, sub5;
    private ArrayList<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        loadingImage = (ImageView) findViewById(R.id.imagebackground);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i;
                if(MainActivity.mode.equals("normalgame")) {
                    i = new Intent(LoadingScreen.this, quickgame.class);
                    subjects = getIntent().getStringArrayListExtra("listOfsubjects");
                    i.putStringArrayListExtra("listOfsubjects", subjects);
                    startActivity(i);
                }else if(MainActivity.mode.equals("quickgame")){
                    i = new Intent(LoadingScreen.this, quickThree.class);
                    subjects = getIntent().getStringArrayListExtra("listOfsubjects");
                    i.putStringArrayListExtra("listOfsubjects", subjects);
                    startActivity(i);
                } else if(MainActivity.mode.equals("timelimit")){
                    list = getIntent().getStringArrayListExtra("listOfsubjects");
                    sub1 = list.get(0);
                    sub2 = list.get(1);
                    sub3 = list.get(2);
                    sub4 = list.get(3);
                    sub5 = list.get(4);

                    if(normalgame.gameType.equalsIgnoreCase("multiple")){
                        i = new Intent(LoadingScreen.this, timeLimit.class);
                        startActivity(i);
                    }else if(normalgame.gameType.equalsIgnoreCase("rumble")){
                        i = new Intent(LoadingScreen.this, FillTime.class);
                        startActivity(i);
                    }else if(normalgame.gameType.equalsIgnoreCase("verifying")){
                        i = new Intent(LoadingScreen.this, VerTime.class);
                        startActivity(i);
                    }else if(normalgame.gameType.equalsIgnoreCase("random")){
                        i = new Intent(LoadingScreen.this, randomTime.class);
                        startActivity(i);
                    }
                } else if( MainActivity.mode.equals("dual")){
                    if(normalgame.gameType.equalsIgnoreCase("multiple")){
                        i = new Intent(LoadingScreen.this, DualPlayerMultiple.class);
                        startActivity(i);
                    }else if(normalgame.gameType.equalsIgnoreCase("rumble")){

                    }else if(normalgame.gameType.equalsIgnoreCase("verifying")){
                        i = new Intent(LoadingScreen.this, DualPlayerVer.class);
                        startActivity(i);
                    }else if(normalgame.gameType.equalsIgnoreCase("random")){

                    }
                }
                finish();
            }


        },spalshInterval);
    };

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(LoadingScreen.this, thinking.class));
            stopService(new Intent(LoadingScreen.this, thinkingmusic.class));
            stopService(new Intent(LoadingScreen.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }


}


