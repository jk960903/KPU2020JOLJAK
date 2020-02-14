package com.example.joljakclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class AppSettings {
    // Constants
    public static final int SETTINGS_BACKGROUND_SERVICE = 1;
    public static final int SETTINGS_WEIGHT = 2;


    private static boolean mIsInitialized = false;
    private static Context mContext;

    // Setting values
    private static boolean mUseBackgroundService;
    private static int mWeight;


    public static void initializeAppSettings(Context c) {
        if(mIsInitialized)
            return;

        mContext = c;

        // Load setting values from preference
        mUseBackgroundService = loadBgService();
        mWeight = loadWeight();

        mIsInitialized = true;
    }
    // Remember setting value
    public static void setSettingsValue(int type, boolean boolValue, int intValue, String stringValue) {
        if(mContext == null)
            return;

        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        switch(type) {
            case SETTINGS_BACKGROUND_SERVICE:
                editor.putBoolean(Constants.PREFERENCE_KEY_BG_SERVICE, boolValue);
                editor.commit();
                mUseBackgroundService = boolValue;
                break;
            case SETTINGS_WEIGHT:
                editor.putInt(Constants.PREFERENCE_KEY_WEIGHT, intValue);
                editor.commit();
                mWeight = intValue;
                break;
            default:
                editor.commit();
                break;
        }
    }

    /**
     * Load 'Run in background' setting value from preferences
     * @return	boolean		is true
     */
    public static boolean loadBgService() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isTrue = prefs.getBoolean(Constants.PREFERENCE_KEY_BG_SERVICE, false);
        return isTrue;
    }

    /**
     * Returns 'Run in background' setting
     * @return	boolean		is true
     */
    public static boolean getBgService() {
        return mUseBackgroundService;
    }

    /**
     * Load 'Run in background' setting value from preferences
     * @return		User's weight
     */
    public static int loadWeight() {
        SharedPreferences prefs = mContext.getSharedPreferences(Constants.PREFERENCE_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Constants.PREFERENCE_KEY_WEIGHT, 68);
    }

    /**
     * Returns 'Run in background' setting
     * @return	int		User's weight
     */
    public static int getWeight() {
        return mWeight;
    }
}
