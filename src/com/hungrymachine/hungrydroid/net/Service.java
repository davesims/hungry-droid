package com.hungrymachine.hungrydroid.net;

import java.util.HashMap;

public interface Service {
    String get(String url, HashMap<String, String> headerList);

    String get(String url, HashMap<String, String> headers, HashMap<String, String> params);

    String post(String uri, HashMap<String, String> params, HashMap<String, String> headerList);

    String post(String uri, HashMap<String, String> headerList);

    String delete(String uri, HashMap<String, String> params, HashMap<String, String> headerList);

    String put(String uri, HashMap<String, String> requestParams, HashMap<String, String> headers);

    String post(String uri, String requestBody, HashMap<String, String> headerList);

    String httpPut(String uri, String postBody, HashMap<String, String> headerlist);
}
