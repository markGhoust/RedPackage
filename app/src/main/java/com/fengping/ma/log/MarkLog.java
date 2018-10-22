package com.fengping.ma.log;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarkLog {

    private static MarkLog loger = new MarkLog();
    private static final String logFileName = "/RedPackageLog.txt";
    private static File logFile;
    private Handler mHandler = new LogHandler();
    private static BufferedOutputStream bos;
    private static PrintWriter pw = null;
    private Date date = new Date();
    private DateFormat format = SimpleDateFormat.getDateInstance();


    public static boolean debug = true;
    public static boolean hasPermission = false;

    private MarkLog() {
    }

    private static class LogHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 12138 && msg.obj != null) {
                logTofile((String) msg.obj);
            }
        }
    }

    private static void logTofile(String info) {
        pw.println(info);
        pw.flush();
    }

    public void close() {
        if (pw != null) {
            pw.close();
        }
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static MarkLog getInstance() {
        if (logFile == null) {
            initialLoger();
        }
        return loger;
    }

    private static boolean initialLoger() {
        if (debug) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File sdDire = Environment.getExternalStorageDirectory();
                logFile = new File(sdDire.getPath() + logFileName);
                try {
                    bos = new BufferedOutputStream(new FileOutputStream(logFile,true));
                    pw = new PrintWriter(bos);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return true;
    }

    public void flog(String TAG, String text) {
        //Log.i(TAG, "debug: " + debug + " haspermission: " +hasPermission + text);
        if (pw == null) {
            if (initialLoger()) {
                return;
            }

        }
        if (text == null) {
            text = "null";
        }
        if (debug && hasPermission) {
            Log.i(TAG, text);
            date.setTime(System.currentTimeMillis());
            mHandler.obtainMessage(12138, format.format(date) + "     " + TAG + "    :    " + text).sendToTarget();
        }
    }
}
