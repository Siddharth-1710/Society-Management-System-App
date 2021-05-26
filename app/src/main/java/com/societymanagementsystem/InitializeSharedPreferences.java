package com.societymanagementsystem;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mayur on 05-10-2018.
 */

public class InitializeSharedPreferences {

    private String sharedPrefFile = "com.example.source.pillreminder.user";

    protected static SharedPreferences sharedPreferences = null;
    protected static SharedPreferences.Editor editor = null;

    protected InitializeSharedPreferences(Context context){
        if(sharedPreferences==null) {
            sharedPreferences = context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
        }
        if(editor==null){
            if(sharedPreferences==null) {
                sharedPreferences = context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
            }
            editor = sharedPreferences.edit();
        }
    }
}
