package com.sstudio.xshell;

import android.content.Context;
import android.widget.Toast;


public class Logger {
    
    public static Context context;
    private static int logLevel = 0;
    public static final int NONE = 0;
    public static final int DEBUG = 4;
    public static final int INFO = 3;
    public static final int WARN = 2;
    public static final int ERROR = 1;
    
    public static void debug(String tag, String message) {
        if (logLevel >= DEBUG) {
        Toast.makeText(context, tag + ":" + message, Toast.LENGTH_LONG).show();
        }
    }
    
    public static void error(String tag, String message) {
        if (logLevel >= ERROR) {
            Toast.makeText(context, tag + ":" + message, Toast.LENGTH_LONG).show();
        }
    }
    
    public static void warn(String tag, String message) {
        if (logLevel >= WARN) {
        Toast.makeText(context, tag + ":" + message, Toast.LENGTH_LONG).show();
        }
    }
    
    public static void info(String tag, String message) {
        if (logLevel >= INFO) {
        Toast.makeText(context, tag + ":" + message, Toast.LENGTH_LONG).show();
        }
    }
    
    public static void setLogLevel(int level) {
        logLevel = level;
    }
    
}
