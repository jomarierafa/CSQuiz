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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

public class timeLimit extends Activity {
    private LinearLayout lifelines;
    private TextView question, score, clock, courseName;
    private Button choice1, choice2, choice3, choice4;
    private ImageButton half, correct, doubleTip;
    private ImageView timerIcon;
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private String correctAns;
    private int mQuestionNumber = 0;
    private Timer timer;
    final Countdown timers = new Countdown(181 * 1000, 100);
    private SoundPlayer sound;
    private int currentScore = 0;
    private boolean doubleTipStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_limit);
        lifelines = (LinearLayout) findViewById(R.id.mLifeline);
        question = (TextView) findViewById(R.id.tquestion);
        score = (TextView) findViewById(R.id.tscore);
        choice1 = (Button) findViewById(R.id.tchoice1);
        choice2 = (Button) findViewById(R.id.tchoice2);
        choice3 = (Button) findViewById(R.id.tchoice3);
        choice4 = (Button) findViewById(R.id.tchoice4);
        clock = (TextView) findViewById(R.id.timer);
        courseName = (TextView) findViewById(R.id.course);
        half = (ImageButton) findViewById(R.id.thalf);
        correct = (ImageButton) findViewById(R.id.tcorrect);
        doubleTip = (ImageButton) findViewById(R.id.tdouble);
        timerIcon = (ImageView) findViewById(R.id.TimerIcon);
        sound = new SoundPlayer(this);
        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        Typeface digital = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        Typeface font  = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        clock.setTypeface(digital);
        courseName.setTypeface(font);
        question.setTypeface(textFont);
        choice1.setTypeface(textFont);
        choice2.setTypeface(textFont);
        choice3.setTypeface(textFont);
        choice4.setTypeface(textFont);

        question.getBackground().setAlpha(40);
        updateQuestion();

        startService(new Intent(timeLimit.this, thinkingmusic.class));

        timers.start();
        Animation blinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        timerIcon.startAnimation(blinking);
        startAnimation();
    }

    private void startAnimation(){
        Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        Animation move5th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        move.setStartOffset(500);
        move2nd.setStartOffset(700);
        move3rd.setStartOffset(900);
        move4th.setStartOffset(1100);
        move5th.setStartOffset(1300);
        choice1.startAnimation(move);
        choice2.startAnimation(move2nd);
        choice3.startAnimation(move3rd);
        choice4.startAnimation(move4th);
        lifelines.startAnimation(move5th);
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
            disabledButtonChoice();
            stopService(new Intent(timeLimit.this, thinkingmusic.class));
            android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(timeLimit.this);
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
                        String json = appSharedPrefs.getString("time", "");
                        if(json.isEmpty()){
                            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
                        }else {
                            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
                            }.getType();
                            mLeaderboardlist = gson.fromJson(json, type);
                        }
                        String date = DateFormat.getDateTimeInstance().format(new Date());
                        startService(new Intent(timeLimit.this, backgroundmusic.class));
                        Leaderboardmodel s = new Leaderboardmodel();
                        s.setDate(date);
                        s.setName(player.getText().toString());
                        s.setScore(Integer.parseInt(String.valueOf(score.getText())));
                        mLeaderboardlist.add(s);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson newgson = new Gson();
                        String newjson = newgson.toJson(mLeaderboardlist);
                        prefsEditor.putString("time", newjson);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        finish();
                        stopService(new Intent(timeLimit.this, thinkingmusic.class));
                        Intent intent = new Intent(timeLimit.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(timeLimit.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
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
                    stopService(new Intent(timeLimit.this, thinkingmusic.class));
                    Intent intent = new Intent(timeLimit.this, MainActivity.class);
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
        List<Integer> x = new ArrayList<>(Arrays.asList(0, 1, 2, 3));
        if (mQuestionNumber < mQuestionList.size()) {
            Question s = mQuestionList.get(mQuestionNumber);
            question.setText(s.getQuestion());
            courseName.setText(s.getCourse());
            //for randoming the question
            Collections.shuffle(x);
            Object[] arr = x.toArray();
            choice1.setText(s.getChoices((Integer) arr[0]));
            choice2.setText(s.getChoices((Integer) arr[1]));
            choice3.setText(s.getChoices((Integer) arr[2]));
            choice4.setText(s.getChoices((Integer) arr[3]));
            correctAns = s.getAnswer();
            mQuestionNumber++;
            doubleTipStatus = false;
        } else {
            Toast.makeText(this, "You Answer All the Question", Toast.LENGTH_SHORT).show();
        }
    }

    public void choose(View view) {
        Button answer = (Button) view;
        if (answer.getText().equals(correctAns)) {
            disabledButtonChoice();
            sound.playCorrectSound();
            updateScore(1);
            answer.setBackgroundResource(R.drawable.correctbutton);
            Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
        } else {
            sound.playWrongSound();
            Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
            if(doubleTipStatus == true){
                doubleTipStatus = false;
                answer.setEnabled(false);
                return;
            }
            doubleTipStatus = false;
            disabledButtonChoice();
            answer.setBackgroundResource(R.drawable.wrongbutton);
            viewCorrectAns();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLimit.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                        resetChoice();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }

    public void viewCorrectAns() {
        if (correctAns.equals(choice1.getText())) {
            choice1.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice2.getText())) {
            choice2.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice3.getText())) {
            choice3.setBackgroundResource(R.drawable.correctbutton);
        } else {
            choice4.setBackgroundResource(R.drawable.correctbutton);
        }
    }

    public void resetChoice() {
        choice1.setClickable(true);
        choice2.setClickable(true);
        choice3.setClickable(true);
        choice4.setClickable(true);
        choice1.setEnabled(true);
        choice2.setEnabled(true);
        choice3.setEnabled(true);
        choice4.setEnabled(true);
        choice1.setBackgroundResource(R.drawable.menubutton);
        choice2.setBackgroundResource(R.drawable.menubutton);
        choice3.setBackgroundResource(R.drawable.menubutton);
        choice4.setBackgroundResource(R.drawable.menubutton);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(true);
        }
}

    public void disabledButtonChoice() {
        choice1.setClickable(false);
        choice2.setClickable(false);
        choice3.setClickable(false);
        choice4.setClickable(false);
        for ( int i = 0; i < lifelines.getChildCount();  i++ ){
            View view = lifelines.getChildAt(i);
            view.setEnabled(false);
        }
    }

    //lifeline
    public void twoChoices(View view) {
        half.setBackgroundResource(R.drawable.lifelineused);
        half.setEnabled(false);
        half.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        choice1.setEnabled(false);
        choice2.setEnabled(false);
        choice3.setEnabled(false);
        choice4.setEnabled(false);
        List<Integer> a = new ArrayList<>(Arrays.asList(0, 1, 2));
        Collections.shuffle(a);
        Object[] arr = a.toArray();
        if (correctAns.equals(choice1.getText())) {
            choice1.setEnabled(true);
            if (arr[0].equals(0)) {
                choice2.setEnabled(true);
            } else if (arr[1].equals(1)) {
                choice3.setEnabled(true);
            } else {
                choice4.setEnabled(true);
            }
        } else if (correctAns.equals(choice2.getText())) {
            choice2.setEnabled(true);
            if (arr[0].equals(0)) {
                choice1.setEnabled(true);
            } else if (arr[1].equals(1)) {
                choice3.setEnabled(true);
            } else {
                choice4.setEnabled(true);
            }
        } else if (correctAns.equals(choice3.getText())) {
            choice3.setEnabled(true);
            if (arr[0].equals(0)) {
                choice1.setEnabled(true);
            } else if (arr[1].equals(1)) {
                choice2.setEnabled(true);
            } else {
                choice4.setEnabled(true);
            }
        } else {
            choice4.setEnabled(true);
            if (arr[0].equals(0)) {
                choice1.setEnabled(true);
            } else if (arr[1].equals(1)) {
                choice2.setEnabled(true);
            } else {
                choice3.setEnabled(true);
            }
        }
    }
    public void corectAns(View view) {
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);
        disabledButtonChoice();
        updateScore(1);
        correct.setEnabled(false);
        if (correctAns.equals(choice1.getText())) {
            choice1.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice2.getText())) {
            choice2.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice3.getText())) {
            choice3.setBackgroundResource(R.drawable.correctbutton);
        } else {
            choice4.setBackgroundResource(R.drawable.correctbutton);
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                timeLimit.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateQuestion();
                        updateScore(1);
                        resetChoice();
                        timer.cancel();
                        timer.purge();
                    }
                });
            }
        }, 3000, 3000);
    }
    public void doubleTip(View view){
        doubleTip.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTip.setBackgroundResource(R.drawable.lifelineused);
        doubleTipStatus = true;
        doubleTip.setEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(timeLimit.this, thinking.class));
            stopService(new Intent(timeLimit.this, thinkingmusic.class));
            stopService(new Intent(timeLimit.this, backgroundmusic.class));
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
                        stopService(new Intent(timeLimit.this, thinkingmusic.class));
                        startService(new Intent(timeLimit.this, backgroundmusic.class));
                        Intent intent = new Intent(timeLimit.this, MainActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    // do something when the button is clicked
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();
    }
}



