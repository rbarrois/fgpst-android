package com.pawciobiel.fgpst;

import android.content.SharedPreferences;
import android.util.Patterns;

import java.util.regex.Pattern;


public class PrefValidator {



    public static boolean isUrlValid(String value){
        return (value != null) &&
               (value.trim().length() > 7) &&
                Patterns.WEB_URL.matcher(value).matches();
    }

    public static boolean isPrefUrlValid(SharedPreferences preferences){

        String value = preferences.getString("URL", "");
        // preferences.contains("URL")
        return isUrlValid(value);

    }

    public static boolean isUserKeyValid(String value){

        return (value != null)  &&
               (value.trim().length() >= 8) &&
               (value.trim().length() < 40) &&
                Pattern.compile("^[a-zA-Z0-9\\-]{8,}$")
                        .matcher(value).matches();
    }

    public static boolean isPrefUserKeyValid(SharedPreferences preferences){
        String value = preferences.getString("user_key", "");
        return isUserKeyValid(value);
    }

    public static boolean isDeviceKeyValid(String value){
        return (value != null)  &&
               (value.trim().length() >= 3) &&
               (value.trim().length() < 40) &&
                Pattern.compile("^[a-zA-Z0-9\\-]{3,}$")
                        .matcher(value).matches();
    }

    public static boolean isPrefDeviceKeyValid(SharedPreferences preferences){
        String value = preferences.getString
                ("device_key", "");
        return isDeviceKeyValid(value);
    }
}
