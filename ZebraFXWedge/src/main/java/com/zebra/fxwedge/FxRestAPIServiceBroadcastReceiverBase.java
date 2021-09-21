package com.zebra.fxwedge;

import android.content.BroadcastReceiver;

public abstract class FxRestAPIServiceBroadcastReceiverBase extends BroadcastReceiver {
    protected void broadcastResult(FXReaderRESTApiFacade.EResult result, String message) {
    }
}
