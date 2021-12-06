package com.zebra.fxwedge;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FXWedgeStaticConfig {
    protected static final String TAG = "FXWedgeConfig";

    protected static boolean mStartServiceOnBoot = false;

    // Store current device serial  number
    protected static String mDeviceSerialNumber = "";

    protected static String mCurrentIP = "";

    protected static String mFXIp = "192.168.4.80";
    protected static String mFXLogin = "admin";
    protected static String mFXPassword = "change";
    protected static int mServerPort = 5000;


    protected static String mForwardIP = "sms.ckpfra.ovh";
    protected static int mForwardPort = 1706;
    protected static boolean mEnableForwarding = false;
    protected static String mFXName = "";
    protected static String mFXNameEncoded = "";

    protected static boolean mEnableIOTAForwarding = false;
    protected static String mIOTAForwardingKey = "EbJbpyTL9C1oBXTYy5GxWhKk8AKNSM4n";
    protected static String mIOTAForwardingEndPoint = "https://sandbox-api.zebra.com/v2/ledger/tangle";

    protected static double mDeviceLatitude = 0.0;
    protected static double mDeviceLongitude = 0.0;

    public static void storeInSharedPreferences(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, mStartServiceOnBoot);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, mFXName);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, mFXIp);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, mFXLogin);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, mFXPassword);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_IP, mForwardIP);
        editor.putInt(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_PORT, mForwardPort);
        editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_ENABLED, mEnableForwarding);
        editor.putInt(RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, mServerPort);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENDPOINT, mIOTAForwardingEndPoint);
        editor.putBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENABLED, mEnableIOTAForwarding);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_APIKEY, mIOTAForwardingKey);
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_LATITUDE, String.valueOf(mDeviceLatitude));
        editor.putString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_LONGITUDE, String.valueOf(mDeviceLongitude));
        editor.commit();
        Log.d(TAG, "Config saved successfully");
    }

    public static void getFromSharedPreferences(Context context)
    {
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);

        mFXName = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, "FX7500FCDA1B");

        try {
            FXWedgeStaticConfig.mFXNameEncoded = URLEncoder.encode(mFXName,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            FXWedgeStaticConfig.mFXNameEncoded = "";
        }

        mFXIp = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, "192.168.4.80");
        mServerPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, 5000);
        mStartServiceOnBoot = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_START_SERVICE_ON_BOOT, false);

        mFXLogin = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, "admin");
        mFXPassword = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, "change");

        mEnableForwarding = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_ENABLED, false);
        mForwardIP = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_IP, "192.168.4.199");

        mForwardPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_PORT, 5000);

        mEnableIOTAForwarding = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENABLED, false);

        mIOTAForwardingKey = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_APIKEY, "EbJbpyTL9C1oBXTYy5GxWhKk8AKNSM4n");

        mIOTAForwardingEndPoint = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENDPOINT, "https://sandbox-api.zebra.com/v2/ledger/tangle");

        String sDeviceLatitude = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_LATITUDE, "0");
        mDeviceLatitude = Double.valueOf(sDeviceLatitude);
        String sDeviceLongitude = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_LONGITUDE, "0");
        mDeviceLongitude = Double.valueOf(sDeviceLongitude);
        Log.d(TAG, "Config read successfully: \n");

    }

    private static class FXWedgeConfigDataModel
    {
        protected boolean   mStartServiceOnBoot = false;
        protected String    mDeviceSerialNumber = "";
        protected String    mCurrentIP = "";
        protected String    mFXIp = "192.168.4.80";
        protected String    mFXLogin = "admin";
        protected String    mFXPassword = "change";
        protected int       mServerPort = 5000;
        protected String    mForwardIP = "sms.ckpfra.ovh";
        protected int       mForwardPort = 1706;
        protected boolean   mEnableForwarding = false;
        protected String    mFXName = "";
        protected String    mFXNameEncoded = "";
        protected boolean   mEnableIOTAForwarding = false;
        protected String    mIOTAForwardingKey = "EbJbpyTL9C1oBXTYy5GxWhKk8AKNSM4n";
        protected String    mIOTAForwardingEndPoint = "https://sandbox-api.zebra.com/v2/ledger/tangle";
        protected double    mDeviceLatitude = 0.0;
        protected double    mDeviceLongitude = 0.0;

        public void getDataFromStatics()
        {
            mStartServiceOnBoot         = FXWedgeStaticConfig.mStartServiceOnBoot    ;
            mDeviceSerialNumber         = FXWedgeStaticConfig.mDeviceSerialNumber    ;
            mCurrentIP                  = FXWedgeStaticConfig.mCurrentIP             ;
            mFXIp                       = FXWedgeStaticConfig.mFXIp                  ;
            mFXLogin                    = FXWedgeStaticConfig.mFXLogin               ;
            mFXPassword                 = FXWedgeStaticConfig.mFXPassword            ;
            mServerPort                 = FXWedgeStaticConfig.mServerPort            ;
            mForwardIP                  = FXWedgeStaticConfig.mForwardIP             ;
            mForwardPort                = FXWedgeStaticConfig.mForwardPort           ;
            mEnableForwarding           = FXWedgeStaticConfig.mEnableForwarding      ;
            mFXName                     = FXWedgeStaticConfig.mFXName                ;
            mFXNameEncoded              = FXWedgeStaticConfig.mFXNameEncoded         ;
            mEnableIOTAForwarding       = FXWedgeStaticConfig.mEnableIOTAForwarding  ;
            mIOTAForwardingKey          = FXWedgeStaticConfig.mIOTAForwardingKey     ;
            mIOTAForwardingEndPoint     = FXWedgeStaticConfig.mIOTAForwardingEndPoint;
            mDeviceLatitude             = FXWedgeStaticConfig.mDeviceLatitude        ;
            mDeviceLongitude            = FXWedgeStaticConfig.mDeviceLongitude       ;
        }

        public void storeDataInStatics()
        {
            FXWedgeStaticConfig.mStartServiceOnBoot         = mStartServiceOnBoot    ;
            FXWedgeStaticConfig.mDeviceSerialNumber         = mDeviceSerialNumber    ;
            FXWedgeStaticConfig.mCurrentIP                  = mCurrentIP             ;
            FXWedgeStaticConfig.mFXIp                       = mFXIp                  ;
            FXWedgeStaticConfig.mFXLogin                    = mFXLogin               ;
            FXWedgeStaticConfig.mFXPassword                 = mFXPassword            ;
            FXWedgeStaticConfig.mServerPort                 = mServerPort            ;
            FXWedgeStaticConfig.mForwardIP                  = mForwardIP             ;
            FXWedgeStaticConfig.mForwardPort                = mForwardPort           ;
            FXWedgeStaticConfig.mEnableForwarding           = mEnableForwarding      ;
            FXWedgeStaticConfig.mFXName                     = mFXName                ;
            FXWedgeStaticConfig.mFXNameEncoded              = mFXNameEncoded         ;
            FXWedgeStaticConfig.mEnableIOTAForwarding       = mEnableIOTAForwarding  ;
            FXWedgeStaticConfig.mIOTAForwardingKey          = mIOTAForwardingKey     ;
            FXWedgeStaticConfig.mIOTAForwardingEndPoint     = mIOTAForwardingEndPoint;
            FXWedgeStaticConfig.mDeviceLatitude             = mDeviceLatitude        ;
            FXWedgeStaticConfig.mDeviceLongitude            = mDeviceLongitude       ;
        }
    }

    public static boolean readConfig(Context context, String filePath) throws IOException {
        File myJsonFile = new File(filePath);
        if(myJsonFile.exists() == false)
            return false;
        FileInputStream fin = new FileInputStream(myJsonFile);
        BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        String fileJSONContent = sb.toString();
        //Make sure you close all streams.
        fin.close();

        FXWedgeConfigDataModel dataModel = new Gson().fromJson(fileJSONContent, FXWedgeConfigDataModel.class);
        if(dataModel != null)
        {
            Log.d(TAG, dataModel.toString());
            dataModel.storeDataInStatics();
            storeInSharedPreferences(context);
            return true;
        }
        return false;
    }

    public static boolean writeConfig(Context context, String filePath) throws IOException {
        File myJsonFile = new File(filePath);
        if(myJsonFile.exists() == true) {
            myJsonFile.delete();
        }

        FXWedgeConfigDataModel config = new FXWedgeConfigDataModel();
        config.getDataFromStatics();

        String configAsJson = new Gson().toJson(config);

        FileOutputStream out = new FileOutputStream(myJsonFile.getPath());
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
        outputStreamWriter.write(configAsJson);
        outputStreamWriter.close();
        out.close();

        return (myJsonFile.exists() && myJsonFile.length() > 0);
    }
}
