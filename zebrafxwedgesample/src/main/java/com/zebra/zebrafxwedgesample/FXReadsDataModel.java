package com.zebra.zebrafxwedgesample;

import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FXReadsDataModel {
    public static class data
    {
        public int antenna;
        public long eventNum;
        public String format;
        public String idHex;
        public int peakRssi;
        public long reads;

        public String toString()
        {
            String returnValue = "antenna: " + antenna + "\n";
            returnValue += "eventNum: " + eventNum + "\n";
            returnValue += "format: " + format + "\n";
            returnValue += "idHex: " + idHex + "\n";
            returnValue += "peakRssi: " + peakRssi + "\n";
            returnValue += "reads: " + reads + "\n";
            return returnValue;
        }
    }
    public data data;
    public Date timestamp;
    public String type;

    public String toString()
    {
        String returnValue = data.toString();
        returnValue += "timeStamp: " + timestamp + "\n";
        returnValue += "type: " + type + "\n";
        return returnValue;
    }

    public Bundle toBundle()
    {
        Bundle bundle = new Bundle();
        Bundle dataBundle = new Bundle();
        dataBundle.putInt(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_ANTENNA, data.antenna);
        dataBundle.putLong(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_EVENTNUM, data.eventNum);
        dataBundle.putString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_FORMAT, data.format);
        dataBundle.putString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_IDHEX, data.idHex);
        dataBundle.putInt(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_PEAKRSSI, data.peakRssi);
        dataBundle.putLong(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_READS, data.reads);
        bundle.putBundle(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_DATA, dataBundle);
        SimpleDateFormat dateFormat = new SimpleDateFormat(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP_DATEFORMAT);
        String formattedDate = dateFormat.format(timestamp);
        bundle.putString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP, formattedDate);
        bundle.putString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TYPE, type);
        return bundle;
    }

    public static FXReadsDataModel fromBundle(Bundle bundle)
    {
        FXReadsDataModel returnObject = new FXReadsDataModel();
        returnObject.type = bundle.getString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TYPE);
        SimpleDateFormat formater = new SimpleDateFormat(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP_DATEFORMAT);
        try {
            String dateAsString = bundle.getString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_TIMESTAMP);
            returnObject.timestamp = formater.parse(dateAsString);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
        Bundle dataBundle = bundle.getBundle(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_DATA);
        returnObject.data = new data();
        returnObject.data.antenna = dataBundle.getInt(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_ANTENNA);
        returnObject.data.eventNum = dataBundle.getLong(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_EVENTNUM);
        returnObject.data.format = dataBundle.getString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_FORMAT);
        returnObject.data.idHex = dataBundle.getString(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_IDHEX);
        returnObject.data.peakRssi = dataBundle.getInt(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_PEAKRSSI);
        returnObject.data.reads = dataBundle.getLong(FXWedgeConstants.FXDATA_BROADCAST_INTENT_EXTRA_READS);

        return returnObject;
    }
}
