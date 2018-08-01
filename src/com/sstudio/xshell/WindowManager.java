package com.sstudio.xshell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;


public class WindowManager {
    
    private final Map<String, OnResultViewListener> listenerPool;
    private static final List<WindowManager>  consoleManagerList = new ArrayList<>();
    
    public WindowManager() {
        consoleManagerList.add(this);
        listenerPool = new HashMap<>();
    }
    
    public void registerWindow(String id, OnResultViewListener listener) {
        listenerPool.put(id, listener);
    }
    
    public static void sendResult(View view, String id) {
        for (WindowManager manager : consoleManagerList) {
            OnResultViewListener listener = manager.listenerPool.get(id);
            if (listener != null) {
                listener.onReceiveResult(view);
            }
        }
    }
    
    public static interface OnResultViewListener {
        public void onReceiveResult(View view);
    }
    
}
