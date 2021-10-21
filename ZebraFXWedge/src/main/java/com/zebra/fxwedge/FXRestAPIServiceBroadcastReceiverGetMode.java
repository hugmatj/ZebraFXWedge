package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class FXRestAPIServiceBroadcastReceiverGetMode extends FxRestAPIServiceBroadcastReceiverBase {
    @Override
    public void onReceive(Context context, Intent intent) {
        mSourceName = RESTHostServiceConstants.FX_INTENT_ACTION_GET_MODE;
        Log.d(FXReaderRESTApiFacade.TAG, "FXRestAPIServiceBroadcastReceiverGetMode::onReceive");
        mContext = context;
        FXReaderRESTApiFacade fxReaderRESTApiFacade = new FXReaderRESTApiFacade(context);
        fxReaderRESTApiFacade.getMode(new FXReaderRESTApiFacade.RESTAPICallCallback() {
            @Override
            public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                Log.d(FXReaderRESTApiFacade.TAG, result == FXReaderRESTApiFacade.EResult.SUCCESS ? "GetMode successful" : "GetMode error");
                Log.d(FXReaderRESTApiFacade.TAG, "Message: " + message);
                broadcastGetModeResult(result, message);
            }
        });
    }
    
    private void broadcastGetModeResult(FXReaderRESTApiFacade.EResult result, String message)
    {
        FXReaderRESTApiFacade.GetSetModeConfig config = FXReaderRESTApiFacade.getSetModeConfigFromJSon(message);
        Intent intent = new Intent(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT);
        intent.addCategory(RESTHostServiceConstants.FX_INTENT_CATEGORY);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_SOURCE, mSourceName);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_STATUS, result.toString());
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_RESULT_EXTRA_MESSAGE, message);

        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TYPE, config.type);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A1, config.enableAntenna1);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A2, config.enableAntenna2);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A3, config.enableAntenna3);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A4, config.enableAntenna4);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A1, config.transmitPowerAntenna1);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A2, config.transmitPowerAntenna2);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A3, config.transmitPowerAntenna3);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A4, config.transmitPowerAntenna4);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_UNIT, config.modeSpecificSettings.interval.unit);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_VALUE, config.modeSpecificSettings.interval.value);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_MATCH, config.filter.match);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_OPERATION, config.filter.operation);
        intent.putExtra(RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_VALUE, config.filter.value);

        mContext.sendBroadcast(intent);
    }
}
