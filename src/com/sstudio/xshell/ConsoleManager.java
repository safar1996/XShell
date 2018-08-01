package com.sstudio.xshell;

import android.content.Context;
import java.io.File;


public class ConsoleManager {
    
    public static final String BIN = "/usr/bin";
    public static final String LIB = "/usr/lib";
    public static final String HOME = "home";
    public static final String ANDROID_DATA = "/data";
    public static final String ANDROID_ROOT = "/system";
    public static final String PS1 = "$";
    
    
    public static Shell.Environment getEnvironment(final Context context) {
        Shell.Environment result = new Shell.Environment() {
            
            @Override
            public void set(String key, String value) {
                
            }
            
            @Override
            public String get(String key) {
                return "";
            }
            
            @Override
            public void remove(String key) {
                
            }
            
            @Override
            public void clear() {
                
            }
            
            @Override
            public String[] toStringArray() {
                return new String[] { "PATH=" + getBinaryPath(context),
                                      "LD_LIBRARY_PATH=" + getLibraryPath(context),
                                      "HOME=" + getWorkingDir(context),
                                      "ANDROID_DATA=" + ANDROID_DATA,
                                      "ANDROID_ROOT=" + ANDROID_ROOT
                                     };
            }
        };
        return result;
    }
    
    public static String getWorkingDir(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + HOME;
    }
    
    public static String getBinaryPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + BIN;
    }
    
    public static String getLibraryPath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + LIB;
    }
    
}
