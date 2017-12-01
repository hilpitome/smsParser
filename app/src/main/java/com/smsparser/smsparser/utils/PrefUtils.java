package com.smsparser.smsparser.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Lawrence on 10/12/16.
 */

public class PrefUtils {


    /**
     * Shared Preferences
     */
    private SharedPreferences sharedPreferencesCompat;

    /**
     * Editor for Shared preferences
     */
    private SharedPreferences.Editor editor;

    /**
     * Context
     */
    private Context mContext;

    /**
     * Shared pref mode
     */
    private int PRIVATE_MODE = 0;

    /**
     * Shared preferences file name
     */
    private static final String PREF_NAME = "smsParserPref";

    /**
     *
     */
    private static final String KEY_HAS_PHONE_NUMBER= "hasPhoneNumber";

    private static final String KEY_SIM_NUMBER = "sim_number";


    public PrefUtils(Context mContext) {
        this.mContext = mContext;
        this.sharedPreferencesCompat = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        this.editor = this.sharedPreferencesCompat.edit();
    }


    public boolean hasPhoneNumer() {
        return sharedPreferencesCompat.getBoolean(KEY_HAS_PHONE_NUMBER, false);
    }



    public void setKeyHasPhoneNumber(boolean hasPhoneNumber) {
        editor.putBoolean(KEY_HAS_PHONE_NUMBER, hasPhoneNumber);
        editor.commit();
    }





    public void setSimNUmber(String simNUmber){
        editor.putString(KEY_SIM_NUMBER, simNUmber);
        editor.commit();
    }

    public String getKeySimNumber() {
        return sharedPreferencesCompat.getString(KEY_SIM_NUMBER, null);
    }

}
