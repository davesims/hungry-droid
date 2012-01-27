package com.hungrymachine.hungrydroid.net;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import com.hungrymachine.hungrydroid.HungryDroid;
import com.hungrymachine.hungrydroid.R;
import com.hungrymachine.hungrydroid.utils.HungryLogger;
import com.hungrymachine.hungrydroid.utils.StringUtils;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class NetworkService implements Service {

    private static String name = "NetworkService";
    public final static int TIMEOUT = 30000;
    public static final String NO_NETWORK_AVAILABLE = HungryDroid.appInstance().getString(R.string.no_network_available);

    private static class LazyHolder {
        public static final ConnectivityManager INSTANCE = (ConnectivityManager) HungryDroid.appInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static final ConnectivityManager connectionManager() {
        return LazyHolder.INSTANCE;
    }

    static {
        // There's something wrong with hostname verification against our servers on Android < 2.2 (FROYO).
        // If there's a mismatch, ignore it.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
            HttpsURLConnection.setDefaultHostnameVerifier(new AllowAllHostnameVerifier());
        }

        // never cache network lookups so that the /System/etc/hosts file can be used consistently
        System.setProperty("networkaddress.cache.ttl", "0");
        System.setProperty("networkaddress.cache.negative.ttl", "0");
    }

    @Override
    public String get(String url, HashMap<String, String> headerList) {
        if (!NetworkUtil.isNetworkAvailable()) {
            throw new RuntimeException(NO_NETWORK_AVAILABLE);
        }

        boolean backgroundData = connectionManager().getBackgroundDataSetting();
        String result = "";
        if (backgroundData) {
            result = httpGet(url, headerList);
        }
        return result;
    }

    @Override
    public String get(String url, HashMap<String, String> headers, HashMap<String, String> params) {
        if (params != null) {
            url = url + getParamsString(params, true);
        }
        return get(url, headers);
    }

    @Override
    public String post(String uri, HashMap<String, String> params, HashMap<String, String> headerList) {
        return post(uri, getParamsString(params, false), headerList);
    }

    public static String getParamsString(HashMap<String, String> params, boolean get) {
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        StringBuilder paramStringBuilder = new StringBuilder(get ? "?" : "");
        while (iterator.hasNext()) {
            Map.Entry<String, String> param = iterator.next();
            paramStringBuilder.append(param.getKey() + "=" + param.getValue() + "&");
        }
        String paramsString = paramStringBuilder.toString().replaceAll("&$", "");
        return paramsString;
    }

    @Override
    public String post(String uri, String requestBody, HashMap<String, String> headerList){
        return post(uri, requestBody, "POST", "application/x-www-form-urlencoded", headerList);
    }

    public String post(String uri, String requestBody, String requestVerb, String contentType, HashMap<String, String> headerList) {
        if (!NetworkUtil.isNetworkAvailable()) {
            throw new RuntimeException(NO_NETWORK_AVAILABLE);
        }

        HungryLogger.d(name, "Calling " + requestVerb + "...");
        HttpURLConnection connection;
        InputStream inputStream;
        try {

            connection = getConnection(uri, requestVerb);
            connection.setRequestProperty("Content-length", Integer.toString(requestBody.length()));
            if (!StringUtils.isBlank(contentType)){
                connection.setRequestProperty("Content-Type", contentType);
            }

            for (Map.Entry<String, String> header : headerList.entrySet()) {
                connection.addRequestProperty(header.getKey().trim(), header.getValue().trim());
            }

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(requestBody);
            wr.flush();
            wr.close();

            // For OS 2.3 & above, the below method call throws IOException for 401 response code.
            int respCode = connection.getResponseCode();
            if(respCode == HttpURLConnection.HTTP_UNAUTHORIZED){
                throw new Exception("Received authentication challenge is null");
            }
            inputStream = connection.getInputStream();
        } catch (Exception e) {
            HungryLogger.e(name, "Exception thrown in NetworkService.post: " + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return streamToString(inputStream);
    }

    private HttpURLConnection getConnection(String uri, String method) throws IOException {
        HttpURLConnection connection;
        URL url;
        url = new URL(uri);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(TIMEOUT);
        connection.setDoOutput(true);
        return connection;
    }

    @Override
    public String post(String uri, HashMap<String, String> headerList) {
        return post(uri, new HashMap<String, String>(), headerList);
    }

    @Override
    public String delete(String uri, HashMap<String, String> requestParams, HashMap<String, String> headerList) {
        requestParams.put("_method", "delete");
        return post(uri, requestParams, headerList);
    }

    @Override
    public String put(String uri, HashMap<String, String> requestParams, HashMap<String, String> headerList) {
        requestParams.put("_method", "put");
        return post(uri, requestParams, headerList);
    }

    @Override
    public String httpPut(String uri, String postBody, HashMap<String, String> headerlist){
        return post(uri, postBody, "PUT", "", headerlist);
    }

    private String streamToString(InputStream stream) {
        try {
            if (stream != null) {
                return new Scanner(stream).useDelimiter("\\A").next();
            } else {
                return "";
            }
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private String httpGet(String urlString, HashMap<String, String> headerList) {
        HttpURLConnection connection = null;
        URL url;
        InputStream inputStream = null;
        try {
            url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(TIMEOUT);
            connection.setConnectTimeout(TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);

            for (Map.Entry<String, String> header : headerList.entrySet()) {
                connection.addRequestProperty(header.getKey().trim(), header.getValue().trim());
            }

            connection.connect();
            inputStream = connection.getInputStream();
        } catch (Exception e) {
            HungryLogger.e(name, e.getLocalizedMessage() + "\n" + e.getStackTrace());
            throw new RuntimeException(e);
        }

        return streamToString(inputStream);
    }
}
