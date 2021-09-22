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

    // Intent extras keys
    protected static final String EXTRA_CONFIGURATION_START_ON_BOOT = "startonboot";
    protected static final String EXTRA_CONFIGURATION_SET_FX_IP = "setfxip";
    protected static final String EXTRA_CONFIGURATION_SET_FX_NAME = "setfxname";
    protected static final String EXTRA_CONFIGURATION_SET_SERVER_PORT = "serverport";
    protected static final String EXTRA_CONFIGURATION_SET_FXLOGIN = "login";
    protected static final String EXTRA_CONFIGURATION_SET_FXPASSWORD = "password";

    // Server configuration
    protected static final String SERVER_CORS_ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS, HEAD";
    protected static final int SERVER_CORS_MAX_AGE = 42 * 60 * 60;
    protected static final String SERVER_DEFAULT_ALLOWED_HEADERS = "origin,accept,content-type";
    protected static final String SERVER_ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME = "AccessControlAllowHeader";

    // Intents
    protected static final String FXDATA_BROADCAST_INTENT_CATEGORY = "android.intent.category.DEFAULT";
    protected static final String FXDATA_BROADCAST_INTENT_ACTION = "com.zebra.fxwedge.DATA";

    // Broadcast intent extra values
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

    // FX Intent Commands
    protected static final String FX_INTENT_CATEGORY = "android.intent.category.DEFAULT";
    protected static final String FX_INTENT_ACTION_LOGIN = "com.zebra.fxwedge.fxlogin";
    protected static final String FX_INTENT_ACTION_SETUP = "com.zebra.fxwedge.fxsetup";
    protected static final String FX_INTENT_ACTION_REBOOT = "com.zebra.fxwedge.fxreboot";
    protected static final String FX_INTENT_ACTION_START_READING = "com.zebra.fxwedge.fxstartreading";
    protected static final String FX_INTENT_ACTION_STOP_READING = "com.zebra.fxwedge.fxstopreading";
    protected static final String FX_INTENT_ACTION_RESULT = "com.zebra.fxwedge.result";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_SOURCE = "com.zebra.fxwedge.result.extra.source";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_STATUS = "com.zebra.fxwedge.result.extra.status";
    protected static final String FX_INTENT_ACTION_RESULT_EXTRA_MESSAGE = "com.zebra.fxwedge.result.extra.message";
}
