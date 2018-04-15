package com.example.jomarie.csquiz.sounds;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.example.jomarie.csquiz.R;

/**
 * Created by jomarie on 7/14/2017.
 */

public class SoundPlayer {
    private AudioAttributes audioAttributes;
    final int SOUND_POOL_MAX = 2;

    private static SoundPool soundPool;
    private static int correctSound;
    private static int wrongSound;
    private static int buttonPressSound, checkboxSound;
    private boolean silentSound;


    public SoundPlayer(Context context){
        //for lolipop version API level 21
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(SOUND_POOL_MAX)
                    .build();
        }else {
            soundPool = new SoundPool(SOUND_POOL_MAX, AudioManager.STREAM_MUSIC, 0);
        }

        correctSound = soundPool.load(context, R.raw.correct, 1);
        wrongSound = soundPool.load(context, R.raw.wronng, 1);
        buttonPressSound = soundPool.load(context, R.raw.buttonpress, 1);
        checkboxSound = soundPool.load(context, R.raw.checkbox, 1);

        SharedPreferences settingsSound = context.getSharedPreferences("sound", 0);
        silentSound = settingsSound.getBoolean("switchkeySound", false);
    }

    public void playCorrectSound(){
        if(silentSound == true) {
            soundPool.play(correctSound, 5.0f, 5.0f, 1, 0, 1.0f);
        }
    }
    public void playWrongSound(){
        if(silentSound == true) {
            soundPool.play(wrongSound, 5.0f, 5.0f, 1, 0, 1.0f);
        }
    }
    public void playButtonSound(){
        if(silentSound == true) {
            soundPool.play(buttonPressSound, 5.0f, 5.0f, 1, 0, 1.0f);
        }
    }
    public  void playCheckboxSound() {
        if (silentSound == true) {
            soundPool.play(checkboxSound, 5.0f, 5.0f, 1, 0, 1.0f);
        }
    }


}
