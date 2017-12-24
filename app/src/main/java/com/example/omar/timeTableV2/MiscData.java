package com.example.omar.timeTableV2;


import android.content.Context;
import android.content.SharedPreferences;


public class MiscData{


    //makes sure all data is static
    private static MiscData instance   = new MiscData();
    private static String   preference = "pref";
    private static String   isDone     = "isDone";
    //shared preferences strings
    private SharedPreferences pref;


    private MiscData(){

    }


    public static MiscData getInstance(){

        return instance;
    }


    public boolean isDone(Context context){

        pref = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        return pref.getBoolean(isDone, false);
    }


    public void doneSetup(Context context){

        pref = context.getSharedPreferences(preference, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(MiscData.isDone, true);
        editor.apply();

    }
}
