package com.example.prm392_assignment_project.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.prm392_assignment_project.views.view_callbacks.IOnWifiDisableCallback;
import com.example.prm392_assignment_project.views.view_callbacks.IOnWifiEnableCallback;

public class WifiReceiver extends BroadcastReceiver
{
    private final IOnWifiEnableCallback wifiEnableCallback;
    private final IOnWifiDisableCallback wifiDisableCallback;
    private ConnectivityManager connectivityManager;
    private int wifiState;

    public WifiReceiver(
        IOnWifiEnableCallback wifiEnableCallback,
        IOnWifiDisableCallback wifiDisableCallback)
    {
        // Prevent null pointer exception.
        if (wifiDisableCallback == null)
        {
            wifiDisableCallback = this::handleDefault;
        };

        if (wifiEnableCallback == null)
        {
            wifiEnableCallback = this::handleDefault;
        }

        this.wifiEnableCallback = wifiEnableCallback;
        this.wifiDisableCallback = wifiDisableCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        final String action = intent.getAction();

        if (!WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action))
        {
             return;
        }

        wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

        if (connectivityManager == null)
        {
            connectivityManager = context.getSystemService(ConnectivityManager.class);
            connectivityManager.addDefaultNetworkActiveListener(this::handleOnNetworkActive);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(this::checkWifiState);
    }

    private void handleDefault() {}

    private void handleOnNetworkActive()
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(wifiEnableCallback::resolve);
    }

    private void checkWifiState()
    {
        switch (wifiState)
        {
            case WifiManager.WIFI_STATE_ENABLED:
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable())
                {
                    wifiEnableCallback.resolve();
                }
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                wifiDisableCallback.resolve();
                break;
        }
    }
}
