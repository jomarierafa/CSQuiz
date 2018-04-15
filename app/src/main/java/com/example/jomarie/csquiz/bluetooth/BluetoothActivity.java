package com.example.jomarie.csquiz.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.jomarie.csquiz.DualPlayerMultiple;
import com.example.jomarie.csquiz.DualPlayerVer;
import com.example.jomarie.csquiz.DualplayerWinner;
import com.example.jomarie.csquiz.MainActivity;
import com.example.jomarie.csquiz.R;
import com.example.jomarie.csquiz.database.DatabaseHelper;
import com.example.jomarie.csquiz.gameMode;
import com.example.jomarie.csquiz.model.Leaderboardmodel;
import com.example.jomarie.csquiz.model.Question;
import com.example.jomarie.csquiz.quickThree;
import com.example.jomarie.csquiz.quickgame;
import com.example.jomarie.csquiz.sounds.SoundPlayer;
import com.example.jomarie.csquiz.sounds.backgroundmusic;
import com.example.jomarie.csquiz.sounds.thinkingmusic;
import com.example.jomarie.csquiz.timeLimit;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import es.dmoral.toasty.Toasty;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;


public class BluetoothActivity extends Activity implements AdapterView.OnItemClickListener{
    private static final String TAG = "BluetoothActivity";
    public  static LinearLayout gameMode, pairing;

    BluetoothAdapter mBluetoothAdapter;
    ImageButton btnEnableDisableDiscoverable, btnSearch;

    BluetoothConnectionService mBluetoothConnection;
    public  static  Button btnStartConnection;
    TextView enemyScore;
    StringBuilder message;
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    BluetoothDevice mBTDevice;

    public  ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public DeviceListAdapter mDeviceListAdapter;
    ListView newDevices;


    //Gaming Mode
    private LinearLayout lifelines;
    private TextView question, score, courseName, prwin, prlose, prdraw, clock, loading;
    private Button choice1, choice2, choice3, choice4;
    private ImageButton half, correct, doubleTip;
    private ProgressBar meter;
    private List<Question> mQuestionList;
    private DatabaseHelper mDBHelper;
    private String correctAns;
    private int mQuestionNumber = 0;
    private Timer timer;
    private ImageView timerIcon;
    private SoundPlayer sound;
    private int currentScore = 0, meterProgress = 50;
    private boolean doubleTipStatus = false;

