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
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jomarie.csquiz.R;
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

import static com.example.jomarie.csquiz.FillInTheBlank.shuffleString;

public class randomTime extends Activity {
    private LinearLayout multiple, verifying, fib;
    private TextView question, score, clock, courseName, givenWord, shuffleWord, correctWord;
    private Button submitVer, submitFill, choice1, choice2, choice3, choice4;
    private RadioButton radCorrect, radIncorrect;
    private EditText answer;
    private ImageButton doubleTry, correct, specialLifeline;
    private ImageView timerIcon;
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private String correctAns, ch1, ch2, ch3, ch4, tmpAnswr;
    private int mQuestionNumber = 0;
    private Timer timer;
    final Countdown timers = new Countdown(181 * 1000, 100);
    private SoundPlayer sound;
    private int currentScore = 0;
    private boolean doubleTryStatus = false, shuffleStatus = true;;
    private Animation blinking, correctAnsAnim, givenWordAnim, forGlitches;
    String qtype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_time);
        question = (TextView) findViewById(R.id.tquestionRad);
        score = (TextView) findViewById(R.id.tscoreRad);
        clock = (TextView) findViewById(R.id.timerRad);
        courseName = (TextView) findViewById(R.id.courseRad);
        doubleTry = (ImageButton) findViewById(R.id.tdoubleTryVerRad);
        correct = (ImageButton) findViewById(R.id.tcorrectVerRad);
        specialLifeline = (ImageButton) findViewById(R.id.tseeChoicesRad);
        givenWord = (TextView) findViewById(R.id.tgivenWordRad);
        shuffleWord = (TextView) findViewById(R.id.tshuffleWordRad);
        correctWord = (TextView) findViewById(R.id.tcorrectWordRad);
        submitVer = (Button) findViewById(R.id.submitVerRad);
        submitFill = (Button) findViewById(R.id.tsubmitFillRad);
        choice1 = (Button) findViewById(R.id.tchoice1Rad);
        choice2 = (Button) findViewById(R.id.tchoice2Rad);
        choice3 = (Button) findViewById(R.id.tchoice3Rad);
        choice4 = (Button) findViewById(R.id.tchoice4Rad);
        answer = (EditText) findViewById(R.id.tfillAnswerRad);
        radCorrect = (RadioButton) findViewById(R.id.tradCorrectVerRad);
        radIncorrect = (RadioButton) findViewById(R.id.tincorrectVerRad);
        timerIcon = (ImageView) findViewById(R.id.verTimerIconRad);
        multiple = (LinearLayout) findViewById(R.id.multiple);
        verifying = (LinearLayout) findViewById(R.id.verifying);
        fib = (LinearLayout) findViewById(R.id.fib);

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
        choice1.setTypeface(textFont);
        choice2.setTypeface(textFont);
        choice3.setTypeface(textFont);
        choice4.setTypeface(textFont);
        givenWord.setTypeface(textFont);
        correctWord.setTypeface(textFont);
        shuffleWord.setTypeface(textFont);
        radCorrect.setTypeface(textFont);
        radIncorrect.setTypeface(textFont);

        question.getBackground().setAlpha(40);
        updateQuestion();

        startService(new Intent(randomTime.this, thinkingmusic.class));

        timers.start();
        startAnimation();

        //para hindi lumabas agad ang keyboard
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    private void startAnimation(){
        blinking = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink);
        timerIcon.startAnimation(blinking);
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
            stopService(new Intent(randomTime.this, thinkingmusic.class));
            android.support.v7.app.AlertDialog.Builder sBuilder = new android.support.v7.app.AlertDialog.Builder(randomTime.this);
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
                        String json = appSharedPrefs.getString("randomTypes", "");
                        if(json.isEmpty()){
                            mLeaderboardlist = new ArrayList<Leaderboardmodel>();
                        }else {
                            Type type = new TypeToken<ArrayList<Leaderboardmodel>>() {
                            }.getType();
                            mLeaderboardlist = gson.fromJson(json, type);
                        }
                        String date = DateFormat.getDateTimeInstance().format(new Date());
                        startService(new Intent(randomTime.this, backgroundmusic.class));
                        Leaderboardmodel s = new Leaderboardmodel();
                        s.setDate(date);
                        s.setName(player.getText().toString());
                        s.setScore(Integer.parseInt(String.valueOf(score.getText())));
                        mLeaderboardlist.add(s);

                        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
                        Gson newgson = new Gson();
                        String newjson = newgson.toJson(mLeaderboardlist);
                        prefsEditor.putString("randomTypes", newjson);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        finish();
                        stopService(new Intent(randomTime.this, thinkingmusic.class));
                        Intent intent = new Intent(randomTime.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(randomTime.this, "Please Enter Name", Toast.LENGTH_SHORT).show();
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
                    stopService(new Intent(randomTime.this, thinkingmusic.class));
                    Intent intent = new Intent(randomTime.this, MainActivity.class);
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
            ch1 = s.getChoices((Integer) arr[0]);
            ch2 = s.getChoices((Integer) arr[1]);
            ch3 = s.getChoices((Integer) arr[2]);
            ch4 = s.getChoices((Integer) arr[3]);
            choice1.setText(ch1);
            choice2.setText(ch2);
            choice3.setText(ch3);
            choice4.setText(ch4);
            correctAns = s.getAnswer();
            correctWord.setText(correctAns);

            List<String> randomChoices = new ArrayList<>(Arrays.asList(ch1, correctAns, ch2, ch3, correctAns, ch4));
            Collections.shuffle(randomChoices);
            tmpAnswr = randomChoices.get(0);
            givenWord.setText(tmpAnswr.toUpperCase());

            String tmpAnswrFill = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswrFill).toUpperCase());

            getType();
            doubleTryStatus = false;

            mQuestionNumber++;
        } else {
            Toast.makeText(this, "You Answer All the Question", Toast.LENGTH_SHORT).show();
        }
    }

    public void getType(){
        List<Integer> n = new ArrayList<>(Arrays.asList(0,1,2));
        Collections.shuffle(n);
        Integer type = n.get(0);
        if(type == 0){
            fib.startAnimation(forGlitches);
            multiple.setVisibility(View.VISIBLE);
            verifying.setVisibility(View.INVISIBLE);
            fib.setVisibility(View.INVISIBLE);
            multipleAnimation();
            qtype = "multiple";
        }else if(type == 1){
            fib.startAnimation(forGlitches);
            multiple.setVisibility(View.INVISIBLE);
            verifying.setVisibility(View.VISIBLE);
            fib.setVisibility(View.INVISIBLE);
            verifyingAnimation();
            qtype = "ver";
        } else{
            multiple.setVisibility(View.INVISIBLE);
            verifying.setVisibility(View.INVISIBLE);
            fib.setVisibility(View.VISIBLE);
            fibAnimation();
            qtype = "fib";
        }
    }

    //Animation for multiple choice
    public void multipleAnimation(){
            Animation move = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
            Animation move2nd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
            Animation move3rd = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
            Animation move4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
            move.setStartOffset(500);
            move2nd.setStartOffset(700);
            move3rd.setStartOffset(900);
            move4th.setStartOffset(1100);
            choice1.startAnimation(move);
            choice2.startAnimation(move2nd);
            choice3.startAnimation(move3rd);
            choice4.startAnimation(move4th);
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
                randomTime.this.runOnUiThread(new Runnable() {
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
    public void submitFill(View view) {
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
                randomTime.this.runOnUiThread(new Runnable() {
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
    public void choose(View view) {
        Button answer = (Button) view;
        if (answer.getText().equals(correctAns)) {
            disabledUserAccess();
            sound.playCorrectSound();
            updateScore(1);
            answer.setBackgroundResource(R.drawable.correctbutton);
            Toasty.success(this, "Correct!", Toast.LENGTH_SHORT, true).show();
        } else {
            sound.playWrongSound();
            Toasty.error(this, "Wrong!", Toast.LENGTH_SHORT, true).show();
            if(doubleTryStatus == true){
                doubleTryStatus = false;
                answer.setEnabled(false);
                return;
            }
            doubleTryStatus = false;
            disabledUserAccess();
            answer.setBackgroundResource(R.drawable.wrongbutton);
            viewCorrectAns();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                randomTime.this.runOnUiThread(new Runnable() {
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
        //fill in the blank
        submitFill.setClickable(false);
        answer.setEnabled(false);

        //verifying
        submitVer.setClickable(false);
        radCorrect.setClickable(false);
        radIncorrect.setClickable(false);

        //for multiple choice
        choice1.setClickable(false);
        choice2.setClickable(false);
        choice3.setClickable(false);
        choice4.setClickable(false);
    }
    public void resetUserAccess() {
        //fill in the blank
        submitFill.setClickable(true);
        answer.setEnabled(true);
        answer.setText("");
        answer.setTextColor(Color.WHITE);

        //verifying
        radCorrect.setChecked(true);
        submitVer.setClickable(true);
        radCorrect.setClickable(true);
        radIncorrect.setClickable(true);
        givenWord.setTextColor(Color.WHITE);
        correctWord.startAnimation(forGlitches);
        givenWord.clearAnimation();

        //for multiple choice
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
    }

    //for fill in the blank
    public void shuffle(View view){
        if(shuffleStatus) {
            String tmpAnswr = correctAns;
            shuffleWord.setText(shuffleString(tmpAnswr).toUpperCase());
            shuffleStatus = false;
        }else{
            Toasty.info(this, "Already Shuffled!", Toast.LENGTH_SHORT, true).show();
        }
    }
    public void fibAnimation(){
        Animation moveIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveright);
        moveIn.setStartOffset(500);
        fib.startAnimation(moveIn);
    }

    //for multiple choice
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

    //for verifying
    private void verifyingAnimation(){
        Animation fadeFirst = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeSecond = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fadeThird = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);
        Animation fade4th = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fading);

        fadeFirst.setDuration(500);
        fadeSecond.setDuration(500);
        fadeThird.setDuration(500);
        fade4th.setDuration(500);
        fadeFirst.setStartOffset(500);
        fadeSecond.setStartOffset(700);
        fadeThird.setStartOffset(900);
        fade4th.setStartOffset(1100);
        givenWord.startAnimation(fadeFirst);
        radCorrect.startAnimation(fadeSecond);
        radIncorrect.startAnimation(fadeThird);
        submitVer.startAnimation(fade4th);
    }
    private void correctAnswerAnimation(){
        givenWordAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveout);
        correctAnsAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.moveleft);
        givenWordAnim.setStartOffset(600);
        correctAnsAnim.setStartOffset(620);
        givenWord.startAnimation(givenWordAnim);
        correctWord.startAnimation(correctAnsAnim);
    }

    public void specialLifeline(View view){
        specialLifeline.setEnabled(false);
        specialLifeline.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        specialLifeline.setBackgroundResource(R.drawable.lifelineused);
        if(qtype.equalsIgnoreCase("ver")) {
            android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(randomTime.this);
            View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
            Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
            sBuilderDirection.setView(sViewDirection);
            TextView possibleAnswer = (TextView) sViewDirection.findViewById(R.id.txtDirection);
            TextView label = (TextView) sViewDirection.findViewById(R.id.txtRequirements);
            label.setText("All Possible Answer");
            possibleAnswer.setText("* " + ch1 + "\n* " + ch2 + "\n* " + ch3 + "\n* " + ch4);
            final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
            dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogDirection.show();
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogDirection.cancel();
                }
            });
        } else{
            char firstChar = correctAns.charAt(0);
            answer.setText(String.valueOf(firstChar));

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
    }
    public void correctAnswer(View view){
        updateScore(1);
        correct.setEnabled(false);
        correct.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        correct.setBackgroundResource(R.drawable.lifelineused);

        //for verifying mode
        givenWord.setTextColor(Color.GREEN);
        givenWord.setText(correctAns);

        //for fill in the blank
        answer.setText(correctAns);

        //for multiple choice

        if (correctAns.equals(choice1.getText())) {
            choice1.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice2.getText())) {
            choice2.setBackgroundResource(R.drawable.correctbutton);
        } else if (correctAns.equals(choice3.getText())) {
            choice3.setBackgroundResource(R.drawable.correctbutton);
        } else {
            choice4.setBackgroundResource(R.drawable.correctbutton);
        }

        disabledUserAccess();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                randomTime.this.runOnUiThread(new Runnable() {
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
            stopService(new Intent(randomTime.this, thinking.class));
            stopService(new Intent(randomTime.this, thinkingmusic.class));
            stopService(new Intent(randomTime.this, backgroundmusic.class));
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
                        stopService(new Intent(randomTime.this, thinkingmusic.class));
                        startService(new Intent(randomTime.this, backgroundmusic.class));
                        Intent intent = new Intent(randomTime.this, MainActivity.class);
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
