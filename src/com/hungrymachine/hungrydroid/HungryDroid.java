package com.hungrymachine.hungrydroid;

import android.app.Application;
import android.content.Context;

public class HungryDroid {
    private static Application applicationInstance;
    private static Context applicationContext;

    public static void initialize(Application application, Context context){
        applicationContext = context;
        applicationInstance = application;
    }
    
    public static Application appInstance(){
        return applicationInstance;
    }
    
    public static Context getApplicationContext(){
        return applicationContext;
    }
}
