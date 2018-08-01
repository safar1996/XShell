package com.sstudio.xshell;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.*;
import java.io.*;
import android.content.*;
import android.support.v4.content.*;
import android.os.*;


public class ConsoleActivity extends Activity implements View.OnClickListener {
    
    private static final String TAG = ConsoleActivity.class.getName();
    private ShellService mShellService;
    private LinearLayout mConsoleOutputContainer;
    private EditText mInputCommand;
    private Button mExecuteBtn;
    private ScrollView mScrollView;
    private static final String WINDOW_ID = "window1";
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.console_activity);
        Logger.context = this;
        Logger.setLogLevel(Logger.DEBUG);
        initView();
        initShell();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ShellService.ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
    }
    
    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.execute_btn:
                invokeCommand();
                break;
            default:
                break;
        }
    }
    
    public void onStartShellService() {
        Intent intent = new Intent(this, ShellService.class);
        startService(intent);
    }
    
    private void invokeCommand() {
        final String command = mInputCommand.getText().toString();
        View view = OutputManager.createOutputView(LayoutInflater.from(this), ConsoleManager.PS1 + " " + command);
        mConsoleOutputContainer.addView(view);
        scrollToBottom();
        mShellService.executeCmd(WINDOW_ID, command);
    }
    
    private void addOutput(String output) {
        View view = OutputManager.createOutputView(LayoutInflater.from(this), output);
        mConsoleOutputContainer.addView(view);
        scrollToBottom();
    }
    
    private void initView() {
        mConsoleOutputContainer = (LinearLayout) findViewById(R.id.output_list);
        mInputCommand = (EditText) findViewById(R.id.input_command);
        mExecuteBtn = (Button) findViewById(R.id.execute_btn);
        mScrollView = (ScrollView) findViewById(R.id.output_scroll_vertical);
        mExecuteBtn.setOnClickListener(this);
    }
    
    private void initShell() {
        WorkspaceManager.ensureInstallation(this);
        onStartShellService();
        Intent intent = new Intent(this, ShellService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    private void scrollToBottom() {
        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
    
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == ShellService.ACTION) {
                String output = intent.getStringExtra(ShellService.SHELL_OUTPUT_VALUE);
                if (output != null && output.length() > 0) {
                    addOutput(output);
                }
            }
        }
    };
    
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName component, IBinder binder) {
            ShellService.LocalBinder localBinder = (ShellService.LocalBinder) binder;
            mShellService = localBinder.getInstanceService();
            mShellService.createShell(WINDOW_ID);
        }

        @Override
        public void onServiceDisconnected(ComponentName component) {
            
        }
    };
    
}
