package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FXRestAPIServiceBroadcastReceiverReboot extends FxRestAPIServiceBroadcastReceiverBase {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(FXReaderRESTApiFacade.TAG, "FXRestAPIServiceBroadcastReceiverReboot::onReceive");
        FXReaderRESTApiFacade fxReaderRESTApiFacade = new FXReaderRESTApiFacade(context);
        fxReaderRESTApiFacade.reboot(new FXReaderRESTApiFacade.RESTAPICallCallback() {
            @Override
            public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                Log.d(FXReaderRESTApiFacade.TAG, result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Reboot successful" : "Reboot error");
                Log.d(FXReaderRESTApiFacade.TAG, "Message: " + message);
                broadcastResult(result, message);
            }
        });
    }
}
