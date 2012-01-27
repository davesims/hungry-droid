package com.hungrymachine.hungrydroid.api;

/**
 * An interface for abstracting the actions of getting a String value from the server
 * and parsing/creating the expected return object.
 *
 * @param <T> The generic type of the return value.
 */
public abstract class NetworkTask<T> {
    // child classes that set this string are signaling to
    private String errorMessage = null;

    public abstract String getValueFromServer();

    public abstract T createReturnObject(String cachedValue);

    protected String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set this error message to signal to the caller that something went wrong.
     *
     * @param errorMessage
     */
    protected void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Some api errors are returned in the response body even though
     * the server returned a 200 result (boo). This interface accounts for "successfull" api
     * calls that were actually errors. If the error result is populated, assume the api
     * call returned an error result.
     *
     * @return
     */
    public boolean serverReturnedError() {
        return getErrorMessage() != null;
    }
}