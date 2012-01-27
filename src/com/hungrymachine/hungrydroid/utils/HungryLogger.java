package com.hungrymachine.hungrydroid.utils;

import android.util.Log;

import java.util.HashMap;

public class HungryLogger {

    private static final String ERROR_SOURCE = "Error Source";

    private static HashMap<String, String> notifierParams = new HashMap<String, String>(){{
        put(ERROR_SOURCE, "LsLogger");
    }};

    private static int logLevel = Log.DEBUG;

    public static void d(String tag, String message) {
        if (logLevel <= Log.DEBUG) {
            Log.d(tag, message);
        }
    }

    public static void e(String tag, String message) {
        if (logLevel <= Log.ERROR) {
            Log.e(tag, message);
        }
    }

    public static void i(String tag, String message) {
        if (logLevel <= Log.INFO) {
            Log.i(tag, message);
        }
    }

    public static void wtf(String tag, String message) {
        if (logLevel <= Log.INFO) {
            Log.wtf(tag, message);
        }
    }

    public static boolean isVerbose(){
        return logLevel <= Log.DEBUG;
    }

    public static void setVerbose(boolean bVerbose){
        logLevel = bVerbose ? Log.DEBUG : Log.INFO;
    }
}

