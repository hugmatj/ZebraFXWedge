package com.zebra.fxwedge;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class FXWedgeSetupActivity extends AppCompatActivity {

    private Switch mAutoStartServiceOnBootSwitch = null;
    private EditText mFXIPTextView = null;
    private EditText mFXName = null;
    private EditText mServerPortTextView = null;
    private EditText mFXLogin = null;
    private EditText mFXPassword = null;
    private Button mButtonSave = null;

    public static FXWedgeSetupActivity mMainActivity = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fxwedge_setup);

        mMainActivity = this;

        mAutoStartServiceOnBootSwitch = (Switch)findViewById(R.id.startOnBootSwitch);
        mAutoStartServiceOnBootSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    mAutoStartServiceOnBootSwitch.setText(getString(R.string.startOnBoot));
                }
                else
                {
                    mAutoStartServiceOnBootSwitch.setText(getString(R.string.doNothingOnBoot));
                }
                SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, isChecked);
                editor.commit();
            }
        });

        mFXName = (EditText)findViewById(R.id.et_fxname);
        mFXIPTextView = (EditText)findViewById(R.id.et_fxip);
        mServerPortTextView = (EditText)findViewById(R.id.et_serverport);
        mFXLogin = (EditText)findViewById(R.id.et_fxlogin);
        mFXPassword = (EditText)findViewById(R.id.et_fxpassword);

        SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        String FXName = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, "FX7500FCDA1B");
        mFXName.setText(FXName);

        String FXIp = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, "192.168.4.80");
        mFXIPTextView.setText(FXIp);

        int ServerPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, 5000);
        mServerPortTextView.setText(String.valueOf(ServerPort));

        boolean startServiceOnBoot = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, false);
        setAutoStartServiceOnBootSwitch(startServiceOnBoot);

        String FXLogin = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, "admin");
        mFXLogin.setText(FXLogin);

        String FXPassword = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, "change");
        mFXPassword.setText(FXPassword);

        mButtonSave = (Button)findViewById(R.id.bt_save);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePreferences();
            }
        });
    }

    private void savePreferences()
    {
        SharedPreferences sharedpreferences = getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, mAutoStartServiceOnBootSwitch.isChecked());
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, mFXName.getText().toString());
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, mFXIPTextView.getText().toString());
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, mFXLogin.getText().toString());
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, mFXPassword.getText().toString());
        int serverPort = Integer.valueOf(mServerPortTextView.getText().toString());
        editor.putInt(RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, serverPort);
        editor.commit();
        Toast.makeText(FXWedgeSetupActivity.this, getString(R.string.configsaved), Toast.LENGTH_SHORT).show();
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
                mFXIPTextView.setText(RESTServiceWebServer.mFXIp);
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
                mServerPortTextView.setText(RESTServiceWebServer.mServerPort);
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


}