package com.zebra.fxwedge;

import android.content.Context;
import android.content.IntentFilter;

public class FxRestAPIServiceBroadcastReceiverHelper {
    protected static FXRestAPIServiceBroadcastReceiverLogin restAPIServiceBroadcastReceiverLogin;
    protected static FXRestAPIServiceBroadcastReceiverSetup restAPIServiceBroadcastReceiverSetup;
    protected static FXRestAPIServiceBroadcastReceiverEnroll restAPIServiceBroadcastReceiverEnroll;
    protected static FXRestAPIServiceBroadcastReceiverSetMode restAPIServiceBroadcastReceiverSetMode;
    protected static FXRestAPIServiceBroadcastReceiverGetMode restAPIServiceBroadcastReceiverGetMode;
    protected static FXRestAPIServiceBroadcastReceiverReboot restAPIServiceBroadcastReceiverReboot;
    protected static FXRestAPIServiceBroadcastReceiverStartReading fxRestAPIServiceBroadcastReceiverStartReading;
    protected static FXRestAPIServiceBroadcastReceiverStopReading fxRestAPIServiceBroadcastReceiverStopReading;
    public static void registerReceivers(Context context)
    {
        restAPIServiceBroadcastReceiverLogin = new FXRestAPIServiceBroadcastReceiverLogin();
        context.registerReceiver(restAPIServiceBroadcastReceiverLogin, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_LOGIN));

        restAPIServiceBroadcastReceiverSetup = new FXRestAPIServiceBroadcastReceiverSetup();
        context.registerReceiver(restAPIServiceBroadcastReceiverSetup, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_SETUP));

        restAPIServiceBroadcastReceiverEnroll = new FXRestAPIServiceBroadcastReceiverEnroll();
        context.registerReceiver(restAPIServiceBroadcastReceiverEnroll, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_ENROLL));

        restAPIServiceBroadcastReceiverSetMode = new FXRestAPIServiceBroadcastReceiverSetMode();
        context.registerReceiver(restAPIServiceBroadcastReceiverSetMode, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_SET_MODE));

        restAPIServiceBroadcastReceiverGetMode = new FXRestAPIServiceBroadcastReceiverGetMode();
        context.registerReceiver(restAPIServiceBroadcastReceiverGetMode, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_GET_MODE));

        restAPIServiceBroadcastReceiverReboot = new FXRestAPIServiceBroadcastReceiverReboot();
        context.registerReceiver(restAPIServiceBroadcastReceiverReboot, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_REBOOT));

        fxRestAPIServiceBroadcastReceiverStartReading = new FXRestAPIServiceBroadcastReceiverStartReading();
        context.registerReceiver(fxRestAPIServiceBroadcastReceiverStartReading, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_START_READING));

        fxRestAPIServiceBroadcastReceiverStopReading = new FXRestAPIServiceBroadcastReceiverStopReading();
        context.registerReceiver(fxRestAPIServiceBroadcastReceiverStopReading, new IntentFilter(RESTHostServiceConstants.FX_INTENT_ACTION_STOP_READING));

    }

    public static void unregisterReceivers(Context context)
    {
        if(restAPIServiceBroadcastReceiverLogin != null)
        {
            context.unregisterReceiver(restAPIServiceBroadcastReceiverLogin);
            context.unregisterReceiver(restAPIServiceBroadcastReceiverSetup);
            context.unregisterReceiver(restAPIServiceBroadcastReceiverEnroll);
            context.unregisterReceiver(restAPIServiceBroadcastReceiverSetMode);
            context.unregisterReceiver(restAPIServiceBroadcastReceiverGetMode);
            context.unregisterReceiver(restAPIServiceBroadcastReceiverReboot);
            context.unregisterReceiver(fxRestAPIServiceBroadcastReceiverStartReading);
            context.unregisterReceiver(fxRestAPIServiceBroadcastReceiverStopReading);
            restAPIServiceBroadcastReceiverLogin = null;
            restAPIServiceBroadcastReceiverSetup = null;
            restAPIServiceBroadcastReceiverEnroll = null;
            restAPIServiceBroadcastReceiverSetMode = null;
            restAPIServiceBroadcastReceiverGetMode = null;
            restAPIServiceBroadcastReceiverReboot = null;
            fxRestAPIServiceBroadcastReceiverStartReading = null;
            fxRestAPIServiceBroadcastReceiverStopReading = null;
        }
    }
}
