package com.zebra.zebrafxwedgesample;

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
                Bundle extraDataBundle = intent.getBundleExtra(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA);
                FXReadsDataModel data = FXReadsDataModel.fromBundle(extraDataBundle);
                if (data != null)
                    addLineToResults(data.toString());
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
            if(source.equalsIgnoreCase(FXWedgeConstants.FX_INTENT_ACTION_GET_MODE) && status.equalsIgnoreCase(EResult.SUCCESS.toString()))
            {
                updateSettingsFromGetMode(intent);
            }
        }
    }
    FXIntentAPIBroadcastReceiver fxIntentAPIBroadcastReceiver = null;

    public enum EResult
    {
        SUCCESS("SUCCESS"),
        ERROR("ERROR");

        String mName = "";
        EResult(String name)
        {
            mName = name;
        }

        public String toString()
        {
            return mName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fxhardware_setup);

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
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_LOGIN);
            }
        });

        bt_setup = (Button)findViewById(R.id.bt_setup);
        bt_setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Setup FX Reader local cloud parameters.");
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_SETUP);
            }
        });

        bt_enroll = (Button)findViewById(R.id.bt_enroll);
        bt_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Enroll FX Reader to local cloud.");
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_ENROLL);
            }
        });

        bt_reboot = (Button)findViewById(R.id.bt_reboot);
        bt_reboot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Reboot FX Reader.");
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_REBOOT);
            }
        });

        bt_start = (Button)findViewById(R.id.bt_start);
        bt_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Start Reading.");
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_START_READING);
            }
        });

        bt_stop = (Button)findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Stop Reading");
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

        bt_getConfig = (Button) findViewById(R.id.bt_getconfig);
        bt_getConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Get FX Reader configuration");
                sendIntent(FXWedgeConstants.FX_INTENT_ACTION_GET_MODE);
            }
        });

        bt_getConnectedAntennas = (Button)findViewById(R.id.bt_getconnectedantennas);
        bt_getConnectedAntennas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement get mode

                addLineToResults("Get antenna status (connected/disconnected) : NOT IMPLEMENTED NOW");
            }
        });

        bt_setConfig = (Button)findViewById(R.id.bt_setconfig);
        bt_setConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLineToResults("Setting FX Reader Mode");
                Intent intent = new Intent(FXWedgeConstants.FX_INTENT_ACTION_SET_MODE);
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TYPE, getMode());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A1, ((CheckBox) findViewById(R.id.cb_A1)).isChecked());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A2, ((CheckBox) findViewById(R.id.cb_A2)).isChecked());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A3, ((CheckBox) findViewById(R.id.cb_A3)).isChecked());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A4, ((CheckBox) findViewById(R.id.cb_A4)).isChecked());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A1, sb_transmitPowerA1.getProgress()/10.0f);
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A2, sb_transmitPowerA2.getProgress()/10.0f);
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A3, sb_transmitPowerA3.getProgress()/10.0f);
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A4, sb_transmitPowerA4.getProgress()/10.0f);
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_UNIT, ((EditText) findViewById(R.id.et_intervalunit)).getText().toString());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_VALUE, Long.valueOf(((EditText) findViewById(R.id.et_interval)).getText().toString()));
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_MATCH, ((EditText) findViewById(R.id.et_filtermatch)).getText().toString());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_OPERATION, ((EditText) findViewById(R.id.et_filteroperation)).getText().toString());
                intent.putExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_VALUE, ((EditText) findViewById(R.id.et_filter)).getText().toString());
                sendBroadcast(intent);
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

    private void updateSettingsFromGetMode(Intent intent)
    {
        updateMode(intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TYPE));
        cb_A1.setChecked(intent.getBooleanExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A1, false));
        cb_A2.setChecked(intent.getBooleanExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A2, false));
        cb_A3.setChecked(intent.getBooleanExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A3, false));
        cb_A4.setChecked(intent.getBooleanExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A4, false));
        float transmitPowerAntenna1 = intent.getFloatExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A1, 10.0f);
        float transmitPowerAntenna2 = intent.getFloatExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A2, 10.0f);
        float transmitPowerAntenna3 = intent.getFloatExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A3, 10.0f);
        float transmitPowerAntenna4 = intent.getFloatExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A4, 10.0f);
        sb_transmitPowerA1.setProgress((int) (transmitPowerAntenna1 * 10.0f));
        tv_transmitPowerA1.setText(String.valueOf(transmitPowerAntenna1));
        sb_transmitPowerA2.setProgress((int) (transmitPowerAntenna2 * 10.0f));
        tv_transmitPowerA2.setText(String.valueOf(transmitPowerAntenna2));
        sb_transmitPowerA3.setProgress((int) (transmitPowerAntenna3 * 10.0f));
        tv_transmitPowerA3.setText(String.valueOf(transmitPowerAntenna3));
        sb_transmitPowerA4.setProgress((int) (transmitPowerAntenna4 * 10.0f));
        tv_transmitPowerA4.setText(String.valueOf(transmitPowerAntenna4));

        ((EditText) findViewById(R.id.et_interval)).setText(String.valueOf(intent.getLongExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_VALUE, 1)));
        ((EditText) findViewById(R.id.et_intervalunit)).setText(intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_UNIT));
        ((EditText) findViewById(R.id.et_filter)).setText(intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_VALUE));
        ((EditText) findViewById(R.id.et_filtermatch)).setText(intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_MATCH));
        ((EditText) findViewById(R.id.et_filteroperation)).setText(intent.getStringExtra(FXWedgeConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_OPERATION));
        addLineToResults("Update Settings from GetMode succeeded");
    }

    private void sendIntent(String action)
    {
        Intent intent = new Intent(action);
        sendBroadcast(intent);
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
        IntentFilter filter = new IntentFilter(FXWedgeConstants.FXDATA_BROADCAST_INTENT_ACTION);
        filter.addCategory(FXWedgeConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        fxBroadcastReceiver = new FXBroadcastReceiver();
        registerReceiver(fxBroadcastReceiver, filter);

        IntentFilter filterFXResults = new IntentFilter(FXWedgeConstants.FX_INTENT_ACTION_RESULT);
        filterFXResults.addCategory(FXWedgeConstants.FX_INTENT_CATEGORY);
        fxIntentAPIBroadcastReceiver = new FXHardwareSetupActivity.FXIntentAPIBroadcastReceiver();
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