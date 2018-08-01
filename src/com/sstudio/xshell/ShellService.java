package com.sstudio.xshell;

import android.os.Binder;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import android.os.IBinder;
import android.content.Intent;
import android.app.IntentService;
import android.support.v4.content.LocalBroadcastManager;
import java.util.Map;
import java.util.*;


public class ShellService extends IntentService {
    
    public static final String ACTION = "com.sstudio.xshell.ShellService";
    public static final String SHELL_OUTPUT_VALUE = "outputValue";
    private LocalBinder mBinder = new LocalBinder();
    private Map<String, Shell> mShellMap = new HashMap<>();
    
    public ShellService() {
        super("ShellService");
    }
    
    @Override
    public void onHandleIntent(Intent intent) {
        while (true) {
            Collection<String> keys =  mShellMap.keySet();
            for (String id : keys) {
                Shell shell = mShellMap.get(id);
                Intent in = new Intent(ACTION);
                String output = shell.getOutput();
                in.putExtra(SHELL_OUTPUT_VALUE, output);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(in);
            }
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    public class LocalBinder extends Binder {
        public ShellService getInstanceService() {
            return ShellService.this;
        }
    }
    
    public boolean createShell(String id) {
        if (mShellMap.containsKey(id)) {
            return false;
        }
        Shell shell = Shell.Factory.create(getApplicationContext(), Shell.DEFAULT_INTERPRETER, ConsoleManager.getEnvironment(getApplicationContext()), new File(ConsoleManager.getWorkingDir(getApplicationContext())));
        shell.init();
        mShellMap.put(id, shell);
        return true;
    }
    
    public void executeCmd(String id, String command) {
        Shell shell = mShellMap.get(id);
        if (shell != null) {
            shell.execute(command);
        }
    }
    
}
