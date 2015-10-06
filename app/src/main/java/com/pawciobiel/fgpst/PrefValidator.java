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

    public static boolean isVehicleIDValid(String value){
        return (value != null)  &&
               (value.trim().length() >= 3) &&
               (value.trim().length() < 37) &&
                Pattern.compile("^[a-zA-Z0-9\\-]{3,}$")
                        .matcher(value).matches();
    }

    public static boolean isPrefVehicleIDValid(SharedPreferences preferences){
        String value = preferences.getString
                ("vehicle_id", "");
        return isVehicleIDValid(value);
    }
}
