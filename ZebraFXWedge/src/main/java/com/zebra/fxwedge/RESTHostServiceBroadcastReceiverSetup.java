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
            FXWedgeStaticConfig.mFXIp = setFxIP;
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
            FXWedgeStaticConfig.mServerPort = serverPort;
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

        String setForwardIP = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FORWARDING_IP, null);
        if(setForwardIP != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set Forward IP extra found with value:" + setForwardIP);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_IP, setForwardIP);
            // Update rest server if launched
            FXWedgeStaticConfig.mForwardIP = setForwardIP;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateForwardIPIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No forward IPs extra found.");
        }

        String setForwardPort = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FORWARDING_PORT, null);
        if(setForwardPort != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set Forward Port extra found with value:" + setForwardPort);
            int forwardPort = Integer.valueOf(setForwardPort);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_PORT, forwardPort);
            // Update rest server if launched
            FXWedgeStaticConfig.mForwardPort = forwardPort;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateForwardPortIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No forward port extra found.");
        }


        String setEnableForward = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_FORWARDING_ENABLED, null);
        if(setEnableForward != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set Enable Forward extra found with value:" + setEnableForward);
            boolean enableForward = Boolean.valueOf(setEnableForward);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_ENABLED, enableForward);
            // Update rest server if launched
            FXWedgeStaticConfig.mEnableForwarding = enableForward;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateEnableForwardIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No enable forward extra found.");
        }

        String setEnableIOTAForward = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_ENABLED, null);
        if(setEnableIOTAForward != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set Enable IOTA Forward extra found with value:" + setEnableIOTAForward);
            boolean enableIOTAForward = Boolean.valueOf(setEnableIOTAForward);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENABLED, enableIOTAForward);
            // Update rest server if launched
            FXWedgeStaticConfig.mEnableIOTAForwarding = enableIOTAForward;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateEnableIOTAForwardIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No enable IOTA forward extra found.");
        }

        String setIOTAForwardAPIKey = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_APIKEY, null);
        if(setIOTAForwardAPIKey != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set IOTA Forward API Key extra found with value:" + setIOTAForwardAPIKey);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_APIKEY, setIOTAForwardAPIKey);
            // Update rest server if launched
            FXWedgeStaticConfig.mIOTAForwardingKey = setIOTAForwardAPIKey;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateIOTAApiKeyIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No IOTA forward API Key extra found.");
        }

        String setIOTAForwardEndPoint = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_ENDPOINT, null);
        if(setIOTAForwardEndPoint != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set IOTA Forward Endpoint extra found with value:" + setIOTAForwardEndPoint);
            setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENDPOINT, setIOTAForwardEndPoint);
            // Update rest server if launched
            FXWedgeStaticConfig.mIOTAForwardingEndPoint = setIOTAForwardEndPoint;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateIOTAEndPointIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No IOTA forward Endpoint extra found.");
        }

        String setIOTAForwardLatitude = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_LATITUDE, null);
        if(setIOTAForwardLatitude != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set IOTA Forward Latitude extra found with value:" + setIOTAForwardLatitude);
            double deviceLatitude = Double.valueOf(setIOTAForwardLatitude);
            if(deviceLatitude != 0)
                setSharedPreference(context, RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_LATITUDE, setIOTAForwardLatitude);
            // Update rest server if launched
            FXWedgeStaticConfig.mDeviceLatitude = deviceLatitude;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateIOTALatitudeIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No IOTA forward Latitude extra found.");
        }
        
        String setIOTAForwardLongitude = intent.getExtras().getString(RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_LONGITUDE, null);
        if(setIOTAForwardLongitude != null)
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:Set IOTA Forward Longitude extra found with value:" + setIOTAForwardLongitude);
            double deviceLongitude = Double.valueOf(setIOTAForwardLongitude);
            if(deviceLongitude != 0)
                setSharedPreference(context, RESTHostServiceConstants.EXTRA_CONFIGURATION_SET_IOTAFORWARDING_LONGITUDE, setIOTAForwardLongitude);
            // Update rest server if launched
            FXWedgeStaticConfig.mDeviceLongitude = deviceLongitude;
            // Update GUI if necessary
            FXWedgeSetupActivity.updateIOTALongitudeIfNecessary();
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::onReceive:No IOTA forward Longitude extra found.");
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
    
    private void setSharedPreference(Context context, String key, float value)
    {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverSetup::setSharedPreference: Key=" + key + " | Value=" + value);
        // Setup shared preferences for next reboot
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putFloat(key, value);
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
