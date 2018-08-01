package com.sstudio.xshell;

import java.util.ArrayList;
import android.content.Context;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.List;
import java.io.IOException;


public class Shell {
    
    private static final String TAG = Shell.class.getName();
    protected Environment mEnvironment;
    protected File mWorkingDirectory;
    protected String mInterpreter;
    private DataInputStream mInStream;
    private DataOutputStream mOutStream;
    private DataInputStream mErrStream;
    private boolean mRunning = false;
    private Process mProcess;
    public static final Factory.INTERPRETER DEFAULT_INTERPRETER = Factory.INTERPRETER.BASH;
    
    private Shell() {
        
    }
    
    public void init() {
        try {
            mProcess = Runtime.getRuntime().exec(mInterpreter, mEnvironment.toStringArray(), mWorkingDirectory);
            mInStream = new DataInputStream(mProcess.getInputStream());
            mOutStream = new DataOutputStream(mProcess.getOutputStream());
            mErrStream = new DataInputStream(mProcess.getErrorStream());
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
            return;
        }
        mRunning = true;
    }
    
    public void execute(String command) {
        if (!mRunning) {
            return;
        }
        try {
            mOutStream.writeBytes(command + "\n");
            mOutStream.flush();
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        }
    }
    
    public void execute(String command, final OnReceiveResult callback) {
        
        try {
            final Process process = Runtime.getRuntime().exec(mInterpreter,
                              mEnvironment.toStringArray(), mWorkingDirectory);
            DataOutputStream cmdStream = new DataOutputStream(process.getOutputStream());
            cmdStream.writeBytes(command + "\n");
            cmdStream.flush();
            cmdStream.writeBytes("exit\n");
            cmdStream.flush();
            StringBuffer resultBuffer = new StringBuffer();
            StringBuffer errorBuffer = new StringBuffer();
            DataInputStream resultStream = new DataInputStream(process.getInputStream());
            String line;
            
            while ((line = resultStream.readLine()) != null) {
                resultBuffer.append(line);
                resultBuffer.append("\n");
            }
            DataInputStream errorStream = new DataInputStream(process.getErrorStream());
            while ((line = errorStream.readLine()) != null) {
                errorBuffer.append(line);
                errorBuffer.append("\n");
            }
            callback.onReceiveResult(resultBuffer.toString(), errorBuffer.toString());
                     
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        }

    }
    
    public String getOutput() {
        if (!mRunning) {
            return "";
        }
        try {
            String result = mInStream.readLine();
            String error = mErrStream.readLine();
            result = (result != null) ? result : "";
            error = (error != null) ? error : "";
            return result + error;
            
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        }
        return "";
    }
    
    
    public static interface OnReceiveResult {
        public void onReceiveResult(String result, String error);
    }

    public static interface Environment {
        
        public void set(String key, String value);
        public void remove(String key);
        public String get(String key);
        public void clear();
        public String[] toStringArray();
        
    }
    
    public static class Factory {
        
        enum INTERPRETER { BASH, SH, LUA }
        
        public static Shell create(Context context, INTERPRETER interpreter, Environment environment, File workingDir) {
            Shell result = new Shell();
            result.mEnvironment = environment;
            result.mWorkingDirectory = workingDir;
            switch (interpreter) {
                case BASH:
                     result.mInterpreter = ConsoleManager.getBinaryPath(context) + File.separator + "bash";
                     break;
                case SH:
                     result.mInterpreter = ConsoleManager.getBinaryPath(context) + File.separator + "sh";
                     break;
                case LUA:
                     result.mInterpreter = ConsoleManager.getBinaryPath(context) + File.separator + "lua";
                     break;
                default:
                     return null;
            }
            return result;
        }
        
    }
    
}
