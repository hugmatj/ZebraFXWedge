package com.zebra.fxwedge;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class RESTServiceWebServer extends NanoHTTPD {

    private Context mContext = null;
    private static RESTHostServiceNetworkStateObserver mIPChangeObserver = null;
    private static boolean mStopServing = false;

    protected enum EJobStatus
    {
        SUCCEEDED,
        FAILED,
        TIMEOUT,
        CUSTOM
    }


    private RestServiceFXEndPoint mFXEndPoint;

    public RESTServiceWebServer(int port, Context context) {
        super(port);
        mContext = context;
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        FXWedgeStaticConfig.mFXIp = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, "192.168.4.80");
        FXWedgeStaticConfig.mServerPort = sharedpreferences.getInt(RESTHostServiceConstants.SHARED_PREFERENCES_SERVER_PORT, 5000);
        mFXEndPoint = new RestServiceFXEndPoint(context);
        ZebraDeviceHelper.getDeviceSerialNumber(context, null);
    }

    public static void updateIP()
    {
        if(mIPChangeObserver != null)
        {
            FXWedgeStaticConfig.mCurrentIP = mIPChangeObserver.getIPAddress();
        }
    }

    public static boolean isRunning()
    {
        return mStopServing == false;
    }

    @Override
    public void start(final int timeout) throws IOException {
        super.start(timeout, true);
        if(mIPChangeObserver == null)
        {
            // We launch the observer but we do not need to be notified here if the IP change
            mIPChangeObserver = new RESTHostServiceNetworkStateObserver(mContext, new RESTHostServiceNetworkStateObserver.IIPChangeObserver() {
                @Override
                public void onIPChanged(String newIP) {
                    if(newIP.equalsIgnoreCase("0.0.0.0"))
                    {
                        // This means we are actually getting a new IP but the IP resolution is not finished
                        // We block any request from the webpage
                        mStopServing = true;
                        FXWedgeStaticConfig.mCurrentIP = "0.0.0.0";
                    }
                    if(newIP.equalsIgnoreCase("0.0.0.0") == false && newIP.equalsIgnoreCase(FXWedgeStaticConfig.mCurrentIP) == false) {
                        // If the IP has changed, we need to restart the server and unstop the service if necessary
                        mStopServing = true;
                        try {
                            FXWedgeStaticConfig.mCurrentIP = newIP;
                            //Thread.sleep(2000);
                            mStopServing = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mIPChangeObserver.startObserver();
        }
        else if(mIPChangeObserver.isStarted() == false)
        {
            mIPChangeObserver.startObserver();
        }
        mStopServing = false;
    }

    @Override
    public void stop() {
        mStopServing = true;
        if(mIPChangeObserver != null && mIPChangeObserver.isStarted())
        {
            mIPChangeObserver.stopObserver();
        }
        mIPChangeObserver = null;
        super.stop();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String responseJSON = "";
        if(mStopServing)
        {
            // We are stopped, so we don't accept any requests from the webpage, and return an empty json string
            // Something happen and we did something, so the response will allways be OK from the serve method perspective
            // Other kind of responses (timeout, etc...) are managed by the REST server itself.
            Response resp =  newFixedLengthResponse(Response.Status.OK, "application/json", responseJSON);
            // We need to add CORS header to the response to allow Cross Origin Resource Sharing
            resp = addCORSHeaders(session.getHeaders(), resp, "*");
            return resp;
        }
        // Check if the remote IP is equal to the device IP
        if(session.getRemoteIpAddress().equalsIgnoreCase(FXWedgeStaticConfig.mFXIp) == false)
        {
            responseJSON= "{\n \"result\": \"error\",\n \"message\":\"Trying to access to the webservice from a different IP than the FX Reader IP. Check FX IP configuration in settings.\"\n}";
        }
        else
        {
            Pair<EJobStatus, String> processParamsReturned = null;

            // Extract path from URI to retrieve command namespace or path
            // i.e. printconnect, or datawedge
            String path = "";
            try {
                URI uri = new URI(session.getUri());
                String splitted[] = uri.getPath().split("/");
                if(splitted.length > 1)
                {
                    // we only need the first argument in the path
                    path = splitted[1];
                }
                else
                {
                    path = "";
                }
            } catch (URISyntaxException e) {
                path = "";
                e.printStackTrace();
            }

            if(session.getMethod() == Method.POST)
            {
                processParamsReturned = mFXEndPoint.processSession(session);

                if(processParamsReturned != null)
                {
                    switch(processParamsReturned.first)
                    {
                        case SUCCEEDED:
                            responseJSON= "{\n \"result\": \"succeeded\" \n}";
                            break;
                        case FAILED:
                            responseJSON= "{\n \"result\": \"error\",\n \"message\":\"" + processParamsReturned.second + "\"\n}";
                            break;
                        case TIMEOUT:
                            responseJSON= "{\n \"result\": \"timeout\",\n \"message\":\"" + processParamsReturned.second + "\"\n}";
                            break;
                        case CUSTOM:
                            responseJSON = processParamsReturned.second;
                            break;
                    }
                }
                else
                {
                    responseJSON= "{\n \"result\": \"error\",\n \"message\":\"Path:" + path + " not recognized.\"\n}";
                }
            }
            else
            {
                responseJSON= "{\n \"result\": \"error\",\n \"message\":\"Path:" + path + " malformed.\"\n}";
            }
        }
        // Something happen and we did something, so the response will allways be OK from the serve method perspective
        // Other kind of responses (timeout, etc...) are managed by the REST server itself.
        Response resp =  newFixedLengthResponse(Response.Status.OK, "application/json", responseJSON);

        // We need to add CORS header to the response to allow Cross Origin Resource Sharing
        resp = addCORSHeaders(session.getHeaders(), resp, "*");

        return resp;
    }


    protected Response addCORSHeaders(Map<String, String> queryHeaders, Response resp, String cors) {
        resp.addHeader("Access-Control-Allow-Origin", cors);
        resp.addHeader("Access-Control-Allow-Headers", calculateAllowHeaders(queryHeaders));
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", RESTHostServiceConstants.SERVER_CORS_ALLOWED_METHODS);
        resp.addHeader("Access-Control-Max-Age", "" + RESTHostServiceConstants.SERVER_CORS_MAX_AGE);

        return resp;
    }

    private String calculateAllowHeaders(Map<String, String> queryHeaders) {
        // here we should use the given asked headers
        // but NanoHttpd uses a Map whereas it is possible for requester to send
        // several time the same header
        // let's just use default values for this version
        return System.getProperty(RESTHostServiceConstants.SERVER_ACCESS_CONTROL_ALLOW_HEADER_PROPERTY_NAME, RESTHostServiceConstants.SERVER_DEFAULT_ALLOWED_HEADERS);
    }
}
