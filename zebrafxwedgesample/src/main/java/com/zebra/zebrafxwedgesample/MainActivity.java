package com.zebra.zebrafxwedgesample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    /*
    Result view objects
     */
    private TextView et_results;
    private ScrollView sv_results;
    private String mResults = "";
    private boolean mOptmizeRefresh = true;
    private CheckBox cb_displayReadings = null;
    private Button bt_setup = null;
    private Button bt_start = null;
    private Button bt_stop = null;
    private Button bt_clear = null;

    /*
    Handler and runnable to scroll down result view
    */
    private Handler mScrollDownHandler = null;
    private Runnable mScrollDownRunnable = null;

    /*
    FXData receiver
     */
    protected class FXBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(cb_displayReadings.isChecked()) {
                Bundle extraDataBundle = intent.getBundleExtra(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA);
                FXReadsDataModel data = FXReadsDataModel.fromBundle(extraDataBundle);
                if (data != null) {
                    String sourceName = intent.getStringExtra(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCENAME);
                    String sourceIP = intent.getStringExtra(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCEIP);
                    addLineToResults("Source name: " + sourceName);
                    addLineToResults("Source IP: " + sourceIP);
                    addLineToResults(data.toString());
                }
            }
        }
    }
    FXBroadcastReceiver fxBroadcastReceiver = null;

    /*
    FXIntent API Results receiver
     */
    protected class FXIntentAPIBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
                String source = intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_RESULT_EXTRA_SOURCE);
                String status = intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_RESULT_EXTRA_STATUS);
                String message = intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_RESULT_EXTRA_MESSAGE);
                addLineToResults("Result received from source: " + source);
                addLineToResults("Status: " + status);
                addLineToResults("Message: " + message);
        }
    }
    FXIntentAPIBroadcastReceiver fxIntentAPIBroadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        et_results = (TextView)findViewById(R.id.et_results);
        sv_results = (ScrollView)findViewById(R.id.sv_results);
        cb_displayReadings = (CheckBox)findViewById(R.id.cb_showreadings);

        bt_setup = (Button)findViewById(R.id.bt_setup);
        bt_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FXHardwareSetupActivity.class);
                startActivity(intent);
            }
        });

        bt_start = (Button)findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_START_READING);
            }
        });

        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_STOP_READING);
            }
        });

        bt_clear = (Button) findViewById(R.id.bt_clear);
        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mResults = "";
                et_results.setText(mResults);
            }
        });
    }

    @Override
    protected void onResume() {
        mScrollDownHandler = new Handler(Looper.getMainLooper());

        IntentFilter filterFXData = new IntentFilter(FXWedgeConstants.FXDATA_BROADCAST_INTENT_ACTION);
        filterFXData.addCategory(FXWedgeConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        fxBroadcastReceiver = new FXBroadcastReceiver();
        registerReceiver(fxBroadcastReceiver, filterFXData);

        IntentFilter filterFXResults = new IntentFilter(FXWedgeConstants.FX_INTENT_ACTION_RESULT);
        filterFXResults.addCategory(FXWedgeConstants.FX_INTENT_CATEGORY);
        fxIntentAPIBroadcastReceiver = new FXIntentAPIBroadcastReceiver();
        registerReceiver(fxIntentAPIBroadcastReceiver, filterFXResults);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if(mScrollDownRunnable != null)
        {
            mScrollDownHandler.removeCallbacks(mScrollDownRunnable);
            mScrollDownRunnable = null;
            mScrollDownHandler = null;
        }
        if(fxBroadcastReceiver != null)
        {
            unregisterReceiver(fxBroadcastReceiver);
            fxBroadcastReceiver = null;
        }
        if(fxIntentAPIBroadcastReceiver != null)
        {
            unregisterReceiver(fxIntentAPIBroadcastReceiver);
            fxIntentAPIBroadcastReceiver = null;
        }
        super.onPause();
    }

    private void sendIntent(String action)
    {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void addLineToResults(final String lineToAdd)
    {
        mResults += lineToAdd + "\n";
        updateAndScrollDownTextView();
    }

    private void updateAndScrollDownTextView() {
        if (mOptmizeRefresh) {
            if (mScrollDownRunnable == null) {
                mScrollDownRunnable = new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                et_results.setText(mResults);
                                sv_results.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        sv_results.fullScroll(ScrollView.FOCUS_DOWN);
                                    }
                                });
                            }
                        });
                    }
                };
            } else {
                // A new line has been added while we were waiting to scroll down
                // reset handler to repost it....
                mScrollDownHandler.removeCallbacks(mScrollDownRunnable);
            }
            mScrollDownHandler.postDelayed(mScrollDownRunnable, 300);
        } else {
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_results.setText(mResults);
                    sv_results.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}