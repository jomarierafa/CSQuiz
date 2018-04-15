package com.example.jomarie.csquiz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinking;
import com.example.jomarie.csquiz.sounds.thinkingmusic;

import org.w3c.dom.Text;

public class DualplayerWinner extends Activity {
    private String p1Score, p2Score;
    private TextView p1winnerText, p2winnerText, txtP1score, txtP2score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dualplayer_winner);

        p1winnerText = (TextView) findViewById(R.id.textView14);
        p2winnerText = (TextView) findViewById(R.id.textView11);
        txtP1score = (TextView) findViewById(R.id.textView15);
        txtP2score = (TextView) findViewById(R.id.textView12);


        p1Score = getIntent().getStringExtra("p1score");
        p2Score = getIntent().getStringExtra("p2score");

        txtP1score.setText(p1Score);
        txtP2score.setText(p2Score);
        int scoreP1 = Integer.parseInt(p1Score);
        int scorep2 = Integer.parseInt(p2Score);

        if(scoreP1 > scorep2){
            p1winnerText.setText("You Win!");
            p2winnerText.setText("You Lose!");
            txtP1score.setBackgroundResource(R.drawable.correctbutton);
            txtP2score.setBackgroundResource(R.drawable.wrongbutton);
            p1winnerText.setTextColor(Color.GREEN);
            p2winnerText.setTextColor(Color.RED);
        } else if(scoreP1 < scorep2){
            p2winnerText.setText("You Win!");
            p1winnerText.setText("You Lose!");
            txtP2score.setBackgroundResource(R.drawable.correctbutton);
            txtP1score.setBackgroundResource(R.drawable.wrongbutton);
            p2winnerText.setTextColor(Color.GREEN);
            p1winnerText.setTextColor(Color.RED);
        } else{
            p1winnerText.setText("Draw!");
            p2winnerText.setText("Draw!");
            p1winnerText.setTextColor(Color.MAGENTA);
            p2winnerText.setTextColor(Color.MAGENTA);
        }

    }

    //back button of android
    @Override
    protected void onPause() {
        super.onPause();
        if(!this.isFinishing()){
            stopService(new Intent(DualplayerWinner.this, thinking.class));
            stopService(new Intent(DualplayerWinner.this, thinkingmusic.class));
            stopService(new Intent(DualplayerWinner.this, backgroundmusic.class));
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
                        finish();
                        Intent intent = new Intent(DualplayerWinner.this, MainActivity.class);
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
