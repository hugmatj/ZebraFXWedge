package com.zebra.fxwedge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class FxRestAPIServiceBroadcastReceiverBase extends BroadcastReceiver {
    protected Context mContext = null;
    protected String mSourceName = "";
    protected void broadcastResult(FXReaderRESTApiFacade.EResult result, String message) {
        Intent intent = new Intent(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT);
        intent.addCategory(RESTHostServiceConstants.FX_INTENT_CATEGORY);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_SOURCE, mSourceName);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_STATUS, result.toString());
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_MESSAGE, message);
        mContext.sendBroadcast(intent);
    }
}
