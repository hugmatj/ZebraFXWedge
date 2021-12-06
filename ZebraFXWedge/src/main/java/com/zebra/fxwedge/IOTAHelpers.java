package com.zebra.fxwedge;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class IOTAHelpers {

    public static final String TAG = "FXWIOTA";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(RESTHostServiceConstants.FXDATA_IOTA_FORWARDING_TIMESTAMP_DATEFORMAT);

    public static class IOTAData
    {
        public String deviceId;
        public String antennaId;
        public int peakRssi;
        public String epc;
        public String type = "readTransaction";
        public String timestamp;
        public static class location
        {
            public float longitude;
            public float latitude;
        }
        public location location;
        public static class jsonData
        {
            String deviceSerialNumber;
        }
        public jsonData jsonData;
    }

    public static boolean forwardSessionToIOTA(NanoHTTPD.IHTTPSession session, Map<String, String> files, String IOTAEndPoint, String iotaAPIKey) {
        OkHttpClient client = new OkHttpClient();
        Request request = null;
        MediaType mediaType = MediaType.parse("application/json");
        String postData = files.get("postData");
        final FXReadsDataModel data = new Gson().fromJson(postData, FXReadsDataModel.class);
        IOTAData iotaData = new IOTAData(){{
           deviceId = FXReaderRESTApiFacade.FXName;
           antennaId = String.valueOf(data.data.antenna);
           peakRssi = data.data.peakRssi;
           epc = data.data.idHex;
           timestamp = dateFormat.format(data.timestamp);
           location = new location();
           location.latitude = -30.215115f;
           location.longitude = -25.215115f;
           jsonData = new jsonData();
           jsonData.deviceSerialNumber = FXWedgeStaticConfig.mDeviceSerialNumber;
        }};
        // TODO: Create the correct JSON structure for IOTA
        String iotaDataJSON = new Gson().toJson(iotaData, IOTAData.class);
        Log.d(TAG, "IOTA request body:\n" + iotaDataJSON);
        RequestBody body = RequestBody.create(iotaDataJSON,mediaType);
        long contentLength;
        try {
            contentLength = body.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
            contentLength = iotaDataJSON.length();
        }

        Request.Builder okHttpBuilder = new Request.Builder();
        okHttpBuilder = okHttpBuilder.url(IOTAEndPoint + "/read");
        //okHttpBuilder = okHttpBuilder.post(body);
        okHttpBuilder = okHttpBuilder.method("POST", body);

        //okHttpBuilder = okHttpBuilder.addHeader("remote-addr", session.getRemoteIpAddress());
        //okHttpBuilder = okHttpBuilder.addHeader("remote-host", session.getRemoteHostName());
        //okHttpBuilder = okHttpBuilder.addHeader("http-client-ip", session.getRemoteIpAddress());
        okHttpBuilder = okHttpBuilder.addHeader("Content-Length", String.valueOf(contentLength));
        okHttpBuilder = okHttpBuilder.addHeader("Host", "sandbox-api.zebra.com:443");
        okHttpBuilder = okHttpBuilder.addHeader("Content-Type", "application/json");
        okHttpBuilder = okHttpBuilder.addHeader("Cache-Control", "no-cache");
        okHttpBuilder = okHttpBuilder.addHeader("apikey", iotaAPIKey);
        okHttpBuilder = okHttpBuilder.addHeader("Accept", "*/*");
        Log.d(TAG, "API Key: " + iotaAPIKey);

        request = okHttpBuilder.build();
        Log.d(TAG, "IOTA request: " + request.toString());
/*
        try {
            Log.d(TAG, "IOTA request: " + request.toString());
            Response response = client.newCall(request).execute();
            Log.d(TAG, "IOTA response: " + response.message() + "\nBody:" + response.body().string());
            return null; //response;
        } catch (Exception e) {
            Log.d(TAG, "IOTA exception");
            Log.d(TAG, e.getMessage());
            return null;
        }
        */
        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(final Call call, IOException e) {
                        // Error
                        Log.d(TAG, "Forward IOTA failure: " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, final Response response) throws IOException {
                        String res = response.body().string();

                        Log.d(TAG, "Forward IOTA response: " + response.toString());
                    }
                });
        return true;
    }
}
