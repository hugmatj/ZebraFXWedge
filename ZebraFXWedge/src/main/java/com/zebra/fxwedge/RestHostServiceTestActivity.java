package com.zebra.fxwedge;

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

public class RestHostServiceTestActivity extends AppCompatActivity {

    /*
    Result view objects
     */
    private TextView et_results;
    private ScrollView sv_results;
    private String mResults = "";
    private boolean mOptmizeRefresh = true;
    private CheckBox cb_displayReadings = null;
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
                Bundle extraDataBundle = intent.getBundleExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA);
                FXReadsDataModel data = FXReadsDataModel.fromBundle(extraDataBundle);
                if (data != null) {
                    String sourceName = intent.getStringExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCENAME);
                    String sourceIP = intent.getStringExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCEIP);
                    addLineToResults("Source name: " + sourceName);
                    addLineToResults("Source IP: " + sourceIP);
                    addLineToResults(data.toString());
                }
            }
        }
    }
    FXBroadcastReceiver fxBroadcastReceiver = null;

    /*
    FX REST API Facade
     */
    FXReaderRESTApiFacade fxReaderRESTApiFacade = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_host_service_test);

        fxReaderRESTApiFacade = new FXReaderRESTApiFacade(this);

        et_results = (TextView)findViewById(R.id.et_results);
        sv_results = (ScrollView)findViewById(R.id.sv_results);
        cb_displayReadings = (CheckBox)findViewById(R.id.cb_showreadings);

        bt_start = (Button)findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fxReaderRESTApiFacade.startReading(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Start reading successful" : "Start reading error");
                        addLineToResults("Message: " + message);
                    }
                });
            }
        });

        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fxReaderRESTApiFacade.stopReading(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Stop reading successful" : "Stop reading error");
                        addLineToResults("Message: " + message);
                    }
                });
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
        IntentFilter filter = new IntentFilter(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_ACTION);
        filter.addCategory(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        fxBroadcastReceiver = new FXBroadcastReceiver();
        registerReceiver(fxBroadcastReceiver, filter);
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
        super.onPause();
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
                        RestHostServiceTestActivity.this.runOnUiThread(new Runnable() {
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
            RestHostServiceTestActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_results.setText(mResults);
                    sv_results.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}