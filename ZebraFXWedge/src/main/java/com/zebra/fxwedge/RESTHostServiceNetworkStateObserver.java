package com.zebra.fxwedge;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static android.content.Context.WIFI_SERVICE;

public class RESTHostServiceNetworkStateObserver {

    private BroadcastReceiver mNetworkStateBroadcastReceiver = null;
    protected static String mIpAddress = "";
    private Context mContext = null;
    private CountDownLatch mJobDoneLatch = null;

    public interface IIPChangeObserver
    {
        void onIPChanged(String newIP);
    }
    private IIPChangeObserver mIPChangeObserver = null;

    public RESTHostServiceNetworkStateObserver(Context aContext, IIPChangeObserver aIIPChangeObserver)
    {
        mContext = aContext;
        mIPChangeObserver = aIIPChangeObserver;
    }

    public void startObserver()
    {
        Log.d(RESTHostServiceConstants.TAG, "Starting ip change observer.");
        registerNetworkChangeBroadcastReceiver();
    }

    public void stopObserver()
    {
        Log.d(RESTHostServiceConstants.TAG, "Stopping ip change observer.");
        unregisterNetworkChangeBroadcastReceiver();
    }

    public  boolean isStarted()
    {
        return mNetworkStateBroadcastReceiver != null;
    }

    public String getIPAddress()
    {
        getDeviceIP();
        return mIpAddress;
    }

    //////////////////////////////////////////////////////////////
    // Wifi network related methods
    //////////////////////////////////////////////////////////////
    private  void registerNetworkChangeBroadcastReceiver() {
        if(mNetworkStateBroadcastReceiver == null)
        {
            final IntentFilter filters = new IntentFilter();
            filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
            filters.addAction("android.net.wifi.STATE_CHANGE");
            mNetworkStateBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                        getIPAddress();
                        if(mIPChangeObserver != null)
                            mIPChangeObserver.onIPChanged(mIpAddress);
                }
            };
            mContext.registerReceiver(mNetworkStateBroadcastReceiver, filters);
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "StartObserver: Warning, WifiIPObserver already running.");
        }
        // get current ip if connected
        if(isConnected())
            mIpAddress = getIPAddress();
    }

    private void unregisterNetworkChangeBroadcastReceiver()
    {
        if(mNetworkStateBroadcastReceiver != null)
        {
            mContext.unregisterReceiver(mNetworkStateBroadcastReceiver);
            mNetworkStateBroadcastReceiver = null;
        }
        else
        {
            Log.d(RESTHostServiceConstants.TAG, "StartObserver: Warning, WifiIPObserver already stopped.");
        }
    }

    public boolean isConnected() {
        ConnectivityManager connectivityManager =  (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null)
        {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        } else {
            return false;
        }
    }

    public void getDeviceIP() {
        if(isConnected()) {
            mIpAddress = "";
            WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
            // If wifi is enabled we just get the WIFI address
            if (wifiManager != null && wifiManager.isWifiEnabled() ) {
                getWifiIPAddress(wifiManager);
            }
            if(mIpAddress == "" || mIpAddress.equalsIgnoreCase("0.0.0.0")){
                // We try to get the WAN address (or any other address...)
                try {
                    getWanIPAddress();
                } catch (Exception e) {
                    e.printStackTrace();
                    mIpAddress = "";
                }
            }
            if(mIpAddress == "")
            {
                getLanIPAddress();
            }
        }
        else
            mIpAddress = "127.0.0.1";
    }

    private void getWifiIPAddress(WifiManager wifiManager)
    {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            int ipAddress = wifiInfo.getIpAddress();
            final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            mIpAddress = formatedIpAddress;
        }

    }

    private void getWanIPAddress() throws Exception {
        if(mJobDoneLatch != null)
        {
            // we are already reading the IP address, this case shouldn't appears in the workflow, but who knows
            throw new Exception("Already looking for an IP address.");
        }
        mJobDoneLatch = new CountDownLatch(1);

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    try
                    {
                        InetAddress primera = InetAddress.getLocalHost();
                        String hostname = InetAddress.getLocalHost().getHostName ();

                        if (!primera.isLoopbackAddress () &&
                                !hostname.equalsIgnoreCase ("localhost") &&
                                primera.getHostAddress ().indexOf (':') == -1)
                        {
                            // Got it without delay!!
                            mIpAddress = primera.getHostAddress ();
                            //System.out.println ("First try! " + HOST_NAME + " IP " + HOST_IPADDRESS);
                            mJobDoneLatch.countDown();
                            return;
                        }
                        for (Enumeration<NetworkInterface> netArr = NetworkInterface.getNetworkInterfaces(); netArr.hasMoreElements();)
                        {
                            NetworkInterface netInte = netArr.nextElement ();
                            for (Enumeration<InetAddress> addArr = netInte.getInetAddresses (); addArr.hasMoreElements ();)
                            {
                                InetAddress laAdd = addArr.nextElement ();
                                String ipstring = laAdd.getHostAddress ();
                                String hostName = laAdd.getHostName ();

                                if (laAdd.isLoopbackAddress()) continue;
                                if (hostName.equalsIgnoreCase ("localhost")) continue;
                                if (ipstring.indexOf (':') >= 0) continue;

                                mIpAddress = ipstring;
                                mJobDoneLatch.countDown();
                                return;
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            mJobDoneLatch.await();
            mJobDoneLatch = null;
        } catch (InterruptedException e) {
            if (mJobDoneLatch != null) {
                while (mJobDoneLatch.getCount() > 0)
                    mJobDoneLatch.countDown();
                mJobDoneLatch = null;
            }
        }
    }

    private void getLanIPAddress()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Service.CONNECTIVITY_SERVICE);
        try
        {
            List<LinkAddress> linkAddresses =  connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork()).getLinkAddresses();
            for(LinkAddress address : linkAddresses)
            {
                if(address.getAddress() instanceof Inet4Address == true)
                {
                    mIpAddress = address.getAddress().toString();
                    return;
                }
            }
        }
        catch(Exception e)
        {
            mIpAddress = "";
        }
        mIpAddress = "";
    }
}
