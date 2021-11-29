package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.Call;
import okhttp3.Callback;
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
    public static String mFXNameEncoded = "";

    public static boolean mEnableIOTAForwarding = false;
    public static String mIOTAForwardingKey = "EbJbpyTL9C1oBXTYy5GxWhKk8AKNSM4n";
    public static String mIOTAForwardingEndPoint = "https://sandbox-api.zebra.com/v2/ledger/tangle";

    protected static String TAG = "FXEP";

    public RestServiceFXEndPoint(Context context) {
        mContext = context;
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        mEnableForwarding = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_ENABLED, false);
        mForwardIP = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_IP, "sms.ckpfra.ovh");
        mForwardPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_FORWARDING_PORT, 1706);
        try {
            mFXNameEncoded = URLEncoder.encode(sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, "FX7500"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            mFXNameEncoded = "";
        }
        mEnableIOTAForwarding = sharedpreferences.getBoolean(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENABLED, false);
        mIOTAForwardingKey = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_APIKEY, "EbJbpyTL9C1oBXTYy5GxWhKk8AKNSM4n");
        mIOTAForwardingEndPoint = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_IOTAFORWARDING_ENDPOINT, "https://sandbox-api.zebra.com/v2/ledger/tangle");
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
                broadcastJSONData(session, json);

            if(mEnableForwarding )
                forwardSession(session, files);

            if(mEnableIOTAForwarding)
                IOTAHelpers.forwardSessionToIOTA(session, files, mIOTAForwardingEndPoint, mIOTAForwardingKey);
            //Log.w(TAG, "json " + json);
        }



        return new Pair<>(RESTServiceWebServer.EJobStatus.SUCCEEDED, json);
    }

    private void broadcastJSONData(NanoHTTPD.IHTTPSession session,String json) {
        FXReadsDataModel data = new Gson().fromJson(json, FXReadsDataModel.class);
        Log.v(TAG, "Read data: \n" + data.toString());
        Intent intent = new Intent(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_ACTION);
        intent.addCategory(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA, data.toBundle());
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCENAME, session.getRemoteHostName());
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_SOURCEIP, session.getRemoteIpAddress());
        mContext.sendBroadcast(intent);
    }

    private boolean forwardSession(NanoHTTPD.IHTTPSession session, Map<String, String> files)
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
        okHttpBuilder = okHttpBuilder.addHeader("fxforward", mFXNameEncoded);
        okHttpBuilder = okHttpBuilder.addHeader("accept", "*/*");

        request = okHttpBuilder.build();

            Log.d(TAG, "Forward request: " + request.toString());

            client.newCall(request)
                    .enqueue(new Callback() {
                        @Override
                        public void onFailure(final Call call, IOException e) {
                            // Error
                            Log.d(TAG, "Forward Request failure: " + e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            String res = response.body().string();

                            Log.d(TAG, "Forward response: " + response.message());
                        }
                    });

            //Response response = client.newCall(request).execute();

            return true;
    }


}
