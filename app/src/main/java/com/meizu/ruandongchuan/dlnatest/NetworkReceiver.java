package com.meizu.ruandongchuan.dlnatest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.meizu.ruandongchuan.dlnatest.service.DLNAService;

public class NetworkReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals( ConnectivityManager.CONNECTIVITY_ACTION)){
            Log.i("Network:", intent.getAction());
            NetworkInfo netInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            ConnectivityManager cMgr = ( ConnectivityManager ) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo active = cMgr.getActiveNetworkInfo();
            if ( ( active != null  && active.getType() == ConnectivityManager.TYPE_WIFI && !active.isConnectedOrConnecting() )
                    || ( ( active != null ) && active.getType() == ConnectivityManager.TYPE_ETHERNET && !active.isConnectedOrConnecting() )
                    || ( active != null && !active.isConnectedOrConnecting())){
                stop(context);
                Log.i(DLNAService.TAG, "Network:wifi网络连接断开");
            }else {
                stop(context);
                Log.i(DLNAService.TAG, "wifi网络连接断开");
            }
            if ( (netInfo != null) && (netInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) ){
                Log.i(DLNAService.TAG, "连接到网络" + netInfo.getTypeName());
                stop(context);
                // TODO: 3/2/16  
                start(context);
            }
        }
    }
    private void stop(Context context){
        Intent intent = new Intent(context, DLNAService.class);
        context.stopService(intent);
    }
    private void start(Context context){
        Intent intent = new Intent(context, DLNAService.class);
        context.startService(intent);
    }
}
