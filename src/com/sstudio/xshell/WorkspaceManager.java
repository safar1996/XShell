package com.sstudio.xshell;

import android.content.Context;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import android.widget.Toast;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class WorkspaceManager {
    private static final String TAG = WorkspaceManager.class.getName();
    private static final String PREFERENCES_TAG = "workspace_pref";
    private static final String INSTALLED_TAG = "installed";
    private static final String WORKSPACE_FILENAME = "workspace.zip";
    
    public static void build(Context context) {
        
        byte[] buffer = new byte[1024];
        String appDataDir = context.getFilesDir().getAbsolutePath();
        Toast.makeText(context, appDataDir, Toast.LENGTH_SHORT).show();
        try {
            ZipInputStream zis = new ZipInputStream(context.getAssets().open(WORKSPACE_FILENAME));
            ZipEntry ze = zis.getNextEntry();
            
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(appDataDir + File.separator + fileName);
                if (ze.isDirectory()) {
                    newFile.mkdir();
                } else {
                    newFile.setExecutable(true);
                    newFile.setReadable(true);
                    newFile.setWritable(true, true);
                    FileOutputStream fos = new FileOutputStream(newFile);
                    int length;
                    while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                    }
                    fos.close();
                }
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        }
        setPermission(context);
        createBusyboxApplets(context);
    }
    
    public static boolean isInstalled(Context context) {
        boolean status = context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE)
                        .getBoolean(INSTALLED_TAG, false);
        return status;
    }
    
    public static boolean ensureInstallation(Context context) {
        boolean installed = isInstalled(context);
        if (!installed) {
            build(context);
            context.getSharedPreferences(PREFERENCES_TAG, Context.MODE_PRIVATE)
            .edit().putBoolean(INSTALLED_TAG, true)
            .commit();
        }
        return installed;
    }
    
    private static void setPermission(Context context) {
        String root = context.getFilesDir().getAbsolutePath();
        String usr = root + File.separator + "usr";
        String bin = usr + File.separator + "bin";
        String lib = usr + File.separator + "lib";
        String cmdPrefix = "chmod -R 755 ";
        String cmdSetAll = "chmod -R 777 " + root + File.separator + "*";
        try {
            Process shell = Runtime.getRuntime().exec("sh");
            DataOutputStream cmdOut = new DataOutputStream(shell.getOutputStream());
            cmdOut.writeBytes(cmdSetAll);
            cmdOut.flush();
            cmdOut.writeBytes(cmdPrefix + bin + "/*\n");
            cmdOut.flush();
            cmdOut.writeBytes(cmdPrefix + lib + "/*\n");
            cmdOut.flush();
            cmdOut.writeBytes("exit\n");
            cmdOut.flush();
            shell.waitFor();
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        } catch (InterruptedException ie) {
            Logger.error(TAG, ie.getMessage());
        }
        
    }
    
    private static boolean createBusyboxApplets(Context context) {
        boolean status = false;
        final String[] applets = new String[] { "ar", "arp", "awk", "base64", "basename",
                                                "bbconfig", "bunzip2", "bzip2", "cal",
                                                "cat", "chattr", "chgrp", "chmod", "chown",
                                                "chpst", "chrt", "cksum", "clear", "cmp",
                                                "comm", "cp", "cpio", "crond", "crontab",
                                                "cut", "date", "dc", "dd", "diff", "dirname",
                                                "dos2unix", "du", "echo", "egrep", "env",
                                                "envdir", "expand", "expr", "false", "find",
                                                "fold", "free", "fsync", "ftpd", "ftpget",
                                                "ftpput", "fuser", "getopt", "grep",
                                                "gunzip", "gzip", "hd", "head", "hexdump",
                                                "hostname", "httpd", "id", "ifconfig",
                                                "inotifyd", "install", "iostat", "ipcalc",
                                                "kill", "killall", "less", "linux32",
                                                "linux64", "ln", "ls", "lsattr", "lsof",
                                                "lsusb", "lzma", "makemime", "md5sum",
                                                "mkdir", "mkfifo", "mknod", "mktemp",
                                                "more", "mpstat", "mv", "nc", "netstat",
                                                "nice", "nmeter", "nohup", "nproc", "od",
                                                "patch", "pgrep", "pidof",
                                                "pipe_progress", "pkill", "pmap",
                                                "popmaildir", "printenv", "printf", "ps",
                                                "pscan", "pstree", "pwd", "pwdx",
                                                "readlink", "realpath", "reformime",
                                                "renice", "reset", "resize", "rev", "rm",
                                                "rmdir", "route", "run-parts", "runsv",
                                                "runsvdir", "rx", "script",
                                                "scriptreplay", "sed", "sendmail", "seq",
                                                "setsid", "setuidgid", "sha1sum",
                                                "sha256sum", "sha3sum", "sha512sum",
                                                "shuf", "sleep", "smemcap", "softlimit",
                                                "sort", "split", "start-stop-daemon",
                                                "strings", "stty", "sum", "sv", "svlogd",
                                                "sync", "sysctl", "tac", "tail", "tar",
                                                "tcpsvd", "tee", "telnet", "telnetd",
                                                "sync", "sysctl", "tac", "tail", "tar",
                                                "tcpsvd", "tee", "telnet", "telnetd",
                                                "test", "tftp", "tftpd", "time", "timeout",
                                                "top", "touch", "tr", "true", "tty",
                                                "ttysize", "udpsvd", "uname",
                                                "uncompress", "unexpand", "uniq",
                                                "unix2dos", "unlink", "unlzma", "unxz",
                                                "unzip", "uptime", "usleep", "uudecode",
                                                "uuencode", "vi", "watch", "wc", "wget",
                                                "which", "whoami", "whois", "xargs", "xz",
                                                "yes"           
                                              };
        final String cmdPrefix = "ln -sf";
        try {
            Process shell = Runtime.getRuntime().exec("sh", null, new File(ConsoleManager.getBinaryPath(context)));
            DataOutputStream cmdStream = new DataOutputStream(shell.getOutputStream());
            for (int i = 0; i < applets.length; i++) {
                cmdStream.writeBytes(cmdPrefix + " " + "busybox" + " " + applets[i] + "\n");
                cmdStream.flush();
            }
            cmdStream.writeBytes("exit\n");
            cmdStream.flush();
            status = true;
        } catch (IOException ioe) {
            Logger.error(TAG, ioe.getMessage());
        }
        return status;
    }
    
}
