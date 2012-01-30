package com.hungrymachine.hungrydroid.api;

import android.content.Context;
import android.os.AsyncTask;
import com.hungrymachine.hungrydroid.HungryDroid;
import com.hungrymachine.hungrydroid.R;
import com.hungrymachine.hungrydroid.utils.HungryLogger;

/**
 * This class encapsulates the result of an asynchronous API call off of the main UI thread. In case
 * of success it returns an object (usually a model) of type T. If an Exception is thrown it contains
 * that Exception, and the value of ApiResult#success() will be false. This allows the UI
 * thread to make a decision as to what to present to the user in case something went wrong (see: ApiResultProcessor).
 *
 * @param <T> The type of object returned when the API call is successful.
 * @author davesims
 * @see ApiResultProcessor, ApiResultUIProcessor
 */
public class ApiResult<T> {
    private ApiException exception = null;
    private T resultObject;
    private final static String name = "ApiResult";

    /**
     * Use this constructor for a return value when an Exception is thrown during the API call.
     *
     * @param e The Exception thrown while attempting the API call
     */
    public ApiResult(ApiException e) {
        if (e != null) {
            HungryLogger.d(name, "Creating ApiResult with Exception: " + e.getMessage());
        }
        this.exception = e;
    }

    /**
     * Use this constructor when the API call was successful, passing in the result of the API call for use
     * by the UI/Main thread.
     *
     * @param resultObject The object of type T that is returned as the result of a successful API call.
     */
    public ApiResult(T resultObject) {
        if (resultObject != null) {
            HungryLogger.d(name, "Creating successful ApiResult with a result of type: " + resultObject.getClass().getName());
            this.resultObject = resultObject;
        } else {
            HungryLogger.e(name, "Null object given attempting to create ApiResult");
        }
    }

    /**
     * Returns the object of type T that is the result of a successful API call.
     *
     * @return The result object.
     */
    public T getResultObject() {
        return this.resultObject;
    }

    /**
     * Returns true if the API call was successful and result is not null
     *
     * @return
     */
    public boolean success() {
        return this.exception == null && resultObject != null;
    }

    /**
     * Returns the user-facing text message explaining what went wrong (Timeout, server error, etc.) for use in something like
     * an error dialog on the UI thread, etc.
     *
     * @return User-friendly text message describing what went wrong.
     */
    public String getErrorMessageForUser() {
        if (this.exception != null) {
            return this.exception.getErrorMessageForUser();
        } else {
            return HungryDroid.appInstance().getString(R.string.hd_error_message);
        }
    }

    public boolean authenticationRejected() {
        return this.exception != null && this.exception.isAuthenticationException();
    }

    /**
     * Encapsulates the execution of an asynchronous api call and returns the resulting ApiResult object.
     *
     * @param <ResultT>
     */
    public static abstract class Task<ResultT> extends AsyncTask<Object, Object, ApiResult<ResultT>> {
        private Context context;

        private ApiResultProcessor<ResultT> processor;

        public Task(Context context, ApiResultProcessor<ResultT> processor) {
            this.context = context;
            this.processor = processor;
        }

        public Task(ApiResultProcessor<ResultT> processor) {
            this.processor = processor;
        }

        @Override
        protected void onPostExecute(ApiResult<ResultT> result) {
            processor.processResult(result, context);
        }
    }

}
