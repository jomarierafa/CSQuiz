package com.example.jomarie.csquiz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;


import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

public class Settings extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{
    public static  Switch swtMusic, swtSound;
    public static boolean silentSound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        swtMusic = (Switch) findViewById(R.id.swtMusic);
        swtSound = (Switch) findViewById(R.id.swtSound);

        Typeface font = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        swtMusic.setTypeface(font);
        swtSound.setTypeface(font);

        swtMusic.setOnCheckedChangeListener(this);
        swtSound.setOnCheckedChangeListener(this);


        SharedPreferences settings = getSharedPreferences("music", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        swtMusic.setChecked(silent);

        SharedPreferences settingsSound = getSharedPreferences("sound", 0);
        silentSound = settingsSound.getBoolean("switchkeySound", false);
        swtSound.setChecked(silentSound);



    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        switch (compoundButton.getId()){
            case R.id.swtMusic:
                if(swtMusic.isChecked()){
                    startService(new Intent(this, backgroundmusic.class));
                }else {
                    stopService(new Intent(this, backgroundmusic.class));
                }

                SharedPreferences settings = getSharedPreferences("music", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("switchkey", isChecked);
                editor.commit();
                break;

            case R.id.swtSound:
                SharedPreferences settingsSound = getSharedPreferences("sound", 0);
                SharedPreferences.Editor editorSound = settingsSound.edit();
                editorSound.putBoolean("switchkeySound", isChecked);
                editorSound.commit();
                break;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(Settings.this, thinking.class));
            stopService(new Intent(Settings.this, thinkingmusic.class));
            stopService(new Intent(Settings.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Settings.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
