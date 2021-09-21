package com.zebra.fxwedge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class RestServiceFXEndPoint implements RESTServiceInterface{
    private Context mContext = null;

    protected static String TAG = "FXEP";

    public RestServiceFXEndPoint(Context context) {
        mContext = context;
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

        return new Pair<>(RESTServiceWebServer.EJobStatus.SUCCEEDED, json);
    }

    private void broadcastJSONData(String json) {
        FXReadsDataModel data = new Gson().fromJson(json, FXReadsDataModel.class);
        Log.v(TAG, "Read data: \n" + data.toString());
        Intent intent = new Intent(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_ACTION);
        intent.addCategory(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_CATEGORY);
        intent.putExtra(RESTHostServiceConstants.FXDATA_BROADCAST_INTENT_EXTRA_READDATA, data.toBundle());
        mContext.sendBroadcast(intent);
    }
}
