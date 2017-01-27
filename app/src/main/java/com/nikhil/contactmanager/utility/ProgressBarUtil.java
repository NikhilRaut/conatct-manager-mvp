package com.nikhil.contactmanager.utility;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Nikhil on 18-01-2017.
 */

public class ProgressBarUtil {
    private static ProgressDialog mProgressDialog;
    private static Object mObject = new Object();

    public static void startProgressDialog(Context context, String message) {
        try {
            synchronized (mObject) {
                if (mProgressDialog == null) {
                    mProgressDialog = ProgressDialog.show(context, "", message);
                    mProgressDialog.setIndeterminate(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgressDialog(Context context) {
        try {
            synchronized (mObject) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateProgress(int message) {
        try {
            mProgressDialog.setProgress(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
