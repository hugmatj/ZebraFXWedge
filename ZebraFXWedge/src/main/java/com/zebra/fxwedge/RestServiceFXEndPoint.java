package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestServiceFXEndPoint implements RESTServiceInterface{
    private Context mContext = null;
    public static String mForwardIP = "sms.ckpfra.ovh";
    public static int mForwardPort = 1706;
    public static boolean mEnableForwarding = false;

    protected static String TAG = "FXEP";

    public RestServiceFXEndPoint(Context context) {
        mContext = context;
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mEnableForwarding = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_ENABLED, false);
        mForwardIP = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_IP, "sms.ckpfra.ovh");
        mForwardPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_PORT, 1706);
    }

    @Override
    public Pair<RESTServiceWebServer.EJobStatus, String> processSession(NanoHTTPD.IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        try {
            session.parseBody(files);
        } catch (IOException e) {
            e.printStackTrace();
            return new Pair<>(RESTServiceWebServer.EJobStatus.FAILED, "500 : IOException: " + e.getMessage());
        } catch (NanoHTTPD.ResponseException e) {
            e.printStackTrace();
            return new Pair<>(RESTServiceWebServer.EJobStatus.FAILED, "501 : ResponseException: " + e.getMessage());
        }

        String json = "";
        if(files.size() > 0) {
            // get the POST body
            //String postBody = session.getQueryParameterString();
            // or you can access the POST request's parameters
            //Map<String, List<String>> parameters = session.getParameters();
            //Log.w(TAG, "postBody " + postBody);
            //Log.w(TAG, "postParameter " + parameters.toString());
//
            json = files.get("postData");

            if(json.isEmpty() == false)
                broadcastJSONData(json);

            //Log.w(TAG, "json " + json);
        }

        if(mEnableForwarding && files.size() > 0)
            forwardSession(session, files);

        return new Pair<>(RESTServiceWebServer.EJobStatus.SUCCEEDED, json);
    }

    private void broadcastJSONData(String json) {
        FXReadsDataModel data = new Gson().fromJson(json, FXReadsDataModel.class);
        Log.v(TAG, "Read data: \n" + data.toString());
        Intent intent = new Intent(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_ACTION);
        intent.addCategory(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA, data.toBundle());
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCENAME, FXReaderRESTApiFacade.FXName);
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCEIP, FXReaderRESTApiFacade.FXReaderIP);
        mContext.sendBroadcast(intent);
    }

    private Response forwardSession(NanoHTTPD.IHTTPSession session, Map<String, String> files)
    {
        OkHttpClient client = new OkHttpClient();
        HttpUrl endPointURL = HttpUrl.parse(session.getUri());
        Request request = null;
        MediaType mediaType = MediaType.parse("application/json");
        String postData = files.get("postData");
        RequestBody body = RequestBody.create(postData, mediaType);

        Request.Builder okHttpBuilder = new Request.Builder();
        okHttpBuilder = okHttpBuilder.url("http://" + mForwardIP + ":" + String.valueOf(mForwardPort) + "");
        okHttpBuilder = okHttpBuilder.method(session.getMethod().name(), body);

        okHttpBuilder = okHttpBuilder.addHeader("remote-addr", session.getRemoteIpAddress());
        okHttpBuilder = okHttpBuilder.addHeader("remote-host", session.getRemoteHostName());
        okHttpBuilder = okHttpBuilder.addHeader("http-client-ip", session.getRemoteIpAddress());
        okHttpBuilder = okHttpBuilder.addHeader("host", mForwardIP + ":" + String.valueOf(mForwardPort));
        okHttpBuilder = okHttpBuilder.addHeader("fxforward", FXReaderRESTApiFacade.FXName);
        okHttpBuilder = okHttpBuilder.addHeader("accept", "*/*");

        request = okHttpBuilder.build();

        try {
            Log.d(TAG, "Forward request: " + request.toString());
            Response response = client.newCall(request).execute();
            Log.d(TAG, "Forward response: " + response.message());
            return response;
        } catch (IOException e) {
            return null;
        }
    }
}
