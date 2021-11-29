package com.zebra.fxwedge;

import java.util.Date;

public class RESTHostServiceConstants {
    protected static final String TAG  ="RESTHostService";

    // Shared preference keys
    protected static final String SHARED_PREFERENCES_NAME = "RESTHostService";
    protected static final String SHARED_PREFERENCES_START_SERVICE_ON_BOOT = "startonboot";
    protected static final String SHARED_PREFERENCES_FX_NAME = "fxname";
    protected static final String SHARED_PREFERENCES_FX_IP = "fxip";
    protected static final String SHARED_PREFERENCES_FX_LOGIN = "admin";
    protected static final String SHARED_PREFERENCES_FX_PASSWORD = "change";
    protected static final String SHARED_PREFERENCES_SERVER_PORT = "serverport";
    protected static final String SHARED_PREFERENCES_FORWARDING_ENABLED = "forwardingenabled";
    protected static final String SHARED_PREFERENCES_FORWARDING_IP = "forwardingip";
    protected static final String SHARED_PREFERENCES_FORWARDING_PORT = "forwardingport";
    protected static final String SHARED_PREFERENCES_IOTAFORWARDING_ENABLED = "iotaforwardingenabled";
    protected static final String SHARED_PREFERENCES_IOTAFORWARDING_APIKEY = "iotaforwardingapikey";
    protected static final String SHARED_PREFERENCES_IOTAFORWARDING_ENDPOINT = "iotaforwardingendpoint";

    // Intent settings xtras keys
    protected static final String EXTRA_CONFIGURATION_START_ON_BOOT = "startonboot";
    protected static final String EXTRA_CONFIGURATION_SET_FX_IP = "setfxip";
    protected static final String EXTRA_CONFIGURATION_SET_FX_NAME = "setfxname";
    protected static final String EXTRA_CONFIGURATION_SET_SERVER_PORT = "serverport";
    protected static final String EXTRA_CONFIGURATION_SET_FXLOGIN = "login";
    protected static final String EXTRA_CONFIGURATION_SET_FXPASSWORD = "password";
    protected static final String EXTRA_CONFIGURATION_SET_FORWARDING_ENABLED = "forwardingenabled";
    protected static final String EXTRA_CONFIGURATION_SET_FORWARDING_IP = "forwardingip";
    protected static final String EXTRA_CONFIGURATION_SET_FORWARDING_PORT = "forwardingport";
    protected static final String EXTRA_CONFIGURATION_SET_IOTAFORWARDING_ENABLED = "iotaforwardingenabled";
    protected static final String EXTRA_CONFIGURATION_SET_IOTAFORWARDING_APIKEY = "iotaforwardingapikey";
    protected static final String EXTRA_CONFIGURATION_SET_IOTAFORWARDING_ENDPOINT = "iotaforwardingendpoint";

    // Server configuration
    protected static final String SERVER_CORS_ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    protected static final int SERVER_CORS_MAX_AGE = 42 * 60 * 60;
    protected static final String SERVER_DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
    protected static final String SERVER_ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME = "AccessControlAllowHeader";

    // Intents
    protected static final String FXDATA_BROADCAST_INTENT_CATEGORY = "android.intent.category.DEFAULT";
    protected static final String FXDATA_BROADCAST_INTENT_ACTION = "com.zebra.fxwedge.DATA";

    // Broadcast intent extra values
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_SOURCENAME = "readername";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_SOURCEIP = "readerip";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_READDATA = "readdata";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_DATA = "data";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_ANTENNA = "antenna";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_EVENTNUM = "eventNum";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_FORMAT = "format";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_IDHEX = "idHex";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_PEAKRSSI = "peakRssi";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_READS = "reads";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP = "timestamp";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_TYPE = "type";
    protected static final String FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP_DATEFORMAT = "yyyy-MM-dd HH:mm:ss z";
    protected static final String FXDATA_IOTA_FORWARDING_TIMESTAMP_DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    // FX Intent Commands
    protected static final String FX_INTENT_CATEGORY = "android.intent.category.DEFAULT";
    protected static final String FX_INTENT_ACTION_LOGIN = "com.zebra.fxwedge.fxlogin";
    protected static final String FX_INTENT_ACTION_SETUP = "com.zebra.fxwedge.fxsetup";
    protected static final String FX_INTENT_ACTION_ENROLL = "com.zebra.fxwedge.fxenroll";
    protected static final String FX_INTENT_ACTION_REBOOT = "com.zebra.fxwedge.fxreboot";
    protected static final String FX_INTENT_ACTION_START_READING = "com.zebra.fxwedge.fxstartreading";
    protected static final String FX_INTENT_ACTION_STOP_READING = "com.zebra.fxwedge.fxstopreading";
    protected static final String FX_INTENT_ACTION_RESULT = "com.zebra.fxwedge.result";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_SOURCE = "com.zebra.fxwedge.result.extra.source";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_STATUS = "com.zebra.fxwedge.result.extra.status";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_MESSAGE = "com.zebra.fxwedge.result.extra.message";

    protected static final String FX_INTENT_ACTION_GET_MODE = "com.zebra.fxwedge.fxgetmode";
    protected static final String FX_INTENT_ACTION_SET_MODE = "com.zebra.fxwedge.fxsetmode";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_TYPE = "com.zebra.fxwedge.fxsetmode.extra.type";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A1 = "com.zebra.fxwedge.fxsetmode.extra.enablea1";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A2 = "com.zebra.fxwedge.fxsetmode.extra.enablea2";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A3 = "com.zebra.fxwedge.fxsetmode.extra.enablea3";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_ENABLE_A4 = "com.zebra.fxwedge.fxsetmode.extra.enablea4";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A1 = "com.zebra.fxwedge.fxsetmode.extra.transmitpowera1";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A2 = "com.zebra.fxwedge.fxsetmode.extra.transmitpowera2";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A3 = "com.zebra.fxwedge.fxsetmode.extra.transmitpowera3";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_TRANSMIT_POWER_A4 = "com.zebra.fxwedge.fxsetmode.extra.transmitpowera4";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_UNIT = "com.zebra.fxwedge.fxsetmode.extra.intervalunit";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_INTERVAL_VALUE = "com.zebra.fxwedge.fxsetmode.extra.intervalvalue";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_FILTER_MATCH = "com.zebra.fxwedge.fxsetmode.extra.filtermatch";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_FILTER_OPERATION = "com.zebra.fxwedge.fxsetmode.extra.filteroperation";
    protected static final String FX_INTENT_ACTION_MODE_EXTRA_FILTER_VALUE = "com.zebra.fxwedge.fxsetmode.extra.filtervalue";

}
