package com.oado.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Developer on 2/25/17.
 */

public class ConnectivityReceiver  extends BroadcastReceiver{


    public static ConnectivityReceiverListener connectivityReceiverListener;

    private Context mcontext;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        this.mcontext = context;
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }

        String status = NetworkUtil.getConnectivityStatusString(context);

        //Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }

    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) GlobalClass.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }


    public void internet_speed(){

        String status = NetworkUtil.getConnectivityStatusString(mcontext);

     //   Toast.makeText(mcontext, status, Toast.LENGTH_LONG).show();

    }




}
