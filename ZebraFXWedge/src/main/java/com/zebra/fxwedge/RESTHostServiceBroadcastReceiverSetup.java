package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class RESTHostServiceBroadcastReceiverSetup extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive");
        if(intent.getExtras() == null) {
            Log.e(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive::No extras found in intent.");
            return;
        }
        String sStartOnBoot = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_START_ON_BOOT, null);
        if(sStartOnBoot != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Start on boot extra found with value:" + sStartOnBoot);
            boolean bStartOnBoot = sStartOnBoot.equalsIgnoreCase("true") || sStartOnBoot.equalsIgnoreCase("1");
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, bStartOnBoot);
            // Update GUI if necessary
            FXWedgeSetupActivity.updateGUISwitchesIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No start on boot extra found.");
        }

        String setFxIP = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FX_IP, null);
        if(setFxIP != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set FX IP extra found with value:" + setFxIP);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, setFxIP);
            // Update rest server if launched
            RESTServiceWebServer.mFXIp = setFxIP;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateFxIPIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No allow external IPs extra found.");
        }

        String setServerPort = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_SERVER_PORT, null);
        if(setServerPort != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set Server Port extra found with value:" + setServerPort);
            int serverPort = Integer.valueOf(setServerPort);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, serverPort);
            // Update rest server if launched
            RESTServiceWebServer.mServerPort = serverPort;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateServerPortIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No allow external IPs extra found.");
        }
        
        String setFxName = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FX_NAME, null);
        if(setFxName != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set FX name found with value:" + setFxName);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, setFxName);
            // Update rest server if launched
            FXReaderRESTApiFacade.FXName = setFxName;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateFxNameIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No FX name extra found.");
        }

        String setFxLogin = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FXLOGIN, null);
        if(setFxLogin != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set FX login found with value:" + setFxLogin);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, setFxLogin);
            // Update rest server if launched
            FXReaderRESTApiFacade.FXLogin = setFxLogin;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateLoginIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No FX login extra found.");
        }

        String setFxPassword = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FXPASSWORD, null);
        if(setFxPassword != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set FX password found with value:" + setFxPassword);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, setFxPassword);
            // Update rest server if launched
            FXReaderRESTApiFacade.FXPassword = setFxPassword;
            // Update GUI if necessary
            FXWedgeSetupActivity.updatePasswordIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No FX password extra found.");
        }

    }

    private void setSharedPreference(Context context, String key, boolean value)
    {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::setSharedPreference: Key=" + key + " | Value=" + value);
        // Setup shared preferences for next reboot
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void setSharedPreference(Context context, String key, String value)
    {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::setSharedPreference: Key=" + key + " | Value=" + value);
        // Setup shared preferences for next reboot
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private void setSharedPreference(Context context, String key, int value)
    {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::setSharedPreference: Key=" + key + " | Value=" + value);
        // Setup shared preferences for next reboot
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
