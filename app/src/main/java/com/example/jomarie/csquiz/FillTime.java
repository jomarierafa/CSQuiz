package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jomarie.csquiz.database.DatabaseHelper;
import com.example.jomarie.csquiz.model.Leaderboardmodel;
import com.example.jomarie.csquiz.model.Question;
import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static com.example.jomarie.csquiz.FillInTheBlank.shuffleString;

public class FillTime extends Activity {
    private TextView question, score, clock, courseName, shuffleWord;
    private Button submit;
    private EditText answer;
    private ImageButton doubleTry, correct, firstLetter;
    private ImageView timerIcon;
    private LinearLayout lifelines, userAccess;
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private String correctAns;
    private int mQuestionNumber = 0;
    private Timer timer;
    final Countdown timers = new Countdown(181 * 1000, 100);
    private SoundPlayer sound;
    private int currentScore = 0;
    private boolean doubleTryStatus = false, shuffleStatus = true;
    private  Animation blinking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_time);
        lifelines = (LinearLayout) findViewById(R.id.fillTimeLifelines);
        userAccess = (LinearLayout) findViewById(R.id.tUserAccess);
        question = (TextView) findViewById(R.id.tquestionFill);
        score = (TextView) findViewById(R.id.tscoreFill);
        clock = (TextView) findViewById(R.id.timerFill);
        courseName = (TextView) findViewById(R.id.courseFilll);
        doubleTry = (ImageButton) findViewById(R.id.thalfFill);
        correct = (ImageButton) findViewById(R.id.tcorrectFill);
        firstLetter = (ImageButton) findViewById(R.id.tfirstLetter);
        shuffleWord = (TextView) findViewById(R.id.tshuffleWord);
        submit = (Button) findViewById(R.id.tsubmitFill);
        answer = (EditText) findViewById(R.id.tfillAnswer);
        timerIcon = (ImageView) findViewById(R.id.fillTimerIcon);
        sound = new SoundPlayer(this);
        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        question.getBackground().setAlpha(40);
        startAnimation();
        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface font  = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        courseName.setTypeface(font);
        question.setTypeface(textFont);
        shuffleWord.setTypeface(textFont);

        updateQuestion();

        startService(new Intent(FillTime.this, thinkingmusic.class));

        timers.start();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void startAnimation(){
        Animation moveIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation moveIn2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        blinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        timerIcon.startAnimation(blinking);

        moveIn.setStartOffset(500);
        moveIn2nd.setStartOffset(700);
        userAccess.startAnimation(moveIn);
        lifelines.startAnimation(moveIn2nd);

    }

    public class Countdown extends CountDownTimer {

        public Countdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millis) {
            int minutes = (int) (millis / (1000 * 60));
            int seconds = (int) ((millis / 1000) % 60);
            int seconds100 = (int) ((millis / 10) % 100);
            clock.setText("" + String.format("%d:%02d.%02d", minutes, seconds, seconds100));
        }

        @Override
        public void onFinish() {
            timerIcon.clearAnimation();
            clock.setText("");
            disabledUserAccess();
            stopService(new Intent(FillTime.this, thinkingmusic.class));
            android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(FillTime.this);
            View sView = getLayoutInflater().inflate(R.layout.dialog_score, null);
            final EditText player = (EditText) sView.findViewById(R.id.PlayerName);
            Button submit = (Button) sView.findViewById(R.id.btn_Submit);
            Button cancel = (Button) sView.findViewById(R.id.btn_SubmitNot);
            TextView scoreView = (TextView) sView.findViewById(R.id.scoreMessage);
            scoreView.setText("You Got " + score.getText() + " Points");
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!player.getText().toString().isEmpty()) {
                        ArrayList<Leaderboardmodel> mLeaderboardlist = new ArrayList<>();
                        SharedPreferences appSharedPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        Gson gson = new Gson();
                        String json = appSharedPrefs.getString("Filltime", "");
                        if(json.isEmpty()){
                            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
                        }else {
                            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
                            }.getType();
                            mLeaderboardlist = gson.fromJson(json, type);
                        }
                        String date = DateFormat.getDateTimeInstance().format(new Date());
                        startService(new Intent(FillTime.this, backgroundmusic.class));
                        Leaderboardmodel s = new Leaderboardmodel();
                        s.setDate(date);
                        s.setName(player.getText().toString());
                        s.setScore(Integer.parseInt(String.valueOf(score.getText())));
                        mLeaderboardlist.add(s);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson newgson = new Gson();
                        String newjson = newgson.toJson(mLeaderboardlist);
                        prefsEditor.putString("Filltime", newjson);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        finish();
                        stopService(new Intent(FillTime.this, thinkingmusic.class));
                        Intent intent = new Intent(FillTime.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(FillTime.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
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
                    finish();
                    stopService(new Intent(FillTime.this, thinkingmusic.class));
                    Intent intent = new Intent(FillTime.this, MainActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    private void updateScore(int point) {
        currentScore += point;
        score.setText(String.valueOf(currentScore));
    }

    private void updateQuestion() {
        if (mQuestionNumber < mQuestionList.size()) {
            Question s = mQuestionList.get(mQuestionNumber);
            question.setText(s.getQuestion());
            courseName.setText(s.getCourse());
            correctAns = s.getAnswer();
            String tmpAnswr = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
            mQuestionNumber++;
            doubleTryStatus = false;
        } else {
            Toast.makeText(this, "You Answer All the Question", Toast.LENGTH_SHORT).show();
        }
    }

    public void choose(View view) {
        if(answer.getText().toString().isEmpty()){
            Toasty.info(this, "Provide an Answer!", Toast.LENGTH_SHORT, true).show();
            return;
        }

        if(answer.getText().toString().equalsIgnoreCase(correctAns)){
            sound.playCorrectSound();
            updateScore(1);
            answer.setTextColor(Color.GREEN);
            Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
        } else {
            sound.playWrongSound();
            Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
            if(doubleTryStatus){
                doubleTryStatus = false;
                answer.setText("");
                return;
            }
            doubleTryStatus = false;
            disabledUserAccess();
            answer.setTextColor(Color.RED);
            shuffleWord.setText(correctAns);
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FillTime.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                        resetUserAccess();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    public void resetUserAccess(){
        submit.setClickable(true);
        answer.setEnabled(true);
        answer.setText("");
        answer.setTextColor(Color.WHITE);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(true);
        }
    }
    public void disabledUserAccess(){
        submit.setClickable(false);
        answer.setEnabled(false);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(false);
        }
    }

    //lifeline
    public void firstLetter(View view){
        char firstChar = correctAns.charAt(0);
        answer.setText(String.valueOf(firstChar));
        firstLetter.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        firstLetter.setEnabled(false);
        firstLetter.setBackgroundResource(R.drawable.lifelineused);
    }
    public void shuffle(View view){
        if(shuffleStatus) {
            String tmpAnswr = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
            shuffleStatus = false;
        }else{
            Toasty.info(this, "Already Shuffled!", Toast.LENGTH_SHORT, true).show();
        }
    }
    public void doubleTry(View view){
        doubleTry.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTry.setBackgroundResource(R.drawable.lifelineused);
        doubleTryStatus = true;
        doubleTry.setEnabled(false);
    }
    public void correctAnswer(View view){
        timerIcon.clearAnimation();
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);
        disabledUserAccess();
        updateScore(1);
        correct.setEnabled(false);
        answer.setText(correctAns);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                FillTime.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                        resetUserAccess();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(FillTime.this, thinking.class));
            stopService(new Intent(FillTime.this, thinkingmusic.class));
            stopService(new Intent(FillTime.this, backgroundmusic.class));
            finish();
            System.exit(0);
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
                .setMessage("Do you want to back on the Main Menu?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                        timers.cancel();
                        finish();
                        stopService(new Intent(FillTime.this, thinkingmusic.class));
                        startService(new Intent(FillTime.this, backgroundmusic.class));
                        Intent intent = new Intent(FillTime.this, MainActivity.class);
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
