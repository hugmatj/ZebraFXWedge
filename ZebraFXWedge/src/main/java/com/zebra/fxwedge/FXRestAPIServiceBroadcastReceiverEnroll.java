package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class FXRestAPIServiceBroadcastReceiverEnroll extends FxRestAPIServiceBroadcastReceiverBase {
    @Override
    public void onReceive(Context context, Intent intent) {
        mSourceName = RESTHostServiceConstants.FX_INTENT_ACTION_ENROLL;
        Log.d(FXReaderRESTApiFacade.TAG, "FXRestAPIServiceBroadcastReceiverEnroll::onReceive");
        mContext = context;
        FXReaderRESTApiFacade fxReaderRESTApiFacade = new FXReaderRESTApiFacade(context);
        fxReaderRESTApiFacade.enrollToCloud(new FXReaderRESTApiFacade.RESTAPICallCallback() {
            @Override
            public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                Log.d(FXReaderRESTApiFacade.TAG, result == FXReaderRESTApiFacade.EResult.SUCCESS ? "Enroll FX successful" : "Enroll FX error");
                Log.d(FXReaderRESTApiFacade.TAG, "Message: " + message);
                broadcastResult(result, message);
            }
        });
    }
}
