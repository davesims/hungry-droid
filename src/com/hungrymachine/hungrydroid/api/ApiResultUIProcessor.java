package com.hungrymachine.hungrydroid.api;

import android.content.Context;
import com.hungrymachine.hungrydroid.utils.HungryLogger;
import com.hungrymachine.hungrydroid.views.HungryAlert;

/**
 * This class is used by a caller on the UI thread to process the result of an asynchronous API call.
 * Child classes (usually anonymous inner classes) must override handleSuccess, which will be executed
 * asynchronously on the UI thread if the API call returns successfully.
 * <p/>
 * Child classes *may* override handleError if the caller needs to perform some other action.
 * <p/>
 * IMPORTANT: An alert dialog will be shown any time an Exception is encountered, hence this is only to be used by Activities
 * on the UI thread, and NOT Services or AsyncTasks, etc. Anyone not on the UI thread can use the base class ApiResultProcessor.
 *
 * @author davesims
 * @see ApiResultProcessor
 */
public abstract class ApiResultUIProcessor<T> extends ApiResultProcessor<T> {

    private final String name = "ApiResultUIProcessor";

    public void processResult(final ApiResult<T> apiResult, final Context context) {
        if (apiResult != null) {
            if (apiResult.success()) {
                HungryLogger.d(name, "Processing successful ApiResult with result type: " + apiResult.getResultObject().getClass().getName());
                handleSuccess(apiResult.getResultObject());
            } else if (apiResult.authenticationRejected()) {
                logOutAndShowAuthenticationRejectedMessage(apiResult, context);
            } else {
                HungryLogger.d(name, "The Api call resulted in an error...");
                HungryAlert.show(apiResult.getErrorMessageForUser(), context, new Runnable() {
                    public void run() {
                        handleError(apiResult.getErrorMessageForUser());
                    }
                });
            }
        } else {
            HungryLogger.e(name, "null ApiResult given to processResult");
        }
    }

    /**
     * This can be overridden by a child/anonymous inner class if you need to present a different authentication result.
     * Like, for instance, Logins...
     *
     * @param apiResult
     * @param context
     */
    public abstract void logOutAndShowAuthenticationRejectedMessage(ApiResult<T> apiResult, final Context context);
}

