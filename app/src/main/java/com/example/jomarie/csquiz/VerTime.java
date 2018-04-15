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
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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

import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;


public class VerTime extends Activity {
    private TextView question, score, clock, courseName, givenWord, correctWord;
    private Button submit;
    private RadioButton radCorrect, radIncorrect;
    private ImageButton doubleTry, correct, seeChoices;
    private ImageView timerIcon;
    private LinearLayout lifelines;
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private String correctAns, choice1, choice2, choice3, choice4, tmpAnswr;
    private int mQuestionNumber = 0;
    private Timer timer;
    final Countdown timers = new Countdown(181 * 1000, 100);
    private SoundPlayer sound;
    private int currentScore = 0;
    private boolean doubleTryStatus = false;
    private  Animation blinking, givenWordAnim, correctAnsAnim, forGlitches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_time);
        question = (TextView) findViewById(R.id.tquestionVer);
        score = (TextView) findViewById(R.id.tscoreVer);
        clock = (TextView) findViewById(R.id.timerVer);
        courseName = (TextView) findViewById(R.id.courseVer);
        doubleTry = (ImageButton) findViewById(R.id.tdoubleTryVer);
        correct = (ImageButton) findViewById(R.id.tcorrectVer);
        seeChoices = (ImageButton) findViewById(R.id.tseeChoices);
        lifelines = (LinearLayout) findViewById(R.id.verTimelifelines);
        givenWord = (TextView) findViewById(R.id.tgivenWord);
        correctWord = (TextView) findViewById(R.id.tcorrectWord);
        submit = (Button) findViewById(R.id.submitVer);
        radCorrect = (RadioButton) findViewById(R.id.tradCorrectVer);
        radIncorrect = (RadioButton) findViewById(R.id.tincorrectVer);
        timerIcon = (ImageView) findViewById(R.id.verTimerIcon);
        sound = new SoundPlayer(this);
        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        forGlitches = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading_out);
        forGlitches.setDuration(0);

        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface font  = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        courseName.setTypeface(font);
        question.setTypeface(textFont);
        radCorrect.setTypeface(textFont);
        radIncorrect.setTypeface(textFont);
        givenWord.setTypeface(textFont);
        correctWord.setTypeface(textFont);

        question.getBackground().setAlpha(40);
        updateQuestion();

        startService(new Intent(VerTime.this, thinkingmusic.class));

        timers.start();
        startAnimation();
    }

    private void startAnimation(){
        Animation fadeFirst = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeSecond = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeThird = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fade4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation moveIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        blinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);

        fadeFirst.setDuration(500);
        fadeSecond.setDuration(500);
        fadeThird.setDuration(500);
        fade4th.setDuration(500);
        fadeFirst.setStartOffset(500);
        fadeSecond.setStartOffset(700);
        fadeThird.setStartOffset(900);
        fade4th.setStartOffset(1100);
        moveIn.setStartOffset(1500);
        timerIcon.startAnimation(blinking);
        givenWord.startAnimation(fadeFirst);
        radCorrect.startAnimation(fadeSecond);
        radIncorrect.startAnimation(fadeThird);
        submit.startAnimation(fade4th);
        lifelines.startAnimation(moveIn);
    }
    private void correctAnswerAnimation(){
        givenWordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        correctAnsAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        givenWordAnim.setStartOffset(600);
        correctAnsAnim.setStartOffset(620);
        givenWord.startAnimation(givenWordAnim);
        correctWord.startAnimation(correctAnsAnim);
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
            stopService(new Intent(VerTime.this, thinkingmusic.class));
            android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(VerTime.this);
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
                        String json = appSharedPrefs.getString("Vertime", "");
                        if(json.isEmpty()){
                            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
                        }else {
                            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
                            }.getType();
                            mLeaderboardlist = gson.fromJson(json, type);
                        }
                        String date = DateFormat.getDateTimeInstance().format(new Date());
                        startService(new Intent(VerTime.this, backgroundmusic.class));
                        Leaderboardmodel s = new Leaderboardmodel();
                        s.setDate(date);
                        s.setName(player.getText().toString());
                        s.setScore(Integer.parseInt(String.valueOf(score.getText())));
                        mLeaderboardlist.add(s);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson newgson = new Gson();
                        String newjson = newgson.toJson(mLeaderboardlist);
                        prefsEditor.putString("Vertime", newjson);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        finish();
                        stopService(new Intent(VerTime.this, thinkingmusic.class));
                        Intent intent = new Intent(VerTime.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(VerTime.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
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
                    stopService(new Intent(VerTime.this, thinkingmusic.class));
                    Intent intent = new Intent(VerTime.this, MainActivity.class);
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
        List<Integer> x = new ArrayList<>(Arrays.asList(0,1,2,3));
        if (mQuestionNumber < mQuestionList.size()) {
            Question s = mQuestionList.get(mQuestionNumber);
            question.setText(s.getQuestion());
            courseName.setText(s.getCourse());
            //to make a random choices
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            choice1 = s.getChoices((Integer) arr[0]);
            choice2 = s.getChoices((Integer) arr[1]);
            choice3 = s.getChoices((Integer) arr[2]);
            choice4 = s.getChoices((Integer) arr[3]);
            correctAns = s.getAnswer();
            correctWord.setText(correctAns);

            List<String> randomChoices = new ArrayList<>(Arrays.asList(choice1, correctAns, choice2, choice3, correctAns, choice4));
            Collections.shuffle(randomChoices);
            tmpAnswr = randomChoices.get(0);
            givenWord.setText(tmpAnswr.toUpperCase());
            mQuestionNumber++;
            doubleTryStatus = false;
        } else {
            Toast.makeText(this, "You Answer All the Question", Toast.LENGTH_SHORT).show();
        }
    }

    public void submit(View view) {
        if(radCorrect.isChecked()) {
            if (tmpAnswr.equalsIgnoreCase(correctAns)) {
                sound.playCorrectSound();
                updateScore(1);
                disabledUserAccess();
                givenWord.setTextColor(Color.GREEN);
                Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
            } else {
                sound.playWrongSound();
                Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
                if (doubleTryStatus) {
                    doubleTryStatus = false;
                    return;
                }
                correctAnswerAnimation();
                disabledUserAccess();
                givenWord.setTextColor(Color.RED);
            }

        }else{
            if (!tmpAnswr.equalsIgnoreCase(correctAns)) {
                correctAnswerAnimation();
                sound.playCorrectSound();
                updateScore(1);
                disabledUserAccess();
                givenWord.setTextColor(Color.RED);
                Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
            } else {
                sound.playWrongSound();
                Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
                if (doubleTryStatus) {
                    doubleTryStatus = false;
                    return;
                }

                disabledUserAccess();
                givenWord.setTextColor(Color.GREEN);
            }
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                VerTime.this.runOnUiThread(new Runnable() {
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

    public void disabledUserAccess(){
        submit.setClickable(false);
        radCorrect.setClickable(false);
        radIncorrect.setClickable(false);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(false);
        }
    }
    public void resetUserAccess() {
        radCorrect.setChecked(true);
        submit.setClickable(true);
        radCorrect.setClickable(true);
        radIncorrect.setClickable(true);
        givenWord.setTextColor(Color.WHITE);
        correctWord.startAnimation(forGlitches);
        givenWord.clearAnimation();
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(true);
        }

    }

    public void seeChoices(View view){
        seeChoices.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        seeChoices.setEnabled(false);
        seeChoices.setBackgroundResource(R.drawable.lifelineused);
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(VerTime.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);
        TextView possibleAnswer = (TextView) sViewDirection.findViewById(R.id.txtDirection);
        TextView label = (TextView) sViewDirection.findViewById(R.id.txtRequirements);
        label.setText("All Possible Answer");
        possibleAnswer.setText("* " + choice1 + "\n* " + choice2 + "\n* " + choice3 + "\n* " + choice4);
        final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
        dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDirection.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                dialogDirection.cancel();
            }
        });
    }

    public void correctAnswer(View view){
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);
        correct.setEnabled(false);
        givenWord.setTextColor(Color.GREEN);
        givenWord.setText(correctAns);
        disabledUserAccess();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                VerTime.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateScore(1);
                        updateQuestion();
                        resetUserAccess();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }
    public void doubleTry(View view){
        doubleTry.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTry.setBackgroundResource(R.drawable.lifelineused);
        doubleTryStatus = true;
        doubleTry.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(VerTime.this, thinking.class));
            stopService(new Intent(VerTime.this, thinkingmusic.class));
            stopService(new Intent(VerTime.this, backgroundmusic.class));
            finish();
            System.exit(0);
        }

    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitByBackKey();
            //moveTaskToBack(false);
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
                        stopService(new Intent(VerTime.this, thinkingmusic.class));
                        startService(new Intent(VerTime.this, backgroundmusic.class));
                        Intent intent = new Intent(VerTime.this, MainActivity.class);
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

