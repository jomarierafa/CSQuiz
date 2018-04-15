package com.example.jomarie.csquiz.sounds;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.example.jomarie.csquiz.R;

/**
 * Created by jomarie on 8/22/2017.
 */

public class thinkingmusic extends Service {
    private MediaPlayer player;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        player = MediaPlayer.create(this, R.raw.gamemode);
        player.setLooping(true);
        player.setVolume(100, 100);
        SharedPreferences settings = getSharedPreferences("music", 0);
        boolean silent = settings.getBoolean("switchkey", false);
        if(silent == true) {
            player.start();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.stop();
    }
}
