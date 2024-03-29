package com.zebra.fxwedge;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.master.permissionhelper.PermissionHelper;
import com.zebra.deviceidentifierswrapper.DIHelper;
import com.zebra.deviceidentifierswrapper.IDIResultCallbacks;

import androidx.appcompat.app.AppCompatActivity;

// The service can be launched using the graphical user interface, intent actions or adb.
//
// If the option "Start on boot" is enabled, the service will be automatically launched when the boot is complete.
//
//
// The service respond to two intent actions (both uses the category: android.intent.category.DEFAULT)
// - "com.zebra.fxwedge.startservice" sent on the component "com.zebra.fxwedge/com.zebra.fxwedge.RESTHostServiceBroadcastReceiverStart":
//   Start the service.
// - "com.zebra.fxwedge.stopservice" sent on the component "com.zebra.fxwedge/com.zebra.fxwedge.RESTHostServiceBroadcastReceiverStop":
//   Stop the service.
//
// The service can be started and stopped manually using the following adb commands:
//  - Start service:
//      adb shell am broadcast -a com.zebra.fxwedge.startservice -n com.zebra.fxwedge/com.zebra.fxwedge.RESTHostServiceBroadcastReceiverStart
//  - Stop service:
//      adb shell am broadcast -a com.zebra.fxwedge.stopservice -n com.zebra.fxwedge/com.zebra.fxwedge.RESTHostServiceBroadcastReceiverStop
//  - Setup service
//          The service can be configured using the following intent:
//          adb shell am broadcast -a com.zebra.fxwedge.setupservice -n com.zebra.fxwedge/com.zebra.fxwedge.RESTHostServiceBroadcastReceiverSetup --es startonboot "true"
//          The command must contain at least one of the extras:
//
//          - Configure autostart on boot:
//          --es startonboot "true"
//                  If the device get rebooted the service will start automatically once the reboot is completed.
//          --es startonboot "false"
//                  If the device is rebooted, the service will not be started (unless it has been setup/configured to boot on startup).
//
//          - Setup FX Reader and Android local webservice
//          --es setfxip "192.168.1.1"
//                  Set FX IP
//          --es setfxname "FXA0DB423"
//                  Set FX Reader Name
//          --es serverport "5000"
//                  Set the port of the android local webserver
//          --es login "admin"
//                  Set the login information to setup the FXReader
//          --es password "change"
//                  Set the password information to setup the FXReader


public class RESTHostServiceActivity extends AppCompatActivity {
    private Switch mStartStopServiceSwitch = null;
    private TextView mDeviceIPTextView = null;
    private TextView mDeviceSerialNumber = null;
    protected static RESTHostServiceActivity mMainActivity;
    private RESTHostServiceNetworkStateObserver mIPChangeObserver = null;

    private final static String TAG = "FXWedgeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkPermissions();

        if(ZebraDeviceHelper.isZebraDevice(this) == false)
        {
            setContentView(R.layout.activity_onlyonzebradevices);
            return;
        }

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
        mDeviceSerialNumber = (TextView)findViewById(R.id.tv_serial);

        SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        boolean startServiceOnBoot = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, false);
        if(startServiceOnBoot == true && RESTHostService.isRunning(this.getApplicationContext()) == false)
        {
            // we automatically start the service if the option StartOnBoot is set to true, and the service is not started
            RESTHostService.startService(this.getApplicationContext());
        }

    }

    private void checkPermissions() {
        PermissionHelper permissionHelper = new PermissionHelper(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        permissionHelper.request(new PermissionHelper.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                Log.d(TAG, "onPermissionGranted() called");
                // TODO: MANAGE ALL FILES ON API 30
                /*
                    if(Environment.isExternalStorageManager() == false)
                    {

                    }
                */
            }

            @Override
            public void onIndividualPermissionGranted(String[] grantedPermission) {
                Log.d(TAG, "onIndividualPermissionGranted() called with: grantedPermission = [" + TextUtils.join(",",grantedPermission) + "]");
            }

            @Override
            public void onPermissionDenied() {
                Log.d(TAG, "onPermissionDenied() called");
            }

            @Override
            public void onPermissionDeniedBySystem() {
                Log.d(TAG, "onPermissionDeniedBySystem() called");
            }
        });
    }

    @Override
    protected void onResume() {
        if(ZebraDeviceHelper.isZebraDevice(this)) {
            mMainActivity = this;
            if (mIPChangeObserver == null) {
                mIPChangeObserver = new RESTHostServiceNetworkStateObserver(getApplicationContext(), new RESTHostServiceNetworkStateObserver.IIPChangeObserver() {
                    @Override
                    public void onIPChanged(String newIP) {
                        updateIP();
                    }
                });

                mIPChangeObserver.startObserver();
            } else if (mIPChangeObserver.isStarted() == false) {
                mIPChangeObserver.startObserver();
            }
            updateSwitches();
            updateIP();
            updateSerialNumber();
        }
        super.onResume();
    }

    protected void updateSerialNumber()
    {
        DIHelper.getSerialNumber(this, new IDIResultCallbacks() {
            @Override
            public void onSuccess(String message) {
                final String fMessage = message;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceSerialNumber.setText("Device Serial: " + fMessage);
                        FXWedgeStaticConfig.mDeviceSerialNumber = fMessage;
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDeviceSerialNumber.setText("Device Serial: " + "Error retrieving serial number");
                    }
                });
            }

            @Override
            public void onDebugStatus(String message) {
                Log.d(TAG, message);
            }
        });

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
        if(ZebraDeviceHelper.isZebraDevice(this)) {
            // Stop observing IP Changes
            if (mIPChangeObserver != null && mIPChangeObserver.isStarted()) {
                mIPChangeObserver.stopObserver();
            }
            mIPChangeObserver = null;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.resthostserviceactivitymenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuitem_fxwedgesetup:
                if(ZebraDeviceHelper.isZebraDevice(this)) {
                    startActivity(new Intent(RESTHostServiceActivity.this, FXWedgeSetupActivity.class));
                }
                else
                {
                    Toast.makeText(this, getString(R.string.zebra_enterprise_services_notification_text_onlyzebra), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menuitem_setupfxhardware:
                if(ZebraDeviceHelper.isZebraDevice(this)) {
                    startActivity(new Intent(RESTHostServiceActivity.this, FXHardwareSetupActivity.class));
                }
                else
                {
                    Toast.makeText(this, getString(R.string.zebra_enterprise_services_notification_text_onlyzebra), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.menuitem_test:
                if(ZebraDeviceHelper.isZebraDevice(this)) {
                    startActivity(new Intent(RESTHostServiceActivity.this, RestHostServiceTestActivity.class));
                }
                else
                {
                    Toast.makeText(this, getString(R.string.zebra_enterprise_services_notification_text_onlyzebra), Toast.LENGTH_LONG).show();
                }
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
