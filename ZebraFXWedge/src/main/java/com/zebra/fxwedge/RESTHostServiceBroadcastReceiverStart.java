package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class RESTHostServiceBroadcastReceiverStart extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(RESTHostServiceConstants.TAG, "RESTHostServiceBroadcastReceiverStart::onReceive");
        // Start service
        RESTHostService.startService(context);
        if(RESTHostServiceActivity.mMainActivity != null)
            RESTHostServiceActivity.mMainActivity.updateSwitches();
    }
}
