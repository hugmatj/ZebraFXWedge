package com.zebra.fxwedge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FXReaderRESTApiFacade {

    protected static final String TAG = "FXRESTAPI";
    private Context context;
    protected static String sessionID;
    protected static String FXReaderIP = "";
    protected static String FXLogin = "admin";
    protected static String FXPassword = "change";
    protected static String FXName = "FX7500FCDA1B";

    public enum EResult
    {
        SUCCESS("SUCCESS"),
        ERROR("ERROR");

        String mName = "";
        EResult(String name)
        {
            mName = name;
        }

        public String toString()
        {
            return mName;
        }
    }

    protected FXReaderRESTApiFacade(Context context)
    {
        this.context = context;
        SharedPreferences sharedpreferences = context.getSharedPreferences(RESTHostServiceConstants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        FXReaderIP = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_IP, "192.168.4.80");
        FXLogin = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_LOGIN, "admin");
        FXPassword = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_PASSWORD, "change");
        FXName = sharedpreferences.getString(RESTHostServiceConstants.SHARED_PREFERENCES_FX_NAME, "FX7500FCDA1B");
    }

    public interface RESTAPICallCallback
    {
        void onRestCallFinished(EResult result, String message);
    }

    private interface AsyncTaskCallback
    {
        void onPostExecute(Response response, Exception exception);
    }

    private class RESTAPICallAsync extends AsyncTask<String, Void, Response> {

        protected AsyncTaskCallback asyncTaskCallback = null;
        protected RESTAPICallCallback restapiCallCallback = null;
        protected Exception exception;

        public RESTAPICallAsync(AsyncTaskCallback asyncTaskCallback, RESTAPICallCallback restapiCallCallback)
        {
            this.asyncTaskCallback = asyncTaskCallback;
            this.restapiCallCallback = restapiCallCallback;
        }

        /***
         *
         * @param params URL, bodyMediaType, bodyContent
         * @return
         */
        protected Response doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            HttpUrl endPointURL = HttpUrl.parse(params[0]);
            Request request = null;
            if(params.length == 3 && params[1] != null && params[1].isEmpty() == false && params[2] != null && params[2].isEmpty() == false)
            {
                MediaType mediaType = MediaType.parse(params[1]);
                RequestBody body = RequestBody.create(params[2], mediaType);

                request = new Request.Builder()
                        .url(endPointURL)
                        .post(body)
                        .addHeader("content-type", params[1])
                        .build();
            }
            else if(params.length == 2 && params[1] != null && params[1].isEmpty() == false)
            {
                MediaType mediaType = MediaType.parse(params[1]);
                RequestBody body = RequestBody.create("", mediaType);

                request = new Request.Builder()
                        .method("PUT",body)
                        .url(endPointURL)
                        .addHeader("content-type", params[1])
                        .build();
            }
            else
            {
                request = new Request.Builder()
                        .url(endPointURL)
                        .build();
            }

            try {
                Response response = client.newCall(request).execute();
                //Log.d(TAG, response.toString());
                return response;
            } catch (IOException e) {
                exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(Response response) {
            if(asyncTaskCallback != null)
                asyncTaskCallback.onPostExecute(response, exception);
            else if(restapiCallCallback != null)
            {
                if(exception != null)
                {
                    restapiCallCallback.onRestCallFinished(EResult.ERROR, exception.getMessage());
                } else if(response.isSuccessful())
                {
                    String result = null;
                    try {
                        result = response.body().string();
                        if(result.isEmpty())
                        {
                            result = response.message();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    restapiCallCallback.onRestCallFinished(EResult.SUCCESS, result);
                }
                else
                {
                    if(response.body() != null) {
                        try {
                            restapiCallCallback.onRestCallFinished(EResult.ERROR, response.body().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                            restapiCallCallback.onRestCallFinished(EResult.ERROR, e.getMessage());
                        }
                    }
                    else
                    {
                        restapiCallCallback.onRestCallFinished(EResult.ERROR, response.message());
                    }
                }

            }
        }
    }

    public void login(@NotNull final RESTAPICallCallback callback)
    {
        final RESTAPICallAsync restapiCallAsync = new RESTAPICallAsync(new AsyncTaskCallback() {
            @Override
            public void onPostExecute(Response response, Exception exception) {
                if(exception != null)
                {
                    callback.onRestCallFinished(EResult.ERROR, exception.getMessage());
                } else if(response.isSuccessful())
                {
                    // extract session ID
                    sessionID = getSessionID(response.body().byteStream());
                    callback.onRestCallFinished(EResult.SUCCESS, response.message());
                }
                else
                {
                    callback.onRestCallFinished(EResult.ERROR, response.message());
                }

            }
        }, null);
        restapiCallAsync.execute("http://" + FXReaderIP + "/control", "application/xml", getLoginXMLBody());
    }

    private String getSessionID(InputStream byteStream) {
        String sessionID = null;
        String resultCode = null;
        try
        {
            KXMLParserExtended parserExtended = KXMLParserExtended.newPullParser();
            parserExtended.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parserExtended.setInput(byteStream, null);
            resultCode = parserExtended.findElement("g1:resultCode").readText();
            sessionID = parserExtended.findElement("g3:sessionID").readText();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                byteStream.close();
                if(resultCode != null && resultCode.equalsIgnoreCase("0"))
                    return sessionID;
                else return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private String getLoginXMLBody()
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<rm:command epcglobal:creationDate=\"2001-12-17T09:30:47.0Z\"\n" +
                "            epcglobal:schemaVersion=\"0.0\"\n" +
                "            xsi:schemaLocation=\"urn:epcglobal:rm:xsd:1 ../../../schemas/RmCommand.xsd\"\n" +
                "            xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "            xmlns:rm=\"urn:epcglobal:rm:xsd:1\"\n" +
                "            xmlns:epcglobal=\"urn:epcglobal:xsd:1\"\n" +
                "            xmlns:motorm=\"urn:motorfid:rm:xsd:1\">\n" +
                "  <rm:id>99</rm:id>\n" +
                "  <rm:targetName>" + FXName + "</rm:targetName>\n" +
                "  <motorm:readerDevice>\n" +
                "      <motorm:doLogin>\n" +
                "        <motorm:username>" + FXLogin + "</motorm:username>\n" +
                "        <motorm:password>" + FXPassword + "</motorm:password>\n" +
                "        <motorm:forceLogin>true</motorm:forceLogin>\n" +
                "      </motorm:doLogin>\n" +
                "  </motorm:readerDevice>\n" +
                "</rm:command>";
    }

    public EResult logout()
    {
        //TODO: To be implemented
        return EResult.SUCCESS;
    }

    public void setupFxReader(@NotNull final RESTAPICallCallback callback)
    {
        if(sessionID == null || sessionID.isEmpty())
        {
            callback.onRestCallFinished(EResult.ERROR, "No session ID, please Login before trying to setup FXReader");
            return;
        }
        if(RESTServiceWebServer.isRunning() == false)
        {
            callback.onRestCallFinished(EResult.ERROR, "Server must be running before tying to setup FXReader to obtain local IP address");
            return;
        }
        final RESTAPICallAsync restapiCallAsync = new RESTAPICallAsync(new AsyncTaskCallback() {
            @Override
            public void onPostExecute(Response response, Exception exception) {
                if(exception != null)
                {
                    callback.onRestCallFinished(EResult.ERROR, exception.getMessage());
                } else if(response.isSuccessful())
                {
                    // extract result code
                    String resultCode = getSetupFxReaderResultCode(response.body().byteStream());
                    EResult result = resultCode.equalsIgnoreCase("0") ? EResult.SUCCESS : EResult.ERROR;
                    callback.onRestCallFinished(result, response.message());

                }
                else
                {
                    callback.onRestCallFinished(EResult.ERROR, response.message());
                }

            }
        }, null);
        RESTServiceWebServer.updateIP();
        String setupFX = getSetupFXReaderXMLBody();
        restapiCallAsync.execute("http://" + FXReaderIP + "/control", "application/xml", getSetupFXReaderXMLBody());
    }

    private String getSetupFXReaderXMLBody() {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<rm:command epcglobal:creationDate=\"2001-12-17T09:30:47.0Z\"\n" +
                "epcglobal:schemaVersion=\"0.0\"\n" +
                "xsi:schemaLocation=\"urn:epcglobal:rm:xsd:1 ../../../schemas/RmCommand.xsd\"\n" +
                "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "xmlns:rm=\"urn:epcglobal:rm:xsd:1\"\n" +
                "xmlns:epcglobal=\"urn:epcglobal:xsd:1\"\n" +
                "xmlns:motorm=\"urn:motorfid:rm:xsd:1\">\n" +
                "<rm:id>99</rm:id>\n" +
                "<rm:targetName>"+ FXName + "</rm:targetName>\n" +
                "<motorm:readerDevice>\n" +
                "<motorm:sessionID>"+ sessionID + "</motorm:sessionID>\n" +
                "<motorm:importCloudConfigToReader>\n" +
                "<motorm:CloudConfigData>{\n" +
                "    \"control\": {\n" +
                "        \"commandResponse\": {\n" +
                "            \"enableLocalRest\": true\n" +
                "        }\n" +
                "    },\n" +
                "    \"data\": {\n" +
                "        \"event\": {\n" +
                "            \"connections\": [\n" +
                "                {\n" +
                "                    \"type\": \"httpPost\",\n" +
                "                    \"options\": {\n" +
                "                        \"URL\": \"http://" + RESTServiceWebServer.mCurrentIP + ":" + String.valueOf(RESTServiceWebServer.mServerPort) + "\",\n" +
                "                        \"security\": {\n" +
                "                            \"verifyPeer\": false,\n" +
                "                            \"verifyHost\": false,\n" +
                "                            \"authenticationType\": \"NONE\"\n" +
                "                        }\n" +
                "                    }\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    },\n" +
                "    \"management\": {\n" +
                "        \"commandResponse\": {\n" +
                "            \"enableLocalRest\": true\n" +
                "        }\n" +
                "    }\n" +
                "}</motorm:CloudConfigData>\n" +
                "</motorm:importCloudConfigToReader>\n" +
                "</motorm:readerDevice>\n" +
                "</rm:command>";
    }

    private String getSetupFxReaderResultCode(InputStream byteStream) {
        String resultCode = null;
        try
        {
            KXMLParserExtended parserExtended = KXMLParserExtended.newPullParser();
            parserExtended.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parserExtended.setInput(byteStream, null);
            resultCode = parserExtended.findElement("g1:resultCode").readText();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                byteStream.close();
                if(resultCode != null)
                    return resultCode;
                else return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void startReading(@NotNull final RESTAPICallCallback callback)
    {
        final RESTAPICallAsync restapiCallAsync = new RESTAPICallAsync(null, callback);
        restapiCallAsync.execute("http://" + FXReaderIP + "/cloud/start", "application/json");
    }

    public void stopReading(@NotNull final RESTAPICallCallback callback)
    {
        final RESTAPICallAsync restapiCallAsync = new RESTAPICallAsync(null, callback);
        restapiCallAsync.execute("http://" + FXReaderIP + "/cloud/stop", "application/json");
    }

    public void reboot(@NotNull final RESTAPICallCallback callback)
    {
        final RESTAPICallAsync restapiCallAsync = new RESTAPICallAsync(null, callback);
        restapiCallAsync.execute("http://" + FXReaderIP + "/cloud/reboot", "application/json");
    }


}
