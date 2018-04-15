package com.example.jomarie.csquiz;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

public class HelpDesk extends Activity {
    private RelativeLayout container;
    private LinearLayout descriptionBackground;
    private ScrollView lifelinesView, quiztypeView, gameModesView, developerView;
    private Button gameModes, quizTypes, lifeLines, developer, exit;
    private SoundPlayer sound;
    Animation forGlitches, movein, movein2nd, movein3rd, movein4th, movein5th;
    Animation moveout, moveout2nd, moveout3rd, moveout4th, moveout5th;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_desk);
        sound = new SoundPlayer(this);
        container = (RelativeLayout) findViewById(R.id.container);container.getBackground().setAlpha(40);
        descriptionBackground = (LinearLayout) findViewById(R.id.descriptionBackground);
        Typeface font1 = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        lifelinesView = (ScrollView) findViewById(R.id.helpLifelines);
        quiztypeView = (ScrollView) findViewById(R.id.helpQuizType);
        gameModesView = (ScrollView) findViewById(R.id.helpGamemodes);
        developerView = (ScrollView) findViewById(R.id.helpDevelopers);
        gameModes = (Button) findViewById(R.id.hgamemode);gameModes.setTypeface(font1);
        quizTypes = (Button) findViewById(R.id.hquiztype);quizTypes.setTypeface(font1);
        lifeLines = (Button) findViewById(R.id.hlifelines);lifeLines.setTypeface(font1);
        developer = (Button) findViewById(R.id.hdeveloper);developer.setTypeface(font1);
        exit = (Button) findViewById(R.id.hexit);exit.setTypeface(font1);
        TextView lifelineheading = (TextView) findViewById(R.id.lifelineheading);lifelineheading.setTypeface(font1);
        TextView quiztypeheading = (TextView) findViewById(R.id.quiztypeheading);quiztypeheading.setTypeface(font1);
        TextView gameModeheading = (TextView) findViewById(R.id.gamemodeheading);gameModeheading.setTypeface(font1);
        TextView developerheading= (TextView) findViewById(R.id.developerheading);developerheading.setTypeface(font1);


        moveout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        moveout2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);moveout2nd.setStartOffset(200);
        moveout3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);moveout3rd.setStartOffset(400);
        moveout4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);moveout4th.setStartOffset(600);
        moveout5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);moveout5th.setStartOffset(800);
        startAnimation();
        setFont();
    }

    public void setFont(){
        Typeface font = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        TextView description = (TextView) findViewById(R.id.description);description.setTypeface(font);
        TextView lifeline1 = (TextView) findViewById(R.id.hlifeline1);lifeline1.setTypeface(font);
        TextView lifeline2 = (TextView) findViewById(R.id.hlifeline2);lifeline2.setTypeface(font);
        TextView lifeline3 = (TextView) findViewById(R.id.hlifeline3);lifeline3.setTypeface(font);
        TextView lifeline4 = (TextView) findViewById(R.id.hlifeline4);lifeline4.setTypeface(font);
        TextView lifeline5 = (TextView) findViewById(R.id.hlifeline5);lifeline5.setTypeface(font);
        TextView lifeline6 = (TextView) findViewById(R.id.hlifeline6);lifeline6.setTypeface(font);
        TextView lifeline7 = (TextView) findViewById(R.id.hlifeline7);lifeline7.setTypeface(font);
        TextView quiztype1 = (TextView) findViewById(R.id.htypeofquiz1);quiztype1.setTypeface(font);
        TextView quiztype2 = (TextView) findViewById(R.id.htypeofquiz2);quiztype2.setTypeface(font);
        TextView quiztype3 = (TextView) findViewById(R.id.htypeofquiz3);quiztype3.setTypeface(font);
        TextView gameMode1 = (TextView) findViewById(R.id.hgamemode1);gameMode1.setTypeface(font);
        TextView gameMode2 = (TextView) findViewById(R.id.hgamemode2);gameMode2.setTypeface(font);
        TextView gameMode3 = (TextView) findViewById(R.id.hgamemode3);gameMode3.setTypeface(font);
        TextView gameMode4 = (TextView) findViewById(R.id.hgamemode4);gameMode4.setTypeface(font);
        TextView gameMode5 = (TextView) findViewById(R.id.hgamemode5);gameMode5.setTypeface(font);
        TextView developer1 = (TextView) findViewById(R.id.hdeveloper1);developer1.setTypeface(font);
        TextView developer2 = (TextView) findViewById(R.id.hdeveloper2);developer2.setTypeface(font);
        TextView developer3 = (TextView) findViewById(R.id.hdeveloper3);developer3.setTypeface(font);
        TextView developer4 = (TextView) findViewById(R.id.hdeveloper4);developer4.setTypeface(font);
        TextView developer5 = (TextView) findViewById(R.id.hdeveloper5);developer5.setTypeface(font);


    }
    public void startAnimation(){
        movein = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        movein.setStartOffset(500);
        movein2nd.setStartOffset(700);
        movein3rd.setStartOffset(900);
        movein4th.setStartOffset(1100);
        movein5th.setStartOffset(1300);
        gameModes.startAnimation(movein);
        quizTypes.startAnimation(movein2nd);
        lifeLines.startAnimation(movein3rd);
        developer.startAnimation(movein4th);
        exit.startAnimation(movein5th);

        forGlitches = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        forGlitches.setDuration(0);
    }

    public void GameModes(View view){
        sound.playButtonSound();
        gameModesView.setVisibility(View.VISIBLE);
        lifelinesView.setVisibility(View.INVISIBLE);
        descriptionBackground.setVisibility(View.INVISIBLE);
        quiztypeView.setVisibility(View.INVISIBLE);
        developerView.setVisibility(View.INVISIBLE);
    }
    public void lifelines(View view){
        sound.playButtonSound();
        lifelinesView.setVisibility(View.VISIBLE);
        descriptionBackground.setVisibility(View.INVISIBLE);
        quiztypeView.setVisibility(View.INVISIBLE);
        gameModesView.setVisibility(View.INVISIBLE);
        developerView.setVisibility(View.INVISIBLE);
    }
    public void quiztypes(View view){
        sound.playButtonSound();
        quiztypeView.setVisibility(View.VISIBLE);
        descriptionBackground.setVisibility(View.INVISIBLE);
        lifelinesView.setVisibility(View.INVISIBLE);
        gameModesView.setVisibility(View.INVISIBLE);
        developerView.setVisibility(View.INVISIBLE);
    }
    public void developer(View view){
        sound.playButtonSound();
        sound.playButtonSound();
        developerView.setVisibility(View.VISIBLE);
        gameModesView.setVisibility(View.INVISIBLE);
        lifelinesView.setVisibility(View.INVISIBLE);
        descriptionBackground.setVisibility(View.INVISIBLE);
        quiztypeView.setVisibility(View.INVISIBLE);
    }
    public void exit(View view){
        sound.playButtonSound();
        Intent intent = new Intent(HelpDesk.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent i = new Intent(HelpDesk.this, MainActivity.class);
            startActivity(i);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(HelpDesk.this, thinking.class));
            stopService(new Intent(HelpDesk.this, thinkingmusic.class));
            stopService(new Intent(HelpDesk.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }

}
