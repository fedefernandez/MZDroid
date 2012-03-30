package com.projectsexception.mzdroid.util;

import android.util.Log;

public class CustomLog {
    
    private static final String TAG = "MZDroid";
    
    private static final int LOG_LEVEL = Log.DEBUG;
    private static final boolean VERBOSE = LOG_LEVEL <= Log.VERBOSE;
    private static final boolean DEBUG = LOG_LEVEL <= Log.DEBUG;
    private static final boolean INFO = LOG_LEVEL <= Log.INFO;
    private static final boolean WARN = LOG_LEVEL <= Log.WARN;
    private static final boolean ERROR = LOG_LEVEL <= Log.ERROR;

    public static void verbose(String source, String msg) {
        if (VERBOSE) {
            Log.v(TAG, source + ": " + msg);
        }       
    }

    public static void debug(String source, String msg) {
        if (DEBUG) {
            Log.d(TAG, source + ": " + msg);
        }       
    }

    public static void info(String source, String msg) {
        if (INFO) {
            Log.i(TAG, source + ": " + msg);
        }       
    }

    public static void warn(String source, String msg) {
        if (WARN) {
            Log.w(TAG, source + ": " + msg);
        }       
    }

    public static void error(String source, String msg) {
        if (ERROR) {
            Log.e(TAG, source + ": " + msg);
        }       
    }
}
