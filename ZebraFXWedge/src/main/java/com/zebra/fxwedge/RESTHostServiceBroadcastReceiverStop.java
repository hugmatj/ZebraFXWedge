package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RESTHostServiceBroadcastReceiverStop extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverStop::onReceive");
        // Start service
        RESTHostService.stopService(context);
        if(RESTHostServiceActivity.mMainActivity != null)
            RESTHostServiceActivity.mMainActivity.updateSwitches();
    }
}
