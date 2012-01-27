package com.hungrymachine.hungrydroid.api;

import android.util.Log;
import com.hungrymachine.hungrydroid.net.NetworkUtil;
import com.hungrymachine.hungrydroid.net.Service;
import com.hungrymachine.hungrydroid.utils.HungryLogger;
import com.hungrymachine.hungrydroid.utils.StringUtils;

public abstract class ApiBase {
    private static String name = "ApiBase";

    protected Service service;

    /**
     * Will return cached value if value exists in cache and hasn't expired.
     * Otherwise will make a network call to retrieve latest.
     *
     * @param apiParams@return
     */
    protected String getResourcePreferCache(ApiParams apiParams) {
        HungryLogger.d(name, "getResourcePreferCache called for " + apiParams.getUri());
        String value = null;

        value = HungryCache.getCachedValue(apiParams.getUri());

        HungryLogger.d(name, "Force refresh from server is: " + apiParams.forceRefreshFromServer());
        if (apiParams.forceRefreshFromServer() || StringUtils.isBlank(value)) {
            HungryLogger.d(name, "Bypassing cache, getting new value from server");
            value = getFromServer(apiParams);
            HungryLogger.d(name, "Value from server was: " + value);
        }

        return value;
    }

    /**
     * Will attempt to get a fresh value from the server and will only attempt to get a cached value
     * if there is no Network Connection.
     *
     * @return
     */
    protected String getResourcePreferServer(ApiParams params) {
        HungryLogger.d(name, "getResourcePreferServer called for " + params.getUri());
        String value;

        if (!NetworkUtil.isNetworkAvailable()) {
            HungryLogger.d(name, "getResourcePreferServer:, no Network available...");
            value = HungryCache.getCachedValue(params.getUri());
            if (StringUtils.isBlank(value)) {
                HungryLogger.d(name, "getResourcePreferServer: no Network or cached value found, throwing Exception...");
                throw new ApiException.ConnectionUnavailable();
            }
        } else {
            value = getFromServer(params);
        }

        return value;
    }


    // TODO: DRY these up
    protected String putToServer(ApiParams params) {
        HungryLogger.d(name, "putToServer called for " + params.getUri());
        setAuthentication(params);
        String result = service.put(params.getUri(), params.getRequestParams(), params.getHeaders());
        return result;
    }

    protected String httpPutToServer(ApiParams params) {
        setAuthentication(params);
        String result = service.httpPut(params.getUri(), params.getPostBody(), params.getHeaders());
        return result;
    }

    protected String postToServer(ApiParams params) {

        setAuthentication(params);
        String result = "";
        if (null != params.getPostBody()){
            result = service.post(params.getUri(), params.getPostBody(), params.getHeaders());
        } else {
            result = service.post(params.getUri(), params.getRequestParams(), params.getHeaders());
        }
        return result;
    }

    protected String deleteFromServer(ApiParams params) {
        HungryLogger.d(name, "Delete called for " + params.getUri());
        setAuthentication(params);
        String result = service.delete(params.getUri(), params.getRequestParams(), params.getHeaders());
        return result;
    }

    protected String getFromServer(ApiParams params) {
        String value;
        HungryLogger.d(name, "getFromServer for: " + params.getUri());
        setAuthentication(params);
        value = service.get(params.getUri(), params.getHeaders(), params.getRequestParams());
        cacheValue(params.getUri(), value);
        return value;
    }

    protected abstract void setAuthentication(ApiParams params);

    protected void cacheValue(String uri, String value) {
        if (!StringUtils.isBlank(value)) {
            HungryLogger.d(name, "Value from server not empty, caching...");
            HungryCache.instance().put(uri, value);
        } else {
            HungryLogger.d(name, "Value retrieved from server was blank or null.");
        }
    }



    /**
     * In case of a server error, attempt to find a cached value; if there's no cache, return an Exception ApiResult.
     *
     * @param cacheKey
     * @param task
     * @param ex
     * @param <ResultTypeT> The type of returned Object (Voucher, Deal, etc.)
     * @return
     */
    protected <ResultTypeT> ApiResult<ResultTypeT> getCachedValueOrCreateExceptionResult(String cacheKey, NetworkTask<ResultTypeT> task, Exception ex) {
        ApiException apiException = ApiException.create(ex);

        HungryLogger.e(name, "runNetworkTask: Exception thrown attempting to make a server call for " + cacheKey + ": " + apiException.getMessage());

        if (apiException.isAuthenticationException()) {
            return new ApiResult<ResultTypeT>(apiException);
        }

        String cachedValue = HungryCache.getValueWithoutValidation(cacheKey);

        if (!StringUtils.isBlank(cachedValue)) {
            Log.d(name, "runNetworkTask: Exception thrown but found a cached value, returning that instead...");
            return new ApiResult<ResultTypeT>(task.createReturnObject(cachedValue));
        }

        Log.d(name, "runNetworkTask: No cached value found after Exception was thrown.");
        return new ApiResult<ResultTypeT>(apiException);
    }

    /**
     * Runs a network call and returns the requested type of ApiResult<ResultTypeT>.
     * Attempts to return a cached value in case of timeouts or recoverable server
     * errors, i.e., anything that's not a 401/unauthorized. In the case of timeouts
     * or network unavailable, will retrieve from cache without validation.
     *
     * @param cacheKey      The key for recovering a cached value from the Cache in case of error or timeout.
     * @param task
     * @param <ResultTypeT> The Type of object to be created/parsed from the returned String value
     * @return
     */
    protected <ResultTypeT> ApiResult<ResultTypeT> runNetworkTask(String cacheKey, final NetworkTask<ResultTypeT> task) {
        try {
            String result = task.getValueFromServer();
            ResultTypeT resultObject = task.createReturnObject(result);
            if (task.serverReturnedError()) {
                HungryLogger.d(name, "Server returned an error in the response body: " + task.getErrorMessage());
                ApiException apiEx = new ApiException() {
                    @Override
                    public String getErrorMessageForUser() {
                        return task.getErrorMessage();
                    }
                };
                return new ApiResult<ResultTypeT>(apiEx);
            }
            return new ApiResult<ResultTypeT>(resultObject);
        } catch (Exception ex) {
            return getCachedValueOrCreateExceptionResult(cacheKey, task, ex);
        }
    }
}
