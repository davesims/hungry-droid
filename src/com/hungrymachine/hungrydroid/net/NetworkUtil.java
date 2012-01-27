package com.hungrymachine.hungrydroid.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.hungrymachine.hungrydroid.HungryDroid;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class NetworkUtil {


    protected static final String name = "NetworkUtil";
    // File not found is the message returned with a 401 unauthorized
    public static final int NETWORK_RETRY_DELAY_MS = 50;
    public static final int NETWORK_RETRIES = 5;
    private static boolean mostRecentConnectedStatusWasTrue = true;

    /**
     * Return true if network connectivity is available.
     *
     * @return
     */
    public static boolean isNetworkAvailable() {
        int attempts = 0;
        while (mostRecentConnectedStatusWasTrue && !networkConnected() && attempts++ < NETWORK_RETRIES){
            Log.d(name, "No connection, attempt #" + attempts);
            try {Thread.sleep(NETWORK_RETRY_DELAY_MS);}catch(Exception ex){}
        }
        return mostRecentConnectedStatusWasTrue = networkConnected();
    }

    private static boolean networkConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) HungryDroid.appInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    /**
     * Use this in trustAllCerts for connecting with a self-signed
     * certificate, such as that used by our qa instances.
     *
     * @author davesims
     *
     */
    private static class TrustAllSSLTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }
    }

}
