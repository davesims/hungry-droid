package com.hungrymachine.hungrydroid.utils;

public class StringUtils {

    public static boolean isBlank(String val) {
        return (val == null || val.trim().equals("") || (val.trim().equals("null")));
    }

    public static String underscore(String val) {
        return val.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2").
                replaceAll("([a-z\\d])([A-Z])", "$1_$2").
                replaceAll("-", "_").toLowerCase();
    }

    public static String stripSpacesAndDashes(String val){
        return val.replaceAll("[-\\s]", "");
    }


}

