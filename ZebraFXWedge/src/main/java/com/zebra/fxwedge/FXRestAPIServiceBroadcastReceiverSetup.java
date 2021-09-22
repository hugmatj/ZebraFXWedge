package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FXRestAPIServiceBroadcastReceiverSetup extends FxRestAPIServiceBroadcastReceiverBase {
    @Override
    public void onReceive(Context context, Intent intent) {
        mSourceName = RESTHostServiceConstants.FX_INTENT_ACTION_SETUP;
        Log.d(FXReaderRESTApiFacade.TAG, "FXRestAPIServiceBroadcastReceiverSetup::onReceive");
        mContext = context;
        FXReaderRESTApiFacade fxReaderRESTApiFacade = new FXReaderRESTApiFacade(context);
        fxReaderRESTApiFacade.setupFxReader(new FXReaderRESTApiFacade.RESTAPICallCallback() {
            @Override
            public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                Log.d(FXReaderRESTApiFacade.TAG, result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Setup FX successful" : "Setup FX reading error");
                Log.d(FXReaderRESTApiFacade.TAG, "Message: " + message);
                broadcastResult(result, message);
            }
        });
    }
}
