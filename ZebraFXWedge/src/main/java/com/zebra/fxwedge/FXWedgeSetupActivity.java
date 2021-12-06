package com.zebra.fxwedge;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FXWedgeSetupActivity extends AppCompatActivity {

    private Switch mAutoStartServiceOnBootSwitch = null;
    private EditText mFXIPTextView = null;
    private EditText mFXName = null;
    private EditText mServerPortTextView = null;
    private EditText mFXLogin = null;
    private EditText mFXPassword = null;
    private Button mButtonSave = null;
    private Button mButtonImportConfig = null;
    private Button mButtonExportConfig = null;
    private Button mButtonDeleteConfig = null;
    private Button mButtonGetLocation = null;
    private CheckBox mEnableForwarding = null;
    private EditText mForwardIP = null;
    private EditText mForwardPort = null;
    private CheckBox mEnableIOTAForwarding = null;
    private EditText mIOTAForwardingAPIKey = null;
    private EditText mIOTAForwardingEndpoint = null;
    private EditText mDeviceLatitude = null;
    private EditText mDeviceLongitude = null;

    public static FXWedgeSetupActivity mMainActivity = null;

    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fxwedge_setup);

        mMainActivity = this;

        mAutoStartServiceOnBootSwitch = (Switch) findViewById(R.id.startOnBootSwitch);
        mAutoStartServiceOnBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mAutoStartServiceOnBootSwitch.setText(getString(R.string.startOnBoot));
                } else {
                    mAutoStartServiceOnBootSwitch.setText(getString(R.string.doNothingOnBoot));
                }
                SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, isChecked);
                editor.commit();
                FXWedgeStaticConfig.getFromSharedPreferences(FXWedgeSetupActivity.this);
            }
        });

        mFXName = (EditText) findViewById(R.id.et_fxname);
        mFXIPTextView = (EditText) findViewById(R.id.et_fxip);
        mServerPortTextView = (EditText) findViewById(R.id.et_serverport);
        mFXLogin = (EditText) findViewById(R.id.et_fxlogin);
        mFXPassword = (EditText) findViewById(R.id.et_fxpassword);
        mEnableForwarding = (CheckBox) findViewById(R.id.cb_allow_forwarding);
        mForwardIP = (EditText) findViewById(R.id.et_forwardip);
        mForwardPort = (EditText) findViewById(R.id.et_forwardport);
        mEnableIOTAForwarding = (CheckBox) findViewById(R.id.cb_allow_iotaforwarding);
        mIOTAForwardingAPIKey = (EditText) findViewById(R.id.et_apikey);
        mIOTAForwardingEndpoint = (EditText) findViewById(R.id.et_iotaserver);
        mDeviceLatitude = (EditText) findViewById(R.id.et_latitude);
        mDeviceLongitude = (EditText) findViewById(R.id.et_longitude);

        FXWedgeStaticConfig.getFromSharedPreferences(this);
        updateUIElements();

        mButtonSave = (Button) findViewById(R.id.bt_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save all preferences
                savePreferences();
                // Update config from preferences
                FXWedgeStaticConfig.getFromSharedPreferences(FXWedgeSetupActivity.this);
                Toast.makeText(FXWedgeSetupActivity.this, "Config saved", Toast.LENGTH_SHORT).show();

            }
        });

        mButtonGetLocation = (Button) findViewById(R.id.bt_getlocation);
        mButtonGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateLocationFused();
            }
        });

        mButtonExportConfig = (Button) findViewById(R.id.bt_export);
        mButtonExportConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = getExternalFilesDir(null);
                File filePath = new File(path, "config.json");
                try {
                    boolean result = FXWedgeStaticConfig.writeConfig(FXWedgeSetupActivity.this, filePath.getPath());
                    if (result) {
                        Toast.makeText(FXWedgeSetupActivity.this, "Config exported successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(FXWedgeSetupActivity.this, "Error trying to export configuration", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FXWedgeSetupActivity.this, "Error trying to export configuration:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        mButtonImportConfig = (Button) findViewById(R.id.bt_import);
        mButtonImportConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = getExternalFilesDir(null);
                File filePath = new File(path, "config.json");
                try {
                    boolean result = FXWedgeStaticConfig.readConfig(FXWedgeSetupActivity.this, filePath.getPath());
                    if (result) {
                        Toast.makeText(FXWedgeSetupActivity.this, "Config imported successfully", Toast.LENGTH_SHORT).show();
                        updateUIElements();
                        updateUglyStatics();
                    } else {
                        Toast.makeText(FXWedgeSetupActivity.this, "Error trying to import configuration", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(FXWedgeSetupActivity.this, "Error trying to import configuration:\n" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        mButtonDeleteConfig = (Button) findViewById(R.id.bt_delete);
        mButtonDeleteConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File path = getFilesDir();
                File filePath = new File(path, "config.json");
                if(filePath.exists())
                    filePath.delete();
                Toast.makeText(FXWedgeSetupActivity.this, "Config file deleted successfully.", Toast.LENGTH_SHORT).show();
            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    final Location flocation = location;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDeviceLatitude.setText(String.valueOf(flocation.getLatitude()));
                            mDeviceLongitude.setText(String.valueOf(flocation.getLongitude()));
                            stopLocationUpdates();
                        }
                    });
                    break;
                }
            }
        };
    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        if(locationRequest == null) {
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
        locationRequest = null;
    }

    private void updateUIElements() {
        mFXName.setText(FXWedgeStaticConfig.mFXName);
        mFXIPTextView.setText(FXWedgeStaticConfig.mFXIp);
        mServerPortTextView.setText(String.valueOf(FXWedgeStaticConfig.mServerPort));
        setAutoStartServiceOnBootSwitch(FXWedgeStaticConfig.mStartServiceOnBoot);
        mFXLogin.setText(FXWedgeStaticConfig.mFXLogin);
        mFXPassword.setText(FXWedgeStaticConfig.mFXPassword);

        mEnableForwarding.setChecked(FXWedgeStaticConfig.mEnableForwarding);

        mForwardIP.setText(FXWedgeStaticConfig.mForwardIP);

        mForwardPort.setText(String.valueOf(FXWedgeStaticConfig.mForwardPort));

        mEnableIOTAForwarding.setChecked(FXWedgeStaticConfig.mEnableIOTAForwarding);
        mIOTAForwardingAPIKey.setText(FXWedgeStaticConfig.mIOTAForwardingKey);

        mIOTAForwardingEndpoint.setText(FXWedgeStaticConfig.mIOTAForwardingEndPoint);

        mDeviceLatitude.setText(String.valueOf(FXWedgeStaticConfig.mDeviceLatitude));

        mDeviceLongitude.setText(String.valueOf(FXWedgeStaticConfig.mDeviceLongitude));
    }

    @SuppressLint("MissingPermission")
    private void updateLocationFused()
    {
        if(fusedLocationClient != null) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                final Location flocation = location;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mDeviceLatitude.setText(String.valueOf(flocation.getLatitude()));
                                        mDeviceLongitude.setText(String.valueOf(flocation.getLongitude()));
                                    }
                                });
                            }
                            else
                            {
                                startLocationUpdates();
                            }
                        }
                    });
        }
    }

    private void savePreferences()
    {
        updateUglyStatics();
        FXWedgeStaticConfig.storeInSharedPreferences(this);
    }

    private void updateUglyStatics()
    {
        FXWedgeStaticConfig.mFXIp = mFXIPTextView.getText().toString();
        FXWedgeStaticConfig.mServerPort = Integer.valueOf(mServerPortTextView.getText().toString());;
        FXWedgeStaticConfig.mForwardIP = mForwardIP.getText().toString();
        FXWedgeStaticConfig.mForwardPort = Integer.valueOf(mForwardPort.getText().toString());;
        FXWedgeStaticConfig.mEnableForwarding = mEnableForwarding.isChecked();
        FXWedgeStaticConfig.mEnableIOTAForwarding = mEnableIOTAForwarding.isChecked();
        FXWedgeStaticConfig.mIOTAForwardingKey = mIOTAForwardingAPIKey.getText().toString();
        FXWedgeStaticConfig.mIOTAForwardingEndPoint = mIOTAForwardingEndpoint.getText().toString();
        FXWedgeStaticConfig.mDeviceLatitude = Double.valueOf(mDeviceLatitude.getText().toString());
        FXWedgeStaticConfig.mDeviceLongitude = Double.valueOf(mDeviceLongitude.getText().toString());
        try {
            FXWedgeStaticConfig.mFXNameEncoded = URLEncoder.encode(mFXName.getText().toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            FXWedgeStaticConfig.mFXNameEncoded = "";
        }
    }

    private void setAutoStartServiceOnBootSwitch(final boolean checked)
    {
        mAutoStartServiceOnBootSwitch.setChecked(checked);
        mAutoStartServiceOnBootSwitch.setText(checked ? R.string.startOnBoot : R.string.doNothingOnBoot);
    }

    public void updateSwitches()
    {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                boolean startServiceOnBoot = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, false);
                setAutoStartServiceOnBootSwitch(startServiceOnBoot);
            }
        });

    }

    // Update GUI controls only if the activity exists
    public static void updateGUISwitchesIfNecessary()
    {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateSwitches();
        }
    }

    // Update GUI controls only if the activity exists
    public static void updateFxNameIfNecessary()
    {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateFXNameTextView();
        }
    }

    private void updateFXNameTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFXName.setText(FXReaderRESTApiFacade.FXName);
            }
        });

    }

    // Update GUI controls only if the activity exists
    public static void updateFxIPIfNecessary()
    {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateFXIPTextView();
        }
    }

    private void updateFXIPTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFXIPTextView.setText(FXWedgeStaticConfig.mFXIp);
            }
        });

    }
    public static void updateServerPortIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateServerPortTextView();
        }
    }

    private void updateServerPortTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mServerPortTextView.setText(FXWedgeStaticConfig.mServerPort);
            }
        });
    }

    public static void updateLoginIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateLoginTextView();
        }
    }

    private void updateLoginTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFXLogin.setText(FXReaderRESTApiFacade.FXLogin);
            }
        });
    }

    public static void updatePasswordIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updatePasswordTextView();
        }
    }

    private void updatePasswordTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mFXPassword.setText(FXReaderRESTApiFacade.FXPassword);
            }
        });
    }

    // Update GUI controls only if the activity exists
    public static void updateForwardIPIfNecessary()
    {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateForwardIPTextView();
        }
    }

    private void updateForwardIPTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mForwardIP.setText(FXWedgeStaticConfig.mForwardIP);
            }
        });

    }
    public static void updateForwardPortIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateForwardPortTextView();
        }
    }

    private void updateForwardPortTextView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mForwardPort.setText(String.valueOf(FXWedgeStaticConfig.mForwardPort));
            }
        });
    }

    public static void updateEnableForwardIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableForwardCheckBox();
        }
    }

    private void updateEnableForwardCheckBox() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEnableForwarding.setChecked(FXWedgeStaticConfig.mEnableForwarding);
            }
        });
    }


    public static void updateEnableIOTAForwardIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableIOTAForwardCheckBox();
        }
    }

    private void updateEnableIOTAForwardCheckBox() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEnableIOTAForwarding.setChecked(FXWedgeStaticConfig.mEnableIOTAForwarding);
            }
        });
    }


    public static void updateIOTAApiKeyIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableIOTAForwardAPIKey();
        }
    }

    private void updateEnableIOTAForwardAPIKey() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIOTAForwardingAPIKey.setText(FXWedgeStaticConfig.mIOTAForwardingKey);
            }
        });
    }


    public static void updateIOTAEndPointIfNecessary() {
        // Update GUI if necessary
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableIOTAEndPoint();
        }
    }

    private void updateEnableIOTAEndPoint() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mIOTAForwardingEndpoint.setText(FXWedgeStaticConfig.mIOTAForwardingEndPoint);
            }
        });
    }

    public static void updateIOTALatitudeIfNecessary() {
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableIOTALatitude();
        }
    }

    private void updateEnableIOTALatitude() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceLatitude.setText(String.valueOf(FXWedgeStaticConfig.mDeviceLatitude));
            }
        });
    }

    public static void updateIOTALongitudeIfNecessary() {
        if(FXWedgeSetupActivity.mMainActivity != null) // The application default activity has been opened
        {
            FXWedgeSetupActivity.mMainActivity.updateEnableIOTALongitude();
        }
    }

    private void updateEnableIOTALongitude() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDeviceLongitude.setText(String.valueOf(FXWedgeStaticConfig.mDeviceLongitude));
            }
        });
    }
}