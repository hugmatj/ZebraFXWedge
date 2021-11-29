package com.zebra.fxwedge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.zebra.deviceidentifierswrapper.DIHelper;
import com.zebra.deviceidentifierswrapper.IDIResultCallbacks;

public class ZebraDeviceHelper {
    public static String TAG = "ZDeviceHelper";

    public static String mDeviceSerialNumber = "";

    public static boolean isZebraDevice(Context context)
    {
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Log.d(TAG, "device name:" + android.os.Build.MODEL);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equalsIgnoreCase("com.symbol.datawedge") &&
            packageInfo.sourceDir.equalsIgnoreCase("/system/priv-app/com.symbol.datawedge/com.symbol.datawedge.apk") &&
            Build.MANUFACTURER.contains("Zebra"))
                return true;
        }
        return false;
    }

    public interface getDeviceSerialCallback
    {
        void onSerialNumberRetrieved(String serialNumber);
    }

    public static void getDeviceSerialNumber(Context context, final getDeviceSerialCallback getDeviceSerialCallback)
    {
        if(mDeviceSerialNumber.isEmpty()) {
            DIHelper.getSerialNumber(context, new IDIResultCallbacks() {
                @Override
                public void onSuccess(String message) {
                    // The message contains the serial number
                    mDeviceSerialNumber = message;
                    if(getDeviceSerialCallback != null)
                    {
                        getDeviceSerialCallback.onSerialNumberRetrieved(mDeviceSerialNumber);
                    }
                }

                @Override
                public void onError(String message) {
                    // An error occurred
                    mDeviceSerialNumber = "SN Error";
                    if(getDeviceSerialCallback != null)
                    {
                        getDeviceSerialCallback.onSerialNumberRetrieved("SN Error");
                    }
                }

                @Override
                public void onDebugStatus(String message) {
                    // You can use this method to get verbose information
                    // about what's happening behind the curtain
                }
            });
            try {
            } catch (Exception e) {
                e.printStackTrace();
                if(getDeviceSerialCallback != null)
                {
                    getDeviceSerialCallback.onSerialNumberRetrieved("SN Error");
                }
            }
        }
    }
}
