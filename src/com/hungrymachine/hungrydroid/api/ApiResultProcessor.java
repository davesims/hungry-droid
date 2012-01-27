package com.hungrymachine.hungrydroid.api;

import android.content.Context;
import com.hungrymachine.hungrydroid.utils.HungryLogger;

public abstract class ApiResultProcessor<T> {
    private final String name = "ApiResultProcessor";

    public void processResult(ApiResult<T> apiResult, Context context){
        if (apiResult.success()){
            handleSuccess(apiResult.getResultObject());
        } else {
            handleError(apiResult.getErrorMessageForUser());
        }
    };

    public void handleSuccess(T resultObject){};

    public void handleError(String message) {
        HungryLogger.d(name, "Default error handling for ApiResultProcessor: " + message);
    }
}
