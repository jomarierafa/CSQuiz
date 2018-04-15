package com.example.jomarie.csquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import java.util.ArrayList;

public class LauncherScreen extends Activity {

    private static int spalshInterval = 6000;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher_screen);

        image = (ImageView) findViewById(R.id.imagelauncher);
        Animation fading = AnimationUtils.loadAnimation(this, R.anim.fading);
        fading.setDuration(3000);
        image.setAnimation(fading);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(LauncherScreen.this, MainActivity.class);
                startActivity(i);

                this.finish();
            }
            private void finish(){
            }

        },spalshInterval);
    };


}
