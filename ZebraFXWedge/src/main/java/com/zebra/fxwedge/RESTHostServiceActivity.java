package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// The service can be launched using the graphical user interface, intent actions or adb.
//
// If the option "Start on boot" is enabled, the service will be automatically launched when the boot is complete.
//
//
// The service respond to two intent actions (both uses the category: android.intent.category.DEFAULT)
// - "com.zebra.fxwedge.startservice" sent on the component "com.zebra.fxwedge/com.zebra.fxwedge.StartServiceBroadcastReceiver":
//   Start the service.
// - "com.zebra.fxwedge.stopservice" sent on the component "com.zebra.fxwedge/com.zebra.fxwedge.StopServiceBroadcastReceiver":
//   Stop the service.
//
// The service can be started and stopped manually using the following adb commands:
//  - Start service:
//      adb shell am broadcast -a com.zebra.fxwedge.startservice -n com.zebra.fxwedge/com.zebra.fxwedge.StartServiceBroadcastReceiver
//  - Stop service:
//      adb shell am broadcast -a com.zebra.fxwedge.stopservice -n com.zebra.fxwedge/com.zebra.fxwedge.StopServiceBroadcastReceiver
//  - Setup service
//          The service can be configured using the following intent:
//          adb shell am broadcast -a com.zebra.fxwedge.setupservice -n com.zebra.fxwedge/com.zebra.fxwedge.SetupServiceBroadcastReceiver --es startonboot "true" --es allowexternalips "false"
//          The command must contain at least one of the extras:
//
//          - Configure autostart on boot:
//          --es startonboot "true"
//                  If the device get rebooted the service will start automatically once the reboot is completed.
//          --es startonboot "false"
//                  If the device is rebooted, the service will not be started (unless it has been setup/configured to boot on startup).
//
// TODO: finish to document setup intent API


public class RESTHostServiceActivity extends AppCompatActivity {
    private Switch mStartStopServiceSwitch = null;
    private TextView mDeviceIPTextView = null;
    protected static RESTHostServiceActivity mMainActivity;
    private RESTHostServiceWifiStateObserver mIPChangeObserver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restservice);

        mStartStopServiceSwitch = (Switch)findViewById(R.id.startStopServiceSwitch);
        mStartStopServiceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mStartStopServiceSwitch.setText(getString(R.string.serviceStarted));
                    if(!RESTHostService.isRunning(RESTHostServiceActivity.this))
                        RESTHostService.startService(RESTHostServiceActivity.this);
                    updateIP();
                }
                else
                {
                    mStartStopServiceSwitch.setText(getString(R.string.serviceStopped));
                    if(RESTHostService.isRunning(RESTHostServiceActivity.this))
                        RESTHostService.stopService(RESTHostServiceActivity.this);
                    updateIP();
                }
            }
        });

        mDeviceIPTextView = (TextView)findViewById(R.id.tv_ip);

        SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean startServiceOnBoot = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, false);
        if(startServiceOnBoot == true && RESTHostService.isRunning(this.getApplicationContext()) == false)
        {
            // we automatically start the service if the option StartOnBoot is set to true, and the service is not started
            RESTHostService.startService(this.getApplicationContext());
        }

    }

    @Override
    protected void onResume() {
        mMainActivity = this;
        if(mIPChangeObserver == null)
        {
            mIPChangeObserver = new RESTHostServiceWifiStateObserver(getApplicationContext(), new RESTHostServiceWifiStateObserver.IIPChangeObserver() {
                @Override
                public void onIPChanged(String newIP) {
                    updateIP();
                }
            });

            mIPChangeObserver.startObserver();
        }
        else if(mIPChangeObserver.isStarted() == false)
        {
            mIPChangeObserver.startObserver();
        }
        super.onResume();
        updateSwitches();
        updateIP();
    }

    protected void updateSwitches()
    {
        if(RESTHostService.isRunning(RESTHostServiceActivity.this))
        {
            setServiceStartedSwitchValues(true, getString(R.string.serviceStarted));
        }
        else
        {
            setServiceStartedSwitchValues(false, getString(R.string.serviceStopped));
        }
    }

    @Override
    protected void onPause() {
        mMainActivity = null;
        super.onPause();
        // Stop observing IP Changes
        if(mIPChangeObserver != null && mIPChangeObserver.isStarted())
        {
            mIPChangeObserver.stopObserver();
        }
        mIPChangeObserver = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.resthostserviceactivitymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_setup:
                startActivity(new Intent(RESTHostServiceActivity.this, FXWedgeSetupActivity.class));
                break;
            case R.id.menuitem_test:
                startActivity(new Intent(RESTHostServiceActivity.this, RestHostServiceTestActivity.class));
                break;
            case R.id.menuitem_license:
                startActivity(new Intent(RESTHostServiceActivity.this, LicenceActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setServiceStartedSwitchValues(final boolean checked, final String text)
    {
        mStartStopServiceSwitch.setChecked(checked);
        mStartStopServiceSwitch.setText(text);
    }

    private void updateIP()
    {
        if(mIPChangeObserver != null)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(RESTHostService.isRunning(RESTHostServiceActivity.this.getApplicationContext()) && mIPChangeObserver.isConnected())
                    {
                        String deviceIP = mIPChangeObserver.getIPAddress();
                        mDeviceIPTextView.setVisibility(View.VISIBLE);
                        mDeviceIPTextView.setText(getString(R.string.iptextviewprefix) + deviceIP);
                    }
                    else
                    {
                        mDeviceIPTextView.setVisibility(View.GONE);
                    }
                }
            });
        }
    }
}