    public Countdown timers = new Countdown(180 * 1000, 100);

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: State OFF");
                        Toast.makeText(context, "Bluetooth Off", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "bluetooth On", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    /**
     * Broadcast Reciever for changes maide to bluetooth states such as:
     * 1) Discoverabilty mode/ on/off or expire
     */
    private final BroadcastReceiver mBroadcastReciever2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.EXTRA_SCAN_MODE)) {
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

                switch (mode) {
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Toast.makeText(context, "Device can see in 30 seconds", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mBroadcastReceive2: Dicoverability Enabled");
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Toast.makeText(context, "able to receive connection", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mBroadcastReceive2: Dicoverability Disabled. Able to Recieve connections");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Toast.makeText(context, "Not able to recieve connections", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mBroadcastReceive2: Dicoverability Disabled. Not Able to Recieve connections");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(context, "Connecting...", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mBroadcastReceive2: Connecting....");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mBroadcastReceive2: Connected");
                        break;
                }
            }
        }
    };
    /**
     * Broadcast Receiver for listing devices that are not yet paired
     * -Executed by btnDiscover() method.
     */
    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Toast.makeText(context, "" + device.getName() + ": " + device.getAddress(), Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onReceive: " + device.getName() + ": " + device.getAddress());
                mDeviceListAdapter = new DeviceListAdapter(context, R.layout.deviceadapter_view, mBTDevices);
                newDevices.setAdapter(mDeviceListAdapter);
            }
        }
    };

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    mBTDevice = mDevice;
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");

                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {}
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                gameMode.setVisibility(View.VISIBLE);
                pairing.setVisibility(View.INVISIBLE);
                timers.start();
                stopService(new Intent(BluetoothActivity.this, backgroundmusic.class));
                startService(new Intent(BluetoothActivity.this, thinkingmusic.class));

            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "Horray!", Toast.LENGTH_SHORT).show();

            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                timers.cancel();
                gameMode.setEnabled(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: called.");
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReciever2);
            unregisterReceiver(mBroadcastReceiver3);
            unregisterReceiver(mBroadcastReceiver4);
            unregisterReceiver(mBroadcastReceiver5);
        }
        catch (final Exception exception) {
            // The receiver was not registered.
            // There is nothing to do in that case.
            // Everything is fine.
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        MainActivity.mode = "dual";
        lifelines = (LinearLayout) findViewById(R.id.blifeline);
        gameMode = (LinearLayout) findViewById(R.id.dualgamemode);
        pairing = (LinearLayout) findViewById(R.id.pairing);
        final Button btnONOFF = (Button) findViewById(R.id.btnOnOff);
        btnEnableDisableDiscoverable = (ImageButton) findViewById(R.id.btnDiscoverabilityOn);
        btnSearch = (ImageButton) findViewById(R.id.btnDiscoverDevices);
        newDevices = (ListView) findViewById(R.id.lvNewDevices);
        mBTDevices = new ArrayList<>();
        btnStartConnection = (Button) findViewById(R.id.btnStartConnection);

        message = new StringBuilder();
        question = (TextView) findViewById(R.id.bquestion);
        score = (TextView) findViewById(R.id.bscore);
        enemyScore = (TextView) findViewById(R.id.bEnemyScore);
        prwin = (TextView) findViewById(R.id.win);
        prlose = (TextView) findViewById(R.id.lose);
        prdraw = (TextView) findViewById(R.id.draw);
        choice1 = (Button) findViewById(R.id.bchoice1);
        choice2 = (Button) findViewById(R.id.bchoice2);
        choice3 = (Button) findViewById(R.id.bchoice3);
        choice4 = (Button) findViewById(R.id.bchoice4);
        courseName = (TextView) findViewById(R.id.bcourse);
        clock = (TextView) findViewById(R.id.btimer);
        half = (ImageButton) findViewById(R.id.bhalf);
        correct = (ImageButton) findViewById(R.id.bcorrect);
        doubleTip = (ImageButton) findViewById(R.id.bdouble);
        timerIcon = (ImageView) findViewById(R.id.bTimerIcon);
        sound = new SoundPlayer(this);
        mDBHelper = new DatabaseHelper(this);
        mQuestionList = mDBHelper.getListQuestion();

        meter = (ProgressBar) findViewById(R.id.bmeter);
        meter.setProgress(meterProgress);

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("incomingMessage"));

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        newDevices.setOnItemClickListener(BluetoothActivity.this);
        btnONOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: enabling/disabling bluetooth.");
                enableDisableBT();
            }
        });

        btnStartConnection.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                loading = (TextView) findViewById(R.id.loadingText);
                try {
                    btnStartConnection.setVisibility(View.INVISIBLE);
                    startConnection();
                } catch (Exception e){
                    btnStartConnection.setVisibility(View.VISIBLE);
                    Toast.makeText(BluetoothActivity.this, "Make Sure both device is connected.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        question.getBackground().setAlpha(40);
        newDevices.getBackground().setAlpha(40);

        IntentFilter filters = new IntentFilter();
        filters.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filters.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        this.registerReceiver(mBroadcastReceiver5, filters);
        getPersonalRecord();
        setFont();
        showDirectionBT();

    }

    public void setFont(){
        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        Typeface timerFont = Typeface.createFromAsset(getAssets(), "Crysta.ttf");
        updateQuestion();
        clock.setTypeface(timerFont);
        question.setTypeface(textFont);
        choice1.setTypeface(textFont);
        choice2.setTypeface(textFont);
        choice3.setTypeface(textFont);
        choice4.setTypeface(textFont);
        prwin.setTypeface(textFont);
        prlose.setTypeface(textFont);
        prdraw.setTypeface(textFont);
        courseName.setTypeface(textFont);
        btnStartConnection.setTypeface(textFont);
    }
    public void sendScore(){
        byte[] bytes = score.getText().toString().getBytes(Charset.defaultCharset());
        mBluetoothConnection.write(bytes);
        increment();
    }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent.getStringExtra("theMessage");

            message.append(text);
            enemyScore.setText(text);
            decrement();
        }
    };

    //crate method for starting connection
    // need to fair first
    public void startConnection(){
        startBTConnection(mBTDevice, MY_UUID_INSECURE);

    }

    /*
    *starting chat service method
     */

    public void startBTConnection(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");

        mBluetoothConnection.startClient(device,uuid);
    }
    public void enableDisableBT() {
        if (mBluetoothAdapter == null) {
            Toasty.error(this, "Bluetooth not Supported!", Toast.LENGTH_SHORT, true).show();
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(TAG, "enableDisableBT: enabling BT.");
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBTIntent);

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }
        if (mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.disable();

            IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
            registerReceiver(mBroadcastReceiver1, BTIntent);
        }

    }
    public void btnDiscoverabilityOn(View view) {
        sound.playButtonSound();
        Log.d(TAG, " btnDiscoverabilityOn: Making device discoverable for 300 seconds");

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
        startActivity(discoverableIntent);

        IntentFilter intentFilter = new IntentFilter(mBluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        intentFilter.addAction(String.valueOf(BluetoothAdapter.SCAN_MODE_CONNECTABLE));
        registerReceiver(mBroadcastReciever2, intentFilter);
    }
    public void btnDiscover(View view) {
        sound.playButtonSound();
        Log.d(TAG, "btnDiscover: Looking for unpaired devices.");

        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "btnDiscover: Canceling discovery.");

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
        if (!mBluetoothAdapter.isDiscovering()) {

            //check BT permissions in manifest
            checkBTPermissions();

            mBluetoothAdapter.startDiscovery();
            IntentFilter discoverDevicesIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver3, discoverDevicesIntent);
        }
    }

    /**
     * This method is required for all devices running API23+
     * Android must programmatically check the permissions for bluetooth. Putting the proper permissions
     * in the manifest is not enough.
     * <p>
     * NOTE: This will only execute on versions > LOLLIPOP because it is not needed otherwise.
     */
    private void checkBTPermissions() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        } else {
            Log.d(TAG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //first cancel discovery because its very memory intensive.
        mBluetoothAdapter.cancelDiscovery();

        Log.d(TAG, "onItemClick: You Clicked on a device.");
        String deviceName = mBTDevices.get(i).getName();
        String deviceAddress = mBTDevices.get(i).getAddress();

        Log.d(TAG, "onItemClick: deviceName = " + deviceName);
        Log.d(TAG, "onItemClick: deviceAddress = " + deviceAddress);

        //create the bond.
        //NOTE: Requires API 18+
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1){
            Toast.makeText(this, "Trying to pair with " + deviceName, Toast.LENGTH_SHORT).show();
            mBTDevices.get(i).createBond();
            mBTDevice = mBTDevices.get(i);
            mBluetoothConnection = new BluetoothConnectionService(BluetoothActivity.this);
        } else{
            Toast.makeText(this, "Below JellyBean is not supported", Toast.LENGTH_SHORT).show();
        }
    }


    //game mode

    private void updateScore(int point) {
        currentScore += point;
        score.setText(String.valueOf(currentScore));
        try{sendScore();}catch (Exception e){
            timers.cancel();
            mBluetoothAdapter.disable();
            try{mBluetoothConnection.close();}
            catch (Exception es){}
            finish();
            stopService(new Intent(BluetoothActivity.this, thinkingmusic.class));
            Intent intent = new Intent(BluetoothActivity.this, MainActivity.class);
            startActivity(intent);
        }
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
                BluetoothActivity.this.runOnUiThread(new Runnable() {
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
                BluetoothActivity.this.runOnUiThread(new Runnable() {
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
    public void doubleTip(View view){
        doubleTip.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        doubleTip.setBackgroundResource(R.drawable.lifelineused);
        doubleTipStatus = true;
        doubleTip.setEnabled(false);
    }

    //meter of two player
    private void increment(){
        if(meterProgress < 100){
            meterProgress += 5;
            meter.setProgress(meterProgress);
        }
        if(meterProgress == 95){
           timers.onFinish();
        }
    }
    private void decrement(){
        if(meterProgress > 0){
            meterProgress -= 5;
            meter.setProgress(meterProgress);
        }
        if(meterProgress == 5){
            timers.onFinish();
        }
    }

    public void showWinner(){
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_direction, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);
        TextView txtRequirement = (TextView) sViewDirection.findViewById(R.id.txtDirection);
        TextView lvlText = (TextView) sViewDirection.findViewById(R.id.txtRequirements);


        SharedPreferences prefs = getSharedPreferences("pr", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int win = 0, lose = 0, draw = 0;
        if (prefs != null) {
            win = prefs.getInt("win", 0);
            lose = prefs.getInt("lose", 0);
            draw = prefs.getInt("draw", 0);
        }

        if(Integer.parseInt((String) score.getText()) < Integer.parseInt((String) enemyScore.getText())){
            lvlText.setText("You Lose");
            editor.putInt("lose", lose + 1);
            editor.apply();
            txtRequirement.setText("New Personal Record: \nWin: " + win + "\nLose: " + (lose + 1) + "\nDraw: " + draw);
        } else if (Integer.parseInt((String) score.getText()) > Integer.parseInt((String) enemyScore.getText())){
            lvlText.setText("You Win");
            editor.putInt("win", win + 1);
            editor.apply();
            txtRequirement.setText("New Personal Record: \nWin: " + (win + 1) + "\nLose: " + lose + "\nDraw: " + draw);
        } else{
            lvlText.setText("Draw");
            editor.putInt("draw", draw + 1);
            editor.apply();
            txtRequirement.setText("New Personal Record: \nWin: " + win + "\nLose: " + lose + "\nDraw: " + (draw + 1));
        }


        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        txtRequirement.setTypeface(textFont);
        lvlText.setTypeface(textFont);

        final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
        dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDirection.setCanceledOnTouchOutside(false);
        dialogDirection.setCancelable(false);
        dialogDirection.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                timers.cancel();
                dialogDirection.cancel();
                finish();
                stopService(new Intent(BluetoothActivity.this, thinkingmusic.class));
                Intent intent = new Intent(BluetoothActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getPersonalRecord(){
        SharedPreferences prefs = getSharedPreferences("pr", MODE_PRIVATE);
        if (prefs != null) {
            int win = prefs.getInt("win", 0);
            int lose = prefs.getInt("lose", 0);
            int draw = prefs.getInt("draw", 0);
            prwin.setText("Win: " + win);
            prlose.setText("Lose: " + lose);
            prdraw.setText("Draw: " + draw);
        }
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
            try{mBluetoothConnection.close();} catch (Exception e){}
            mBluetoothAdapter.disable();
            showWinner();
            timers.cancel();
        }
    }

    public void showDirection(View view){
        showDirectionBT();
    }
    public void showDirectionBT(){
        android.support.v7.app.AlertDialog.Builder sBuilderDirection = new android.support.v7.app.AlertDialog.Builder(BluetoothActivity.this);
        View sViewDirection = getLayoutInflater().inflate(R.layout.dialog_bt_tips, null);
        Button ok = (Button) sViewDirection.findViewById(R.id.btn_ok);
        sBuilderDirection.setView(sViewDirection);


        Typeface textFont = Typeface.createFromAsset(getAssets(), "VTC-KomikaHandOne.ttf");
        TextView heading = (TextView) sViewDirection.findViewById(R.id.txtDirection);heading.setTypeface(textFont);
        TextView step1 = (TextView) sViewDirection.findViewById(R.id.step1);step1.setTypeface(textFont);
        TextView step2 = (TextView) sViewDirection.findViewById(R.id.step2);step2.setTypeface(textFont);
        TextView step3 = (TextView) sViewDirection.findViewById(R.id.step3);step3.setTypeface(textFont);
        TextView step4 = (TextView) sViewDirection.findViewById(R.id.step4);step4.setTypeface(textFont);

        final android.support.v7.app.AlertDialog dialogDirection = sBuilderDirection.create();
        dialogDirection.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDirection.show();
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                sound.playButtonSound();
                dialogDirection.cancel();
            }
        });
    }
    public void quitB(View view){
        sound.playButtonSound();
        exitByBackKey();
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

                    public void onClick(DialogInterface arg0, int arg1) {
                        timers.cancel();
                        mBluetoothAdapter.disable();
                        try{mBluetoothConnection.close();} catch (Exception e){}
                        finish();
                        stopService(new Intent(BluetoothActivity.this, thinkingmusic.class));
                        Intent intent = new Intent(BluetoothActivity.this, MainActivity.class);
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
