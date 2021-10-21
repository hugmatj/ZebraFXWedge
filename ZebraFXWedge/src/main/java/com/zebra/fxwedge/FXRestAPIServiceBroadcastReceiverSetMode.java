package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public class FXRestAPIServiceBroadcastReceiverSetMode extends FxRestAPIServiceBroadcastReceiverBase {
    @Override
    public void onReceive(Context context, Intent intent) {
        mSourceName = RESTHostServiceConstants.FX_INTENT_ACTION_SET_MODE;
        Log.d(FXReaderRESTApiFacade.TAG, "FXRestAPIServiceBroadcastReceiverSetMode::onReceive");
        mContext = context;
        FXReaderRESTApiFacade fxReaderRESTApiFacade = new FXReaderRESTApiFacade(context);
        FXReaderRESTApiFacade.GetSetModeConfig setModeConfig = GetSetModeConfig(intent);
        fxReaderRESTApiFacade.setMode(new FXReaderRESTApiFacade.RESTAPICallCallback() {
            @Override
            public void onRestCallFinished(FXReaderRESTApiFacade.EResult result, String message) {
                Log.d(FXReaderRESTApiFacade.TAG, result == FXReaderRESTApiFacade.EResult.SUCCESS ? "SetMode successful" : "SetMode error");
                Log.d(FXReaderRESTApiFacade.TAG, "Message: " + message);
                broadcastResult(result, message);
            }
        },setModeConfig);
    }

    public String getStringExtraIfExists(@NotNull Intent intent, @NotNull String stringExtraName, @NotNull String defaultValue)
    {
        String extraString = intent.getStringExtra(stringExtraName);
        return extraString != null ? extraString : defaultValue;
    }

    public float getFloatExtraIfExists(@NotNull Intent intent, @NotNull String stringExtraName, float defaultValue)
    {
        return intent.getFloatExtra(stringExtraName, defaultValue);
    }

    public boolean getBooleanExtraIfExists(@NotNull Intent intent, @NotNull String stringExtraName, boolean defaultValue)
    {
        return intent.getBooleanExtra(stringExtraName, defaultValue);
    }

    public long getLongExtraIfExists(@NotNull Intent intent, @NotNull String stringExtraName, long defaultValue)
    {
        return intent.getLongExtra(stringExtraName, defaultValue);
    }

    public FXReaderRESTApiFacade.GetSetModeConfig GetSetModeConfig(Intent intent)
    {
        FXReaderRESTApiFacade.GetSetModeConfig config = new FXReaderRESTApiFacade.GetSetModeConfig();
        config.type = getStringExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TYPE, config.type);
        config.enableAntenna1 = getBooleanExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A1, false);
        config.enableAntenna2 = getBooleanExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A2, false);
        config.enableAntenna3 = getBooleanExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A3, false);
        config.enableAntenna4 = getBooleanExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A4, false);
        config.transmitPowerAntenna1 = getFloatExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A1, config.transmitPowerAntenna1);
        config.transmitPowerAntenna2 = getFloatExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A2, config.transmitPowerAntenna2);
        config.transmitPowerAntenna3 = getFloatExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A3, config.transmitPowerAntenna3);
        config.transmitPowerAntenna4 = getFloatExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A4, config.transmitPowerAntenna4);
        config.modeSpecificSettings.interval.unit = getStringExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_UNIT, config.modeSpecificSettings.interval.unit);
        config.modeSpecificSettings.interval.value = getLongExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_VALUE, config.modeSpecificSettings.interval.value);
        config.filter.match = getStringExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_MATCH, config.filter.match);
        config.filter.operation = getStringExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_OPERATION, config.filter.operation);
        config.filter.value = getStringExtraIfExists(intent, RESTHostServiceConstants.FX_INTENT_ACTION_MODE_EXTRA_FILTER_VALUE, config.filter.value);
        return config;
    }
}
