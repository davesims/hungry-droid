package com.hungrymachine.hungrydroid.api;

import com.hungrymachine.hungrydroid.utils.HungryLogger;

import java.util.HashMap;

public abstract class ApiParams {
    protected String uri;
    protected boolean forceRefreshFromServer = false;
    protected boolean useBasicAuthCredentials = true;
    protected String postBody = null;
    protected HashMap<String, String> requestParams = new HashMap<String, String>();
    private static String name = "ApiParams";


    public ApiParams(String uri){
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public boolean forceRefreshFromServer() {
        return forceRefreshFromServer;
    }

    public void setForceRefreshFromServer(boolean forceRefreshFromServer) {
        this.forceRefreshFromServer = forceRefreshFromServer;
    }

    public boolean useBasicAuthCredentials() {
        return useBasicAuthCredentials;
    }

    public void setUseBasicAuthCredentials(boolean useBasicAuthCredentials) {
        this.useBasicAuthCredentials = useBasicAuthCredentials;
    }

    public void setRequestParams(HashMap<String, String> urlparams) {
        this.requestParams = urlparams;
    }

    public void setPostBody(String postBody){
        HungryLogger.d(name, "Setting post body to: " + postBody);
        this.postBody = postBody;
    }

    public String getPostBody(){
        return this.postBody;
    }


    public abstract HashMap<String, String> getHeaders();
    public abstract HashMap<String, String> getRequestParams();
}

