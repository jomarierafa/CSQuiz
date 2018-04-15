package com.example.jomarie.csquiz;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;


public class normalgame extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner spinner;
    public static String gameType;
    private ScrollView myScrollView;
    private SoundPlayer sound;
    private Button startNormal;
    private TextView numCourses, info;
    private RelativeLayout spinnerBackground, background;
    Animation animate;
    List<String> randomSubjects;
    ArrayList<String> subjects = new ArrayList<String>();
    int limit = 0 , checkboxChecked = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normalgame);
        sound = new SoundPlayer(this);
        startNormal = (Button) findViewById(R.id.startNormal);
        numCourses = (TextView) findViewById(R.id.reqCourses);
        info = (TextView) findViewById(R.id.info);
        myScrollView = (ScrollView) findViewById(R.id.scrollView2);
        background = (RelativeLayout) findViewById(R.id.scrollViewBackground);
        spinnerBackground = (RelativeLayout) findViewById(R.id.spinnerBackground);
        changeCourseFont();


        String[]paths;
        if(MainActivity.mode.equals("quickgame")) {
            numCourses.setText("Select 3");
            limit = 3;
            paths = new String[]{"", "Multiple Choice", "Fill in the Blank", "Verifying"};
        } else if(MainActivity.mode.equals("normalgame") || MainActivity.mode.equals("timelimit")){
            numCourses.setText("Select 5");
            limit = 5;
            paths = new String[]{"Play All", "Multiple Choice", "Fill in the Blank", "Verifying"};
        } else{
            info.setVisibility(View.VISIBLE);
            myScrollView.setVisibility(View.INVISIBLE);
            startNormal.setText("Start");
            paths = new String[]{"", "Multiple Choice", "", "Verifying"};
        }

        spinner = (Spinner)findViewById(R.id.spinGameMode);
        ArrayAdapter<String>adapter = new ArrayAdapter<String>(normalgame.this,
               R.layout.spinner_item,paths);

        background.getBackground().setAlpha(40);
        spinnerBackground.getBackground().setAlpha(40);

        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        animate = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myanimation);
        animate.setDuration(300);




    }




    public void numSubCheck(View view) {
        sound.playCheckboxSound();
        boolean checked = ((CheckBox) view).isChecked();
        CompoundButton buttonView = ((CheckBox) view);

        if (checked) {
            checkboxChecked++;
        } else {
            checkboxChecked--;
        }
        if (checkboxChecked > limit) {
            numCourses.startAnimation(animate);
            buttonView.setChecked(false);
            checkboxChecked = limit;
            return;
        } else if (checkboxChecked == limit){
            startNormal.setText("Start");
        } else {
            startNormal.setText("Play Random");
        }




        switch (view.getId()) {
            case R.id.checkBox:
                if (checked) {
                    subjects.add("AIntelligence");
                } else {
                    subjects.remove("AIntelligence");
                }
                break;
            case R.id.checkBox2:
                if (checked) {
                    subjects.add("COA");
                } else {
                    subjects.remove("COA");
                }
                break;
            case R.id.checkBox3:
                if (checked) {
                    subjects.add("DBMS");
                } else {
                    subjects.remove("DBMS");
                }
                break;
            case R.id.checkBox4:
                if (checked) {
                    subjects.add("Algo");
                } else {
                    subjects.remove("Algo");
                }
                break;
            case R.id.checkBox5:
                if (checked) {
                    subjects.add("LogicCircuit");
                } else {
                    subjects.remove("LogicCircuit");
                }
                break;
            case R.id.checkBox6:
                if (checked) {
                    subjects.add("OST");
                } else {
                    subjects.remove("OST");
                }
                break;
            case R.id.checkBox7:
                if (checked) {
                    subjects.add("ProgrammingLan");
                } else {
                    subjects.remove("ProgrammingLan");
                }
                break;
            case R.id.checkBox8:
                if (checked) {
                    subjects.add("WebProg");
                } else {
                    subjects.remove("WebProg");
                }
                break;
            case R.id.checkbox9:
                if (checked) {
                    subjects.add("Automata");
                } else {
                    subjects.remove("Automata");
                }
                break;
            case R.id.checkBox10:
                if (checked) {
                    subjects.add("Cisco");
                } else {
                    subjects.remove("Cisco");
                }
                break;
            case R.id.checkBox11:
                if (checked) {
                    subjects.add("IntroductionToCS");
                } else {
                    subjects.remove("IntroductionToCS");
                }
                break;
            case R.id.checkbox12:
                if (checked) {
                    subjects.add("OOP");
                } else {
                    subjects.remove("OOP");
                }
                break;
        }

    }



    public void changeCourseFont(){
        Typeface font = Typeface.createFromAsset(getAssets(), "freedom.ttf");
        ((CheckBox)findViewById(R.id.checkBox)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox2)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox3)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox4)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox5)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox6)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox7)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox8)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkbox9)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox10)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkBox11)).setTypeface(font);
        ((CheckBox)findViewById(R.id.checkbox12)).setTypeface(font);
        startNormal.setTypeface(font);
        numCourses.setTypeface(font);
        info.setTypeface(font);
    }

    public void doneSelection(View view){
        if(spinner.getSelectedItem().equals("")){
            Toasty.info(this, "Please Choose Quiz Type", Toast.LENGTH_SHORT, true).show();
            return;
        }
        stopService(new Intent(normalgame.this, backgroundmusic.class));
        sound.playButtonSound();
        Intent intent = new Intent(this, LoadingScreen.class);
        if(startNormal.getText().equals("Play Random")){
            randomSubjects = new ArrayList<>(Arrays.asList("Cisco","Automata","OOP","IntroductionToCS","AIntelligence","DBMS","Algo","COA","OST","WebProg","LogicCircuit","ProgrammingLan"));
            Collections.shuffle(randomSubjects);
            intent.putStringArrayListExtra("listOfsubjects", (ArrayList<String>) randomSubjects);
        }else {
            intent.putStringArrayListExtra("listOfsubjects", subjects);
        }
        startActivity(intent);

        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 0:
                gameType = "random";
                break;
            case 1:
                gameType = "multiple";
                break;
            case 2:
                gameType = "rumble";
                break;
            case 3:
                gameType = "verifying";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }



    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(normalgame.this, thinking.class));
            stopService(new Intent(normalgame.this, thinkingmusic.class));
            stopService(new Intent(normalgame.this, backgroundmusic.class));
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
                .setMessage("Back to MainMenu?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        Intent i = new Intent(normalgame.this, MainActivity.class);
                        startActivity(i);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                })
                .show();

    }
}
