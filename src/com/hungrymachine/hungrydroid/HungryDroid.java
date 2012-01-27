package com.hungrymachine.hungrydroid;

import android.app.Application;

public class HungryDroid {
    private static Application applicationInstance;
    
    public static void initialize(Application application){
        applicationInstance = application;
    }
    
    public static Application appInstance(){
        return applicationInstance;
    }
}
