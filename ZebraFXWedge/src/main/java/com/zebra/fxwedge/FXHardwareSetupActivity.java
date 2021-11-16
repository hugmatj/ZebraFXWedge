package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class FXHardwareSetupActivity extends AppCompatActivity {

    private static String TAG = "FXHDWSETUP";

    /*
    Result view objects
     */
    private TextView et_results;
    private ScrollView sv_results;
    private String mResults = "";
    private boolean mOptmizeRefresh = true;
    private CheckBox cb_displayReadings = null;
    private Button bt_login = null;
    private Button bt_setup = null;
    private Button bt_enroll = null;
    private Button bt_reboot = null;
    private Button bt_start = null;
    private Button bt_stop = null;
    private Button bt_clear = null;
    private Button bt_getConfig = null;
    private Button bt_getConnectedAntennas = null;
    private Button bt_setConfig = null;

    private CheckBox cb_A1 = null;
    private CheckBox cb_A2 = null;
    private CheckBox cb_A3 = null;
    private CheckBox cb_A4 = null;
    private SeekBar sb_transmitPowerA1 = null;
    private SeekBar sb_transmitPowerA2 = null;
    private SeekBar sb_transmitPowerA3 = null;
    private SeekBar sb_transmitPowerA4 = null;
    private TextView tv_transmitPowerA1 = null;
    private TextView tv_transmitPowerA2 = null;
    private TextView tv_transmitPowerA3 = null;
    private TextView tv_transmitPowerA4 = null;

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
        setContentView(R.layout.activity_fxhardwaresetup);

        fxReaderRESTApiFacade = new FXReaderRESTApiFacade(this);

        et_results = (TextView)findViewById(R.id.et_results);
        sv_results = (ScrollView)findViewById(R.id.sv_results);
        cb_displayReadings = (CheckBox)findViewById(R.id.cb_showreadings);

        tv_transmitPowerA1 = (TextView)findViewById(R.id.tv_power_a1);
        sb_transmitPowerA1 = (SeekBar)findViewById(R.id.sb_power_a1);
        sb_transmitPowerA1.setMax(290);
        sb_transmitPowerA1.setMin(0);
        sb_transmitPowerA1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_transmitPowerA1.setText(String.valueOf(progress/10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_transmitPowerA1.setText(String.valueOf(sb_transmitPowerA1.getProgress()/10.0));
            }
        });

        tv_transmitPowerA2 = (TextView)findViewById(R.id.tv_power_a2);
        sb_transmitPowerA2 = (SeekBar)findViewById(R.id.sb_power_a2);
        sb_transmitPowerA2.setMax(290);
        sb_transmitPowerA2.setMin(0);
        sb_transmitPowerA2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_transmitPowerA2.setText(String.valueOf(progress/10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_transmitPowerA2.setText(String.valueOf(sb_transmitPowerA2.getProgress()/10.0));
            }
        });

        tv_transmitPowerA3 = (TextView)findViewById(R.id.tv_power_a3);
        sb_transmitPowerA3 = (SeekBar)findViewById(R.id.sb_power_a3);
        sb_transmitPowerA3.setMax(290);
        sb_transmitPowerA3.setMin(0);
        sb_transmitPowerA3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_transmitPowerA3.setText(String.valueOf(progress/10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_transmitPowerA3.setText(String.valueOf(sb_transmitPowerA3.getProgress()/10.0));
            }
        });

        tv_transmitPowerA4 = (TextView)findViewById(R.id.tv_power_a4);
        sb_transmitPowerA4 = (SeekBar)findViewById(R.id.sb_power_a4);
        sb_transmitPowerA4.setMax(290);
        sb_transmitPowerA4.setMin(0);
        sb_transmitPowerA4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_transmitPowerA4.setText(String.valueOf(progress/10.0));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                tv_transmitPowerA4.setText(String.valueOf(sb_transmitPowerA4.getProgress()/10.0));
            }
        });

        cb_A1 = (CheckBox)findViewById(R.id.cb_A1);
        cb_A1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a1, R.id.tv_power_a1, R.id.sb_power_a1}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        cb_A2 = (CheckBox)findViewById(R.id.cb_A2);
        cb_A2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a2, R.id.tv_power_a2, R.id.sb_power_a2}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        cb_A3 = (CheckBox)findViewById(R.id.cb_A3);
        cb_A3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a3, R.id.tv_power_a3, R.id.sb_power_a3}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        cb_A4 = (CheckBox)findViewById(R.id.cb_A4);
        cb_A4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a4, R.id.tv_power_a4, R.id.sb_power_a4}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        bt_login = (Button)findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Login to FX Reader.");
                fxReaderRESTApiFacade.login(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Login successful" : "Login error");
                        addLineToResults("Message: " + message);
                    }
                });
            }
        });

        bt_setup = (Button)findViewById(R.id.bt_setup);
        bt_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Setup FX Reader local cloud parameters.");
                fxReaderRESTApiFacade.setupFxReader(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Setup successful" : "Setup error");
                        addLineToResults("Message: " + message);
                    }
                });
            }
        });

        bt_enroll = (Button)findViewById(R.id.bt_enroll);
        bt_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Enroll FX Reader to local cloud.");
                fxReaderRESTApiFacade.enrollToCloud(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Enroll successful" : "Enroll error");
                        addLineToResults("Message: " + message);
                    }
                });
            }
        });

        bt_reboot = (Button)findViewById(R.id.bt_reboot);
        bt_reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Reboot FX Reader.");
                fxReaderRESTApiFacade.reboot(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        addLineToResults(result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Reboot successful" : "Reboot error");
                        addLineToResults("Message: " + message);
                    }
                });
            }
        });

        bt_start = (Button)findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Start Reading.");
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
                addLineToResults("Stop Reading");
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

        bt_getConfig = (Button) findViewById(R.id.bt_getconfig);
        bt_getConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Get FX Reader configuration");
                fxReaderRESTApiFacade.getMode(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        Log.d(TAG, "getModeResult: " + result.toString());
                        Log.d(TAG, "getModeMessage: " + message);
                        if(result == FXReaderRESTApiFacade.EResult.SUCCESS) {
                            FXReaderRESTApiFacade.GetSetModeConfig setmode = FXReaderRESTApiFacade.getSetModeConfigFromJSon(message);
                            sb_transmitPowerA1.setProgress((int) (setmode.transmitPowerAntenna1 * 10.0f));
                            tv_transmitPowerA1.setText(String.valueOf(setmode.transmitPowerAntenna1));
                            sb_transmitPowerA2.setProgress((int) (setmode.transmitPowerAntenna2 * 10.0f));
                            tv_transmitPowerA2.setText(String.valueOf(setmode.transmitPowerAntenna2));
                            sb_transmitPowerA3.setProgress((int) (setmode.transmitPowerAntenna3 * 10.0f));
                            tv_transmitPowerA3.setText(String.valueOf(setmode.transmitPowerAntenna3));
                            sb_transmitPowerA4.setProgress((int) (setmode.transmitPowerAntenna4 * 10.0f));
                            tv_transmitPowerA4.setText(String.valueOf(setmode.transmitPowerAntenna4));
                            cb_A1.setChecked(setmode.enableAntenna1);
                            cb_A2.setChecked(setmode.enableAntenna2);
                            cb_A3.setChecked(setmode.enableAntenna3);
                            cb_A4.setChecked(setmode.enableAntenna4);
                            updateMode(setmode.type);
                            ((EditText) findViewById(R.id.et_interval)).setText(String.valueOf(setmode.modeSpecificSettings.interval.value));
                            ((EditText) findViewById(R.id.et_intervalunit)).setText(setmode.modeSpecificSettings.interval.unit);
                            ((EditText) findViewById(R.id.et_filter)).setText(setmode.filter.value);
                            ((EditText) findViewById(R.id.et_filtermatch)).setText(setmode.filter.match);
                            ((EditText) findViewById(R.id.et_filteroperation)).setText(setmode.filter.operation);
                            addLineToResults("Get Config succeeded");
                            addLineToResults("JSON Configuration received:\n" + message);
                        }
                        else
                        {
                            addLineToResults("Get Config error: " + message);
                        }
                    }
                });
            }
        });

        bt_getConnectedAntennas = (Button)findViewById(R.id.bt_getconnectedantennas);
        bt_getConnectedAntennas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Get antenna status (connected/disconnected)");
                fxReaderRESTApiFacade.getStatus(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        if(result == FXReaderRESTApiFacade.EResult.SUCCESS)
                        {
                            addLineToResults("Get Antennas status succeeded");
                            FXReaderRESTApiFacade.GetStatusData getStatusData = FXReaderRESTApiFacade.GetStatusData.fromJSon(message);
                            addLineToResults("Antenna1:" + getStatusData.antennas.antenna1);
                            addLineToResults("Antenna2:" + getStatusData.antennas.antenna2);
                            addLineToResults("Antenna3:" + getStatusData.antennas.antenna3);
                            addLineToResults("Antenna4:" + getStatusData.antennas.antenna4);
                            ((CheckBox) findViewById(R.id.cb_A1)).setChecked(getStatusData.antennas.antenna1.equalsIgnoreCase("connected"));
                            ((CheckBox) findViewById(R.id.cb_A2)).setChecked(getStatusData.antennas.antenna2.equalsIgnoreCase("connected"));
                            ((CheckBox) findViewById(R.id.cb_A3)).setChecked(getStatusData.antennas.antenna3.equalsIgnoreCase("connected"));
                            ((CheckBox) findViewById(R.id.cb_A4)).setChecked(getStatusData.antennas.antenna4.equalsIgnoreCase("connected"));
                        }
                        else
                        {
                            addLineToResults("Get Antennas status error: " + message);
                        }
                    }
                });
            }
        });

        bt_setConfig = (Button)findViewById(R.id.bt_setconfig);
        bt_setConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Setting FX Reader configuration");
                FXReaderRESTApiFacade.GetSetModeConfig setmode = new FXReaderRESTApiFacade.GetSetModeConfig();
                setmode.enableAntenna1 = ((CheckBox) findViewById(R.id.cb_A1)).isChecked();
                setmode.enableAntenna2 = ((CheckBox) findViewById(R.id.cb_A2)).isChecked();
                setmode.enableAntenna3 = ((CheckBox) findViewById(R.id.cb_A3)).isChecked();
                setmode.enableAntenna4 = ((CheckBox) findViewById(R.id.cb_A4)).isChecked();
                setmode.transmitPowerAntenna1 = sb_transmitPowerA1.getProgress()/10.0f;
                setmode.transmitPowerAntenna2 = sb_transmitPowerA2.getProgress()/10.0f;
                setmode.transmitPowerAntenna3 = sb_transmitPowerA3.getProgress()/10.0f;
                setmode.transmitPowerAntenna4 = sb_transmitPowerA4.getProgress()/10.0f;
                setmode.type = getMode();
                setmode.modeSpecificSettings.interval.value = Long.valueOf(((EditText) findViewById(R.id.et_interval)).getText().toString());
                setmode.modeSpecificSettings.interval.unit = ((EditText) findViewById(R.id.et_intervalunit)).getText().toString();
                setmode.filter.value = ((EditText) findViewById(R.id.et_filter)).getText().toString();
                setmode.filter.match = ((EditText) findViewById(R.id.et_filtermatch)).getText().toString();
                setmode.filter.operation = ((EditText) findViewById(R.id.et_filteroperation)).getText().toString();
                fxReaderRESTApiFacade.setMode(new FXReaderRESTApiFacade.RESTAPICallCallback() {
                    @Override
                    public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                        if(result == FXReaderRESTApiFacade.EResult.SUCCESS)
                        {
                            addLineToResults("SetMode succeeded: " + message);
                        }
                        else
                        {
                            addLineToResults("SetMode error: " + message);
                        }
                    }
                }, setmode);
            }
        });

        ((RadioGroup)findViewById(R.id.rb_mode)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_inventory)
                {
                    setVisibility(new int[]{R.id.tv_interval_title, R.id.et_interval, R.id.tv_intervalunit_title, R.id.et_intervalunit, R.id.bt_setconfig}, View.VISIBLE);
                }
                else
                {
                    setVisibility(new int[]{R.id.tv_interval_title, R.id.et_interval, R.id.tv_intervalunit_title, R.id.et_intervalunit, R.id.bt_setconfig}, View.GONE);
                }
            }
        });
    }

    private void setVisibility(int[] ids, int visibility)
    {
        for(int id : ids)
        {
            findViewById(id).setVisibility(visibility);
        }
    }

    private void setupAntennaCheckboxes()
    {
        ((CheckBox) findViewById(R.id.cb_A1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a1, R.id.tv_power_a1, R.id.sb_power_a1}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        ((CheckBox) findViewById(R.id.cb_A2)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a2, R.id.tv_power_a2, R.id.sb_power_a2}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        ((CheckBox) findViewById(R.id.cb_A3)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a3, R.id.tv_power_a3, R.id.sb_power_a3}, isChecked ? View.VISIBLE : View.GONE);
            }
        });

        ((CheckBox) findViewById(R.id.cb_A4)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setVisibility(new int[]{R.id.tv_power_title_a4, R.id.tv_power_a4, R.id.sb_power_a4}, isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void updateMode(String mode)
    {
        switch(mode)
        {
            case "simple":
                ((RadioButton)findViewById(R.id.rb_simple)).setChecked(true);
                break;
            case "portal":
                ((RadioButton)findViewById(R.id.rb_portal)).setChecked(true);
                break;
            case "conveyor":
                ((RadioButton)findViewById(R.id.rb_conveyor)).setChecked(true);
                break;
            case "custom":
                ((RadioButton)findViewById(R.id.rb_custom)).setChecked(true);
                break;
            default:
                ((RadioButton)findViewById(R.id.rb_inventory)).setChecked(true);
        }
    }

    private String getMode()
    {
        if(((RadioButton)findViewById(R.id.rb_simple)).isChecked())
            return "simple";
        if(((RadioButton)findViewById(R.id.rb_portal)).isChecked())
            return "portal";
        if(((RadioButton)findViewById(R.id.rb_conveyor)).isChecked())
            return "conveyor";
        if(((RadioButton)findViewById(R.id.rb_custom)).isChecked())
            return "custom";
        return "inventory";
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
                        FXHardwareSetupActivity.this.runOnUiThread(new Runnable() {
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
            FXHardwareSetupActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    et_results.setText(mResults);
                    sv_results.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}