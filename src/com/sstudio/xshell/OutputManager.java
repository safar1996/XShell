package com.sstudio.xshell;

import android.content.Context;
import android.view.*;
import android.widget.*;


public class OutputManager {
    
    public static View createOutputView(LayoutInflater inflater, String value) {
        TextView result = (TextView) inflater.inflate(R.layout.ouput, null);
        result.setText(value);
        return result;
    }
    
    
}
