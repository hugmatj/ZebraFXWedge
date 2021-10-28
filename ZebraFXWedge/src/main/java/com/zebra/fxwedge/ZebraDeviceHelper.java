package com.zebra.fxwedge;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.List;

public class ZebraDeviceHelper {
    public static String TAG = "ZDeviceHelper";

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
}
